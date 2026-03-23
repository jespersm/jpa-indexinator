package io.github.jespersm.indexinator.test;

import io.github.jespersm.indexinator.core.Indexinator;
import io.github.jespersm.indexinator.core.model.InspectionReport;
import io.github.jespersm.indexinator.core.model.Issue;
import io.github.jespersm.indexinator.core.model.IssueSeverity;
import io.github.jespersm.indexinator.core.model.IssueType;
import io.github.jespersm.indexinator.test.entity.SchoolClass;
import io.github.jespersm.indexinator.test.entity.Student;
import io.github.jespersm.indexinator.test.entity.Teacher;
import io.github.jespersm.indexinator.test.repository.SchoolClassRepository;
import io.github.jespersm.indexinator.test.repository.StudentRepository;
import io.github.jespersm.indexinator.test.repository.TeacherRepository;
import io.github.jespersm.unselectinator.core.LazySelectReport;
import io.github.jespersm.unselectinator.core.ObservationResult;
import io.github.jespersm.unselectinator.core.Unselectinator;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Indexinator using Spring Boot 3.5 + Testcontainers PostgreSQL
 */
@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
class IndexinatorIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("indexinator_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    private SchoolClassRepository schoolClassRepository;

    @Autowired
    private Unselectinator unselectinator;

    @Autowired
    @Qualifier("observedEntityManager")
    private EntityManager entityManager;

    @Test
    @Transactional
    void testModelPrefixSearchWorks() {
        List<SchoolClass> schoolClasses = schoolClassRepository.findAllByCourseNameStartsWith("Data");
        assertEquals(2, schoolClasses.size());
        assertEquals(2, schoolClasses.iterator().next().getStudents().size());
    }

    @Test
    @Transactional
    void testLazyLoadingProducesNPlusOneSelects() {
        SchoolClass schoolClass = schoolClassRepository.findByCourseCode("CS101")
                .orElseThrow(() -> new AssertionError("Expected to find seeded class CS101"));

        // This intentionally demonstrates a classic N+1 pattern:
        // 1 query loads the class, 1 query initializes schoolClass.students,
        // then 1 additional query per student initializes student.classes lazily.
        List<String> studentEnrollmentSummaries = schoolClass.getStudents().stream()
                .map(student -> "%s (advisor: %s) is enrolled in %d classes".formatted(
                        student.getFirstName(),
                        student.getAdvisor().getLastName(),
                        student.getClasses().size()))
                .sorted()
                .toList();

        assertEquals(
                List.of(
                        "Alice (advisor: Smith) is enrolled in 3 classes",
                        "Bob (advisor: Smith) is enrolled in 3 classes",
                        "Diana (advisor: Brown) is enrolled in 2 classes"
                ),
                studentEnrollmentSummaries
        );
    }

    @Test
    @Transactional
    void testUnselectinatorAttributesLazySelectsToRepositoryMethod() {
        ObservationResult<List<String>> observation = unselectinator.observe(() -> {
            SchoolClass schoolClass = schoolClassRepository.findByCourseCode("CS101")
                    .orElseThrow(() -> new AssertionError("Expected to find seeded class CS101"));

            return schoolClass.getStudents().stream()
                    .map(student -> "%s (advisor: %s) is enrolled in %d classes".formatted(
                            student.getFirstName(),
                            student.getAdvisor().getLastName(),
                            student.getClasses().size()))
                    .sorted()
                    .toList();
        });

        assertEquals(
                List.of(
                        "Alice (advisor: Smith) is enrolled in 3 classes",
                        "Bob (advisor: Smith) is enrolled in 3 classes",
                        "Diana (advisor: Brown) is enrolled in 2 classes"
                ),
                observation.value()
        );

        LazySelectReport report = observation.report();
        assertEquals(4, report.getLazySelectCount(), "Expected one lazy load for students and three for student.classes");
        assertEquals(1L, report.countLazySelectsByRelation("students"));
        assertEquals(3L, report.countLazySelectsByRelation("classes"));
        assertEquals(4L, report.countLazySelectsInitiatedBy(SchoolClassRepository.class.getName() + "#findByCourseCode"));
    }

    @Test
    @Transactional
    void testUnselectinatorTracksEntityManagerQueriesToo() {
        ObservationResult<List<String>> observation = unselectinator.observe(() -> {
            SchoolClass schoolClass = entityManager.createQuery(
                            "select sc from SchoolClass sc where sc.courseCode = :courseCode",
                            SchoolClass.class
                    )
                    .setParameter("courseCode", "CS101")
                    .getSingleResult();

            return schoolClass.getStudents().stream()
                    .map(student -> student.getFirstName() + " is enrolled in " + student.getClasses().size() + " classes")
                    .sorted()
                    .toList();
        });

        assertEquals(
                List.of(
                        "Alice is enrolled in 3 classes",
                        "Bob is enrolled in 3 classes",
                        "Diana is enrolled in 2 classes"
                ),
                observation.value()
        );

        LazySelectReport report = observation.report();
        assertEquals(4, report.getLazySelectCount(), "Expected one lazy load for students and three for student.classes");
        assertEquals(4L, report.countLazySelectsInitiatedBy("jakarta.persistence.EntityManager#createQuery"));
    }

    @Test
    void testIndexinatorDetectsMissingIndexes() throws Exception {
        Indexinator indexinator = new Indexinator();

        try (Connection connection = dataSource.getConnection()) {
            // Test with repository analysis
            InspectionReport report = indexinator.inspect(
                    connection,
                    List.of(Teacher.class, Student.class, SchoolClass.class),
                    List.of(SchoolClassRepository.class)
            );

            // Print the report for debugging
            System.out.println(report);

            // With our static SQL schema, we intentionally omit indexes on FK columns!
            // This allows Indexinator to detect real issues.

            // Verify the tool ran successfully
            assertNotNull(report, "Report should not be null");
            assertTrue(report.getTablesInspected() > 0, "Should inspect at least one table");
            assertEquals(3, report.getEntitiesAnalyzed(), "Should analyze 3 entities");

            // We expect to find missing FK indexes!
            assertTrue(report.hasIssues(), "Should detect missing indexes in the schema");

            // We expect at least 2 missing FK indexes:
            // 1. classes.teacher_id (ManyToOne)
            // 2. students.advisor_id (ManyToOne)
            List<Issue> fkIndexIssues = report.getIssues().stream()
                    .filter(issue -> issue.getType() == IssueType.MISSING_FK_INDEX)
                    .toList();

            assertTrue(fkIndexIssues.size() >= 2,
                    "Should detect at least 2 missing FK indexes. Found: " + fkIndexIssues.size());

            // Verify specific issues
            boolean foundTeacherIdIssue = fkIndexIssues.stream()
                    .anyMatch(issue -> issue.getColumnName() != null &&
                            issue.getColumnName().equalsIgnoreCase("teacher_id"));

            boolean foundAdvisorIdIssue = fkIndexIssues.stream()
                    .anyMatch(issue -> issue.getColumnName() != null &&
                            issue.getColumnName().equalsIgnoreCase("advisor_id"));

            assertTrue(foundTeacherIdIssue,
                    "Should detect missing index on classes.teacher_id");
            assertTrue(foundAdvisorIdIssue,
                    "Should detect missing index on students.advisor_id");

            // Verify issue severities
            long highSeverityCount = report.getIssuesBySeverity(IssueSeverity.HIGH).size();
            assertTrue(highSeverityCount > 0,
                    "Should have HIGH severity issues for ManyToOne FKs");

            // Check for repository query index issues
            List<Issue> queryIndexIssues = report.getIssues().stream()
                    .filter(issue -> issue.getType() == IssueType.MISSING_QUERY_INDEX)
                    .toList();

            // We should find at least 1 query index issue (course_name from findAllByCourseNameStartsWith)
            assertEquals(3, queryIndexIssues.size(),
                    "Should detect both missing query index.");

            // Check for missing unique indexes issues
            List<Issue> queryUniqueIssues = report.getIssues().stream()
                    .filter(issue -> issue.getType() == IssueType.MISSING_UNIQUE_INDEX)
                    .toList();

            // We should find at least 1 query index issue (course_name from findAllByCourseNameStartsWith)
            assertEquals(1, queryUniqueIssues.size(),
                    "Should detect at least 1 missing unique index. Found: " + queryUniqueIssues.size());

            System.out.println("Findings:");
            System.out.println("   - " + fkIndexIssues.size() + " missing FK indexes");
            System.out.println("   - " + queryIndexIssues.size() + " missing query indexes");
        }
    }

    @Test
    void testIndexinatorReportStructure() throws Exception {
        Indexinator indexinator = new Indexinator();

        try (Connection connection = dataSource.getConnection()) {
            InspectionReport report = indexinator.inspect(
                    connection,
                    Teacher.class,
                    Student.class,
                    SchoolClass.class
            );

            // Verify report structure
            assertNotNull(report.getTimestamp(), "Report should have a timestamp");
            assertTrue(report.getTablesInspected() > 0, "Should inspect at least one table");
            assertEquals(3, report.getEntitiesAnalyzed(), "Should analyze 3 entities");

            // Verify report can be converted to string
            String reportString = report.toString();
            assertNotNull(reportString, "Report should convert to string");
            assertTrue(reportString.contains("Indexinator"), "Report should contain 'Indexinator'");
        }
    }

    @Test
    void testBuilderPatternWithExplicitClasses() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            InspectionReport report = Indexinator.builder()
                    .withEntities(Teacher.class, Student.class, SchoolClass.class)
                    .withRepositories(TeacherRepository.class, StudentRepository.class, SchoolClassRepository.class)
                    .build()
                    .inspect(connection);

            // Should produce same results as traditional approach
            assertNotNull(report, "Report should not be null");
            assertTrue(report.hasIssues(), "Should detect missing indexes");
            assertEquals(3, report.getEntitiesAnalyzed(), "Should analyze 3 entities");
        }
    }

    @Test
    void testBuilderPatternWithPackageScanning() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            InspectionReport report = Indexinator.builder()
                    .withScannedEntities("io.github.jespersm.indexinator.test.entity")
                    .withScannedRepositories("io.github.jespersm.indexinator.test.repository")
                    .build()
                    .inspect(connection);

            // Should find all 3 entities via scanning
            assertNotNull(report, "Report should not be null");
            assertTrue(report.hasIssues(), "Should detect missing indexes");
            assertEquals(3, report.getEntitiesAnalyzed(), "Should find 3 entities via package scan");

            // Should detect repository query issues via scanning
            List<Issue> queryIndexIssues = report.getIssues().stream()
                    .filter(issue -> issue.getType() == IssueType.MISSING_QUERY_INDEX)
                    .toList();
            assertTrue(queryIndexIssues.size() >= 1,
                    "Should detect query index issues from scanned repositories");
        }
    }

    @Test
    void testBuilderPatternWithExclusions() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            InspectionReport report = Indexinator.builder()
                    .withScannedEntities("io.github.jespersm.indexinator.test.entity")
                    .exceptEntity(Teacher.class)
                    .build()
                    .inspect(connection);

            // Should only analyze 2 entities (Student and SchoolClass)
            assertNotNull(report, "Report should not be null");
            assertEquals(2, report.getEntitiesAnalyzed(), "Should analyze only 2 entities after exclusion");
        }
    }
}


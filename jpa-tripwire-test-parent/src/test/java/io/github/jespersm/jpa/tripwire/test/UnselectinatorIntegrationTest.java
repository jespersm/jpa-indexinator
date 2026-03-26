package io.github.jespersm.jpa.tripwire.test;

import io.github.jespersm.jpa.tripwire.test.entity.SchoolClass;
import io.github.jespersm.jpa.tripwire.test.repository.SchoolClassRepository;
import io.github.jespersm.jpa.tripwire.unselectinator.core.LazySelectReport;
import io.github.jespersm.jpa.tripwire.unselectinator.core.ObservationResult;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for Indexinator using Spring Boot 3.5 + Testcontainers PostgreSQL
 */
class UnselectinatorIntegrationTest extends AbstractTripwireTest {
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
}


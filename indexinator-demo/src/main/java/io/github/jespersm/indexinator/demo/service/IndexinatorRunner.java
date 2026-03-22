package io.github.jespersm.indexinator.demo.service;

import io.github.jespersm.indexinator.core.Indexinator;
import io.github.jespersm.indexinator.core.model.InspectionReport;
import io.github.jespersm.indexinator.demo.entity.SchoolClass;
import io.github.jespersm.indexinator.demo.entity.Student;
import io.github.jespersm.indexinator.demo.entity.Teacher;
import io.github.jespersm.indexinator.demo.repository.SchoolClassRepository;
import io.github.jespersm.indexinator.demo.repository.StudentRepository;
import io.github.jespersm.indexinator.demo.repository.TeacherRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManagerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.Arrays;

/**
 * Runs Indexinator inspection after application startup
 */
@Component
public class IndexinatorRunner implements CommandLineRunner {

    private final DataSource dataSource;
    private final EntityManagerFactory entityManagerFactory;

    public IndexinatorRunner(DataSource dataSource, EntityManagerFactory entityManagerFactory) {
        this.dataSource = dataSource;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public void run(String... args) throws Exception {
        // Give the data initialization service time to complete
        Thread.sleep(1000);

        System.out.println("\n========================================");
        System.out.println("Running Indexinator Database Inspection");
        System.out.println("========================================\n");

        try (Connection connection = dataSource.getConnection()) {
            // Demo 1: Traditional approach (explicit class listing)
            System.out.println("Method 1: Traditional explicit class listing");
            System.out.println("--------------------------------------------");
            Indexinator indexinator = new Indexinator();
            InspectionReport report1 = indexinator.inspect(
                    connection,
                    entityManagerFactory,
                    Arrays.asList(Teacher.class, Student.class, SchoolClass.class),
                    Arrays.asList(TeacherRepository.class, StudentRepository.class, SchoolClassRepository.class)
            );
            System.out.println(report1);

            // Demo 2: Builder pattern with package scanning
            System.out.println("\n\nMethod 2: Builder pattern with package scanning");
            System.out.println("------------------------------------------------");
            InspectionReport report2 = Indexinator.builder()
                    .withScannedEntities("io.github.jespersm.indexinator.demo.entity")
                    .withScannedRepositories("io.github.jespersm.indexinator.demo.repository")
                    .build()
                    .inspect(connection);
            System.out.println(report2);
      }
    }
}

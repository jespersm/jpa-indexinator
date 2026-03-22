package io.github.jespersm.indexinator.demo.repository;

import io.github.jespersm.indexinator.demo.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}

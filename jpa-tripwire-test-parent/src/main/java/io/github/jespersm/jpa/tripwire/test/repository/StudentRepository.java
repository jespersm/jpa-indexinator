package io.github.jespersm.jpa.tripwire.test.repository;

import io.github.jespersm.jpa.tripwire.test.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
}


package com.tuition.tms.student.repository;

import com.tuition.tms.student.entity.Enrollment;
import com.tuition.tms.student.entity.enums.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByStudentId(Long studentId);

    List<Enrollment> findByBatchId(Long batchId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    boolean existsByStudentIdAndBatchId(Long studentId, Long batchId);
}

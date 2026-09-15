package com.tuition.teacher.batch;

import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherBatchRepository extends JpaRepository<TeacherBatch, Long> {

    boolean existsByTeacherIdAndBatchId(Long teacherId, Long batchId);
}
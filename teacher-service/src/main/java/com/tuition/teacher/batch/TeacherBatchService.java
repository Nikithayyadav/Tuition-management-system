package com.tuition.teacher.batch;

import org.springframework.stereotype.Service;

@Service
public class TeacherBatchService {

    private final TeacherBatchRepository teacherBatchRepository;

    public TeacherBatchService(TeacherBatchRepository teacherBatchRepository) {
        this.teacherBatchRepository = teacherBatchRepository;
    }

    public TeacherBatch assignTeacherToBatch(Long teacherId, Long batchId) {

        if (teacherBatchRepository.existsByTeacherIdAndBatchId(
                teacherId, batchId)) {

            throw new TeacherBatchAlreadyExistsException(
                    "Teacher is already assigned to this batch");
        }

        TeacherBatch teacherBatch = new TeacherBatch();

        teacherBatch.setTeacherId(teacherId);
        teacherBatch.setBatchId(batchId);

        return teacherBatchRepository.save(teacherBatch);
    }
}
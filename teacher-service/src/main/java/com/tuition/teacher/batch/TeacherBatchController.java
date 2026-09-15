package com.tuition.teacher.batch;

import com.tuition.teacher.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/teacher-batches")
public class TeacherBatchController {

    private final TeacherBatchService teacherBatchService;

    public TeacherBatchController(TeacherBatchService teacherBatchService) {
        this.teacherBatchService = teacherBatchService;
    }

    @PostMapping
    public ApiResponse<TeacherBatch> assignTeacherToBatch(
            @RequestParam Long teacherId,
            @RequestParam Long batchId) {

        TeacherBatch teacherBatch =
                teacherBatchService.assignTeacherToBatch(
                        teacherId, batchId);

        return new ApiResponse<>(
                true,
                teacherBatch,
                null,
                null
        );
    }
    @GetMapping
    public ApiResponse<List<TeacherBatch>> getAllTeacherBatches() {

        List<TeacherBatch> teacherBatches =
                teacherBatchService.getAllTeacherBatches();

        return new ApiResponse<>(
                true,
                teacherBatches,
                null,
                null
        );
    }
}
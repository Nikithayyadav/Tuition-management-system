package com.tuition.tms.student.dto.response;

import com.tuition.tms.student.entity.enums.EnrollmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response details for student enrollment")
public class EnrollmentResponse {

    @Schema(description = "Enrollment record ID", example = "1")
    private Long id;

    @Schema(description = "Student ID", example = "10")
    private Long studentId;

    @Schema(description = "Student Code", example = "STU-00001")
    private String studentCode;

    @Schema(description = "Student Full Name", example = "Rahul Sharma")
    private String studentName;

    @Schema(description = "Batch ID", example = "1")
    private Long batchId;

    @Schema(description = "Batch Name", example = "MRG_BATCH")
    private String batchName;

    @Schema(description = "Batch Start Time", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "Batch End Time", example = "11:00:00")
    private LocalTime endTime;

    @Schema(description = "Enrollment Status", example = "ACTIVE")
    private EnrollmentStatus status;

    @Schema(description = "Enrollment creation timestamp")
    private LocalDateTime createdAt;
}

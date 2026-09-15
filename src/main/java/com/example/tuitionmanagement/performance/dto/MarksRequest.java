package com.example.tuitionmanagement.performance.dto;

import com.example.tuitionmanagement.performance.entity.ExamName;
import com.example.tuitionmanagement.performance.entity.Subject;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

import lombok.Data;

@Data
@Schema(description = "Request for entering student examination marks")
public class MarksRequest {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be greater than zero")
    @Schema(
            description = "Student ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long studentId;

    @NotNull(message = "Exam name is required")
    @Schema(
            description = "Examination name",
            example = "MOCK_EXAM",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ExamName examName;

    @NotNull(message = "Subject is required")
    @Schema(
            description = "Subject name",
            example = "JAVA",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Subject subject;

    @NotNull(message = "Marks obtained are required")
    @DecimalMin(
            value = "0.00",
            inclusive = true,
            message = "Marks obtained cannot be negative"
    )
    @Schema(
            description = "Marks obtained by the student",
            example = "85",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal marksObtained;

    @NotNull(message = "Maximum marks are required")
    @DecimalMin(
            value = "0.01",
            inclusive = true,
            message = "Maximum marks must be greater than zero"
    )
    @Schema(
            description = "Maximum marks for the examination",
            example = "100",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private BigDecimal maximumMarks;
}
package com.tuition.tms.student.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for enrolling a student and assigning to a batch")
public class StudentEnrollmentRequest {

    @NotNull(message = "Batch ID is required")
    @Positive(message = "Batch ID must be a positive number")
    @Schema(description = "ID of the batch to assign the student to", example = "1")
    private Long batchId;
}

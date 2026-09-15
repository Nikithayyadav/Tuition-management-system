package com.tuition.tms.student.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response payload for available batch details")
public class BatchResponse {

    @Schema(description = "Batch unique ID", example = "1")
    private Long id;

    @Schema(description = "Batch name", example = "MRG_BATCH")
    private String name;

    @Schema(description = "Start time", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "End time", example = "11:00:00")
    private LocalTime endTime;
}

package com.example.tuitionmanagement.fee.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FeeStructureResponse {

    private Long id;

    private Long batchId;

    private Long subjectId;

    private BigDecimal annualFee;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
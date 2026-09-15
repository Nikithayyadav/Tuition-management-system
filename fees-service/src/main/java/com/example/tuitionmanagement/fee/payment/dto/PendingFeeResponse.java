package com.example.tuitionmanagement.fee.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PendingFeeResponse {

    private Long studentId;

    private String studentCode;

    private String studentName;

    private Long batchId;

    private String batchName;

    private Long feeStructureId;

    private Long subjectId;

    private BigDecimal annualFee;

    private BigDecimal totalAmountPaid;

    private BigDecimal remainingAmount;
}
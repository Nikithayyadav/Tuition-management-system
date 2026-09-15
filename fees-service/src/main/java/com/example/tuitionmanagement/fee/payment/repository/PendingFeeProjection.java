package com.example.tuitionmanagement.fee.payment.repository;

import java.math.BigDecimal;

public interface PendingFeeProjection {

    Long getStudentId();

    String getStudentCode();

    String getStudentName();

    Long getBatchId();

    String getBatchName();

    Long getFeeStructureId();

    Long getSubjectId();

    BigDecimal getAnnualFee();

    BigDecimal getTotalAmountPaid();

    BigDecimal getRemainingAmount();
}
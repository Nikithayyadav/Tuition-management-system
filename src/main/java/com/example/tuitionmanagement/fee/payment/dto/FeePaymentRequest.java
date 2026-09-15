package com.example.tuitionmanagement.fee.payment.dto;

import com.example.tuitionmanagement.fee.payment.entity.PaymentMethod;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class FeePaymentRequest {

    @NotNull(message = "Student ID is required")
    @Positive(message = "Student ID must be greater than zero")
    private Long studentId;

    @NotNull(message = "Fee structure ID is required")
    @Positive(message = "Fee structure ID must be greater than zero")
    private Long feeStructureId;

    @NotNull(message = "Amount paid is required")
    @DecimalMin(
            value = "0.01",
            message = "Amount paid must be greater than zero"
    )
    private BigDecimal amountPaid;

    @NotNull(message = "Payment date is required")
    private LocalDate paymentDate;

    @NotNull(message = "Payment method is required")
    private PaymentMethod paymentMethod;
}
package com.example.tuitionmanagement.fee.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class FeeStructureRequest {

    @NotNull(message = "Annual fee is required and cannot be empty")
    @DecimalMin(
            value = "0.01",
            message = "Annual fee must be greater than zero"
    )
    private BigDecimal annualFee;
}
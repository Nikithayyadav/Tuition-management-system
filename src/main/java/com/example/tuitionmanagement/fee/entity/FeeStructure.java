package com.example.tuitionmanagement.fee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "fee_structures",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"batch_id", "subject_id"}
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeeStructure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "batch_id", nullable = false)
    private Long batchId;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Column(
            name = "annual_fee",
            nullable = false,
            precision = 10,
            scale = 2
    )
    private BigDecimal annualFee;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
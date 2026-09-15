package com.example.tuitionmanagement.fee.payment.repository;

import com.example.tuitionmanagement.fee.payment.entity.FeePayment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.tuitionmanagement.fee.payment.repository.PendingFeeProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.math.BigDecimal;

public interface FeePaymentRepository extends JpaRepository<FeePayment, Long> {

    Page<FeePayment> findByStudentId(
            Long studentId,
            Pageable pageable
    );

    Page<FeePayment> findByFeeStructureId(
            Long feeStructureId,
            Pageable pageable
    );

    Page<FeePayment> findByStudentIdAndFeeStructureId(
            Long studentId,
            Long feeStructureId,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(p.amountPaid), 0)
            FROM FeePayment p
            WHERE p.studentId = :studentId
              AND p.feeStructureId = :feeStructureId
              AND p.paymentStatus = com.example.tuitionmanagement.fee.payment.entity.PaymentStatus.SUCCESS
            """)
    BigDecimal findTotalPaidAmount(
            @Param("studentId") Long studentId,
            @Param("feeStructureId") Long feeStructureId
    );

    @Query(
            value = """
                SELECT
                    s.id AS studentId,
                    s.student_code AS studentCode,
                    CONCAT(s.first_name, ' ', s.last_name) AS studentName,
                    b.id AS batchId,
                    b.name AS batchName,
                    fs.id AS feeStructureId,
                    fs.subject_id AS subjectId,
                    fs.annual_fee AS annualFee,

                    COALESCE(
                        SUM(
                            CASE
                                WHEN fp.payment_status = 'SUCCESS'
                                THEN fp.amount_paid
                                ELSE 0
                            END
                        ),
                        0
                    ) AS totalAmountPaid,

                    (
                        fs.annual_fee -
                        COALESCE(
                            SUM(
                                CASE
                                    WHEN fp.payment_status = 'SUCCESS'
                                    THEN fp.amount_paid
                                    ELSE 0
                                END
                            ),
                            0
                        )
                    ) AS remainingAmount

                FROM students s

                INNER JOIN enrollments e
                    ON e.student_id = s.id
                    AND e.status = 'ACTIVE'

                INNER JOIN batches b
                    ON b.id = e.batch_id

                INNER JOIN fee_structures fs
                    ON fs.batch_id = b.id

                LEFT JOIN fee_payments fp
                    ON fp.student_id = s.id
                    AND fp.fee_structure_id = fs.id

                WHERE
                    (:studentId IS NULL OR s.id = :studentId)
                    AND (:batchId IS NULL OR b.id = :batchId)
                    AND (:feeStructureId IS NULL OR fs.id = :feeStructureId)
                    AND (:subjectId IS NULL OR fs.subject_id = :subjectId)
                    AND (:studentStatus IS NULL OR s.status = :studentStatus)

                GROUP BY
                    s.id,
                    s.student_code,
                    s.first_name,
                    s.last_name,
                    b.id,
                    b.name,
                    fs.id,
                    fs.subject_id,
                    fs.annual_fee

                HAVING
                    (
                        fs.annual_fee -
                        COALESCE(
                            SUM(
                                CASE
                                    WHEN fp.payment_status = 'SUCCESS'
                                    THEN fp.amount_paid
                                    ELSE 0
                                END
                            ),
                            0
                        )
                    ) > 0

                ORDER BY s.id
                """,
            countQuery = """
                SELECT COUNT(*)
                FROM (
                    SELECT
                        s.id,
                        fs.id

                    FROM students s

                    INNER JOIN enrollments e
                        ON e.student_id = s.id
                        AND e.status = 'ACTIVE'

                    INNER JOIN batches b
                        ON b.id = e.batch_id

                    INNER JOIN fee_structures fs
                        ON fs.batch_id = b.id

                    LEFT JOIN fee_payments fp
                        ON fp.student_id = s.id
                        AND fp.fee_structure_id = fs.id

                    WHERE
                        (:studentId IS NULL OR s.id = :studentId)
                        AND (:batchId IS NULL OR b.id = :batchId)
                        AND (:feeStructureId IS NULL OR fs.id = :feeStructureId)
                        AND (:subjectId IS NULL OR fs.subject_id = :subjectId)
                        AND (:studentStatus IS NULL OR s.status = :studentStatus)

                    GROUP BY
                        s.id,
                        fs.id,
                        fs.annual_fee

                    HAVING
                        (
                            fs.annual_fee -
                            COALESCE(
                                SUM(
                                    CASE
                                        WHEN fp.payment_status = 'SUCCESS'
                                        THEN fp.amount_paid
                                        ELSE 0
                                    END
                                ),
                                0
                            )
                        ) > 0
                ) AS pending_fees
                """,
            nativeQuery = true
    )
    Page<PendingFeeProjection> findPendingFees(
            @Param("studentId") Long studentId,
            @Param("batchId") Long batchId,
            @Param("feeStructureId") Long feeStructureId,
            @Param("subjectId") Long subjectId,
            @Param("studentStatus") String studentStatus,
            Pageable pageable
    );
}
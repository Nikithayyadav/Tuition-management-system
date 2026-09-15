package com.example.tuitionmanagement.fee.payment.service;

import com.example.tuitionmanagement.exception.BadRequestException;
import com.example.tuitionmanagement.fee.entity.FeeStructure;
import com.example.tuitionmanagement.fee.payment.dto.FeePaymentRequest;
import com.example.tuitionmanagement.fee.payment.dto.FeePaymentResponse;
import com.example.tuitionmanagement.fee.payment.dto.PendingFeeResponse;
import com.example.tuitionmanagement.fee.payment.entity.FeePayment;
import com.example.tuitionmanagement.fee.payment.entity.PaymentStatus;
import com.example.tuitionmanagement.fee.payment.repository.FeePaymentRepository;
import com.example.tuitionmanagement.fee.payment.repository.PendingFeeProjection;
import com.example.tuitionmanagement.fee.repository.FeeStructureRepository;
import com.example.tuitionmanagement.response.ApiResponse;
import com.example.tuitionmanagement.response.MetaResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeePaymentServiceImpl implements FeePaymentService {

    @Autowired
    private FeePaymentRepository feePaymentRepository;

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private Clock indianClock;

    // ----------------------------------------------------
    // RECORD PAYMENT
    // ----------------------------------------------------

    @Override
    public ResponseEntity<ApiResponse<FeePaymentResponse>> recordPayment(
            FeePaymentRequest request) {

        LocalDate currentDate = LocalDate.now(indianClock);

        if (request.getPaymentDate().isBefore(currentDate)) {
            throw new BadRequestException(
                    "Payment date cannot be in the past"
            );
        }

        FeeStructure feeStructure =
                feeStructureRepository.findById(
                        request.getFeeStructureId()
                ).orElseThrow(() ->
                        new BadRequestException(
                                "No fee structure found with id "
                                        + request.getFeeStructureId()
                        )
                );

        BigDecimal annualFee = feeStructure.getAnnualFee();

        BigDecimal alreadyPaid =
                feePaymentRepository.findTotalPaidAmount(
                        request.getStudentId(),
                        request.getFeeStructureId()
                );

        if (alreadyPaid == null) {
            alreadyPaid = BigDecimal.ZERO;
        }

        BigDecimal remainingBeforePayment =
                annualFee.subtract(alreadyPaid);

        if (remainingBeforePayment.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "The annual fee has already been fully paid"
            );
        }

        if (request.getAmountPaid().compareTo(
                remainingBeforePayment
        ) > 0) {

            throw new BadRequestException(
                    "Payment amount exceeds the remaining fee. "
                            + "Remaining amount is "
                            + remainingBeforePayment
            );
        }

        BigDecimal totalAmountPaid =
                alreadyPaid.add(request.getAmountPaid());

        BigDecimal remainingAmount =
                annualFee.subtract(totalAmountPaid);

        LocalDateTime currentTime =
                LocalDateTime.now(indianClock);

        FeePayment feePayment = FeePayment.builder()
                .studentId(request.getStudentId())
                .feeStructureId(request.getFeeStructureId())
                .amountPaid(request.getAmountPaid())
                .paymentDate(request.getPaymentDate())
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(PaymentStatus.SUCCESS)
                .createdAt(currentTime)
                .updatedAt(currentTime)
                .build();

        FeePayment savedPayment =
                feePaymentRepository.save(feePayment);

        FeePaymentResponse paymentResponse =
                FeePaymentResponse.builder()
                        .id(savedPayment.getId())
                        .studentId(savedPayment.getStudentId())
                        .feeStructureId(savedPayment.getFeeStructureId())
                        .amountPaid(savedPayment.getAmountPaid())
                        .paymentDate(savedPayment.getPaymentDate())
                        .paymentMethod(savedPayment.getPaymentMethod())
                        .paymentStatus(savedPayment.getPaymentStatus())
                        .annualFee(annualFee)
                        .totalAmountPaid(totalAmountPaid)
                        .remainingAmount(remainingAmount)
                        .createdAt(savedPayment.getCreatedAt())
                        .updatedAt(savedPayment.getUpdatedAt())
                        .build();

        String message;

        if (remainingAmount.compareTo(BigDecimal.ZERO) == 0) {
            message = "Full payment completed successfully";
        } else {
            message = "Partial payment done successfully. "
                    + "Please pay the remaining fee before the due date";
        }

        ApiResponse<FeePaymentResponse> response =
                ApiResponse.<FeePaymentResponse>builder()
                        .success(true)
                        .data(paymentResponse)
                        .error(null)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message(message)
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ----------------------------------------------------
    // GET PAYMENTS
    // ----------------------------------------------------

    @Override
    public ResponseEntity<ApiResponse<?>> getPayments(
            Long paymentId,
            Long studentId,
            Long feeStructureId,
            int page,
            int size) {

        validatePagination(page, size);

        PageRequest pageRequest =
                PageRequest.of(page, size);

        Page<FeePayment> paymentPage;

        if (paymentId != null) {

            FeePayment feePayment =
                    feePaymentRepository.findById(paymentId)
                            .orElseThrow(() ->
                                    new BadRequestException(
                                            "No payment found with id "
                                                    + paymentId
                                    )
                            );

            List<FeePaymentResponse> data =
                    List.of(mapToResponse(feePayment));

            return buildListResponse(
                    data,
                    "Fee payment retrieved successfully"
            );
        }

        if (studentId != null && feeStructureId != null) {

            paymentPage =
                    feePaymentRepository
                            .findByStudentIdAndFeeStructureId(
                                    studentId,
                                    feeStructureId,
                                    pageRequest
                            );

        } else if (studentId != null) {

            paymentPage =
                    feePaymentRepository.findByStudentId(
                            studentId,
                            pageRequest
                    );

        } else if (feeStructureId != null) {

            paymentPage =
                    feePaymentRepository.findByFeeStructureId(
                            feeStructureId,
                            pageRequest
                    );

        } else {

            paymentPage =
                    feePaymentRepository.findAll(pageRequest);
        }

        List<FeePaymentResponse> data =
                paymentPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return buildListResponse(
                data,
                "Fee payments retrieved successfully"
        );
    }

    // ----------------------------------------------------
    // GET PAYMENT BY ID
    // ----------------------------------------------------

    @Override
    public ResponseEntity<ApiResponse<FeePaymentResponse>> getPaymentById(
            Long paymentId) {

        FeePayment feePayment =
                feePaymentRepository.findById(paymentId)
                        .orElseThrow(() ->
                                new BadRequestException(
                                        "No payment found with id "
                                                + paymentId
                                )
                        );

        ApiResponse<FeePaymentResponse> response =
                ApiResponse.<FeePaymentResponse>builder()
                        .success(true)
                        .data(mapToResponse(feePayment))
                        .error(null)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message(
                                                "Fee payment retrieved successfully"
                                        )
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // ----------------------------------------------------
    // GET PENDING FEES
    // ----------------------------------------------------

    @Override
    public ResponseEntity<ApiResponse<?>> getPendingFees(
            Long studentId,
            Long batchId,
            Long feeStructureId,
            Long subjectId,
            String studentStatus,
            int page,
            int size) {

        validatePagination(page, size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by(
                                Sort.Direction.ASC,
                                "studentId"
                        )
                );

        Page<PendingFeeProjection> pendingFeePage =
                feePaymentRepository.findPendingFees(
                        studentId,
                        batchId,
                        feeStructureId,
                        subjectId,
                        studentStatus,
                        pageable
                );

        Page<PendingFeeResponse> responsePage =
                pendingFeePage.map(
                        this::mapPendingFeeResponse
                );

        ApiResponse<Page<PendingFeeResponse>> response =
                ApiResponse.<Page<PendingFeeResponse>>builder()
                        .success(true)
                        .data(responsePage)
                        .error(null)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message(
                                                "Pending fees retrieved successfully"
                                        )
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    // ----------------------------------------------------
    // VALIDATE PAGINATION
    // ----------------------------------------------------

    private void validatePagination(
            int page,
            int size) {

        if (page < 0) {
            throw new BadRequestException(
                    "Page number must be greater than or equal to zero"
            );
        }

        if (size <= 0 || size > 100) {
            throw new BadRequestException(
                    "Page size must be between 1 and 100"
            );
        }
    }

    // ----------------------------------------------------
    // MAP FEE PAYMENT RESPONSE
    // ----------------------------------------------------

    private FeePaymentResponse mapToResponse(
            FeePayment entity) {

        return FeePaymentResponse.builder()
                .id(entity.getId())
                .studentId(entity.getStudentId())
                .feeStructureId(entity.getFeeStructureId())
                .amountPaid(entity.getAmountPaid())
                .paymentDate(entity.getPaymentDate())
                .paymentMethod(entity.getPaymentMethod())
                .paymentStatus(entity.getPaymentStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    // ----------------------------------------------------
    // MAP PENDING FEE RESPONSE
    // ----------------------------------------------------

    private PendingFeeResponse mapPendingFeeResponse(
            PendingFeeProjection projection) {

        return PendingFeeResponse.builder()
                .studentId(projection.getStudentId())
                .studentCode(projection.getStudentCode())
                .studentName(projection.getStudentName())
                .batchId(projection.getBatchId())
                .batchName(projection.getBatchName())
                .feeStructureId(projection.getFeeStructureId())
                .subjectId(projection.getSubjectId())
                .annualFee(projection.getAnnualFee())
                .totalAmountPaid(projection.getTotalAmountPaid())
                .remainingAmount(projection.getRemainingAmount())
                .build();
    }

    // ----------------------------------------------------
    // BUILD PAYMENT LIST RESPONSE
    // ----------------------------------------------------

    private ResponseEntity<ApiResponse<?>> buildListResponse(
            List<FeePaymentResponse> data,
            String message) {

        ApiResponse<?> response =
                ApiResponse.builder()
                        .success(true)
                        .data(data)
                        .error(null)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message(message)
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
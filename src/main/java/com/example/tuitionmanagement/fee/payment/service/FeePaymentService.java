package com.example.tuitionmanagement.fee.payment.service;

import com.example.tuitionmanagement.fee.payment.dto.FeePaymentRequest;
import com.example.tuitionmanagement.fee.payment.dto.FeePaymentResponse;
import com.example.tuitionmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;


public interface FeePaymentService {

    ResponseEntity<ApiResponse<FeePaymentResponse>> recordPayment(
            FeePaymentRequest request
    );

    ResponseEntity<ApiResponse<?>> getPayments(
            Long paymentId,
            Long studentId,
            Long feeStructureId,
            int page,
            int size
    );

    ResponseEntity<ApiResponse<FeePaymentResponse>> getPaymentById(
            Long paymentId
    );

    ResponseEntity<ApiResponse<?>> getPendingFees(
            Long studentId,
            Long batchId,
            Long feeStructureId,
            Long subjectId,
            String studentStatus,
            int page,
            int size
    );
}
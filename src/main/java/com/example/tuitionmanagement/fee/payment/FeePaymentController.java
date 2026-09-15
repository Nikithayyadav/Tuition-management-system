package com.example.tuitionmanagement.fee.payment;

import com.example.tuitionmanagement.fee.payment.dto.FeePaymentRequest;
import com.example.tuitionmanagement.fee.payment.dto.FeePaymentResponse;
import com.example.tuitionmanagement.fee.payment.service.FeePaymentService;
import com.example.tuitionmanagement.response.ApiResponse;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fees/payments")
public class FeePaymentController {

    @Autowired
    private FeePaymentService feePaymentService;

    // ----------------------------------------------------
    // RECORD PAYMENT
    // ----------------------------------------------------

    @PostMapping
    public ResponseEntity<ApiResponse<FeePaymentResponse>> recordPayment(
            @Valid @RequestBody FeePaymentRequest request) {

        return feePaymentService.recordPayment(request);
    }

    // ----------------------------------------------------
    // GET PAYMENTS
    // ----------------------------------------------------

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPayments(
            @RequestParam(required = false) Long paymentId,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long feeStructureId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return feePaymentService.getPayments(
                paymentId,
                studentId,
                feeStructureId,
                page,
                size
        );
    }

    // ----------------------------------------------------
    // CHECK PENDING FEES
    // ----------------------------------------------------

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<?>> getPendingFees(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long batchId,
            @RequestParam(required = false) Long feeStructureId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) String studentStatus,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return feePaymentService.getPendingFees(
                studentId,
                batchId,
                feeStructureId,
                subjectId,
                studentStatus,
                page,
                size
        );
    }
}
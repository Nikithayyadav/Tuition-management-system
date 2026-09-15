package com.example.tuitionmanagement.fee.service;

import com.example.tuitionmanagement.fee.dto.FeeStructureRequest;
import com.example.tuitionmanagement.fee.dto.FeeStructureResponse;
import com.example.tuitionmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface FeeStructureService {

    ResponseEntity<ApiResponse<?>> getFeeStructures(
            Long batchId,
            int page,
            int size
    );

    ResponseEntity<ApiResponse<FeeStructureResponse>> updateFeeStructure(
            Long feeId,
            FeeStructureRequest request
    );
}
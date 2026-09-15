package com.example.tuitionmanagement.fee.service;

import com.example.tuitionmanagement.fee.dto.FeeStructureRequest;
import com.example.tuitionmanagement.fee.dto.FeeStructureResponse;
import com.example.tuitionmanagement.fee.entity.FeeStructure;
import com.example.tuitionmanagement.fee.repository.FeeStructureRepository;
import com.example.tuitionmanagement.response.ApiResponse;
import com.example.tuitionmanagement.response.MetaResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class FeeStructureServiceImpl implements FeeStructureService {

    @Autowired
    private FeeStructureRepository feeStructureRepository;

    @Autowired
    private Clock indianClock;

    @Override
    public ResponseEntity<ApiResponse<?>> getFeeStructures(
            Long batchId,
            int page,
            int size) {

        if (page < 0) {
            throw new RuntimeException("Page number cannot be negative");
        }

        if (size <= 0) {
            throw new RuntimeException("Page size must be greater than zero");
        }

        PageRequest pageRequest = PageRequest.of(page, size);

        Page<FeeStructure> feeStructurePage;

        if (batchId != null) {
            feeStructurePage = feeStructureRepository.findByBatchId(
                    batchId,
                    pageRequest
            );
        } else {
            feeStructurePage = feeStructureRepository.findAll(pageRequest);
        }

        List<FeeStructureResponse> data = feeStructurePage
                .getContent()
                .stream()
                .map(this::mapToResponse)
                .toList();

        ApiResponse<?> response = ApiResponse.builder()
                .success(true)
                .data(data)
                .error(null)
                .meta(
                        MetaResponse.builder()
                                .timestamp(LocalDateTime.now(indianClock))
                                .message("Fee structures retrieved successfully")
                                .build()
                )
                .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Override
    public ResponseEntity<ApiResponse<FeeStructureResponse>>
    updateFeeStructure(
            Long feeId,
            FeeStructureRequest request) {

        FeeStructure feeStructure = feeStructureRepository
                .findById(feeId)
                .orElseThrow(() -> new RuntimeException(
                        "No fee structure found with id " + feeId
                ));

        feeStructure.setAnnualFee(request.getAnnualFee());
        feeStructure.setUpdatedAt(LocalDateTime.now(indianClock));

        FeeStructure updatedFeeStructure =
                feeStructureRepository.save(feeStructure);

        ApiResponse<FeeStructureResponse> response =
                ApiResponse.<FeeStructureResponse>builder()
                        .success(true)
                        .data(mapToResponse(updatedFeeStructure))
                        .error(null)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message(
                                                "Fee structure updated successfully"
                                        )
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    private FeeStructureResponse mapToResponse(FeeStructure entity) {

        return FeeStructureResponse.builder()
                .id(entity.getId())
                .batchId(entity.getBatchId())
                .subjectId(entity.getSubjectId())
                .annualFee(entity.getAnnualFee())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
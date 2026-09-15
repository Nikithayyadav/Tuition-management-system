package com.example.tuitionmanagement.fee;

import com.example.tuitionmanagement.fee.dto.FeeStructureRequest;
import com.example.tuitionmanagement.fee.dto.FeeStructureResponse;
import com.example.tuitionmanagement.fee.service.FeeStructureService;
import com.example.tuitionmanagement.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fees/structure")
public class FeeStructureController {

    @Autowired
    private FeeStructureService feeStructureService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFeeStructures(

            @RequestParam(required = false)
            Long batchId,

            @RequestParam(defaultValue = "0")
            int page,

            @RequestParam(defaultValue = "10")
            int size) {

        return feeStructureService.getFeeStructures(
                batchId,
                page,
                size
        );
    }

    @PutMapping("/{feeId}")
    public ResponseEntity<ApiResponse<FeeStructureResponse>>
    updateFeeStructure(
            @PathVariable Long feeId,
            @Valid @RequestBody FeeStructureRequest request) {

        return feeStructureService.updateFeeStructure(
                feeId,
                request
        );
    }
}
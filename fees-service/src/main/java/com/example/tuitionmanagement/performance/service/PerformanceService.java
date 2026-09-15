package com.example.tuitionmanagement.performance.service;

import com.example.tuitionmanagement.performance.dto.MarksRequest;
import com.example.tuitionmanagement.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface PerformanceService {

    ResponseEntity<ApiResponse<?>> enterMarks(
            MarksRequest request
    );

    ResponseEntity<ApiResponse<?>> getPerformances(
            Long studentId,
            String examName,
            int page,
            int size
    );

    ResponseEntity<ApiResponse<?>> getPerformanceReport(
            Long studentId,
            String examName
    );
}
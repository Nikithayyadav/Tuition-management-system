package com.example.tuitionmanagement.performance.service;

import com.example.tuitionmanagement.performance.dto.MarksRequest;
import com.example.tuitionmanagement.performance.dto.PerformanceReportResponse;
import com.example.tuitionmanagement.performance.dto.PerformanceResponse;
import com.example.tuitionmanagement.performance.entity.ExamName;
import com.example.tuitionmanagement.performance.entity.Performance;
import com.example.tuitionmanagement.performance.repository.PerformanceRepository;
import com.example.tuitionmanagement.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PerformanceServiceImpl implements PerformanceService {

    @Autowired
    private PerformanceRepository performanceRepository;

    @Override
    public ResponseEntity<ApiResponse<?>> enterMarks(MarksRequest request) {

        if (request.getMarksObtained()
                .compareTo(request.getMaximumMarks()) > 0) {

            throw new IllegalArgumentException(
                    "Marks obtained cannot be greater than maximum marks"
            );
        }

        boolean alreadyExists =
                performanceRepository.existsByStudentIdAndExamNameAndSubject(
                        request.getStudentId(),
                        request.getExamName(),
                        request.getSubject()
                );

        if (alreadyExists) {
            throw new IllegalArgumentException(
                    "Marks already entered for this student, exam and subject"
            );
        }

        BigDecimal percentage = calculatePercentage(
                request.getMarksObtained(),
                request.getMaximumMarks()
        );

        String grade = calculateGrade(percentage);

        Performance performance = Performance.builder()
                .studentId(request.getStudentId())
                .examName(request.getExamName())
                .subject(request.getSubject())
                .marksObtained(request.getMarksObtained())
                .maximumMarks(request.getMaximumMarks())
                .percentage(percentage)
                .grade(grade)
                .build();

        Performance savedPerformance =
                performanceRepository.save(performance);

        PerformanceResponse response =
                mapToResponse(savedPerformance);

        ApiResponse<PerformanceResponse> apiResponse =
                ApiResponse.<PerformanceResponse>builder()
                        .success(true)
                        .data(response)
                        .error(null)
                        .meta(null)
                        .build();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getPerformances(
            Long studentId,
            String examName,
            int page,
            int size
    ) {

        validatePagination(page, size);

        PageRequest pageRequest =
                PageRequest.of(page, size);

        Page<Performance> performancePage;

        if (studentId != null && examName != null) {

            ExamName parsedExamName = parseExamName(examName);

            performancePage =
                    performanceRepository.findByStudentIdAndExamName(
                            studentId,
                            parsedExamName,
                            pageRequest
                    );

        } else if (studentId != null) {

            performancePage =
                    performanceRepository.findByStudentId(
                            studentId,
                            pageRequest
                    );

        } else {

            performancePage =
                    performanceRepository.findAll(pageRequest);
        }

        Page<PerformanceResponse> responsePage =
                performancePage.map(this::mapToResponse);

        ApiResponse<Page<PerformanceResponse>> apiResponse =
                ApiResponse.<Page<PerformanceResponse>>builder()
                        .success(true)
                        .data(responsePage)
                        .error(null)
                        .meta(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    @Override
    public ResponseEntity<ApiResponse<?>> getPerformanceReport(
            Long studentId,
            String examName
    ) {

        if (studentId == null || studentId <= 0) {
            throw new IllegalArgumentException(
                    "Student ID must be greater than zero"
            );
        }

        ExamName parsedExamName = parseExamName(examName);

        List<Performance> performances =
                performanceRepository
                        .findByStudentIdAndExamName(
                                studentId,
                                parsedExamName,
                                PageRequest.of(0, 100)
                        )
                        .getContent();

        if (performances.isEmpty()) {
            throw new IllegalArgumentException(
                    "No performance records found for the given student and exam"
            );
        }

        BigDecimal totalMarksObtained = performances.stream()
                .map(Performance::getMarksObtained)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalMaximumMarks = performances.stream()
                .map(Performance::getMaximumMarks)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal overallPercentage =
                calculatePercentage(
                        totalMarksObtained,
                        totalMaximumMarks
                );

        String overallGrade =
                calculateGrade(overallPercentage);

        List<PerformanceResponse> subjectPerformances =
                performances.stream()
                        .map(this::mapToResponse)
                        .toList();

        PerformanceReportResponse report =
                PerformanceReportResponse.builder()
                        .studentId(studentId)
                        .examName(parsedExamName)
                        .totalMarksObtained(totalMarksObtained)
                        .totalMaximumMarks(totalMaximumMarks)
                        .overallPercentage(overallPercentage)
                        .overallGrade(overallGrade)
                        .subjectPerformances(subjectPerformances)
                        .build();

        ApiResponse<PerformanceReportResponse> apiResponse =
                ApiResponse.<PerformanceReportResponse>builder()
                        .success(true)
                        .data(report)
                        .error(null)
                        .meta(null)
                        .build();

        return ResponseEntity.ok(apiResponse);
    }

    private BigDecimal calculatePercentage(
            BigDecimal marksObtained,
            BigDecimal maximumMarks
    ) {

        return marksObtained
                .multiply(BigDecimal.valueOf(100))
                .divide(maximumMarks, 2, RoundingMode.HALF_UP);
    }

    private String calculateGrade(BigDecimal percentage) {

        if (percentage.compareTo(BigDecimal.valueOf(90)) >= 0) {
            return "A+";
        }

        if (percentage.compareTo(BigDecimal.valueOf(80)) >= 0) {
            return "A";
        }

        if (percentage.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return "B";
        }

        if (percentage.compareTo(BigDecimal.valueOf(60)) >= 0) {
            return "C";
        }

        if (percentage.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return "D";
        }

        return "F";
    }

    private ExamName parseExamName(String examName) {

        if (examName == null || examName.isBlank()) {
            throw new IllegalArgumentException(
                    "Exam name is required"
            );
        }

        try {
            return ExamName.valueOf(examName.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Invalid exam name. Allowed values are MOCK_EXAM and PRACTICE_EXAM"
            );
        }
    }

    private PerformanceResponse mapToResponse(
            Performance performance
    ) {

        return PerformanceResponse.builder()
                .id(performance.getId())
                .studentId(performance.getStudentId())
                .examName(performance.getExamName())
                .subject(performance.getSubject())
                .marksObtained(performance.getMarksObtained())
                .maximumMarks(performance.getMaximumMarks())
                .percentage(performance.getPercentage())
                .grade(performance.getGrade())
                .createdAt(performance.getCreatedAt())
                .updatedAt(performance.getUpdatedAt())
                .build();
    }

    private void validatePagination(int page, int size) {

        if (page < 0) {
            throw new IllegalArgumentException(
                    "Page number cannot be negative"
            );
        }

        if (size < 1 || size > 100) {
            throw new IllegalArgumentException(
                    "Page size must be between 1 and 100"
            );
        }
    }
}
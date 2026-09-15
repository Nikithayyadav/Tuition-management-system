package com.example.tuitionmanagement.performance.dto;

import com.example.tuitionmanagement.performance.entity.ExamName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceReportResponse {

    private Long studentId;

    private ExamName examName;

    private BigDecimal totalMarksObtained;

    private BigDecimal totalMaximumMarks;

    private BigDecimal overallPercentage;

    private String overallGrade;

    private List<PerformanceResponse> subjectPerformances;
}
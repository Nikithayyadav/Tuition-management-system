package com.example.tuitionmanagement.performance.dto;

import com.example.tuitionmanagement.performance.entity.ExamName;
import com.example.tuitionmanagement.performance.entity.Subject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceResponse {

    private Long id;
    private Long studentId;
    private ExamName examName;
    private Subject subject;
    private BigDecimal marksObtained;
    private BigDecimal maximumMarks;
    private BigDecimal percentage;
    private String grade;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
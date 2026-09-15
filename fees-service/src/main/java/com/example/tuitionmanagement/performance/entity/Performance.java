package com.example.tuitionmanagement.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "performances",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_student_exam_subject",
                        columnNames = {
                                "student_id",
                                "exam_name",
                                "subject"
                        }
                )
        }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "exam_name", nullable = false, length = 30)
    private ExamName examName;

    @Enumerated(EnumType.STRING)
    @Column(name = "subject", nullable = false, length = 20)
    private Subject subject;

    @Column(name = "marks_obtained", nullable = false, precision = 5, scale = 2)
    private BigDecimal marksObtained;

    @Column(name = "maximum_marks", nullable = false, precision = 5, scale = 2)
    private BigDecimal maximumMarks;

    @Column(name = "percentage", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentage;

    @Column(name = "grade", nullable = false, length = 5)
    private String grade;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
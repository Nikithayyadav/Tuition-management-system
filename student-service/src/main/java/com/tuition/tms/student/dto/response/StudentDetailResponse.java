package com.tuition.tms.student.dto.response;

import com.tuition.tms.student.entity.enums.Gender;
import com.tuition.tms.student.entity.enums.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete profile and history details of a student including enrollments")
public class StudentDetailResponse {

    @Schema(description = "Student system ID", example = "1")
    private Long id;

    @Schema(description = "Auto-generated unique Student Code", example = "STU-10001")
    private String studentCode;

    @Schema(description = "First Name", example = "Rahul")
    private String firstName;

    @Schema(description = "Last Name", example = "Sharma")
    private String lastName;

    @Schema(description = "Full Name", example = "Rahul Sharma")
    private String fullName;

    @Schema(description = "Date of Birth", example = "2008-05-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Gender", example = "MALE")
    private Gender gender;

    @Schema(description = "Mobile Phone Number", example = "9876543210")
    private String phone;

    @Schema(description = "Email Address", example = "rahul.sharma@example.com")
    private String email;

    @Schema(description = "Parent / Guardian Name", example = "Suresh Sharma")
    private String parentName;

    @Schema(description = "Parent / Guardian Phone", example = "9876543211")
    private String parentPhone;

    @Schema(description = "Residential Address", example = "123 Park Street, Sector 4, City")
    private String address;

    @Schema(description = "Student Status", example = "ACTIVE")
    private StudentStatus status;

    @Schema(description = "List of current and past enrollments")
    @Builder.Default
    private List<EnrollmentResponse> enrollments = new ArrayList<>();

    @Schema(description = "Record Creation Timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record Last Update Timestamp")
    private LocalDateTime updatedAt;
}

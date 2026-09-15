package com.tuition.tms.student.dto.request;

import com.tuition.tms.student.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for updating student profile details")
public class UpdateStudentRequest {

    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "Student's first name", example = "Rahul")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    @Schema(description = "Student's last name", example = "Sharma")
    private String lastName;

    @NotNull(message = "Date of birth is required")
    @Past(message = "Date of birth must be in the past")
    @Schema(description = "Date of birth in YYYY-MM-DD format", example = "2008-05-15")
    private LocalDate dateOfBirth;

    @NotNull(message = "Gender is required (MALE, FEMALE, OTHER)")
    @Schema(description = "Gender of the student", example = "MALE")
    private Gender gender;

    @NotBlank(message = "Phone number cannot be blank")
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be a valid 10-digit number")
    @Schema(description = "Student's 10-digit mobile number", example = "9876543210")
    private String phone;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Please provide a valid email address")
    @Schema(description = "Student's email address", example = "rahul.updated@example.com")
    private String email;

    @Size(max = 100, message = "Parent name cannot exceed 100 characters")
    @Schema(description = "Parent or guardian name", example = "Suresh Sharma")
    private String parentName;

    @Pattern(regexp = "^[0-9]{10}$|^$", message = "Parent phone must be a valid 10-digit number")
    @Schema(description = "Parent or guardian 10-digit contact number", example = "9876543211")
    private String parentPhone;

    @Size(max = 500, message = "Address cannot exceed 500 characters")
    @Schema(description = "Residential address", example = "Flat 402, Sunshine Heights, City")
    private String address;
}

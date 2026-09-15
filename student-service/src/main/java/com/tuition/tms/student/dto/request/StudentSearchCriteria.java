package com.tuition.tms.student.dto.request;

import com.tuition.tms.student.entity.enums.StudentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Query parameters for searching and filtering students")
public class StudentSearchCriteria {

    @Schema(description = "Single search term matched across first name, last name, phone, email, and student code", example = "Rahul")
    private String search;

    @Schema(description = "Student active/inactive status", example = "ACTIVE")
    private StudentStatus status;
}

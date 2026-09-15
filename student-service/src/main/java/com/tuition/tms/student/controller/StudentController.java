package com.tuition.tms.student.controller;

import com.tuition.tms.common.dto.ApiResponse;
import com.tuition.tms.student.dto.request.CreateStudentRequest;
import com.tuition.tms.student.dto.request.StudentEnrollmentRequest;
import com.tuition.tms.student.dto.request.StudentSearchCriteria;
import com.tuition.tms.student.dto.request.UpdateStudentRequest;
import com.tuition.tms.student.dto.response.BatchResponse;
import com.tuition.tms.student.dto.response.EnrollmentResponse;
import com.tuition.tms.student.dto.response.StudentDetailResponse;
import com.tuition.tms.student.dto.response.StudentResponse;
import com.tuition.tms.student.entity.enums.StudentStatus;
import com.tuition.tms.student.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "REST endpoints for managing students and enrollments")
public class StudentController {

    private final StudentService studentService;

    /**
     * 1. Add Student
     */
    @PostMapping
    @Operation(summary = "Add a new student", description = "Creates a new student record.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student created successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate phone or email")
    })
    public ResponseEntity<ApiResponse<StudentResponse>> createStudent(
            @Valid @RequestBody CreateStudentRequest request,
            HttpServletRequest httpRequest) {
        StudentResponse response = studentService.createStudent(request);
        return new ResponseEntity<>(
                ApiResponse.success(response, httpRequest.getRequestURI()),
                HttpStatus.CREATED
        );
    }

    /**
     * 2. Search / List Students
     */
    @GetMapping
    @Operation(summary = "Search and list students", description = "Search and filter students by search term (across name, phone, email, student code) and status.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Students retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<StudentResponse>>> searchStudents(
            @Parameter(description = "Search term matching name, phone, email, or student code") @RequestParam(required = false) String search,
            @Parameter(description = "Filter by status (ACTIVE / INACTIVE)") @RequestParam(required = false) StudentStatus status,
            HttpServletRequest httpRequest) {

        StudentSearchCriteria criteria = StudentSearchCriteria.builder()
                .search(search)
                .status(status)
                .build();

        List<StudentResponse> students = studentService.searchStudents(criteria);
        return ResponseEntity.ok(
                ApiResponse.success(students, httpRequest.getRequestURI())
        );
    }

    /**
     * 3. Get Available Batches
     */
    @GetMapping("/batches")
    @Operation(summary = "Get available batches", description = "Retrieves all available batches from the shared database for student enrollment.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Batches retrieved successfully")
    })
    public ResponseEntity<ApiResponse<List<BatchResponse>>> getAvailableBatches(HttpServletRequest httpRequest) {
        List<BatchResponse> batches = studentService.getAvailableBatches();
        return ResponseEntity.ok(
                ApiResponse.success(batches, httpRequest.getRequestURI())
        );
    }

    /**
     * 4. View One Student's Complete Details
     */
    @GetMapping("/{id}")
    @Operation(summary = "View complete student details", description = "Retrieves full profile of a single student including complete enrollment history.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student details retrieved successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentById(
            @Parameter(description = "Student ID", required = true) @PathVariable Long id,
            HttpServletRequest httpRequest) {
        StudentDetailResponse response = studentService.getStudentById(id);
        return ResponseEntity.ok(
                ApiResponse.success(response, httpRequest.getRequestURI())
        );
    }

    /**
     * 5. Update Student
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update student details", description = "Updates editable details for an existing student.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student updated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate phone or email conflict")
    })
    public ResponseEntity<ApiResponse<StudentResponse>> updateStudent(
            @Parameter(description = "Student ID", required = true) @PathVariable Long id,
            @Valid @RequestBody UpdateStudentRequest request,
            HttpServletRequest httpRequest) {
        StudentResponse response = studentService.updateStudent(id, request);
        return ResponseEntity.ok(
                ApiResponse.success(response, httpRequest.getRequestURI())
        );
    }

    /**
     * 6. Delete / Deactivate Student (Soft Delete)
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Deactivate student (Soft Delete)", description = "Deactivates a student by changing their status from ACTIVE to INACTIVE.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Student deactivated successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Student is already inactive"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student not found")
    })
    public ResponseEntity<ApiResponse<Void>> deactivateStudent(
            @Parameter(description = "Student ID", required = true) @PathVariable Long id,
            HttpServletRequest httpRequest) {
        studentService.deactivateStudent(id);
        return ResponseEntity.ok(
                ApiResponse.success(null, httpRequest.getRequestURI())
        );
    }

    /**
     * 7. Student Enrollment + Batch Assignment
     */
    @PostMapping("/{id}/enrollment")
    @Operation(summary = "Enroll student and assign to batch", description = "Enrolls an active student into a designated batch.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Student enrolled successfully"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid payload or inactive student"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Student or Batch not found"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Duplicate enrollment")
    })
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollStudent(
            @Parameter(description = "Student ID", required = true) @PathVariable Long id,
            @Valid @RequestBody StudentEnrollmentRequest request,
            HttpServletRequest httpRequest) {
        EnrollmentResponse response = studentService.enrollStudent(id, request);
        return new ResponseEntity<>(
                ApiResponse.success(response, httpRequest.getRequestURI()),
                HttpStatus.CREATED
        );
    }
}

package com.example.tuitionmanagement.performance;

import com.example.tuitionmanagement.performance.dto.MarksRequest;
import com.example.tuitionmanagement.performance.service.PerformanceService;
import com.example.tuitionmanagement.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/performance")
public class PerformanceController {

    @Autowired
    private PerformanceService performanceService;

    @Operation(
            summary = "Enter student examination marks",
            description = """
                    Records marks for one student, one examination and one subject.
                    Percentage and grade are calculated automatically in the service layer.

                    Supported subjects:
                    JAVA, PYTHON, MYSQL

                    Supported examinations:
                    MOCK_EXAM, PRACTICE_EXAM
                    """
    )
    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Marks entered successfully"
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid request, invalid marks, or duplicate performance record",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": "Marks obtained cannot be greater than maximum marks",
                                              "meta": null
                                            }
                                            """
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": "An unexpected error occurred",
                                              "meta": null
                                            }
                                            """
                            )
                    )
            )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<?>> enterMarks(
            @Valid @RequestBody MarksRequest request
    ) {
        return performanceService.enterMarks(request);
    }

    @Operation(
            summary = "Get performance records",
            description = """
                    Returns paginated performance records.

                    Optional filters:
                    - Student ID
                    - Exam name

                    Supported exam names:
                    MOCK_EXAM, PRACTICE_EXAM
                    """
    )
    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Performance records retrieved successfully"
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid exam name, student ID, page number, or page size",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": "Page size must be between 1 and 100",
                                              "meta": null
                                            }
                                            """
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getPerformances(

            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @RequestParam(required = false)
            Long studentId,

            @Parameter(
                    description = "Exam name: MOCK_EXAM or PRACTICE_EXAM",
                    example = "MOCK_EXAM"
            )
            @RequestParam(required = false)
            String examName,

            @Parameter(
                    description = "Page number starting from zero",
                    example = "0"
            )
            @RequestParam(defaultValue = "0")
            int page,

            @Parameter(
                    description = "Page size between 1 and 100",
                    example = "10"
            )
            @RequestParam(defaultValue = "10")
            int size
    ) {
        return performanceService.getPerformances(
                studentId,
                examName,
                page,
                size
        );
    }

    @Operation(
            summary = "Generate student performance report",
            description = """
                    Generates a performance report for one student and one examination.

                    The report includes:
                    - Subject-wise marks
                    - Total marks obtained
                    - Total maximum marks
                    - Overall percentage
                    - Overall grade

                    Supported exam names:
                    MOCK_EXAM, PRACTICE_EXAM
                    """
    )
    @ApiResponses(value = {

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Performance report generated successfully"
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid student ID or exam name",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": "Student ID must be greater than zero",
                                              "meta": null
                                            }
                                            """
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "No performance records found for the given student and exam",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": false,
                                              "data": null,
                                              "error": "No performance records found for the given student and exam",
                                              "meta": null
                                            }
                                            """
                            )
                    )
            ),

            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Internal server error"
            )
    })
    @GetMapping("/report")
    public ResponseEntity<ApiResponse<?>> getPerformanceReport(

            @Parameter(
                    description = "Student ID",
                    example = "1"
            )
            @RequestParam
            Long studentId,

            @Parameter(
                    description = "Exam name: MOCK_EXAM or PRACTICE_EXAM",
                    example = "MOCK_EXAM"
            )
            @RequestParam
            String examName
    ) {
        return performanceService.getPerformanceReport(
                studentId,
                examName
        );
    }
}
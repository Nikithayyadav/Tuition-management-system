package com.example.tuitionmanagement.exception;

import com.example.tuitionmanagement.response.ApiResponse;
import com.example.tuitionmanagement.response.MetaResponse;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Clock indianClock;

    public GlobalExceptionHandler(Clock indianClock) {
        this.indianClock = indianClock;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        errors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiResponse<?> response =
                ApiResponse.builder()
                        .success(false)
                        .data(null)
                        .error(errors.toString())
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message("Validation failed")
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(
            BadRequestException exception) {

        ApiResponse<?> response =
                ApiResponse.builder()
                        .success(false)
                        .data(null)
                        .error(exception.getMessage())
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message("Bad request")
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleUnreadableMessage(
            HttpMessageNotReadableException exception) {

        String errorMessage =
                "Invalid JSON request body. "
                        + "Please check the field values and JSON format.";

        Throwable cause = exception.getMostSpecificCause();

        if (cause instanceof JsonParseException jsonParseException) {

            String originalMessage =
                    jsonParseException.getOriginalMessage();

            if (originalMessage != null
                    && originalMessage.contains("Unexpected character")) {

                errorMessage =
                        "Invalid JSON value. A field contains a missing "
                                + "or malformed value. Please provide a valid "
                                + "value for every field.";
            }

            else if (originalMessage != null
                    && originalMessage.contains("Unexpected end-of-input")) {

                errorMessage =
                        "Invalid JSON request body. The request ended "
                                + "before all field values were provided.";
            }

            else {

                errorMessage =
                        "Invalid JSON syntax. Please check commas, "
                                + "quotation marks, brackets, and field values.";
            }
        }

        else if (cause instanceof MismatchedInputException
                mismatchedInputException) {

            String fieldName = getFieldName(mismatchedInputException);

            errorMessage =
                    "Invalid or missing value for '"
                            + fieldName
                            + "'. Please provide a valid value.";
        }

        else if (cause instanceof JsonMappingException
                jsonMappingException) {

            String fieldName = getFieldName(jsonMappingException);

            errorMessage =
                    "Invalid value for '"
                            + fieldName
                            + "'. Please check the supplied value.";
        }

        ApiResponse<?> response =
                ApiResponse.builder()
                        .success(false)
                        .data(null)
                        .error(errorMessage)
                        .meta(
                                MetaResponse.builder()
                                        .timestamp(
                                                LocalDateTime.now(indianClock)
                                        )
                                        .message("Invalid request body")
                                        .build()
                        )
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatchException(
            MethodArgumentTypeMismatchException exception) {

        String errorMessage =
                "Invalid value for parameter '"
                        + exception.getName()
                        + "'.";

        if (exception.getRequiredType() != null) {

            errorMessage +=
                    " Expected type: "
                            + exception.getRequiredType().getSimpleName()
                            + ".";
        }

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(LocalDateTime.now(indianClock))
                        .message("Invalid request parameter")
                        .build();

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .data(null)
                        .error(errorMessage)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception exception) {

        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "Request failed";

        String errorMessage = exception.getMessage();

        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage =
                    "An unexpected error occurred while processing the request.";
        }

        if (errorMessage.startsWith("No fee structure found")) {

            status = HttpStatus.NOT_FOUND;
            message = "Fee structure not found";

        } else if (errorMessage.startsWith("Fee structure already exists")) {

            status = HttpStatus.CONFLICT;
            message = "Fee structure already exists";

        } else if (errorMessage.startsWith("Class level")) {

            status = HttpStatus.BAD_REQUEST;
            message = "Invalid class level";

        } else if (errorMessage.startsWith("Fee amount")) {

            status = HttpStatus.BAD_REQUEST;
            message = "Invalid fee amount";

        } else if (errorMessage.startsWith("Page")) {

            status = HttpStatus.BAD_REQUEST;
            message = "Invalid pagination parameters";

        } else if (errorMessage.startsWith("Student")) {

            status = HttpStatus.NOT_FOUND;
            message = "Student not found";

        } else if (errorMessage.startsWith("Payment")) {

            status = HttpStatus.BAD_REQUEST;
            message = "Invalid payment request";

        } else if (errorMessage.startsWith("Fee payment")) {

            status = HttpStatus.NOT_FOUND;
            message = "Fee payment not found";
        }

        MetaResponse meta =
                MetaResponse.builder()
                        .timestamp(LocalDateTime.now(indianClock))
                        .message(message)
                        .build();

        ApiResponse<Void> response =
                ApiResponse.<Void>builder()
                        .success(false)
                        .data(null)
                        .error(errorMessage)
                        .meta(meta)
                        .build();

        return ResponseEntity
                .status(status)
                .body(response);
    }

    private String getFieldName(JsonMappingException exception) {

        if (exception.getPath() != null
                && !exception.getPath().isEmpty()) {

            JsonMappingException.Reference reference =
                    exception.getPath()
                            .get(exception.getPath().size() - 1);

            if (reference.getFieldName() != null
                    && !reference.getFieldName().isBlank()) {

                return reference.getFieldName();
            }
        }

        return "request field";
    }
}
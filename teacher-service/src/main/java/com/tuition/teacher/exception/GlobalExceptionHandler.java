package com.tuition.teacher.exception;

import com.tuition.teacher.common.ApiResponse;
import com.tuition.teacher.teacher.TeacherAlreadyExistsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.tuition.teacher.teacher.TeacherNotFoundException;
import com.tuition.teacher.batch.TeacherBatchAlreadyExistsException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TeacherAlreadyExistsException.class)
    public ApiResponse<Object> handleTeacherAlreadyExists(
            TeacherAlreadyExistsException exception) {

        return new ApiResponse<>(
                false,
                null,
                exception.getMessage(),
                null
        );
    }
    @ExceptionHandler(TeacherNotFoundException.class)
    public ApiResponse<Object> handleTeacherNotFound(
            TeacherNotFoundException exception) {

        return new ApiResponse<>(
                false,
                null,
                exception.getMessage(),
                null
        );
    }
    @ExceptionHandler(TeacherBatchAlreadyExistsException.class)
    public ApiResponse<Object> handleTeacherBatchAlreadyExists(
            TeacherBatchAlreadyExistsException exception) {

        return new ApiResponse<>(
                false,
                null,
                exception.getMessage(),
                null
        );
    }
}
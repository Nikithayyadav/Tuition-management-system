package com.tuition.tms.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;

    private T data;

    private ResponseMetadata metadata;

    private ApiError error;

    public static <T> ApiResponse<T> success(T data, String path) {
        Integer count = (data instanceof Collection<?>) ? ((Collection<?>) data).size() : null;
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .metadata(ResponseMetadata.builder()
                        .timestamp(LocalDateTime.now())
                        .path(path)
                        .count(count)
                        .build())
                .error(null)
                .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    public static <T> ApiResponse<T> error(String code, String message, Object details, String path) {
        return ApiResponse.<T>builder()
                .success(false)
                .data(null)
                .metadata(ResponseMetadata.builder()
                        .timestamp(LocalDateTime.now())
                        .path(path)
                        .build())
                .error(ApiError.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, String path) {
        return error(code, message, null, path);
    }
}

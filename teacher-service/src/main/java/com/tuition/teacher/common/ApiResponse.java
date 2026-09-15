package com.tuition.teacher.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T>{
    private boolean success;
    private T data;
    private Object error;
    private Object meta;



}

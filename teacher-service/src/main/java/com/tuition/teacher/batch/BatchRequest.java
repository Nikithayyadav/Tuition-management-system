package com.tuition.teacher.batch;

import lombok.Data;

import java.time.LocalTime;

@Data
public class BatchRequest {

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
}
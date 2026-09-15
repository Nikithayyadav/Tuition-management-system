package com.tuition.teacher.batch;

public class TeacherBatchAlreadyExistsException extends RuntimeException {

    public TeacherBatchAlreadyExistsException(String message) {
        super(message);
    }
}
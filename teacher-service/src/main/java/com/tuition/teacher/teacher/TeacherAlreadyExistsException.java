package com.tuition.teacher.teacher;

public class TeacherAlreadyExistsException extends RuntimeException {

    public TeacherAlreadyExistsException(String message) {
        super(message);
    }
}
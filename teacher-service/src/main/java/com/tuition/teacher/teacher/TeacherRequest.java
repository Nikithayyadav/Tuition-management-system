package com.tuition.teacher.teacher;

import lombok.Data;

@Data
public class TeacherRequest {
    private String name;
    private String phone;
    private String email;
    private String specialization;
}

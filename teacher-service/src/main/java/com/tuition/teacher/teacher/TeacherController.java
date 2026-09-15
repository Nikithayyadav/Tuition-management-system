package com.tuition.teacher.teacher;

import com.tuition.teacher.common.ApiResponse;
import com.tuition.teacher.common.PageMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teachers")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping
    public ApiResponse<Teacher> addTeacher(
            @RequestBody TeacherRequest teacherRequest) {

        Teacher teacher = new Teacher();

        teacher.setName(teacherRequest.getName());
        teacher.setEmail(teacherRequest.getEmail());
        teacher.setPhone(teacherRequest.getPhone());
        teacher.setSpecialization(teacherRequest.getSpecialization());

        Teacher savedTeacher = teacherService.addTeacher(teacher);

        return new ApiResponse<>(
                true,
                savedTeacher,
                null,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<Teacher>> getAllTeachers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Teacher> teachers = teacherService.getAllTeachers(pageable);

        PageMeta pageMeta = new PageMeta(
                teachers.getNumber(),
                teachers.getSize(),
                teachers.getTotalElements(),
                teachers.getTotalPages()
        );

        return new ApiResponse<>(
                true,
                teachers.getContent(),
                null,
                pageMeta
        );
    }

    @PutMapping("/{id}")
    public ApiResponse<Teacher> updateTeacher(
            @PathVariable Long id,
            @RequestBody TeacherRequest teacherRequest) {

        Teacher updatedTeacher =
                teacherService.updateTeacher(id, teacherRequest);

        return new ApiResponse<>(
                true,
                updatedTeacher,
                null,
                null
        );
    }
}
package com.tuition.teacher.teacher;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public Teacher addTeacher(Teacher teacher) {

        if (teacherRepository.existsByEmail(teacher.getEmail())) {
            throw new TeacherAlreadyExistsException("Email Already Exists");
        }

        if (teacherRepository.existsByPhone(teacher.getPhone())) {
            throw new TeacherAlreadyExistsException("Phone already exists");
        }

        return teacherRepository.save(teacher);
    }

    public Page<Teacher> getAllTeachers(Pageable pageable) {
        return teacherRepository.findAll(pageable);
    }

    public Teacher updateTeacher(Long id, TeacherRequest teacherRequest) {

        Teacher teacher = teacherRepository.findById(id)
                .orElseThrow(() ->
                        new TeacherNotFoundException(
                                "Teacher not found with id: " + id));

        if (teacherRepository.existsByEmailAndIdNot(
                teacherRequest.getEmail(), id)) {
            throw new TeacherAlreadyExistsException(
                    "Email already exists");
        }

        if (teacherRepository.existsByPhoneAndIdNot(
                teacherRequest.getPhone(), id)) {
            throw new TeacherAlreadyExistsException(
                    "Phone already exists");
        }

        teacher.setName(teacherRequest.getName());
        teacher.setEmail(teacherRequest.getEmail());
        teacher.setPhone(teacherRequest.getPhone());
        teacher.setSpecialization(teacherRequest.getSpecialization());

        return teacherRepository.save(teacher);
    }
}
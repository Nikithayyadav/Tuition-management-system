package com.tuition.tms.student.service;

import com.tuition.tms.common.exception.BadRequestException;
import com.tuition.tms.common.exception.DuplicateResourceException;
import com.tuition.tms.common.exception.ResourceNotFoundException;
import com.tuition.tms.student.dto.request.CreateStudentRequest;
import com.tuition.tms.student.dto.request.StudentEnrollmentRequest;
import com.tuition.tms.student.dto.request.StudentSearchCriteria;
import com.tuition.tms.student.dto.request.UpdateStudentRequest;
import com.tuition.tms.student.dto.response.BatchResponse;
import com.tuition.tms.student.dto.response.EnrollmentResponse;
import com.tuition.tms.student.dto.response.StudentDetailResponse;
import com.tuition.tms.student.dto.response.StudentResponse;
import com.tuition.tms.student.entity.Batch;
import com.tuition.tms.student.entity.Enrollment;
import com.tuition.tms.student.entity.Student;
import com.tuition.tms.student.entity.enums.EnrollmentStatus;
import com.tuition.tms.student.entity.enums.StudentStatus;
import com.tuition.tms.student.repository.BatchRepository;
import com.tuition.tms.student.repository.EnrollmentRepository;
import com.tuition.tms.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final BatchRepository batchRepository;

    /**
     * 1. Add / Create a new student
     */
    @Transactional
    public StudentResponse createStudent(CreateStudentRequest request) {
        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        String phone = request.getPhone().trim();
        String email = request.getEmail().trim();

        String parentName = request.getParentName() != null ? request.getParentName().trim() : null;
        String parentPhone = request.getParentPhone() != null ? request.getParentPhone().trim() : null;
        String address = request.getAddress() != null ? request.getAddress().trim() : null;

        if (studentRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("student", "email", email);
        }
        if (studentRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("student", "phone", phone);
        }

        String studentCode = generateStudentCode();
        Student student = Student.builder()
                .studentCode(studentCode)
                .firstName(firstName)
                .lastName(lastName)
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .phone(phone)
                .email(email)
                .parentName(parentName)
                .parentPhone(parentPhone)
                .address(address)
                .status(StudentStatus.ACTIVE)
                .build();

        Student savedStudent = studentRepository.save(student);
        log.info("Created new student with code: {} and ID: {}", savedStudent.getStudentCode(), savedStudent.getId());

        return mapToStudentResponse(savedStudent);
    }

    private String generateStudentCode() {
        int number = 1;
        String studentCode;
        do {
            studentCode = String.format("STU-%05d", number);
            number++;
        } while (studentRepository.existsByStudentCode(studentCode));

        return studentCode;
    }

    /**
     * 2. Search / List students with single search criteria and optional status filter
     */
    @Transactional(readOnly = true)
    public List<StudentResponse> searchStudents(StudentSearchCriteria criteria) {
        Specification<Student> specification = Specification.where(null);

        if (criteria.getSearch() != null && !criteria.getSearch().trim().isEmpty()) {
            String search = criteria.getSearch().trim().toLowerCase();
            specification = specification.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("firstName")), "%" + search + "%"),
                    cb.like(cb.lower(root.get("lastName")), "%" + search + "%"),
                    cb.like(cb.lower(root.get("phone")), "%" + search + "%"),
                    cb.like(cb.lower(root.get("email")), "%" + search + "%"),
                    cb.like(cb.lower(root.get("studentCode")), "%" + search + "%")
            ));
        }

        if (criteria.getStatus() != null) {
            specification = specification.and((root, query, cb) -> cb.equal(root.get("status"), criteria.getStatus()));
        }

        List<Student> students = studentRepository.findAll(specification);

        return students.stream()
                .map(this::mapToStudentResponse)
                .toList();
    }

    /**
     * 3. View complete details of a single student (profile + enrollments)
     */
    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        List<Enrollment> enrollments = enrollmentRepository.findByStudentId(id);
        List<EnrollmentResponse> enrollmentResponses = enrollments.stream()
                .map(enrollment -> mapToEnrollmentResponse(enrollment, student))
                .toList();

        return StudentDetailResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .phone(student.getPhone())
                .email(student.getEmail())
                .parentName(student.getParentName())
                .parentPhone(student.getParentPhone())
                .address(student.getAddress())
                .status(student.getStatus())
                .enrollments(enrollmentResponses)
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    /**
     * 4. Update student editable details
     */
    @Transactional
    public StudentResponse updateStudent(Long id, UpdateStudentRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        String firstName = request.getFirstName().trim();
        String lastName = request.getLastName().trim();
        String phone = request.getPhone().trim();
        String email = request.getEmail().trim();

        String parentName = request.getParentName() != null ? request.getParentName().trim() : null;
        String parentPhone = request.getParentPhone() != null ? request.getParentPhone().trim() : null;
        String address = request.getAddress() != null ? request.getAddress().trim() : null;

        if (!student.getEmail().equalsIgnoreCase(email) && studentRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateResourceException("student", "email", email);
        }
        if (!student.getPhone().equals(phone) && studentRepository.existsByPhone(phone)) {
            throw new DuplicateResourceException("student", "phone", phone);
        }

        student.setFirstName(firstName);
        student.setLastName(lastName);
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setPhone(phone);
        student.setEmail(email);
        student.setParentName(parentName);
        student.setParentPhone(parentPhone);
        student.setAddress(address);

        Student updatedStudent = studentRepository.save(student);
        log.info("Updated student details for ID: {}", id);

        return mapToStudentResponse(updatedStudent);
    }

    /**
     * 5. Delete / Deactivate student (Soft deletion: ACTIVE -> INACTIVE)
     */
    @Transactional
    public void deactivateStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", id));

        if (student.getStatus() == StudentStatus.INACTIVE) {
            throw new BadRequestException("Student is already inactive with id: " + id);
        }

        student.setStatus(StudentStatus.INACTIVE);
        studentRepository.save(student);
        log.info("Deactivated student with ID: {}", id);
    }

    /**
     * 6. Retrieve available batches from the shared database
     */
    @Transactional(readOnly = true)
    public List<BatchResponse> getAvailableBatches() {
        List<Batch> batches = batchRepository.findAll();
        return batches.stream()
                .map(batch -> BatchResponse.builder()
                        .id(batch.getId())
                        .name(batch.getName())
                        .startTime(batch.getStartTime())
                        .endTime(batch.getEndTime())
                        .build())
                .toList();
    }

    /**
     * 7. Student enrollment & batch assignment
     */
    @Transactional
    public EnrollmentResponse enrollStudent(Long studentId, StudentEnrollmentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));

        if (student.getStatus() != StudentStatus.ACTIVE) {
            throw new BadRequestException("Cannot enroll an inactive student with id: " + studentId);
        }

        Batch batch = batchRepository.findById(request.getBatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Batch", "id", request.getBatchId()));

        if (enrollmentRepository.existsByStudentIdAndBatchId(studentId, request.getBatchId())) {
            throw new DuplicateResourceException("Student is already enrolled in this batch");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .batch(batch)
                .status(EnrollmentStatus.ACTIVE)
                .build();

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        log.info("Enrolled student ID: {} into batch ID: {}", studentId, batch.getId());

        return mapToEnrollmentResponse(savedEnrollment, student);
    }

    private StudentResponse mapToStudentResponse(Student student) {
        return StudentResponse.builder()
                .id(student.getId())
                .studentCode(student.getStudentCode())
                .firstName(student.getFirstName())
                .lastName(student.getLastName())
                .fullName(student.getFirstName() + " " + student.getLastName())
                .dateOfBirth(student.getDateOfBirth())
                .gender(student.getGender())
                .phone(student.getPhone())
                .email(student.getEmail())
                .parentName(student.getParentName())
                .parentPhone(student.getParentPhone())
                .address(student.getAddress())
                .status(student.getStatus())
                .createdAt(student.getCreatedAt())
                .updatedAt(student.getUpdatedAt())
                .build();
    }

    private EnrollmentResponse mapToEnrollmentResponse(Enrollment enrollment, Student student) {
        return EnrollmentResponse.builder()
                .id(enrollment.getId())
                .studentId(student.getId())
                .studentCode(student.getStudentCode())
                .studentName(student.getFirstName() + " " + student.getLastName())
                .batchId(enrollment.getBatch().getId())
                .batchName(enrollment.getBatch().getName())
                .startTime(enrollment.getBatch().getStartTime())
                .endTime(enrollment.getBatch().getEndTime())
                .status(enrollment.getStatus())
                .createdAt(enrollment.getCreatedAt())
                .build();
    }
}
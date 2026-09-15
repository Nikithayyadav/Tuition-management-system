package com.tuition.tms.student;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tuition.tms.student.dto.request.CreateStudentRequest;
import com.tuition.tms.student.dto.request.StudentEnrollmentRequest;
import com.tuition.tms.student.dto.request.UpdateStudentRequest;
import com.tuition.tms.student.entity.Batch;
import com.tuition.tms.student.entity.Student;
import com.tuition.tms.student.entity.enums.Gender;
import com.tuition.tms.student.entity.enums.StudentStatus;
import com.tuition.tms.student.repository.BatchRepository;
import com.tuition.tms.student.repository.EnrollmentRepository;
import com.tuition.tms.student.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class StudentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private BatchRepository batchRepository;

    private Batch testBatch;

    @BeforeEach
    void setUp() {
        enrollmentRepository.deleteAll();
        studentRepository.deleteAll();
        batchRepository.deleteAll();

        testBatch = batchRepository.save(Batch.builder()
                .name("MRG_BATCH")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(11, 0))
                .build());
    }

    @Test
    @DisplayName("POST /students - Successfully create a student")
    void testCreateStudentSuccess() throws Exception {
        CreateStudentRequest request = CreateStudentRequest.builder()
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .parentName("Suresh Sharma")
                .parentPhone("9876543211")
                .address("123 Park Street, Sector 4, City")
                .build();

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.firstName", is("Rahul")))
                .andExpect(jsonPath("$.data.lastName", is("Sharma")))
                .andExpect(jsonPath("$.data.studentCode", startsWith("STU-")))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("POST /students - Duplicate email/phone returns 409 conflict")
    void testCreateStudentDuplicateConflict() throws Exception {
        studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        CreateStudentRequest duplicateRequest = CreateStudentRequest.builder()
                .firstName("Another")
                .lastName("Student")
                .dateOfBirth(LocalDate.of(2009, 1, 10))
                .gender(Gender.FEMALE)
                .phone("9876543210")
                .email("another@example.com")
                .build();

        mockMvc.perform(post("/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(duplicateRequest)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("GET /students - Search students with single search query")
    void testSearchStudents() throws Exception {
        studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        mockMvc.perform(get("/students")
                        .param("search", "rahul")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].firstName", is("Rahul")));
    }

    @Test
    @DisplayName("GET /students/batches - Retrieve available batches")
    void testGetAvailableBatches() throws Exception {
        mockMvc.perform(get("/students/batches")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].id", is(testBatch.getId().intValue())))
                .andExpect(jsonPath("$.data[0].name", is("MRG_BATCH")))
                .andExpect(jsonPath("$.data[0].startTime", is("09:00:00")))
                .andExpect(jsonPath("$.data[0].endTime", is("11:00:00")));
    }

    @Test
    @DisplayName("GET /students/{id} - Get complete student details")
    void testGetStudentById() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        mockMvc.perform(get("/students/{id}", s1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.id", is(s1.getId().intValue())))
                .andExpect(jsonPath("$.data.firstName", is("Rahul")));
    }

    @Test
    @DisplayName("PUT /students/{id} - Update student details")
    void testUpdateStudent() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        UpdateStudentRequest updateRequest = UpdateStudentRequest.builder()
                .firstName("Rahul")
                .lastName("Verma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.verma@example.com")
                .build();

        mockMvc.perform(put("/students/{id}", s1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.lastName", is("Verma")))
                .andExpect(jsonPath("$.data.email", is("rahul.verma@example.com")));
    }

    @Test
    @DisplayName("DELETE /students/{id} - Soft deactivate student")
    void testDeactivateStudent() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        mockMvc.perform(delete("/students/{id}", s1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        mockMvc.perform(get("/students/{id}", s1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status", is("INACTIVE")));
    }

    @Test
    @DisplayName("POST /students/{id}/enrollment - Enroll student in batch")
    void testEnrollStudent() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        StudentEnrollmentRequest request = StudentEnrollmentRequest.builder()
                .batchId(testBatch.getId())
                .build();

        mockMvc.perform(post("/students/{id}/enrollment", s1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.studentId", is(s1.getId().intValue())))
                .andExpect(jsonPath("$.data.batchId", is(testBatch.getId().intValue())))
                .andExpect(jsonPath("$.data.batchName", is("MRG_BATCH")))
                .andExpect(jsonPath("$.data.startTime", is("09:00:00")))
                .andExpect(jsonPath("$.data.endTime", is("11:00:00")))
                .andExpect(jsonPath("$.data.status", is("ACTIVE")));
    }

    @Test
    @DisplayName("POST /students/{id}/enrollment - Duplicate enrollment returns 409 conflict")
    void testEnrollStudentDuplicateRejection() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.ACTIVE)
                .build());

        StudentEnrollmentRequest request = StudentEnrollmentRequest.builder()
                .batchId(testBatch.getId())
                .build();

        mockMvc.perform(post("/students/{id}/enrollment", s1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        // Second enrollment attempt for the same student and batch
        mockMvc.perform(post("/students/{id}/enrollment", s1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("POST /students/{id}/enrollment - Inactive student enrollment returns 400 bad request")
    void testEnrollInactiveStudentRejection() throws Exception {
        Student s1 = studentRepository.save(Student.builder()
                .studentCode("STU-00001")
                .firstName("Rahul")
                .lastName("Sharma")
                .dateOfBirth(LocalDate.of(2008, 5, 15))
                .gender(Gender.MALE)
                .phone("9876543210")
                .email("rahul.sharma@example.com")
                .status(StudentStatus.INACTIVE)
                .build());

        StudentEnrollmentRequest request = StudentEnrollmentRequest.builder()
                .batchId(testBatch.getId())
                .build();

        mockMvc.perform(post("/students/{id}/enrollment", s1.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }
}

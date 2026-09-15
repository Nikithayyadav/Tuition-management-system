package com.tuition.teacher.subject;

import com.tuition.teacher.common.ApiResponse;
import com.tuition.teacher.common.PageMeta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @PostMapping
    public ApiResponse<Subject> addSubject(
            @RequestBody SubjectRequest subjectRequest) {

        Subject savedSubject = subjectService.addSubject(subjectRequest);

        return new ApiResponse<>(
                true,
                savedSubject,
                null,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<Subject>> getAllSubjects(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Subject> subjects = subjectService.getAllSubjects(pageable);

        PageMeta pageMeta = new PageMeta(
                subjects.getNumber(),
                subjects.getSize(),
                subjects.getTotalElements(),
                subjects.getTotalPages()
        );

        return new ApiResponse<>(
                true,
                subjects.getContent(),
                null,
                pageMeta
        );
    }
}
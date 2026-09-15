package com.tuition.teacher.subject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;

    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Subject addSubject(SubjectRequest subjectRequest) {

        Subject subject = new Subject();

        subject.setName(subjectRequest.getName());
        subject.setDescription(subjectRequest.getDescription());

        return subjectRepository.save(subject);
    }

    public Page<Subject> getAllSubjects(Pageable pageable) {
        return subjectRepository.findAll(pageable);
    }
}
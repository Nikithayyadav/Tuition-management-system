package com.example.tuitionmanagement.fee.repository;

import com.example.tuitionmanagement.fee.entity.FeeStructure;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeeStructureRepository
        extends JpaRepository<FeeStructure, Long> {

    Page<FeeStructure> findByBatchId(
            Long batchId,
            Pageable pageable
    );
}
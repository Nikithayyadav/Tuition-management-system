package com.tuition.teacher.batch;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BatchService {

    private final BatchRepository batchRepository;

    public BatchService(BatchRepository batchRepository) {
        this.batchRepository = batchRepository;
    }

    public Batch addBatch(BatchRequest batchRequest) {

        Batch batch = new Batch();

        batch.setName(batchRequest.getName());
        batch.setStartTime(batchRequest.getStartTime());
        batch.setEndTime(batchRequest.getEndTime());

        return batchRepository.save(batch);
    }

    public Page<Batch> getAllBatches(Pageable pageable) {
        return batchRepository.findAll(pageable);
    }
}
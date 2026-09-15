package com.tuition.teacher.batch;

import com.tuition.teacher.common.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import com.tuition.teacher.common.PageMeta;
import java.util.List;

@RestController
@RequestMapping("/api/batches")
public class BatchController {

    private final BatchService batchService;

    public BatchController(BatchService batchService) {
        this.batchService = batchService;
    }

    @PostMapping
    public ApiResponse<Batch> addBatch(
            @RequestBody BatchRequest batchRequest) {

        Batch savedBatch = batchService.addBatch(batchRequest);

        return new ApiResponse<>(
                true,
                savedBatch,
                null,
                null
        );
    }

    @GetMapping
    public ApiResponse<List<Batch>> getAllBatches(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Batch> batches = batchService.getAllBatches(pageable);

        PageMeta pageMeta = new PageMeta(
                batches.getNumber(),
                batches.getSize(),
                batches.getTotalElements(),
                batches.getTotalPages()
        );

        return new ApiResponse<>(
                true,
                batches.getContent(),
                null,
                pageMeta
        );
    }
}
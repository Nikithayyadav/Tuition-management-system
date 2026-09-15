package com.tuition.teacher.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageMeta {

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
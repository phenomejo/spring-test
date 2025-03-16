package com.org.test.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GenericPaginationDTO <T> {

    private List<T> content;
    private Pagination pagination;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Pagination {

        private Integer totalPages;
        private Long totalElements;
        private Integer numberOfElements;
        private Integer pageSize;
        private Integer pageNumber;
        private boolean hasNext;
        private boolean hasPrevious;

    }
}

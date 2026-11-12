package org.upyog.wms.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pagination {

    private Integer limit;

    private Integer offset;

    private Long totalCount;

    private String sortBy;

    private String sortOrder;
}

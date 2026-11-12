package org.upyog.wms.web.models.idgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdRequest {

    private String idName;

    private String tenantId;

    private String format;

    private Integer count;
}

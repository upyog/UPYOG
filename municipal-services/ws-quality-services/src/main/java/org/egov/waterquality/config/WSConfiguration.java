package org.egov.waterquality.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

@Component
public class WSConfiguration {

    @Value("${egov.waterservice.pagination.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.waterservice.pagination.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.waterservice.pagination.max.limit}")
    private Integer maxLimit;

    // IDGEN
    @Value("${egov.idgen.wqid.sample.name}")
    private String waterQualitySampleIdGenName;

    @Value("${egov.idgen.wqid.sample.format}")
    private String waterQualitySampleIdGenFormat;

    @Value("${egov.idgen.wqid.test.name}")
    private String waterQualityTestIdGenName;

    @Value("${egov.idgen.wqid.test.format}")
    private String waterQualityTestIdGenFormat;

    // Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

}

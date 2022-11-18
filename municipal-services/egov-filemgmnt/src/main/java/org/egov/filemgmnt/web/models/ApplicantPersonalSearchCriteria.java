package org.egov.filemgmnt.web.models;

//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j
public class ApplicantPersonalSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("id")
    private String id;

    @JsonProperty("fileCode")
    private String fileCode;

    @JsonProperty("fromDate")
    private Long fromDate;

    @JsonProperty("toDate")
    private Long toDate;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    @JsonProperty("aadhaarno")
    private String aadhaarno;

    public boolean tenantIdOnly() {
        log.info("file code : \n{}", fileCode);

        return (StringUtils.isNotBlank(tenantId) && StringUtils.isBlank(fileCode));
    }

    public boolean isEmpty() {
        return (StringUtils.isBlank(tenantId) && StringUtils.isBlank(fileCode));
    }

}

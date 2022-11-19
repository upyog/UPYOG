package org.egov.filemgmnt.web.models;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Slf4j

public class CommunicationFileSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("ids")
    private List<String> ids;

    @JsonProperty("fileCode")
    private String fileCode;

    public boolean isEmpty() {

        return (StringUtils.isBlank(tenantId) && StringUtils.isEmpty(fileCode));

    }

    public boolean tenantIdOnly() {
        // return (tenantId != null);
        return (StringUtils.isNotBlank(tenantId) && StringUtils.isEmpty(fileCode));
    }

}

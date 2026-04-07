package org.egov.dx.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileStoreResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("message")
    private String message;
    
    @JsonProperty("jobid")
    private String jobid;
    
    @JsonProperty("tenantid")
    private String tenantid;
    
    @JsonProperty("totalcount")
    private String totalcount;
    
    @JsonProperty("key")
    private String key;
    
    @JsonProperty("documentType")
    private String documentType;
    
    @JsonProperty("moduleName")
    private String moduleName;
    
    @JsonProperty("filestoreIds")
    private List<String> filestoreIds;
    
}

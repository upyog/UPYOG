package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GrbgDocument {

    private String uuid;
    private String docRefId;
    private String docName;
    private String docType;
    private String docCategory;
    private String tblRefUuid;
}

package org.egov.filemgmnt.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CommunicationFileResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("CommunicationFile")
    @Valid
    private List<CommunicationFile> communicationFiles;

    @JsonProperty("Count")
    private int count;

    public CommunicationFileResponse addCommunicationFile(CommunicationFile communicationFile) {

        if (communicationFiles == null) {
            communicationFiles = new ArrayList<>();
        }
        communicationFiles.add(communicationFile);
        return this;

    }

}

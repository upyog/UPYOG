package org.egov.filemgmnt.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Communication Filel request for create and update.")
@Validated
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class CommunicationFileRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("CommunicationFile")
    @Valid
    private List<CommunicationFile> communicationFiles;

    public CommunicationFileRequest addCommunicationFile(CommunicationFile communicationFile) {

        if (communicationFiles == null) {
            communicationFiles = new ArrayList<>();
        }
        communicationFiles.add(communicationFile);
        return this;
    }

}

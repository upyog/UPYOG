package org.egov.loadgenerator.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.loadgenerator.model.JobStatus;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadGeneratorResponse {

    @JsonProperty("jobId")
    private String jobId;

    @JsonProperty("message")
    private String message;

    @JsonProperty("status")
    private JobStatus status;
}

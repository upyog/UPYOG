package org.upyog.pgrai.web.models.grievanceclient;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Map;

/**
 * GrievanceResponse is a class that represents the response from the grievance fast api service.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrievanceResponse {
    @JsonProperty("_shards")
    private Map<String, Object> shards;
    private String result;
}

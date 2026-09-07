package org.egov.nationaldashboardingest.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ExternalApiResponseEventWrapper {

    @JsonProperty("apiResponseEvent")
    private ExternalApiResponseEvent apiResponseEvent;
}

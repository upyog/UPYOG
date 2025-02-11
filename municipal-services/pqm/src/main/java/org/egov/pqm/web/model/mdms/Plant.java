package org.egov.pqm.web.model.mdms;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class Plant {
    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("plantType")
    private String plantType;

    @JsonProperty("processes")
    private List<String> processes;

    @JsonProperty("wasteType")
    private List<String> wasteType;

    @JsonProperty("description")
    private String description;

    @JsonProperty("plantConfig")
    private String plantConfig;


}

package org.egov.pgr.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceWrapper {


    @Valid
    @NonNull
    @JsonProperty("service")
    private Service service = null;

    @Valid
    @JsonProperty("workflow")
    private Workflow workflow = null;
    
    @Valid
    @NotNull
    @JsonProperty("address")
    private Address address = null;


}

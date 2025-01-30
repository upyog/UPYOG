package org.egov.dx.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Encryption / Decryption Request Meta-data and Values
 */
@ApiModel(description = "Encryption / Decryption Request Meta-data and Values")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-10-11T17:31:52.360+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DecReqObject {

    @NotNull
    @JsonProperty("value")
    @Valid
    private Object value = null;

}


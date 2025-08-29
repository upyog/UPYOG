package org.egov.pgr.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.Valid;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageResponseWrapper {


    @Valid
    @NonNull
    @JsonProperty("service")
    private ImageData imagedata = null;

   
}

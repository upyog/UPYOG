package org.ksmart.birth.web.model.abandoned;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CareTaker {
    @Size(max = 1000)
    @JsonProperty("institutionName")
    private String institutionName;

    @Size(max = 1000)
    @JsonProperty("caretakerName")
    private String caretakerName;

    @Size(max = 1000)
    @JsonProperty("caretakerDesignation")
    private String caretakerDesignation;

    @Size(max = 12)
    @JsonProperty("caretakerMobile")
    private String caretakerMobile;

    @Size(max = 2500)
    @JsonProperty("caretakerAddress")
    private String caretakerAddress;
}

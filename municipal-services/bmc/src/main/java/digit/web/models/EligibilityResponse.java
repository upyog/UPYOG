package digit.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EligibilityResponse {


    @JsonProperty
    private Boolean machineTaken;

    @JsonProperty
    private Boolean courseTaken;

    @JsonProperty
    private Boolean pensionApplied;

    @JsonProperty
    private Boolean addressValidated;

    @JsonProperty
    private String errorMessage;


}

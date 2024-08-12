package digit.web.models;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.VerificationDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Component
public class VerifierResponse {

    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;
    
     @JsonProperty("Applications")
     List<VerificationDetails> verificationDetails;

    private String errorMessage;

}

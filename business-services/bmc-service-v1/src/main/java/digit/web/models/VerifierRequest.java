package digit.web.models;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

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
public class VerifierRequest {
    
    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;
    @JsonProperty("schemeId")
    private Long schemeId;
    @JsonProperty("detailID")
    private Long detailId;
    @JsonProperty("type")
    private String type;
    @JsonProperty("action")
    private String action;

    private String previousState;
}


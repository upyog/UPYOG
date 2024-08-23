package digit.web.models;

import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.UserSchemeApplication;
import digit.web.models.user.DocumentDetails;
import digit.web.models.user.UserSubSchemeMapping;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserSchemeApplicationRequest {
    
    @JsonProperty("RequestInfo")
    RequestInfo requestInfo;
    
    @JsonProperty("SchemeApplications")
    SchemeApplication schemeApplication;

    @JsonProperty("SchemeApplication")
    UserSchemeApplication UserSchemeApplication;

    List<SchemeApplication> schemeApplicationList;
    
    @JsonProperty("UserSubSchemeMapping")
    UserSubSchemeMapping userSubSchemeMapping;


}

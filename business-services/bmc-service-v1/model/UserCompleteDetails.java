package digit.bmc.model;

import org.egov.common.contract.models.Address;
import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class UserCompleteDetails {
   
    @JsonProperty("RequestInfo")
    RequestInfo requestInfo;
    
    @JsonProperty("gender")
    private String gender;

    @JsonProperty("address")
    private Address address;

    @JsonProperty("userOtherDetails")
    private UserOtherDetails userOtherDetails;

}

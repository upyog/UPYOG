package digit.web.models.user;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.repository.UserSearchCriteria;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
/**
 * SchemeApplicationSearchRequest
 */
@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class UserRequest {

    @JsonProperty("RequestInfo")
    @Valid
    @Builder.Default
    private RequestInfo requestInfo = null;

    @JsonProperty("UserSearchCriteria")
    @Valid
    @Builder.Default
    private UserSearchCriteria userSearchCriteria = null;
    
    @JsonProperty("ApplicantDetails")
    private UserDetails userDetails;
    
    private String house;
    private String street;
    private String landmark;
    private String locality;
    private String city;
    private String subDistrict;
    private String district;
    private String state;
    private String pinCode;
    private String zoneName;
    private String blockName;
    private String wardName;

}
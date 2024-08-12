package digit.web.models;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SchemeValidationResponse {

   @JsonProperty("ResponseInfo")
   private ResponseInfo responseInfo;
   
   @JsonProperty
   private String schemeType;
   
   @JsonProperty("Error")
   private StringBuilder error;

   private Boolean ageEligibility;

   private Boolean disability;

   private Boolean incomeEligibility;

   private Boolean genderEligibility;

   private Boolean educationEligibility;

   private Boolean rationCardEligibility;



}

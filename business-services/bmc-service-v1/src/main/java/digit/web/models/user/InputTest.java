package digit.web.models.user;



import java.util.List;

import org.egov.common.contract.request.RequestInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.AadharUser;
import digit.bmc.model.UserOtherDetails;
import digit.web.models.BankDetails;
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
public class InputTest {

@JsonProperty("RequestInfo")
RequestInfo requestInfo;

@JsonProperty("updatedBank")
List<BankDetails> bankDetailsList;

@JsonProperty("AadharUser")
AadharUser aadharUser;

@JsonProperty("updatedQualifications")
List<QualificationSave> qualificationDetailsList;

@JsonProperty("updatedAddress")
UserAddressDetails userAddressDetails;

@JsonProperty("updatedDisability")
DivyangDetails divyangDetails;

@JsonProperty("UserOtherDetails")
UserOtherDetails userOtherDetails;

@JsonProperty("updatedPersonalDetails")
PersonalDetails personalDetails;






}

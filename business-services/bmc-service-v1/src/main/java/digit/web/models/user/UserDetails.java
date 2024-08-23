package digit.web.models.user;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.AadharUser;
import digit.bmc.model.UserOtherDetails;
import digit.web.models.BankDetails;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class UserDetails {
    
    @JsonProperty("UserBank")
    private List<BankDetails> bankDetail;
    
    @JsonProperty("UserQualification")
    private List<QualificationDetails> qualificationDetails;
    
    @JsonProperty("UserOtherDetails")
    private UserOtherDetails userOtherDetails ;
    
    @JsonProperty("AadharUser")
    private AadharUser aadharUser;

    private String title;
    private Long userID;
    private UserAddressDetails address;
    private DivyangDetails divyang;
    private List<DocumentDetails> documentDetails;
}

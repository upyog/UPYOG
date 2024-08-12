package digit.bmc.model;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


import digit.web.models.user.UserDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class VerificationDetails {

    
    private String applicationNumber;
    private Long userId;
    private String tenantId;
    private String scheme;
    private List<String> course;
    private List<String> machine;
    
    @JsonProperty("ApplicantDetails")
    private List<UserDetails> userDetails;

    // private String pincode;
    // private String ward;
    // private String subward;

}


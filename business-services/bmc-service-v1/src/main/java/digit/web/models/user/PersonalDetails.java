package digit.web.models.user;

import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.Caste;
import digit.web.models.Religion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PersonalDetails {

    
    private Title title;
    @JsonProperty("father")
    private String aadharFatherName;
    @JsonProperty("aadharName")
    private String aadharName;
    @JsonProperty("dob")
    private Date aadharDob; 
    private String aadharMobile;
    @JsonProperty("gender")
    private Gender gender;
    @JsonProperty("aadharRef")
    private String aadharRef;

    @JsonProperty("casteCategory")
    private Caste caste;
    @JsonProperty("religion")
    private Religion religion;
    
    @JsonProperty("transgenderId")
    private String transgenderId;

}

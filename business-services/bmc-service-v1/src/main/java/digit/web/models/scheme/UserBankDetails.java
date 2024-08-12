package digit.web.models.scheme;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Component
public class UserBankDetails {
    
    @JsonProperty("Bank")
    private String name ;
    
    @JsonProperty("Branch")
    private String branchName;
   
    @JsonProperty("Account Number")
    private String accountNumber;
   
    @JsonProperty("IFSC")
    private String ifsc;
   
    @JsonProperty("MICR")
    private String micr;

}

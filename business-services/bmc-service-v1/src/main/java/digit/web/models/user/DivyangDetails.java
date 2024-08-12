package digit.web.models.user;


import com.fasterxml.jackson.annotation.JsonProperty;

import digit.bmc.model.Divyang;
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
public class DivyangDetails {
    private Long divyangid;
    private Double divyangpercent;
    @JsonProperty("disabilitytype")
    private Divyang divyangtype;
    private String divyangcardid;
   

    
}
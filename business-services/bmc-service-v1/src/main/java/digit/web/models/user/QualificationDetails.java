package digit.web.models.user;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
@Setter
public class QualificationDetails {
   

    
    private Long qualificationId;
    private String qualification;
    private Long percentage;
    private Long yearOfPassing;
    private String board;

}



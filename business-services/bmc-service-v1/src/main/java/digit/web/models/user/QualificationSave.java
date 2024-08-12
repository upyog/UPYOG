package digit.web.models.user;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class QualificationSave {


    @JsonProperty("qualification")
    private Qualification qualificationDetails;
   
    @JsonProperty("yearOfPassing")
    private YearOfPassing yearOfPassingValue;

    @JsonProperty("board")
    private Board boardValue;

    private Long userId;

    private Long percentage;

    private String createdBy;
    private String modifiedBy;
    private Long createdOn;
    private Long modifiedOn;

}


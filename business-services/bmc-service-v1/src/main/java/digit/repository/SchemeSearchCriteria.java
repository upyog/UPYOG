package digit.repository;

import com.fasterxml.jackson.annotation.JsonProperty;

import digit.common.Status;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;



@Data
@NoArgsConstructor
@ToString
public class SchemeSearchCriteria {

    @JsonProperty("Status")
    private Status status;

    @JsonProperty("StartDate")
    private String startDate;

    @JsonProperty("EndDate")
    private String endDate;

    @JsonProperty("ID")
    private Integer id;

    @JsonProperty("SchemeHead")
    private String schemehead;

    @JsonProperty("SchemeHeadDesc")
    private String schemeheaddesc;
}

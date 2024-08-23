package digit.web.models.employee;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApplicationStatus {

    private String action;

    @JsonProperty("Comment")
    private String comment; 

    @JsonProperty("ApplicationNumbers")
    private List<String> applicationNumbers;

}

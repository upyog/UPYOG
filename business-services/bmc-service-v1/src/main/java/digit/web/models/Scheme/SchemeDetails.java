package digit.web.models.scheme;

import java.util.List;

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
public class SchemeDetails {
    private Long schemeID;
    private String schemeName;
    private String schemeDesc;
    private List<CriteriaDetails> criteria;
    private List<CourseDetails> courses;
    private List<MachineDetails> machines;
}
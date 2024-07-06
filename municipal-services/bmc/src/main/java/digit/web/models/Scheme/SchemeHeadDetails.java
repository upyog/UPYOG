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
public class SchemeHeadDetails{
    private String schemeHead;
    private String schemeheadDesc;
    private List<SchemeDetails> schemeDetails;
}

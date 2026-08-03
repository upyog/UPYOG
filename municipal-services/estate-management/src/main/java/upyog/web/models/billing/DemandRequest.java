package upyog.web.models.billing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequest {
    private RequestInfo requestInfo;
    private List<Demand> demands;
}

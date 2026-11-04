package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SchedulerLogRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("schedulerLog")
    private SchedulerLog schedulerLog;
}

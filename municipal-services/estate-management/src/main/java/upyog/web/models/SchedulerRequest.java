package upyog.web.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.egov.common.contract.request.RequestInfo;

import java.time.LocalDate;

@Data
public class SchedulerRequest {
    private RequestInfo requestInfo;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate billingDate;
}

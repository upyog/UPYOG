package digit.models.coremodels.mdms;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdmsCriteriaReq {

    @JsonProperty("RequestInfo")
    @Valid
    @NotNull
    private RequestInfo requestInfo;

    @JsonProperty("MdmsCriteria")
    @Valid
    @NotNull
    private MdmsCriteria mdmsCriteria;

}

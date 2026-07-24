package org.egov.garbageservice.contract.bill;

import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DemandRequest {

    private RequestInfo requestInfo;
    private List<Demand> demands;
}
package org.egov.inbox.web.model.ndc;

import lombok.Data;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;
@Data
public class NdcResult {
    private int totalCount;
    private ResponseInfo ResponseInfo;
    private List<NdcApplicationResponse> Applications;
}
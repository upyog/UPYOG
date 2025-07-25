/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */
package org.egov.finance.voucher.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseInfo   {
    private String apiId;
    private String ver;
    private Long ts;
    private String resMsgId;
    private String msgId;
    private String status;
    private String tenantId;
}

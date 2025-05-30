/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResponseInfo   {
    private String apiId;
    private String ver;
    private String ts;
    private String resMsgId;
    private String msgId;
    private String status;
    private String tenantId;
}

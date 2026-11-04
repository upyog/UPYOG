package org.upyog.dx.upyogdata.pfms.models;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseInfo {
    private String apiId;
    private String ver;
    private Long ts;
    private String msgId;
    private String resMsgId;
    private String status;
}

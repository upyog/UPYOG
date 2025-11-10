package org.upyog.chb.web.models.user;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSearchRequest {
  private RequestInfo requestInfo;
  private String tenantId;
  private Boolean active;
  private String userType;
  private String mobileNumber;
  private String name;
  private List<String> uuid;
  private String userName;
}

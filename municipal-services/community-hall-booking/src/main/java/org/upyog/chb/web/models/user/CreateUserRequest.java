package org.upyog.chb.web.models.user;

import lombok.*;
import org.egov.common.contract.request.RequestInfo;
import  org.upyog.chb.web.models.User;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateUserRequest {
  private RequestInfo requestInfo;
  private User user;
}

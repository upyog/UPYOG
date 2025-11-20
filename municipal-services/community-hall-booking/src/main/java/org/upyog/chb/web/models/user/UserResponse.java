package org.upyog.chb.web.models.user;

import lombok.*;
import org.upyog.chb.web.models.OwnerInfo;
import  org.upyog.chb.web.models.User;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse {
  private List<OwnerInfo> user;
}

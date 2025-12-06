package org.egov.user.web.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UpdatePasswordResponse {
    private ResponseInfo responseInfo;
}

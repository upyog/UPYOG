package org.egov.persistence.contract;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.egov.common.contract.response.ResponseInfo;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class OtpListResponse {
    private ResponseInfo responseInfo;
    private List<Otp> otp;

}



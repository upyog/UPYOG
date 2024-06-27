package org.egov.user.web.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HpSsoValidateToken {

	private String token;
	private String secret_key;
	private String service_id;
}

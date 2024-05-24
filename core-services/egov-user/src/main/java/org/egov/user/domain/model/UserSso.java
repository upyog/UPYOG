package org.egov.user.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserSso {
	
	private Long id;
	private Long ssoId;
	private String userUuid;
	private AuditDetails auditDetails;

}

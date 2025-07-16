package org.hpud.util;

import org.hpud.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class SupportConstants {

	@Value("${egov.user.host}")
	private String userServiceHostUrl;
	
	@Value("${egov.user.context.path}")
	private String userContextPath;

	@Value("${egov.user.search.endpoint}")
	private String userSearchEndpoint;
}

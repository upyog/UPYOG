package org.egov.pt.calculator.service;

import java.util.Collections;

import org.egov.common.contract.request.User;
import org.egov.pt.calculator.web.models.UserDetailResponse;
import org.egov.pt.calculator.web.models.UserSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {

	private static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";
	private static final String USER_ROLE = "SUPERUSER";

	@Autowired
	private RestTemplate restTemplate;

	@Value("${state.level.tenant.name}")
	private String stateLevelTenant;

	@Value("${egov.user.search.path}")
	private String userSearchURI;

	@Value("${egov.pt.assessment.job.user.name}")
	private String ptAsmtUserName;

	public User fetchPTAsseessmentUser() {

		UserSearchRequest userSearchRequest = new UserSearchRequest();
		userSearchRequest.setUserType(USER_TYPE_EMPLOYEE);
		userSearchRequest.setRoleCodes(Collections.singletonList(USER_ROLE));
		userSearchRequest.setUserName(ptAsmtUserName);
		userSearchRequest.setPageSize(1);
		userSearchRequest.setTenantId(stateLevelTenant);

		StringBuilder userSearchUri = new StringBuilder(userSearchURI);
		User user = null;
		try {
			UserDetailResponse response = restTemplate.postForObject(userSearchUri.toString(), userSearchRequest,
					UserDetailResponse.class);
			if (!CollectionUtils.isEmpty(response.getUser())) {
				user = response.getUser().get(0);
				log.info("PT Assessment User :" + user.toString());
			}
		} catch (Exception e) {
			log.error("Exception while fetching PT Assessment user: ", e);
		}
		return user;
	}
}
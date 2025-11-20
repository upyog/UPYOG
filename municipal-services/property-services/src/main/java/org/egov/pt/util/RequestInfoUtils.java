package org.egov.pt.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.user.UserSearchRequest;
import org.egov.pt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RequestInfoUtils {

	private static final String SYSTEM = "SYSTEM";

	@Autowired
	private UserService userService;

	@Autowired
	private PropertyConfiguration applicationConfig;

	private volatile RequestInfo defaultRequestInfo;

	public RequestInfo getSystemRequestInfo() {
		if (defaultRequestInfo == null) {
			synchronized (this) {
				if (defaultRequestInfo == null) {
					defaultRequestInfo = createDefaultRequestInfo();
				}
			}
		}
		return defaultRequestInfo;
	}

	private RequestInfo createDefaultRequestInfo() {
		User userInfo = User.builder()
				.uuid(SYSTEM)
				.type(SYSTEM).roles(Collections.emptyList()).id(0L).build();

		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setUserInfo(userInfo);

		List<String> roles = new ArrayList<>();
		roles.add(SYSTEM);

		List<User> users = fetchEmployeeByRole(requestInfo, roles);

		if (!CollectionUtils.isEmpty(users)) {
			requestInfo.setUserInfo(users.get(0));
		}

		return requestInfo;
	}

	private List<User> fetchEmployeeByRole(@NotNull RequestInfo requestInfo, List<String> roleCodes) {
		List<User> userList = new ArrayList<>();

		UserSearchRequest request = UserSearchRequest.builder().tenantId(applicationConfig.getStateLevelTenantId())
				.roleCodes(roleCodes).requestInfo(requestInfo).build();

		Map<String, User> uuidToUserMap = userService.searchUser(request);

		if (!uuidToUserMap.isEmpty()) {
			User user = uuidToUserMap.values().stream().findFirst().get();
			userList.add(user);
		}

		return userList;
	}
}
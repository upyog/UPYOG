package org.egov.schedulerservice.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.stereotype.Component;

@Component
public class RequestInfoUtils {

	private static final String SYSTEM = "SYSTEM";

//	@Autowired
//	private UserService userService;
//
//	@Autowired
//	private IdcSchedulerConfiguration estateConfiguration;

//	@Autowired
//	private RequestInfoModelMapper requestInfoModelMapper;

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
		User userInfo = User.builder().uuid(SYSTEM).type(SYSTEM).roles(Collections.emptyList()).id(0L).build();
//
		RequestInfo requestInfo = new RequestInfo();
		requestInfo.setUserInfo(userInfo);
//
		List<String> roles = new ArrayList<>();
//		roles.add(SchedulerConstants.INTERNAL_MICROSERVICE_ROLE_CODE);
//
//		List<User> users = fetchEmployeeByRole(requestInfo, roles);
//
//		requestInfo.setUserInfo(users.get(0));
//
		return requestInfo;
	}
//
//	private List<User> fetchEmployeeByRole(@NotNull RequestInfo requestInfo,
//			List<String> roleCodes) {
//		List<User> userList = new ArrayList<>();
//
//		UserSearchRequest request = UserSearchRequest.builder().tenantId(estateConfiguration.getStateLevelTenantId())
//				.roleCodes(roleCodes).requestInfo(requestInfo).build();
//
//		Map<String, User> uuidToUserMap = userService.searchUser(request);
//
//		if (!uuidToUserMap.isEmpty()) {
//			User user = uuidToUserMap.values().stream().findFirst().get();
//			userList.add(user);
//		}
//
//		return userList;
//	}
}
package org.egov.web.notification.sms.service;

import org.egov.web.notification.sms.models.UserSearchResponse;

public interface UserService {

	UserSearchResponse searchUser(String userUuid);

}

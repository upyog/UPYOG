package org.upyog.cdwm.notification;

import org.upyog.cdwm.web.models.CNDApplicationRequest;

public interface CNDNotificationService {

	public void process(CNDApplicationRequest cndApplicationRequest, String applicationStatus);
	

}

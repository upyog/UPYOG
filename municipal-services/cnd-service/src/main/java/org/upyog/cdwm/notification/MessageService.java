package org.upyog.cdwm.notification;

import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.upyog.cdwm.web.models.CNDApplicationDetail;

public interface MessageService {
	
	 Map<String, String> getCustomizedMsg(RequestInfo requestInfo, CNDApplicationDetail applicationDetail, 
             String localizationMessage);

	 String getMessageWithNumberAndFinalDetails(CNDApplicationDetail applicationDetail, String message);

}

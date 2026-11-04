package org.egov.echallan.service;


import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.*;
import org.egov.echallan.model.Challan.StatusEnum;
import org.egov.echallan.util.NotificationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

import static org.egov.echallan.util.ChallanConstants.*;


@Service
@Slf4j
public class NotificationService {
	private ChallanConfiguration config;

	private NotificationUtil util;

	@Autowired
	public NotificationService(ChallanConfiguration config, NotificationUtil util) {
		this.config = config;
		this.util = util;
	}

	public void sendChallanNotification(ChallanRequest challanRequest,boolean isSave) {
		String action;
		String code;

		if (isSave) {
			action = CREATE_ACTION;
			code = CREATE_CODE;
		} else if (challanRequest.getChallan().getApplicationStatus() == StatusEnum.ACTIVE) {
			action = UPDATE_ACTION;
			code = UPDATE_CODE;
		} else if (challanRequest.getChallan().getApplicationStatus() == StatusEnum.CANCELLED) {
			action = CANCEL_ACTION;
			code = CANCEL_CODE;
		} else if (challanRequest.getChallan().getApplicationStatus() == StatusEnum.PAID) {
			action = PAYMENT_ACTION;
			code = PAYMENT_CODE;
		} else {
			action = "";
			code = null;
		}

		List<String> configuredChannelNames =  util.fetchChannelList(new RequestInfo(), challanRequest.getChallan().getTenantId(), MCOLLECT_BUSINESSSERVICE, action);
		if(configuredChannelNames.contains(CHANNEL_NAME_SMS)){
			List<SMSRequest> smsRequests = new LinkedList<>();
			if (null != config.getIsSMSEnabled()) {
				log.info("is sms enabled: "+config.getIsSMSEnabled());
				if (config.getIsSMSEnabled()) {
					enrichSMSRequest(challanRequest, smsRequests, code);
					if (!CollectionUtils.isEmpty(smsRequests)) {
						util.sendSMS(smsRequests, config.getIsSMSEnabled());
						log.info("smsRequests is not empty: "+smsRequests);
					}
				}
			}
		}
	}

	/**
	 * Enriches the smsRequest with the customized messages
	 *
	 * @param challanRequest
	 *            The challanRequest
	 * @param smsRequestslist
	 *            List of SMSRequets
	 * @param code
	 *            Notification Template Code
	 */
	private void enrichSMSRequest(ChallanRequest challanRequest, List<SMSRequest> smsRequestslist, String code) {
		String message = util.getCustomizedMsg(challanRequest.getRequestInfo(), challanRequest.getChallan(), code);
		String mobilenumber = challanRequest.getChallan().getCitizen().getMobileNumber();

		if (message != null && StringUtils.isNotEmpty(message)) {
			SMSRequest smsRequest = SMSRequest.builder().
					mobileNumber(mobilenumber).
					message(message).build();
			smsRequestslist.add(smsRequest);
		} else {
			log.error("No message configured! Notification will not be sent.");
		}
	}


}

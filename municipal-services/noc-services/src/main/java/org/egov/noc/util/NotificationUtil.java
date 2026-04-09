package org.egov.noc.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.producer.Producer;
import org.egov.noc.repository.ServiceRequestRepository;
import org.egov.noc.service.UserService;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.SMSRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import static org.egov.noc.util.NOCConstants.ACTION_STATUS_CREATED;
import static org.egov.noc.util.NOCConstants.ACTION_STATUS_INITIATED;
import static org.egov.noc.util.NOCConstants.ACTION_STATUS_REJECTED;
import static org.egov.noc.util.NOCConstants.ACTION_STATUS_APPROVED;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NotificationUtil {

	@Autowired
	private NOCConfiguration config;

	@Autowired
	private Producer producer;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private NOCUtil nocUtil;
	
	@Autowired
	ObjectMapper mapper;
	
	@Autowired
	UserService userService;

	/**
	 * Send the SMSRequest on the SMSNotification kafka topic
	 * 
	 * @param smsRequestList
	 *            The list of SMSRequest to be sent
	 */
	public void sendSMS(List<SMSRequest> smsRequestList, boolean isSMSEnabled) {
		if (isSMSEnabled) {
			if (CollectionUtils.isEmpty(smsRequestList))
				log.info("Messages from localization couldn't be fetched!");
			for (SMSRequest smsRequest : smsRequestList) {
				producer.push(config.getSmsNotifTopic(), smsRequest.getMobileNumber(),smsRequest);
				log.info("MobileNumber: " + smsRequest.getMobileNumber() + " Messages: " + smsRequest.getMessage());
			}
		}
	}

	/**
	 * Creates sms request for the each owners
	 * 
	 * @param message
	 *            The message for the specific noc
	 * @param mobileNumberToOwnerName
	 *            Map of mobileNumber to OwnerName
	 * @return List of SMSRequest
	 */
	public List<SMSRequest> createSMSRequest(String message, Map<String, String> mobileNumberToOwner) {
		List<SMSRequest> smsRequest = new LinkedList<>();
		for (Map.Entry<String, String> entryset : mobileNumberToOwner.entrySet()) {
			smsRequest.add(new SMSRequest(entryset.getKey(), message.replace("{1}", entryset.getValue())));
		}
		return smsRequest;
	}

	/**
	 * Returns the uri for the localization call
	 * 
	 * @param tenantId
	 *            TenantId of the propertyRequest
	 * @return The uri for localization search call
	 */
	public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {
		if (config.getIsLocalizationStateLevel())
			tenantId = tenantId.split("\\.")[0];
		String locale = "en_IN";
		if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("|").length >= 2)
			locale = requestInfo.getMsgId().split("\\|")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint()).append("?").append("locale=").append(locale)
				.append("&tenantId=").append(tenantId).append("&module=").append(NOCConstants.SEARCH_MODULE);
		return uri;
	}

	/**
	 * Fetches messages from localization service
	 * 
	 * @param tenantId
	 *            tenantId of the NOC
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @return Localization messages for the module
	 */
	@SuppressWarnings("rawtypes")
	public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {
		LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
				requestInfo);
		String jsonString = new JSONObject(responseMap).toString();
		return jsonString;
	}

	/**
	 * Creates customized message based on noc
	 * 
	 * @param noc
	 *            The noc for which message is to be sent
	 * @param localizationMessage
	 *            The messages from localization
	 * @return customized message based on noc
	 */
	public String getCustomizedMsg(RequestInfo requestInfo, Noc noc, String localizationMessage, String rawRecord) {
		String message = null;
		String messageCode;
		if(noc.getWorkflow() != null) {
			Object mdmsData = getMDMSData(requestInfo, noc.getTenantId().split("\\.")[0]);
			Map<String, Object> notificationConfig = getMDMSNotificationConfig(noc.getWorkflow().getAction()
					,noc.getApplicationStatus(), mdmsData);
			
			messageCode = notificationConfig.getOrDefault("messageCode", "").toString();
			message = getMessageTemplate(messageCode, localizationMessage);
			
			List<Map<String, Object>> variables = JsonPath.read(notificationConfig, "$.variables");
			Map<String, String> employeeMap = new HashMap<>();
			for(Map<String, Object> variable : variables) {
				if((boolean)variable.get("isFixed"))
					message = message.replace(variable.get("variable").toString(), variable.get("value").toString());
				else {
					String filter = variable.get("filter").toString();
					String fixedValue = variable.get("value").toString();
					if(StringUtils.isEmpty(fixedValue)) {
						String value = JsonPath.read(rawRecord, filter).toString();
						message = message.replace(variable.get("variable").toString(), value);
					}else {
						String filteredValue = JsonPath.read(rawRecord, filter);
						employeeMap.put(fixedValue, filteredValue);
						message = message.replace(variable.get("variable").toString(), fixedValue);
					}
				}
			}
			
			String uuids = employeeMap.entrySet().stream().map(Entry::getValue).collect(Collectors.joining(","));
			
			if(!StringUtils.isEmpty(uuids)) {
				Map<String, String> designationMap = userService.getEmployeeDesignation(requestInfo, uuids, noc.getTenantId());
				employeeMap.entrySet().stream().forEach(entry -> {
					List<String> designation = JsonPath.read(mdmsData, "$.MdmsRes.common-masters.Designation.[?(@.code == '" + designationMap.get(entry.getValue()) + "')].name");
					entry.setValue(designation.get(0));
				});
				
				for(Entry<String, String> entry : employeeMap.entrySet()) {
					message = message.replace(entry.getKey(), entry.getValue());
				}
			}
			
		}		
		return message;
	}

	/**
	 * Extracts message for the specific code
	 * 
	 * @param notificationCode
	 *            The code for which message is required
	 * @param localizationMessage
	 *            The localization messages
	 * @return message for the specific code
	 */
	@SuppressWarnings("rawtypes")
	public String getMessageTemplate(String notificationCode, String localizationMessage) {
		String path = "$..messages[?(@.code==\"{}\")].message";
		path = path.replace("{}", notificationCode);
		String message = null;
		try {
			List data = JsonPath.parse(localizationMessage).read(path);
			if (!CollectionUtils.isEmpty(data))
				message = data.get(0).toString();
			else
				log.error("Fetching from localization failed with code " + notificationCode);
		} catch (Exception e) {
			log.warn("Fetching from localization failed", e);
		}
		return message;
	}
	
	public Map<String, Object> getMDMSNotificationConfig(String action, String state, Object mdmsdata){
		final String nocFilterCode = "$.MdmsRes.NOC.NotificationConfig.[?(@.active==true && @.action contains '" + action + "' && @.state contains '" + state + "')]";
		
		List<Map<String, Object>> list = JsonPath.read(mdmsdata, nocFilterCode);
		
		if(CollectionUtils.isEmpty(list))
			return new HashMap<>();
		else
			return list.get(0);
	}
	
	public Object getMDMSData(RequestInfo requestInfo, String tenantId){

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		
		ModuleDetail nocModuleDetails = ModuleDetail.builder()
				.masterDetails(Collections.singletonList(MasterDetail.builder()
						.name("NotificationConfig")
						.build()))
				.moduleName(NOCConstants.NOC_MODULE).build();
		
		ModuleDetail designationModuleDetails = ModuleDetail.builder()
				.masterDetails(Collections.singletonList(MasterDetail.builder()
						.name("Designation")
						.build()))
				.moduleName("common-masters").build();
		
		moduleDetails.add(nocModuleDetails);
		moduleDetails.add(designationModuleDetails);
		
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		
		return serviceRequestRepository.fetchResult(nocUtil.getMdmsSearchUrl(), mdmsCriteriaReq);
	}

}
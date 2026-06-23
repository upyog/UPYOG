package org.egov.rl.calculator.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.calculator.util.Configurations;
import org.egov.rl.calculator.util.RLConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class JobScheduler {

	@Autowired
	ObjectMapper mapper;

	@Autowired
	DemandService demandService;
	
	@Autowired
	RLAutoEscalationService autoEscalationService;
	
	@Autowired
	private Configurations config;

	
	@Scheduled(cron = "${scheduler.bulk.cron:0 30 3 * * *}", zone = RLConstants.TIME_ZONE)
	public void bulkDemandGenerationCronJob() {
		log.info("Scheduler Start Every day at 03:30 AM");
//		getOAuthToken();
		log.info("Morning Scheduler Start Date Time :{}",LocalDateTime.now());
		  demandService.generateBatchDemand(getOAuthToken(),null,null);
		log.info("Morning Scheduler End Date Time :{}",LocalDateTime.now());
	}
	
//	@Scheduled(cron = "0 30 10 * * *", zone = RLConstants.TIME_ZONE)
//	@Scheduled(cron = "${scheduler.notification.cron:0 0 1 * * *}", zone = RLConstants.TIME_ZONE)
	@Scheduled(cron = "0 * * * * *", zone = RLConstants.TIME_ZONE)
	public void sendNotificationAndUpdateDemandCronJob() {
		log.info("Scheduler Start Every day at 01:00 AM");
		
		log.info("Nightly Scheduler Start Date Time :{}",LocalDateTime.now());
		  demandService.sendNotificationAndUpdateDemand(getOAuthToken(),null,null);
		log.info("Nightly Scheduler End Date Time :{}",LocalDateTime.now());
		
	}


	public RequestInfo getOAuthToken() {

		return autoEscalationService.getDefaultRequestInfo();
	}
	
//	public RequestInfo getOAuthToken() {
//		
//		String baseHost = config.getUserHost();
//
//		String url = baseHost+"/user/oauth/token";
//
//		// -------- Headers --------
//		HttpHeaders headers = new HttpHeaders();
//		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//		headers.add("Accept", "application/json, text/plain, */*");
//		headers.add("Accept-Language", "en-GB,en-US;q=0.9,en;q=0.8");
//		headers.add("Authorization", "Basic " + "ZWdvdi11c2VyLWNsaWVudDplZ292LXVzZXItc2VjcmV0");
//		headers.add("Origin", "https://mseva-dev.lgpunjab.gov.in");
//		headers.add("Referer", "https://mseva-dev.lgpunjab.gov.in/digit-ui/");
//		headers.add("Priority", "u=1, i");
//
//		// -------- BODY (x-www-form-urlencoded) --------
//		MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
//		body.add("username", "9872904783");
//		body.add("password", "123456");
//		body.add("tenantId", "pb");
//		body.add("userType", "citizen");
//		body.add("grant_type", "password");
//
//		System.out.println("userReq:---"+headers);
//
//		System.out.println("userbody:---"+body);
//		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
//
//		RestTemplate restTemplate = new RestTemplate();
//		Map requestInfo = restTemplate.postForEntity(url, request, Map.class).getBody();
//		Object user = requestInfo.get("UserRequest");
//
//		User users = mapper.convertValue(user, User.class);
//		List<Role> r = users.getRoles().stream().map(d -> {
//			d = Role.builder().code(d.getCode()).id(d.getId()).name(d.getName()).tenantId(d.getTenantId()).build();
//			return d;
//		}).collect(Collectors.toList());
//		users.setRoles(r);
//		RequestInfo requestInfo1 = RequestInfo.builder().apiId("Rainmaker")
//				.authToken(requestInfo.get("access_token").toString()).msgId("1756728031554|en_IN")
//				.plainAccessRequest(null).userInfo(users).build();
//		return requestInfo1;
//	}
}

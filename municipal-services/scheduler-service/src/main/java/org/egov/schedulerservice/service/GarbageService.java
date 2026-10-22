package org.egov.schedulerservice.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.schedulerservice.config.SchedulerConfiguration;
import org.egov.schedulerservice.constants.ErrorConstants;
import org.egov.schedulerservice.exception.SchedulerServiceException;
import org.egov.schedulerservice.util.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class GarbageService {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private SchedulerConfiguration applicationConfig;

	public String generateGarbageBills(RequestInfo requestInfo) {

		try {
			StringBuilder url = new StringBuilder(applicationConfig.getGarbageServiceHostUrl());
			url.append(applicationConfig.getGarbageBillGeneratorEndpoint());
			// Make the POST request

			RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

			ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
					String.class);
			return responseEntity.getBody();
		} catch (Exception e) {
			log.error("Error occured while generating garbage bill.", e);
			throw new SchedulerServiceException(ErrorConstants.ERR_GARBAGE_SERVICE_ERROR,
					"Error occured while generating garbage bill. Message: " + e.getMessage());
		}

	}
	
	 public String updatePenaltyAmount(RequestInfo requestInfo) {
	        try {
	            StringBuilder url = new StringBuilder(applicationConfig.getGarbageServiceHostUrl());
	            url.append(applicationConfig.getGarbageUpdatePenaltyAmountEndpoint());

	            RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder()
	                    .requestInfo(requestInfo)
	                    .build();

	            ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
	                    String.class);
	            return responseEntity.getBody();
	        } catch (Exception e) {
	            log.error("Error occured while updating garbage penalty amount.", e);
	            throw new SchedulerServiceException("ERR_GARBAGE_SERVICE_ERROR",
	                    "Error occured while updating garbage penalty amount. Message: " + e.getMessage());
	        }
	    }
	 
	 public String reverseRebateAmount(RequestInfo requestInfo) {

			try {
				StringBuilder url = new StringBuilder(applicationConfig.getGarbageServiceHostUrl());
				
				url.append(applicationConfig.getPropertyReverseRebateAmountEndpoint());
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
						String.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				log.error("Error occured while reverse rebate amount.", e);
				throw new SchedulerServiceException(ErrorConstants.ERR_GARBAGE_SERVICE_ERROR,
						"Error occured while reverse rebate amount. Message: " + e.getMessage());
			}
		}	 
	 
		public String setDdpReadyForPrinting(RequestInfo requestInfo) {
			try {
				StringBuilder url = new StringBuilder(applicationConfig.getGarbageServiceHostUrl());
				url.append(applicationConfig.getDdpReadyForPrintingEndpoint());
				RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();

				ResponseEntity<String> responseEntity = restTemplate.postForEntity(url.toString(), requestInfoWrapper,
						String.class);
				return responseEntity.getBody();
			} catch (Exception e) {
				log.error("Error occured while update penalty amount.", e);
				throw new SchedulerServiceException(ErrorConstants.ERR_PROPERTY_SERVICE_ERROR,
						"Error occured while setting isReadyForPrinting. Message: " + e.getMessage());
			}
		}

}

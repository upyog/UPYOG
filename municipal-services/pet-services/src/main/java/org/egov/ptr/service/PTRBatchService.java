package org.egov.ptr.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.ptr.config.PetConfiguration;
import org.egov.ptr.models.PetApplicationSearchCriteria;
import org.egov.ptr.models.PetRegistrationApplication;
import org.egov.ptr.models.PetRegistrationRequest;
import org.egov.ptr.producer.Producer;
import org.egov.ptr.repository.PetRegistrationRepository;
import static org.egov.ptr.util.PTRConstants.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PTRBatchService {

	@Autowired
	private PetConfiguration config;

	@Autowired
	private PetRegistrationRepository repository;

	@Autowired
	private WorkflowService workflowIntegrator;

	@Autowired
	private Producer producer;

	/**
	 * Searches pet applications which are active and expire their status and sends
	 * reminder sms to owner's of the pet license(pending)
	 * 
	 * @param serviceName
	 * @param requestInfo
	 */
	public void getPetApplicationsAndPerformAction(String serviceName, String jobName, RequestInfo requestInfo) {

		// Pre-calculate validity date outside the loop
		LocalDate today = LocalDate.now();
		LocalDate nextMarch31 = LocalDate.of(today.getYear(), Month.MARCH, 31);
		if (today.isAfter(nextMarch31)) {
			nextMarch31 = nextMarch31.plusYears(1);
		}
		LocalDateTime nextMarch31At8PM = LocalDateTime.of(nextMarch31, LocalTime.of(20, 0));
		long validityDateUnix = nextMarch31At8PM.atZone(ZoneId.systemDefault()).toEpochSecond();

		List<String> tenantIdsFromRepository = repository.fetchPetApplicationTenantIds();

		tenantIdsFromRepository.forEach(tenantIdFromRepository -> {
			try {
				processTenantApplications(tenantIdFromRepository, validityDateUnix, requestInfo);
			} catch (Exception ex) {
				log.error("Batch process failed for tenant ID: " + tenantIdFromRepository, ex);
			}
		});
	}

	private void processTenantApplications(String tenantId, long validityDateUnix, RequestInfo requestInfo) {
		PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder().validityDate(validityDateUnix)
				.status(STATUS_APPROVED).tenantId(tenantId).build();

		int offset = 0;
//		int pageSize = config.getPaginationSize();

		while (true) {
			// Set pagination for current batch
//			criteria.setOffset(offset);
//			criteria.setLimit(pageSize);

			log.info("Fetching applications with offset: " + offset);

			List<PetRegistrationApplication> petApplications = repository.getApplications(criteria);

			// If no more applications, break the loop
			if (CollectionUtils.isEmpty(petApplications)) {
				break;
			}

			// Expire and update applications
			expireAndUpdatePetApplications(requestInfo, petApplications);

			// Move to the next batch
//			offset += pageSize;
		}
	}

	private void expireAndUpdatePetApplications(RequestInfo requestInfo,
			List<PetRegistrationApplication> petApplications) {
		// Expire applications
		petApplications.forEach(petApplication -> {
			petApplication.setExpireFlag(true);
			petApplication.setStatus(STATUS_EXPIRED);
		});

		try {
			// Update applications in batch
			PetRegistrationRequest petRegistrationRequest = new PetRegistrationRequest(requestInfo, petApplications);
			producer.push(config.getUpdatePtrTopic(), petRegistrationRequest);
		} catch (Exception e) {
			log.error("Failed to update pet applications batch.", e);
		}

		try {
			// Update workflow status in batch
			PetRegistrationRequest petRegistrationRequest = new PetRegistrationRequest(requestInfo, petApplications);
			workflowIntegrator.updateWorkflowStatus(petRegistrationRequest);
		} catch (Exception e) {
			log.error("Workflow status update failed for expiring applications.", e);
		}
	}

}

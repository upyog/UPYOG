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

		List<String> tenantIdsFromRepository = repository.fetchTradeLicenseTenantIds();
		tenantIdsFromRepository.forEach(tenantIdFromRepository -> {
			try {
				LocalDate today = LocalDate.now();
				// Get this year's March 31st
				LocalDate nextMarch31 = LocalDate.of(today.getYear(), Month.MARCH, 31);
				// If today's date is after March 31st, use March 31st of next year
				if (today.isAfter(nextMarch31)) {
					nextMarch31 = nextMarch31.plusYears(1);
				}
				LocalDateTime nextMarch31At8PM = LocalDateTime.of(nextMarch31, LocalTime.of(20, 0));

				// Convert LocalDateTime to a Unix timestamp (seconds since 1970-01-01)
				long validityDateUnix = nextMarch31At8PM.atZone(ZoneId.systemDefault()).toEpochSecond(); // Convert to
																											// ZonedDateTime
																											// using
				// the system's default timezone

				PetApplicationSearchCriteria criteria = PetApplicationSearchCriteria.builder()
						.validityDate(validityDateUnix).status(STATUS_APPROVED).tenantId(tenantIdFromRepository)
						.build();
				int offSet = 0;
//				criteria.setOffset(offSet);
				while (true) {
					log.info("current Offset: " + offSet);
					List<PetRegistrationApplication> petApplicationsFromRepository = repository
							.getApplications(criteria);
					if (CollectionUtils.isEmpty(petApplicationsFromRepository))
						break;
					expirePetApplications(requestInfo, petApplicationsFromRepository);
//					offSet = offSet + config.getPaginationSize();
//					criteria.setOffset(offSet);
				}
			} catch (Exception ex) {
				log.error("The batch process could not be completed for the tenant id : " + tenantIdFromRepository);
			}
		});
	}

	private void expirePetApplications(RequestInfo requestInfo,
			List<PetRegistrationApplication> petApplicationsFromRepository) {

		petApplicationsFromRepository.forEach(petApplication -> {
			try {
				petApplication.setExpireFlag(true);
				petApplication.setStatus(STATUS_EXPIRED);
				producer.push(config.getUpdatePtrTopic(),
						new PetRegistrationRequest(requestInfo, petApplicationsFromRepository));
			} catch (Exception e) {
				log.error("The batch process could not be completed while updating workflow or application status ");
			}
		});

		workflowIntegrator.updateWorkflowStatus(new PetRegistrationRequest(requestInfo, petApplicationsFromRepository));
	}
}

package org.egov.pgr.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import org.egov.pgr.util.PGRConstants;
import org.egov.pgr.web.models.RequestInfoWrapper;
import org.egov.pgr.web.models.RequestSearchCriteria;
import org.egov.pgr.web.models.ServiceStatusUpdateRequest;
import org.egov.pgr.web.models.ServiceWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PGRSchedulerService {

	@Autowired
	private PGRService pgrService;

	public void escalateRequest(RequestInfoWrapper requestInfoWrapper) {

		RequestSearchCriteria requestSearchCriteria = RequestSearchCriteria.builder()
				.applicationStatus(Collections.singleton(PGRConstants.PENDINGATLME)).build();

		List<ServiceWrapper> serviceWrappers = pgrService.search(requestInfoWrapper.getRequestInfo(),
				requestSearchCriteria);

		if (!CollectionUtils.isEmpty(serviceWrappers)) {
			serviceWrappers.stream().forEach(serviceWrapper -> {

				if (Instant.ofEpochMilli(serviceWrapper.getService().getAuditDetails().getLastModifiedTime())
						.isBefore(Instant.now().minus(Duration.ofHours(72)))) {

//					ServiceRequest serviceRequest = ServiceRequest.builder()
//							.requestInfo(requestInfoWrapper.getRequestInfo()).service(serviceWrapper.getService())
//							.workflow(serviceWrapper.getWorkflow()).build();
//
//					serviceRequest.getService().setApplicationStatus(PGRConstants.PENDINGATLMHE);
//					serviceRequest.getWorkflow().setAction(PGRConstants.ACTION_FORWARD_TO_APPROVER);

//					pgrService.update(serviceRequest);

					ServiceStatusUpdateRequest serviceStatusUpdateRequest = ServiceStatusUpdateRequest.builder()
							.requestInfo(requestInfoWrapper.getRequestInfo())
							.tenantId(serviceWrapper.getService().getTenantId())
							.serviceRequestId(serviceWrapper.getService().getServiceRequestId())
							.applicationStatus(PGRConstants.PENDINGATLMHE).workflow(serviceWrapper.getWorkflow())
							.build();

					serviceStatusUpdateRequest.getWorkflow().setAction(PGRConstants.ACTION_FORWARD_TO_APPROVER);

					pgrService.updateStatus(serviceStatusUpdateRequest);
				}
			});
		}

	}

}

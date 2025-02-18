package org.egov.pgr.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.pgr.config.PGRConfiguration;
import org.egov.pgr.producer.Producer;
import org.egov.pgr.util.PGRConstants;
import org.egov.pgr.util.PGRUtils;
import org.egov.pgr.web.models.AuditDetails;
import org.egov.pgr.web.models.PGRNotification;
import org.egov.pgr.web.models.PGRNotificationRequest;
import org.egov.pgr.web.models.PgrNotificationSearchCriteria;
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

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private Producer producer;

	@Autowired
	private PGRConfiguration config;

	@Autowired
	private PGRUtils utils;

	public void escalateRequest(RequestInfoWrapper requestInfoWrapper) {

		RequestSearchCriteria requestSearchCriteria = RequestSearchCriteria.builder()
				.applicationStatus(Collections.singleton(PGRConstants.PENDINGATLME)).build();

		List<ServiceWrapper> serviceWrappers = pgrService.search(requestInfoWrapper.getRequestInfo(),
				requestSearchCriteria);

		if (!CollectionUtils.isEmpty(serviceWrappers)) {
			serviceWrappers.stream().forEach(serviceWrapper -> {

				if (Instant.ofEpochMilli(serviceWrapper.getService().getAuditDetails().getLastModifiedTime())
						.isBefore(Instant.now().minus(Duration.ofHours(72)))) {

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

	public void sendNotification(RequestInfoWrapper requestInfoWrapper) {

		PgrNotificationSearchCriteria pgrNotificationSearchCriteria = PgrNotificationSearchCriteria.builder().build();

		List<PGRNotification> pgrNotifications = pgrService.searchPgrNotification(pgrNotificationSearchCriteria);

		if (!CollectionUtils.isEmpty(pgrNotifications)) {
			pgrNotifications.stream().forEach(pgrNotification -> {
				try {
					pgrNotification = notificationService.triggerNotificationsStatusChange(pgrNotification,
							requestInfoWrapper);
					updatePgrNotification(pgrNotification, requestInfoWrapper);
				} catch (Exception e) {
					log.error("NOTIFICATION_NOT_SENT",
							"Notification not sent for Grievance with ID No. " + pgrNotification.getServiceRequestId());
				}
			});
		}

	}

	private void updatePgrNotification(PGRNotification pgrNotification, RequestInfoWrapper requestInfoWrapper) {

		AuditDetails auditDetails = utils.buildUpdateAuditDetails(pgrNotification.getAuditDetails(),
				requestInfoWrapper.getRequestInfo());

		pgrNotification.setAuditDetails(auditDetails);

		PGRNotificationRequest pgrNotificationRequest = PGRNotificationRequest.builder()
				.pgrNotification(pgrNotification).requestInfo(requestInfoWrapper.getRequestInfo()).build();

		producer.push(config.getUpdateNotificationTopic(), pgrNotificationRequest);
	}

	public void deleteNotification(RequestInfoWrapper requestInfoWrapper) {

		PgrNotificationSearchCriteria pgrNotificationSearchCriteria = PgrNotificationSearchCriteria.builder()
				.isEmailSent(true).isSmsSent(true).build();

		List<PGRNotification> pgrNotifications = pgrService.searchPgrNotification(pgrNotificationSearchCriteria);

		List<String> uuidList = pgrNotifications.stream().map(PGRNotification::getUuid).collect(Collectors.toList());

		pgrService.deletePgrNotification(uuidList);
	}

}

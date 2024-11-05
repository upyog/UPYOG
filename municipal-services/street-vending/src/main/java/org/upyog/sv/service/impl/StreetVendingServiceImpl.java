package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.service.DemandService;
import org.upyog.sv.service.EnrichmentService;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.service.WorkflowService;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.validator.StreetVendingValidator;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StreetVendingServiceImpl implements StreetVendingService {

	@Autowired
	private MdmsUtil util;

	@Autowired
	private EnrichmentService enrichmentService;

	@Autowired
	private StreetVendingRepository streetVendingRepository;

	@Autowired
	private DemandService demandService;

	@Autowired
	private WorkflowService workflowService;

	@Autowired
	private StreetVendingValidator validator;

	@Override
	public StreetVendingDetail createStreetVendingApplication(StreetVendingRequest vendingRequest) {
		RequestInfo requestInfo = vendingRequest.getRequestInfo();
		String tenantId = extractTenantId(vendingRequest);
		if (vendingRequest.getStreetVendingDetail().getTenantId().split("\\.").length == 1) {
			throw new CustomException(StreetVendingConstants.INVALID_TENANT,
					"Application cannot be created at StateLevel");
		}

		Object mdmsData = util.mDMSCall(requestInfo, tenantId);
		log.info("MDMS master data : " + mdmsData);

		enrichmentService.enrichCreateStreetVendingRequest(vendingRequest);

		workflowService.updateWorkflowStatus(vendingRequest);

		// TODO: Move demand generation to update booking working api
		// demandService.createDemand(vendingRequest, tenantId);

		streetVendingRepository.save(vendingRequest);

		return vendingRequest.getStreetVendingDetail();
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo info,
			StreetVendingSearchCriteria streetVendingSearchCriteria) {
		List<StreetVendingDetail> applications = streetVendingRepository
				.getStreetVendingApplications(streetVendingSearchCriteria);

		if (CollectionUtils.isEmpty(applications))
			return new ArrayList<>();
		return applications;
	}

	@Override
	public StreetVendingDetail updateStreetVendingApplication(StreetVendingRequest vendingRequest) {
		StreetVendingDetail existingApplication = validator
				.validateApplicationExistence(vendingRequest.getStreetVendingDetail());

		if (existingApplication == null) {
			return null;
		}

		existingApplication.setWorkflow(vendingRequest.getStreetVendingDetail().getWorkflow());
		vendingRequest.setStreetVendingDetail(existingApplication);

		enrichmentService.enrichStreetVendingApplicationUponUpdate(vendingRequest);
		workflowService.updateWorkflowStatus(vendingRequest);
		streetVendingRepository.update(vendingRequest);

		if (StreetVendingConstants.ACTION_PAY.equals(existingApplication.getWorkflow().getAction())) {
			String tenantId = extractTenantId(vendingRequest);
			demandService.createDemand(vendingRequest, tenantId);
		}

		return vendingRequest.getStreetVendingDetail();
	}

	private String extractTenantId(StreetVendingRequest request) {
		return request.getStreetVendingDetail().getTenantId().split("\\.")[0];
	}

	@Override
	public Integer getApplicationsCount(StreetVendingSearchCriteria streetVendingSearchCriteria,
			RequestInfo requestInfo) {
		streetVendingSearchCriteria.setCountCall(true);
		Integer bookingCount = 0;

		streetVendingSearchCriteria = addCreatedByMeToCriteria(streetVendingSearchCriteria, requestInfo);
		bookingCount = streetVendingRepository.getApplicationsCount(streetVendingSearchCriteria);

		return bookingCount;
	}

	private StreetVendingSearchCriteria addCreatedByMeToCriteria(StreetVendingSearchCriteria criteria,
			RequestInfo requestInfo) {
		if (requestInfo.getUserInfo() == null) {
			log.info("Request info is null returning criteira");
			return criteria;
		}
		List<String> roles = new ArrayList<>();
		for (Role role : requestInfo.getUserInfo().getRoles()) {
			roles.add(role.getCode());
		}
		log.info("user roles for searching : " + roles);
		/**
		 * Citizen can see booking details only booked by him
		 */
		List<String> uuids = new ArrayList<>();
		if (roles.contains(StreetVendingConstants.CITIZEN)
				&& !StringUtils.isEmpty(requestInfo.getUserInfo().getUuid())) {
			uuids.add(requestInfo.getUserInfo().getUuid());
			criteria.setCreatedBy(uuids);
			log.debug("loading data of created and by me" + uuids.toString());
		}
		return criteria;
	}
}

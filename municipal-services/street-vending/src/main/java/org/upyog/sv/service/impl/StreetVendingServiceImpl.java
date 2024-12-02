package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

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
import org.upyog.sv.service.StreetVendingEncryptionService;
import org.upyog.sv.service.StreetVendingService;
import org.upyog.sv.service.WorkflowService;
import org.upyog.sv.util.MdmsUtil;
import org.upyog.sv.validator.StreetVendingValidator;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;
import org.upyog.sv.web.models.VendorDetail;
import org.upyog.sv.web.models.workflow.State;

import lombok.NonNull;
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
	
	@Autowired
	private StreetVendingEncryptionService encryptionService;

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
		validator.validateCreate(vendingRequest, mdmsData);
		enrichmentService.enrichCreateStreetVendingRequest(vendingRequest);
		workflowService.updateWorkflowStatus(vendingRequest);
		encryptionService.encryptObject(vendingRequest);
		streetVendingRepository.save(vendingRequest);
		return vendingRequest.getStreetVendingDetail();
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo requestInfo,
			StreetVendingSearchCriteria streetVendingSearchCriteria) {
		
		if (streetVendingSearchCriteria.getMobileNumber() != null) {
			VendorDetail applicantDetail = VendorDetail.builder()
					.mobileNo(streetVendingSearchCriteria.getMobileNumber())
					.dob("1985-02-01").build(); //TODO to remove dob from here 
			
			List<VendorDetail> vendorDetails = new ArrayList<>();
			vendorDetails.add(applicantDetail);
			
			StreetVendingDetail streetVendingDetail = StreetVendingDetail.builder().vendorDetail(vendorDetails).build();
			StreetVendingRequest streetVendingRequest = StreetVendingRequest.builder()
					.streetVendingDetail(streetVendingDetail).requestInfo(requestInfo).build();
			StreetVendingDetail encryptedDetail = encryptionService.encryptObject(streetVendingRequest);
			
			if (encryptedDetail.getVendorDetail() != null && !encryptedDetail.getVendorDetail().isEmpty()) {
				streetVendingSearchCriteria.setMobileNumber(encryptedDetail.getVendorDetail().get(0).getMobileNo());
			} else {
				log.warn("Encryption returned no vendor details. Criteria not updated.");
			}

			log.info("Loading data based on criteria after encrypting mobile number: {}", streetVendingSearchCriteria);
		}
		List<StreetVendingDetail> applications = streetVendingRepository
				.getStreetVendingApplications(streetVendingSearchCriteria);
		if (CollectionUtils.isEmpty(applications)) {
			return new ArrayList<>();
		}
		applications = encryptionService.decryptObject(applications, requestInfo);
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

		
		State state = workflowService.updateWorkflowStatus(vendingRequest);
		String applicationStatus = state.getApplicationStatus();
		enrichmentService.enrichStreetVendingApplicationUponUpdate(applicationStatus,vendingRequest);
		streetVendingRepository.update(vendingRequest);

		if (StreetVendingConstants.ACTION_APPROVE.equals(existingApplication.getWorkflow().getAction())) {
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

	@Override
	public StreetVendingDetail createStreetVendingDraftApplication(StreetVendingRequest vendingRequest) {
		
		if(StringUtils.isNotBlank(vendingRequest.getStreetVendingDetail().getDraftId())) {
			enrichmentService.enrichUpdateStreetVendingDraftApplicationRequest(vendingRequest);
			streetVendingRepository.updateDraftApplication(vendingRequest);
		} else {
			enrichmentService.enrichCreateStreetVendingDraftApplicationRequest(vendingRequest);
			streetVendingRepository.saveDraftApplication(vendingRequest);
		}
		
		
		return vendingRequest.getStreetVendingDetail();
	}
	
	@Override
	public List<StreetVendingDetail> getStreetVendingDraftApplicationDetails(@NonNull RequestInfo requestInfo,
			@Valid StreetVendingSearchCriteria streetVendingSearchCriteria) {
		return streetVendingRepository.getStreetVendingDraftApplications(requestInfo, streetVendingSearchCriteria);
	}
}

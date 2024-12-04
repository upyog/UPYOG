package org.upyog.sv.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
import org.upyog.sv.web.models.BankDetail;
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
		// 1
		validator.validateCreate(vendingRequest, mdmsData);
		// 2
		enrichmentService.enrichCreateStreetVendingRequest(vendingRequest);
		// 3
		workflowService.updateWorkflowStatus(vendingRequest);
		// 4
		StreetVendingDetail originalDetail = copyFieldsToBeEncrypted(vendingRequest.getStreetVendingDetail());
		encryptionService.encryptObject(vendingRequest);
		// 5
		streetVendingRepository.save(vendingRequest);
		String draftId = vendingRequest.getStreetVendingDetail().getDraftId();
		// 6
		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			streetVendingRepository.deleteDraftApplication(draftId);
		}
		// 7
		StreetVendingDetail streetVendingDetail = vendingRequest.getStreetVendingDetail();
		streetVendingDetail.setVendorDetail(originalDetail.getVendorDetail());
		streetVendingDetail.setBankDetail(originalDetail.getBankDetail());
		return streetVendingDetail;
	}

	@Override
	public List<StreetVendingDetail> getStreetVendingDetails(RequestInfo requestInfo,
			StreetVendingSearchCriteria streetVendingSearchCriteria) {

		if (StringUtils.isNotBlank(streetVendingSearchCriteria.getMobileNumber())) {
			String encryptedMobileNumber = encryptMobileNumber(streetVendingSearchCriteria.getMobileNumber(),
					requestInfo);
			streetVendingSearchCriteria.setMobileNumber(encryptedMobileNumber);

			log.info("Updated search criteria with encrypted mobile number: {}", streetVendingSearchCriteria);
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
			throw new CustomException(StreetVendingConstants.INVALID_APPLICATION, "Application not found");
		}

		State state = workflowService.updateWorkflowStatus(vendingRequest);
		enrichmentService.enrichStreetVendingApplicationUponUpdate(state.getApplicationStatus(), vendingRequest);

		if (StreetVendingConstants.ACTION_APPROVE
				.equals(vendingRequest.getStreetVendingDetail().getWorkflow().getAction())) {
			demandService.createDemand(vendingRequest, extractTenantId(vendingRequest));
		}
		StreetVendingDetail originalDetail = copyFieldsToBeEncrypted(vendingRequest.getStreetVendingDetail());
		encryptionService.encryptObject(vendingRequest);
		streetVendingRepository.update(vendingRequest);
		StreetVendingDetail streetVendingDetail = vendingRequest.getStreetVendingDetail();
		streetVendingDetail.setVendorDetail(originalDetail.getVendorDetail());
		streetVendingDetail.setBankDetail(originalDetail.getBankDetail());
		return streetVendingDetail;
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

		if (StringUtils.isNotBlank(vendingRequest.getStreetVendingDetail().getDraftId())) {
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

	public String deleteStreetVendingDraft(String draftId) {

		if (StringUtils.isNotBlank(draftId)) {
			log.info("Deleting draft entry for draft id: " + draftId);
			streetVendingRepository.deleteDraftApplication(draftId);
		}

		return StreetVendingConstants.DRAFT_DISCARDED;
	}

	public String encryptMobileNumber(String mobileNumber, RequestInfo requestInfo) {
		if (StringUtils.isBlank(mobileNumber)) {
			throw new IllegalArgumentException("Mobile number cannot be null or blank.");
		}

		try {
			// Create a StreetVendingRequest object with the mobile number
			StreetVendingRequest streetVendingRequest = StreetVendingRequest.builder()
					.streetVendingDetail(StreetVendingDetail.builder()
							.vendorDetail(
									Collections.singletonList(VendorDetail.builder().mobileNo(mobileNumber).build()))
							.build())
					.requestInfo(requestInfo).build();

			// Encrypt the mobile number
			StreetVendingDetail encryptedDetail = encryptionService.encryptObject(streetVendingRequest);

			if (encryptedDetail == null || encryptedDetail.getVendorDetail() == null
					|| encryptedDetail.getVendorDetail().isEmpty()) {
				throw new CustomException("ENCRYPTION_FAILED", "Failed to encrypt mobile number.");
			}

			return encryptedDetail.getVendorDetail().get(0).getMobileNo();
		} catch (Exception e) {
			log.error("Error encrypting mobile number", e);
			throw new CustomException("ENCRYPTION_ERROR", "Error occurred while encrypting mobile number.");
		}
	}

	private StreetVendingDetail copyFieldsToBeEncrypted(StreetVendingDetail detail) {
		return StreetVendingDetail.builder()
				.vendorDetail(detail.getVendorDetail() != null
						? detail.getVendorDetail().stream().map(VendorDetail::new).collect(Collectors.toList())
						: null)
				.bankDetail(detail.getBankDetail() != null ? new BankDetail(detail.getBankDetail()) : null).build();
	}

}

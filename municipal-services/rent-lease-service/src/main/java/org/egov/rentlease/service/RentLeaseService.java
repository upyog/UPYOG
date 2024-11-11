package org.egov.rentlease.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.Role;
import org.egov.rentlease.model.Asset;
import org.egov.rentlease.model.AssetRequest;
import org.egov.rentlease.model.AuditDetails;
import org.egov.rentlease.model.RentLease;
import org.egov.rentlease.model.RentLeaseCreationRequest;
import org.egov.rentlease.model.RentLeaseCreationResponse;
import org.egov.rentlease.model.RentLeaseSearchRequest;
import org.egov.rentlease.model.SearchCriteria;
import org.egov.rentlease.producer.Producer;
import org.egov.rentlease.repository.RentLeaseRepository;
import org.egov.rentlease.repository.RestCallRepository;
import org.egov.rentlease.util.RentConstants;
import org.egov.rentlease.util.ResponseInfoFactory;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class RentLeaseService {

	@Autowired
	RentLeaseRepository repo;

	@Autowired
	ResponseInfoFactory responseInfoFactory;

	@Autowired
	Producer producer;

	@Autowired
	WorkflowService workflowService;
	
	@Autowired
	RestCallRepository restCallRepository;
	
	@Autowired
	private RentConstants constants;

	public RentLeaseCreationResponse create(RentLeaseCreationRequest request) {
		RentLeaseCreationResponse rentResponse = null;
		try {
			Map<String, Asset> siteMap = validateAndSearchAssetData(request);
			enrichRentAndLease(request);
			createRentAndLease(request);
			workflowService.updateWorkflowStatus(request);
			updateSiteDataAfterBookingCreate(request,siteMap);
			rentResponse = RentLeaseCreationResponse.builder()
					.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), false))
					.rentLease(request.getRentLease()).build();
			
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return rentResponse;
	}

	private void updateSiteDataAfterBookingCreate(RentLeaseCreationRequest request, Map<String, Asset> assetMap) {
		   
		AssetRequest assetRequest = new AssetRequest();
		assetMap.entrySet().stream().forEach(entry -> {
			entry.getValue().setStatus(constants.RENT_STATUS_BOOKED);
			if(null != entry.getValue().getAuditDetails()
					&& null != request.getRequestInfo()
							&& null != request.getRequestInfo().getUserInfo()) {
				entry.getValue().getAuditDetails().setLastModifiedBy(request.getRequestInfo().getUserInfo().getUuid());
				entry.getValue().getAuditDetails().setLastModifiedDate(new Date().getTime());
				//assetRequest = AssetRequest.builder().asset(entry.getValue()).requestInfo(request.getRequestInfo()).build();
				assetRequest.setAsset(entry.getValue());
				assetRequest.setRequestInfo(request.getRequestInfo());
			}
		
			StringBuilder url = new StringBuilder(constants.getAssetHost().concat(constants.getAssetEndPoint()));
			Object optional = restCallRepository.fetchResult(url, assetRequest);
		});
	
		
	}

	private void createRentAndLease(RentLeaseCreationRequest request) {
		producer.push(RentConstants.RENT_LEASE_CREATION, request);

	}

	private void enrichRentAndLease(RentLeaseCreationRequest request) {

		try {
			request.getRentLease().stream().forEach(booking -> {
				booking.setUuid(UUID.randomUUID().toString());
				booking.setStatus(RentConstants.RENT_LEASE_INITIATED);
				booking.setActive(true);
				if (null != request.getRequestInfo() && null != request.getRequestInfo().getUserInfo()) {
					AuditDetails audit = AuditDetails.builder()
							.createdBy(request.getRequestInfo().getUserInfo().getUuid())
							.createdDate(new Date().getTime()).build();
					booking.setAuditDetails(audit);
				}
				String[] result = booking.getTenantId().split("\\.");
				String tenantId = result[1];
				booking.setApplicationNo(
						RentConstants.RENT_LEASE_CONSTANT + "/" + tenantId + "/" + repo.getNextRentSequence());
				if (null != booking.getStartDate() && null != booking.getMonths()) {
					LocalDate startDate = Instant.ofEpochMilli(booking.getStartDate()).atZone(ZoneId.systemDefault())
							.toLocalDate();
					LocalDate endDate = startDate.plus(booking.getMonths(), ChronoUnit.MONTHS);
					Long endDatetoDB = endDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
					booking.setEndDate(endDatetoDB);
				}
			});

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private Map<String, Asset> validateAndSearchAssetData(RentLeaseCreationRequest request) {
		List<RentLease> rentLeaseListFromRequest = request.getRentLease();
		List<String> ids = new ArrayList<>();

		List<Asset> assetListFromDb = new ArrayList<>();
		if (CollectionUtils.isEmpty(rentLeaseListFromRequest)) {
			throw new RuntimeException("Rent Lease Object cannot be empty!!!");
		}
		if (!CollectionUtils.isEmpty(rentLeaseListFromRequest)) {
			ids = request.getRentLease().stream().map(rent -> rent.getAssetId()).collect(Collectors.toList());
			if (CollectionUtils.isEmpty(ids)) {
				throw new RuntimeException("Asset ids Cannot be Empty!!!");
			}
		}
		assetListFromDb = repo.searchAssetFromDB(ids);
		Map<String, Asset> siteMap = assetListFromDb.stream().collect(Collectors.toMap(Asset::getId, asset -> asset));
		request.getRentLease().stream().forEach(booking -> {
			Asset asset = siteMap.get(booking.getAssetId());
			if (null == asset) {
				throw new CustomException("ASSET_NOT_FOUND",
						"Asset not found for the given asset Id:" + booking.getAssetId());
			}
			if (!StringUtils.equalsIgnoreCase(asset.getStatus(), "Available")) {
				throw new CustomException("Asset_CANT_BE_BOOKED",
						"Asset " + asset.getAssetName() + " is not Available.");
			}
		});

		return siteMap;
	}

	public RentLeaseCreationResponse search(RentLeaseSearchRequest rentLeaseSearchRequest) {
		RentLeaseCreationResponse response = null;
		try {

			enrichSearchCriteria(rentLeaseSearchRequest);
			validateSearchCriteria(rentLeaseSearchRequest);
			List<RentLease> criteriaDataFromDB = repo.search(rentLeaseSearchRequest.getSearchCriteria());
			response = RentLeaseCreationResponse.builder()
					.responseInfo(responseInfoFactory
							.createResponseInfoFromRequestInfo(rentLeaseSearchRequest.getRequestInfo(), true))
					.rentLease(criteriaDataFromDB).build();

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}
		return response;
	}

	private void validateSearchCriteria(RentLeaseSearchRequest rentLeaseSearchRequest) {
		if (null != rentLeaseSearchRequest.getSearchCriteria()) {
			throw new CustomException("NULL_SEARCH_PARAMETER", "Provide Search Criteria");
		}
		if ((null != rentLeaseSearchRequest.getRequestInfo()
				&& null != rentLeaseSearchRequest.getRequestInfo().getUserInfo())
				&& (CollectionUtils.isEmpty(rentLeaseSearchRequest.getSearchCriteria().getApplicationNo()))) {
			throw new CustomException("INVALID_SEARCH_PARAMETER", "Pleasa provide required search Parameters");

		}

	}

	private void enrichSearchCriteria(RentLeaseSearchRequest request) {
		try {
			if (null == request.getSearchCriteria()
					&& StringUtils.equalsIgnoreCase(request.getRequestInfo().getUserInfo().getType(), "CITIZEN")) {
				request.setSearchCriteria(SearchCriteria.builder()
						.createdBy(Collections.singletonList(request.getRequestInfo().getUserInfo().getUuid()))
						.build());
			}

			else if (null != request.getSearchCriteria() && null != request.getRequestInfo()
					&& null != request.getRequestInfo().getUserInfo()
					&& StringUtils.equalsIgnoreCase(request.getRequestInfo().getUserInfo().getType(), "CITIZEN")) {
				request.getSearchCriteria()
						.setCreatedBy(Arrays.asList(request.getRequestInfo().getUserInfo().getUuid()));
			} else if (null != request.getSearchCriteria() && null != request.getRequestInfo()
					&& null != request.getRequestInfo().getUserInfo()
					&& StringUtils.equalsIgnoreCase(request.getRequestInfo().getUserInfo().getType(), "EMPLOYEE")) {
				List<String> listOfStatus = getBookingStatusListByRoles(request.getSearchCriteria().getTenantId(),
						request.getRequestInfo().getUserInfo().getRoles());
				if (CollectionUtils.isEmpty(listOfStatus)) {
					throw new CustomException("SEARCH_BOOKING_BY_ROLES",
							"Search can't be performed by this Employee due to lack of roles.");
				}
				request.getSearchCriteria().setStatus(listOfStatus);

			}

		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

	}

	private List<String> getBookingStatusListByRoles(String tenantId, List<Role> roles) {
		List<String> rolesWithinTenant = RentConstants.getRolesByTenantId(tenantId, roles);
		Set<String> statusWithRoles = new HashSet();

		rolesWithinTenant.stream().forEach(role -> {

			if (StringUtils.equalsIgnoreCase(role, RentConstants.RENT_LEASE_VERIFIER)) {
				statusWithRoles.add(RentConstants.STATUS_PENDINGFORVERIFICATION);
			} else if (StringUtils.equalsIgnoreCase(role, RentConstants.RENT_LEASE_APPROVER)) {
				statusWithRoles.add(RentConstants.STATUS_PENDINGFORAPPROVAL);
			}

		});

		return new ArrayList<>(statusWithRoles);
	}

	public RentLeaseCreationResponse update(RentLeaseCreationRequest rentLeaseCreateRequest) {
		RentLeaseCreationResponse response = null;
		
		return response;
	}

}

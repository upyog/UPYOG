package org.upyog.sv.validator;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.sv.config.StreetVendingConfiguration;
import org.upyog.sv.constants.LocalizationConstants;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.repository.StreetVendingRepository;
import org.upyog.sv.util.StreetVendingUtil;
import org.upyog.sv.web.models.StreetVendingDetail;
import org.upyog.sv.web.models.StreetVendingRequest;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class StreetVendingValidator {

	@Autowired
	private MDMSValidator mdmsValidator;

	@Autowired
	private StreetVendingConfiguration config;

	@Autowired
	private StreetVendingRepository repository;

	/**
	 * 
	 * @param bookingRequest
	 * @param mdmsData
	 */
	public void validateCreate(StreetVendingRequest bookingRequest, Object mdmsData) {
		log.info("validating master data for create street vending request for applicant mobile no : "
				+ bookingRequest.getStreetVendingDetail().getApplicationNo());

		// mdmsValidator.validateMdmsData(bookingRequest, mdmsData);
		validateDuplicateDocuments(bookingRequest);
	}

	public void validateUpdate(StreetVendingRequest bookingDetailFromRequest, StreetVendingDetail detailFromDB) {
		// log.info("validating master data for update booking request for booking no :
		// " + bookingDetailFromRequest.getBookingNo());
		// TODO: Add condition for status from to
	}

	/**
	 * 
	 * @param bookingRequest
	 */
	private void validateDuplicateDocuments(StreetVendingRequest streetVendingRequest) {
		if (streetVendingRequest.getStreetVendingDetail().getDocumentDetails() != null) {
			List<String> documentFileStoreIds = new LinkedList<String>();
			streetVendingRequest.getStreetVendingDetail().getDocumentDetails().forEach(document -> {
				if (documentFileStoreIds.contains(document.getFileStoreId()))
					throw new CustomException(StreetVendingConstants.DUPLICATE_DOCUMENT_UPLOADED,
							"Same document cannot be used multiple times");
				else
					documentFileStoreIds.add(document.getFileStoreId());
			});
		} else {
			throw new CustomException(LocalizationConstants.EMPTY_DOCUMENT_ERROR,
					"Documents are mandatory for booking.");
		}
	}

	/**
	 * Validates if the search parameters are valid
	 * 
	 * @param requestInfo The requestInfo of the incoming request
	 * @param criteria    The StreetVendingSearchCriteria Criteria
	 */
	public void validateSearch(RequestInfo requestInfo, StreetVendingSearchCriteria criteria) {
		log.info("Validating search request for criteria " + criteria);
		String userType = requestInfo.getUserInfo().getType();

		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(StreetVendingConstants.CITIZEN) && criteria.isEmpty())
			throw new CustomException(LocalizationConstants.INVALID_SEARCH,
					"Search without any paramters is not allowed");

		if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(StreetVendingConstants.CITIZEN)
				&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		if (requestInfo.getUserInfo().getType().equalsIgnoreCase(StreetVendingConstants.CITIZEN) && !criteria.isEmpty()
				&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "TenantId is mandatory in search");

		String allowedParamStr = null;

		if (!userType.equalsIgnoreCase(StreetVendingConstants.EMPLOYEE))
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
		else if (userType.equalsIgnoreCase(StreetVendingConstants.EMPLOYEE))
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
		else
			throw new CustomException(LocalizationConstants.INVALID_SEARCH,
					"The userType: " + requestInfo.getUserInfo().getType() + " does not have any search config");

		if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "No search parameters are expected");
		else {
			List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
			validateSearchParams(criteria, allowedParams);
		}
	}

	/**
	 * Validates if the paramters coming in search are allowed
	 * 
	 * @param criteria      CHB search criteria
	 * @param allowedParams Allowed Params for search
	 */
	private void validateSearchParams(StreetVendingSearchCriteria criteria, List<String> allowedParams) {
		log.info("Validating search params for allowedParams " + allowedParams);

		if (criteria.getStatus() != null && !allowedParams.contains("status"))
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "Search on Status is not allowed");

		if (criteria.getOffset() != null && !allowedParams.contains("offset"))
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "Search on offset is not allowed");

		if (criteria.getLimit() != null && !allowedParams.contains("limit"))
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "Search on limit is not allowed");

		if (criteria.getMobileNumber() != null && !allowedParams.contains("mobileNumber"))
			throw new CustomException(LocalizationConstants.INVALID_SEARCH, "Search on mobile number is not allowed");

		if (criteria.getFromDate() != null) {
			LocalDate fromDate = StreetVendingUtil.parseStringToLocalDate(criteria.getFromDate());
			if (fromDate.isAfter(LocalDate.now())) {
				throw new CustomException(StreetVendingConstants.INVALID_SEARCH, "From date cannot be a future date");
			}
		}

		if (criteria.getFromDate() != null) {
			LocalDate fromDate = StreetVendingUtil.parseStringToLocalDate(criteria.getFromDate());
			if (fromDate.isBefore(StreetVendingUtil.getMonthsAgo(6))) {
				throw new CustomException(StreetVendingConstants.INVALID_SEARCH, "From date cannot be prior 6 months");
			}
		}

		if (criteria.getToDate() != null && criteria.getFromDate() != null) {
			LocalDate fromDate = StreetVendingUtil.parseStringToLocalDate(criteria.getFromDate());
			LocalDate toDate = StreetVendingUtil.parseStringToLocalDate(criteria.getToDate());
			if (toDate.isBefore(fromDate)) {
				throw new CustomException(StreetVendingConstants.INVALID_SEARCH,
						"To date cannot be prior to from date");
			}
		}
	}

	public StreetVendingDetail validateApplicationExistence(StreetVendingDetail streetVendingDetail) {
		StreetVendingDetail streetVendingDetail2 = repository.getStreetVendingApplications(StreetVendingSearchCriteria.builder()
				.applicationNumber(streetVendingDetail.getApplicationNo()).build()).get(0);

		return streetVendingDetail2;
	}

}

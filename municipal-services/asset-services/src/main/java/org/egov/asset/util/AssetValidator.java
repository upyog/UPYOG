package org.egov.asset.util;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.web.models.AssetSearchCriteria;
import org.egov.asset.web.models.AssetRequest;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AssetValidator {

	@Autowired
	private MDMSValidator mdmsValidator;

	@Autowired
	AssetConfiguration config;

	public void validateCreate(AssetRequest assetRequest, Object mdmsData) {
		mdmsValidator.validateMdmsData(assetRequest, mdmsData);
		// validateApplicationDocuments();
	}

	private void validateApplicationDocuments() {

	}

	/**
	 * Validates if the search parameters are valid
	 * 
	 * @param requestInfo The requestInfo of the incoming request
	 * @param criteria    The BPASearch Criteria
	 */
	// TODO need to make the changes in the data
	public void validateSearch(RequestInfo requestInfo, AssetSearchCriteria criteria) {
		String allowedParamStr = null;
		if (requestInfo.getUserInfo() != null) {

			if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE) && criteria.isEmpty())
				throw new CustomException(AssetConstants.INVALID_SEARCH, "Search without any paramters is not allowed");

			if (!requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE)
					&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
				throw new CustomException(AssetConstants.INVALID_SEARCH, "TenantId is mandatory in search");

			if (requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE) && !criteria.isEmpty()
					&& !criteria.tenantIdOnly() && criteria.getTenantId() == null)
				throw new CustomException(AssetConstants.INVALID_SEARCH, "TenantId is mandatory in search");

			

			if (requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE))
				allowedParamStr = config.getAllowedEmployeeSearchParameters();
			else if (requestInfo.getUserInfo().getType().equalsIgnoreCase(AssetConstants.EMPLOYEE))
				allowedParamStr = config.getAllowedEmployeeSearchParameters();
			else
				throw new CustomException(AssetConstants.INVALID_SEARCH,
						"The userType: " + requestInfo.getUserInfo().getType() + " does not have any search config");
		} else {
			allowedParamStr = config.getAllowedEmployeeSearchParameters();
			if (StringUtils.isEmpty(allowedParamStr) && !criteria.isEmpty())
				throw new CustomException(AssetConstants.INVALID_SEARCH, "No search parameters as expected");
			else {
				List<String> allowedParams = Arrays.asList(allowedParamStr.split(","));
				validateSearchParams(criteria, allowedParams);
			}
		}
	}

	/**
	 * Validates if the paramters coming in search are allowed
	 * 
	 * @param criteria      BPA search criteria
	 * @param allowedParams Allowed Params for search
	 */
	private void validateSearchParams(AssetSearchCriteria criteria, List<String> allowedParams) {

		if (criteria.getApplicationNo() != null && !allowedParams.contains("applicationNo"))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on applicationNo is not allowed");

		if (criteria.getStatus() != null && !allowedParams.contains("status"))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on Status is not allowed");

		if (criteria.getIds() != null && !allowedParams.contains("ids"))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on ids is not allowed");

		if (criteria.getOffset() != null && !allowedParams.contains("offset"))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on offset is not allowed");

		if (criteria.getLimit() != null && !allowedParams.contains("limit"))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "Search on limit is not allowed");

		if (criteria.getApprovalDate() != null && (criteria.getApprovalDate() > new Date().getTime()))
			throw new CustomException(AssetConstants.INVALID_SEARCH,
					"Permit Order Genarated date cannot be a future date");

		if (criteria.getFromDate() != null && (criteria.getFromDate() > new Date().getTime()))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "From date cannot be a future date");

		if (criteria.getToDate() != null && criteria.getFromDate() != null
				&& (criteria.getFromDate() > criteria.getToDate()))
			throw new CustomException(AssetConstants.INVALID_SEARCH, "To date cannot be prior to from date");
	}

}

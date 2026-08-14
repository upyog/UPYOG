package org.egov.echallan.validator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.echallan.config.ChallanConfiguration;
import org.egov.echallan.model.Amount;
import org.egov.echallan.model.Challan;
import org.egov.echallan.model.Challan.StatusEnum;
import org.egov.echallan.model.ChallanRequest;
import org.egov.echallan.model.RequestInfoWrapper;
import org.egov.echallan.repository.ServiceRequestRepository;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import static org.egov.echallan.util.ChallanConstants.*;

@Component
@Slf4j
public class ChallanValidator {

	private final ChallanConfiguration config;
	private final ServiceRequestRepository serviceRequestRepository;

	public ChallanValidator(ChallanConfiguration config, ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	public void validateFields(ChallanRequest request, Object mdmsData) {
		Challan challan = request.getChallan();
		Map<String, String> errorMap = new HashMap<>();

		validateAmounts(challan, errorMap);
		validateRequiredFields(challan, errorMap);
		validateOffenceFields(challan, mdmsData, errorMap);
		validateTaxPeriod(challan, mdmsData, errorMap);

		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

	private void validateAmounts(Challan challan, Map<String, String> errorMap) {
		List<Amount> entAmount = challan.getAmount();
		int totalAmt = 0;
		if (entAmount != null && !entAmount.isEmpty()) {
			for (Amount amount : entAmount) {
				if (amount.getAmount() != null) {
					totalAmt += amount.getAmount().intValue();
					if (amount.getAmount().compareTo(BigDecimal.ZERO) < 0) {
						errorMap.put("Negative Amount", "Amount cannot be negative");
					}
				}
			}
		}
		if (totalAmt <= 0) {
			errorMap.put("Zero amount", "Challan cannot be generated for zero amount");
		}
	}

	private void validateRequiredFields(Challan challan, Map<String, String> errorMap) {
		if (challan.getCitizen().getMobileNumber() == null) {
			errorMap.put("NULL_Mobile Number", " Mobile Number cannot be null");
		}
		if (challan.getBusinessService() == null) {
			errorMap.put("NULL_BusinessService", " Business Service cannot be null");
		}
	}

	private void validateTaxPeriod(Challan challan, Object mdmsData, Map<String, String> errorMap) {
		List<Map<String, Object>> taxPeriods = JsonPath.read(mdmsData, MDMS_FINACIALYEAR_PATH);
		boolean validFinancialYear = false;
		if (challan.getTaxPeriodTo() != null && challan.getTaxPeriodFrom() != null) {
			for (Map<String, Object> financialYearProperties : taxPeriods) {
				Long startDate = (Long) financialYearProperties.get(MDMS_STARTDATE);
				Long endDate = (Long) financialYearProperties.get(MDMS_ENDDATE);
				if (challan.getTaxPeriodFrom() <= challan.getTaxPeriodTo()
						&& challan.getTaxPeriodFrom() >= startDate
						&& challan.getTaxPeriodTo() <= endDate) {
					validFinancialYear = true;
				}
			}
		}
		if (!validFinancialYear) {
			errorMap.put("Invalid TaxPeriod", "Tax period details are invalid");
		}
	}

	public List<String> getLocalityCodes(String tenantId, RequestInfo requestInfo) {
		StringBuilder builder = new StringBuilder(config.getBoundaryHost());
		builder.append(config.getFetchBoundaryEndpoint());
		builder.append("?tenantId=");
		builder.append(tenantId);
		builder.append("&hierarchyTypeCode=");
		builder.append(HIERARCHY_CODE);
		builder.append("&boundaryType=");
		builder.append(BOUNDARY_TYPE);

		Object result = serviceRequestRepository.fetchResult(builder, new RequestInfoWrapper(requestInfo));

		return JsonPath.read(result, LOCALITY_CODE_PATH);
	}

	public void validateUpdateRequest(ChallanRequest request, List<Challan> searchResult) {
		Map<String, String> errorMap = new HashMap<>();
		if (searchResult.isEmpty()) {
			errorMap.put("INVALID_UPDATE_REQ_NOT_EXIST", "The Challan to be updated is not in database");
		}
		if (errorMap.isEmpty()) {
			validateUpdateFieldsMatch(request.getChallan(), searchResult.get(0), request, errorMap);
		}
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

	private void validateUpdateFieldsMatch(Challan challan, Challan searchchallan, ChallanRequest request,
			Map<String, String> errorMap) {
		validateBusinessServiceMatch(challan, searchchallan, errorMap);
		validateChallanNoMatch(challan, searchchallan, errorMap);
		validateAddressMatch(challan, searchchallan, errorMap);
		validateCitizenMatch(challan, searchchallan, errorMap);
		validateChallanStatus(searchchallan, errorMap);
		validateTenantAccess(challan, request, errorMap);
	}

	private void validateBusinessServiceMatch(Challan challan, Challan searchchallan, Map<String, String> errorMap) {
		if (StringUtils.isNotBlank(challan.getBusinessService()) && StringUtils.isNotBlank(searchchallan.getBusinessService())
				&& !challan.getBusinessService().equalsIgnoreCase(searchchallan.getBusinessService())) {
			errorMap.put("INVALID_UPDATE_REQ_NOTMATCHED_BSERVICE", "The business service is not matching with the Search result");
		}
	}

	private void validateChallanNoMatch(Challan challan, Challan searchchallan, Map<String, String> errorMap) {
		if (StringUtils.isNotBlank(challan.getChallanNo()) && StringUtils.isNotBlank(searchchallan.getChallanNo())
				&& !challan.getChallanNo().equalsIgnoreCase(searchchallan.getChallanNo())) {
			errorMap.put("INVALID_UPDATE_REQ_NOTMATCHED_CHALLAN_NO", "The Challan Number is not matching with the Search result");
		}
	}

	private void validateAddressMatch(Challan challan, Challan searchchallan, Map<String, String> errorMap) {
		if (challan.getAddress() != null && searchchallan.getAddress() != null
				&& challan.getAddress().getId() != null && searchchallan.getAddress().getId() != null
				&& !challan.getAddress().getId().equalsIgnoreCase(searchchallan.getAddress().getId())) {
			errorMap.put("INVALID_UPDATE_REQ_NOTMATCHED_ADDRESS", "Address is not matching with the Search result");
		}
	}

	private void validateCitizenMatch(Challan challan, Challan searchchallan, Map<String, String> errorMap) {
		if (challan.getCitizen() != null && searchchallan.getCitizen() != null) {
			if (challan.getCitizen().getUuid() != null && searchchallan.getCitizen().getUuid() != null
					&& !challan.getCitizen().getUuid().equalsIgnoreCase(searchchallan.getCitizen().getUuid())) {
				errorMap.put("INVALID_UPDATE_REQ_NOTMATCHED_UUID", "User UUID not matching with the Search result");
			}
		} else if (challan.getCitizen() != null && challan.getCitizen().getUuid() != null
				&& (searchchallan.getCitizen() == null || searchchallan.getCitizen().getUuid() == null)) {
			log.warn("Citizen UUID in request but not found in search result for challan: {}", challan.getChallanNo());
		}
	}

	private void validateChallanStatus(Challan searchchallan, Map<String, String> errorMap) {
		if (searchchallan.getApplicationStatus() != StatusEnum.ACTIVE) {
			errorMap.put("INVALID_UPDATE_REQ_CHALLAN_INACTIVE", "Challan cannot be updated/cancelled");
		}
	}

	private void validateTenantAccess(Challan challan, ChallanRequest request, Map<String, String> errorMap) {
		if (StringUtils.isNotBlank(challan.getTenantId()) && request.getRequestInfo() != null
				&& request.getRequestInfo().getUserInfo() != null
				&& StringUtils.isNotBlank(request.getRequestInfo().getUserInfo().getTenantId())
				&& !challan.getTenantId().equalsIgnoreCase(request.getRequestInfo().getUserInfo().getTenantId())) {
			errorMap.put("INVALID_UPDATE_REQ_INVALID_TENANTID", "Invalid tenant id");
		}
	}

	public void validateChallanCountRequest(String tenantId) {
		Map<String, String> errorMap = new HashMap<>();
		if (StringUtils.isEmpty(tenantId)) {
			errorMap.put("INVALID_CHALLAN_COUNT_REQ", "Please provide tenant id to get count details");
		}
		if (!errorMap.isEmpty()) {
			throw new CustomException(errorMap);
		}
	}

	/**
	 * Validates offence-related fields against MDMS data
	 */
	private void validateOffenceFields(Challan challan, Object mdmsData, Map<String, String> errorMap) {
		validateOffenceTypeName(challan, mdmsData, errorMap);
		validateOffenceCategoryName(challan, mdmsData, errorMap);
		validateOffenceSubCategoryName(challan, mdmsData, errorMap);
		validateChallanAmounts(challan, errorMap);
	}

	private void validateOffenceTypeName(Challan challan, Object mdmsData, Map<String, String> errorMap) {
		if (StringUtils.isBlank(challan.getOffenceTypeName())) {
			errorMap.put("NULL_OFFENCE_TYPE", "Offence type name cannot be null");
			return;
		}
		List<Map<String, Object>> offenceTypes = JsonPath.read(mdmsData, MDMS_OFFENCE_TYPE_PATH);
		boolean validOffenceType = offenceTypes.stream()
				.anyMatch(type -> type.get("name").equals(challan.getOffenceTypeName()));
		if (!validOffenceType) {
			errorMap.put("INVALID_OFFENCE_TYPE", "Invalid offence type name provided");
		}
	}

	private void validateOffenceCategoryName(Challan challan, Object mdmsData, Map<String, String> errorMap) {
		if (StringUtils.isBlank(challan.getOffenceCategoryName())) {
			errorMap.put("NULL_OFFENCE_CATEGORY", "Offence category name cannot be null");
			return;
		}
		List<Map<String, Object>> categories = JsonPath.read(mdmsData, MDMS_OFFENCE_CATEGORY_PATH);
		boolean validCategory = categories.stream()
				.anyMatch(category -> category.get("name").equals(challan.getOffenceCategoryName()));
		if (!validCategory) {
			errorMap.put("INVALID_OFFENCE_CATEGORY", "Invalid offence category name provided");
		}
	}

	private void validateOffenceSubCategoryName(Challan challan, Object mdmsData, Map<String, String> errorMap) {
		if (StringUtils.isBlank(challan.getOffenceSubCategoryName())) {
			errorMap.put("NULL_OFFENCE_SUBCATEGORY", "Offence subcategory name cannot be null");
			return;
		}
		List<Map<String, Object>> subCategories = JsonPath.read(mdmsData, MDMS_OFFENCE_SUBCATEGORY_PATH);
		boolean validSubCategory = subCategories.stream()
				.anyMatch(subCategory -> subCategory.get("name").equals(challan.getOffenceSubCategoryName()));
		if (!validSubCategory) {
			errorMap.put("INVALID_OFFENCE_SUBCATEGORY", "Invalid offence subcategory name provided");
		}
	}

	private void validateChallanAmounts(Challan challan, Map<String, String> errorMap) {
		if (challan.getChallanAmount() != null && challan.getChallanAmount().compareTo(BigDecimal.ZERO) <= 0) {
			errorMap.put("INVALID_CHALLAN_AMOUNT", "Challan amount must be greater than zero");
		}
		if (challan.getAmount() != null && !challan.getAmount().isEmpty()) {
			for (Amount amount : challan.getAmount()) {
				if (amount.getAmount() == null || amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
					errorMap.put("INVALID_AMOUNT", "Amount must be greater than zero");
				}
				if (StringUtils.isBlank(amount.getTaxHeadCode())) {
					errorMap.put("NULL_TAX_HEAD_CODE", "Tax head code cannot be null");
				}
			}
		}
	}
}

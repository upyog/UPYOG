
package org.egov.pt.web.controllers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Document;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PropertySearchRequest;
import org.egov.pt.models.PropertySearchResponse;
import org.egov.pt.models.enums.Channel;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.oldProperty.OldPropertyCriteria;
import org.egov.pt.models.user.UserDetailResponse;
import org.egov.pt.models.user.UserSearchResponse;
import org.egov.pt.service.FuzzySearchService;
import org.egov.pt.service.MigrationService;
import org.egov.pt.service.PropertyEncryptionService;
import org.egov.pt.service.PropertyService;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.validator.PropertyValidator;
import org.egov.pt.web.contracts.CreateObPassUserRequest;
import org.egov.pt.web.contracts.GenrateArrearRequest;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.PropertyResponse;
import org.egov.pt.web.contracts.PropertyStatusUpdateRequest;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.pt.web.contracts.CancelPropertyBillRequest;
import org.egov.pt.web.contracts.TotalCountRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/property")
@Slf4j
public class PropertyController {

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	private MigrationService migrationService;

	@Autowired
	private PropertyValidator propertyValidator;

	@Autowired
	private PropertyConfiguration configs;

	@Autowired
	FuzzySearchService fuzzySearchService;

	@Autowired
	PropertyEncryptionService propertyEncryptionService;

	@PostMapping("/_create")
	public ResponseEntity<PropertyResponse> create(@Valid @RequestBody PropertyRequest propertyRequest) {

		for (OwnerInfo owner : propertyRequest.getProperty().getOwners()) {
			if (!owner.getOwnerType().equals("NONE")) {
				if (!CollectionUtils.isEmpty(owner.getDocuments())) {
					for (Document document : owner.getDocuments())
						if (document.getDocumentType().contains("OWNER.SPECIALCATEGORYPROOF")) {
							PropertyCriteria propertyCriteria = new PropertyCriteria();
							propertyCriteria.setTenantId(owner.getTenantId());
							propertyCriteria
									.setDocumentNumbers(new HashSet<>(Arrays.asList(document.getDocumentUid())));
							List<Property> properties = propertyService.searchProperty(propertyCriteria,
									propertyRequest.getRequestInfo(), null);
							if (!properties.isEmpty())
								throw new CustomException(null,
										"Document numbers added in Owner Information is already present in the system.");

						}
				}
			}
		}
		Property property = propertyService.createProperty(propertyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(Arrays.asList(property)).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/_update")
	public ResponseEntity<PropertyResponse> update(@Valid @RequestBody PropertyRequest propertyRequest) {

		for (OwnerInfo owner : propertyRequest.getProperty().getOwners()) {
			if (!owner.getOwnerType().equals("NONE")) {
				if (!CollectionUtils.isEmpty(owner.getDocuments())) {
					for (Document document : owner.getDocuments())
						if (document.getDocumentType().contains("OWNER.SPECIALCATEGORYPROOF")) {
							PropertyCriteria propertyCriteria = new PropertyCriteria();
							propertyCriteria.setTenantId(owner.getTenantId());
							propertyCriteria
									.setDocumentNumbers(new HashSet<>(Arrays.asList(document.getDocumentUid())));
							List<Property> properties = propertyService.searchProperty(propertyCriteria,
									propertyRequest.getRequestInfo(), null);
							if (!properties.isEmpty())
								throw new CustomException(null,
										"Document numbers added in Owner Information is already present in the system.");

						}
				}

			}
		}

		Property property = propertyService.updateProperty(propertyRequest, true);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(Arrays.asList(property)).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<PropertyResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria propertyCriteria) {

		// If inbox search has been disallowed at config level or if inbox search is
		// allowed but the current search is NOT, from inbox service validate the search
		// criteria.
		if (!configs.getIsInboxSearchAllowed() || !propertyCriteria.getIsInboxSearch()) {
			propertyValidator.validatePropertyCriteria(propertyCriteria, requestInfoWrapper.getRequestInfo());
		}

		List<Property> properties = new ArrayList<Property>();
		Map<Integer, PropertyCriteria> propertyCriteriaMap = new HashMap<>();
		Integer count = 0;
		Integer counter = 1;

		if (propertyCriteria.getIsRequestForCount()) {
			count = propertyService.count(requestInfoWrapper.getRequestInfo(), propertyCriteria);

		} else {
			propertyCriteriaMap.put(counter++, propertyCriteria);

			if (propertyService.isCriteriaEmpty(propertyCriteria) && null != requestInfoWrapper.getRequestInfo()
					&& null != requestInfoWrapper.getRequestInfo().getUserInfo() && requestInfoWrapper.getRequestInfo()
							.getUserInfo().getType().equalsIgnoreCase(PTConstants.USER_TYPE_EMPLOYEE)) {
				PropertyCriteria propertyCriteriaCreatedBy = propertyCriteria.copy();
				if (!CollectionUtils.isEmpty(propertyCriteriaCreatedBy.getStatusList())) {
					propertyCriteriaCreatedBy.setStatusList(null);
				}
				propertyCriteriaCreatedBy.setCreatedBy(
						Collections.singleton(requestInfoWrapper.getRequestInfo().getUserInfo().getUuid()));

				propertyCriteriaMap.put(counter++, propertyCriteriaCreatedBy);
			}

			if (propertyService.isCriteriaEmpty(propertyCriteria) && null != requestInfoWrapper.getRequestInfo()
					&& null != requestInfoWrapper.getRequestInfo().getUserInfo() && requestInfoWrapper.getRequestInfo()
							.getUserInfo().getType().equalsIgnoreCase(PTConstants.USER_TYPE_EMPLOYEE)) {

				List<String> rolesWithinTenant = propertyValidator.getRolesByTenantId(propertyCriteria.getTenantId(),
						requestInfoWrapper.getRequestInfo().getUserInfo().getRoles());

				for (String role : rolesWithinTenant) {
					if (role.equalsIgnoreCase(PTConstants.USER_ROLE_PROPERTY_VERIFIER)) {
						PropertyCriteria propertyCriteriaFromExcel = propertyCriteria.copy();
						if (!CollectionUtils.isEmpty(propertyCriteriaFromExcel.getStatusList())) {
							propertyCriteriaFromExcel.setStatusList(null);
						}
						propertyCriteriaFromExcel.setStatus(Collections.singleton(Status.INITIATED));
						propertyCriteriaFromExcel.setChannels(Collections.singleton(Channel.MIGRATION));

						propertyCriteriaMap.put(counter++, propertyCriteriaFromExcel);
					}
				}
			}

			properties = propertyService.searchProperty(propertyCriteria, requestInfoWrapper.getRequestInfo(),
					propertyCriteriaMap);
		}

		log.info("Property count after search" + properties.size());

		PropertyResponse response = PropertyResponse
				.builder().responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.properties(properties).count(properties.size()).build();

		propertyService.setAllCount(properties, response);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_migration")
	public ResponseEntity<?> propertyMigration(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute OldPropertyCriteria propertyCriteria) {
		long startTime = System.nanoTime();
		Map<String, String> resultMap = null;
		Map<String, String> errorMap = new HashMap<>();

		resultMap = migrationService.initiateProcess(requestInfoWrapper, propertyCriteria, errorMap);

		long endtime = System.nanoTime();
		long elapsetime = endtime - startTime;
		System.out.println("Elapsed time--->" + elapsetime);

		return new ResponseEntity<>(resultMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/_plainsearch", method = RequestMethod.POST)
	public ResponseEntity<PropertyResponse> plainsearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria propertyCriteria) {
		List<Property> properties = propertyService.searchPropertyPlainSearch(propertyCriteria,
				requestInfoWrapper.getRequestInfo());
		PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
//	@RequestMapping(value = "/_cancel", method = RequestMethod.POST)
//	public ResponseEntity<PropertyResponse> cancel(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
//												   @Valid @ModelAttribute PropertyCancelCriteria propertyCancelCriteria) {
//
//		List<Property> properties = propertyService.cancelProperty(propertyCancelCriteria,requestInfoWrapper.getRequestInfo());
//		PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
//				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//				.build();
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@PostMapping("/_addAlternateNumber")
	public ResponseEntity<PropertyResponse> _addAlternateNumber(@Valid @RequestBody PropertyRequest propertyRequest) {
		Property property = propertyService.addAlternateNumber(propertyRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(propertyRequest.getRequestInfo(),
				true);
		PropertyResponse response = PropertyResponse.builder().properties(Arrays.asList(property)).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/fuzzy/_search")
	public ResponseEntity<PropertyResponse> fuzzySearch(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria fuzzySearchCriteria) {

		List<Property> properties = fuzzySearchService.getProperties(requestInfoWrapper.getRequestInfo(),
				fuzzySearchCriteria);
		PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
				responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Encrypts existing property records
	 *
	 * @param requestInfoWrapper RequestInfoWrapper
	 * @param propertyCriteria   PropertyCriteria
	 * @return list of updated encrypted data
	 */
	/* To be executed only once */
	@RequestMapping(value = "/_encryptOldData", method = RequestMethod.POST)
	public ResponseEntity<PropertyResponse> encryptOldData(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute PropertyCriteria propertyCriteria) {

		throw new CustomException("EG_PT_ENC_OLD_DATA_ERROR", "The encryption of old data is disabled");
		/* Un-comment the below code to enable Privacy */

//        propertyCriteria.setIsRequestForOldDataEncryption(Boolean.TRUE);
//        List<Property> properties = propertyEncryptionService.updateOldData(propertyCriteria, requestInfoWrapper.getRequestInfo());
//        PropertyResponse response = PropertyResponse.builder().properties(properties).responseInfo(
//                        responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true))
//                .build();
//        return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping({ "/fetch", "/fetch/{value}" })
	public ResponseEntity<?> calculateFee(@RequestBody PropertySearchRequest request, @PathVariable String value) {

		PropertySearchResponse response = null;

		/*
		 * if(StringUtils.equalsIgnoreCase(value, "CALCULATEFEE")) { response =
		 * service.getApplicationDetails(siteBookingActionRequest); }
		 */ if (StringUtils.equalsIgnoreCase(value, "ACTIONS")) {
			response = propertyService.getActionsOnApplication(request);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			return new ResponseEntity("Provide parameter to be fetched in URL.", HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/_updateStatus")
	public ResponseEntity<PropertyResponse> updateStatus(@Valid @RequestBody PropertyStatusUpdateRequest request) {

		Property property = propertyService.updateStatus(request);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		PropertyResponse response = PropertyResponse.builder().properties(Arrays.asList(property)).responseInfo(resInfo)
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping(value = "/_counts")
	public ResponseEntity<Map<String, Object>> counts(@Valid @RequestBody TotalCountRequest totalCountRequest) {
//		List<Property> properties = new ArrayList<Property>();
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(totalCountRequest.getRequestInfo(),
				true);
		Map<String, Object> response = new HashMap<>();
		Map<String, Object> result = propertyService.totalCount(totalCountRequest);

		response.put("ResponseInfo", resInfo);
		response.put("Counts", result);

//		Property property = propertyService.updateStatus(request);
//		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
//		PropertyResponse response = PropertyResponse.builder().properties(Arrays.asList(property)).responseInfo(resInfo)
//				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@PostMapping("/_updateExistingOwnerDetails")
	public ResponseEntity<?> updateExistingOwnerDetails(@RequestBody RequestInfoWrapper requestInfoWrapper) {

//		Object owners = propertyService.updateExistingOwnerDetails(requestInfoWrapper);
		propertyService.updateExistingOwnerDetails(requestInfoWrapper);

		return new ResponseEntity("Owner details updated successfully", HttpStatus.OK);
//		return new ResponseEntity(owners, HttpStatus.OK);
	}

	@PostMapping("/_generatePropertyTaxBillReceipt")
	public ResponseEntity<Resource> generatePropertyTaxBillReceipt(
			@Valid @RequestBody RequestInfoWrapper requestInfoWrapper, @RequestParam String propertyId,
			@RequestParam(required = false) String billId) {

		ResponseEntity<Resource> response = propertyService.generatePropertyTaxBillReceipt(requestInfoWrapper,
				propertyId, billId);

		return response;
	}

	@PostMapping("/_checkMasters")
	public ResponseEntity<Map<String, Object>> checkMasters(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@RequestParam String UlbName) {

		Map<String, Object> result = propertyService.checkMastersStatus(requestInfoWrapper, UlbName);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);
		Map<String, Object> response = new HashMap<>();
		response.put("ResponseInfo", resInfo);
		response.put("MasterStatus", result);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_createArear")
	public ResponseEntity<Map<String, Object>> createArear(
			@Valid @RequestBody GenrateArrearRequest genrateArrearRequest) {

		String message = propertyService.generateArrear(genrateArrearRequest);
		ResponseInfo resInfo = responseInfoFactory
				.createResponseInfoFromRequestInfo(genrateArrearRequest.getRequestInfo(), true);
		Map<String, Object> response = new HashMap<>();
		response.put("ResponseInfo", resInfo);
		response.put("message", message);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping("/_checkAndCreateUser")
	public ResponseEntity<Map<String, Object>> checkAndCreateUser(
			@Valid @RequestBody CreateObPassUserRequest createUserRequest) {
		
		ResponseEntity<?> resInfo = propertyService.checkAndCreateUser(createUserRequest);
		Map<String, Object> response = new HashMap<>();
		response.put("ResponseInfo", response);
		response.put("message", "Success");

		@SuppressWarnings("unchecked")
		Map<String, Object> body = (Map<String, Object>) resInfo.getBody();
return ResponseEntity
        .status(HttpStatus.OK)
        .body(body);//		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

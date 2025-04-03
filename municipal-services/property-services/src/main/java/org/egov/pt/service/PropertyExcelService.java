package org.egov.pt.service;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.enums.Status;
import org.egov.pt.models.workflow.ProcessInstance;
import org.egov.pt.models.workflow.ValidActionResponce;
import org.egov.pt.util.ExcelUtils;
import org.egov.pt.util.PTConstants;
import org.egov.pt.util.RequestInfoUtils;
import org.egov.pt.web.contracts.PropertyRequest;
import org.egov.pt.web.contracts.PropertyStatusUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PropertyExcelService {

	@Autowired
	private ExcelUtils excelUtils;

	@Autowired
	private PropertyService propertyService;

	@Autowired
	private RequestInfoUtils requestInfoUtils;

	@Autowired
	private WorkflowService workflowService;

	public List<Property> createFromExcel(MultipartFile file) {

		List<Property> propertiesRes = new ArrayList<>();
		List<Property> propertiesCreated = new ArrayList<>();
		List<Property> propertyList = new ArrayList<>();

		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();

		propertyList = excelUtils.parseExcel(file);

		propertyList.stream().forEach(property -> {
			if (null != requestInfo && null != requestInfo.getUserInfo()) {
				requestInfo.getUserInfo().setType(PTConstants.USER_TYPE_EMPLOYEE);
			}
			PropertyRequest propertyRequest = PropertyRequest.builder().property(property).requestInfo(requestInfo)
					.build();
			propertiesCreated.add(propertyService.createProperty(propertyRequest));
		});

//		propertiesCreated.stream().forEach(property -> {
//			// Ensure user info is set if valid requestInfo is provided
//			updateUserTypeToSystemIfNecessary(requestInfo);
//
//			// Process property until status is APPROVED
//			Property finalProperty = getApprovedProperty(property, requestInfo);
//
//			// Add the processed property to the result
//			propertiesRes.add(finalProperty);
//		});

		propertiesRes.addAll(propertiesCreated);

		return propertiesRes;
	}

	private void updateUserTypeToSystemIfNecessary(RequestInfo requestInfo) {
		if (requestInfo != null && requestInfo.getUserInfo() != null) {
			requestInfo.getUserInfo().setType(PTConstants.USER_TYPE_SYSTEM);
		}
	}

	private Property getApprovedProperty(Property property, RequestInfo requestInfo) {
		Property currentProperty = property;

		while (isNotApproved(currentProperty)) {
			ValidActionResponce validActionResponse = fetchValidAction(requestInfo, currentProperty);

			if (hasValidAction(validActionResponse)) {
				currentProperty = executeValidAction(validActionResponse, requestInfo, currentProperty);
			} else {
				break;
			}
		}

		return currentProperty;
	}

	private boolean isNotApproved(Property property) {
		return !property.getStatus().equals(Status.APPROVED);
	}

	private ValidActionResponce fetchValidAction(RequestInfo requestInfo, Property property) {
		return workflowService.getValidAction(requestInfo, property.getPropertyId(), property.getTenantId());
	}

	private boolean hasValidAction(ValidActionResponce validActionResponse) {
		return validActionResponse != null && !CollectionUtils.isEmpty(validActionResponse.getNextValidAction());
	}

	private Property executeValidAction(ValidActionResponce validActionResponse, RequestInfo requestInfo,
			Property property) {
		ProcessInstance processInstance = buildProcessInstance(validActionResponse);

		PropertyStatusUpdateRequest updateRequest = createPropertyStatusUpdateRequest(requestInfo, property,
				processInstance);

		return propertyService.updateStatus(updateRequest);
	}

	private ProcessInstance buildProcessInstance(ValidActionResponce validActionResponse) {
		return ProcessInstance.builder().action(validActionResponse.getNextValidAction().get(0).getAction())
				.businessService(validActionResponse.getBusinessService())
				.moduleName(validActionResponse.getModuleName()).comment("From Migration").build();
	}

	private PropertyStatusUpdateRequest createPropertyStatusUpdateRequest(RequestInfo requestInfo, Property property,
			ProcessInstance processInstance) {
		return PropertyStatusUpdateRequest.builder().requestInfo(requestInfo).propertyId(property.getPropertyId())
				.workflow(processInstance).build();
	}

}

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

		propertiesCreated.stream().forEach(property -> {
			if (null != requestInfo && null != requestInfo.getUserInfo()) {
				requestInfo.getUserInfo().setType(PTConstants.USER_TYPE_SYSTEM);
			}
			Property finalProperty = property;

			while (!finalProperty.getStatus().equals(Status.APPROVED)) {
				ValidActionResponce validActionResponce = workflowService.getValidAction(requestInfo,
						property.getPropertyId(), property.getTenantId());

				if (null != validActionResponce && !CollectionUtils.isEmpty(validActionResponce.getNextValidAction())) {
					ProcessInstance processInstance = ProcessInstance.builder()
							.action(validActionResponce.getNextValidAction().get(0).getAction())
							.businessService(validActionResponce.getBusinessService())
							.moduleName(validActionResponce.getModuleName()).comment("From Migration").build();

					PropertyStatusUpdateRequest propertyStatusUpdateRequest = PropertyStatusUpdateRequest.builder()
							.requestInfo(requestInfo).propertyId(property.getPropertyId()).workflow(processInstance)
							.build();

					finalProperty = propertyService.updateStatus(propertyStatusUpdateRequest);
				}
			}
			propertiesRes.add(finalProperty);
		});

		return propertiesRes;
	}

}

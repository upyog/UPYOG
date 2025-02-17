package org.egov.vendor.util;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.driver.web.model.DriverRequest;
import org.egov.vendor.repository.ServiceRequestRepository;
import org.egov.vendor.service.ModuleRoleService;
import org.egov.vendor.web.model.AuditDetails;
import org.egov.vendor.web.model.VendorRequest;
import org.egov.vendor.web.model.user.ModuleRoleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

@Component
@Slf4j
public class VendorUtil {

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	@Autowired
	private VendorConfiguration vendorConfiguration;

	@Autowired
	private ObjectMapper mapper;


	public void defaultJsonPathConfig() {
		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}
		});
	}

	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getVendorModuleRequest();
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId, moduleRequest);
		return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
	}

	public Object mDMSCall(RequestInfo requestInfo, String tenantId, List<ModuleDetail> moduleDetails) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId, moduleDetails);
		return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
	}

	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(vendorConfiguration.getMdmsHost())
				.append(vendorConfiguration.getMdmsEndPoint());
	}

	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId, List<ModuleDetail> moduleDetails) {
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();
		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
	}

	public List<ModuleDetail> getVendorModuleRequest() {

		final String activeFilter = "$.[?(@.active==true)]";
		List<ModuleDetail> moduleDtls = new ArrayList<>();

		List<MasterDetail> masterDtls = new ArrayList<>();
		masterDtls.add(MasterDetail.builder().name(VendorConstants.VENDOR_AGENCY_TYPE).filter(activeFilter).build());
		masterDtls.add(
				MasterDetail.builder().name(VendorConstants.VENDOR_PAYMENT_PREFERENCE).filter(activeFilter).build());
		moduleDtls.add(
				ModuleDetail.builder().masterDetails(masterDtls).moduleName(VendorConstants.VENDOR_MODULE).build());

		return moduleDtls;

	}

	public List<ModuleDetail> getModuleRoleMappingRequest() {

		final String activeFilter = "$.[?(@.active==true)]";
		List<ModuleDetail> moduleDtls = new ArrayList<>();

		List<MasterDetail> masterDtls = new ArrayList<>();
		masterDtls.add(MasterDetail.builder().name(VendorConstants.MODULE_VENDOR_ROLE_MAPPING).filter(activeFilter).build());
		masterDtls.add(
				MasterDetail.builder().name(VendorConstants.MODULE_DRIVER_ROLE_MAPPING).filter(activeFilter).build());
		moduleDtls.add(
				ModuleDetail.builder().masterDetails(masterDtls).moduleName(VendorConstants.VENDOR_MODULE).build());

		return moduleDtls;

	}



	public AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = System.currentTimeMillis();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	/**
	 * Retrieves the module name from additional details if available; otherwise, returns the default module name.
	 *
	 * @param vendorRequest Vendor request object
	 * @return Extracted module name or default module name (FSM)
	 * @throws CustomException if the vendor request or vendor details are missing
	 */
	public String getModuleNameOrDefault(VendorRequest vendorRequest) {
		validateVendorRequest(vendorRequest);
		Object additionalDetailsObj = vendorRequest.getVendor().getAdditionalDetails();
		return extractModuleNameFromAdditionalDetails(additionalDetailsObj, ModuleNameEnum.FSM.getModuleName());
	}

	/**
	 * Retrieves the module name from additional details if available; otherwise, returns the default module name.
	 *
	 * @param driverRequest Driver request object
	 * @return Extracted module name or default module name (FSM)
	 * @throws CustomException if the driver request or driver details are missing
	 */
	public String getModuleNameOrDefault(DriverRequest driverRequest) {
		validateDriverRequest(driverRequest);
		Object additionalDetailsObj = driverRequest.getDriver().getAdditionalDetails();
		return extractModuleNameFromAdditionalDetails(additionalDetailsObj, ModuleNameEnum.FSM.getModuleName());
	}

	/**
	 * Extracts the module name from additional details or returns the default module name.
	 *
	 * @param additionalDetailsObj Additional details object
	 * @param defaultModuleName Default module name to return if module name is not found
	 * @return Extracted module name or default
	 */
	private String extractModuleNameFromAdditionalDetails(Object additionalDetailsObj, String defaultModuleName) {
		if (additionalDetailsObj == null) {
			log.info("Additional details are missing. Returning default module name: {}", defaultModuleName);
			return defaultModuleName;
		}

		try {
			HashMap<String, Object> additionalDetails = mapper.convertValue(additionalDetailsObj, HashMap.class);
			Object moduleName = additionalDetails.get(VendorConstants.ADDITIONAL_DETAILS_DESCRIPTION);

			if (moduleName == null) {
				log.info("Module name in additional details is null. Returning default module name: {}", defaultModuleName);
				return defaultModuleName;
			}

			return moduleName.toString();
		} catch (Exception e) {
			log.error("Error extracting module name from additional details. Returning default module name: {}. Error: {}",
					defaultModuleName, e.getMessage(), e);
			return defaultModuleName;
		}
	}

	/**
	 * Extracts the primary tenant ID from the vendor request.
	 *
	 * @param vendorRequest Vendor request object
	 * @return Extracted tenant ID
	 * @throws CustomException If the tenant ID is invalid
	 */
	public static String extractTenantId(VendorRequest vendorRequest) {
		validateVendorRequest(vendorRequest);
		return extractTenantId(vendorRequest.getVendor().getTenantId());
	}

	/**
	 * Extracts the primary tenant ID from the driver request.
	 *
	 * @param driverRequest Driver request object
	 * @return Extracted tenant ID
	 * @throws CustomException If the tenant ID is invalid
	 */
	public static String extractTenantId(DriverRequest driverRequest) {
		validateDriverRequest(driverRequest);
		return extractTenantId(driverRequest.getDriver().getTenantId());
	}

	/**
	 * Extracts tenant ID by splitting it and ensuring validity.
	 *
	 * @param tenantId Tenant ID string
	 * @return Extracted primary tenant ID
	 * @throws CustomException If tenant ID is invalid
	 */
	private static String extractTenantId(String tenantId) {
		String[] tenantParts = tenantId.split("\\.");
		if (tenantParts.length == 1) {
			throw new CustomException("INVALID_TENANT_ID", "Application cannot be created at the State Level.");
		}
		return tenantParts[0];
	}

	/**
	 * Validates if the vendor request and its vendor details are not null.
	 *
	 * @param vendorRequest Vendor request object
	 * @throws CustomException if vendor request or vendor details are missing
	 */
	private static void validateVendorRequest(VendorRequest vendorRequest) {
		if (vendorRequest == null || vendorRequest.getVendor() == null) {
			throw new CustomException("INVALID_VENDOR_REQUEST", "Vendor request or vendor details are missing.");
		}
	}

	/**
	 * Validates if the driver request and its driver details are not null.
	 *
	 * @param driverRequest Driver request object
	 * @throws CustomException if driver request or driver details are missing
	 */
	private static void validateDriverRequest(DriverRequest driverRequest) {
		if (driverRequest == null || driverRequest.getDriver() == null) {
			throw new CustomException("INVALID_DRIVER_REQUEST", "Driver request or driver details are missing.");
		}
	}



}

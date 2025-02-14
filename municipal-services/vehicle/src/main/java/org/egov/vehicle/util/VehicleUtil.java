package org.egov.vehicle.util;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.vehicle.config.VehicleConfiguration;
import org.egov.vehicle.repository.ServiceRequestRepository;
import org.egov.vehicle.service.ModuleRoleService;
import org.egov.vehicle.web.model.AuditDetails;
import org.egov.vehicle.web.model.VehicleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class VehicleUtil {
	@Autowired
	VehicleConfiguration config;

	@Autowired
	ServiceRequestRepository serviceRequestRepository;
	
	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private ModuleRoleService moduleRoleService;

	public void defaultJsonPathConfig() {
		Configuration.setDefaults(new Configuration.Defaults() {

			private final JsonProvider jsonProvider = new JacksonJsonProvider();
			private final MappingProvider mappingProvider = new JacksonMappingProvider();

			@Override
			public JsonProvider jsonProvider() {
				return jsonProvider;
			}

			@Override
			public MappingProvider mappingProvider() {
				return mappingProvider;
			}

			@Override
			public Set<Option> options() {
				return EnumSet.noneOf(Option.class);
			}
		});
	}

	/**
	 * Method to return auditDetails for create/update flows
	 *
	 * @param by
	 * @param isCreate
	 * @return AuditDetails
	 */
	public AuditDetails getAuditDetails(String by, Boolean isCreate, AuditDetails existingAudit) {
		Long time = System.currentTimeMillis();
		if (isCreate)
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else {
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time)
					.createdBy(existingAudit.getCreatedBy()).createdTime(existingAudit.getCreatedTime()).build();
		}

	}

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getVehicleModuleRequest();
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId, moduleRequest);
		return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
	}

	public Object mDMSCall(RequestInfo requestInfo, String tenantId, List<ModuleDetail> moduleDetails) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId, moduleDetails);
		return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
	}

	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());
	}

	/**
	 * prepares the mdms request object
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId, List<ModuleDetail> moduleDetails) {

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo).build();
	}

	public List<ModuleDetail> getVehicleModuleRequest() {

		final String filterCode = "$.[?(@.active==true)].code";
		final String activeFilter = "$.[?(@.active==true)]";
		List<ModuleDetail> moduleDtls = new ArrayList<>();

		List<MasterDetail> masterDtls = new ArrayList<>();
		masterDtls.add(MasterDetail.builder().name(Constants.VEHICLE_SUCTION_TYPE).filter(filterCode).build());
		masterDtls.add(MasterDetail.builder().name(Constants.VEHICLE_MAKE_MODEL).filter(activeFilter).build());
		masterDtls.add(MasterDetail.builder().name(Constants.VEHICLE_OWNER_TYPE).filter(activeFilter).build());
		masterDtls.add(MasterDetail.builder().name(Constants.VEHICLE_DECLINE_REASON).filter(activeFilter).build());
		moduleDtls.add(
				ModuleDetail.builder().masterDetails(masterDtls).moduleName(Constants.VEHICLE_MODULE_CODE).build());

		return moduleDtls;

	}
	
	public List<ModuleDetail> getModuleRoleMappingRequest() {

		final String activeFilter = "$.[?(@.active==true)]";
		List<ModuleDetail> moduleDtls = new ArrayList<>();

		List<MasterDetail> masterDtls = new ArrayList<>();
		masterDtls.add(
				MasterDetail.builder().name(Constants.MODULE_DRIVER_ROLE_MAPPING).filter(activeFilter).build());
		moduleDtls.add(
				ModuleDetail.builder().masterDetails(masterDtls).moduleName(Constants.VENDOR_MODULE).build());

		return moduleDtls;

	}

	
	public String getModuleNameOrDefault(VehicleRequest vehicleRequest) {
		validateVehicleRequest(vehicleRequest);
		Object additionalDetailsObj = vehicleRequest.getVehicle().getAdditionalDetails();
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
			Object moduleName = additionalDetails.get(Constants.ADDITIONAL_DETAILS_DESCRIPTION);

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

	
	
	public static String extractTenantId(VehicleRequest vehicleRequest) {
		validateVehicleRequest(vehicleRequest);
		return extractTenantId(vehicleRequest.getVehicle().getTenantId());
	}

	
	/**
	 * Extracts tenant ID by splitting it and ensuring validity.
	 *
	 * @param tenantId Tenant ID string
	 * @return Extracted primary tenant ID
	 * @throws CustomException If tenant ID is invalid
	 */
	public static String extractTenantId(String tenantId) {
		String[] tenantParts = tenantId.split("\\.");
		if (tenantParts.length == 1) {
			throw new CustomException("INVALID_TENANT_ID", "Application cannot be created at the State Level.");
		}
		return tenantParts[0];
	}

	
	/**
	 * Validates if the vehicle request and its vehicle details are not null.
	 *
	 * @param vehicleRequest vehicle request object
	 * @throws CustomException if vehicle request or driver details are missing
	 */
	private static void validateVehicleRequest(VehicleRequest vehicleRequest) {
		if (vehicleRequest == null || vehicleRequest.getVehicle() == null) {
			throw new CustomException("INVALID_VEHICLE_REQUEST", "vehicle request or driver details are missing.");
		}
	}

}

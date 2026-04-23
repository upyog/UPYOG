package com.cdac.esign.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.cdac.esign.repository.ServiceRequestRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class MDMSService {

	private ServiceRequestRepository serviceRequestRepository;
	
	private Environment env;
	
	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		return serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
	}
	
	/**
	 * prepares the mdms request object
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getModuleRequest();

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleRequest).tenantId(tenantId).build();

		return MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
	}
	
	/**
	 * Creates request to search ApplicationType and etc from MDMS
	 * 
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @param tenantId
	 *            The tenantId of the BPA
	 * @return request to search ApplicationType and etc from MDMS
	 */
	public List<ModuleDetail> getModuleRequest() {		
		
		// master details for common-masters module
		ModuleDetail designationModuleDetails = ModuleDetail.builder()
				.masterDetails(Collections.singletonList(MasterDetail.builder()
						.name("Designation")
						.build()))
				.moduleName("common-masters").build();
		
		//Tenant module for Ulb type 
		List<MasterDetail> tenantMasterDetails = new ArrayList<>();
		tenantMasterDetails
				.add(MasterDetail.builder().name("tenants").build());
		ModuleDetail tenantMDtl = ModuleDetail.builder().masterDetails(tenantMasterDetails)
				.moduleName("tenant").build();

		return Arrays.asList(designationModuleDetails, tenantMDtl);

	}
	
	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(env.getProperty("egov.mdms.host")).append(env.getProperty("egov.mdms.search.endpoint"));
	}
}

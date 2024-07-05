package org.upyog.chb.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.chb.config.CommunityHallBookingConfiguration;
import org.upyog.chb.constants.CommunityHallBookingConstants;
import org.upyog.chb.repository.ServiceRequestRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MdmsUtil {

	@Autowired
	private CommunityHallBookingConfiguration config;

	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	/*
	 * @Autowired private MDMSClient mdmsClient;
	 */

	@Autowired
	public MdmsUtil(CommunityHallBookingConfiguration config, ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
	}

	/**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		log.info("Master data fetched from MDMSfrom fiegn client : " + result);
		//Object result = mdmsClient.getMDMSData(mdmsCriteriaReq);
	//	log.info("Master data fetched from MDMSfrom fiegn client : " + result);
		return result;
	}

	/**
	 * Returns the URL for MDMS search end point
	 *
	 * @return URL for MDMS search end point
	 */
	public StringBuilder getMdmsSearchUrl() {
		return new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsPath());
	}

	/**
	 * prepares the mdms request object
	 * 
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getCHBModuleRequest();

		log.info("Module details data needs to be fetched from MDMS : " + moduleRequest);

		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.addAll(moduleRequest);

		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId).build();

		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria).requestInfo(requestInfo)
				.build();
		return mdmsCriteriaReq;
	}

	/**
	 * Creates request to search ApplicationType and etc from MDMS
	 * 
	 * @param requestInfo The requestInfo of the request
	 * @param tenantId    The tenantId of the CHB
	 * @return request to search ApplicationType and etc from MDMS
	 */
	public List<ModuleDetail> getCHBModuleRequest() {

		// master details for CHB module
		List<MasterDetail> chbMasterDtls = new ArrayList<>();

		// filter to only get code field from master data
		final String filterCode = "$.[?(@.active==true)].code";

		chbMasterDtls
				.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_PURPOSE).filter(filterCode).build());

		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_RESIDENT_TYPE)
				.filter(filterCode).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_SPECIAL_CATEGORY)
				.filter(filterCode).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_CALCULATION_TYPE).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_COMMNUITY_HALLS).filter(filterCode).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_HALL_CODES).filter(filterCode).build());
		chbMasterDtls.add(MasterDetail.builder().name(CommunityHallBookingConstants.CHB_DOCUMENTS).filter(filterCode).build());

		ModuleDetail moduleDetail = ModuleDetail.builder().masterDetails(chbMasterDtls)
				.moduleName(config.getModuleName()).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(CommunityHallBookingConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(moduleDetail, commonMasterMDtl);

	}
}
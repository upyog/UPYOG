package org.egov.asset.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.egov.asset.config.AssetConfiguration;
import org.egov.asset.repository.ServiceRequestRepository;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MdmsUtil {
	
	
	private AssetConfiguration config;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private RestTemplate restTemplate;
   

	@Autowired
	public MdmsUtil(AssetConfiguration config, ServiceRequestRepository serviceRequestRepository) {
		this.config = config;
		this.serviceRequestRepository = serviceRequestRepository;
	}

//    @Value("${egov.mdms.master.name}")
//    private String masterName;
//
//    @Value("${egov.mdms.module.name}")
//    private String moduleName;


//    public Integer fetchRegistrationChargesFromMdms(RequestInfo requestInfo, String tenantId) {
//        StringBuilder uri = new StringBuilder();
//        uri.append(mdmsHost).append(mdmsUrl);
//        MdmsCriteriaReq mdmsCriteriaReq = getMdmsRequestForCategoryList(requestInfo, tenantId);
//        Object response = new HashMap<>();
//        Integer rate = 0;
//        try {
//            response = restTemplate.postForObject(uri.toString(), mdmsCriteriaReq, Map.class);
//            rate = JsonPath.read(response, "$.MdmsRes.VTR.RegistrationCharges.[0].amount");
//        }catch(Exception e) {
//            log.error("Exception occurred while fetching category lists from mdms: ",e);
//        }
//        //log.info(ulbToCategoryListMap.toString());
//        return rate;
//    }
	
//    private MdmsCriteriaReq getMdmsRequestForCategoryList(RequestInfo requestInfo, String tenantId) {
//        MasterDetail masterDetail = new MasterDetail();
//        masterDetail.setName(masterName);
//        List<MasterDetail> masterDetailList = new ArrayList<>();
//        masterDetailList.add(masterDetail);
//
//        ModuleDetail moduleDetail = new ModuleDetail();
//        moduleDetail.setMasterDetails(masterDetailList);
//        moduleDetail.setModuleName(moduleName);
//        List<ModuleDetail> moduleDetailList = new ArrayList<>();
//        moduleDetailList.add(moduleDetail);
//
//        MdmsCriteria mdmsCriteria = new MdmsCriteria();
//        mdmsCriteria.setTenantId(tenantId.split("\\.")[0]);
//        mdmsCriteria.setModuleDetails(moduleDetailList);
//
//        MdmsCriteriaReq mdmsCriteriaReq = new MdmsCriteriaReq();
//        mdmsCriteriaReq.setMdmsCriteria(mdmsCriteria);
//        mdmsCriteriaReq.setRequestInfo(requestInfo);
//
//        return mdmsCriteriaReq;
//    }
    
    
    /**
	 * makes mdms call with the given criteria and reutrn mdms data
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public Object mDMSCall(RequestInfo requestInfo, String tenantId) {
		MdmsCriteriaReq mdmsCriteriaReq = getMDMSRequest(requestInfo, tenantId);
		Object result = serviceRequestRepository.fetchResult(getMdmsSearchUrl(), mdmsCriteriaReq);
		return result;
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
	 * @param requestInfo
	 * @param tenantId
	 * @return
	 */
	public MdmsCriteriaReq getMDMSRequest(RequestInfo requestInfo, String tenantId) {
		List<ModuleDetail> moduleRequest = getBPAModuleRequest();

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
	 * @param requestInfo
	 *            The requestInfo of the request
	 * @param tenantId
	 *            The tenantId of the BPA
	 * @return request to search ApplicationType and etc from MDMS
	 */
	public List<ModuleDetail> getBPAModuleRequest() {

		// master details for BPA module
		List<MasterDetail> assetMasterDtls = new ArrayList<>();

		// filter to only get code field from master data
		final String filterCode = "$.[?(@.active==true)].code";

		//assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CLASSIFICATION_MAPPING).filter(filterCode).build());
		assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CLASSIFICATION).filter(filterCode).build());
		assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_PARENT_CATEGORY).filter(filterCode).build());
		assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_CATEGORY).filter(filterCode).build());
		assetMasterDtls.add(MasterDetail.builder().name(AssetConstants.ASSET_SUB_CATEGORY).filter(filterCode).build());

		ModuleDetail bpaModuleDtls = ModuleDetail.builder().masterDetails(assetMasterDtls)
				.moduleName(AssetConstants.ASSET_MODULE).build();

		// master details for common-masters module
		List<MasterDetail> commonMasterDetails = new ArrayList<>();
		ModuleDetail commonMasterMDtl = ModuleDetail.builder().masterDetails(commonMasterDetails)
				.moduleName(AssetConstants.COMMON_MASTERS_MODULE).build();

		return Arrays.asList(bpaModuleDtls, commonMasterMDtl);

	}

}
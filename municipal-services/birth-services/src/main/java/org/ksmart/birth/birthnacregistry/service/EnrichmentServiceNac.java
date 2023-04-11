package org.ksmart.birth.birthnacregistry.service;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.ksmart.birth.birthnacregistry.model.NacCertRequest;
import org.ksmart.birth.birthnacregistry.model.NacCertificate;
import org.ksmart.birth.birthregistry.model.BirthCertRequest;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.common.Idgen.IdResponse;
import org.ksmart.birth.common.model.Amount;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.common.repository.ServiceRequestRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.CommonUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MasterDetail;
import org.egov.mdms.model.MdmsCriteria;
import org.egov.mdms.model.MdmsCriteriaReq;
import org.egov.mdms.model.ModuleDetail;
import org.ksmart.birth.utils.BirthDeathConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jayway.jsonpath.JsonPath;

@Service
public class EnrichmentServiceNac {

	@Autowired
    CommonUtils commUtils;
	
	@Autowired
    IdGenRepository idGenRepository;
	
	@Autowired
    BirthConfiguration config;
	
	@Autowired
    ServiceRequestRepository serviceRequestRepository;
	
    public void enrichCreateRequest(NacCertRequest nacCertRequest) {
        RequestInfo requestInfo = nacCertRequest.getRequestInfo();
        String uuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = commUtils.buildAuditDetails(uuid, true);
        NacCertificate nacCert = nacCertRequest.getNacCertificate();
        nacCert.setAuditDetails(auditDetails);
        nacCert.setId(UUID.randomUUID().toString());
    }
    public void setGLCode(NacCertRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		NacCertificate nacCert = request.getNacCertificate();
		String tenantId = nacCert.getTenantId();
		ModuleDetail glCodeRequest = getGLCodeRequest(); 
		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.add(glCodeRequest);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
				.build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
				.requestInfo(requestInfo).build();

		StringBuilder url = new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());

		Object result = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
		String jsonPath = BirthDeathConstants.GL_CODE_JSONPATH_CODE.replace("{}",nacCert.getBusinessService());
		List<Map<String,Object>> jsonOutput =  JsonPath.read(result, jsonPath);
		if(!jsonOutput.isEmpty()) {
			Map<String,Object> glCodeObj = jsonOutput.get(0);
			nacCert.setAdditionalDetail(glCodeObj);
		}
	}

	private ModuleDetail getGLCodeRequest() {
		List<MasterDetail> masterDetails = new ArrayList<>();
		masterDetails.add(MasterDetail.builder().name(BirthDeathConstants.GL_CODE_MASTER).build());
		return ModuleDetail.builder().masterDetails(masterDetails)
				.moduleName(BirthDeathConstants.BILLING_SERVICE).build();
	}

	public void setDemandParams(NacCertRequest nachCertRequest) {
		NacCertificate nacCert = nachCertRequest.getNacCertificate();
		nacCert.setBusinessService(BirthDeathConstants.BIRTH_CERT);
		ArrayList<Amount> amounts = new ArrayList<>();
		Amount amount=new Amount();
		amount.setTaxHeadCode(BirthDeathConstants.BIRTH_CERT_FEE);
		amount.setAmount(new BigDecimal(50));
		amounts.add(amount);
		 
//		nacCert.setCitizen(nachCertRequest.getRequestInfo().getUserInfo());
 
	}

}


package org.ksmart.birth.birthregistry.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
public class EnrichmentService {

	@Autowired
    CommonUtils commUtils;
	
	@Autowired
    IdGenRepository idGenRepository;
	
	@Autowired
    BirthConfiguration config;
	
	@Autowired
    ServiceRequestRepository serviceRequestRepository;
	
    public void enrichCreateRequest(BirthCertRequest birthCertRequest) {
        RequestInfo requestInfo = birthCertRequest.getRequestInfo();
        String uuid = requestInfo.getUserInfo().getUuid();
        AuditDetails auditDetails = commUtils.buildAuditDetails(uuid, true);
        BirthCertificate birthCert = birthCertRequest.getBirthCertificate();
        birthCert.setAuditDetails(auditDetails);
        birthCert.setId(UUID.randomUUID().toString());
    }

//    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
//                                   String idformat, int count) {
//        List<IdResponse> idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();
//
//        if (CollectionUtils.isEmpty(idResponses))
//            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");
//
//        return idResponses.stream()
//                .map(IdResponse::getId).collect(Collectors.toList());
//    }

//    public void setIdgenIds(BirthCertRequest request) {
//        RequestInfo requestInfo = request.getRequestInfo();
//        String tenantId = request.getBirthCertificate().getTenantId();
//        BirthCertificate birthCert = request.getBirthCertificate();
//        String applNo = getIdList(requestInfo, tenantId, config.getBirthApplNumberIdgenName(), config.getBirthApplNumberIdgenFormat(), 1).get(0);
//        birthCert.setBirthCertificateNo(applNo);
//    }

	public void setGLCode(BirthCertRequest request) {
		RequestInfo requestInfo = request.getRequestInfo();
		BirthCertificate birthCert = request.getBirthCertificate();
		String tenantId = birthCert.getTenantId();
		ModuleDetail glCodeRequest = getGLCodeRequest(); 
		List<ModuleDetail> moduleDetails = new LinkedList<>();
		moduleDetails.add(glCodeRequest);
		MdmsCriteria mdmsCriteria = MdmsCriteria.builder().moduleDetails(moduleDetails).tenantId(tenantId)
				.build();
		MdmsCriteriaReq mdmsCriteriaReq = MdmsCriteriaReq.builder().mdmsCriteria(mdmsCriteria)
				.requestInfo(requestInfo).build();

		StringBuilder url = new StringBuilder().append(config.getMdmsHost()).append(config.getMdmsEndPoint());

		Object result = serviceRequestRepository.fetchResult(url, mdmsCriteriaReq);
		String jsonPath = BirthDeathConstants.GL_CODE_JSONPATH_CODE.replace("{}",birthCert.getBusinessService());
		List<Map<String,Object>> jsonOutput =  JsonPath.read(result, jsonPath);
		if(!jsonOutput.isEmpty()) {
			Map<String,Object> glCodeObj = jsonOutput.get(0);
			birthCert.setAdditionalDetail(glCodeObj);
		}
	}

	private ModuleDetail getGLCodeRequest() {
		List<MasterDetail> masterDetails = new ArrayList<>();
		masterDetails.add(MasterDetail.builder().name(BirthDeathConstants.GL_CODE_MASTER).build());
		return ModuleDetail.builder().masterDetails(masterDetails)
				.moduleName(BirthDeathConstants.BILLING_SERVICE).build();
	}

	public void setDemandParams(BirthCertRequest birthCertRequest) {
		BirthCertificate birthCert = birthCertRequest.getBirthCertificate();
		birthCert.setBusinessService(BirthDeathConstants.BIRTH_CERT);
		ArrayList<Amount> amounts = new ArrayList<>();
		Amount amount=new Amount();
		amount.setTaxHeadCode(BirthDeathConstants.BIRTH_CERT_FEE);
		amount.setAmount(new BigDecimal(50));
		amounts.add(amount);
		//birthCert.setAmount(amounts);
		birthCert.setCitizen(birthCertRequest.getRequestInfo().getUserInfo());
//		birthCert.setTaxPeriodFrom(System.currentTimeMillis());
//		birthCert.setTaxPeriodTo(System.currentTimeMillis()+86400000);
	}

}

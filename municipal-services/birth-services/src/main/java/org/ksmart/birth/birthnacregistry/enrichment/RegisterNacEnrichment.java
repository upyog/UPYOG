package org.ksmart.birth.birthnacregistry.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.egov.tracer.model.CustomException;
 
import org.ksmart.birth.birthnacregistry.model.NacCertificate;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import static org.ksmart.birth.utils.BirthConstants.CERTIFICATE_NO;
import static org.ksmart.birth.utils.BirthConstants.REGISTRATION_NO;
import static org.ksmart.birth.utils.BirthConstants.NACMODULE;
import static org.ksmart.birth.utils.BirthConstants.REG_STATUS_ACTIVE;

@Component
public class RegisterNacEnrichment implements BaseEnrichment  {
	  @Autowired
	    IdGenRepository idGenRepository;
	    @Autowired
	    BirthConfiguration config;
	    
	    public void enrichCreate(RegisterNacRequest request) {

	        RequestInfo requestInfo = request.getRequestInfo();
	        User userInfo = requestInfo.getUserInfo();
	        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

	        request.getRegisternacDetails().forEach(register -> {

	            register.setId(UUID.randomUUID().toString());
	            register.setRegistrationDate(System.currentTimeMillis());
	            register.setRegistrationStatus(REG_STATUS_ACTIVE);
	            register.setAuditDetails(auditDetails);
	            setRegistrationNumber(request);
	           
	        });
	    }
	    
	    public void enrichUpdate(RegisterNacRequest request) {

	        RequestInfo requestInfo = request.getRequestInfo();
	        User userInfo = requestInfo.getUserInfo();

	        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

	        request.getRegisternacDetails()
	                .forEach(personal -> personal.setAuditDetails(auditDetails));
	    }
	    private void setRegistrationNumber(RegisterNacRequest request) {
	        RequestInfo requestInfo = request.getRequestInfo();
	        List<RegisterNac> birthDetails = request.getRegisternacDetails();
	        String tenantId = birthDetails.get(0)
	                .getTenantid();

	        List<String> codes = getIds(requestInfo,
	                tenantId,
	                config.getBirthRegisNumberName(),
	                NACMODULE, REGISTRATION_NO, birthDetails.size());
	        validateFileCodes(codes, birthDetails.size());
	        ListIterator<String> itr = codes.listIterator();
	        request.getRegisternacDetails()
	                .forEach(birth -> {
	                    birth.setRegistrationno(itr.next());
	                });
	    }
	    public void setCertificateNumber(NacCertificate nacCertificate, RequestInfo requestInfo) {
	        String tenantId = nacCertificate.getTenantId();
	        String certCode = getId(requestInfo,
	                tenantId,
	                config.getBirthRegisNumberName(),
	                NACMODULE,
	                CERTIFICATE_NO,
	                1);
	        //validateFileCodes(certCode, 1);
	        Long currentTime = Long.valueOf(System.currentTimeMillis());
	      //  ListIterator<String> itr = filecodes.listIterator();
	        nacCertificate.setNacCertificateNo(certCode);
	    }
	    private String getId(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
	        return idGenRepository.getId(requestInfo, tenantId, idName, moduleCode, fnType, count);
	    }
	    private List<String> getIds(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
	        return idGenRepository.getIdList(requestInfo, tenantId, idName, moduleCode, fnType, count);
	    }
	    private void validateFileCodes(List<String> fileCodes, int count) {
	        if (CollectionUtils.isEmpty(fileCodes)) {
	            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(), "No file code(s) returned from idgen service");
	        }

	        if (fileCodes.size() != count) {
	            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(),
	                    "The number of file code(s) returned by idgen service is not equal to the request count");
	        }
	    }
}

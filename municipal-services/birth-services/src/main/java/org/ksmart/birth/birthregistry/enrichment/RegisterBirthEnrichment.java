package org.ksmart.birth.birthregistry.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.kafka.common.protocol.types.Field;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import static org.ksmart.birth.utils.BirthConstants.*;

@Component
public class RegisterBirthEnrichment implements BaseEnrichment {
    @Autowired
    IdGenRepository idGenRepository;
    @Autowired
    BirthConfiguration config;
    public void enrichCreate(RegisterBirthDetailsRequest request) {
        System.out.println(request.getRegisterBirthDetails().get(0).getRegistrationNo());
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getRegisterBirthDetails().forEach(register -> {

            register.setId(UUID.randomUUID().toString());
            register.setRegistrationDate(System.currentTimeMillis());
            register.setRegistrationStatus(REG_STATUS_ACTIVE);
            register.setAuditDetails(auditDetails);
            setRegistrationNumber(request);

            register.getRegisterBirthPlace().setId(UUID.randomUUID().toString());

            register.getRegisterBirthFather().setId(UUID.randomUUID().toString());

            register.getRegisterBirthMother().setId(UUID.randomUUID().toString());

            register.getRegisterBirthPermanent().setId(UUID.randomUUID().toString());

            register.getRegisterBirthPresent().setId(UUID.randomUUID().toString());

            register.getRegisterBirthStatitical().setId(UUID.randomUUID().toString());
        });
    }

    private void setRegistrationNumber(RegisterBirthDetailsRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<RegisterBirthDetail> birthDetails = request.getRegisterBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> codes = getIds(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                request.getRegisterBirthDetails().get(0).getApplicationType(), REGISTRATION_NO, birthDetails.size());
        validateFileCodes(codes, birthDetails.size());
        ListIterator<String> itr = codes.listIterator();
        request.getRegisterBirthDetails()
                .forEach(birth -> {
                    birth.setRegistrationNo(itr.next());
                });
    }
    public void enrichUpdate(RegisterBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getRegisterBirthDetails()
                .forEach(personal -> personal.setAuditDetails(auditDetails));
    }

    public void setCertificateNumber(BirthCertificate birthCertificate, RequestInfo requestInfo) {
        String tenantId = birthCertificate.getTenantId();
        String certCode = getId(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                birthCertificate.getApplicationType(),
                CERTIFICATE_NO,
                1);
        //validateFileCodes(certCode, 1);
        Long currentTime = Long.valueOf(System.currentTimeMillis());
      //  ListIterator<String> itr = filecodes.listIterator();
        birthCertificate.setBirthCertificateNo(certCode);
    }

    private List<String> getIds(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
        return idGenRepository.getIdList(requestInfo, tenantId, idName, moduleCode, fnType, count);
    }

    private String getId(RequestInfo requestInfo, String tenantId, String idName, String moduleCode, String  fnType, int count) {
        return idGenRepository.getId(requestInfo, tenantId, idName, moduleCode, fnType, count);
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

package org.ksmart.birth.birthregistry.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.tracer.model.CustomException;
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
@Component
public class RegisterBirthEnrichment implements BaseEnrichment {
    @Autowired
    IdGenRepository idGenRepository;
    @Autowired
    BirthConfiguration config;
    public void enrichCreate(RegisterBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getRegisterBirthDetails().forEach(register -> {

            register.setId(UUID.randomUUID().toString());
            register.setRegistrationDate(System.currentTimeMillis());
            register.setAuditDetails(auditDetails);

            register.getRegisterBirthPlace().setId(UUID.randomUUID().toString());

            register.getRegisterBirthFather().setId(UUID.randomUUID().toString());

            register.getRegisterBirthMother().setId(UUID.randomUUID().toString());

            register.getRegisterBirthPermanent().setId(UUID.randomUUID().toString());

            register.getRegisterBirthPresent().setId(UUID.randomUUID().toString());

            register.getRegisterBirthStatitical().setId(UUID.randomUUID().toString());
        });
    }

    public void enrichUpdate(RegisterBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getRegisterBirthDetails()
                .forEach(personal -> personal.setAuditDetails(auditDetails));
    }

    private void setCertificateNumber(NewBirthDetailRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<NewBirthApplication> birthDetails = request.getNewBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                request.getNewBirthDetails().get(0).getApplicationType(),
                "REG",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getNewBirthDetails()
                .forEach(birth -> {
                    if((birth.getApplicationStatus() == "APPROVED" && birth.getAction() == "APPROVE")) {
                        birth.setRegistrationNo(itr.next());
                        birth.setRegistrationDate(currentTime);
                    }
                });
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

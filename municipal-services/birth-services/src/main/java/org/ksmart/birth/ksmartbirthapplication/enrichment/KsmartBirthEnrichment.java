package org.ksmart.birth.ksmartbirthapplication.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthapplication.enrichment.BaseEnrichment;
import org.ksmart.birth.birthapplication.model.BirthApplicationDetail;
import org.ksmart.birth.birthapplication.model.birth.BirthDetailsRequest;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthAppliactionDetail;
import org.ksmart.birth.ksmartbirthapplication.model.newbirth.KsmartBirthDetailsRequest;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Component
public class KsmartBirthEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;

    @Autowired
    IdGenRepository idGenRepository;

    public void enrichCreate(KsmartBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        request.getKsmartBirthDetails().forEach(birth -> {

            birth.setId(UUID.randomUUID().toString());

            birth.setAuditDetails(auditDetails);

//            birth.getBirthPlace().setId(UUID.randomUUID().toString());
//
//            birth.getBirthFatherInfo().setId(UUID.randomUUID().toString());
//
//            birth.getBirthMotherInfo().setId(UUID.randomUUID().toString());
//
//            birth.getBirthPermanentAddress().setId(UUID.randomUUID().toString());
//
//            birth.getBirthPresentAddress().setId(UUID.randomUUID().toString());
//
//            birth.getBirthStatisticalInformation().setId(UUID.randomUUID().toString());
        });
        setApplicationNumbers(request);
        setFileNumbers(request);
        setRegistrationNumber(request);
    }

    public void enrichUpdate(KsmartBirthDetailsRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getKsmartBirthDetails()
                .forEach(birth -> birth.setAuditDetails(auditDetails));
    }

    private void setApplicationNumbers(KsmartBirthDetailsRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<KsmartBirthAppliactionDetail> birthDetails = request.getKsmartBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthApplNumberIdName(),
                config.getBirthApplNumberIdFormat(),
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());

                });
    }

    private void setFileNumbers(KsmartBirthDetailsRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<KsmartBirthAppliactionDetail> birthDetails = request.getKsmartBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                config.getBirthFileNumberFormat(),
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
                });
    }

    private void setRegistrationNumber(KsmartBirthDetailsRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<KsmartBirthAppliactionDetail> birthDetails = request.getKsmartBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthRegisNumberName(),
                config.getBirthRegisNumberFormat(),
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getKsmartBirthDetails()
                .forEach(birth -> {
                    birth.setRegistrationNo(itr.next());
                    birth.setRegistrationDate(currentTime);
                });
    }

    private List<String> getIds(RequestInfo requestInfo, String tenantId, String idKey, String idformat,
                                int count) {
        return idGenRepository.getIdList(requestInfo, tenantId, idKey, idformat, count);
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

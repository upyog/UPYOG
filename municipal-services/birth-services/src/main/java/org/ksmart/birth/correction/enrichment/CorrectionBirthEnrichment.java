package org.ksmart.birth.correction.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.service.RegisterBirthService;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.correction.service.MdmsForCorrectionBirthService;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.correction.CorrectionApplication;
import org.ksmart.birth.web.model.correction.CorrectionRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Component
public class CorrectionBirthEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
   MdmsUtil mdmsUtil;
    @Autowired
    MdmsForCorrectionBirthService mdmsBirthService;
    @Autowired
    MdmsTenantService mdmsTenantService;
    @Autowired
    IdGenRepository idGenRepository;

    @Autowired
    DetailCorrectionEnrichment detailEnrichment;

    @Autowired
    RegisterBirthService registerBirthService;
    public void enrichCreate(CorrectionRequest request) {
       // String tenantId = null;
        String registrationNo = null;
        Date date = new Date();
        long doreport = date.getTime();
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        for (CorrectionApplication correctionApplication : request.getCorrectionDetails()) {
            correctionApplication.setId(UUID.randomUUID().toString());
            correctionApplication.setAuditDetails(auditDetails);
            registrationNo = correctionApplication.getRegistrationNo();
            correctionApplication.setDateOfReport(doreport);
        }
        RegisterBirthSearchCriteria criteria =  new RegisterBirthSearchCriteria();
        criteria.setRegistrationNo(registrationNo);
        List<RegisterBirthDetail> registerBirthDetails = registerBirthService.searchRegisterBirthDetails(criteria);
        setApplicationNumbers(request);
        setFileNumbers(request);
        request.getCorrectionDetails()
                .forEach(birth -> {
                   if(registerBirthDetails.size() >0) {
                       birth.setDateOfBirth(registerBirthDetails.get(0).getDateOfBirth());
                       birth.setAadharNo(registerBirthDetails.get(0).getAadharNo());
                       birth.setGender(registerBirthDetails.get(0).getGender());
                       birth.setFirstNameEn(registerBirthDetails.get(0).getFirstNameEn());
                       birth.setFirstNameMl(registerBirthDetails.get(0).getFirstNameMl());
                       birth.setMiddleNameEn(registerBirthDetails.get(0).getMiddleNameEn());
                       birth.setMiddleNameMl(registerBirthDetails.get(0).getMiddleNameMl());
                       birth.setLastNameEn(registerBirthDetails.get(0).getLastNameEn());
                       birth.setLastNameMl(registerBirthDetails.get(0).getLastNameMl());
                       birth.setRegistrationNo(registerBirthDetails.get(0).getRegistrationNo());
                       birth.setRegistrationDate(registerBirthDetails.get(0).getRegistrationDate());

                       //Father Details
                       birth.setFatherUuid(UUID.randomUUID().toString());
                       birth.setFatherFirstNameEn(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameEn());
                       birth.setFatherFirstNameMl(registerBirthDetails.get(0).getRegisterBirthFather().getFirstNameMl());
                       birth.setFatherAadhar(registerBirthDetails.get(0).getRegisterBirthFather().getAadharNo());

                       //Mother Details
                       birth.setMotherUuid(UUID.randomUUID().toString());
                       birth.setMotherfirstNameEn(registerBirthDetails.get(0).getRegisterBirthMother().getFirstNameEn());
                       birth.setMotherfirstNameMl(registerBirthDetails.get(0).getRegisterBirthMother().getLastNameMl());
                       birth.setMotherAadhar(registerBirthDetails.get(0).getRegisterBirthMother().getAadharNo());

                       //Birthplace
                       //Present Address

                       //Permanent Address
                       if (birth.getCorrectionAddress() != null) {
                           birth.getCorrectionAddress().setPermanentUuid(UUID.randomUUID().toString());
                           birth.getCorrectionAddress().setPermanentHouseNameEn(registerBirthDetails.get(0).getRegisterBirthPermanent().getHouseNameEn());
                           birth.getCorrectionAddress().setPermanentHouseNameMl(registerBirthDetails.get(0).getRegisterBirthPermanent().getHouseNameMl());
                           birth.getCorrectionAddress().setPermanentLocalityNameEn(registerBirthDetails.get(0).getRegisterBirthPermanent().getLocalityEn());
                           birth.getCorrectionAddress().setPermanentLocalityNameMl(registerBirthDetails.get(0).getRegisterBirthPermanent().getLocalityMl());
                           birth.getCorrectionAddress().setPermanentStreetNameEn(registerBirthDetails.get(0).getRegisterBirthPermanent().getStreetNameEn());
                           birth.getCorrectionAddress().setPermanentStreetNameMl(registerBirthDetails.get(0).getRegisterBirthPermanent().getStreetNameMl());
                       }
                   }

                });
        if(registerBirthDetails.size() >0) {
            detailEnrichment.correctionField(request, registerBirthDetails, auditDetails);
        }
    }

    public void enrichUpdate(CorrectionRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);


    }
    private void setApplicationNumbers(CorrectionRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<CorrectionApplication> birthDetails = request.getCorrectionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                                        tenantId,
                                        config.getBirthApplNumberIdName(),
                                        request.getCorrectionDetails().get(0).getApplicationType(),
                                        "APPL",
                                        birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());
                });
    }

    private void setFileNumbers(CorrectionRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<CorrectionApplication> birthDetails = request.getCorrectionDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                request.getCorrectionDetails().get(0).getApplicationType(),
                "FILE",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getCorrectionDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
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

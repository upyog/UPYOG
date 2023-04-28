package org.ksmart.birth.abandoned.enrichment;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.abandoned.service.MdmsForAbandonedService;
import org.ksmart.birth.common.enrichment.BaseEnrichment;
import org.ksmart.birth.common.model.AuditDetails;
import org.ksmart.birth.common.repository.IdGenRepository;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.config.BirthConfiguration;
import org.ksmart.birth.newbirth.service.MdmsForNewBirthService;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.MdmsUtil;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.abandoned.AbandonedApplication;
import org.ksmart.birth.web.model.abandoned.AbandonedRequest;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

@Component
public class AbandonedEnrichment implements BaseEnrichment {
    @Autowired
    BirthConfiguration config;
    @Autowired
    MdmsUtil mdmsUtil;
    @Autowired
    MdmsForAbandonedService mdmsBirthService;
    @Autowired
    MdmsTenantService mdmsTenantService;
    @Autowired
    IdGenRepository idGenRepository;


    public void enrichCreate(AbandonedRequest request) {
        String tenantId = null;
        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);
        for (AbandonedApplication birth : request.getBirthDetails()) {
            tenantId = birth.getTenantId();
        }
        setPlaceOfBirth(request, tenantId, auditDetails);
        setApplicationNumbers(request);
        setFileNumbers(request);
        setStatisticalInfo(request);
    }

    public void enrichUpdate(AbandonedRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();
        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setAuditDetails(auditDetails);
                });
    }
    private void setApplicationNumbers(AbandonedRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<AbandonedApplication> birthDetails = request.getBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();
        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthApplNumberIdName(),
                request.getBirthDetails().get(0).getApplicationType(),
                "APPL",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setApplicationNo(itr.next());
                });
    }

    private void setFileNumbers(AbandonedRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<AbandonedApplication> birthDetails = request.getBirthDetails();
        String tenantId = birthDetails.get(0)
                .getTenantId();

        List<String> filecodes = getIds(requestInfo,
                tenantId,
                config.getBirthFileNumberName(),
                request.getBirthDetails().get(0).getApplicationType(),
                "FILE",
                birthDetails.size());
        validateFileCodes(filecodes, birthDetails.size());
        Long currentTime = Long.valueOf(System.currentTimeMillis());
        ListIterator<String> itr = filecodes.listIterator();
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setFileNumber(itr.next());
                    birth.setFileDate(currentTime);
                });
    }
private void setPlaceOfBirth(AbandonedRequest request, String trnantId, AuditDetails auditDetails) {
    Object mdmsData = mdmsUtil.mdmsCallForLocation(request.getRequestInfo(), trnantId);
    Object mdmsDataComm = mdmsUtil.mdmsCall(request.getRequestInfo());
    request.getBirthDetails().forEach(birth -> {
        birth.setId(UUID.randomUUID().toString());
        birth.setAuditDetails(auditDetails);
        if(birth.getPlaceofBirthId() != null || !birth.getPlaceofBirthId().isEmpty()){
            mdmsBirthService.setLocationDetails(birth, mdmsData);
            mdmsBirthService.setInstitutionDetails(birth, mdmsDataComm);
        }
        birth.setBirthPlaceUuid(UUID.randomUUID().toString());
        birth.getParentsDetails().setFatherUuid(UUID.randomUUID().toString());
        birth.getParentsDetails().setMotherUuid(UUID.randomUUID().toString());
        if(birth.getParentsDetails() != null) {
            if(!birth.getParentsDetails().getIsMotherInfoMissing()){
                birth.getParentsDetails().setMotherBioAdopt("BIOLOGICAL");
            }
        }


    });
}
    private void setStatisticalInfo(AbandonedRequest request) {
        request.getBirthDetails()
                .forEach(birth -> {
                    birth.setBirthStatisticsUuid(UUID.randomUUID().toString());
                    if(birth.getCaretakerDetails() != null) {
                        birth.setBirthCareTakerUuid(UUID.randomUUID().toString());
                    }
//                    if(birth.getInitiatorDetails() != null) {
//                        birth.setBirthCareTakerUuid(UUID.randomUUID().toString());
//                    }
                    birth.setBirthInitiatorUuid(UUID.randomUUID().toString());
                    Object mdmsData = mdmsUtil.mdmsCall(request.getRequestInfo());
                     mdmsBirthService.setTenantDetails(birth, mdmsData);

                    //  TOWN VILLAGe INSIDE Kerala
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

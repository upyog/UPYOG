package org.egov.filemgmnt.enrichment;

import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.config.FMConfiguration;
import org.egov.filemgmnt.util.IdgenUtil;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.egov.filemgmnt.web.models.FileDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPersonalEnrichment implements BaseEnrichment {

    private final FMConfiguration fmConfig;
    private final IdgenUtil idgenUtil;

    @Autowired
    ApplicantPersonalEnrichment(FMConfiguration fmConfig, IdgenUtil idgenUtil) {
        this.fmConfig = fmConfig;
        this.idgenUtil = idgenUtil;
    }

    /**
     * Enrich applicant personal create request.
     *
     * @param request the
     *                {@link org.egov.filemgmnt.web.models.ApplicantPersonalRequest
     *                ApplicantPersonalRequest}
     */
    public void enrichCreate(ApplicantPersonalRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.TRUE);

        request.getApplicantPersonals()
               .forEach(personal -> {
                   personal.setId(UUID.randomUUID()
                                      .toString());
                   personal.setAuditDetails(auditDetails);

                   personal.getServiceDetails()
                           .setId(UUID.randomUUID()
                                      .toString());

                   personal.getApplicantAddress()
                           .setId(UUID.randomUUID()
                                      .toString());

                   personal.getApplicantServiceDocuments()
                           .setId(UUID.randomUUID()
                                      .toString());
                   personal.getApplicantDocuments()
                           .setId(UUID.randomUUID()
                                      .toString());
                   personal.getFileDetail()
                           .setId(UUID.randomUUID()
                                      .toString());
               });

        setFileCodes(request);
    }

    /**
     * Enrich applicant personal update request.
     *
     * @param request the
     *                {@link org.egov.filemgmnt.web.models.ApplicantPersonalRequest
     *                ApplicantPersonalRequest}
     */
    public void enrichUpdate(ApplicantPersonalRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getApplicantPersonals()
               .forEach(personal -> personal.setAuditDetails(auditDetails));
    }

    private void setFileCodes(ApplicantPersonalRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        String tenantId = applicantPersonals.get(0)
                                            .getTenantId();

        List<String> filecodes = getFileCodes(requestInfo,
                                              tenantId,
                                              fmConfig.getFilemgmntFileCodeName(),
                                              fmConfig.getFilemgmntFileCodeFormat(),
                                              applicantPersonals.size());
        validateFileCodes(filecodes, applicantPersonals.size());

        ListIterator<String> itr = filecodes.listIterator();
        request.getApplicantPersonals()
               .forEach(personal -> {
                   FileDetail fileDetail = personal.getFileDetail();
                   fileDetail.setFileCode(itr.next());
               });
    }

    private List<String> getFileCodes(RequestInfo requestInfo, String tenantId, String idKey, String idformat,
                                      int count) {
        return idgenUtil.getIdList(requestInfo, tenantId, idKey, idformat, count);
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

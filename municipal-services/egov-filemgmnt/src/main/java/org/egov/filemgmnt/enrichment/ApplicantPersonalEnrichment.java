package org.egov.filemgmnt.enrichment;

import java.util.List;
import java.util.UUID;

import org.apache.commons.collections4.CollectionUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.filemgmnt.util.IdgenUtil;
import org.egov.filemgmnt.web.enums.ErrorCodes;
import org.egov.filemgmnt.web.models.ApplicantPersonal;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.egov.filemgmnt.web.models.FileDetail;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicantPersonalEnrichment implements BaseEnrichment {
    private final FilemgmntConfiguration config;
    private final IdgenUtil idgenUtil;

    @Autowired
    ApplicantPersonalEnrichment(FilemgmntConfiguration config, IdgenUtil idgenUtil) {
        this.config = config;
        this.idgenUtil = idgenUtil;
    }

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

        setIdgenIds(request);
    }

    /**
     * Sets the FileCode for given ServiceRequest
     *
     * @param request Request which is to be created
     */
    private void setIdgenIds(ApplicantPersonalRequest request) {
        RequestInfo requestInfo = request.getRequestInfo();
        List<ApplicantPersonal> applicantPersonals = request.getApplicantPersonals();

        String tenantId = applicantPersonals.get(0)
                                            .getTenantId();

        List<String> filecodes = getIdList(requestInfo,
                                           tenantId,
                                           config.getFilemgmntFileCodeName(),
                                           config.getFilemgmntFileCodeFormat(),
                                           applicantPersonals.size());

//        ListIterator<String> itr = filecodes.listIterator();
//        request.getApplicantPersonals()
//               .forEach(personal -> {
//                   ServiceDetails details = personal.getServiceDetails();
//                   details.setFileCode(itr.next());
//               });

        for (int i = 0; i < applicantPersonals.size(); i++) {

//            ServiceDetails details = applicantPersonals.get(i)
//                                                       .getServiceDetails();

            FileDetail details = applicantPersonals.get(i)
                                                   .getFileDetail();

            details.setFileCode(filecodes.get(i));
        }

        log.info("itr : {}", filecodes.get(0));
    }

    /**
     * Returns a list of numbers generated from idgen
     *
     * @param requestInfo RequestInfo from the request
     * @param tenantId    tenantId of the city
     * @param idKey       code of the field defined in application properties for
     *                    which ids are generated for
     * @param idformat    format in which ids are to be generated
     * @param count       Number of ids to be generated
     * @return List of ids generated using idGen service
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey, String idformat, int count) {
        List<String> idResponses = idgenUtil.getIdList(requestInfo, tenantId, idKey, idformat, count);

        if (CollectionUtils.isEmpty(idResponses)) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(), "No ids returned from idgen service");
        }

        if (idResponses.size() != count) {
            throw new CustomException(ErrorCodes.IDGEN_ERROR.getCode(),
                    "The number of fileCode returned by idgen service is not equal to the request count");
        }
        return idResponses;
    }

    public void enrichUpdate(ApplicantPersonalRequest request) {

        RequestInfo requestInfo = request.getRequestInfo();
        User userInfo = requestInfo.getUserInfo();

        AuditDetails auditDetails = buildAuditDetails(userInfo.getUuid(), Boolean.FALSE);

        request.getApplicantPersonals()
               .forEach(personal -> personal.setAuditDetails(auditDetails));
    }
}

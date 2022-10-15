package org.egov.filemgmnt.enrichment;

 
import java.util.UUID;
import java.util.*;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.filemgmnt.web.models.ApplicantPersonalRequest;
import org.egov.filemgmnt.web.models.AuditDetails;
import org.egov.filemgmnt.web.models.ServiceDetails;
import org.egov.filemgmnt.web.models.idgen.IdResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.egov.filemgmnt.config.FilemgmntConfiguration;
import org.egov.tracer.model.CustomException;
import org.egov.filemgmnt.util.IdgenUtil;

@Component
public class ApplicantPersonalEnrichment implements BaseEnrichment {
	private FilemgmntConfiguration config;
	private IdgenUtil util ;
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
        String tenantId = request.getApplicantPersonals().get(0).getTenantId();
        
        List<String> filecodes = getIdList(requestInfo, tenantId, config.getFileCodeIdgenNameFM(), config.getFileCodeIdgenFormatFM(), request.getApplicantPersonals().size());
                
        if (filecodes.size() != request.getApplicantPersonals().size()) {
            Map<String, String>  errorMap = Collections.singletonMap("IDGEN ERROR ", "The number of fileCode returned by idgen is not equal to fileCode of Filemanagment");
            throw new CustomException(errorMap);
        }
        
        ListIterator<String> itr = filecodes.listIterator();

        request.getApplicantPersonals().forEach(personal -> {
            ServiceDetails details = personal.getServiceDetails();
            details.setFileCode(itr.next());
        });
        
//        for (int i=0; i<applicant.size(); i++) {
//            ApplicantDocuments document = applicant.get(i).getApplicantDocument();
//            document.setDocumentNumber(filecodes.get(i));
//        }
        
    }
    /**
     * Returns a list of numbers generated from idgen
     *
     * @param requestInfo RequestInfo from the request
     * @param tenantId    tenantId of the city
     * @param idKey       code of the field defined in application properties for which ids are generated for
     * @param idformat    format in which ids are to be generated
     * @param count       Number of ids to be generated
     * @return List of ids generated using idGen service
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
                                   String idformat, int count) {
        List<String> idResponses = util.getIdList(requestInfo, tenantId, idKey, idformat, null);

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

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

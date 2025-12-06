package org.egov.pqm.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.repository.ServiceRequestRepository;
import org.egov.pqm.util.ErrorConstants;
import org.egov.pqm.util.MDMSUtils;
import org.egov.pqm.validator.PqmValidator;
import org.egov.pqm.web.model.EgovPdfResp;
import org.egov.pqm.web.model.QualityCriteria;
import org.egov.pqm.web.model.Test;
import org.egov.pqm.web.model.TestRequest;
import org.egov.pqm.web.model.mdms.MDMSQualityCriteria;
import org.egov.tracer.model.CustomException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;

import static org.egov.pqm.util.Constants.*;
import static org.egov.pqm.util.ErrorConstants.*;
import static org.egov.pqm.util.MDMSUtils.parseJsonToMap;

@Service
@Slf4j
public class PdfService {

    @Autowired
    private PqmValidator pqmValidator;
    @Autowired
    private MDMSUtils mdmsUtil;
    @Autowired
    private ServiceConfiguration config;

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private ObjectMapper mapper;

    String tenantIdReplacer = "$tenantId";
    String applicationKeyReplacer = "$applicationkey";


    public EgovPdfResp enrichQualityCriteria(TestRequest testRequest)
    {
        Test test = testRequest.getTests().get(0);

        //fetch mdms data for QualityCriteria Master
        Object jsondata = mdmsUtil.mdmsCallV2(testRequest.getRequestInfo(),
                testRequest.getTests().get(0).getTenantId().split("\\.")[0], MASTER_NAME_QUALITY_CRITERIA, new ArrayList<>());
        String jsonString = "";

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            jsonString = objectMapper.writeValueAsString(jsondata);
        } catch (Exception e) {
            throw new CustomException(ErrorConstants.PARSING_ERROR,
                    "Unable to parse QualityCriteria mdms data ");
        }

        // Parse JSON Response and create the map for QualityCriteria
        Map<String, MDMSQualityCriteria> codeToQualityCriteriaMap = parseJsonToMap(jsonString);

        //enrich Quality Criteria
        for (QualityCriteria qualityCriteria : test.getQualityCriteria()) {
            String uom = codeToQualityCriteriaMap.get(qualityCriteria.getCriteriaCode()).getUnit();
            qualityCriteria.setUom(uom);

            List<BigDecimal> benchmarkValues = codeToQualityCriteriaMap.get(qualityCriteria.getCriteriaCode()).getBenchmarkValues();
            qualityCriteria.setBenchmarkValue(benchmarkValues);
        }

        try {
            enrichLocalizedValues(test.getTenantId(), testRequest.getRequestInfo(), test.getQualityCriteria());
        }
        catch (Exception e)
        {
            log.error(e.getMessage());
        }

    return getFileStoreIdFromPDFService(testRequest, test.getTenantId(), PQM_PDF_KEY);
    }

    public void enrichLocalizedValues(String tenantId, RequestInfo requestInfo, List<QualityCriteria> qualityCriteriaList)
    {
        String localizedMessages = getLocalizationMessages(tenantId, requestInfo);
        for (QualityCriteria qualityCriteria : qualityCriteriaList) {
            String localizedUom = getMessageTemplate("PQM_UNIT_" + qualityCriteria.getUom(), localizedMessages);
            if (localizedUom.isEmpty())
                throw new CustomException(PQM_LOCALIZATION_ERROR, PQM_LOCALIZATION_ERROR_DESC_UNIT + " " + qualityCriteria.getUom());
            qualityCriteria.setUom(localizedUom);


            String localizedCriteriaCode = getMessageTemplate("PQM_QUALITYCRITERIA_" + qualityCriteria.getCriteriaCode(), localizedMessages);
            if (localizedCriteriaCode.isEmpty())
                throw new CustomException(PQM_LOCALIZATION_ERROR, PQM_LOCALIZATION_ERROR_DESC_QUALITYCRITERIA + " " + qualityCriteria.getCriteriaCode());

            qualityCriteria.setCriteriaName(localizedCriteriaCode);

        }

    }


    /**
     * Fetches messages from localization service
     *
     * @param tenantId    tenantId of the fsm
     * @param requestInfo The requestInfo of the request
     * @return Localization messages for the module
     */
    @SuppressWarnings("rawtypes")
    public String getLocalizationMessages(String tenantId, RequestInfo requestInfo) {

        LinkedHashMap responseMap = (LinkedHashMap) serviceRequestRepository.fetchResult(getUri(tenantId, requestInfo),
                requestInfo);
        return new JSONObject(responseMap).toString();
    }

    /**
     * Extracts message for the specific code
     *
     * @param notificationCode    The code for which message is required
     * @param localizationMessage The localization messages
     * @return message for the specific code
     */
    @SuppressWarnings("rawtypes")
    public String getMessageTemplate(String notificationCode, String localizationMessage) {
        String path = "$..messages[?(@.code==\"{}\")].message";
        String message = null;
        log.info("notificationCode :::  {} " + notificationCode);
        if (null != notificationCode) {
            try {
                path = path.replace("{}", notificationCode.trim());
                List data = JsonPath.parse(localizationMessage).read(path);
                if (!CollectionUtils.isEmpty(data))
                    message = data.get(0).toString();
                else
                    log.error("Fetching from localization failed with code " + notificationCode);
            } catch (Exception e) {
                log.warn("Fetching from localization failed", e);
            }
        }
        return message;
    }


    /**
     * Returns the uri for the localization call
     *
     * @param tenantId TenantId of the propertyRequest
     * @return The uri for localization search call
     */
    public StringBuilder getUri(String tenantId, RequestInfo requestInfo) {

        if (config.getIsLocalizationStateLevel())
            tenantId = tenantId.split("\\.")[0];

        String locale = "en_IN";
        if (!StringUtils.isEmpty(requestInfo.getMsgId()) && requestInfo.getMsgId().split("\\|").length >= 2)
            locale = requestInfo.getMsgId().split("\\|")[1];

        StringBuilder uri = new StringBuilder();
        uri.append(config.getLocalizationHost())
                .append(config.getLocalizationContextPath())
                .append(config.getLocalizationSearchEndpoint()).append("?").append("locale=")
                .append(locale).append("&tenantId=").append(tenantId).append("&module=")
                .append(SEARCH_MODULE_MDMS).append(",")
                .append(TQM_LOC_SEARCH_MODULE);
        return uri;
    }


    /**
     * Get file store id from PDF service
     *
     * @param testRequest TestRequest Json Object
     * @param tenantId Tenant Id
     * @param applicationKey Application Key String
     * @return file store id
     */
    private EgovPdfResp getFileStoreIdFromPDFService(TestRequest testRequest, String tenantId,
                                                     String applicationKey) {
        try {
            StringBuilder builder = new StringBuilder();
            builder.append(config.getPdfServiceHost());
            String pdfLink = config.getPdfServiceLink();
            pdfLink = pdfLink.replace(tenantIdReplacer, tenantId).replace(applicationKeyReplacer, applicationKey);
            builder.append(pdfLink);
            Object responseObject = serviceRequestRepository.fetchResult(builder, testRequest);

            EgovPdfResp response = mapper.convertValue(responseObject, EgovPdfResp.class);
            if (response != null && CollectionUtils.isEmpty(response.getFilestoreIds())) {
                throw new CustomException("EMPTY_FILESTORE_IDS_FROM_PDF_SERVICE",
                        "No file store id found from pdf service");
            }
//            result.setFilestoreIds(response.getFilestoreIds());
            EgovPdfResp egovPdfResp =  EgovPdfResp.builder().filestoreIds(response.getFilestoreIds()).build();
            return egovPdfResp;
        } catch (Exception ex) {
            throw new CustomException("WATER_FILESTORE_PDF_EXCEPTION", "PDF response can not parsed!!!");
        }
    }


}

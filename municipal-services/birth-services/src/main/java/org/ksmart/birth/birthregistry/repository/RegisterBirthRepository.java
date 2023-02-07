package org.ksmart.birth.birthregistry.repository;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthregistry.model.BirthCertRequest;
import org.ksmart.birth.birthregistry.enrichment.RegisterBirthEnrichment;
import org.ksmart.birth.birthregistry.model.BirthPdfRegisterRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetail;
import org.ksmart.birth.birthregistry.model.RegisterBirthDetailsRequest;
import org.ksmart.birth.birthregistry.model.RegisterBirthSearchCriteria;
import org.ksmart.birth.birthregistry.repository.querybuilder.RegisterQueryBuilder;
import org.ksmart.birth.birthregistry.repository.rowmapper.BirthRegisterRowMapper;
import org.ksmart.birth.common.services.MdmsTenantService;
import org.ksmart.birth.common.contract.EgovPdfResp;
import org.ksmart.birth.common.producer.BndProducer;
import org.ksmart.birth.config.BirthConfiguration;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class RegisterBirthRepository {
    private final BndProducer producer;
    private final BirthConfiguration config;
    private final JdbcTemplate jdbcTemplate;
    private final RegisterBirthEnrichment registerBirthDetailsEnrichment;
    private final BirthRegisterRowMapper birthRegisterRowMapper;
    private final RegisterQueryBuilder registerQueryBuilder;
    private final RestTemplate restTemplate;

    @Autowired
    RegisterBirthRepository(JdbcTemplate jdbcTemplate, RegisterBirthEnrichment registerBirthDetailsEnrichment,
                            BirthConfiguration birthDeathConfiguration, BndProducer producer, RegisterQueryBuilder registerQueryBuilder,
                            BirthRegisterRowMapper birthRegisterRowMapper, RestTemplate restTemplate) {
        this.jdbcTemplate=jdbcTemplate;
        this.registerBirthDetailsEnrichment=registerBirthDetailsEnrichment;
        this.config=birthDeathConfiguration;
        this.producer=producer;
        this.registerQueryBuilder=registerQueryBuilder;
        this.birthRegisterRowMapper=birthRegisterRowMapper;
        this.restTemplate=restTemplate;
    }

    public List<RegisterBirthDetail> saveRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        Boolean isAdopted=false;
        registerBirthDetailsEnrichment.enrichCreate(request);
        request.getRegisterBirthDetails()
                .forEach(register -> {
                    if (register.getIsAdopted()) {
                        producer.push(config.getSaveBirthRegisterTopic(), request);
                    } else {
                        producer.push(config.getSaveBirthRegisterTopic(), request);
                    }
                });
        return request.getRegisterBirthDetails();
    }

    public List<RegisterBirthDetail> updateRegisterBirthDetails(RegisterBirthDetailsRequest request) {
        registerBirthDetailsEnrichment.enrichUpdate(request);
        producer.push(config.getUpdateBirthRegisterTopic(), request);
        return request.getRegisterBirthDetails();
    }

    public List<RegisterBirthDetail> searchRegisterBirthDetails(RegisterBirthSearchCriteria criteria) {
        List<Object> preparedStmtValues=new ArrayList<>();
        String query=registerQueryBuilder.getRegBirthApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);
        List<RegisterBirthDetail> result=jdbcTemplate.query(query, preparedStmtValues.toArray(), birthRegisterRowMapper);
        return result;
    }

    public void saveRegisterBirthCert(BirthCertRequest request) {
//         registerBirthDetailsEnrichment.enrichCreate(request);
        producer.push(config.getSaveBirthCertificateTopic(), request);
        //return request.getRegisterBirthDetails();
    }

    public EgovPdfResp saveBirthCertPdf(BirthPdfRegisterRequest pdfApplicationRequest) {
        EgovPdfResp result=new EgovPdfResp();
        try {
            SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
            pdfApplicationRequest.getBirthCertificate()
                    .forEach(cert -> {
                        String uiHost=config.getUiAppHost();
                        String birthCertPath=config.getBirthCertLink();
                        birthCertPath=birthCertPath.replace("$id", cert.getId());
                        birthCertPath=birthCertPath.replace("$tenantId", cert.getTenantId());
                        birthCertPath=birthCertPath.replace("$regNo", cert.getRegistrationNo());
                        birthCertPath=birthCertPath.replace("$dateofbirth", format.format(cert.getDateOfBirth()));
                        birthCertPath=birthCertPath.replace("$gender", cert.getGender().toString());
                        birthCertPath=birthCertPath.replace("$birthcertificateno", cert.getRegistrationNo());
                        String finalPath=uiHost + birthCertPath;
                        cert.setEmbeddedUrl(getShortenedUrl(finalPath));
                    });
            log.info(new Gson().toJson(pdfApplicationRequest));

            BirthPdfRegisterRequest req=BirthPdfRegisterRequest.builder()
                    .birthCertificate(pdfApplicationRequest.getBirthCertificate())
                    .requestInfo(pdfApplicationRequest.getRequestInfo())
                    .build();

            pdfApplicationRequest.getBirthCertificate()
                    .forEach(cert -> {
                        String uiHost=config.getEgovPdfHost();
                        String birthCertPath=config.getEgovPdfBirthEndPoint();
                        String tenantId=cert.getTenantId()
                                            .split("\\.")[0];
                        birthCertPath=birthCertPath.replace("$tenantId", tenantId);
                        String pdfFinalPath=uiHost + birthCertPath;
                        EgovPdfResp response=restTemplate.postForObject(pdfFinalPath, req, EgovPdfResp.class);
                        if (response != null && CollectionUtils.isEmpty(response.getFilestoreIds())) {
                            throw new CustomException("EMPTY_FILESTORE_IDS_FROM_PDF_SERVICE", "No file store id found from pdf service");
                        }
                        result.setFilestoreIds(response.getFilestoreIds());
                    });
        } catch (Exception e) {
            e.printStackTrace();
            throw new CustomException("PDF_ERROR", "Error in generating PDF");
        }
        return result;
    }

    public String getShortenedUrl(String url) {
        HashMap<String, String> body=new HashMap<>();
        body.put("url", url);
        String res=restTemplate.postForObject(config.getUrlShortnerHost() + config.getUrlShortnerEndpoint(), body, String.class);
        if (StringUtils.isEmpty(res)) {
            log.error("URL_SHORTENING_ERROR", "Unable to shorten url: " + url);
            return url;
        } else return res;
    }
}

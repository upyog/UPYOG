package org.ksmart.birth.birthnacregistry.repository;
import org.ksmart.birth.birthnacregistry.enrichment.RegisterNacEnrichment;
import org.ksmart.birth.birthnacregistry.model.NacPdfRegisterRequest;
import org.ksmart.birth.birthnacregistry.model.RegisterNac;
import org.ksmart.birth.birthnacregistry.model.RegisterNacRequest;
import org.ksmart.birth.birthnacregistry.repository.rowmapper.NacRegisterRowMapper;
import org.ksmart.birth.birthnacregistry.model.RegisterNacSearchCriteria;
 
import org.ksmart.birth.birthnacregistry.model.NacCertRequest;
 
 
 
 
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

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
 
 
import org.ksmart.birth.birthnacregistry.repository.querybuilder.NacRegisterQueryBuilder;
 
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
import org.ksmart.birth.birthnacregistry.repository.rowmapper.NacCetificateRowMapper;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Repository
public class RegisterNacRepository {
	  private final BndProducer producer;
	    private final BirthConfiguration config;
	    private final JdbcTemplate jdbcTemplate;
	    private final RegisterNacEnrichment registerBirthDetailsEnrichment;
	    private final NacRegisterRowMapper nacRegisterRowMapper;
	    private final NacRegisterQueryBuilder registerQueryBuilder;
	    private final RestTemplate restTemplate;
	    private final NacCetificateRowMapper nacCetificateRowMapper;
	    
	    @Autowired
	    RegisterNacRepository(JdbcTemplate jdbcTemplate, RegisterNacEnrichment registerBirthDetailsEnrichment,NacCetificateRowMapper nacCetificateRowMapper,
	                            BirthConfiguration birthDeathConfiguration, BndProducer producer, NacRegisterQueryBuilder registerQueryBuilder,
	                            NacRegisterRowMapper nacRegisterRowMapper, RestTemplate restTemplate) {
	        this.jdbcTemplate=jdbcTemplate;
	        this.registerBirthDetailsEnrichment=registerBirthDetailsEnrichment;
	        this.config=birthDeathConfiguration;
	        this.producer=producer;
	        this.registerQueryBuilder=registerQueryBuilder;
	        this.nacRegisterRowMapper=nacRegisterRowMapper;
	        this.restTemplate=restTemplate;
	       // this.certificateQueryBuilder = certificateQueryBuilder;
	        this.nacCetificateRowMapper = nacCetificateRowMapper;
	    }
	    public List<RegisterNac> saveRegisterBirthDetails(RegisterNacRequest request) {
	        Boolean isAdopted=false;
	        registerBirthDetailsEnrichment.enrichCreate(request);	       
	        request.getRegisternacDetails()
	                .forEach(register -> {
	                    producer.push(config.getSaveNacBirthRegisterTopic(), request);
	                });
	        return request.getRegisternacDetails();
	    }
	    public List<RegisterNac> updateRegisterNacDetails(RegisterNacRequest request) {
	        registerBirthDetailsEnrichment.enrichUpdate(request);
	        producer.push(config.getUpdateBirthRegisterTopic(), request);
	        return request.getRegisternacDetails();
	    }
	    public List<RegisterNac> searchRegisterNacDetails(RegisterNacSearchCriteria criteria) {
	        List<Object> preparedStmtValues=new ArrayList<>();
	        String query=registerQueryBuilder.getRegBirthNacApplicationSearchQuery(criteria, preparedStmtValues, Boolean.FALSE);	       
	        List<RegisterNac> result=jdbcTemplate.query(query, preparedStmtValues.toArray(), nacRegisterRowMapper);
	        return result;
	    }
	    public void saveRegisterNacCert(NacCertRequest request) { 
	        producer.push(config.getNacCertSave(), request);
	       
	    }
	    public EgovPdfResp saveBirthCertPdf(NacPdfRegisterRequest pdfApplicationRequest) {
	        EgovPdfResp result=new EgovPdfResp();
	        try {
	            SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");
	            pdfApplicationRequest.getNacCertificate()
	                    .forEach(cert -> {
	                     
	                        String uiHost=config.getUiAppHost();
	                        String birthCertPath=config.getNacCertLink();
	                        birthCertPath=birthCertPath.replace("$id", cert.getId());
	                        birthCertPath=birthCertPath.replace("$tenantId", cert.getTenantId());
	                        birthCertPath=birthCertPath.replace("$regNo", cert.getRegistrationNo());
	                        birthCertPath=birthCertPath.replace("$dateofbirth", cert.getDobStr());	                        
	                        birthCertPath=birthCertPath.replace("$birthcertificateno", cert.getRegistrationNo());
	                        String finalPath=uiHost + birthCertPath;
	                        cert.setEmbeddedUrl(getShortenedUrl(finalPath));
	                    });
	            log.info(new Gson().toJson(pdfApplicationRequest));

	            NacPdfRegisterRequest req=NacPdfRegisterRequest.builder()
	                    .nacCertificate(pdfApplicationRequest.getNacCertificate())
	                    .requestInfo(pdfApplicationRequest.getRequestInfo())
	                    .build();

	            pdfApplicationRequest.getNacCertificate()
	                    .forEach(cert -> {
	                        String uiHost=config.getEgovPdfHost();
	                        String birthCertPath= null;
	                        if(cert.getIsBirthNAC() == true) {
	                        	 birthCertPath=config.getEgovPdfBirthNacEndPoint();
	                        }
	                        else  if(cert.getIsBirthNAC() == true) {
	                        	 birthCertPath=config.getEgovPdfBirthNiaEndPoint();
	                        }
	                        
	                         
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

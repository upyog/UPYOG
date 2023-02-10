package org.ksmart.birth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class BirthConfiguration {


    //Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${egov.workflow.host}")
    private String WfHost;

    @Value("${egov.workflow.path}")
    private String WfTransitionPath;


    @Value("${egov.idgen.birthapplnum.name}")
    private String birthApplNumberIdgenName;

    @Value("${egov.idgen.birthapplnum.format}")
    private String birthApplNumberIdgenFormat;

    @Value("${egov.idgen.birthapp.name}")
    private String birthApplNumberIdName;

    @Value("${egov.idgen.birthapp.format}")
    private String birthApplNumberIdFormat;

    @Value("${egov.idgen.birthfile.name}")
    private String birthFileNumberName;

    @Value("${egov.idgen.birthfile.name}")
    private String birthFileNumberFormat;
    @Value("${egov.idgen.birthreg.name}")
    private String birthRegisNumberName;

    @Value("${egov.idgen.birthreg.format}")
    private String birthRegisNumberFormat;

    //Persister Config
    @Value("${persister.save.birth.cert.topic}")
    private String saveBirthCertificateTopic;

    @Value("${persister.update.birth.cert.topic}")
    private String updateBirthCertificateTopic;

    @Value("${persister.save.adoption.topic}")
    private String saveBirthAdoptionTopic;

    @Value("${persister.update.adoption.topic}")
    private String updateBirthAdoptionTopic;

    @Value("${persister.save.birth.application.topic}")
    private String saveBirthApplicationTopic;

    @Value("${persister.update.birth.application.topic}")
    private String updateBirthApplicationTopic;

    @Value("${persister.ksmart.save.birth.topic}")
    private String saveKsmartBirthApplicationTopic;

    @Value("${persister.ksmart.update.birth.topic}")
    private String updateKsmartBirthApplicationTopic;

    @Value("${persister.save.birth.register.topic}")
    private String saveBirthRegisterTopic;

    @Value("${persister.update.birth.register.topic}")
    private String updateBirthRegisterTopic;

    //MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndPoint;

    @Value("${egov.billingservice.host}")
    private String billingHost;

    @Value("${egov.bill.gen.endpoint}")
    private String fetchBillEndpoint;

    @Value("${egov.demand.create.endpoint}")
    private String demandCreateEndpoint;
    
    @Value("${egov.pdf.host}")
    private String pdfHost;
    
    @Value("${egov.pdf.birthcert.postendpoint}")
    private String saveBirthCertEndpoint;
    
    @Value("${egov.pdf.deathcert.postendpoint}")
    private String saveDeathCertEndpoint;
    
    @Value("${egov.bnd.birthcert.link}")
    private String birthCertLink;
    
    @Value("${egov.bnd.deathcert.link}")
    private String deathCertLink;

    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;
    
    @Value("${egov.url.shortner.endpoint}")
    private String urlShortnerEndpoint;
    
    @Value("${egov.ui.app.host}")
	private String uiAppHost;

    @Value("${egov.bnd.default.limit}")
    private Integer defaultBndLimit;

    @Value("${egov.bnd.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.bnd.max.limit}")
    private Integer maxSearchLimit;
    
    @Value("${egov.bnd.download.bufferdays}")
    private Integer downloadBufferDays;
    
    @Value("${egov.collection.service.host}")
	private String collectionServiceHost;
	
	@Value("${egov.payment.search.endpoint}")
	private String	PaymentSearchEndpoint;

    @Value("${egov.pdfservice.host}")
    private String	egovPdfHost;

    @Value("${egov.pdf.birthcert.createEndPoint}")
    private String	egovPdfBirthEndPoint;

    @Value("${egov.pdf.deathcert.createEndPoint}")
    private String	egovPdfDeathEndPoint;



}

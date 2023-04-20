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
    
    @Value("${egov.idgen.adoptionack.name}")
    private String adoptionAckIdName;
    

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

    @Value("${persister.update.birth.payment.topic}")
    private String updateBirthPaymentTopic;

    @Value("${persister.save.adoption.topic}")
    private String saveBirthAdoptionTopic;

    @Value("${persister.update.adoption.topic}")
    private String updateBirthAdoptionTopic;

    @Value("${persister.save.still.birth.topic}")
    private String saveStillBirthTopic;

    @Value("${persister.update.still.birth.topic}")
    private String updateStillBirthTopic;

    @Value("${persister.save.correction.birth.topic}")
    private String saveCorrectionBirthTopic;

    @Value("${persister.update.correction.birth.topic}")
    private String updateCorrectionBirthTopic;

    @Value("${persister.save.abandoned.birth.topic}")
    private String saveAbandonedBirthTopic;

    @Value("${persister.update.abandoned.birth.topic}")
    private String updateAbandonedBirthTopic;

    @Value("${persister.save.born.outside.birth.topic}")
    private String saveBornOutsideTopic;

    @Value("${persister.update.born.outside.birth.topic}")
    private String updateBornOutsideTopic;

    @Value("${persister.ksmart.save.birth.topic}")
    private String saveKsmartBirthApplicationTopic;

    @Value("${persister.ksmart.update.birth.topic}")
    private String updateKsmartBirthApplicationTopic;

    @Value("${persister.save.birth.register.topic}")
    private String saveBirthRegisterTopic;

    @Value("${persister.update.birth.register.topic}")
    private String updateBirthRegisterTopic;

    @Value("${persister.save.nac.birth.register.topic}")
    private String saveNacBirthRegisterTopic;

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
    
    @Value("${egov.pdf.nacbirthcert.createEndPoint}")
    private String	egovPdfBirthNacEndPoint;

    @Value("${egov.pdf.deathcert.createEndPoint}")
    private String	egovPdfDeathEndPoint;
    
    @Value("${persister.save.nac.topic}")
    private String saveBirthNacTopic;

    @Value("${persister.update.nac.topic}")
    private String updateBirthNacTopic;
    
    @Value("${egov.bnd.naccert.link}")
    private String nacCertLink;



}

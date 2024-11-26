package org.egov.tl.consumer;

import static org.egov.tl.util.TLConstants.businessService_BPA;
import static org.egov.tl.util.TLConstants.businessService_TL;

import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.egov.tl.service.AlfrescoService;
import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.service.notification.TLNotificationService;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.contract.Alfresco.DMSResponse;
import org.egov.tl.web.models.contract.Alfresco.DmsRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class TradeLicenseConsumer {

    private TLNotificationService notificationService;

    private TradeLicenseService tradeLicenseService;
    
    @Autowired
    AlfrescoService alfrescoService;

    @Autowired
    public TradeLicenseConsumer(TLNotificationService notificationService, TradeLicenseService tradeLicenseService) {
        this.notificationService = notificationService;
        this.tradeLicenseService = tradeLicenseService;
    }

    @KafkaListener(topics = {"${persister.update.tradelicense.topic}","${persister.save.tradelicense.topic}","${persister.update.tradelicense.workflow.topic}","${save.tl.certificate}"})
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        ObjectMapper mapper = new ObjectMapper();
        TradeLicenseRequest tradeLicenseRequest = new TradeLicenseRequest();

        try {
            tradeLicenseRequest = mapper.convertValue(record, TradeLicenseRequest.class);
        } catch (final Exception e) {
            log.error("Error while listening to value: " + record + " on topic: " + topic + ": " + e);
        }
        if(StringUtils.equals(topic, "${save.tl.certificate}")) {
        	saveTlCertificate(tradeLicenseRequest);
        	return ;
        }
        
        if (!tradeLicenseRequest.getLicenses().isEmpty()) {
            String businessService = tradeLicenseRequest.getLicenses().get(0).getBusinessService();
            if (businessService == null)
                businessService = businessService_TL;
            switch (businessService) {
                case businessService_BPA:
                    try {
                        tradeLicenseService.checkEndStateAndAddBPARoles(tradeLicenseRequest);
                    } catch (final Exception e) {
                        log.error("Error occurred while adding roles for BPA user " + e);
                    }
                    break;
            }
        }
        notificationService.process(tradeLicenseRequest);
    }

	public void saveTlCertificate(TradeLicenseRequest tradeLicenseRequest) {

		TradeLicense license = tradeLicenseRequest.getLicenses().get(0);

		// validate trade license
		tradeLicenseService.validateTradeLicenseCertificateGeneration(license,
				tradeLicenseRequest.getRequestInfo());

		// create pdf
		Resource resource = tradeLicenseService.createNoSavePDF(license,
				tradeLicenseRequest.getRequestInfo());

		// upload pdf
		DmsRequest dmsRequest = tradeLicenseService.generateDmsRequestByTradeLicense(resource, license,
				tradeLicenseRequest.getRequestInfo());
		try {
			String documentReferenceId = alfrescoService.uploadAttachment(dmsRequest,
					tradeLicenseRequest.getRequestInfo());
		} catch (IOException e) {
			throw new CustomException("UPLOAD_ATTACHMENT_FAILED",
					"Upload Attachment failed." + e.getMessage());
		}

	}
}

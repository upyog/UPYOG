package org.egov.vendor.kafka;

import lombok.extern.slf4j.Slf4j;
import org.egov.vendor.repository.VendorAdditionalDetailsRepository;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.egov.vendor.web.models.VendorAdditionalDetailsRequest;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;

@Slf4j
@Component
public class Consumer {

    private final VendorAdditionalDetailsRepository vendorRepository;

    public Consumer(VendorAdditionalDetailsRepository contractorRepository, ObjectMapper objectMapper) {
        this.vendorRepository = contractorRepository;
    }

    @KafkaListener(topics = {
            "${persister.kafka.topic.save.vendordetails}",
            "${persister.kafka.topic.update.vendordetails}"
    })
    public void listen(final VendorAdditionalDetailsRequest message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.info("VendorAdditionalDetailsRequest Consumer : {}", topic);
        //ObjectMapper mapper = new ObjectMapper();
        //VendorAdditionalDetailsRequest vendorAdditionalDetailsRequest = new VendorAdditionalDetailsRequest();
        //vendorAdditionalDetailsRequest = mapper.convertValue(record, VendorAdditionalDetailsRequest.class);
        VendorAdditionalDetails vendorAdditionalDetails = new VendorAdditionalDetails();

        try {
            log.info("Consuming record: " + message.toString());
            vendorAdditionalDetails = message.getVendorAdditionalDetails();
            vendorRepository.save(vendorAdditionalDetails);
            log.info("Vendor Additional Details saved: " + vendorAdditionalDetails.getVendorId());

        } catch (final Exception e) {
            log.error("Error while listening to value: " + message + " on topic: " + topic + ": " + e);
        }
    }
}

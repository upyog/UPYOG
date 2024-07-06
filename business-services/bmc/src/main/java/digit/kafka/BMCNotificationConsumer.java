// package digit.kafka;

// import java.util.HashMap;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.kafka.annotation.KafkaListener;
// import org.springframework.kafka.support.KafkaHeaders;
// import org.springframework.messaging.handler.annotation.Header;
// import org.springframework.stereotype.Component;

// import com.fasterxml.jackson.databind.ObjectMapper;

// import digit.service.BmcNotificationService;
// import digit.web.models.SchemeApplicationRequest;
// import lombok.extern.slf4j.Slf4j;

// @Component
// @Slf4j
// public class BMCNotificationConsumer {
//     @Autowired
//     private BmcNotificationService notificationService;
//     @Autowired
//     private ObjectMapper mapper;


//     @KafkaListener(topics = { "${bmc.kafka.create.topic}", "${bmc.kafka.update.topic}" })
//     public void listen(final HashMap<String, Object> message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

//         SchemeApplicationRequest schemeApplicationRequest = new SchemeApplicationRequest();
//         try {
//             log.debug("Consuming record in BMC for notification: " + message.toString());
//             schemeApplicationRequest = mapper.convertValue(message, SchemeApplicationRequest.class);
//         } catch (final Exception e) {
//             log.error("Error while listening to value: " + message + " on topic: " + topic + ": " + e);
//         }

//         log.info("BMC Application Received: " + schemeApplicationRequest.getSchemeApplications().get(0).getApplicationNumber());

//         notificationService.process(schemeApplicationRequest);
//     }
// }

package digit.kafka;

import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import digit.service.BmcNotificationService;
import digit.web.models.SchemeApplicationRequest;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BMCNotificationConsumer {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BmcNotificationService notificationService;

    @KafkaListener(topics = { "${bmc.kafka.create.topic}", "${bmc.kafka.update.topic}" })
    public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {

        try {
            SchemeApplicationRequest request = mapper.convertValue(record, SchemeApplicationRequest.class);
            notificationService.process(request);

        } catch (final Exception e) {

            log.error("Error while listening to value: " + record + " on topic: " + topic + ": ", e);
        }
    }

}


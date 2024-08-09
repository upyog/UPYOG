package digit.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import digit.config.BmcConfiguration;
import digit.kafka.Producer;
import digit.web.models.SMSRequest;
import digit.web.models.SchemeApplication;
import digit.web.models.SchemeApplicationRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BmcNotificationService {

    @Autowired
    private Producer producer;
    @Autowired
    private BmcConfiguration config;

    private static final String SMS_TEMPLATE = "Dear {USER_NAME}, your scheme application has been successfully created with application number - {APPNUMBER}.";

    public void process(SchemeApplicationRequest request) {
        List<SMSRequest> smsRequestList = new ArrayList<>();
        request.getSchemeApplications().forEach(application -> {
            SMSRequest smsRequest = SMSRequest.builder()
                    .mobileNumber(application.getUser().getMobileNumber())
                    .message(getCustomMessage(SMS_TEMPLATE, application))
                    .build();
            smsRequestList.add(smsRequest);
        });
        for (SMSRequest smsRequest : smsRequestList) {
            producer.push(config.getSmsNotificationTopic(), smsRequest);
            log.info("Messages: " + smsRequest.getMessage());
        }
    }

    private String getCustomMessage(String template, SchemeApplication application) {
        template = template.replace("{APPNUMBER}", application.getApplicationNumber());
        template = template.replace("{USER_NAME}", application.getUser().getName());
        return template;
    }
}

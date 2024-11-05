package org.egov.nationaldashboardingest.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.web.models.Email;
import org.egov.nationaldashboardingest.web.models.EmailRequest;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.springframework.stereotype.Component;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Component
public class EmailUtil {

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationProperties appProp;

    public List<EmailRequest> createEmailRequest(RequestInfo requestInfo, Map<String,Map<String, String>> missingStates) {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<EmailRequest> emailRequestList = new LinkedList<>();
        for (Map.Entry<String, Map<String,String>> entry : missingStates.entrySet()) {
            Map<String,String> officerInfo = entry.getValue();
            String stateName = entry.getKey();
            String email = officerInfo.get("email");
            String nodalOfficer = officerInfo.get("nodalOfficer");

            String subject = "Urgent: Data Ingestion Issue on UMEED National Dashboard";
            String body = String.format(
                            "Dear " + nodalOfficer + " ,\n\n" +
                            "We have observed that data from " + stateName + " has not been ingested onto the UMEED National Dashboard on " + formattedDate +".\n\n" +
                            "This data gap is hindering our ability to monitor urban performance and make informed decisions. " +
                            "We kindly request you to take immediate action to resolve the issue and ensure timely data ingestion.\n\n" +
                            "Please coordinate with your technical team to identify/rectify the root cause of the problem. " +
                            "If you need technical assistance or support, please contact the NUDM-NIUA technical team.\n\n" +
                            "We appreciate your prompt attention to this matter.\n\n" +
                            "Thank you,\n" +
                            "[Your Name]\n" +
                            "[Your Designation]\n" +
                            "[Organization]\n" +
                            "[Contact Information]"

            );
            Email emailObj = Email.builder()
                    .emailTo(Collections.singleton(email))
                    .isHTML(true)
                    .body(body)
                    .subject(subject)
                    .build();

            EmailRequest emailRequest = new EmailRequest(requestInfo, emailObj);
            emailRequestList.add(emailRequest);
        }
        return emailRequestList;
    }
    public void sendEmail(Map<String, Map<String,String>> missingStates) {

        List<EmailRequest> emailRequestList = createEmailRequest(new RequestInfo(), missingStates);

            for (EmailRequest emailRequest : emailRequestList) {
                producer.push(appProp.getEmailNotifTopic(), emailRequest);
                log.info("Email Request -> " + emailRequest.toString());
                log.info("EMAIL notification sent!");

        }
    }
}



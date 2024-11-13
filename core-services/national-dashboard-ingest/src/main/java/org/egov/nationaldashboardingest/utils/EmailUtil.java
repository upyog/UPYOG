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
                            "<p>Dear " + nodalOfficer + " ,<br><br></p>" +
                            "<p>We have observed that data from " + stateName + " has not been ingested onto the UMEED National Dashboard on " + formattedDate +".</p>" +
                            "<p>This data gap is hindering our ability to monitor urban performance and make informed decisions. " +
                            "We kindly request you to take immediate action to resolve the issue and ensure timely data ingestion.</p>" +
                            "<p>Please coordinate with your technical team to identify/rectify the root cause of the problem.</p>" +
                            "<p>If you need technical assistance or support, please contact the NUDM-NIUA technical team.</p>" +
                            "<p>We appreciate your prompt attention to this matter.<br><br></p>" +
                            "<p>Warm Regards,<br>" +
                            "National Urban Digital Mission, Centre for Digital Governance<br>" +
                            "National Institute of Urban Affairs (NIUA)<br>" +
                            "Email: cdg-contact@niua.org | niua.in/cdg/Home<br>" +
                            "Core 4B, 1st and 2nd Floor, India Habitat Centre, Lodhi Road | New Delhi - 110003</p>"
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
    public void sendEmail(RequestInfo requestInfo, Map<String, Map<String,String>> missingStates) {

        List<EmailRequest> emailRequestList = createEmailRequest(requestInfo, missingStates);

            for (EmailRequest emailRequest : emailRequestList) {
                producer.push(appProp.getEmailNotifTopic(), emailRequest);
                log.info("Email Request -> " + emailRequest.toString());
                log.info("EMAIL notification sent!");

        }
    }
}



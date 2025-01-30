package org.egov.nationaldashboardingest.utils;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.nationaldashboardingest.producer.Producer;
import org.egov.nationaldashboardingest.web.models.Attachments;
import org.egov.nationaldashboardingest.web.models.Email;
import org.egov.nationaldashboardingest.web.models.EmailRequest;
import org.egov.nationaldashboardingest.config.ApplicationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

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

    @Autowired
    private ExcelUtil excelUtil;

    public List<EmailRequest> createEmailRequest(RequestInfo requestInfo, Map<String, Map<String, Object>> stateList) throws IOException {

        LocalDate yesterday = LocalDate.now().minusDays(1);
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        List<Attachments> attachmentsList = excelUtil.generateExcelFiles(stateList);

        List<EmailRequest> emailRequestList = new LinkedList<>();
        for (Map.Entry<String, Map<String, Object>> entry : stateList.entrySet()) {
            Map<String, Object> officerInfo = entry.getValue();
            String stateName = entry.getKey();
            String email = (String) officerInfo.get("email");
            String nodalOfficer = (String) officerInfo.get("nodalOfficer");

            Attachments stateExcel = attachmentsList.stream()
                    .filter(attachments -> attachments.getFileName().equals(stateName + ".xlsx"))
                    .findFirst()
                    .orElse(null);

            String subject = stateName + " : Data Ingestion Issue on UMEED National Dashboard";
            String body = String.format(
                    "<p>Dear %s,<br><br></p>" +
                            "<p>We have observed that data from %s has not been ingested onto the UMEED National Dashboard on %s.</p>" +
                            "<p>This data gap is hindering our ability to monitor urban performance and make informed decisions. " +
                            "We kindly request you to take immediate action to resolve the issue and ensure timely data ingestion.</p>" +
                            "<p>Please coordinate with your technical team to identify/rectify the root cause of the problem." +
                            "If you need technical assistance or support, please contact the NUDM-NIUA technical team.</p>" +
                            "<p>We appreciate your prompt attention to this matter.<br><br></p>" +
                            "<p>Warm Regards,<br>" +
                            "National Urban Digital Mission, Centre for Digital Governance<br>" +
                            "National Institute of Urban Affairs (NIUA)<br>" +
                            "Email: cdg-contact@niua.org | niua.in/cdg/Home<br>" +
                            "Core 4B, 1st and 2nd Floor, India Habitat Centre, Lodhi Road | New Delhi - 110003</p>",
                    nodalOfficer, stateName, formattedDate
            );


            Email emailObj = new Email();
            emailObj.setEmailTo(Collections.singleton(email));
            emailObj.setHTML(true);
            emailObj.setBody(body);
            emailObj.setSubject(subject);
            if(stateExcel != null) {
                emailObj.setAttachments(stateExcel);
            }

            EmailRequest emailRequest = new EmailRequest(requestInfo, emailObj);
            emailRequestList.add(emailRequest);
        }

        return emailRequestList;
    }


    public void sendEmail(RequestInfo requestInfo, Map<String, Map<String, Object>> stateList) throws IOException {
        List<EmailRequest> emailRequestList = createEmailRequest(requestInfo, stateList);

        for (EmailRequest emailRequest : emailRequestList) {
            producer.push(appProp.getEmailNotifTopic(), emailRequest);
            log.info("Email Request -> {}", emailRequest);
            log.info("EMAIL notification sent!");
        }
    }

}

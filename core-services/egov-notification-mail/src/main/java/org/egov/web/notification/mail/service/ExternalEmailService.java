package org.egov.web.notification.mail.service;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.mail.internet.MimeMessage;

import org.egov.web.notification.mail.consumer.contract.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@ConditionalOnProperty(value = "mail.enabled", havingValue = "true")
@Slf4j
public class ExternalEmailService implements EmailService {

    private JavaMailSenderImpl mailSender;

    @Value("${egov.filestore.internal.host}")
    private String internalHost;

    public ExternalEmailService(JavaMailSenderImpl mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendEmail(Email email) {
        try {
            if (email.isHTML()) {
                sendHTMLEmail(email);
            } else {
                sendTextEmail(email);
            }
        } catch (Exception e) {
            log.error("Error in sendEmail for: " + email.getEmailTo(), e);
        }
    }

    private void sendTextEmail(Email email) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(mailSender.getUsername());
        mailMessage.setTo(email.getEmailTo().toArray(new String[0]));
        mailMessage.setSubject(email.getSubject());
        mailMessage.setText(email.getBody());
        
        log.info("Sending text email to: {}", String.join(", ", email.getEmailTo()));
        mailSender.send(mailMessage);
    }

    private void sendHTMLEmail(Email email) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            // true flag indicates multipart message for attachments
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(mailSender.getUsername());
            helper.setTo(email.getEmailTo().toArray(new String[0]));
            helper.setSubject(email.getSubject());
            helper.setText(email.getBody(), true);

            if (!CollectionUtils.isEmpty(email.getAttachments())) {
                processAttachments(email.getAttachments(), helper);
            }

            log.info("Handing over to SMTP Server ({}) for delivery...", mailSender.getHost());
            mailSender.send(message);
            log.info("Email sent successfully to: {}", String.join(", ", email.getEmailTo()));

        } catch (Exception e) {
            log.error("CRITICAL: Error during SMTP handover for HTML email", e);
        }
    }

    private void processAttachments(List<String> attachments, MimeMessageHelper helper) {
        for (String urlString : attachments) {
            try {
                String downloadUrl = urlString;

                // Use the internal routing logic to bypass SSL issues on the server
                // We check for the filestore path specifically
                if (urlString.contains("/filestore/v1/files/viewfile")) {
                    downloadUrl = urlString.replaceFirst("https?://[^/]+", internalHost);
                    log.info("Rerouting attachment download: {} -> {}", urlString, downloadUrl);
                }

                java.net.URL url = new java.net.URL(downloadUrl);
                java.net.URLConnection conn = url.openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(10000);

                // Download bytes to memory
                byte[] bytes = org.springframework.util.StreamUtils.copyToByteArray(conn.getInputStream());

                if (bytes != null && bytes.length > 0) {
                    // Use YOUR extractFileName method here
                    String fileName = extractFileName(urlString);
                    
                    // Attach the unique file
                    helper.addAttachment(fileName, new org.springframework.core.io.ByteArrayResource(bytes));
                    log.info("Successfully attached file: {} (Size: {} bytes)", fileName, bytes.length);
                }
            } catch (Exception e) {
                log.error("Failed to process attachment for URL: " + urlString, e);
                // We continue the loop so other attachments/email can still proceed
            }
        }
    }

    /**
     * Extracts filename from Filestore URL, handling encoded slashes and query params.
     */
    private String extractFileName(String urlString) {
        try {
            String fileName = "attachment.pdf"; // Default fallback
            
            if (urlString.contains("name=")) {
                // 1. Get everything after "name="
                fileName = urlString.split("name=")[1];
                
                // 2. Remove any other parameters after the name (like &tenantId=...)
                if (fileName.contains("&")) {
                    fileName = fileName.substring(0, fileName.indexOf("&"));
                }
                
                // 3. Get the last part of the path (after the last %2F or /)
                int lastSlash = Math.max(fileName.lastIndexOf("/"), fileName.lastIndexOf("%2F"));
                if (lastSlash != -1) {
                    // If it was %2F, we need to skip 3 characters, if /, just 1
                    int offset = fileName.contains("%2F") && lastSlash == fileName.lastIndexOf("%2F") ? 3 : 1;
                    fileName = fileName.substring(lastSlash + offset);
                }
            }
            
            // Decode the URL characters (like %20 to space) just in case
            return java.net.URLDecoder.decode(fileName, "UTF-8");
            
        } catch (Exception e) {
            log.warn("Could not extract filename from URL, using default. URL: {}", urlString);
            return "Sanction_Document.pdf";
        }
    }
}
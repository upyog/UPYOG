package org.egov.web.notification.mail.consumer.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmailAttachment {
    private String url;
    private String contentId;
    private String mimeType;
}
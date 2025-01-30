package org.egov.web.notification.mail.consumer.contract;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class Attachments {
    private String fileName;
    private byte[] content;
    private String contentType;
}

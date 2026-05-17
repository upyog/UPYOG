package org.egov.echallan.model;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailAttachment {
    private String url;         // The Dev/Public URL to download the image
    private String contentId;   // This MUST match the cid: tag in HTML (e.g., "evidence1")
    private String mimeType;    // e.g., "image/jpeg"
}
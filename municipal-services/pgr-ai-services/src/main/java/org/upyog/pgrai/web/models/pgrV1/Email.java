package org.upyog.pgrai.web.models.pgrV1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Represents an email in the system.
 * This class encapsulates the details of an email, including:
 * - The recipient's email address.
 * - The subject of the email.
 * - The body content of the email.
 * - A flag indicating whether the email content is in HTML format.
 *
 * This class is part of the PGR V1 module and is used to manage
 * email-related information within the system.
 */
@AllArgsConstructor
@Getter
@EqualsAndHashCode
@Builder
public class Email {
    private String toAddress;
    private String subject;
    private String body;
    private boolean html;
}

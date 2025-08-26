package org.upyog.pgrai.web.models.Notification;

import lombok.*;

/**
 * Represents a request for sending SMS notifications in the notification system.
 * This class encapsulates the details of an SMS request, including:
 * - The mobile number to which the SMS is sent.
 * - The message content of the SMS.
 *
 * This class is part of the notification module in the PGR system.
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SMSRequest {
    private String mobileNumber;
    private String message;
}

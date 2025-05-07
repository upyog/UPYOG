package org.upyog.pgrai.web.models.Notification;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Represents the recipient details in the notification system.
 * This class encapsulates the information about the recipients of a notification, including:
 * - A list of roles to which the notification is sent.
 * - A list of users to whom the notification is sent.
 *
 * This class is part of the notification module in the PGR system.
 */
@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Recepient {
	
	private List<String> toRoles;
	
	private List<String> toUsers;

}

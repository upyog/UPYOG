package org.upyog.pgrai.web.models.Notification;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents an action associated with a notification in the system.
 * This class encapsulates the details of an action, including:
 * - The tenant ID to which the action belongs.
 * - The unique ID of the action.
 * - The event ID associated with the action.
 * - A list of {@link ActionItem} objects representing the URLs for the action.
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
public class Action {
	
	private String tenantId;
	
	private String id;
	
	private String eventId;
	
	@NotNull
	private List<ActionItem> actionUrls;
	
}

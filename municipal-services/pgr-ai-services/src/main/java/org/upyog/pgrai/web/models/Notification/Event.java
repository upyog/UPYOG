package org.upyog.pgrai.web.models.Notification;

import lombok.*;
import org.upyog.pgrai.web.models.Status;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Represents an event in the notification system.
 * This class encapsulates the details of an event, including:
 * - The tenant ID to which the event belongs.
 * - The unique ID of the event.
 * - A reference ID associated with the event.
 * - The type and name of the event.
 * - A description of the event.
 * - The status of the event.
 * - The source of the event.
 * - The user who posted the event.
 * - The recipient details of the event.
 * - Associated actions for the event.
 * - Additional details about the event.
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
public class Event {
	
	@NotNull
	private String tenantId;
	
	private String id;
	
	private String referenceId;
	
	@NotNull
	private String eventType;
	
	private String name;
	
	@NotNull
	private String description;
	
	private Status status;
	
	@NotNull
	private Source source;
	
	private String postedBy;
	
	@Valid
	@NotNull
	private Recepient recepient;
	
	private Action actions;
	
	private EventDetails eventDetails;
		

}

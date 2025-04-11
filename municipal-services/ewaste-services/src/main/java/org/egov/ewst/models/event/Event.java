package org.egov.ewst.models.event;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * Represents an event in the Ewaste application.
 * This class contains details about the event such as tenant ID, event type, description, status, source, recipient, actions, and event details.
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

	// Tenant ID associated with the event
	@NotNull
	private String tenantId;

	// Unique identifier for the event
	private String id;

	// Reference ID associated with the event
	private String referenceId;

	// Type of the event
	@NotNull
	private String eventType;

	// Name of the event
	private String name;

	// Description of the event
	@NotNull
	private String description;

	// Status of the event
	private Status status;

	// Source of the event
	@NotNull
	private Source source;

	// User who posted the event
	private String postedBy;

	// Recipient of the event
	@Valid
	@NotNull
	private Recepient recepient;

	// Actions associated with the event
	private Action actions;

	// Additional details of the event
	private EventDetails eventDetails;

}
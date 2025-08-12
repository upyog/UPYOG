package org.egov.ewst.models.event;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Represents an action associated with an event in the Ewaste application.
 * This class contains details about the action such as tenant ID, action URLs, etc.
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

	// Tenant ID associated with the action
	private String tenantId;

	// Unique identifier for the action
	private String id;

	// Event ID associated with the action
	private String eventId;

	// List of action URLs associated with the action
	@NotNull
	private List<ActionItem> actionUrls;

}
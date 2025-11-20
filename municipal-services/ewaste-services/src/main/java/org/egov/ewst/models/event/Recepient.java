package org.egov.ewst.models.event;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * Represents a recipient in the Ewaste application.
 * This class contains details about the recipient such as roles and users.
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

	// List of roles associated with the recipient
	private List<String> toRoles;

	// List of users associated with the recipient
	private List<String> toUsers;

}
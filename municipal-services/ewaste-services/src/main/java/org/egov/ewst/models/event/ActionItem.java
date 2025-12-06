package org.egov.ewst.models.event;

import lombok.*;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

/**
 * Represents an action item associated with an event in the Ewaste application.
 * This class contains details about the action item such as action URL and code.
 */
@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class ActionItem {

	// URL associated with the action item
	@NotNull
	private String actionUrl;

	// Code associated with the action item
	@NotNull
	private String code;

}
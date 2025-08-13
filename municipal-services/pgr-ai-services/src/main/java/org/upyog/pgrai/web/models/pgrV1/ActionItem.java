package org.upyog.pgrai.web.models.pgrV1;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

/**
 * Represents an action item in the system.
 * This class encapsulates the details of an action item, including:
 * - The URL associated with the action.
 * - The unique code identifying the action.
 *
 * This class is part of the PGR V1 module and is used to define actions
 * that can be performed within the system.
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
	
	@NotNull
	private String actionUrl;
	
	@NotNull
	private String code;

}

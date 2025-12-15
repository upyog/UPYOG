package org.upyog.adv.web.models.events;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

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

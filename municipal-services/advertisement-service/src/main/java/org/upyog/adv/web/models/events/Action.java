package org.upyog.adv.web.models.events;

import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.*;


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

package org.upyog.adv.web.models.events;

import jakarta.validation.constraints.NotNull;
import lombok.*;


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

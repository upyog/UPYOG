package org.upyog.adv.web.models.events;

import java.util.List;
import lombok.*;


@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Recepient {

	private List<String> toRoles;

	private List<String> toUsers;

}

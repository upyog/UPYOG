package org.upyog.rs.web.models.events;

import lombok.*;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

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

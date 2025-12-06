package org.upyog.sv.web.models.idgen;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

/**
 * <h1>IdRequest</h1>
 * 
 * @author Narendra
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IdRequest {

	@JsonProperty("idName")
	@NotNull
	private String idName;

	@NotNull
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("format")
	private String format;

}

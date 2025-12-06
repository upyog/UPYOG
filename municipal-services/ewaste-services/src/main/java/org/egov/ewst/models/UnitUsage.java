package org.egov.ewst.models;

<<<<<<< HEAD
import jakarta.validation.constraints.NotNull;
=======
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
<<<<<<< HEAD
=======
import org.hibernate.validator.constraints.SafeHtml;
>>>>>>> master-LTS

@SuppressWarnings("deprecation")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitUsage {

<<<<<<< HEAD
	@JsonProperty("id")
	private String id;

=======
	@SafeHtml
	@JsonProperty("id")
	private String id;

	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("unitId")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@NotNull
	private String unitId;

	@JsonProperty("usageCategory")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@NotNull
	private String usageCategory;

	@JsonProperty("occupancyType")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@NotNull
	private String occupancyType;

	@JsonProperty("occupancyDate")
	private Long occupancyDate;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}

package digit.bmc.model;

import org.egov.common.contract.models.AuditDetails;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitUsage {

	@JsonProperty("id")
	private String id;

	
	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("unitId")
	
	@NotNull
	private String unitId;

	@JsonProperty("usageCategory")
	
	@NotNull
	private String usageCategory;

	@JsonProperty("occupancyType")
	
	@NotNull
	private String occupancyType;

	@JsonProperty("occupancyDate")
	private Long occupancyDate;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails;

}

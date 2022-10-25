package org.egov.tl.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import org.apache.http.impl.client.NullBackoffStrategy;
import org.egov.tl.web.models.AuditDetails;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;
import org.egov.tl.web.models.TradeLicenseDetail;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-09-18T17:06:11.263+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class StructurePlace {

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("id")
    private String id;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("tenantId")
    private String tenantId = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("structurePlaceSubType")
    private String structurePlaceSubType = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("blockNo")
    private String blockNo = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("surveyNo")
    private String surveyNo = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("subDivisionNo")
    private String subDivisionNo = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("zonalcode")
    private String zonalcode = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("wardcode")
    private String wardcode = null;

    @Range(min = 0, max = 100)
    @JsonProperty("wardNo")
    private Integer wardNo = 0;

    @Range(min = 0, max = 100000)
    @JsonProperty("doorNo")
    private Integer doorNo = 0;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("doorNoSub")
    private String doorNoSub = null;

    // @Size(max = 64)
    @JsonProperty("buildingId")
    private Long buildingId = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("vehicleNo")
    private String vehicleNo = null;

    @Size(max = 64)
    @SafeHtml
    @JsonProperty("vesselNo")
    private String vesselNo = null;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

}

package org.egov.vehicle.web.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import org.egov.vehicle.web.model.user.User;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import org.egov.vehicle.validation.SanitizeHtml;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:37:21.257Z[GMT]")
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class Vehicle {

    @SanitizeHtml
    @JsonProperty("id")
    private String id = null;

	@Size(max=64)
    @SanitizeHtml
    @JsonProperty("tenantId")
    private String tenantId = null;

    @NonNull
    @SanitizeHtml
	@Size(max=64)
    @JsonProperty("registrationNumber")
    private String registrationNumber  = null;

    @SanitizeHtml
	@Size(max=64)
    @JsonProperty("model")
    private String model = null;

    @NonNull
    @SanitizeHtml
	@Size(max=64)
    @JsonProperty("type")
    private String type = null;


    @JsonProperty("tankCapacity")
    private Double tankCapacity;
    
    @SanitizeHtml
    @JsonProperty("suctionType")
    private String suctionType = null;
    
    @SanitizeHtml
    @JsonProperty("vehicleOwner")
    private String vehicleOwner = null;

    @JsonProperty("pollutionCertiValidTill")
    private Long pollutionCertiValidTill;

    @JsonProperty("InsuranceCertValidTill")
    private Long InsuranceCertValidTill;

    @JsonProperty("fitnessValidTill")
    private Long fitnessValidTill;

    @JsonProperty("roadTaxPaidTill")
    private Long  roadTaxPaidTill;

    @JsonProperty("gpsEnabled")
    private boolean gpsEnabled;

    @JsonProperty("additionalDetails")
    private Object additionalDetails = null;

    @SanitizeHtml
    @JsonProperty("source")
    private String source = null;

    @SanitizeHtml
    @JsonProperty("ownerId")
    private String ownerId = null; 

    public enum StatusEnum {
        ACTIVE("ACTIVE"),
        INACTIVE("INACTIVE"),
    	DISABLED("DISABLED");
        private String value;

        StatusEnum(String value) {
            this.value = value;
        }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static StatusEnum fromValue(String text) {
        for (StatusEnum b : StatusEnum.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        return null;
    }
}
    @JsonProperty("status")
    private StatusEnum status = null;

    @JsonProperty("owner")
    @Valid
    private User owner = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    

}



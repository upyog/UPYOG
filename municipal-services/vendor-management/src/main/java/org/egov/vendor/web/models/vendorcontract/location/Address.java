package org.egov.vendor.web.models.vendorcontract.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.vendor.web.models.AuditDetails;
import org.egov.vendor.web.models.vendorcontract.vendor.Vendor;
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.*;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */
//@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
//@Embeddable
@Entity
@Table(name = "eg_vendor_address")
public class Address {


	@Transient
	@JsonProperty("tenantId")
	private String tenantId = null;

	@JsonProperty("doorNo")
	private String doorNo = null;


	@JsonProperty("plotNo")
	private String plotNo = null;


	@Id
	@JsonProperty("id")
	private String id = null;

	@JsonProperty("landmark")
	private String landmark = null;

	@JsonProperty("city")
	private String city = null;

	@JsonProperty("district")
	private String district = null;

	@JsonProperty("region")
	private String region = null;

	@JsonProperty("state")
	private String state = null;

	@JsonProperty("country")
	private String country = null;

	@JsonProperty("pincode")
	private String pincode = null;

	@Transient
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@JsonProperty("buildingName")
	private String buildingName = null;

	@JsonProperty("street")
	private String street = null;

	@Transient
	@JsonProperty("locality")
	private Boundary locality = null;

	@Transient
	@JsonProperty("geoLocation")
	private GeoLocation geoLocation = null;

	@Transient
	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;


	@JsonIgnore
	//@JsonProperty("vendorId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", referencedColumnName = "id")
	private Vendor vendor;

}

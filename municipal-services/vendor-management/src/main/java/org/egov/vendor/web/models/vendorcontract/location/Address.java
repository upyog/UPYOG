package org.egov.vendor.web.models.vendorcontract.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.vendor.web.models.AuditDetails;
import org.egov.vendor.web.models.vendorcontract.vendor.Vendor;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */
//@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
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


	@SafeHtml
	@Transient
	@JsonProperty("tenantId")
	private String tenantId = null;

	@SafeHtml
	@JsonProperty("doorNo")
	private String doorNo = null;

	@SafeHtml
	@JsonProperty("plotNo")
	private String plotNo = null;


	@Id
	@SafeHtml
	@JsonProperty("id")
	private String id = null;

	@SafeHtml
	@JsonProperty("landmark")
	private String landmark = null;

	@SafeHtml
	@JsonProperty("city")
	private String city = null;

	@SafeHtml
	@JsonProperty("district")
	private String district = null;

	@SafeHtml
	@JsonProperty("region")
	private String region = null;

	@SafeHtml
	@JsonProperty("state")
	private String state = null;

	@SafeHtml
	@JsonProperty("country")
	private String country = null;

	@SafeHtml
	@JsonProperty("pincode")
	private String pincode = null;

	@Transient
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

	@SafeHtml
	@JsonProperty("buildingName")
	private String buildingName = null;

	@SafeHtml
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

	@SafeHtml
	@JsonIgnore
	//@JsonProperty("vendorId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", referencedColumnName = "id")
	private Vendor vendor;

}

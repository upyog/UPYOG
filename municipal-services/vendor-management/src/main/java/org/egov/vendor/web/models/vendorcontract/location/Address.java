package org.egov.vendor.web.models.vendorcontract.location;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.vendor.web.models.AuditDetails;
import org.egov.vendor.web.models.vendorcontract.vendor.Vendor;
<<<<<<< HEAD
import org.springframework.validation.annotation.Validated;

import jakarta.persistence.*;
=======
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
>>>>>>> master-LTS

/**
 * Representation of a address. Indiavidual APIs may choose to extend from this
 * using allOf if more details needed to be added in their case.
 */
//@Schema(description = "Representation of a address. Indiavidual APIs may choose to extend from this using allOf if more details needed to be added in their case. ")
@Validated
<<<<<<< HEAD
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
=======
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
>>>>>>> master-LTS
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

<<<<<<< HEAD
=======

	@SafeHtml
>>>>>>> master-LTS
	@Transient
	@JsonProperty("tenantId")
	private String tenantId = null;

<<<<<<< HEAD
	@JsonProperty("doorNo")
	private String doorNo = null;

=======
	@SafeHtml
	@JsonProperty("doorNo")
	private String doorNo = null;

	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("plotNo")
	private String plotNo = null;


	@Id
<<<<<<< HEAD
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

=======
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
>>>>>>> master-LTS
	@JsonProperty("pincode")
	private String pincode = null;

	@Transient
	@JsonProperty("additionalDetails")
	private Object additionalDetails = null;

<<<<<<< HEAD
	@JsonProperty("buildingName")
	private String buildingName = null;

=======
	@SafeHtml
	@JsonProperty("buildingName")
	private String buildingName = null;

	@SafeHtml
>>>>>>> master-LTS
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

<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@JsonIgnore
	//@JsonProperty("vendorId")
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "vendor_id", referencedColumnName = "id")
	private Vendor vendor;

}

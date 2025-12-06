package org.egov.vendor.web.models.vendorcontract.vendor;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;

=======
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.*;
>>>>>>> master-LTS
import org.egov.common.contract.request.User;
import org.egov.vendor.web.models.AuditDetails;
import org.egov.vendor.web.models.vendorcontract.driver.Driver;
import org.egov.vendor.web.models.vendorcontract.location.Address;
import org.egov.vendor.web.models.vendorcontract.vehicle.Vehicle;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Type;
<<<<<<< HEAD
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
=======
import org.hibernate.annotations.TypeDef;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
>>>>>>> master-LTS


/**
 * Capture the vendor information in the system.
 */
@Validated
<<<<<<< HEAD
@jakarta.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
=======
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2021-01-06T05:34:12.238Z[GMT]")
>>>>>>> master-LTS
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Immutable
@Table(name = "eg_Vendor")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Vendor {

<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Id
	@JsonProperty("id")
	@Column(name = "id", nullable = false, length = 64)
	private String id = null;

	@JsonProperty("tenantId")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Size(max=64)
	@Column(name = "tenantid", length = 64)
	private String tenantId = null;

	@JsonProperty("name")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Size(max=128)
	@Column(name = "name", length = 128)
	private String name = null;

	//@Transient
	//@Embedded
	@OneToOne(mappedBy = "vendor", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = true)
	private Address address = null;

	@Transient
	@JsonProperty("owner")
	@Valid
	private User owner = null;

	@Transient
	@JsonProperty("vehicles")
	@Valid
	private List<Vehicle> vehicles = new ArrayList<Vehicle>();

	@Transient
	@JsonProperty("drivers")
	@Valid
	private List<Driver> drivers = null;

	//@Transient
	@JsonProperty("additionalDetails")
	@Type(type = "jsonb") // Ensure Hibernate recognizes it as JSONB
	@Column(columnDefinition = "jsonb", name = "additionalDetails")
	private Object additionalDetails = null;

<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("source")
	@Column(name = "source", length = 256)
	private String source = null;

<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@JsonProperty("description")
	@Column(name = "description", length = 256)
	private String description = null;
	
	@JsonProperty("ownerId")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Size(max=64)
	private String ownerId = null;

	@JsonProperty("agencyType")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Size(max=128)
	@Column(name = "agencytype", length = 128, nullable = false)
	private String agencyType = null;
	
	@JsonProperty("paymentPreference")
<<<<<<< HEAD
=======
	@SafeHtml
>>>>>>> master-LTS
	@Size(max=128)
	@Column(name = "paymentpreference", length = 128, nullable = false)
	private String paymentPreference = null;
	
		
	/**
	 * Inactive records will be consider as soft deleted
	 */
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

	@Enumerated(EnumType.STRING)
	@JsonProperty("status")
	private StatusEnum status = null;

	@JsonProperty("auditDetails")
	private AuditDetails auditDetails = null;
}

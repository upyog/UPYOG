package org.upyog.pgrai.web.models.pgrv1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * Represents a citizen in the system.
 * This class encapsulates the details of a citizen, including:
 * - Unique identifiers such as ID and UUID.
 * - Personal information like name, address, and contact details.
 * - Authentication details such as username and password.
 * - Additional attributes like roles, user type, and gender.
 *
 * This class is part of the PGR V1 module and is used to manage
 * citizen-related information within the system.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Citizen {

	private Long id;
	private String uuid;
	
	@Size(max=30)
	private String name;
	
	@JsonProperty("permanentAddress")
	@Size(max=160)
	private String address;
	
	private String mobileNumber;
	
	private String aadhaarNumber;
	private String pan;
	
	@jakarta.validation.constraints.Email
	private String emailId;
	private String userName;
	private String password;
	private Boolean active;
	private UserType type;
	private Gender gender;
	private String tenantId; 
	
	@JsonProperty("roles")
    private List<Role> roles;
}

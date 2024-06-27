package org.egov.user.web.contract;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HpSsoValidateTokenResponse {


	@JsonProperty("sso_id")
    private Long ssoId;
	@JsonProperty("vault_id")
    private String vaultId;
    private String username;
    private String name;
    private String mobile;
    private String email;
    private String gender;
    private String dob; // Consider using LocalDate for date representations
    private String co; // Care of
    private String street;
    private String lm; // Landmark
    private String loc; // Location
    private String vtc; // Village/Town/City
    private String dist; // District
    private String state;
    private String pc; // Postal Code
	@JsonProperty("UsersArray")
    private List<Object> usersArray; // Type should be specified more precisely if possible

    
}

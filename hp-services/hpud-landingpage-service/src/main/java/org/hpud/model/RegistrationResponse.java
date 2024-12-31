package org.hpud.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegistrationResponse {
	private String businessName;
	private String nameOfEmployer;
	private String natureOfBusiness;
	private String categoryOfBusiness;
	private String DateOfCommencementOfBusiness;
	private String PostalAddress;
	private String status;
	private String msg;
	private String validUptoDate;

}

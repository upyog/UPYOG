package org.upyog.sv.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BeneficiaryScheme {

	private String id;
	
    private String applicationId;

    private String schemeName;
    
    private String enrollmentId;

}

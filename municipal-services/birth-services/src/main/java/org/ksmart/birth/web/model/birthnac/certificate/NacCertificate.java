package org.ksmart.birth.web.model.birthnac.certificate; 

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class NacCertificate {
	
	private String applicantNameEn;
	private String localAreaEn;
	private String taluk;
	private String village;
	private String district;
	private String state;
	private String childFullNameEn;
	private String motherFullNameEn;

}

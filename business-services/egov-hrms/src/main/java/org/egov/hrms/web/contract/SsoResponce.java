package org.egov.hrms.web.contract;

import lombok.Data;
import lombok.Setter;

@Data
@Setter
public class SsoResponce {

    private String userName;
	private String tenantId;
	private String employeeType;
	private String url;
    
}

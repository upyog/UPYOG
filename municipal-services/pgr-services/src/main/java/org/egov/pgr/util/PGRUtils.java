package org.egov.pgr.util;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pgr.web.models.AuditDetails;
import org.egov.pgr.web.models.Service;
import org.springframework.stereotype.Component;

@Component
public class PGRUtils {



    /**
     * Method to return auditDetails for create/update flows
     *
     * @param by
     * @param isCreate
     * @return AuditDetails
     */
    public AuditDetails getAuditDetails(String by, Service service, Boolean isCreate) {
        Long time = System.currentTimeMillis();
        if(isCreate)
            return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time).build();
        else
            return AuditDetails.builder().createdBy(service.getAuditDetails().getCreatedBy()).lastModifiedBy(by)
                    .createdTime(service.getAuditDetails().getCreatedTime()).lastModifiedTime(time).build();
    }
    
	public AuditDetails buildCreateAuditDetails(RequestInfo requestInfo) {
		String uuid = requestInfo.getUserInfo().getUuid();
		return AuditDetails.builder().createdBy(uuid).createdTime(System.currentTimeMillis()).lastModifiedBy(uuid)
				.lastModifiedTime(System.currentTimeMillis()).build();
	}

	public AuditDetails buildUpdateAuditDetails(AuditDetails auditDetails, RequestInfo requestInfo) {
		String uuid = requestInfo.getUserInfo().getUuid();

		if (null == auditDetails) {
			auditDetails = AuditDetails.builder().build();
		}
		auditDetails.setLastModifiedBy(uuid);
		auditDetails.setLastModifiedTime(System.currentTimeMillis());

		return auditDetails;
	}
	
	public String capitalizeFirstLetter(String input) {
		if (input == null || input.isEmpty()) {
			return input;
		}

		// Convert the first letter to uppercase and the rest to lowercase
		return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
	}

}

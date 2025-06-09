package org.egov.garbageservice.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.model.AuditDetails;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class GrbgUtils {

	public static String toCamelCase(String str) {
		// Convert the entire string to lowercase and then capitalize the first letter
		if (str == null || str.isEmpty()) {
			return str;
		}
		// Convert the first letter to uppercase and the rest to lowercase
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static String removeFirstAndLastChar(String str) {
		// Check if the string is long enough to remove first and last characters
		if (str == null || str.length() <= 2) {
			return ""; // Return empty string if length is 2 or less
		}

		// Use substring to remove first and last character
		return str.substring(1, str.length() - 1);
	}

	public String getContentAsString(ClassPathResource resource) {
	    try (InputStream is = resource.getInputStream();
	         BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
	        return reader.lines().collect(Collectors.joining("\n"));
	    } catch (IOException e) {
	        e.printStackTrace();
	        return "";
	    }
	}
	
	public AuditDetails buildCreateAuditDetails(RequestInfo requestInfo) {
		String uuid = requestInfo.getUserInfo().getUuid();
		return AuditDetails.builder().createdBy(uuid).createdDate(System.currentTimeMillis()).lastModifiedBy(uuid)
				.lastModifiedDate(System.currentTimeMillis()).build();
	}

	public AuditDetails buildUpdateAuditDetails(AuditDetails auditDetails, RequestInfo requestInfo) {
		String uuid = requestInfo.getUserInfo().getUuid();

		if (null == auditDetails) {
			auditDetails = AuditDetails.builder().build();
		}
		auditDetails.setLastModifiedBy(uuid);
		auditDetails.setLastModifiedDate(System.currentTimeMillis());

		return auditDetails;
	}

}

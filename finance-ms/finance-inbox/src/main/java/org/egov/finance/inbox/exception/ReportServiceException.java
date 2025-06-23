/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.inbox.exception;

import java.util.Map;

import org.egov.finance.inbox.util.ReportConstants;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class ReportServiceException extends RuntimeException{

	private static final long serialVersionUID = -19921057227329617L;

	private final Map<String, String> errors;
    
	public ReportServiceException(Map<String, String> errors) {
		super();
		this.errors = errors;
		log.error(ReportConstants.EXCEPTION_FROM_REPORT_SERVICE_MSG,errors);
	}
    
    
    
}

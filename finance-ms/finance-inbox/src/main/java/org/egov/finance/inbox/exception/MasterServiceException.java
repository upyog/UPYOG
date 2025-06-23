/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.inbox.exception;

import java.util.Map;

import org.egov.finance.inbox.util.MasterConstants;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class MasterServiceException extends RuntimeException{

	private static final long serialVersionUID = -19921057227329617L;

	private final Map<String, String> errors;
    
	public MasterServiceException(Map<String, String> errors) {
		super();
		this.errors = errors;
		log.error(MasterConstants.EXCEPTION_FROM_MASTER_SERVICE_MSG,errors);
	}
    
    
    
}

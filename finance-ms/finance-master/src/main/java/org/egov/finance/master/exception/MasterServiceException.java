/**
 * 
 * 
 * @author Surya
 */
package org.egov.finance.master.exception;

import java.util.Map;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = false)
public class MasterServiceException extends RuntimeException{

	private static final long serialVersionUID = -19921057227329617L;

	private final Map<String, String> errors;
    
	public MasterServiceException(Map<String, String> errors) {
		super();
		this.errors = errors;
	}
    
    
    
}

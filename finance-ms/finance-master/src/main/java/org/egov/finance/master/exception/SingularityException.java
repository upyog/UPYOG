package org.egov.finance.master.exception;

import java.util.Map;

import lombok.Data;


@Data
public class SingularityException extends RuntimeException{

	private static final long serialVersionUID = -19921057227329617L;

	private final Map<String, String> errors;
    
	public SingularityException(Map<String, String> errors) {
		super();
		this.errors = errors;
	}
    
    
    
}

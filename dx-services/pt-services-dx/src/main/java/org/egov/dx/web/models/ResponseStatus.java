package org.egov.dx.web.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@XStreamAlias("ResponseStatus")

public class ResponseStatus {
	
	 @XStreamAsAttribute
	    private String ts;
	    	
	    @XStreamAsAttribute
	    private String txn;
	    
	    @XStreamAsAttribute
	    private String Status;
	    

	   
}

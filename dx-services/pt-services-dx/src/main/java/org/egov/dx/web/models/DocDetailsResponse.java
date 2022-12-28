package org.egov.dx.web.models;

import com.thoughtworks.xstream.annotations.XStreamAlias;

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
@XStreamAlias("DocDetails")

public class DocDetailsResponse {
	
	@XStreamAlias("IssuedTo")
    private IssuedTo issuedTo;
	
	@XStreamAlias("URI")
    private String URI;
	
	
	@XStreamAlias("DocContent")
    private String docContent;
	
	@XStreamAlias("DataContent")
    private String dataContent;
     
}

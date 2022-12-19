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
@XStreamAlias("PullDocResponse")

public class PullURIResponsePOJO {

	@XStreamAlias("ResponseStatus")
	private String responseStatus;
    
    @XStreamAlias("DocDetails")
    private DocDetailsResponse docDetails;

     
}

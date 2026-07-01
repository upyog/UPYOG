package org.egov.pgr.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SwachhImageData {
    private String id;
    private String tenantId;
    private String userUuid;
    private Double latitude;
    private Double longitude;
    private String locality;
    private User userDetail = null;   
    private String imagerUrl;
    private Long createdTime;
    
    private String dateOfAttendance; // new

	
	
	
}

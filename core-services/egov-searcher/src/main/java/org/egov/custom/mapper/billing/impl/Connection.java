package org.egov.custom.mapper.billing.impl;

import java.util.HashMap;
import java.util.Map;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode
public class Connection {

	private String propertyId;
	
	
	private String oldConnectionNo;
	
	private String status;

	private Object additionalDetails;
	
	public void setAdditionalDetails(Object additionalDetails) {
		 if (additionalDetails instanceof PGobject) {
	            PGobject pgObject = (PGobject) additionalDetails;
	            try {
	            
	                String additionalDetailsValue = pgObject.getValue();
	                String additionalDetailsType= pgObject.getType();

	                ObjectMapper objectMapper = new ObjectMapper();
	                Map<String, Object> parsedValue = objectMapper.readValue(additionalDetailsValue, Map.class);

	                pgObject.setValue(objectMapper.writeValueAsString(parsedValue));
	                
	                Map<String, Object> additionalDetailsMap = new HashMap<>();
	                additionalDetailsMap.put("type", additionalDetailsType);
	                additionalDetailsMap.put("value", parsedValue);
	                
	                this.additionalDetails = additionalDetailsMap;

	            } catch (Exception e) {
	                e.printStackTrace();
	                // Handle error in parsing JSON
	            }
	        }
	    }

}

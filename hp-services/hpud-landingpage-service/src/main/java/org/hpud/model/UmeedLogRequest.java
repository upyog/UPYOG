package org.hpud.model;

import com.fasterxml.jackson.databind.JsonNode;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UmeedLogRequest {
	
	private String date;

    private String serviceType;

    private JsonNode requestPayload;

    private JsonNode responsePayload;

}

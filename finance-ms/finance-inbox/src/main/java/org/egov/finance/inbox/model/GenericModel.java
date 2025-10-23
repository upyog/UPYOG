/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class GenericModel {
	
	private Long id;
	private PrintType type;
	private String code;
	private String name;
	
	
	public enum PrintType {
        PDF,
        XLS,
        UNKNOWN; // fallback

        @JsonCreator
        public static PrintType forValue(String value) {
            for (PrintType type : PrintType.values()) {
                if (type.name().equalsIgnoreCase(value)) {
                    return type;
                }
            }
            return UNKNOWN;
        }

        @JsonValue
        public String toValue() {
            return this.name();
        }
        
    }
	
	
}


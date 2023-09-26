package org.egov.pt.calculator.web.models.property;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Channel {
	
	  SYSTEM("SYSTEM"),
	  
	  CFC_COUNTER("CFC_COUNTER"),
	  
	  CITIZEN("CITIZEN"),
	  
	  DATA_ENTRY("DATA_ENTRY"),
	  
	  LEGACY_MIGRATION("LEGACY_MIGRATION"),
		
	  MIGRATION("MIGRATION");

	  private String value;

	  Channel(String value) {
	    this.value = value;
	  }

	  @Override
	  @JsonValue
	  public String toString() {
	    return String.valueOf(value);
	  }

	  @JsonCreator
	  public static Channel fromValue(String text) {
	    for (Channel b : Channel.values()) {
	      if (String.valueOf(b.value).equalsIgnoreCase(text)) {
	        return b;
	      }
	    }
	    return null;
	  }
	}

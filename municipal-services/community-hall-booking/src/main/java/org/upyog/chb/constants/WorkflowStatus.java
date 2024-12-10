package org.upyog.chb.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * New property comes into system either property is newly constructed or existing property got sub divided. Here the reason for creation will be captured.
 */

public enum WorkflowStatus {
	
  CREATE ("CREATE"),
  
  UPDATE ("UPDATE"),
  
  STATUS("STATUS");

  private String value;

  WorkflowStatus(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

	@JsonCreator
	public static WorkflowStatus fromValue(String text) {
		for (WorkflowStatus b : WorkflowStatus.values()) {
			if (String.valueOf(b.value).equalsIgnoreCase(text)) {
				return b;
			}
		}
		return null;
	}
}

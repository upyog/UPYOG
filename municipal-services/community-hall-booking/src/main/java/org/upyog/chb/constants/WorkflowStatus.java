package org.upyog.chb.constants;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * This enum represents the various workflow statuses used in the Community Hall Booking module.
 * 
 * Purpose:
 * - To define a set of predefined constants representing workflow states.
 * - To ensure consistency and avoid hardcoding workflow status values across the application.
 * 
 * Enum Values:
 * 1. CREATE:
 *    - Represents the creation of a new workflow or entity.
 * 
 * 2. UPDATE:
 *    - Represents the update or modification of an existing workflow or entity.
 * 
 * 3. STATUS:
 *    - Represents a generic status or state in the workflow.
 * 
 * Features:
 * - Overrides the `toString` method to return the string representation of the enum value.
 * - Provides a static method `fromValue` to convert a string to the corresponding enum value.
 * 
 * Usage:
 * - This enum is used in workflow-related operations to handle and validate workflow states.
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

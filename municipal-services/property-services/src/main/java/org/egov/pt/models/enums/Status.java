package org.egov.pt.models.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * status of the Property
 */
public enum Status {

	ACTIVE ("ACTIVE"),

	INACTIVE ("INACTIVE"),

	INWORKFLOW ("INWORKFLOW"),
	
	CANCELLED ("CANCELLED"),
	
	REJECTED ("REJECTED"),
	
	INITIATED ("INITIATED"),
	
	PENDINGFORVERIFICATION ("PENDINGFORVERIFICATION"),
	
	PENDINGFORMODIFICATION ("PENDINGFORMODIFICATION"),
	
	PENDINGFORAPPROVAL ("PENDINGFORAPPROVAL"),
	
	APPROVED ("APPROVED");

	private String value;

  Status(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static Status fromValue(String text) {
    for (Status b : Status.values()) {
      if (String.valueOf(b.value).equalsIgnoreCase(text)) {
        return b;
      }
    }
    return null;
  }
}

package org.egov.fsm.web.model.worker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkerStatus {
  ACTIVE("ACTIVE"),
  INACTIVE("INACTIVE"),
  DISABLED("DISABLED");

  private String value;

  WorkerStatus(String value) {
    this.value = value;
  }

  @JsonCreator
  public static WorkerStatus fromValue(String text) {
    for (WorkerStatus b : WorkerStatus.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
    }
    return null;
  }

  @Override
  @JsonValue
  public String toString() {

    return String.valueOf(value);
  }
}
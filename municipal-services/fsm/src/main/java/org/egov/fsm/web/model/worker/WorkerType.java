package org.egov.fsm.web.model.worker;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum WorkerType {
  DRIVER("DRIVER"),
  HELPER("HELPER");

  private String value;

  WorkerType(String value) {
    this.value = value;
  }

  @JsonCreator
  public static WorkerType fromValue(String text) {
    for (WorkerType b : WorkerType.values()) {
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
package org.egov.pqm.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TestResultStatus {
  PASS,
  FAIL,
  PENDING
}

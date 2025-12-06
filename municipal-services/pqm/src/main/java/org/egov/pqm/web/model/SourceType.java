package org.egov.pqm.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum SourceType {
  LAB_SCHEDULED,
  IOT_SCHEDULED,
  LAB_ADHOC
}

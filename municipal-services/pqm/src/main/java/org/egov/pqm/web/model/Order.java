package org.egov.pqm.web.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Order {

  ASC("ASC"),

  DESC("DESC");

  private String value;
}
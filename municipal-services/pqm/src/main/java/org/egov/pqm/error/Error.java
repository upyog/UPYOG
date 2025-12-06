package org.egov.pqm.error;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.EXPECTATION_FAILED;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.PRECONDITION_FAILED;
import static org.springframework.http.HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS;

import java.text.MessageFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Error implements IError {
  record_not_found(CustomException.builder()
      .code(BAD_REQUEST.name()), "Record {0} is not found with identifier ::: {1}", 2001),
  record_creation_failed(CustomException.builder()
      .code(EXPECTATION_FAILED.name()), "Record {0} creation failed with identifier {1} due to {2}",
      2002),
  record_update_failed(CustomException.builder()
      .code(BAD_REQUEST.name()), "Record {0} update failed with identifier {1} due to {2}", 2003),
  record_is_disabled(CustomException.builder()
      .code(UNAVAILABLE_FOR_LEGAL_REASONS.name()),
      "Record {0} with identifier {1} is disabled/blocked, " +
          "reasons (if any) :: {2}", 2004),
  duplicate_record_creation_attempt(CustomException.builder()
      .code(CONFLICT.name()), "Duplicate record creation attempt for {0} " +
      "with identifier name:::{1} & value ::: {2}", 2005),
  mandatory_field_missing(CustomException.builder()
      .code(PRECONDITION_FAILED.name()),
      "Mandatory Field(s) are missing in {0} name ::: {1}, Type ::: {2}", 3001),
  invalid_applicant_error(CustomException.builder()
      .code(FORBIDDEN.name()),
      "Operation {0} is not allowed for the current user ::: {1}, because of missing data ::: {2}", 4003);


  private final CustomException.CustomExceptionBuilder builder;
  private final String message;
  private final int errorCode;

  public CustomException.CustomExceptionBuilder getBuilder(String... paramArray) {
    return builder
        .message(MessageFormat.format(message, (Object[]) paramArray))
        .code(this.name());
  }
}
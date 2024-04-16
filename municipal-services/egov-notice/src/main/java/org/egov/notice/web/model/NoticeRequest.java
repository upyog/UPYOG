package org.egov.notice.web.model;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NoticeRequest   {
  @JsonProperty("RequestInfo")
  private org.egov.notice.web.model.RequestInfo RequestInfo;

  @Valid
  @JsonProperty("notice")
  private Notice notice ;



}
package org.egov.pgr.web.models;

import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Email {

	private Set<String> emailTo;

	private String subject;

	private String body;

	@JsonProperty("isHTML")
	private boolean isHTML;

	private List<String> fileStoreIds;
}
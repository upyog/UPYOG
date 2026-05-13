package org.egov.waterconnection.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.Set;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Email {

	private Set<String> emailTo;
	private String subject;
	private String body;
	private List<String> attachments;
	@JsonProperty("isHTML")
	private boolean isHTML;

}

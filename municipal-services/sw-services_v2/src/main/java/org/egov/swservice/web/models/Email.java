package org.egov.swservice.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Set;

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

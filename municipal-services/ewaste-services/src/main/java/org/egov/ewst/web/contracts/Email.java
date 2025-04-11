package org.egov.ewst.web.contracts;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Email class represents the structure of an email object.
 * It contains details such as recipients, subject, body, and whether the email is in HTML format.
 */
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
	@JsonProperty("isHTML")
	private boolean isHTML;

}

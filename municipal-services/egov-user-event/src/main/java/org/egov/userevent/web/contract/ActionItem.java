package org.egov.userevent.web.contract;

import jakarta.validation.constraints.NotNull;

import org.egov.userevent.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Validated
@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
@Builder
public class ActionItem {
	
	@NotNull
	@SanitizeHtml
	private String actionUrl;

	@NotNull
	@SanitizeHtml
	private String code;

}

package org.egov.pdf.request;

import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Role {
	private Long id;

	@Size(max = 128)
	private String name;

	@Size(max = 50)
	private String code;

	@Size(max = 256)
	private String tenantId;
}
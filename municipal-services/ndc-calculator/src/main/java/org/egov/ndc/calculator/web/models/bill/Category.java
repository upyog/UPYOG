package org.egov.ndc.calculator.web.models.bill;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Category {

	@NotNull
	private Long id;

	@NotNull
	@Size(min=2, max=100)
	private String name;

	@Size(max=250)
	private String description;

	@NotNull
	private Boolean active;

	@NotNull
	private String tenantId;

}
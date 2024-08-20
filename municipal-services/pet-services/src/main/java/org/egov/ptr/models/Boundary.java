package org.egov.ptr.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

import org.egov.ptr.models.Boundary;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Boundary
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-08-20T09:30:27.617+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Boundary {
	@JsonProperty("code")
	private String code = null;

	@JsonProperty("name")
	private String name = null;

	@JsonProperty("label")
	private String label = null;

	@JsonProperty("latitude")
	private String latitude = null;

	@JsonProperty("longitude")
	private String longitude = null;

	@JsonProperty("children")
	@Valid
	private List<Boundary> children = null;

	@JsonProperty("materializedPath")
	private String materializedPath = null;

	public Boundary addChildrenItem(Boundary childrenItem) {
		if (this.children == null) {
			this.children = new ArrayList<>();
		}
		this.children.add(childrenItem);
		return this;
	}

}

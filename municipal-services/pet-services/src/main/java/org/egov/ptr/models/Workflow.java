package org.egov.ptr.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Fields related to workflow service
 */
@Schema(description = "Fields related to workflow service")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2023-08-20T09:30:27.617+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workflow {
	@JsonProperty("action")
	private String action = null;

	@JsonProperty("status")
	private String status = null;

	@JsonProperty("comments")
	private String comments = null;

	@JsonProperty("assignes")
	@Valid
	private List<String> assignes = null;

	@JsonProperty("documents")
	@Valid
	private List<Document> documents = null;

	public Workflow addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}
		this.documents.add(documentsItem);
		return this;
	}

}

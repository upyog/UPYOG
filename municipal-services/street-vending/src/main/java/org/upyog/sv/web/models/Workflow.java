package org.upyog.sv.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.upyog.sv.web.models.workflow.Document;

import javax.validation.Valid;

/**
 * BPA application object to capture the details of land, land owners, and
 * address of the land.
 */
@ApiModel(description = "BPA application object to capture the details of land, land owners, and address of the land.")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.SpringCodegen", date = "2020-06-23T05:52:32.717Z[GMT]")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
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

	@JsonProperty("businessService")
	private String businessService = null;
	
	@JsonProperty("moduleName")
	private String moduleName = null;
	
	public Workflow addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}
		this.documents.add(documentsItem);
		return this;
	}

}

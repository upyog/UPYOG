package org.upyog.cdwm.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;
import org.upyog.cdwm.web.models.workflow.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
	
	/**
	 * Adds a document to the documents list. If the list is null, it initializes a new ArrayList.
	 * 
	 * @param documentsItem The document item to be added
	 * @return The updated Workflow object
	 */
	public Workflow addDocumentsItem(Document documentsItem) {
		if (this.documents == null) {
			this.documents = new ArrayList<>();
		}
		this.documents.add(documentsItem);
		return this;
	}
}

package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Fields related to workflow service
 */
@ApiModel(description = "Fields related to workflow service")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Workflow {

    @JsonProperty("action")
    private String action = null;

    @JsonProperty("status")
    private String status = null;

    @JsonProperty("moduleName")
    private String moduleName = null;

    @JsonProperty("businessService")
    private String businessService = null;

    @JsonProperty("assignes")
    @Valid
    private List<String> assignes = null;

    @JsonProperty("comments")
    private String comments = null;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents = null;

    @JsonProperty("varificationDocuments")
    @Valid
    private List<Document> varificationDocuments = null;


    public Workflow addDocumentsItem(Document documentsItem) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        this.documents.add(documentsItem);
        return this;
    }

}


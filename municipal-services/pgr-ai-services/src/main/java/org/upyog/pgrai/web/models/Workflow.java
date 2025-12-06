package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import org.upyog.pgrai.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
=======
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
>>>>>>> master-LTS

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * BPA application object to capture the details of land, land owners, and address of the land.
 */
<<<<<<< HEAD
@Schema(description = "BPA application object to capture the details of land, land owners, and address of the land.")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@ApiModel(description = "BPA application object to capture the details of land, land owners, and address of the land.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Workflow   {
<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("action")
        private String action = null;

        @JsonProperty("assignes")
        @Valid
        private List<String> assignes = null;

<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("comments")
        private String comments = null;

        @JsonProperty("verificationDocuments")
        @Valid
        private List<Document> verificationDocuments = null;


        public Workflow addAssignesItem(String assignesItem) {
            if (this.assignes == null) {
            this.assignes = new ArrayList<>();
            }
        this.assignes.add(assignesItem);
        return this;
        }

        public Workflow addVarificationDocumentsItem(Document verificationDocumentsItem) {
            if (this.verificationDocuments == null) {
            this.verificationDocuments = new ArrayList<>();
            }
        this.verificationDocuments.add(verificationDocumentsItem);
        return this;
        }

}


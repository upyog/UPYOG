package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import org.upyog.pgrai.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
=======
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
>>>>>>> master-LTS

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Boundary
 */
@Validated
<<<<<<< HEAD
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
=======
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Boundary   {

        @NotNull
<<<<<<< HEAD
        @SanitizeHtml
        @JsonProperty("code")
        private String code = null;

        @SanitizeHtml
        @JsonProperty("name")
        private String name = null;

        @SanitizeHtml
        @JsonProperty("label")
        private String label = null;

        @SanitizeHtml
        @JsonProperty("latitude")
        private String latitude = null;

        @SanitizeHtml
=======
        @SafeHtml
        @JsonProperty("code")
        private String code = null;

        @SafeHtml
        @JsonProperty("name")
        private String name = null;

        @SafeHtml
        @JsonProperty("label")
        private String label = null;

        @SafeHtml
        @JsonProperty("latitude")
        private String latitude = null;

        @SafeHtml
>>>>>>> master-LTS
        @JsonProperty("longitude")
        private String longitude = null;

        @JsonProperty("children")
        @Valid
        private List<Boundary> children = null;

<<<<<<< HEAD
        @SanitizeHtml
=======
        @SafeHtml
>>>>>>> master-LTS
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


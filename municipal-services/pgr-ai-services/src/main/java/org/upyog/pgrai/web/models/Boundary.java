package org.upyog.pgrai.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import org.upyog.pgrai.validation.SanitizeHtml;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Boundary
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2020-07-15T11:35:33.568+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Boundary   {

        @NotNull
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
        @JsonProperty("longitude")
        private String longitude = null;

        @JsonProperty("children")
        @Valid
        private List<Boundary> children = null;

        @SanitizeHtml
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


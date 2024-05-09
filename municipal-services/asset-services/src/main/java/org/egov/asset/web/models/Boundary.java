package org.egov.asset.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Boundary
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Boundary   {
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


package org.upyog.sv.web.models.common;

import java.time.LocalDate;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Role
 */
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role   {
        @JsonProperty("id")
        private Long id = null;

        @JsonProperty("name")
        private String name = null;

        @JsonProperty("code")
        private String code = null;

        @JsonProperty("description")
        private String description = null;

        @JsonProperty("createdBy")
        private Long createdBy = null;

        @JsonProperty("createdDate")
        private LocalDate createdDate = null;

        @JsonProperty("lastModifiedBy")
        private Long lastModifiedBy = null;

        @JsonProperty("lastModifiedDate")
        private LocalDate lastModifiedDate = null;

        @JsonProperty("tenantId")
        private String tenantId = null;


}


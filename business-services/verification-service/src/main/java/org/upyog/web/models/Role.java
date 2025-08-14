package org.upyog.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.time.LocalDate;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * Role
 */
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")

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


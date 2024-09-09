package org.egov.wf.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.egov.common.contract.request.User;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a Trade License
 */
@ApiModel(description = "A Object holds the basic data for a Trade License")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString
public class BPAEscalationInstance   {

        @Size(max=64)
        @JsonProperty("id")
        private String id = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("tenantId")
        private String tenantId = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("businessService")
        private String businessService = null;

        @NotNull
        @Size(max=128)
        @JsonProperty("businessId")
        private String businessId = null;

        @NotNull 
        @JsonProperty("authToken")
        private String authToken = null;
               
        @NotNull 
        @JsonProperty("userInfo")
        private User userInfo = null;
         

}


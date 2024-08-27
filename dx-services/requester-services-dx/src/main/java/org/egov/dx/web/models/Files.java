package org.egov.dx.web.models;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Files {


	 @JsonProperty("fileStoreId")
     @NotNull
     private String fileStoreId;

     @JsonProperty("tenantId")
     @NotNull
     private String tenantId;
  }

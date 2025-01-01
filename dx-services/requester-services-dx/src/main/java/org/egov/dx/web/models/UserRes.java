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
public class UserRes {
     
     @JsonProperty("digilockerid")
     @NotNull
     private  String digilockerId;
     
     @JsonProperty("name")
     @NotNull
     private  String name;
     
     @JsonProperty("dob")
     @NotNull
     private  String dob;
     
     @JsonProperty("gender")
     @NotNull
     private  String gender;
     
     @JsonProperty("eaadhaar")
     @NotNull
     private  String eaadhaar;
     
     @JsonProperty("reference_key")
     @NotNull
     private  String referenceKey;
     
     @JsonProperty("mobile")
     @NotNull
     private  String mobile;

    }

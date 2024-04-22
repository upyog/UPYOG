package org.egov.dx.web.models;

import java.util.List;

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
public class IssuedDocument {


	 @JsonProperty("name")
     @NotNull
     private String name;

     @JsonProperty("type")
     @NotNull
     private  String type;
     
     @JsonProperty("size")
     @NotNull
     private  String size;
     
     @JsonProperty("date")
     @NotNull
     private  String date;
     
     @JsonProperty("parent")
     @NotNull
     private  String parent;
     
     @JsonProperty("uri")
     @NotNull
     private  String uri;
     
     @JsonProperty("doctype")
     @NotNull
     private  String doctype;
     
     @JsonProperty("description")
     @NotNull
     private  String description;
     
     @JsonProperty("issuerid")
     @NotNull
     private  String issuerid;
     
     @JsonProperty("issuer")
     @NotNull
     private  String issuer;
     
     @JsonProperty("mime")
     @NotNull
     private  List<String> mime;
     
}
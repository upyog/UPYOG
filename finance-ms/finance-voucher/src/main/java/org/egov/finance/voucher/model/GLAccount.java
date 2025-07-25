package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GLAccount implements Serializable {

	private static final long serialVersionUID = 7369183258745701979L;

	private long ID;
	private String glCode;
	private String name;
	private Boolean functionRequired;

	@JsonProperty("isactiveforposting")
	private boolean isactiveforposting;

	@JsonProperty("classification")
	private Long classification;

	// ❌ DON'T use raw ArrayList
	// private ArrayList glParameters = new ArrayList();

	// Use typed list
	private List<GLParameter> glParameters = new ArrayList<>();
}

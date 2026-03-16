package org.egov.pt.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ServiceResponse {

	@JsonProperty("service")
	List<ServiceWithProperties> service;
}

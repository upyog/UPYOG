package org.egov.pt.models;

import java.util.List;
import java.util.Map;

import org.egov.pt.models.collection.Payment;
import org.egov.pt.models.collection.RevenuDataBucket;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DashboardReport {
	
	private String ulb;
	private String fromdate;
	private String todate;
	private String ward;
	private String state;
	private String module;
	private String region;
	@JsonProperty("data")
	private List<ServiceWithProperties> services;
	private Map<String, List<Assessment>> assesments;
	private Map<String, List<Payment>> payments;
	private Map<String, List<RevenuDataBucket>> penalty;
	//private List<ServiceWithProperties> revenue;

}

package org.egov.pt.models;

@lombok.Data
public class Data {
	private String date;
    private String module;
    private String ward;
    private String ulb;
    private String region;
    private String state;
    private Metrics metrics;

}

package org.egov.refund.service;

public interface RefundNumberService {

	String generateRefundNo(String moduleName,String businessService,String consumerCode);
}
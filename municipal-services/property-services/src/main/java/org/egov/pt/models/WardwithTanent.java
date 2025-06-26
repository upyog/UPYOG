package org.egov.pt.models;

import java.math.BigDecimal;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WardwithTanent {
	int count;
	String WardNo;
	String Tanentid;
	String Action;
	String Financiyalyear;
	String Usagecategory;
	BigDecimal TodaysCollection;
	BigDecimal Todayrebategiven;
	BigDecimal Todaypenaltycollection;
	BigDecimal Todayinterestcollection;
}

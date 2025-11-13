package org.egov.demand.service;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class TestDate {
	private long getDateDifference(String startDate , String endDate) {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate firstDate = LocalDate.parse(startDate, dtf);
		LocalDate secondDate = LocalDate.parse(endDate, dtf);
		long datediff=ChronoUnit.DAYS.between(firstDate, secondDate);
		return datediff;
	}

	public static void main(String[] args) {
		TestDate td =  new TestDate();
		Date currentDate = new Date(System.currentTimeMillis());
		Calendar crd = Calendar.getInstance();
		crd.setTime(currentDate);
		Integer date = crd.get(Calendar.DAY_OF_MONTH);
		Integer cuurentMonth = crd.get(Calendar.MONTH)+1;
		String updatedDate  = date.toString()+"-0"+cuurentMonth.toString()+"-"+"2023";
		System.out.println(updatedDate);
		String expiryDateQ2 = "30-09-"+crd.get(Calendar.YEAR);
		System.out.println(expiryDateQ2);
		System.out.println(td.getDateDifference( updatedDate,expiryDateQ2));
		
		

	}

}

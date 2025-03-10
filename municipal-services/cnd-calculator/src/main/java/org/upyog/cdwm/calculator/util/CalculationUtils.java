package org.upyog.cdwm.calculator.util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.calculator.config.CalculatorConfig;
import org.upyog.cdwm.calculator.web.models.AuditDetails;
import org.upyog.cdwm.calculator.web.models.CNDRequest.StatusEnum;
import org.upyog.cdwm.calculator.web.models.ResponseInfo;

import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Component
public class CalculationUtils {

    @Autowired
    private CalculatorConfig config;
    
    public final static String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Creates demand Search url based on tenanatId,businessService and ConsumerCode
     * @return demand search url
     */
    public String getDemandSearchURL(){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getDemandSearchEndpoint());
        url.append("?");
        url.append("tenantId=");
        url.append("{1}");
        url.append("&");
        url.append("businessService=");
        url.append("{2}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{3}");
        return url.toString();
    }


    /**
     * Creates generate bill url using tenantId,consumerCode and businessService
     * @return Bill Generate url
     */
    public String getBillGenerateURI(){
        StringBuilder url = new StringBuilder(config.getBillingHost());
        url.append(config.getBillGenerateEndpoint());
        url.append("?");
        url.append("tenantId=");
        url.append("{1}");
        url.append("&");
        url.append("consumerCode=");
        url.append("{2}");
        url.append("&");
        url.append("businessService=");
        url.append("{3}");

        return url.toString();
    }
    
    public static ResponseInfo createReponseInfo(final RequestInfo requestInfo, String resMsg, StatusEnum status) {

		final String apiId = requestInfo != null ? requestInfo.getApiId() : StringUtils.EMPTY;
		final String ver = requestInfo != null ? requestInfo.getVer() : StringUtils.EMPTY;
		Long ts = null;
		if (requestInfo != null)
			ts = requestInfo.getTs();
		final String msgId = requestInfo != null ? requestInfo.getMsgId() : StringUtils.EMPTY;

		ResponseInfo responseInfo = ResponseInfo.builder().apiId(apiId).ver(ver).ts(ts).msgId(msgId).resMsgId(resMsg)
				.build();

		return responseInfo;
	}

  
    
    /**
     * identify the billingBusinessService matching to the calculation FeeType
     */
	public String getBillingBusinessService( String feeType) {

		String billingBusinessService;
		switch (feeType) {
		case CalculatorConstants.APPLICATION_FEE:
			
				billingBusinessService = config.getApplicationFeeBusinessService();
			break;
		
		default:
			billingBusinessService = feeType;
			break;
		}
		return billingBusinessService;
	}
	
	/**
	* identify the billingBusinessService matching to the calculation FeeType
	*/
	public String getTaxHeadCode(String feeType) {

		String billingTaxHead;
		switch (feeType) {
		case CalculatorConstants.APPLICATION_FEE:
		
				billingTaxHead = config.getApplicationFeeTaxHead();
			break;
		
		default:
			billingTaxHead = feeType;
			break;
		}
		return billingTaxHead;
	}
	
	public static Long getCurrentTimestamp() {
		return Instant.now().toEpochMilli();
	}

	public static LocalDate getCurrentDate() {
		return LocalDate.now();
	}

	public static AuditDetails getAuditDetails(String by, Boolean isCreate) {
		Long time = getCurrentTimestamp();
		if (isCreate)
			// TODO: check if we can set lastupdated details to empty
			return AuditDetails.builder().createdBy(by).lastModifiedBy(by).createdTime(time).lastModifiedTime(time)
					.build();
		else
			return AuditDetails.builder().lastModifiedBy(by).lastModifiedTime(time).build();
	}

	/*
	 * Commented and used Instant public static Long getCurrentTimestamp() { return
	 * System.currentTimeMillis(); }
	 */

	public static String getRandonUUID() {
		return UUID.randomUUID().toString();
	}

	public static LocalDate parseStringToLocalDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);
		LocalDate localDate = LocalDate.parse(date, formatter);
		return localDate;
	}

	public static Long minusOneDay(LocalDate date) {
		return date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}
	
	// To get the current financial year end date in epoch to set in Tax to in demand
		public static long getFinancialYearEnd() {

			YearMonth currentYearMonth = YearMonth.now();
			int year = currentYearMonth.getYear();
			int month = currentYearMonth.getMonthValue();

			// If current month is Jan-March, end year should be current year
			if (month < Month.APRIL.getValue()) {
				year -= 1;
			}

			LocalDateTime endOfYear = LocalDateTime.of(year + 1, Month.MARCH, 31, 23, 59, 59, 999000000);
			return endOfYear.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();

		}


}

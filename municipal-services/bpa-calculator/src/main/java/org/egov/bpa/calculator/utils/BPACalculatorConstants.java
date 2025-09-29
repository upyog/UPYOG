package org.egov.bpa.calculator.utils;

import java.math.BigDecimal;

public class BPACalculatorConstants {

	

    public static final String MDMS_EGF_MASTER = "egf-master";

    public static final String MDMS_FINANCIALYEAR  = "FinancialYear";

    public static final String MDMS_FINACIALYEAR_PATH = "$.MdmsRes.egf-master.FinancialYear[?(@.code==\"{}\")]";

    public static final String MDMS_STARTDATE  = "startingDate";

    public static final String MDMS_ENDDATE  = "endingDate";

    public static final String MDMS_CALCULATIONTYPE = "CalculationType";

    public static final String MDMS_CALCULATIONTYPE_PATH = "$.MdmsRes.BPA.CalculationType";

    public static final String MDMS_BPA_PATH = "$.MdmsRes.BPA";

    public static final String MDMS_BPA = "BPA";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR= "financialYear";

    public static final String MDMS_CALCULATIONTYPE_FINANCIALYEAR_PATH = "$.MdmsRes.BPA.CalculationType[?(@.financialYear=='{}')]";

	public static final Object MDMS_CALCULATIONTYPE_SERVICETYPE = "serviceType";

	public static final Object MDMS_CALCULATIONTYPE_RISKTYPE = "riskType";

	public static final String MDMS_ROUNDOFF_TAXHEAD = "TL_ROUNDOFF";

	public static final String MDMS_CALCULATIONTYPE_AMOUNT = "amount";
	
	public static final String MDMS_CALCULATIONTYPE_APL_FEETYPE = "ApplicationFee";
	
	public static final String MDMS_CALCULATIONTYPE_SANC_FEETYPE = "SanctionFee";

	public static final String LOW_RISK_PERMIT_FEE_TYPE = "LOW_RISK_PERMIT_FEE";

	public static final String MDMS_CALCULATIONTYPE_LOW_SANC_FEETYPE = "Low_SanctionFee";

	public static final String MDMS_CALCULATIONTYPE_LOW_APL_FEETYPE = "Low_ApplicationFee";
	
	// Error messages in BPA Calculator
	
	public static final String PARSING_ERROR = "PARSING ERROR";
	
	public static final String INVALID_AMOUNT = "INVALID AMOUNT";
	
	public static final String INVALID_UPDATE = "INVALID UPDATE";
	
	public static final String INVALID_ERROR = "INVALID ERROR";
	
	public static final String INVALID_APPLICATION_NUMBER = "INVALID APPLICATION NUMBER";
	
	public static final String EDCR_ERROR = "EDCR_ERROR";
	
	public static final String CALCULATION_ERROR = "CALCULATION ERROR";
	
	public static final String MDMS_CURRENT_FINANCIAL_YEAR = "$.MdmsRes.egf-master.FinancialYear[?(@.module==\"{}\")]";
	
	public static final String MDMS_BPA_LOW = "BPA_LOW";
	
	public static final BigDecimal SQYARD_TO_SQFEET = BigDecimal.valueOf(9.0);
	
	public static final BigDecimal SQMETER_TO_SQYARD = BigDecimal.valueOf(1.19599);
	
	public static final String MDMS_CHARGES_TYPE  = "ChargesType";
	
	public static final String MDMS_NAME  = "name";
	
	public static final String MDMS_CHARGES_TYPE_CODE  = "101";
	
	public static final String MDMS_CHARGES_TYPE_PATH  = "$.MdmsRes.BPA.ChargesType.*.Charges.*.[?( @.applicable==true )]";
	
	public static final String BPA_PROCESSING_FEES = "BPA_PROCESSING_FEES";

	public static final String BPA_CLU_CHARGES = "BPA_CLU_CHARGES";

	public static final String BPA_EXTERNAL_DEVELOPMENT_CHARGES = "BPA_EXTERNAL_DEVELOPMENT_CHARGES";

	public static final String BPA_URBAN_DEVELOPMENT_CESS = "BPA_URBAN_DEVELOPMENT_CESS";

	public static final String BPA_MALBA_CHARGES = "BPA_MALBA_CHARGES";

	public static final String BPA_WATER_CHARGES = "BPA_WATER_CHARGES";

	public static final String BPA_MINING_CHARGES = "BPA_MINING_CHARGES";

	public static final String BPA_GAUSHALA_CHARGES_CESS = "BPA_GAUSHALA_CHARGES_CESS";

	public static final String BPA_LABOUR_CESS = "BPA_LABOUR_CESS";

	public static final String BPA_RAIN_WATER_HARVESTING_CHARGES = "BPA_RAIN_WATER_HARVESTING_CHARGES";

	public static final String BPA_CLUBBING_CHARGES = "BPA_CLUBBING_CHARGES";

	public static final String BPA_SUB_DIVISION_CHARGES = "BPA_SUB-DIVISION_CHARGES";
	
	public static final String BPA_LESS_ADJUSMENT_PLOT = "BPA_LESS_ADJUSMENT_PLOT";
	
	public static final String BPA_DEVELOPMENT_CHARGES = "BPA_DEVELOPMENT_CHARGES";

	public static final String BPA_OTHER_CHARGES = "BPA_OTHER_CHARGES";
	
}

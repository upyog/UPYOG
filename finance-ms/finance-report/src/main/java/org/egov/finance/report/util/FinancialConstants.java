
package org.egov.finance.report.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * @author bdhal
 */
public interface FinancialConstants {

	public static final String alphaNumericwithspecialchar = "[0-9a-zA-Z-& :,/.()@]+";
	public static final String alphaNumericwithspecialcharForContraWOAndSupplierName = "[a-zA-Z0-9 @#&/\\\\()-.*,\":]+";
	public static final String alphaNumericwithoutspecialchar = "[0-9a-zA-Z/]+";
	public static final String numericwithoutspecialchar = "[0-9-]+";
	public static final String ALPHANUMERICWITHALLSPECIALCHAR = "[0-9a-zA-Z_@./#&+-/!(){}\",^$%*|=;:<>?`~ ]+";
	public static final int AMOUNT_SCALE = 2;// scale value for BigDecimal
												// having amounts
	// bill types ie module types used in egw_status
	public static final String SALARYBILL = "SALBILL";
	// this will be used by External Systems
	public static final String CONTINGENCYBILL = "CBILL";
	// this is the one which is used by financials from 13/03/2010
	public static final String CONTINGENCYBILL_FIN = "EXPENSEBILL";
	public static final String SUPPLIERBILL = "PURCHBILL";
	public static final String CONTRACTORBILL = "WORKSBILL";
	public static final String PENSIONBILL = "PENSIONBILL";
	public static final String SBILL = "SBILL";
	public static final String CONTRACTOR_BILL = "CONTRACTORBILL";

	// Standard Bill Types or Expernditure type

	public static final String STANDARD_EXPENDITURETYPE_CONTINGENT = "Expense";
	public static final String STANDARD_EXPENDITURETYPE_WORKS = "Works";
	public static final String STANDARD_EXPENDITURETYPE_PURCHASE = "Purchase";
	public static final String STANDARD_EXPENDITURETYPE_SALARY = "Salary";
	public static final String STANDARD_EXPENDITURETYPE_PENSION = "Pension";

	public static final String BILLTYPE_FINAL_BILL = "Final Bill";
	public static final String BILLTYPE_PART_BILL = "Part Bill";

	public static final String STANDARD_BILLTYPE_FINALBILL = "FinalBill";
	public static final String STANDARD_BILLTYPE_RUNNINGBILL = "RunningBill";
	// Status for Salary Bills
	public static final String SALARYBILL_CREATED_STATUS = "Created";
	public static final String SALARYBILL_CANCELLED_STATUS = "Cancelled";
	public static final String SALARYBILL_APPROVED_STATUS = "Approved";
	public static final String SALARYBILL_PASSED_STATUS = "Passed";
	public static final String SALARYBILL_PAID_STATUS = "paid";
	// Status for Contingent Bills
	public static final String CONTINGENCYBILL_CREATED_STATUS = "CREATED";
	public static final String CONTINGENCYBILL_CANCELLED_STATUS = "Cancelled";
	public static final String CONTINGENCYBILL_APPROVED_STATUS = "Approved";
	public static final String CONTINGENCYBILL_PASSED_STATUS = "Voucher Created";
	public static final String CONTINGENCYBILL_PAID_STATUS = "payment confirmed";
	public static final String CONTINGENCYBILL_REJECTED_STATUS = "Rejected";
	// Status for Supplier Bills
	public static final String SUPPLIERBILL_CREATED_STATUS = "Created";
	public static final String SUPPLIERBILL_CANCELLED_STATUS = "Cancelled";
	public static final String SUPPLIERBILL_APPROVED_STATUS="Approved";
	public static final String SUPPLIERBILL_PASSED_STATUS = "Passed";
	public static final String SUPPLIERBILL_PAID_STATUS = "Paid";
	public static final String SUPPLIERBILL_REJECTED_STATUS = "Rejected";
	// Status for Contrator Bills
	public static final String CONTRACTORBILL_CREATED_STATUS = "CREATED";
	public static final String CONTRACTORBILL_CANCELLED_STATUS = "CANCELLED";
	public static final String CONTRACTORBILL_APPROVED_STATUS = "APPROVED";
	public static final String CONTRACTORBILL_PASSED_STATUS = "Passed";
	public static final String CONTRACTORBILL_PAID_STATUS = "Paid";
	public static final String CONTRACTORBILL_REJECTED_STATUS = "REJECTED";
	

	// Status for Pension Bills
	public static final String PENSIONBILL_CANCELLED_STATUS = "Cancelled";

	// vouchertypes
	public final String APPLCONFIGNAME = "egf_config.xml";
	public final String CATEGORYFORVNO = "vouchernumberformat";
	public final String CATEGORYFORDEFALULTPURPOSEID = "defaultValues";

	/*
	 * public static final String RECEIPT_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "receipt", "", CATEGORYFORVNO); public
	 * static final String PAYMENT_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "payment", "", CATEGORYFORVNO); public
	 * static final String JOURNAL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "journal", "", CATEGORYFORVNO); public
	 * static final String CONTRA_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "contra", "", CATEGORYFORVNO); public
	 * static final String PURCHBILL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "purchasejv", "", CATEGORYFORVNO);
	 * public static final String WORKSBILL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "worksjv", "", CATEGORYFORVNO); public
	 * static final String FIXEDASSET_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "fixedassetjv", "", CATEGORYFORVNO);
	 * public static final String SALBILL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "salaryjv", "", CATEGORYFORVNO);
	 * public static final String PENBILL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "pensionjv", "", CATEGORYFORVNO);
	 * public static final String CBILL_VOUCHERNO_TYPE =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "contingentjv", "", CATEGORYFORVNO);
	 * 
	 * public static final String VOUCHERNO_TYPE_LENGTH =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "length", "", CATEGORYFORVNO); public
	 * static final String VOUCHERNO_TYPE_SUBLENGTH =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "sublength", "", CATEGORYFORVNO);
	 * 
	 * // default glcodes for transactions public static final String
	 * SALBILL_DEFAULT_PURPOSEID = EGovConfig.getProperty(APPLCONFIGNAME,
	 * "salaryBillDefaultPurposeId", "", CATEGORYFORDEFALULTPURPOSEID); public
	 * static final String PURCHBILL_DEFAULT_PURPOSEID =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "purchaseBillDefaultPurposeId", "",
	 * CATEGORYFORDEFALULTPURPOSEID); public static final String
	 * WORKSBILL_DEFAULT_PURPOSEID = EGovConfig.getProperty(APPLCONFIGNAME,
	 * "worksBillDefaultPurposeId", "", CATEGORYFORDEFALULTPURPOSEID); public static
	 * final String CBILL_DEFAULT_PURPOSEID = EGovConfig.getProperty(APPLCONFIGNAME,
	 * "cBillDefaultPurposeId", "", CATEGORYFORDEFALULTPURPOSEID);
	 * 
	 * public static final String ModulesForBillAcctModify =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "MODULESFORBILLSACCOUNTINGMODIFY", "",
	 * "general"); public static final String CATEGORFORGLCODE = "AccountCode";
	 * 
	 * // COA GLCODE Details
	 * 
	 * public static final String GLCODEMAXLENGTH =
	 * EGovConfig.getProperty(APPLCONFIGNAME, "glcodeMaxLength", "",
	 * CATEGORFORGLCODE);
	 */
	// Instrument or Cheque related
	public static final String INSTRUMENT_DEPOSITED_STATUS = "Deposited";
	public static final String INSTRUMENT_CREATED_STATUS = "New";
	public static final String INSTRUMENT_RECONCILED_STATUS = "Reconciled";
	public static final String INSTRUMENT_DISHONORED_STATUS = "Dishonored";
	public static final String INSTRUMENT_CANCELLED_STATUS = "Cancelled";
	public static final String INSTRUMENT_SURRENDERED_STATUS = "Surrendered";
	public static final String INSTRUMENT_SURRENDERED_FOR_REASSIGN_STATUS = "Surrender_For_Reassign";
	public static final String INSTRUMENT_INWORKFLOW_STATUS = "InWorkflow";// dishonour
																			// cheque
																			// in
																			// workflow
	public static final String INSTRUMENT_APPROVED_STATUS = "Approved";
	public static final String STATUS_MODULE_INSTRUMENT = "Instrument";

	public static final String STATUS_MODULE_DISHONORCHEQUE = "DISHONORCHEQUE";
	public static final String DISHONORCHEQUE_CREATED_STATUS = "Created";
	public static final String DISHONORCHEQUE_APPROVED_STATUS = "Approved";
	public static final String DISHONORCHEQUE_CANCELLED_STATUS = "Cancelled";
	public static final String DISHONORCHEQUE_REJECTED_STATUS = "Rejected";

	public static final String INSTRUMENT_TYPE_CHEQUE = "cheque";
	public static final String INSTRUMENT_TYPE_CASH = "cash";
	public static final String INSTRUMENT_TYPE_CARD = "card";
	public static final String INSTRUMENT_TYPE_DD = "dd";
	public static final String INSTRUMENT_TYPE_BANK = "bankchallan";
	public static final String INSTRUMENT_TYPE_ADVICE = "advice";
	public static final String INSTRUMENT_TYPE_ONLINE = "online";
	public static final String INSTRUMENT_TYPE_ATM = "atm";
	public static final String INSTRUMENT_TYPE_BANK_TO_BANK = "banktobank";
	public static final String INSTRUMENT_TYPE_ECS = "ecs";

	public static final String IS_PAYCHECK_ONE = "1";
	public static final String IS_PAYCHECK_ZERO = "0";

	// Standard Voucher types
	public static final String STANDARD_VOUCHER_TYPE_CONTRA = "Contra";
	public static final String STANDARD_VOUCHER_TYPE_PAYMENT = "Payment";
	public static final String STANDARD_VOUCHER_TYPE_RECEIPT = "Receipt";
	public static final String STANDARD_VOUCHER_TYPE_JOURNAL = "Journal Voucher";
	// Contra related - Voucher Names
	public static final String CONTRAVOUCHER_NAME_BTOB = "BankToBank";
	public static final String CONTRAVOUCHER_NAME_BTOC = "BankToCash";
	public static final String CONTRAVOUCHER_NAME_CTOB = "CashToBank";
	public static final String CONTRAVOUCHER_NAME_PAYIN = "Pay in slip";
	public static final String CONTRAVOUCHER_NAME_INTERFUND = "InterFundTransfer";
	// Payment related - Voucher Names
	public static final String PAYMENTVOUCHER_NAME_BILL = "Bill Payment";
	public static final String PAYMENTVOUCHER_NAME_ADVANCE = "Advance Payment";
	public static final String PAYMENTVOUCHER_NAME_DIRECTBANK = "Direct Bank Payment";
	public static final String PAYMENTVOUCHER_NAME_REMITTANCE = "Remittance Payment";
	public static final String PAYMENTVOUCHER_NAME_SALARY = "Salary Bill Payment";
	public static final String PAYMENTVOUCHER_NAME_PENSION = "Pension Bill Payment";
	public static final String INTERESTVOUCHER_NAME_BANKENTRY = "Bank Entry";
	public static final String BANKCHARGESVOUCHER_NAME_BANKENTRY = "Bank Entry";

	// Journal -Voucher Names
	public static final String JOURNALVOUCHER_NAME_GENERAL = "JVGeneral";
	public static final String JOURNALVOUCHER_NAME_ISSUE = "Issue";
	public static final String JOURNALVOUCHER_NAME_SUPPLIERJOURNAL = "Supplier Journal";
	public static final String JOURNALVOUCHER_NAME_SUPPLIERRECEIPT = "Supplier Receipt";
	public static final String JOURNALVOUCHER_NAME_CONTRACTORJOURNAL = "Contractor Journal";
	public static final String JOURNALVOUCHER_NAME_SALARYJOURNAL = "Salary Journal";
	public static final String JOURNALVOUCHER_NAME_FIXEDASSETJOURNAL = "Fixedasset Journal";
	public static final String JOURNALVOUCHER_NAME_PURCHASEJOURNAL = "Purchase Journal";
	public static final String JOURNALVOUCHER_NAME_EXPENSEJOURNAL = "Expense Journal";
	public static final String JOURNALVOUCHER_NAME_PENSIONJOURNAL = "Pension Journal";
	public static final String JOURNALVOUCHER_NAME_LE_DEMAND = "LE-Demand-Voucher";
	public static final String JOURNALVOUCHER_NAME_RECEIPT_REVERSAL = "Receipt Reversal";

	public static final Integer REVERSALVOUCHERSTATUS = 2;
	public static final Integer REVERSEDVOUCHERSTATUS = 1;
	public static final Integer CANCELLEDVOUCHERSTATUS = 4;
	public static final Integer PREAPPROVEDVOUCHERSTATUS = 5;
	public static final Integer CREATEDVOUCHERSTATUS = 0;
	// Receipt Names
	public static final String RECEIPT_NAME_DIRECT = "Direct";
	public static final String RECEIPT_NAME_REMITTANCE_PAYMENT = "Remittance Payment";
	public static final String RECEIPT_NAME_PAYMENT_REVERSAL = "Payment Reversal";
	public static final String RECEIPT_NAME_OTHER_RECEIPTS = "Other Receipts";

	// Direct bank payment and payments
	public static final String MODEOFPAYMENT_CHEQUE = "cheque";
	public static final String MODEOFPAYMENT_CASH = "cash";
	public static final String MODEOFPAYMENT_RTGS = "rtgs";
	// Receipt
	public static final String MODEOFCOLLECTION_CHEQUE = "cheque";
	public static final String MODEOFCOLLECTION_OTHER = "other";
	public static final String MODEOFCOLLECTION_BANK = "bank";
	public static final String CBILL_DEFAULTCHECKLISTNAME = "Others";

	public static final String MULTIPLE = "MULTIPLE";
	public static final String MODULE_NAME_APPCONFIG = "EGF";
	public static final String WORKFLOWENDSTATE = "END";
	public static final String KEY_BILLNUMBER_APPCONFIG = "Bill_Number_Geneartion_Auto";
	public static final String KEY_DEFAULTTXNMISATTRRIBUTES = "DEFAULTTXNMISATTRRIBUTES";
	public static final String KEY_CONTINGENCYBILLPURPOSEIDS = "contingencyBillPurposeIds";
	public static final String KEY_CONTINGENCYBILLDEFAULTPURPOSEID = "contingencyBillDefaultPurposeId";

	public static final String TYPEOFACCOUNT_PAYMENTS = "PAYMENTS";
	public static final String TYPEOFACCOUNT_RECEIPTS = "RECEIPTS";
	public static final String TYPEOFACCOUNT_RECEIPTS_PAYMENTS = "RECEIPTS_PAYMENTS";
	public static final String APPCONFIG_COA_MAJORCODE_LENGTH = "coa_majorcode_length";
	public static final String APPCONFIG_COA_MINORCODE_LENGTH = "coa_minorcode_length";
	public static final String APPCONFIG_COA_DETAILCODE_LENGTH = "coa_detailcode_length";
	public static final String APPCONFIG_COA_MAJORCODE_CAPITAL_EXP_FIE_REPORT = "coa_major_capital_exp_fie_report";
	public static final String APPCONFIG_BUDGETGROUP_RANGE = "budgetgroup_range_minor_or_detailed";
	public static final String APPCONFIG_EXCLUDE_STATUS = "exclude_status_forbudget_actual";
	public static final String APPCONFIG_AUTO_BANKACCOUNT_GLCODE = "auto_bankaccount_glcode";
	public static final String DELIMITER_FOR_VOUCHER_STATUS_TO_CHECK_BANK_BALANCE = ",";
	public static final String APPCONFIG_BILLACCOUNTING_ACCROSS_YEARS = "allow_billsaccounting_across_years";
	public static final String APPCONFIG_BILLACCOUNTING_ACCROSS_YEARS_ENDDATE = "billsaccounting_accrossyears_enddate";

	public static final String BANKACCOUNT_WALKIN_PAYMENT_DESCRIPTION = "Walkin payment account";

	// For Receipt Payment Report
	// public static final String YEARLY = "Yearly";
	public static final String RECEIPTS = "Receipts";
	public static final String OPERATING_RECEIPTS = "Operating Receipts";
	public static final String PAYMENTS = "Payments";
	public static final String OPERATING_PAYMENTS = "Operating Payments";

	public static final String SUPERUSER = "super user";
	public static final String REMITTANCE_SCHEDULER_LOG_STATUS_FAILURE = "failure";
	public static final String REMITTANCE_SCHEDULER_LOG_STATUS_SUCCESS = "success";
	public static final String REMITTANCE_SCHEDULER_LOG_STATUS_PARTIAL = "partial";
	public static final Character REMITTANCE_SCHEDULER_SCHEDULAR_TYPE_AUTO = 'A';
	public static final Character REMITTANCE_SCHEDULER_SCHEDULAR_TYPE_MANUAL = 'M';

	public static final String INCOMETAX_CAPITAL = "350200101";
	public static final String INCOMETAX_REVENUE = "350200102";
	public static final String SALESTAX_CAPITAL = "350200201";
	public static final String SALESTAX_REVENUE = "350200202 ";
	public static final String MWGWF_MAINTENANCE = "350200402";
	public static final String MWGWF_CAPITAL = "350200403";
	public static final String SERVICETAX_REVENUE = "350200301";

	public static final String STRUTS_RESULT_PAGE_SEARCH = "search";
	public static final String STRUTS_RESULT_PAGE_VIEW = "view";
	public static final String STRUTS_RESULT_PAGE_NEW = "new";
	public static final String STRUTS_RESULT_PAGE_EDIT = "edit";
	public static final String STRUTS_RESULT_PAGE_UNIQUECHECK = "uniqueCheck";
	public static final String REQUIRED = "Required";
	public static final String STRUTS_RESULT_PAGE_RESULT = "result";

	public static final String REGEXP_ALPHANUMERIC_DOT_SLASH = "^[a-zA-Z0-9/.]+$";
	public static final String REGEXP_ALPHANUMERIC_DOT_COLON_SLASH = "^[a-zA-Z0-9.:/]+$";

	// billSubTypes
	public static final String BILLSUBTYPE_TNEBBILL = "TNEB";

	public static final String EXCLUDED_BILL_TYPES = "'" + BILLSUBTYPE_TNEBBILL + "'";

	public static final String TNEB_MODULETYPE = "TNEB Bill";
	// TNEB Regions
	public static final String REGION_SE_CEDC_NORTH = "SE/CEDC/NORTH";
	public static final String REGION_SE_CEDC_SOUTH = "SE/CEDC/SOUTH";
	public static final String REGION_SE_CEDC_CENTRAL = "SE/CEDC/CENTRAL";
	public static final String REGION_SE_CEDC_EAST = "SE/CEDC/EAST";
	public static final String REGION_SE_CEDC_WEST = "SE/CEDC/WEST";
	// TNEB Voucher Propertys
	public static final String EB_VOUCHER_PROPERTY_FUND = "EB Voucher Property-Fund";
	public static final String EB_VOUCHER_PROPERTY_FUNCTION = "EB Voucher Property-Function";
	public static final String EB_VOUCHER_PROPERTY_DEPARTMENT = "EB Voucher Property-Department";
	public static final String EB_VOUCHER_PROPERTY_BANK = "EB Voucher Property-Bank";
	public static final String EB_VOUCHER_PROPERTY_BANKACCOUNT = "EB Voucher Property-BankAccount";
	public static final String EB_VOUCHER_PROPERTY_BANKBRANCH = "EB Voucher Property-BankBranch";
	public static final String SCHEDULER_STATUS_SCHEDULED = "Scheduled";
	public static final String SCHEDULER_STATUS_RUNNING = "Running";
	public static final String SCHEDULER_STATUS_COMPLETED = "Completed";
	public static final String STATUS_FAILURE = "Failed";
	public static final String STATUS_SUCCESS = "Success";

	public static final String REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT = "Remove Entries With Zero Amount in Report";

	// after this date or if this date is null will use financial year wise
	// rolling sequence for RTGS numbering
	public static final Date RTGS_FINYEAR_WISE_ROLLING_SEQ_CUTOFF_DATE = new GregorianCalendar(1900 + 115, 2, 31)
			.getTime();

	public static final String BUTTONAPPROVE = "Approve";
	public static final String BUTTONFORWARD = "Forward";
	public static final String BUTTONREJECT = "Reject";
	public static final String BUTTONSAVE = "Save";
	public static final String BUTTONCANCEL = "Cancel";
	public static final String BUTTONVERIFY = "Verify";

	public static final String WORKFLOW_STATE_NEW = "NEW";
	public static final String WORKFLOW_STATE_APPROVED = "Approved";
	public static final String WORKFLOW_STATE_REJECTED = "Rejected";
	public static final String WORKFLOW_STATE_FORWARDED = "Forwarded";
	public static final String WORKFLOW_STATE_CANCELLED = "Cancelled";
	public static final String WORKFLOW_STATE_CREATED = "Created";

	public static final String WF_STATE_FINAL_APPROVAL_PENDING = "Final Approval Pending";
	public static final String WF_STATE_EOA_Approval_Pending = "EOA Approval Pending";
	public static final String WF_STATE_COMM_Approval_Pending = "Commissioner Approval Pending";

	public static final String CREATEANDAPPROVE = "Create And Approve";
	public static final String BUDGET_CHECK_ERROR_MESSAGE = "Budgetary check is failed";

	public static final String BUDGET = "BUDGET";
	public static final String BUDGETDETAIL = "BUDGETDETAIL";
	public static final String BUDGETDETAIL_CREATED_STATUS = "Created";
	public static final String BUDGETDETAIL_VERIFIED_STATUS = "VERIFIED";
	public static final String WORKFLOWTYPE_EXPENSE_BILL_DISPLAYNAME = "Expense Bill";
	public static final String WORKFLOWTYPE_WORKS_BILL_DISPLAYNAME = "Works Bill";
	public static final String WORKFLOWTYPE_SBILL_DISPLAYNAME = "Supplier Bill";
	public static final String WORKFLOWTYPE_VOUCHER_DISPLAYNAME = "Voucher";

	public static final String WORKFLOW_STATUS_CODE_REJECTED = "REJECTED";
	public static final String KEY_DATAENTRYCUTOFFDATE = "DataEntryCutOffDate";
	public static final String NA = "na";
	public static final String MANUAL = "Manual";
	public static final String AUTO = "Auto";
	public static final String GENERAL = "General";
	public static final String STANDARD_SUBTYPE_FIXED_ASSET = "Fixed Asset";
	public static final String BILL_EDIT_DESIGNATIONS = "BILL_EDIT_DESIGNATIONS";

	public static final String VOUCHER_TYPE_JOURNAL = "Journal";
	public static final String VOUCHER_TYPE_WORKS = "worksjv";
	public static final String VOUCHER_TYPE_PURCHASE = "purchasejv";
	public static final String VOUCHER_TYPE_SALARY = "salaryjv";
	public static final String VOUCHER_TYPE_CONTINGENT = "contingentjv";
	public static final String VOUCHER_TYPE_FIXEDASSET = "fixedassetjv";
	public static final String VOUCHER_TYPE_PENSION = "pensionjv";
	public static final String BALANCE_CHECK_CONTROL_TYPE = "Balance Check Control Type";
	public static final String MANDATORY = "mandatory";
	public static final String WARNING = "warning";
	public static final String NONE = "none";
	public static final String MODE_OF_PAYMENT = "Mode of Payment";

	public static final String FINANCIAL_VOUCHER_INDEX_NAME = "financialsvoucherdata";
	public static final String FINANCIAL_OPENINGBALANCE_INDEX_NAME = "financialsobdata";
	public static final String FINANCIAL_BUDGET_INDEX_DATA = "financialsbudgetdata";
	public static final SimpleDateFormat DATEFORMATTER_YYYY_MM_DD = new SimpleDateFormat("yyyy-MM-dd");

	public static final String FUND_NAME = "fundName";
	public static final String DEPARTMENT_NAME = "departmentName";
	public static final String FUNCTION_NAME = "functionName";
	public static final String SCHEME_NAME = "schemeName";
	public static final String SUBSCHEME_NAME = "subSchemeName";

	public static final String BUDGETAPPROVEDAMOUNT = "budgetapprovedamount";
	public static final String REAPPROPRIATIONAMOUNT = "reappropriationamount";
	public static final String TOTALBUDGET = "totalbudget";
	public static final String ACTUALAMOUNT = "actualamount";
	public static final String PREVIOUYEARACTUALAMOUNT = "previouyearactualamount";
	public static final String COMMITTEDEXPENDITURE = "committedexpenditure";
	public static final String BUDGETVARIANCE = "budgetvariance";
	public static final String BUDGETDETAILCREATEDDATE = "budgetdetailcreateddate";

	public static final String DISTNAME = "distname";
	public static final String ULBNAME = "ulbname";
	public static final String ULBGRADE = "ulbgrade";
	public static final String REGNAME = "regname";
	public static final String MAJORCODEDESCRIPTION = "majorCodeDescription";
	public static final String MINORCODEDESCRIPTION = "minorCodeDescription";
	public static final String DETAILEDCODEDESCRIPTION = "glcodeDescription";

	public static final String VOUCHERFUNDNAME = "voucherfundname";
	public static final String VOUCHERMISDEPARTMENTNAME = "vouchermisdepartmentname";
	public static final String VOUCHERMISSCHEMENAME = "vouchermisschemename";
	public static final String VOUCHERMISSUBSCHEMENAME = "vouchermissubschemename";
	public static final String VOUCHERMISFUNCTIONNAME = "vouchermisfunctionname";

	public static final String FUND_CODE = "fundCode";
	public static final String DEPARTMENT_CODE = "departmentCode";
	public static final String FUNCTION_CODE = "functionCode";
	public static final String SCHEME_CODE = "schemeCode";
	public static final String SUBSCHEME_CODE = "subSchemeCode";

	public static final String FILESTORE_MODULECODE = "EGF";
	public static final String FILESTORE_MODULEOBJECT = "egBillRegister";
	public static final String BANK_STATEMET_OBJECT = "bankStatement";

	public static final String BUDGETTYPE_CREDIT = "credit";
	public static final String BUDGETTYPE_DEBIT = "debit";
	public static final String BUDGETTYPE_ALL = "all";
	public static final String STATUS_MODULE_NAME_CONTRACTOR = "Contractor";
	public static final String STATUS_MODULE_NAME_SUPPLIER = "Supplier";
	public static final Integer FINANCIALYEAR_STARTING_MONTH = 4;
	public static final String EMPTY_STRING = "";

	
}

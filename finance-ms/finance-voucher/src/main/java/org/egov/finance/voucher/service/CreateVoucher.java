package org.egov.finance.voucher.service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.egov.finance.voucher.daoimpl.AccountdetailtypeHibernateDAO;
import org.egov.finance.voucher.daoimpl.VouchermisHibernateDAO;
import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.Boundary;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.FinancialYear;
import org.egov.finance.voucher.entity.FiscalPeriod;
import org.egov.finance.voucher.entity.Fund;
import org.egov.finance.voucher.entity.Vouchermis;
import org.egov.finance.voucher.enumeration.FinanceEventType;
import org.egov.finance.voucher.enumeration.VoucherSubTypeEnum;
import org.egov.finance.voucher.enumeration.VoucherTypeEnum;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.Transaxtion;
import org.egov.finance.voucher.repository.AccountDetailKeyRepository;
import org.egov.finance.voucher.repository.AccountDetailTypeRepository;
import org.egov.finance.voucher.repository.CChartOfAccountDetailRepository;
import org.egov.finance.voucher.repository.ChartOfAccountsRepository;
import org.egov.finance.voucher.repository.DepartmentRepository;
import org.egov.finance.voucher.repository.FinancialYearRepository;
import org.egov.finance.voucher.repository.FiscalPeriodRepository;
import org.egov.finance.voucher.repository.FunctionRepository;
import org.egov.finance.voucher.repository.FunctionaryRepository;
import org.egov.finance.voucher.repository.FundRepository;
import org.egov.finance.voucher.repository.FundsourceRepository;
import org.egov.finance.voucher.repository.RecoveryRepository;
import org.egov.finance.voucher.repository.SchemeRepository;
import org.egov.finance.voucher.repository.SubSchemeRepository;
import org.egov.finance.voucher.repository.VoucherRepository;
import org.egov.finance.voucher.repository.VouchermisRepository;
import org.egov.finance.voucher.util.FinancialConstants;
import org.egov.finance.voucher.util.VoucherConstant;
import org.egov.finance.voucher.validation.VoucherValidation;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CreateVoucher {

	// Expenditure Types
	private final static String CONBILL = "Works";
	private final static String SUPBILL = "Purchase";
	private final static String SALBILL = "Salary";
	private final static String PENSBILL = "Pension";
	private final static String GRATBILL = "Gratuity";
	// messages
	private final static String FUNDMISSINGMSG = "Fund is not used in Bill ,cannot create Voucher";
	private static final String FAILED = "Transaction failed";
	private static final String EXCEPTION_WHILE_SAVING_DATA = "Exception while saving data";
	private final String ISREQUIRED = ".required";
	private final String SELECT = "  Please Select  ";
	private static final String DD_MMM_YYYY = "dd-MMM-yyyy";
	private static final String DD_MM_YYYY = "dd/MM/yyyy";
	private SimpleDateFormat sdf = new SimpleDateFormat(DD_MM_YYYY);
	private SimpleDateFormat formatter = new SimpleDateFormat(DD_MMM_YYYY);

	@Autowired
	private FinanceDashboardService finDashboardService;

	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Autowired
	private AccountdetailtypeHibernateDAO accountdetailtypeHibernateDAO;

	@Autowired
	private VoucherTypeForULB voucherTypeForULB;
	@Autowired
	private FundRepository fundRepository;
	@Autowired
	private SchemeRepository schemeRepository;
	@Autowired
	private SubSchemeRepository subSchemeRepository;
	@Autowired
	private FundsourceRepository fundsourceRepository;
	@Autowired
	private DepartmentRepository departmentRepository;
	@Autowired
	private FunctionRepository functionRepository;

	@Autowired
	private AccountDetailTypeRepository accountDetailTypeRepository;

	@Autowired
	private ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private BoundaryService boundary;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private VouchermisRepository voucherMISRepository;

	@Autowired
	private ChartOfAccountService chartOfAccountService;

	@Autowired
	private FinancialPeriodService financialPeriodService;

	@Autowired
	private FinancialYearRepository financialYearRepository;

	@Autowired
	private FiscalPeriodRepository fiscalPeriodRepository;

	@Autowired
	private FunctionaryRepository functionaryRepository;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	private GenericSequenceNumberGenerator genericSequenceNumberGenerator;

	@Autowired
	private RecoveryRepository recoveryRepository;

	@Autowired
	private CChartOfAccountDetailRepository cChartOfAccountDetailRepository;

	@Autowired
	private AccountDetailKeyRepository accountDetailKeyRepository;

	@Autowired
	private VouchermisHibernateDAO vmisHibernateDao;
	@Autowired
	private VoucherValidation voucherValidation;

	@Autowired
	private TransactionService transactionService;

	private static final Logger LOGGER = LoggerFactory.getLogger(CreateVoucher.class);

	@Transactional
	public CVoucherHeader createVoucher(final Map<String, Object> headerDetails,
			final List<Map<String, Object>> accountdetails, final List<Map<String, Object>> subledgerDetails)
			throws ApplicationRuntimeException, TaskFailedException {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("start | createVoucher API");
		}

		CVoucherHeader vh;
		Vouchermis mis;

		try {
			// Step 1: Validate inputs using VoucherValidation class
			voucherValidation.validate(headerDetails, accountdetails, subledgerDetails);

			// Step 2: Create Voucher Header and Voucher MIS
			vh = createVoucherHeader(headerDetails);
			mis = createVouchermis(headerDetails);
			mis.setVoucherheaderid(vh);
			vh.setVouchermis(mis);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("start | insertIntoVoucherHeader");
			}

			// Fetch fiscal period
			FiscalPeriod fp = fiscalPeriodRepository.findByVoucherDateBetween(vh.getVoucherDate());
			if (fp == null) {
				throw new ApplicationRuntimeException(
						"Voucher Date not within an open period or Financial year not open for posting");
			}

			vh.setFiscalPeriodId(fp.getId().intValue());
			vh.setFiscalName(fp.getName());

			vh.setCgvn(voucherValidation.getCGVNNumber(vh));
			final String vdt = formatter.format(vh.getVoucherDate());

			// Check voucher number uniqueness
			if (!isUniqueVN(vh.getVoucherNumber(), vdt)) {
				throw new ValidationException(
						Arrays.asList(new ValidationError("Duplicate Voucher Number", "Duplicate Voucher Number")));
			}

			// Save voucher header
			voucherRepository.save(vh);
			log.info("Saved voucher with ID: {}", vh.getId());

			// Update source path if needed
			if (vh.getVouchermis().getSourcePath() != null && vh.getModuleId() == null && vh.getVouchermis()
					.getSourcePath().length() == vh.getVouchermis().getSourcePath().indexOf("=") + 1) {

				StringBuffer sourcePath = new StringBuffer(vh.getVouchermis().getSourcePath())
						.append(vh.getId().toString());
				vh.getVouchermis().setSourcePath(sourcePath.toString());
				voucherRepository.save(vh);
			}

			// Create and post transactions
			// List<Transaxtion> transactions = createTransaction(headerDetails,
			// accountdetails, subledgerDetails, vh);
			List<Transaxtion> transactions = transactionService.createTransaction(headerDetails, accountdetails,
					subledgerDetails, vh);
			Transaxtion[] txnList = transactions.toArray(new Transaxtion[0]);

			final SimpleDateFormat formatter = new SimpleDateFormat(DD_MMM_YYYY);
			if (!chartOfAccountService.postTransactions(txnList, formatter.format(vh.getVoucherDate()))) {
				throw new ApplicationRuntimeException("Voucher creation Failed");
			}

			// Trigger event
			finDashboardService.publishEvent(FinanceEventType.voucherCreateOrUpdate, vh);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("End | createVoucher API");
			}

			return vh;

		} catch (Exception e) {
			LOGGER.error("Error during voucher creation", e);
			throw new ApplicationRuntimeException(e.getMessage());
		}
	}

	@Transactional
	public CVoucherHeader createVoucherHeader(final Map<String, Object> headerDetails) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("START | createVoucherHeader");
		// Connection con = null;
		Query query = null;
		final CVoucherHeader cVoucherHeader = new CVoucherHeader();
		try {
			// String voucherSubType="";
			cVoucherHeader.setName(headerDetails.get(VoucherConstant.VOUCHERNAME).toString());
			String voucherType = headerDetails.get(VoucherConstant.VOUCHERTYPE).toString();
			cVoucherHeader.setType(headerDetails.get(VoucherConstant.VOUCHERTYPE).toString());
			String vNumGenMode = null;

			// -- Voucher Type checking. --START
			if (FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL.equalsIgnoreCase(voucherType))
				vNumGenMode = voucherTypeForULB.readVoucherTypes("Journal");
			else
				vNumGenMode = voucherTypeForULB.readVoucherTypes(voucherType);
			// --END --
			voucherType = voucherType.toUpperCase().replaceAll(" ", "");

			String voucherSubType = null;
			if (headerDetails.get(VoucherConstant.VOUCHERSUBTYPE) != null) {
				voucherSubType = (String) headerDetails.get(VoucherConstant.VOUCHERSUBTYPE);
				voucherSubType = voucherSubType.toUpperCase().replaceAll(" ", "");
			}

			// why it is type,subtype where api expects subtype,type ?
			// if()
			final String voucherNumberPrefix = getVoucherNumberPrefix(voucherType, voucherSubType);
			String voucherNumber = null;
			if (headerDetails.get(VoucherConstant.DESCRIPTION) != null)
				cVoucherHeader.setDescription(headerDetails.get(VoucherConstant.DESCRIPTION).toString());
			final Date voucherDate = (Date) headerDetails.get(VoucherConstant.VOUCHERDATE);
			cVoucherHeader.setVoucherDate(voucherDate);
			Fund fundByCode = fundRepository.findByCode(headerDetails.get(VoucherConstant.FUNDCODE).toString());

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Voucher Type is :" + voucherType);
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("vNumGenMode is  :" + vNumGenMode);

			if (headerDetails.get(VoucherConstant.VOUCHERNUMBER) != null)
				cVoucherHeader.setVoucherNumber(headerDetails.get(VoucherConstant.VOUCHERNUMBER).toString());
			if (null != headerDetails.get(VoucherConstant.MODULEID))
				vNumGenMode = "Auto";

			cVoucherHeader.setFundId(fundByCode);
			if (vNumGenMode.equals("Auto")) {
				cVoucherHeader.setVoucherNumberPrefix(voucherNumberPrefix);
				VouchernumberGenerator v = beanResolver.getAutoNumberServiceFor(VouchernumberGenerator.class);

				final String strVoucherNumber = v.getNextNumber(cVoucherHeader);

				cVoucherHeader.setVoucherNumber(strVoucherNumber);
			}
			/*
			 * if("Auto".equalsIgnoreCase(vNumGenMode) || null !=
			 * headerdetails.get(VoucherConstant.MODULEID)){ if(LOGGER.isDebugEnabled())
			 * LOGGER.debug( "Generating auto voucher number"); SimpleDateFormat df = new
			 * SimpleDateFormat(DD_MM_YYYY); String vDate = df.format(voucherDate);
			 * cVoucherHeader.setVoucherNumber(cmImpl.getTxnNumber
			 * (fundId.toString(),voucherNumberPrefix,vDate,con)); }else { voucherNumber =
			 * headerdetails.get(VoucherConstant.VOUCHERNUMBER).toString();
			 * query=persistenceService.getSession().createQuery(
			 * "select f.identifier from Fund f where id=:fundId");
			 * query.setInteger("fundId", fundId); String fundIdentifier =
			 * query.uniqueResult().toString(); cVoucherHeader.setVoucherNumber(new
			 * StringBuffer().append(fundIdentifier ).append(voucherNumberPrefix).
			 * append(voucherNumber).toString()); }
			 */

			if (headerDetails.containsKey(VoucherConstant.MODULEID)
					&& null != headerDetails.get(VoucherConstant.MODULEID)) {
				cVoucherHeader.setModuleId(Integer.valueOf(headerDetails.get(VoucherConstant.MODULEID).toString()));
				cVoucherHeader.setIsConfirmed(Integer.valueOf(1));
			} else {
				// Fix Me
				/*
				 * PersistenceService<AppConfig, Integer> appConfigSer; appConfigSer = new
				 * PersistenceService<AppConfig, Integer>(); appConfigSer.setSessionFactory(new
				 * SessionFactory()); appConfigSer.setType(AppConfig.class); AppConfig
				 * appConfig= (AppConfig) appConfigSer.find( "from AppConfig where key_name =?",
				 * "JournalVoucher_ConfirmonCreate"); if(null != appConfig && null!=
				 * appConfig.getValues() ){ for (AppConfigValues appConfigVal :
				 * appConfig.getValues()) { cVoucherHeader.
				 * setIsConfirmed(Integer.valueOf(appConfigVal.getValue())); } }
				 */
			}

			if (headerDetails.containsKey(VoucherConstant.STATUS) && null != headerDetails.get(VoucherConstant.STATUS))
				cVoucherHeader.setStatus(Integer.valueOf(headerDetails.get(VoucherConstant.STATUS).toString()));
			else {
				final List list = appConfigValuesService.getConfigValuesByModuleAndKey("EGF",
						"DEFAULTVOUCHERCREATIONSTATUS");
				cVoucherHeader.setStatus(Integer.parseInt(((AppConfigValues) list.get(0)).getValue()));
			}

//			if (null != headerdetails.get(VoucherConstant.ORIGIONALVOUCHER)) {
//
//				final Long origionalVId = Long
//						.parseLong(headerdetails.get(VoucherConstant.ORIGIONALVOUCHER).toString());
//				query = persistenceService.getSession().createQuery("from CVoucherHeader where id=:id");
//				query.setLong("id", origionalVId);
//				if (query.list().size() == 0)
//					throw new ApplicationRuntimeException("Not a valid origional voucherheader id");
//				else
//					cVoucherHeader.setOriginalvcId(origionalVId);
//			}

			if (headerDetails.get(VoucherConstant.ORIGIONALVOUCHER) != null) {
				Long originalVId = Long.parseLong(headerDetails.get(VoucherConstant.ORIGIONALVOUCHER).toString());

				Optional<CVoucherHeader> optionalOriginalVoucher = voucherRepository.findById(originalVId);

				if (!optionalOriginalVoucher.isPresent()) {
					throw new ApplicationRuntimeException("Not a valid original voucherheader id");
				} else {
					cVoucherHeader.setOriginalvcId(originalVId);
				}
			}

			cVoucherHeader.setRefvhId((Long) headerDetails.get(VoucherConstant.REFVOUCHER));
			cVoucherHeader.setEffectiveDate(new Date());
			Object billNumber = headerDetails.get(VoucherConstant.BILLNUMBER);
			cVoucherHeader.setBillNumber(billNumber != null ? billNumber.toString() : "");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Printing Voucher Details------------------------------------------------------------------------------");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(cVoucherHeader.toString());
			if (LOGGER.isDebugEnabled())
				LOGGER.debug(
						"Printing Voucher Details------------------------------------------------------------------------------");
		} catch (final ValidationException e) {
			LOGGER.error(e.getMessage());
			throw e;
		} /*
			 * catch (final Exception e) { LOGGER.error(e); throw new
			 * Exception(e.getMessage()); }
			 */
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("END | createVoucherHeader");
		return cVoucherHeader;
	}

	private String getVoucherNumberPrefix(final String type, String vsubtype) {

		// if sub type is null use type
		if (vsubtype == null)
			vsubtype = type;
		String subtype = vsubtype.toUpperCase().trim();
		String voucherNumberPrefix = null;
		String typetoCheck = subtype;

		if (subtype.equalsIgnoreCase("JOURNAL VOUCHER"))
			typetoCheck = "JOURNALVOUCHER";

		switch (VoucherSubTypeEnum.valueOf(typetoCheck)) {
		case JVGENERAL:
			voucherNumberPrefix = FinancialConstants.JOURNAL_VOUCHERNO_TYPE;
			break;
		case JOURNALVOUCHER:
			voucherNumberPrefix = FinancialConstants.JOURNAL_VOUCHERNO_TYPE;
			break;
		case CONTRA:
			voucherNumberPrefix = FinancialConstants.CONTRA_VOUCHERNO_TYPE;
			break;
		case RECEIPT:
			voucherNumberPrefix = FinancialConstants.RECEIPT_VOUCHERNO_TYPE;
			break;
		case PAYMENT:
			voucherNumberPrefix = FinancialConstants.PAYMENT_VOUCHERNO_TYPE;
			break;
		case PURCHASEJOURNAL:
			voucherNumberPrefix = FinancialConstants.PURCHBILL_VOUCHERNO_TYPE;
			break;
		case WORKS:
			voucherNumberPrefix = FinancialConstants.WORKSBILL_VOUCHERNO_TYPE;
			break;
		case CONTRACTORJOURNAL:
			voucherNumberPrefix = FinancialConstants.WORKSBILL_VOUCHERNO_TYPE;
			break;
		case WORKSJOURNAL:
			voucherNumberPrefix = FinancialConstants.WORKSBILL_VOUCHERNO_TYPE;
			break;
		case FIXEDASSETJOURNAL:
			voucherNumberPrefix = FinancialConstants.FIXEDASSET_VOUCHERNO_TYPE;
			break;
		case CONTINGENTJOURNAL:
			voucherNumberPrefix = FinancialConstants.CBILL_VOUCHERNO_TYPE;
			break;
		case PURCHASE:
			voucherNumberPrefix = FinancialConstants.PURCHBILL_VOUCHERNO_TYPE;
			break;
		case EXPENSEJOURNAL:
			voucherNumberPrefix = FinancialConstants.CBILL_VOUCHERNO_TYPE;
			break;
		case EXPENSE:
			voucherNumberPrefix = FinancialConstants.CBILL_VOUCHERNO_TYPE;
			break;
		case SALARYJOURNAL:
			voucherNumberPrefix = FinancialConstants.SALBILL_VOUCHERNO_TYPE;
			break;
		case SALARY:
			voucherNumberPrefix = FinancialConstants.SALBILL_VOUCHERNO_TYPE;
			break;
		case FIXEDASSET:
			voucherNumberPrefix = FinancialConstants.FIXEDASSET_VOUCHERNO_TYPE;
			break;
		case PENSIONJOURNAL:
			voucherNumberPrefix = FinancialConstants.PENBILL_VOUCHERNO_TYPE;
			break;
		case PENSION:
			voucherNumberPrefix = FinancialConstants.PENBILL_VOUCHERNO_TYPE;
			break;
		default: // if subtype is invalid then use type
			if (voucherNumberPrefix == null)
				voucherNumberPrefix = checkwithvouchertype(type);
			break;
		}
		return voucherNumberPrefix;

	}

	private String checkwithvouchertype(final String type) {
		String typetoCheck = type;
		if (type.equalsIgnoreCase("JOURNAL VOUCHER"))
			typetoCheck = "JOURNALVOUCHER";
		String voucherNumberPrefix = null;
		switch (VoucherTypeEnum.valueOf(typetoCheck)) {
		case JOURNALVOUCHER:
			voucherNumberPrefix = FinancialConstants.JOURNAL_VOUCHERNO_TYPE;
			break;
		case CONTRA:
			voucherNumberPrefix = FinancialConstants.CONTRA_VOUCHERNO_TYPE;
			break;
		case RECEIPT:
			voucherNumberPrefix = FinancialConstants.RECEIPT_VOUCHERNO_TYPE;
			break;
		case PAYMENT:
			voucherNumberPrefix = FinancialConstants.PAYMENT_VOUCHERNO_TYPE;
			break;
		default:// do nothing
			break;
		}
		return voucherNumberPrefix;

	}

	@Transactional
	public Vouchermis createVouchermis(final Map<String, Object> headerDetails) throws ApplicationRuntimeException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("START | createVouchermis");
		final Vouchermis vouchermis = new Vouchermis();
		if (headerDetails.containsKey(VoucherConstant.DEPARTMENTCODE)
				&& null != headerDetails.get(VoucherConstant.DEPARTMENTCODE)) {
			final String departmentCode = headerDetails.get(VoucherConstant.DEPARTMENTCODE).toString();
			vouchermis.setDepartmentcode(departmentCode);
		}
		if (headerDetails.containsKey(VoucherConstant.SCHEMECODE)
				&& null != headerDetails.get(VoucherConstant.SCHEMECODE)) {
			final String schemecode = headerDetails.get(VoucherConstant.SCHEMECODE).toString();
			// old vouchermis.setSchemeid(schemeDAO.getSchemeByCode(schemecode));
			vouchermis.setSchemeid(schemeRepository.findByCode(schemecode));// new
		}
		if (headerDetails.containsKey(VoucherConstant.SUBSCHEMECODE)
				&& null != headerDetails.get(VoucherConstant.SUBSCHEMECODE)) {
			final String subschemecode = headerDetails.get(VoucherConstant.SUBSCHEMECODE).toString();
			// old
			// vouchermis.setSubschemeid(subSchemeDAO.getSubSchemeByCode(subschemecode));
			vouchermis.setSubschemeid(subSchemeRepository.findByCode(subschemecode));// new
		}

		if (headerDetails.containsKey(VoucherConstant.FUNDSOURCECODE)
				&& null != headerDetails.get(VoucherConstant.FUNDSOURCECODE)) {
			final String fundsourcecode = headerDetails.get(VoucherConstant.FUNDSOURCECODE).toString();
			// old
			// vouchermis.setFundsource(fundSourceDAO.getFundSourceByCode(fundsourcecode));
			vouchermis.setFundsource(fundsourceRepository.findByCode(fundsourcecode));// new
		}
		if (null != headerDetails.get(VoucherConstant.FUNCTIONARYCODE))
//	old		vouchermis.setFunctionary(functionaryDAO.getFunctionaryByCode(
//					BigDecimal.valueOf(Long.valueOf(headerdetails.get(VoucherConstant.FUNCTIONARYCODE).toString()))));
			vouchermis.setFunctionary(functionaryRepository.findByCode(
					BigDecimal.valueOf(Long.valueOf(headerDetails.get(VoucherConstant.FUNCTIONARYCODE).toString()))));// new
		if (headerDetails.containsKey(VoucherConstant.FUNCTIONCODE)
				&& null != headerDetails.get(VoucherConstant.FUNCTIONCODE)) {
			final String functionCode = headerDetails.get(VoucherConstant.FUNCTIONCODE).toString();
			// old vouchermis.setFunction(functionDAO.getFunctionByCode(functionCode));
			vouchermis.setFunction(functionRepository.findByCode(functionCode));
		}

		if (null != headerDetails.get(VoucherConstant.SOURCEPATH))
			vouchermis.setSourcePath(headerDetails.get(VoucherConstant.SOURCEPATH).toString());
		if (headerDetails.containsKey(VoucherConstant.DIVISIONID)
				&& headerDetails.get(VoucherConstant.DIVISIONID) != null) {

			Long divisionId = Long.parseLong(headerDetails.get(VoucherConstant.DIVISIONID).toString());

			Boundary division = boundary.getBoundaryById(divisionId)
					.orElseThrow(() -> new RuntimeException("Boundary not found for ID: " + divisionId));

			vouchermis.setDivisionid(division);
		}
		if (headerDetails.containsKey(VoucherConstant.BUDGETCHECKREQ)
				&& null != headerDetails.get(VoucherConstant.BUDGETCHECKREQ))
			vouchermis.setBudgetCheckReq((Boolean) headerDetails.get(VoucherConstant.BUDGETCHECKREQ));
		else
			vouchermis.setBudgetCheckReq(true);
		if (headerDetails.containsKey(VoucherConstant.REFERENCEDOC)
				&& null != headerDetails.get(VoucherConstant.REFERENCEDOC)) {
			final String referencedoc = headerDetails.get(VoucherConstant.REFERENCEDOC).toString();
			vouchermis.setReferenceDocument(referencedoc);
		}
		if (headerDetails.containsKey(VoucherConstant.SERVICE_NAME)
				&& null != headerDetails.get(VoucherConstant.SERVICE_NAME)) {
			final String serviceName = headerDetails.get(VoucherConstant.SERVICE_NAME).toString();
			vouchermis.setServiceName(serviceName);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("END | createVouchermis");
		return vouchermis;
	}

	public boolean isUniqueVN(String vcNum, final String vcDateStr) {
		boolean isUnique = false;

		try {
			vcNum = vcNum.toUpperCase();
			Date vcDate = formatter.parse(vcDateStr);

			Optional<FinancialYear> fyOpt = financialYearRepository.findByDateInRange(vcDate);
			if (fyOpt.isPresent()) {
				FinancialYear fy = fyOpt.get();
				List<CVoucherHeader> matches = voucherRepository.findDuplicateVouchers(vcNum, fy.getStartingDate(),
						fy.getEndingDate());
				isUnique = matches.isEmpty();
			} else {
				throw new ApplicationRuntimeException("No Financial Year found for date: " + vcDateStr);
			}
		} catch (ParseException ex) {
			LOGGER.error("Error parsing date: " + vcDateStr, ex);
			throw new ApplicationRuntimeException("Error parsing voucher date", ex);
		}

		return isUnique;
	}

}

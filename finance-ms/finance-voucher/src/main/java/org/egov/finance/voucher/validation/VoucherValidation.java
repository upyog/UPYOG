package org.egov.finance.voucher.validation;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.finance.voucher.daoimpl.AccountdetailtypeHibernateDAO;
import org.egov.finance.voucher.daoimpl.VouchermisHibernateDAO;
import org.egov.finance.voucher.entity.AccountDetailKey;
import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.Department;
import org.egov.finance.voucher.entity.FiscalPeriod;
import org.egov.finance.voucher.entity.Fund;
import org.egov.finance.voucher.entity.Fundsource;
import org.egov.finance.voucher.entity.Scheme;
import org.egov.finance.voucher.entity.SubScheme;
import org.egov.finance.voucher.enumeration.VoucherTypeEnum;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.FunctionaryModel;
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
import org.egov.finance.voucher.service.AppConfigValueService;
import org.egov.finance.voucher.service.AutonumberServiceBeanResolver;
import org.egov.finance.voucher.service.BoundaryService;
import org.egov.finance.voucher.service.ChartOfAccountService;
import org.egov.finance.voucher.service.FinanceDashboardService;
import org.egov.finance.voucher.service.FinancialPeriodService;
import org.egov.finance.voucher.service.GenericSequenceNumberGenerator;
import org.egov.finance.voucher.service.VoucherTypeForULB;
import org.egov.finance.voucher.util.FinancialConstants;
import org.egov.finance.voucher.util.VoucherConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherValidation {

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

	private static final Logger LOGGER = LoggerFactory.getLogger(VoucherValidation.class);

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

	public void validate(Map<String, Object> headerDetails, List<Map<String, Object>> accountDetails,
			List<Map<String, Object>> subledgerDetails) throws ValidationException {

		if (headerDetails.containsKey(VoucherConstant.SERVICE_NAME)
				&& headerDetails.containsKey(VoucherConstant.REFERENCEDOC)) {
			String serviceName = headerDetails.get(VoucherConstant.SERVICE_NAME).toString();
			String referenceDocument = headerDetails.get(VoucherConstant.REFERENCEDOC).toString();
			validateReferenceDocument(referenceDocument, serviceName);
		}

		validateMandateFields(headerDetails);
		validateLength(headerDetails);
		validateVoucherMIS(headerDetails);
		validateTransaction(accountDetails, subledgerDetails);
		validateFunction(headerDetails, accountDetails);
	}

	public void validateReferenceDocument(String referenceDocument, String serviceName) {
		List<CVoucherHeader> cVoucherHeaders = vmisHibernateDao
				.getRecentVoucherByServiceNameAndReferenceDoc1(serviceName, referenceDocument);
		if (cVoucherHeaders != null && cVoucherHeaders.stream().anyMatch(vh -> vh.getStatus() != 4)) {
			throw new ApplicationRuntimeException("Already voucher exists ("
					+ cVoucherHeaders.stream().map(CVoucherHeader::getVoucherNumber).collect(Collectors.toSet())
					+ ") for service " + serviceName + " with reference number " + referenceDocument + ".");
		}
	}

	private void validateMandateFields(Map<String, Object> headerDetails) {
		List<String> headerMandateFields = getHeaderMandateFields();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Inside Validate Method");
		checkMandatoryField("vouchernumber", headerDetails.get(VoucherConstant.VOUCHERNUMBER), headerDetails,
				headerMandateFields);
		checkMandatoryField("voucherdate", headerDetails.get(VoucherConstant.VOUCHERDATE), headerDetails,
				headerMandateFields);
		checkMandatoryField("fund", headerDetails.get(VoucherConstant.FUNDCODE), headerDetails, headerMandateFields);
		checkMandatoryField("department", headerDetails.get(VoucherConstant.DEPARTMENTCODE), headerDetails,
				headerMandateFields);
		checkMandatoryField("scheme", headerDetails.get(VoucherConstant.SCHEMECODE), headerDetails,
				headerMandateFields);
		checkMandatoryField("subscheme", headerDetails.get(VoucherConstant.SUBSCHEMECODE), headerDetails,
				headerMandateFields);
		checkMandatoryField("functionary", headerDetails.get(VoucherConstant.FUNCTIONARYCODE), headerDetails,
				headerMandateFields);
		// checkMandatoryField("function",headerdetails.get(VoucherConstant.FUNCTIONCODE),headerdetails);
		checkMandatoryField("fundsource", headerDetails.get(VoucherConstant.FUNDSOURCECODE), headerDetails,
				headerMandateFields);
		checkMandatoryField("field", headerDetails.get(VoucherConstant.DIVISIONID), headerDetails, headerMandateFields);

	}

	private void checkMandatoryField(String fieldName, Object value, Map<String, Object> headerDetails,
			List<String> mandatoryFields) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Filed name :=" + fieldName + " Value = :" + value);
		String vNumGenMode = null;
		if (fieldName.equals("vouchernumber")) {
			if (headerDetails.get(VoucherConstant.VOUCHERTYPE) == null)
				throw new ValidationException(Arrays.asList(new ValidationError(
						VoucherConstant.VOUCHERTYPE + ISREQUIRED, VoucherConstant.VOUCHERTYPE + ISREQUIRED)));
			else {
				validateVoucherType(headerDetails.get(VoucherConstant.VOUCHERTYPE).toString());
				if (FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL
						.equalsIgnoreCase(headerDetails.get(VoucherConstant.VOUCHERTYPE).toString()))
					vNumGenMode = voucherTypeForULB.readVoucherTypes("Journal");
				else
					vNumGenMode = voucherTypeForULB
							.readVoucherTypes(headerDetails.get(VoucherConstant.VOUCHERTYPE).toString());
				if (!"Auto".equalsIgnoreCase(vNumGenMode) && (value == null || ((String) value).isEmpty())
						&& headerDetails.get(VoucherConstant.MODULEID) == null)
					throw new ValidationException(
							Arrays.asList(new ValidationError(SELECT + fieldName, SELECT + fieldName)));
			}
		} else if (mandatoryFields.contains(fieldName) && (value == null || StringUtils.isEmpty(value.toString())))
			throw new ValidationException(Arrays.asList(new ValidationError(SELECT + fieldName, SELECT + fieldName)));

	}

	private void validateVoucherType(String vouType) {
		String voucherType = vouType.toUpperCase().replaceAll(" ", "");
		boolean typeFound = false;
		final VoucherTypeEnum[] allvoucherTypeEnum = VoucherTypeEnum.values();
		for (final VoucherTypeEnum voucherTypeEnum : allvoucherTypeEnum)
			if (voucherTypeEnum.toString().equalsIgnoreCase(voucherType)) {
				typeFound = true;
				break;
			}
		if (!typeFound)
			throw new ApplicationRuntimeException("Voucher type is not valid");

	}

	private List<String> getHeaderMandateFields() {
		List<String> mandatoryFields = new ArrayList<String>();
		final List<AppConfigValues> appConfigList = appConfigValuesService
				.getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG, "DEFAULTTXNMISATTRRIBUTES");

		for (final AppConfigValues appConfigVal : appConfigList) {
			final String value = appConfigVal.getValue();
			final String header = value.substring(0, value.indexOf("|"));
			final String mandate = value.substring(value.indexOf("|") + 1);
			if (mandate.equalsIgnoreCase("M"))
				mandatoryFields.add(header);
		}
		return mandatoryFields;
	}

	private void validateLength(Map<String, Object> headerDetails) {
		if (headerDetails.get(VoucherConstant.DESCRIPTION) != null
				&& headerDetails.get(VoucherConstant.DESCRIPTION).toString().length() > 250)
			throw new ValidationException(Arrays.asList(
					new ValidationError("voucher.description.exceeds.max.length", "Narration exceeds maximum length")));
		final String vNumGenMode = voucherTypeForULB
				.readVoucherTypes(headerDetails.get(VoucherConstant.VOUCHERTYPE).toString());
		if (!vNumGenMode.equals("Auto") && headerDetails.get(VoucherConstant.VOUCHERNUMBER) != null) {
			final int typeLength = Integer.valueOf(FinancialConstants.VOUCHERNO_TYPE_LENGTH);
			final int voucherNumberColumnLength = 30;// length in the db
			final int fundIdentfierLength = 1;
			if (headerDetails.get(VoucherConstant.VOUCHERNUMBER).toString().length() > voucherNumberColumnLength
					- (typeLength + fundIdentfierLength)) {
				final String voucheNumberErrMsg = " VoucherNumber length should be lessthan "
						+ (voucherNumberColumnLength - (typeLength + fundIdentfierLength));
				throw new ValidationException(
						Arrays.asList(new ValidationError("voucher.number.exceeds.max.length", voucheNumberErrMsg)));
			}
		}

	}

	private void validateVoucherMIS(Map<String, Object> headerDetails) {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("START | validateVoucherMIS");
		String deptCode = (String) headerDetails.get(VoucherConstant.DEPARTMENTCODE);
		if (deptCode != null && !deptCode.trim().isEmpty()) {
			Department department = departmentRepository.findByCode(deptCode);
			if (department == null)
				throw new ApplicationRuntimeException("Not a valid Department");
		}

		if (null != headerDetails.get(VoucherConstant.FUNCTIONARYCODE)) {
			final FunctionaryModel functionary = FunctionaryRepository.getFunctionaryByCode(
					BigDecimal.valueOf(Long.valueOf(headerDetails.get(VoucherConstant.FUNCTIONARYCODE).toString())));
			if (null == functionary)
				throw new ApplicationRuntimeException("not a valid functionary");
		}
		// validate fund.
		String fundCode = null;
		Fund fund = null;
		if (headerDetails.containsKey(VoucherConstant.FUNDCODE)
				&& null != headerDetails.get(VoucherConstant.FUNDCODE)) {
			fundCode = headerDetails.get(VoucherConstant.FUNDCODE).toString();
			fund = fundRepository.findByCode(fundCode);
			if (null == fund)
				throw new ApplicationRuntimeException("not a valid fund");
		} else
			throw new ApplicationRuntimeException("fund value is missing");
		// validate Scheme
		Scheme scheme = null;
		if (headerDetails.containsKey(VoucherConstant.SCHEMECODE)
				&& null != headerDetails.get(VoucherConstant.SCHEMECODE)) {
			final String schemecode = headerDetails.get(VoucherConstant.SCHEMECODE).toString();
			scheme = schemeRepository.findByCode(schemecode);
			if (null == scheme)
				throw new ApplicationRuntimeException("not a valid scheme");
			if (!fund.getId().equals(scheme.getFund().getId()))
				throw new ApplicationRuntimeException("This scheme does not belong to this fund");
		}
		// validate subscheme
		SubScheme subScheme = null;
		if (headerDetails.containsKey(VoucherConstant.SUBSCHEMECODE)
				&& null != headerDetails.get(VoucherConstant.SUBSCHEMECODE)) {
			final String subSchemeCode = headerDetails.get(VoucherConstant.SUBSCHEMECODE).toString();
			subScheme = subSchemeRepository.findByCode(subSchemeCode);
			if (null == subScheme)
				throw new ApplicationRuntimeException("not a valid subscheme");
			if (!subScheme.getScheme().getId().equals(scheme.getId()))
				throw new ApplicationRuntimeException("This subscheme does not belong to this scheme");
		}
		// validate fundsource
		if (headerDetails.containsKey(VoucherConstant.FUNDSOURCECODE)
				&& null != headerDetails.get(VoucherConstant.FUNDSOURCECODE)) {
			final Fundsource fundsource = fundsourceRepository
					.findByCode(headerDetails.get(VoucherConstant.FUNDSOURCECODE).toString());

			if (null == fundsource)
				throw new ApplicationRuntimeException("not a valid fund source");
		}

		if (headerDetails.containsKey(VoucherConstant.DIVISIONID)
				&& null != headerDetails.get(VoucherConstant.DIVISIONID))
			if (null == boundary
					.getBoundaryById(Long.parseLong(headerDetails.get(VoucherConstant.DIVISIONID).toString())))
				throw new ApplicationRuntimeException("not a valid divisionid");
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("END | validateVoucherMIS");
	}

	public void validateTransaction(final List<Map<String, Object>> accountDetails,
			final List<Map<String, Object>> subledgerDetails) throws ApplicationRuntimeException {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("START | validateTransaction");
		// List<Transaxtion> transaxtionList = new ArrayList<Transaxtion>();
		BigDecimal totaldebitAmount = BigDecimal.valueOf(0);
		BigDecimal totalcreditAmount = BigDecimal.valueOf(0);
		if (accountDetails.isEmpty()) {
			ValidationError error = new ValidationError("Account details Missing", "Account details Missing");
			throw new ValidationException(Arrays.asList(error));
		}

		// Filter out account code entries with both debit and credit zero
//		List<HashMap<String, Object>> validAccountCodeDetails = accountcodedetails.stream()
//		    .filter(map -> {
//		        BigDecimal dr = new BigDecimal(map.get(VoucherConstant.DEBITAMOUNT).toString());
//		        BigDecimal cr = new BigDecimal(map.get(VoucherConstant.CREDITAMOUNT).toString());
//		        return dr.compareTo(BigDecimal.ZERO) != 0 || cr.compareTo(BigDecimal.ZERO) != 0;
//		    }).collect(Collectors.toList());

		final Map<String, BigDecimal> accDetAmtMap = new HashMap<String, BigDecimal>();
		for (final Map<String, Object> accDetailMap : accountDetails) {

			String glcode = null;

			final BigDecimal debitAmount = new BigDecimal(accDetailMap.get(VoucherConstant.DEBITAMOUNT).toString());
			final BigDecimal creditAmount = new BigDecimal(accDetailMap.get(VoucherConstant.CREDITAMOUNT).toString());

//			  // Skip GL lines with zero amounts
//		    if (debitAmount.compareTo(BigDecimal.ZERO) == 0 && creditAmount.compareTo(BigDecimal.ZERO) == 0)
//		        continue;

			totaldebitAmount = totaldebitAmount.add(debitAmount);
			totalcreditAmount = totalcreditAmount.add(creditAmount);

			if (accDetailMap.containsKey(VoucherConstant.GLCODE) && null != accDetailMap.get(VoucherConstant.GLCODE)) {
				glcode = accDetailMap.get(VoucherConstant.GLCODE).toString();
//		old		if (null == chartOfAccountsDAO.getCChartOfAccountsByGlCode(glcode))
//					throw new ApplicationRuntimeException("Not a valid account code " + glcode);

				Optional<CChartOfAccounts> coaOpt = Optional.ofNullable(chartOfAccountsRepository.findByGlcode(glcode));
				if (!coaOpt.isPresent()) {
					throw new ApplicationRuntimeException("Not a valid account code " + glcode);// new
				}
			} else
				throw new ApplicationRuntimeException("glcode is missing or null");
			if (debitAmount.compareTo(BigDecimal.ZERO) != 0 && creditAmount.compareTo(BigDecimal.ZERO) != 0)
				throw new ApplicationRuntimeException(
						"Both debit amount and credit amount cannot be greater than zero");
			if (debitAmount.compareTo(BigDecimal.ZERO) == 0 && creditAmount.compareTo(BigDecimal.ZERO) == 0)
				throw new ApplicationRuntimeException("debit and credit both amount is Zero");
			if (null != accDetailMap.get(VoucherConstant.FUNCTIONCODE)
					&& "" != accDetailMap.get(VoucherConstant.FUNCTIONCODE)) {
				final String functionCode = accDetailMap.get(VoucherConstant.FUNCTIONCODE).toString();
				// old if (null == functionDAO.getFunctionByCode(functionCode))
				if (null == functionRepository.findByCode(functionCode))// new
					throw new ApplicationRuntimeException("not a valid function code");
			}
			if (debitAmount.compareTo(BigDecimal.ZERO) != 0) {
				if (null != accDetAmtMap.get(VoucherConstant.DEBIT + glcode)) {
					final BigDecimal accountCodeTotDbAmt = accDetAmtMap.get(VoucherConstant.DEBIT + glcode)
							.add(debitAmount);
					accDetAmtMap.put(VoucherConstant.DEBIT + glcode, accountCodeTotDbAmt);
				} else
					accDetAmtMap.put(VoucherConstant.DEBIT + glcode, debitAmount);

			} else if (creditAmount.compareTo(BigDecimal.ZERO) != 0)
				if (null != accDetAmtMap.get(VoucherConstant.CREDIT + glcode)) {
					final BigDecimal accountCodeTotCrAmt = accDetAmtMap.get(VoucherConstant.CREDIT + glcode)
							.add(creditAmount);
					accDetAmtMap.put(VoucherConstant.CREDIT + glcode, accountCodeTotCrAmt);
				} else
					accDetAmtMap.put(VoucherConstant.CREDIT + glcode, creditAmount);
		}
		List<String> glcodesList = new ArrayList<>();
		Set<String> creditAndDebitGlcodesList = accDetAmtMap.keySet();
		for (String glcodes : creditAndDebitGlcodesList)
			if (glcodes.contains(VoucherConstant.DEBIT)) {
				glcodesList.add(glcodes.substring(5));
			} else if (glcodes.contains(VoucherConstant.CREDIT)) {
				glcodesList.add(glcodes.substring(6));
			}
		Set<String> duplicated = glcodesList.stream().filter(i -> Collections.frequency(glcodesList, i) > 1)
				.collect(Collectors.toSet());
		LOGGER.debug("duplicated glcodes  :" + duplicated);
		if (!duplicated.isEmpty()) {
			throw new ApplicationRuntimeException(
					"An account code can be used only one time in a voucher : " + duplicated);
		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Total Debit  amount   :" + totaldebitAmount);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Total Credit amount   :" + totalcreditAmount);
		totaldebitAmount = totaldebitAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		totalcreditAmount = totalcreditAmount.setScale(2, BigDecimal.ROUND_HALF_UP);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Total Debit  amount after round off :" + totaldebitAmount);
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Total Credit amount after round off :" + totalcreditAmount);
		if (totaldebitAmount.compareTo(totalcreditAmount) != 0)
			throw new ApplicationRuntimeException("total debit and total credit amount is not matching");
		final Map<String, BigDecimal> subledAmtmap = new HashMap<String, BigDecimal>();
		for (final Map<String, Object> subdetailDetailMap : subledgerDetails) {
			String glcode = null;
			String detailtypeid = null;
			String detailKeyId = null;
			if (null != subdetailDetailMap.get(VoucherConstant.GLCODE)) {
				glcode = subdetailDetailMap.get(VoucherConstant.GLCODE).toString();
				// old if (null == chartOfAccountsDAO.getCChartOfAccountsByGlCode(glcode))
				Optional<CChartOfAccounts> coaOpt = Optional.ofNullable(chartOfAccountsRepository.findByGlcode(glcode));
				if (!coaOpt.isPresent()) {
					throw new ApplicationRuntimeException("Not a valid account code " + glcode);// new
				}

			} else
				throw new ApplicationRuntimeException("glcode is missing");
			List<Long> recoveryIds = recoveryRepository.findRecoveryIdsByGlcode(glcode);

			if (!recoveryIds.isEmpty() && subdetailDetailMap.get(VoucherConstant.TDSID) == null
					&& subdetailDetailMap.get(VoucherConstant.CREDITAMOUNT) != null
					&& new BigDecimal(subdetailDetailMap.get(VoucherConstant.CREDITAMOUNT).toString())
							.compareTo(BigDecimal.ZERO) != 0) {
				// Same logic as before
				// Possibly log or handle according to your journal voucher logic
			}
			// validate the glcode is a subledger code or not.

//			final Query query = persistenceService.getSession()
//					.createQuery("from CChartOfAccountDetail cd,CChartOfAccounts c where "
//							+ "cd.glCodeId = c.id and c.glcode=:glcode");
//
//			query.setString(VoucherConstant.GLCODE, glcode);
//			query.setCacheable(true);

			List<CChartOfAccountDetail> details = cChartOfAccountDetailRepository.findByGlcode(glcode);

			if (details == null || details.isEmpty()) {
				throw new ApplicationRuntimeException("This code is not a control code: " + glcode);
			}

			// validate subledger Detailtypeid
			if (subdetailDetailMap.get(VoucherConstant.DETAILTYPEID) == null) {
				throw new ApplicationRuntimeException("Subledger type value is missing for account code " + glcode);
			}

			detailtypeid = subdetailDetailMap.get(VoucherConstant.DETAILTYPEID).toString();
			List<CChartOfAccountDetail> list = cChartOfAccountDetailRepository.findByGlcodeAndDetailTypeId(glcode,
					Integer.valueOf(detailtypeid));

			if (list == null || list.isEmpty()) {
				throw new ApplicationRuntimeException(
						"The subledger type mapped to this account code is not correct " + glcode);
			}

			// Now it's safe to proceed to DETAILKEYID validation
			if (subdetailDetailMap.get(VoucherConstant.DETAILKEYID) != null) {
				detailKeyId = subdetailDetailMap.get(VoucherConstant.DETAILKEYID).toString();

				Optional<AccountDetailKey> detailKey = accountDetailKeyRepository
						.findByAccountDetailType_IdAndDetailkey(Integer.valueOf(detailtypeid),
								Integer.valueOf(detailKeyId));

				if (detailKey.isEmpty()) {
					throw new ApplicationRuntimeException("Subledger data is not valid for account code " + glcode);
				}
			} else {
				throw new ApplicationRuntimeException("detailkeyid is missing");
			}

			if (null != subdetailDetailMap.get(VoucherConstant.DEBITAMOUNT)
					&& new BigDecimal(subdetailDetailMap.get(VoucherConstant.DEBITAMOUNT).toString())
							.compareTo(BigDecimal.ZERO) != 0) {
				final BigDecimal dbtAmount = new BigDecimal(
						subdetailDetailMap.get(VoucherConstant.DEBITAMOUNT).toString());
				if (null != subledAmtmap.get(VoucherConstant.DEBIT + glcode))
					subledAmtmap.put(VoucherConstant.DEBIT + glcode,
							subledAmtmap.get(VoucherConstant.DEBIT + glcode).add(dbtAmount));
				else
					subledAmtmap.put(VoucherConstant.DEBIT + glcode, dbtAmount);

			} else if (null != subdetailDetailMap.get(VoucherConstant.CREDITAMOUNT)
					&& new BigDecimal(subdetailDetailMap.get(VoucherConstant.CREDITAMOUNT).toString())
							.compareTo(BigDecimal.ZERO) != 0) {
				final BigDecimal creditAmt = new BigDecimal(
						subdetailDetailMap.get(VoucherConstant.CREDITAMOUNT).toString());
				if (null != subledAmtmap.get(VoucherConstant.CREDIT + glcode))
					subledAmtmap.put(VoucherConstant.CREDIT + glcode,
							subledAmtmap.get(VoucherConstant.CREDIT + glcode).add(creditAmt));
				else
					subledAmtmap.put(VoucherConstant.CREDIT + glcode, creditAmt);

			} else
				throw new ApplicationRuntimeException("Incorrect Sub ledger amount supplied for glcode : " + glcode);

		}

		for (final Map<String, Object> accDetailMap : accountDetails) {

			final String glcode = accDetailMap.get(VoucherConstant.GLCODE).toString();

			if (null != subledAmtmap.get(VoucherConstant.DEBIT + glcode))
				// changed since equals does considers decimal values eg 20.0 is
				// not equal to 2
				if (subledAmtmap.get(VoucherConstant.DEBIT + glcode)
						.compareTo(accDetAmtMap.get(VoucherConstant.DEBIT + glcode)) != 0)
					throw new ApplicationRuntimeException(
							"Total of subleger debit amount is not matching with the account code amount " + glcode);
			if (null != subledAmtmap.get(VoucherConstant.CREDIT + glcode))
				// changed since equals does considers decimal values eg 20.0 is
				// not equal to 2
				if (subledAmtmap.get(VoucherConstant.CREDIT + glcode)
						.compareTo(accDetAmtMap.get(VoucherConstant.CREDIT + glcode)) != 0)
					throw new ApplicationRuntimeException(
							"Total of subleger credit amount is not matching with the account code amount " + glcode);

		}
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("END | validateTransaction");

	}

	private void validateFunction(Map<String, Object> headerDetails, List<Map<String, Object>> accountDetails) {
		List<AppConfigValues> configValues = appConfigValuesService.getConfigValuesByModuleAndKey(
				FinancialConstants.MODULE_NAME_APPCONFIG, "ifRestrictedToOneFunctionCenter");

		if (configValues == null || configValues.isEmpty()) {
			throw new ValidationException("Error", "Use Single Function For a transaction is not defined");
		}

		String restrictionValue = configValues.get(0).getValue();

		if ("No".equalsIgnoreCase(restrictionValue)) {
			// If function is present in header, assign it to all account code entries
			if (headerDetails.containsKey(VoucherConstant.FUNCTIONCODE)) {
				String headerFunctionCode = (String) headerDetails.get(VoucherConstant.FUNCTIONCODE);
				for (Map<String, Object> detail : accountDetails) {
					detail.put(VoucherConstant.FUNCTIONCODE, headerFunctionCode);
				}
			}
			return; // No further checks needed
		}

		// If restriction is "Yes", validate that only one function is used
		Set<String> uniqueFunctionCodes = new HashSet<>();
		boolean foundInHeader = false;
		boolean missingInDetail = false;
		String headerFunctionCode = null;

		if (headerDetails.containsKey(VoucherConstant.FUNCTIONCODE)) {
			headerFunctionCode = (String) headerDetails.get(VoucherConstant.FUNCTIONCODE);
			uniqueFunctionCodes.add(headerFunctionCode);
			foundInHeader = true;
		}

		String functionCodeFromDetails = null;

		for (Map<String, Object> detail : accountDetails) {
			Object codeObj = detail.get(VoucherConstant.FUNCTIONCODE);
			if (codeObj != null && !codeObj.toString().trim().isEmpty()) {
				String detailFunctionCode = codeObj.toString();
				uniqueFunctionCodes.add(detailFunctionCode);
				functionCodeFromDetails = detailFunctionCode;
			} else {
				missingInDetail = true;
			}
		}

		if (uniqueFunctionCodes.size() > 1) {
			throw new ValidationException(List.of(new ValidationError("multiple functions found in Header and details",
					"multiple.functions.found.in.Header.and.details")));
		}

		if (uniqueFunctionCodes.isEmpty()) {
			throw new ValidationException(List.of(new ValidationError("function not found in Header or details",
					"function.not.found.in.Header.or.details")));
		}

		if (!foundInHeader && functionCodeFromDetails != null) {
			headerDetails.put(VoucherConstant.FUNCTIONCODE, functionCodeFromDetails);
		}

		if (missingInDetail) {
			for (Map<String, Object> detail : accountDetails) {
				if (!detail.containsKey(VoucherConstant.FUNCTIONCODE)
						|| detail.get(VoucherConstant.FUNCTIONCODE) == null
						|| detail.get(VoucherConstant.FUNCTIONCODE).toString().trim().isEmpty()) {
					detail.put(VoucherConstant.FUNCTIONCODE, headerDetails.get(VoucherConstant.FUNCTIONCODE));
				}
			}
		}
	}

	public String getCGVNNumber(CVoucherHeader vh) {
		String cgvnNumber = "";

		String sequenceName = "";

		final FiscalPeriod fiscalPeriod = fiscalPeriodRepository.findByVoucherDateBetween(vh.getVoucherDate());
		if (fiscalPeriod == null)
			throw new ApplicationRuntimeException("Fiscal period is not defined for the voucher date");
		sequenceName = "sq_" + vh.getFundId().getIdentifier() + "_" + getCgnType(vh.getType()).toLowerCase() + "_cgvn_"
				+ fiscalPeriod.getName();
		Serializable nextSequence = genericSequenceNumberGenerator.getNextSequence(sequenceName);

		cgvnNumber = String.format("%s/%s/%s%010d", vh.getFundId().getIdentifier(), getCgnType(vh.getType()), "CGVN",
				nextSequence);

		return cgvnNumber;
	}

	private String getCgnType(String vouType) {
		String vType = vouType.toUpperCase().replaceAll(" ", "");
		String cgnType = null;
		String typetoCheck = vType;
		if (vType.equalsIgnoreCase("JOURNAL VOUCHER"))
			typetoCheck = "JOURNALVOUCHER";

		switch (VoucherTypeEnum.valueOf(typetoCheck.toUpperCase())) {
		case JOURNALVOUCHER:
			cgnType = "JVG";
			break;
		case CONTRA:
			cgnType = "CSL";
			break;
		case RECEIPT:
			cgnType = "MSR";
			break;
		case PAYMENT:
			cgnType = "DBP";
			break;
		default:// do nothing
			break;
		}
		return cgnType;
	}
}

package org.egov.finance.voucher.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.egov.finance.voucher.daoimpl.BudgetDetailsHibernateDAO;
import org.egov.finance.voucher.daoimpl.EgRemittanceGldtlHibernateDAO;
import org.egov.finance.voucher.daoimpl.GeneralLedgerHibernateDAO;
import org.egov.finance.voucher.daoimpl.GeneralledgerdetailHibernateDAO;
import org.egov.finance.voucher.daoimpl.TdsHibernateDAO;
import org.egov.finance.voucher.entity.AccountDetailType;
import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.egov.finance.voucher.entity.CGeneralLedger;
import org.egov.finance.voucher.entity.CGeneralLedgerDetail;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.EgRemittanceGl;
import org.egov.finance.voucher.entity.FinancialYear;
import org.egov.finance.voucher.entity.Recovery;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.model.CoaCache;
import org.egov.finance.voucher.model.EgRemittanceGldtl;
import org.egov.finance.voucher.model.GLAccount;
import org.egov.finance.voucher.model.GLParameter;
import org.egov.finance.voucher.model.Transaxtion;
import org.egov.finance.voucher.model.TransaxtionParameter;
import org.egov.finance.voucher.repository.AccountDetailTypeRepository;
import org.egov.finance.voucher.repository.CChartOfAccountDetailRepository;
import org.egov.finance.voucher.repository.CGeneralLedgerDetailRepository;
import org.egov.finance.voucher.repository.CGeneralLedgerRepository;
import org.egov.finance.voucher.repository.ChartOfAccountsRepository;
import org.egov.finance.voucher.repository.EgRemittanceGlRepository;
import org.egov.finance.voucher.repository.EgRemittanceGldtlRepository;
import org.egov.finance.voucher.repository.FinancialYearRepository;
import org.egov.finance.voucher.repository.RecoveryRepository;
import org.egov.finance.voucher.repository.VoucherRepository;
import org.egov.finance.voucher.util.ExilPrecision;
import org.egov.finance.voucher.util.FinancialConstants;
import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.spi.mapper.MappingException;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChartOfAccountService {

	static ChartOfAccountService singletonInstance;
	private static final Logger LOGGER = LoggerFactory.getLogger(ChartOfAccountService.class);

	private static final String ROOTNODE = "/COA";
	private static final String GLACCCODENODE = "GlAccountCodes";
	private static final String GLACCIDNODE = "GlAccountIds";
	private static final String ACCOUNTDETAILTYPENODE = "AccountDetailType";
	private static final String EXP = "Exp=";
	private static final String EXILRPERROR = "exilRPError";

	@Autowired
	private VoucherService voucherService;

	@Autowired
	private TdsHibernateDAO tdsHibernateDAO;

	@Autowired
	private ApplicationCacheManager applicationCacheManager;

	@Autowired
	private CoaCache coaCache;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private FinancialYearRepository financialYearRepository;

	@Autowired
	private BudgetDetailsHibernateDAO budgetDetailsDAO;

	@Autowired
	private RequiredValidator rv;

	@Autowired
	private CGeneralLedgerRepository generalLedgerRepository;

	@Autowired
	private GeneralLedgerHibernateDAO cGeneralLedgerHibernateDAO;
	@Autowired
	private EgRemittanceGldtlHibernateDAO egRemittanceGldtlHibernateDAO;

	@Autowired
	private GeneralledgerdetailHibernateDAO cGeneralledgerdetailHibernateDAO;

	@Autowired
	private CGeneralLedgerDetailRepository generalLedgerDetailRepository;

	@Autowired
	private EgRemittanceGlRepository egRemittanceGlRepository;

	@Autowired
	private ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private VoucherRepository voucherRepository;

	@Autowired
	private AccountDetailTypeRepository accountDetailTypeRepository;

	@Autowired
	private EgRemittanceGldtlRepository egRemittanceGldtlRepository;

	@Autowired
	private RecoveryRepository recoveryRepository;

	@Autowired
	private CChartOfAccountDetailRepository cChartOfAccountDetailRepository;

	@Transactional
	private boolean checkBudget(final Transaxtion txnList[]) {
		Map<String, Object> paramMap = null;
		Transaxtion txnObj = null;

		CVoucherHeader voucherHeader = null;
		for (final Transaxtion element : txnList) {
			txnObj = element;
			voucherHeader = (CVoucherHeader) voucherService.getSession().get(CVoucherHeader.class,
					Long.valueOf(txnObj.getVoucherHeaderId()));

			// this code is not working in JPA so added above line
			// voucherHeader =
			// voucherService.find("from CVoucherHeader where id = ?",
			// Long.valueOf(txnObj.voucherHeaderId));
			paramMap = new HashMap<String, Object>();
			if (txnObj.getDrAmount() == null || txnObj.getDrAmount().equals(""))
				paramMap.put("debitAmt", null);
			else
				paramMap.put("debitAmt", new BigDecimal(txnObj.getDrAmount() + ""));
			if (txnObj.getCrAmount() == null || txnObj.getCrAmount().equals(""))
				paramMap.put("creditAmt", null);
			else
				paramMap.put("creditAmt", new BigDecimal(txnObj.getCrAmount() + ""));
			if (voucherHeader.getFundId() != null)
				paramMap.put("fundid", voucherHeader.getFundId().getId());
			if (voucherHeader.getVouchermis().getDepartmentcode() != null)
				paramMap.put("deptid", voucherHeader.getVouchermis().getDepartmentcode());
			if (txnObj.getFunctionId() != null && !txnObj.getFunctionId().equals(""))
				paramMap.put("functionid", Long.valueOf(txnObj.getFunctionId()));
			if (voucherHeader.getVouchermis().getFunctionary() != null)
				paramMap.put("functionaryid", voucherHeader.getVouchermis().getFunctionary().getId());
			if (voucherHeader.getVouchermis().getSchemeid() != null)
				paramMap.put("schemeid", voucherHeader.getVouchermis().getSchemeid().getId());
			if (voucherHeader.getVouchermis().getSubschemeid() != null)
				paramMap.put("subschemeid", voucherHeader.getVouchermis().getSubschemeid().getId());
			if (voucherHeader.getVouchermis().getDivisionid() != null)
				paramMap.put("boundaryid", voucherHeader.getVouchermis().getDivisionid().getId());
			paramMap.put("glcode", txnObj.getGlCode());
			paramMap.put("asondate", voucherHeader.getVoucherDate());
			paramMap.put("mis.budgetcheckreq", voucherHeader.getVouchermis().getBudgetCheckReq());
			paramMap.put("voucherHeader", voucherHeader);
			if (txnObj.getBillId() != null)
				paramMap.put("bill", txnObj.getBillId());
			if (!budgetDetailsDAO.budgetaryCheck(paramMap))
				throw new ValidationException("Budget check failed: Insufficient Budget for " + txnObj.getGlCode(),
						"Budget check failed: Insufficient Budget for " + txnObj.getGlCode());
		}
		return true;
	}

	@Transactional
	public boolean postTransactions(final Transaxtion txnList[], final String vDate) throws TaskFailedException {

		if (!checkBudget(txnList))
			throw new TaskFailedException(FinancialConstants.BUDGET_CHECK_ERROR_MESSAGE);
		// if objects are lost load them
		loadAccountData();
		try {
			if (!validPeriod(vDate))
				throw new TaskFailedException(
						"Voucher Date is not within an open period. Please use an open period for posting");
			if (!validateTxns(txnList))
				return false;
		} catch (final TaskFailedException e) {
			LOGGER.error(e.getMessage(), e);
			throw new TaskFailedException(e.getMessage());
		}
		// entityManager.flush();
		if (!postInGL(txnList))
			return false;
		return true;
	}

	private boolean validateTxns(final Transaxtion txnList[]) throws TaskFailedException {
		// validate the array list for the total number of txns
		if (txnList.length < 2)
			return false;
		double dbAmt = 0;
		double crAmt = 0;
		try {
			for (final Transaxtion element : txnList) {
				final Transaxtion txn = element;
				if (!validateGLCode(txn))
					return false;
				dbAmt += Double.parseDouble(txn.getDrAmount());
				crAmt += Double.parseDouble(txn.getCrAmount());
			}
		} finally {
			RequiredValidator.clearEmployeeMap();
		}
		dbAmt = ExilPrecision.convertToDouble(dbAmt, 2);
		crAmt = ExilPrecision.convertToDouble(crAmt, 2);
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Total Checking.....Debit total is :" + dbAmt + "  Credit total is :" + crAmt);
		if (dbAmt != crAmt)
			throw new TaskFailedException("Total debit and credit not matching. Total debit amount is: " + dbAmt
					+ " Total credit amount is :" + crAmt);
		// return false;
		return true;
	}

	public boolean validateGLCode(final Transaxtion txn) throws TaskFailedException {
		// validate each gl code
		Object raw = getGlAccountCodes().get(txn.getGlCode());
		GLAccount glAcc = null;

		if (raw instanceof GLAccount) {
			glAcc = (GLAccount) raw;
		} else if (raw instanceof LinkedHashMap) {
			ObjectMapper mapper = new ObjectMapper();
			glAcc = mapper.convertValue(raw, GLAccount.class);
		} else {
			throw new TaskFailedException("Invalid GLAccount object for GL Code: " + txn.getGlCode());
		}

		if (glAcc == null) {
			LOGGER.error("GLCode is null");
			return false;
		}
		txn.setGlName(glAcc.getName());

		glAcc.setIsactiveforposting(true);// while worikng purpose
		glAcc.setClassification(4L);// while worikng purpose

		if (LOGGER.isInfoEnabled())
			LOGGER.info(txn.getGlCode() + " is activefor posting :" + glAcc.isIsactiveforposting());
		if (!glAcc.isIsactiveforposting())
			throw new TaskFailedException("The COA(GlCode) " + txn.getGlCode()
					+ "  used is not active for posting. Kindly modify COA from detailed code screen and then proceed for creating voucher");

		if (LOGGER.isInfoEnabled())
			LOGGER.info("Classification....:" + glAcc.getClassification());
		if (glAcc.getClassification() != 4) {
			if (LOGGER.isInfoEnabled())
				LOGGER.info("classification is not detailed code");
			throw new TaskFailedException("Cannot post to " + txn.getGlCode());
		}
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Going to check the Amount.Debit: " + txn.getDrAmount() + " ** Credit :" + txn.getCrAmount());
		if (Double.parseDouble(txn.getDrAmount()) > 0 && Double.parseDouble(txn.getCrAmount()) > 0)
			throw new TaskFailedException("Both Debit and Credit cannot be greater than Zero.");
		// return false;
		if (!isRequiredPresent(txn, glAcc))
			return false;
		return true;
	}

	private boolean isRequiredPresent(final Transaxtion txn, final GLAccount glAcc) throws TaskFailedException {
		int requiredCount = 0;
		int foundCount = 0;
		final List<?> glParamList = glAcc.getGlParameters(); // Use wildcard type
		for (int i = 0; i < glParamList.size(); i++) {
			final GLParameter glPrm = (GLParameter) glParamList.get(i);
			final TransaxtionParameter txnPrm1 = (TransaxtionParameter) txn.getTransaxtionParameters().get(0);
			if (glPrm.getDetailId() == Integer.parseInt(txnPrm1.getDetailTypeId()))
				requiredCount++;
			/*
			 * if(!glPrm.getDetailKey().equalsIgnoreCase("0")&&glPrm. getDetailKey
			 * ().length()>0){ foundCount++; continue; }
			 */
			for (int j = 0; j < txn.getTransaxtionParameters().size(); j++) {
				final TransaxtionParameter txnPrm = (TransaxtionParameter) txn.getTransaxtionParameters().get(j);
				// if(LOGGER.isInfoEnabled())
				// LOGGER.info(glAcc.getCode()+" "+txnPrm.getDetailName()+"
				// "+txnPrm.getDetailKey());
				if (txnPrm.getDetailName().equalsIgnoreCase(glPrm.getDetailName())) {
					final int id = glPrm.getDetailId().intValue();
					if (rv.validateKey(id, txnPrm.getDetailKey()))
						foundCount++;
					else
						return false;
				}
			}
		}
		if (foundCount < requiredCount)
			return false;
		return true;
	}

	public HashMap getGlAccountCodes() {
		LOGGER.debug("in getGlAccountCodes():jndi name is :");
		HashMap retMap = null;
		try {
			HashMap cacheValuesHashMap = new HashMap<Object, Object>();
			try {
				cacheValuesHashMap = (HashMap) applicationCacheManager.get(ROOTNODE);
			} catch (MappingException e) {
				// return null;
			}
			if (cacheValuesHashMap != null && !cacheValuesHashMap.isEmpty())
				retMap = (HashMap) cacheValuesHashMap.get(GLACCCODENODE);
			if (retMap != null)
				LOGGER.debug("in getGlAccountCodes() size is :" + retMap.size());

		} catch (final MappingException e) {
			LOGGER.debug(EXP + e.getMessage());
			throw new ApplicationRuntimeException(e.getMessage());
		}
		return retMap;
	}

	@Transactional
	public boolean postInGL(final Transaxtion[] txnList) throws TaskFailedException {

		for (Transaxtion txn : txnList) {
			// Skip if both debit and credit are zero
			if ("0".equals(txn.getDrAmount()) && "0".equals(txn.getCrAmount())) {
				LOGGER.info("Skipping transaction with zero debit and credit for GL Code: {}", txn.getGlCode());
				return false;
			}

			CGeneralLedger gLedger = new CGeneralLedger();

			// Handle GLAccount object or LinkedHashMap from cache
			Object raw = getGlAccountCodes().get(txn.getGlCode());
			GLAccount glAcc;
			if (raw instanceof GLAccount) {
				glAcc = (GLAccount) raw;
			} else if (raw instanceof LinkedHashMap) {
				glAcc = new ObjectMapper().convertValue(raw, GLAccount.class);
			} else {
				throw new TaskFailedException("Invalid GLAccount object for GL Code: " + txn.getGlCode());
			}

			// Set voucher line ID if available
			if (txn.getVoucherLineId() != null)
				gLedger.setVoucherlineId(Integer.parseInt(txn.getVoucherLineId()));

			// Set Chart of Accounts
			Long glAccId = glAcc.getID();
			CChartOfAccounts coa = chartOfAccountsRepository.findById(glAccId)
					.orElseThrow(() -> new EntityNotFoundException("ChartOfAccounts not found for ID: " + glAccId));
			gLedger.setGlcodeId(coa);
			gLedger.setGlcode(txn.getGlCode());
			gLedger.setDebitAmount(BigDecimal.valueOf(Double.parseDouble(txn.getDrAmount())));
			gLedger.setCreditAmount(BigDecimal.valueOf(Double.parseDouble(txn.getCrAmount())));
			gLedger.setDescription(txn.getNarration());
			gLedger.setEffectiveDate(new Date());

			// Set function ID
			if (txn.getFunctionId() != null && !txn.getFunctionId().trim().isEmpty()
					&& !txn.getFunctionId().equals("0")) {
				gLedger.setFunctionId(Integer.parseInt(txn.getFunctionId()));
			} else if (Boolean.TRUE.equals(glAcc.getFunctionRequired())) {
				throw new ValidationException(List
						.of(new ValidationError("exp", "Function is required for account code: " + txn.getGlCode())));
			} else {
				gLedger.setFunctionId(null);
			}

			// Get VoucherHeader Entity
			CVoucherHeader voucherHeader = voucherRepository.findById(Long.valueOf(txn.getVoucherHeaderId()))
					.orElseThrow(() -> new EntityNotFoundException(
							"VoucherHeader not found for ID: " + txn.getVoucherHeaderId()));

			gLedger.setVoucherHeaderId(voucherHeader);

//			if (voucherHeader.getGeneralLedger() == null) {
//				voucherHeader.setGeneralLedger(new HashSet<>());
//			}
//			voucherHeader.getGeneralLedger().add(gLedger);
			try {
				// Persist General Ledger
				cGeneralLedgerHibernateDAO.create(gLedger);

				// populating the data related to non controlled remitted code
				populateDataForNonControlledRemittedCode(gLedger);
			} catch (final TaskFailedException e) {
				LOGGER.error("error in the gl++++++++++" + e, e);
				return false;
			}

			// If no GL Parameters, skip detail processing
			List<GLParameter> glParams = new ArrayList<>(new HashSet<>(glAcc.getGlParameters()));
			if (glParams.isEmpty())
				continue;

			List<TransaxtionParameter> txnParams = txn.getTransaxtionParameters();

			for (GLParameter glPrm : glParams) {
				for (TransaxtionParameter tParam : txnParams) {
					if (tParam.getDetailName().equalsIgnoreCase(glPrm.getDetailName())
							&& tParam.getGlcodeId().equals(String.valueOf(gLedger.getGlcodeId().getId()))) {

						CGeneralLedgerDetail gLedgerDet = new CGeneralLedgerDetail();

						AccountDetailType detailType = accountDetailTypeRepository.findById(glPrm.getDetailId())
								.orElseThrow(() -> new EntityNotFoundException(
										"AccountDetailType not found for ID: " + glPrm.getDetailId()));

						gLedgerDet.setGeneralLedgerId(gLedger);
						gLedgerDet.setDetailTypeId(detailType);
						gLedgerDet.setDetailKeyId(Integer.parseInt(tParam.getDetailKey()));
						gLedgerDet.setDetailKeyName(tParam.getDetailName());
						gLedgerDet.setAmount(new BigDecimal(tParam.getDetailAmt()));

						cGeneralledgerdetailHibernateDAO.create(gLedgerDet);

						if (validRecoveryGlcode(String.valueOf(gLedger.getGlcodeId().getId()))
								&& gLedger.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {
							EgRemittanceGldtl egRemitGldtl = new EgRemittanceGldtl();
							egRemitGldtl.setGeneralledgerdetail(gLedgerDet);
							egRemitGldtl.setGldtlamt(gLedgerDet.getAmount());

							if (tParam.getTdsId() != null) {
								Recovery tdsentry = recoveryRepository.findById(Long.parseLong(tParam.getTdsId()))
										.orElse(null);
								if (tdsentry != null) {
									egRemitGldtl.setRecovery(tdsentry);
								}
							}

							egRemittanceGldtlHibernateDAO.create(egRemitGldtl);
						}

						if (gLedger.getGeneralLedgerDetails() == null) {
							Set<CGeneralLedgerDetail> detailsSet = new HashSet<>();
							detailsSet.add(gLedgerDet);
							gLedger.setGeneralLedgerDetails(detailsSet);
						} else {
							gLedger.getGeneralLedgerDetails().add(gLedgerDet);
						}
					}
				}
			}

			// Finally attach ledger to voucher
			if (voucherHeader.getGeneralLedger() == null) {
				Set<CGeneralLedger> ledgerSet = new HashSet<>();
				ledgerSet.add(gLedger);
				voucherHeader.setGeneralLedger(ledgerSet);
			} else {
				voucherHeader.getGeneralLedger().add(gLedger);
			}
		}

		return true;
	}

	private boolean validRecoveryGlcode(final String glcodeId) throws TaskFailedException {
		Optional<Recovery> tds = tdsHibernateDAO.findActiveTdsByGlcodeId(Long.valueOf(glcodeId));
		if (tds != null)
			return true;
		else
			return false;

	}

	public void populateDataForNonControlledRemittedCode(CGeneralLedger gLedger) throws TaskFailedException {
		Long glCodeId = gLedger.getGlcodeId().getId();

		// Check if this GL code is a controlled code
		boolean isControlledCode = cChartOfAccountDetailRepository.existsByGlCodeIdId(glCodeId);

		// Proceed only if:
		// - it's not a controlled code
		// - it is a valid recovery GL code
		// - and credit amount is positive
		if (!isControlledCode && validRecoveryGlcode(String.valueOf(glCodeId))
				&& gLedger.getCreditAmount().compareTo(BigDecimal.ZERO) > 0) {

			EgRemittanceGl egRemitGl = new EgRemittanceGl();
			egRemitGl.setGlid(gLedger);
			egRemitGl.setGlamt(gLedger.getCreditAmount());

			Optional<Recovery> tdsEntry = tdsHibernateDAO.findActiveTdsByGlcodeId(glCodeId);
			tdsEntry.ifPresent(egRemitGl::setRecovery); // Set only if present

			egRemitGl.setLastmodifieddate(new Date());

			egRemittanceGlRepository.save(egRemitGl);
		}
	}

	private boolean validPeriod(String vDate) throws TaskFailedException {
		try {
			if (isClosedForPosting(vDate))
				return false;
		} catch (final HibernateException e) {
			LOGGER.error("Inside validPeriod " + e.getMessage(), e);
			throw new TaskFailedException();
		}
		return true;
	}

	private boolean isClosedForPosting(String date) {
		boolean isClosed = true;

		try {
			final SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
			Date parsedDate = formatter.parse(date);

			// Check if the date is within an active financial year
			FinancialYear financialYearByDate = financialYearRepository.getFinancialYearByDate(parsedDate);
			if (financialYearByDate != null)
				isClosed = false;

			if (!isClosed) {
				String hql = "SELECT cp.id FROM ClosedPeriod cp WHERE cp.isClosed = true "
						+ "AND cp.startingDate <= :date AND cp.endingDate >= :date";

				Query query = entityManager.createQuery(hql);
				query.setParameter("date", parsedDate);
				List<?> result = query.getResultList();

				isClosed = result != null && !result.isEmpty();
			}

		} catch (Exception e) {
			isClosed = true;
			LOGGER.error("Exception occurred while checking closed periods: {}", e.getMessage(), e);
		}

		return isClosed;
	}

	private void loadAccountData() {
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("loadAccountData called");

		HashMap<String, HashMap> hm = null;
		hm = (HashMap<String, HashMap>) applicationCacheManager.get(ROOTNODE);
		if (hm == null) {

			coaCache.loadAccountData();
		}

	}

}

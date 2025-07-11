package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.egov.finance.voucher.entity.AccountDetailType;
import org.egov.finance.voucher.entity.CChartOfAccountDetail;
import org.egov.finance.voucher.entity.CChartOfAccounts;

import org.egov.finance.voucher.repository.AccountDetailTypeRepository;
import org.egov.finance.voucher.repository.CChartOfAccountDetailRepository;
import org.egov.finance.voucher.repository.ChartOfAccountsRepository;
import org.egov.finance.voucher.service.ApplicationCacheManager;
import org.egov.finance.voucher.service.ChartOfAccountDetailService;
import org.egov.finance.voucher.service.ChartOfAccountService;
import org.egov.finance.voucher.service.VoucherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class CoaCache implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ChartOfAccountService.class);

	@Autowired
	private ChartOfAccountDetailService chartOfAccountDetailService;
	@Autowired
	private ApplicationCacheManager applicationCacheManager;

	@Autowired
	private VoucherService voucherService;

	@Autowired
	ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	CChartOfAccountDetailRepository cChartOfAccountDetailRepository;

	@Autowired
	AccountDetailTypeRepository accountDetailTypeRepository;

	private static final String ROOTNODE = "/COA";
	private static final String GLACCCODENODE = "GlAccountCodes";
	private static final String GLACCIDNODE = "GlAccountIds";
	private static final String ACCOUNTDETAILTYPENODE = "AccountDetailType";
	private static final String EXP = "Exp=";
	private static final String EXILRPERROR = "exilRPError";

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void loadAccountData() {
		Map<String, AccountDetailType> accountDetailTypeMap = accountDetailTypeRepository.findAll().stream()
				.collect(Collectors.toMap(AccountDetailType::getAttributename, Function.identity()));

		List<CChartOfAccounts> coaList = chartOfAccountsRepository.findAll();

		Map<String, GLAccount> glAccountCodes = coaList.stream().map(this::toGLAccount)
				.collect(Collectors.toMap(GLAccount::getGlCode, Function.identity()));

		Map<Long, GLAccount> glAccountIds = glAccountCodes.values().stream()
				.collect(Collectors.toMap(GLAccount::getID, Function.identity()));

		// Load parameter mapping
		loadParameters(glAccountCodes, glAccountIds);

		// Put in cache
		Map<String, Object> cacheMap = new HashMap<>();
		cacheMap.put(ACCOUNTDETAILTYPENODE, accountDetailTypeMap);
		cacheMap.put(GLACCCODENODE, glAccountCodes);
		cacheMap.put(GLACCIDNODE, glAccountIds);

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("COA Cache Loaded. GL Accounts: " + glAccountCodes.size());
		}

		applicationCacheManager.put(ROOTNODE, cacheMap);
		LOGGER.info("COA Cache Loaded. GL Accounts: " + cacheMap);
	}

	private synchronized void loadParameters(Map<String, GLAccount> glAccountCodes, Map<Long, GLAccount> glAccountIds) {
		List<CChartOfAccountDetail> details = cChartOfAccountDetailRepository.findAll();

		for (CChartOfAccountDetail detail : details) {
			GLParameter param = new GLParameter();
			param.setDetailId(detail.getDetailTypeId().getId());
			param.setDetailName(detail.getDetailTypeId().getAttributename());

			CChartOfAccounts coa = detail.getGlCodeId();

			GLAccount accByCode = glAccountCodes.get(coa.getGlcode());
			GLAccount accById = glAccountIds.get(coa.getId());

			if (accByCode != null) {
				accByCode.getGlParameters().add(param);
			}

			if (accById != null) {
				accById.getGlParameters().add(param);
			}
		}
	}

	private GLAccount toGLAccount(CChartOfAccounts coa) {
		return GLAccount.builder().ID(coa.getId()).glCode(coa.getGlcode()).name(coa.getName())
				.glParameters(new ArrayList<>()).build();
	}

	public void reLoad() {
		clear();
		loadAccountData();
	}

	private void clear() {
		applicationCacheManager.remove(ROOTNODE);
	}
}

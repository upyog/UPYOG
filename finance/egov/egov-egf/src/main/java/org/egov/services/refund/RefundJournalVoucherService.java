package org.egov.services.refund;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Fund;
import org.egov.commons.Vouchermis;
import org.egov.egf.voucher.service.JournalVoucherService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.model.refund.RefundApplication;
import org.egov.utils.FinancialConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.microservice.models.Assignment;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;

@Service
public class RefundJournalVoucherService {

	private static final String REFUND_DESCRIPTION_PREFIX = "Refund for application ";

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private JournalVoucherService journalVoucherService;

	private static final String EGF_MODULE = "EGF";

	private static final String REFUND_JV_APPROVER_CONFIG_KEY = "REFUND_JV_APPROVER_USER_ID";

	@Autowired
	private AppConfigValueService appConfigValueService;

	@Autowired
	private MicroserviceUtils microserviceUtils;

	@Transactional
	public CVoucherHeader createJournalVoucher(final RefundApplication refundApplication, final String comments) {

		validate(refundApplication);

		final CChartOfAccounts debitAccount = findChartOfAccount(refundApplication.getDebitGlCode());

		final CChartOfAccounts creditAccount = findChartOfAccount(refundApplication.getCreditGlCode());

		final Fund fund = findFund(refundApplication.getFundCode());

		findDepartment(refundApplication.getDepartmentCode());

		final CFunction function = findFunction(refundApplication.getFunctionCode());

		final CVoucherHeader voucherHeader = buildVoucherHeader(refundApplication, fund, function);

		final Set<CGeneralLedger> ledgers = new HashSet<>();

		ledgers.add(createLedger(voucherHeader, debitAccount, refundApplication.getRefundAmount().doubleValue(), 0D, 1,
				function));

		ledgers.add(createLedger(voucherHeader, creditAccount, 0D, refundApplication.getRefundAmount().doubleValue(), 2,
				function));

		voucherHeader.setGeneralLedger(ledgers);

		final Long approvalPosition = resolveApprovalPosition();

		return journalVoucherService.createForRefund(voucherHeader, approvalPosition,
				comments == null ? "" : comments.trim());
	}

	private CVoucherHeader buildVoucherHeader(final RefundApplication refundApplication, final Fund fund,
			final CFunction function) {

		final Date voucherDate = refundApplication.getRefundDate() == null ? new Date()
				: new Date(refundApplication.getRefundDate());

		final CVoucherHeader voucherHeader = new CVoucherHeader();

		voucherHeader.setType(FinancialConstants.STANDARD_VOUCHER_TYPE_JOURNAL);

		voucherHeader.setVoucherSubType(FinancialConstants.JOURNALVOUCHER_NAME_GENERAL);

		voucherHeader.setVoucherNumType(FinancialConstants.VOUCHER_TYPE_JOURNAL);

		voucherHeader.setName(FinancialConstants.JOURNALVOUCHER_NAME_GENERAL);

		voucherHeader.setVoucherDate(voucherDate);
		voucherHeader.setEffectiveDate(voucherDate);
		voucherHeader.setFundId(fund);

		voucherHeader.setDescription(REFUND_DESCRIPTION_PREFIX + refundApplication.getRefundApplicationNumber());

		final Vouchermis voucherMis = new Vouchermis();

		voucherMis.setDepartmentcode(refundApplication.getDepartmentCode());

		voucherMis.setFunction(function);

		voucherMis.setReferenceDocument(limitLength(refundApplication.getRefundApplicationNumber(), 50));

		voucherMis.setServiceName(limitLength(refundApplication.getBusinessService(), 100));

		voucherMis.setVoucherheaderid(voucherHeader);
		voucherHeader.setVouchermis(voucherMis);

		return voucherHeader;
	}

	private CGeneralLedger createLedger(final CVoucherHeader voucherHeader, final CChartOfAccounts account,
			final Double debitAmount, final Double creditAmount, final Integer lineNumber, final CFunction function) {

		final CGeneralLedger ledger = new CGeneralLedger();

		ledger.setVoucherlineId(lineNumber);
		ledger.setEffectiveDate(voucherHeader.getVoucherDate());

		ledger.setGlcodeId(account);
		ledger.setGlcode(account.getGlcode());
		ledger.setDebitAmount(debitAmount);
		ledger.setCreditAmount(creditAmount);

		ledger.setDescription(voucherHeader.getDescription());

		ledger.setVoucherHeaderId(voucherHeader);

		if (function != null && function.getId() != null) {
			ledger.setFunctionId(function.getId().intValue());
		}

		return ledger;
	}

	private CChartOfAccounts findChartOfAccount(final String glCode) {

		final List<CChartOfAccounts> accounts = entityManager
				.createQuery("from CChartOfAccounts " + "where glcode = :glCode", CChartOfAccounts.class)
				.setParameter("glCode", glCode.trim()).setMaxResults(1).getResultList();

		if (accounts.isEmpty()) {
			throw new IllegalArgumentException("Invalid GL code: " + glCode);
		}

		return accounts.get(0);
	}

	private Fund findFund(final String fundCode) {

		final List<Fund> funds = entityManager.createQuery("from Fund where code = :fundCode", Fund.class)
				.setParameter("fundCode", fundCode.trim()).setMaxResults(1).getResultList();

		if (funds.isEmpty()) {
			throw new IllegalArgumentException("Invalid fund code: " + fundCode);
		}

		return funds.get(0);
	}

	private Department findDepartment(final String departmentCode) {

		final List<Department> departments = entityManager
				.createQuery("from Department " + "where code = :departmentCode", Department.class)
				.setParameter("departmentCode", departmentCode.trim()).setMaxResults(1).getResultList();

		if (departments.isEmpty()) {
			throw new IllegalArgumentException("Invalid department code: " + departmentCode);
		}

		return departments.get(0);
	}

	private CFunction findFunction(final String functionCode) {

		if (functionCode == null || functionCode.trim().isEmpty()) {
			return null;
		}

		final List<CFunction> functions = entityManager
				.createQuery("from CFunction " + "where code = :functionCode", CFunction.class)
				.setParameter("functionCode", functionCode.trim()).setMaxResults(1).getResultList();

		if (functions.isEmpty()) {
			throw new IllegalArgumentException("Invalid function code: " + functionCode);
		}

		return functions.get(0);
	}

	private void validate(final RefundApplication refundApplication) {

		if (refundApplication == null) {
			throw new IllegalArgumentException("Refund application is mandatory");
		}

		if (refundApplication.getVoucherNumber() != null && !refundApplication.getVoucherNumber().trim().isEmpty()) {
			throw new IllegalArgumentException(
					"Journal Voucher is already created for refund: " + refundApplication.getRefundApplicationNumber());
		}
	}

	private String limitLength(final String value, final int maximumLength) {

		if (value == null) {
			return null;
		}

		return value.length() <= maximumLength ? value : value.substring(0, maximumLength);
	}

	private Long resolveApprovalPosition() {

		final List<AppConfigValues> configValues = appConfigValueService.getConfigValuesByModuleAndKey(EGF_MODULE,
				REFUND_JV_APPROVER_CONFIG_KEY);

		if (configValues == null || configValues.isEmpty() || configValues.get(0) == null
				|| configValues.get(0).getValue() == null || configValues.get(0).getValue().trim().isEmpty()) {
			throw new IllegalArgumentException("AppConfig " + REFUND_JV_APPROVER_CONFIG_KEY + " is not configured");
		}

		final Long approverUserId;

		try {
			approverUserId = Long.valueOf(configValues.get(0).getValue().trim());
		} catch (NumberFormatException exception) {
			throw new IllegalArgumentException("Invalid user ID configured in " + REFUND_JV_APPROVER_CONFIG_KEY,
					exception);
		}

		if (approverUserId <= 0) {
			throw new IllegalArgumentException("Invalid user ID configured in " + REFUND_JV_APPROVER_CONFIG_KEY);
		}

		final List<EmployeeInfo> employees = microserviceUtils.getEmployee(approverUserId, null, null, null);

		if (employees == null || employees.isEmpty()) {
			throw new IllegalArgumentException(
					"HRMS employee was not found for refund JV approver " + "user ID " + approverUserId);
		}

		if (employees.size() != 1) {
			throw new IllegalArgumentException(
					"Multiple HRMS employee records returned for refund JV " + "approver user ID " + approverUserId);
		}

		final EmployeeInfo employee = employees.get(0);

		if (employee == null || employee.getAssignments() == null || employee.getAssignments().isEmpty()) {
			throw new IllegalArgumentException(
					"No HRMS assignments found for refund JV approver " + "user ID " + approverUserId);
		}

		Assignment currentAssignment = null;

		for (final Assignment assignment : employee.getAssignments()) {
			if (assignment == null || !Boolean.TRUE.equals(assignment.getIsCurrentAssignment())) {
				continue;
			}

			if (currentAssignment != null) {
				throw new IllegalArgumentException("Multiple current HRMS assignments found for refund JV "
						+ "approver user ID " + approverUserId);
			}

			currentAssignment = assignment;
		}

		if (currentAssignment == null || currentAssignment.getPosition() == null
				|| currentAssignment.getPosition() <= 0) {
			throw new IllegalArgumentException("Current HRMS assignment with a valid position was not "
					+ "found for refund JV approver user ID " + approverUserId);
		}

		return currentAssignment.getPosition();
	}
}
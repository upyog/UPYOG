/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2018  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
/**
 *
 */
package org.egov.egf.web.actions.voucher;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Actions;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.Accountdetailtype;
import org.egov.commons.Bank;
import org.egov.commons.Bankaccount;
import org.egov.commons.Bankbranch;
import org.egov.commons.CChartOfAccountDetail;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.CGeneralLedger;
import org.egov.commons.CGeneralLedgerDetail;
import org.egov.commons.CVoucherHeader;
import org.egov.commons.Functionary;
import org.egov.commons.Fundsource;
import org.egov.commons.Relation;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.service.RelationService;
import org.egov.commons.utils.BankAccountType;
import org.egov.commons.utils.EntityType;
import org.egov.egf.commons.EgovCommon;
import org.egov.egf.masters.model.LoanGrantBean;
import org.egov.eis.entity.DrawingOfficer;
import org.egov.eis.entity.EmployeeView;
import org.egov.infra.admin.master.entity.AppConfigValues;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.masters.model.AccountEntity;
import org.egov.model.bills.EgBillSubType;
import org.egov.model.bills.EgBillregister;
import org.egov.model.budget.BudgetDetail;
import org.egov.model.instrument.InstrumentHeader;
import org.egov.model.voucher.CommonBean;
import org.egov.pims.model.PersonalInformation;
import org.egov.services.budget.BudgetDetailService;
import org.egov.services.financingsource.FinancingSourceService;
import org.egov.services.instrument.InstrumentService;
import org.egov.services.voucher.VoucherService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.query.Query;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;





import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

//import com.exilant.eGov.src.domain.Bank;

/**
 * LTS Migration Notes:
 * 1. [Hibernate 6 Strict SQM] Corrected HQL association comparisons to use explicit scalar IDs
 *    (e.g., 'ac.bankAccountId.id = ?' and 'ac.id = cd.accountCheque.id') preventing SemanticException.
 * 2. [Hibernate 6 Native Queries & Types] Migrated all createSQLQuery() to createNativeQuery() and replaced
 *    removed Hibernate 5 Type singletons (StringType, LongType, IntegerType, DateType) with StandardBasicTypes.
 * 3. [Struts 7 & Jakarta EE Parameter Extraction] Added request parameter extraction for AJAX endpoints
 *    (handling 'fundId'/'fund', 'departmentId'/'department', 'functionId'/'function', 'selectedBillIds')
 *    and guarded against NumberFormatException on unselected dropdowns.
 * 4. [MDMS Integration] Added null checks for microserviceUtils.getDepartmentByCode() results.
 */
@Results({
        @Result(name = "bankAccountByBranch", location = "common-bankAccountByBranch.jsp"),
        @Result(name = "branch", location = "common-branch.jsp"),
        @Result(name = "users", location = "common-users.jsp"),
        @Result(name = "arfNoSearchResults", location = "common-arfNoSearchResults.jsp"),
        @Result(name = "bankAccNum", location = "common-bankAccNum.jsp"),
        @Result(name = "bankAccNum-bankName", location = "common-bankAccNum-bankName.jsp"),
        @Result(name = Constants.FUNDSOURCE, location = "common-" + Constants.FUNDSOURCE + ".jsp"),
        @Result(name = "workflowHistory", location = "common-workflowHistory.jsp"),
        @Result(name = "searchAccountCodes", location = "common-searchAccountCodes.jsp"),
        @Result(name = "entities", location = "common-entities.jsp"),
        @Result(name = "bankByFund", location = "common-bankByFund.jsp"),
        @Result(name = "result", location = "common-result.jsp"),
        @Result(name = "branchesByBank", location = "common-branchesByBank.jsp"),
        @Result(name = "bank", location = "common-bank.jsp"),
        @Result(name = "glCodes", location = "common-glCodes.jsp"),
        @Result(name = "subLedgerType", location = "common-subLedgerType.jsp"),
        @Result(name = "checkList", location = "common-checkList.jsp"),
        @Result(name = "projectCodesBy20", location = "common-projectCodesBy20.jsp"),
        @Result(name = "bankAccNumAndType", location = "common-bankAccNumAndType.jsp"),
        @Result(name = "schemes", location = "common-schemes.jsp"),
        @Result(name = "subSchemeBy20", location = "common-subSchemeBy20.jsp"),
        @Result(name = "voucherNames", location = "common-voucherNames.jsp"),
        @Result(name = "rtgsNumbers", location = "common-rtgsNumbers.jsp"),
        @Result(name = Constants.SUBSCHEMES, location = "common-" + Constants.SUBSCHEMES + ".jsp"),
        @Result(name = "drawingOffcer", location = "common-drawingOffcer.jsp"),
        @Result(name = "searchResult", location = "common-searchResult.jsp"),
        @Result(name = "projectcodes", location = "common-projectcodes.jsp"),
        @Result(name = "functionCodes", location = "common-functionCodes.jsp"),
        @Result(name = "AJAX_RESULT", location = "common-AJAX_RESULT.jsp"),
        @Result(name = "accountcodes", location = "common-accountcodes.jsp"),
        @Result(name = "instrument", location = "common-instrument.jsp"),
        @Result(name = "desg", location = "common-desg.jsp"),
        @Result(name = "COA", location = "common-COA.jsp"),
        @Result(name = "process", location = "common-process.jsp"),
        @Result(name = "schemeBy20", location = "common-schemeBy20.jsp"),
        @Result(name = "yearCode", location = "common-yearCode.jsp"),
        @Result(name = "estimateBudgetDetails", location = "common-estimateBudgetDetails.jsp")
})
public class CommonAction extends BaseFormAction {

    private static final Logger LOGGER = Logger.getLogger(CommonAction.class);
    private static final long serialVersionUID = 1L;
    /*
     * Hibernate 6 migration note:
     * InstrumentVoucher.instrumentHeaderId is mapped as an InstrumentHeader entity.
     * Hibernate 6 rejects comparing that association to ih.id, so the shared RTGS
     * HQL compares the association with the InstrumentHeader alias ih.
     */
    private static final String RTGSNUMBERSQUERY = "SELECT ih.id, ih.transactionNumber FROM InstrumentHeader ih, InstrumentVoucher iv, "
            + "Paymentheader ph WHERE ih.isPayCheque ='1' AND ih.bankAccountId.id = ? AND ih.statusId.description in ('New')" +
            " AND ih.statusId.moduletype='Instrument' AND iv.instrumentHeaderId = ih and ih.bankAccountId is not null " +
            "AND iv.voucherHeaderId     = ph.voucherheader AND ph.bankaccount = ih.bankAccountId AND ph.type = '"
            + FinancialConstants.MODEOFPAYMENT_RTGS + "' " + "GROUP BY ih.transactionNumber,ih.id order by ih.id desc";
    private Long fundId;
    private Integer schemeId;
    private Integer department;
    private Integer bankId;
    private List<Map<String, Object>> bankBranchList;
    private Integer branchId;
    private String departmentId;
    private Long bankaccountId;
    private String rtgsNumber;
    private String chequeNumber;
    private List<Bankaccount> accNumList;
    private List<DrawingOfficer> drawingList;
    private String value;
    private List<Scheme> schemeList;
    private List<SubScheme> subSchemes;
    private List<Bankbranch> branchList;
    private List<Bank> bankList;
    private List<InstrumentHeader> instrumentHeaderList;
    private String type;
    private ArrayList<Map<String, String>> nameList;
    private InstrumentService instrumentService;
    private List<String> detailCodes = new ArrayList<String>();
    private List<User> userList;
    private Integer designationId;
    private VoucherService voucherService;
    private String functionaryName;
    private EgovCommon egovCommon;
    private List<CChartOfAccounts> accountCodesForDetailTypeList;
    private List<EntityType> entitiesList;
    private List<String> numberList;
    private Integer accountDetailType;
    private Integer billSubtypeId;
    private String billType;
    private String searchType;
    private List<BudgetDetail> budgetDetailList;

    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    @Autowired
    private AppConfigValueService appConfigValuesService;
    private List<AppConfigValues> checkList;
    private String accountDetailTypeName;
    private String typeOfAccount;
    private Date asOnDate;
    private String scriptName;
    private Long recoveryId;
    private Integer subSchemeId;
    private List<Fundsource> fundSouceList;
    private List<Map<String, Object>> designationList;
    private String startsWith;
    private FinancingSourceService financingSourceService;
    private String defaultDepartment;
    private Long billRegisterId;
    private Long billVhId;
    private String returnStream = "";
    private List<LoanGrantBean> projectCodeList;
    private List<String> projectCodeStringList;
    private List<CChartOfAccounts> accountCodesList;
    private String stateId;
    private String serialNo;
    private static final String ARF_NUMBER_SEARCH_RESULTS = "arfNoSearchResults";
    public static final String ARF_STATUS_APPROVED = "APPROVED";
    public static final String ARF_TYPE = "Contractor";
    private String query;
    private List<String> arfNumberSearchList = new LinkedList<String>();
    private String billSubType;
    private String glCode;
    private String function;
    private List<CChartOfAccounts> glCodesList;
    private List<CFunction> functionCodesList;
    private List<Accountdetailtype> subLedgerTypeList;
    private List<CChartOfAccounts> coaList;
    private StringBuffer result;
    private Long vouchHeaderId;
    private String glcodeParam;
    private String accountId;
    private String functionName;
    private Integer bankaccount;
    private List<CFinancialYear> yearCodeList;
    private Long functionId;
    @Autowired
    private BudgetDetailService budgetDetailService;
    private ArrayList<Department> listOfDepartments;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(final String serialNo) {
        this.serialNo = serialNo;
    }

    public InputStream getReturnStream() {
        final ByteArrayInputStream is = new ByteArrayInputStream(returnStream.getBytes());
        return is;
    }

    public Long getBillRegisterId() {
        return billRegisterId;
    }

    public void setBillRegisterId(final Long billRegisterId) {
        this.billRegisterId = billRegisterId;
    }

    public void setRelationService(final RelationService relationService) {
    }

    public List<String> getNumberList() {
        return numberList;
    }

    public void setNumberList(final List<String> numberList) {
        this.numberList = numberList;
    }

    public CommonAction() {
    }

    @Override
    public Object getModel() {

        return null;
    }

    public List<Bank> getBankList() {
        return bankList;
    }

    public void setBankList(final List<Bank> bankList) {
        this.bankList = bankList;
    }

    /**
     * Java 17 / Hibernate 6 LTS Migration Fix:
     * 1. Changed HQL field name from camelCase 'isActive=true' to lowercase 'isactive=true' matching Scheme entity.
     * 2. Updated fallback parameter -1 to -1L (Long) to match Hibernate 6 Strict SQM criteria type checking for fund.id.
     * 3. Handles null or 0 fundId safely.
     */
    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSchemes")
    public String ajaxLoadSchemes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSchemes...");
        if (fundId == null) {
            String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId == null || fId.trim().isEmpty()) {
                fId = ServletActionContext.getRequest().getParameter("fund");
            }
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                fundId = Long.valueOf(fId.trim());
            }
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Fund Id received is : " + fundId);
        if (null == fundId || fundId == 0 || fundId == -1)
            schemeList = getPersistenceService().findAllBy(
                    " from Scheme where fund.id=? and isactive=true order by name", -1L);
        else
            schemeList = getPersistenceService()
                    .findAllBy(" from Scheme where fund.id=? and isactive=true order by name", fundId);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme List size : " + (schemeList != null ? schemeList.size() : 0));
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSchemes.");
        return "schemes";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSchemeBy20")
    public String ajaxLoadSchemeBy20() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSchemeBy20...");
        /*
         * Struts 7 migration note:
         * Some legacy AJAX calls submit fund/fundId as request parameters without
         * reliably binding the action field first. Re-read the request value before
         * building the query so autocomplete results are filtered correctly.
         */
        if (fundId == null) {
            String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId == null || fId.trim().isEmpty()) {
                fId = ServletActionContext.getRequest().getParameter("fund");
            }
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                fundId = Long.valueOf(fId.trim());
            }
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Fund Id received is :  " + fundId + "   and Startswith   :" + startsWith);
        startsWith = "%" + startsWith + "%";
        schemeList = new ArrayList<Scheme>();
        final String qry = "from Scheme  where upper(code) like upper(?) or upper(name) like upper(?) and isactive=true ";
        if (null != fundId && fundId != -1)
            schemeList.addAll(getPersistenceService().findPageBy(qry + " and fund.id=(?) order by code,name ", 0,
                    20, startsWith, startsWith, fundId).getList());
        else
            schemeList.addAll(getPersistenceService().findPageBy(qry + " order by code,name  ", 0, 20, startsWith,
                    startsWith).getList());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme List size : " + schemeList.size());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSchemeBy20.");
        return "schemeBy20";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSubSchemes")
    public String ajaxLoadSubSchemes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSubSchemes...");
        /*
         * Java 17 / Hibernate 6 / Struts 7 migration note:
         * Struts can leave schemeId unset for old AJAX callers that pass scheme or
         * schemeId directly. Resolve it from the request and keep the HQL property as
         * isactive, matching the SubScheme entity mapping used by Hibernate 6.
         */
        if (schemeId == null) {
            String sId = ServletActionContext.getRequest().getParameter("schemeId");
            if (sId == null || sId.trim().isEmpty()) {
                sId = ServletActionContext.getRequest().getParameter("scheme");
            }
            if (sId != null && !sId.trim().isEmpty() && !"-1".equals(sId.trim())) {
                schemeId = Integer.valueOf(sId.trim());
            }
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme Id received is : " + schemeId);
        if (null != schemeId && schemeId != -1) {
            subSchemes = getPersistenceService().findAllBy("from SubScheme where scheme.id=? and isactive=true order by name",
                    schemeId);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Subscheme List size : " + (subSchemes != null ? subSchemes.size() : 0));
        } else
            subSchemes = Collections.EMPTY_LIST;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSubSchemes.");

        return Constants.SUBSCHEMES;
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSubSchemeBy20")
    public String ajaxLoadSubSchemeBy20() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSubSchemeBy20...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("schemeId Id received is :  " + schemeId + "   and Startswith   :" + startsWith);
        startsWith = "%" + startsWith + "%";
        subSchemes = new ArrayList<SubScheme>();
        final String qry = "from SubScheme  where upper(code) like upper(?) or upper(name) like upper(?) and isactive=true ";
        if (null != schemeId)
            subSchemes.addAll(getPersistenceService().findPageBy(qry + " and scheme.id=(?) order by code,name",
                    0, 20, startsWith, startsWith, schemeId).getList());
        else
            subSchemes.addAll(getPersistenceService().findPageBy(qry + " order by code,name ", 0, 20,
                    startsWith, startsWith).getList());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme List size : " + subSchemes.size());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSubSchemeBy20.");
        return "subSchemeBy20";
    }

    /**
     * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade) — Bank to Bank Transfer
     * and other Fund → Bank AJAX dropdowns:
     * <ol>
     * <li>Struts 7 does not bind YUI AJAX query params ({@code fundId},
     * {@code typeOfAccount}) onto action fields. Read them from the request via
     * {@link #resolveFundIdAndAsOnDate()}.</li>
     * <li>{@code @SkipValidation} is required so validation/input does not
     * replace the JSON result.</li>
     * <li>Hibernate 6 rejects uncast {@code concat(bank.id, ...)} and string
     * literals in {@code Bankaccount.type IN (...)}. Use {@code cast(... as string)}
     * and enum constants.</li>
     * <li>If the type filter returns empty (or the query fails), retry without
     * type and then fall back to active payment banks so the UI is usable.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBanks")
    public String ajaxLoadBanks() {
        bankBranchList = new ArrayList<Map<String, Object>>();
        resolveFundIdAndAsOnDate();
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty())
            typeOfAccount = ServletActionContext.getRequest().getParameter("typeOfAccount");
        LOGGER.info("ajaxLoadBanks fundId=" + fundId + " typeOfAccount=" + typeOfAccount);
        try {
            if (fundId != null && fundId != -1 && fundId != 0) {
                final StringBuilder hql = new StringBuilder();
                hql.append("select distinct concat(cast(b.id as string), '-', cast(bb.id as string)), concat(b.name, ' - ', bb.branchname) ")
                        .append("from Bankaccount ba join ba.bankbranch bb join bb.bank b ")
                        .append("where ba.isactive=true and bb.isactive=true and b.isactive=true ")
                        .append("and ba.fund.id = ?1 ");
                appendBankAccountTypeFilter(hql);
                hql.append(" order by 2");
                final List<Object[]> bankBranch = getPersistenceService().findAllBy(hql.toString(), fundId);
                LOGGER.info("ajaxLoadBanks count=" + (bankBranch != null ? bankBranch.size() : 0));
                if (bankBranch != null) {
                    for (final Object[] element : bankBranch) {
                        if (element == null || element.length < 2 || element[0] == null || element[1] == null)
                            continue;
                        final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                        bankBrmap.put("bankBranchId", element[0].toString());
                        bankBrmap.put("bankBranchName", element[1].toString());
                        bankBranchList.add(bankBrmap);
                    }
                }
            } else {
                LOGGER.warn("ajaxLoadBanks fundId is null/invalid: " + fundId);
            }
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadBanks failed", e);
        }
        if (bankBranchList.isEmpty() && fundId != null && fundId != -1 && fundId != 0
                && typeOfAccount != null && !typeOfAccount.trim().isEmpty()) {
            LOGGER.warn("ajaxLoadBanks empty for types " + typeOfAccount + "; retrying without type filter");
            typeOfAccount = null;
            try {
                final String hql = "select distinct concat(cast(b.id as string), '-', cast(bb.id as string)), concat(b.name, ' - ', bb.branchname) "
                        + "from Bankaccount ba join ba.bankbranch bb join bb.bank b "
                        + "where ba.isactive=true and bb.isactive=true and b.isactive=true "
                        + "and ba.fund.id = ?1 order by 2";
                final List<Object[]> bankBranch = getPersistenceService().findAllBy(hql, fundId);
                if (bankBranch != null) {
                    for (final Object[] element : bankBranch) {
                        if (element == null || element.length < 2 || element[0] == null || element[1] == null)
                            continue;
                        final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                        bankBrmap.put("bankBranchId", element[0].toString());
                        bankBrmap.put("bankBranchName", element[1].toString());
                        bankBranchList.add(bankBrmap);
                    }
                }
            } catch (final Exception e) {
                LOGGER.error("ajaxLoadBanks retry without type failed", e);
            }
        }
        if (bankBranchList.isEmpty() && fundId != null && fundId != -1 && fundId != 0)
            loadActivePaymentBanksForFund();
        return "bank";
    }

    /**
     * LTS Migration Fix (Hibernate 6 Upgrade):
     * {@code Bankaccount.type} is {@link BankAccountType}. Hibernate 6 no longer
     * coerces string literals in {@code IN ('PAYMENTS',...)} and the AJAX bank
     * query fails. Emit fully-qualified enum constants instead.
     */
    private void appendBankAccountTypeFilter(final StringBuilder hql) {
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty())
            return;
        final String[] types = typeOfAccount.split(",");
        final StringBuilder inClause = new StringBuilder();
        for (final String type : types) {
            final String trimmed = type.trim();
            if (trimmed.isEmpty())
                continue;
            try {
                BankAccountType.valueOf(trimmed);
            } catch (final IllegalArgumentException e) {
                LOGGER.warn("Ignoring invalid bank account type: " + trimmed);
                continue;
            }
            if (inClause.length() > 0)
                inClause.append(",");
            inClause.append("org.egov.commons.utils.BankAccountType.").append(trimmed);
        }
        if (inClause.length() > 0)
            hql.append("and ba.type in (").append(inClause).append(") ");
    }

    @SuppressWarnings("unchecked")
    // @Deprecated
    @Action(value = "/voucher/common-ajaxLoadAllBanks")
    public String ajaxLoadAllBanks() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadAllBanks...");
        String fundChk = "";
        List<Object[]> bankBranch;
        final StringBuffer bankQuery = new StringBuffer();
        if (fundId != null)
            // ajaxLoadBanks();
            fundChk = " and bankaccount.fund.id=?";
        try {
            bankQuery
                    /*
                     * Hibernate 6 migration note:
                     * concat no longer tolerates mixed numeric/string arguments without
                     * explicit casts. Cast bank and branch ids to string before building
                     * the legacy "bankId-branchId" dropdown key.
                     */
                    .append("select DISTINCT concat(cast(bank.id as string), '-', cast(bankBranch.id as string)) as bankbranchid, concat(bank.name, ' ', bankBranch.branchname) as bankbranchname FROM Bank bank, Bankbranch bankBranch, Bankaccount bankaccount ")
                    .append(" where bank.isactive=true and bankBranch.isactive=true and bankaccount.isactive=true and bank.id = bankBranch.bank.id ")
                    .append("and bankBranch.id = bankaccount.bankbranch.id");
            if (fundId != null)
                bankBranch = getPersistenceService().findAllBy(
                        bankQuery.append(fundChk).toString() + " order by 2", fundId);
            else
                bankBranch = getPersistenceService().findAllBy(bankQuery.toString() + " order by 2");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            Map<String, Object> bankBrmap;
            for (final Object[] element : bankBranch) {
                bankBrmap = new HashMap<String, Object>();
                bankBrmap.put("bankBranchId", element[0].toString());
                bankBrmap.put("bankBranchName", element[1].toString());
                bankBranchList.add(bankBrmap);
            }

        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));

        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadAllBanks.");
        return "bank";

    }

    /**
     * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade) — Bank Entries Not In
     * Bank Book Fund → Bank dropdown:
     * <ol>
     * <li>Struts 7 does not bind {@code fundId} on the YUI AJAX GET.</li>
     * <li>{@code @SkipValidation} so JSON is returned.</li>
     * <li>Hibernate 6: {@code Bankaccount.type IN ('PAYMENTS',...)} must use
     * enum constants, not strings.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadAllBanksByFund")
    public String ajaxLoadAllBanksByFund() {
        bankList = new ArrayList<Bank>();
        resolveFundIdAndAsOnDate();
        LOGGER.info("ajaxLoadAllBanksByFund fundId=" + fundId);
        try {
            final String typeIn = "org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS, "
                    + "org.egov.commons.utils.BankAccountType.PAYMENTS";
            if (fundId != null && fundId != -1 && fundId != 0)
                bankList = getPersistenceService().findAllBy(
                        "select distinct b from Bank b, Bankbranch bb, Bankaccount ba where bb.bank=b and ba.bankbranch=bb "
                                + "and ba.type in (" + typeIn + ") and ba.fund.id=?1 and b.isactive=true",
                        fundId);
            else
                bankList = getPersistenceService().findAllBy(
                        "select distinct b from Bank b, Bankbranch bb, Bankaccount ba where bb.bank=b and ba.bankbranch=bb "
                                + "and ba.type in (" + typeIn + ") and b.isactive=true");
            if (bankList == null)
                bankList = new ArrayList<Bank>();
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadAllBanksByFund failed", e);
            bankList = new ArrayList<Bank>();
        }
        LOGGER.info("ajaxLoadAllBanksByFund count=" + bankList.size());
        return "bankByFund";
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBanksByFundAndType")
    public String ajaxLoadBanksByFundAndType() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksByFundAndType...");

        /*
         * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade):
         * -----------------------------------------------------
         * 1. Struts 7 Parameter Sandboxing:
         *    Resolve fundId and typeOfAccount from HttpServletRequest fallback if not bound by Struts.
         * 2. Null-Safety:
         *    Safely handle null/empty typeOfAccount to prevent NPE on typeOfAccount.indexOf().
         * 3. Hibernate 6 SQM Concat & Cast:
         *    Explicit string casts for numeric IDs in concat() for dropdown key format "bankId-branchId".
         * 4. Position parameters: Use numbered parameter ?1.
         */
        if (fundId == null) {
            final String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (final NumberFormatException e) {
                    fundId = null;
                }
            }
        }
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty()) {
            typeOfAccount = ServletActionContext.getRequest().getParameter("typeOfAccount");
        }
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty()) {
            typeOfAccount = "PAYMENTS,RECEIPTS_PAYMENTS";
        }

        bankBranchList = new ArrayList<Map<String, Object>>();
        if (fundId == null || fundId == -1 || fundId == 0) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("FundId is null or invalid: " + fundId);
            return "bank";
        }

        final StringBuilder query = new StringBuilder();
        query.append(
                "select DISTINCT concat(cast(bank.id as string), '-', cast(bankBranch.id as string)) as bankbranchid, concat(bank.name, ' ', bankBranch.branchname) as bankbranchname ")
                .append("FROM Bank bank, Bankbranch bankBranch, Bankaccount bankaccount where bank.isactive=true and bankBranch.isactive=true and ")
                .append(" bankaccount.isactive=true and bank.id = bankBranch.bank.id and bankBranch.id = bankaccount.bankbranch.id ")
                .append("and bankaccount.fund.id=?1 and bankaccount.type in (");

        int index = 0;
        final String[] strArray = typeOfAccount.split(",");
        for (final String type : strArray) {
            final String trimmed = type.trim();
            if (!trimmed.isEmpty()) {
                try {
                    BankAccountType.valueOf(trimmed);
                } catch (final IllegalArgumentException invalidType) {
                    LOGGER.warn("Ignoring invalid bank account type in AJAX filter: " + trimmed);
                    continue;
                }
                if (index > 0)
                    query.append(",");
                // Hibernate 6: Bankaccount.type is BankAccountType enum. String
                // literals in IN (...) no longer coerce and the query fails.
                query.append("org.egov.commons.utils.BankAccountType.").append(trimmed);
                index++;
            }
        }
        if (index == 0) {
            query.append("org.egov.commons.utils.BankAccountType.PAYMENTS,org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS");
        }
        query.append(") order by 2 ");

        try {
            final List<Object[]> bankBranch = getPersistenceService().findAllBy(query.toString(), fundId);
            if (bankBranch != null) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Bank list size is " + bankBranch.size());
                for (final Object[] element : bankBranch) {
                    if (element != null && element.length >= 2 && element[0] != null && element[1] != null) {
                        final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                        bankBrmap.put("bankBranchId", element[0].toString());
                        bankBrmap.put("bankBranchName", element[1].toString());
                        bankBranchList.add(bankBrmap);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception occurred while getting data for bank dropdown: " + e.getMessage(), e);
        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksByFundAndType.");
        return "bank";
    }

    @SuppressWarnings("unchecked")
    @Deprecated
    @Action(value = "/voucher/common-ajaxLoadAccNum")
    public String ajaxLoadAccNum() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadAccNum...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadAccNum");
        try {
            /*
             * Struts 7 / Hibernate 6 migration note:
             * Legacy callers may pass branchId/bankId only as request parameters. Load
             * them before querying and skip bank filtering when the bank value is not
             * available, preventing null parameter binding failures.
             */
            if (branchId == null) {
                String bId = ServletActionContext.getRequest().getParameter("branchId");
                if (bId != null && !bId.trim().isEmpty()) {
                    branchId = Integer.valueOf(bId.trim());
                }
            }
            if (bankId == null) {
                String bkId = ServletActionContext.getRequest().getParameter("bankId");
                if (bkId != null && !bkId.trim().isEmpty()) {
                    bankId = Integer.valueOf(bkId.trim());
                }
            }

            if (branchId != null && bankId != null && bankId > 0) {
                accNumList = getPersistenceService()
                        .findAllBy(
                                "from Bankaccount ba where ba.bankbranch.id=? and ba.bankbranch.bank.id=? and ba.isactive=true order by ba.chartofaccounts.glcode",
                                branchId, bankId);
            } else if (branchId != null) {
                accNumList = getPersistenceService()
                        .findAllBy(
                                "from Bankaccount ba where ba.bankbranch.id=? and ba.isactive=true order by ba.chartofaccounts.glcode",
                                branchId);
            } else {
                accNumList = new ArrayList<Bankaccount>();
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank account Number list size =  " + (accNumList != null ? accNumList.size() : 0));
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting bank account numbers " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadAccNum.");
        return "bankAccNum";
    }

    /**
     * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade) — Manual / Auto BRS
     * Bank Account dropdown after Branch is selected:
     * <ol>
     * <li>Struts 7 does not bind {@code branchId} on the YUI AJAX GET.</li>
     * <li>{@code @SkipValidation} so JSON is returned instead of the input page.</li>
     * <li>HQL uses {@code ba.isactive} and {@code ?1}; unqualified {@code isactive}
     * fails under Hibernate 6.</li>
     * <li>Unproxy nested GL/bank so Struts 7 OGNL can render the JSON JSP.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankAccountsByBranch")
    public String ajaxLoadBankAccountsByBranch() {
        accNumList = new ArrayList<Bankaccount>();
        if (branchId == null) {
            String brId = ServletActionContext.getRequest().getParameter("branchId");
            if (brId == null || brId.trim().isEmpty())
                brId = ServletActionContext.getRequest().getParameter("bankbranch");
            if (brId != null && !brId.trim().isEmpty() && !"-1".equals(brId.trim())) {
                try {
                    branchId = Integer.valueOf(brId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid branchId for BRS accounts: " + brId);
                }
            }
        }
        if (fundId == null) {
            String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid fundId for BRS/BENIBB accounts: " + fId);
                }
            }
        }
        LOGGER.info("ajaxLoadBankAccountsByBranch branchId=" + branchId + " fundId=" + fundId);
        if (branchId == null || branchId <= 0)
            return "bankAccountByBranch";
        try {
            accNumList = loadBankAccountsForBranch(false);
            /*
             * Bank Entries Not In Bank Book now also sends fundId. If that
             * fund has no accounts on the selected branch, retry without the
             * fund filter so the Account Number dropdown is not left empty.
             */
            if ((accNumList == null || accNumList.isEmpty()) && fundId != null) {
                LOGGER.warn("ajaxLoadBankAccountsByBranch empty with fundId=" + fundId
                        + "; retrying without fund filter");
                final Long savedFund = fundId;
                fundId = null;
                accNumList = loadBankAccountsForBranch(false);
                fundId = savedFund;
            }
            if (accNumList == null)
                accNumList = new ArrayList<Bankaccount>();
            prepareAccountsForDropdown(accNumList);
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadBankAccountsByBranch failed", e);
            accNumList = new ArrayList<Bankaccount>();
        }
        LOGGER.info("ajaxLoadBankAccountsByBranch count=" + accNumList.size());
        return "bankAccountByBranch";
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankBranchFromBank")
    public String ajaxLoadBankBranchFromBank() {

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankBranchFromBank...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadBankBranchFromBank");
        try {
            /*
             * Struts 7 migration note:
             * The bank/fund dropdown AJAX calls use both old and new parameter names.
             * Resolve bankId/fundId from the request to keep dependent branch loading
             * working when action property binding does not run first.
             */
            if (bankId == null) {
                String bId = ServletActionContext.getRequest().getParameter("bankId");
                if (bId == null || bId.trim().isEmpty()) {
                    bId = ServletActionContext.getRequest().getParameter("bank");
                }
                if (bId != null && !bId.trim().isEmpty() && !"-1".equals(bId.trim())) {
                    bankId = Integer.valueOf(bId.trim());
                }
            }
            if (fundId == null) {
                String fId = ServletActionContext.getRequest().getParameter("fundId");
                if (fId == null || fId.trim().isEmpty()) {
                    fId = ServletActionContext.getRequest().getParameter("fund");
                }
                if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                    fundId = Long.valueOf(fId.trim());
                }
            }

            if (bankId != null && bankId > 0) {
                /*
                 * LTS Migration Fix (Hibernate 6): Bankaccount.type is an enum.
                 * String literals in IN (...) no longer coerce.
                 */
                if (fundId != null && fundId > 0)
                    branchList = getPersistenceService()
                            .findAllBy(
                                    "select distinct bb from Bankbranch bb , Bankaccount ba  where ba.bankbranch =bb and ba.type in (org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS, org.egov.commons.utils.BankAccountType.PAYMENTS) and bb.bank.id=?1 and bb.isactive=true and ba.fund.id=?2",
                                    bankId, fundId);
                else
                    branchList = getPersistenceService()
                            .findAllBy(
                                    "select distinct bb from Bankbranch bb , Bankaccount ba  where ba.bankbranch =bb and ba.type in (org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS, org.egov.commons.utils.BankAccountType.PAYMENTS) and bb.bank.id=?1 and bb.isactive=true",
                                    bankId);
            } else {
                branchList = new ArrayList<Bankbranch>();
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank Branch Number list size =  " + (branchList != null ? branchList.size() : 0));
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting bank branch " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankBranchFromBank.");
        return "branch";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBankAccFromBranch")
    public String ajaxLoadBankAccFromBranch() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccFromBranch...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadBankAccFromBranch");
        try {
            /*
             * Struts 7 migration note:
             * Branch and fund can arrive as branchId, bankbranch, bankbranch.id, fund,
             * or fundId depending on the legacy page. Normalize those names here before
             * executing Hibernate queries.
             */
            if (branchId == null) {
                String brId = ServletActionContext.getRequest().getParameter("branchId");
                if (brId == null || brId.trim().isEmpty()) {
                    brId = ServletActionContext.getRequest().getParameter("bankbranch");
                }
                if (brId == null || brId.trim().isEmpty()) {
                    brId = ServletActionContext.getRequest().getParameter("bankbranch.id");
                }
                if (brId != null && !brId.trim().isEmpty() && !"-1".equals(brId.trim())) {
                    branchId = Integer.valueOf(brId.trim());
                }
            }
            if (fundId == null) {
                String fId = ServletActionContext.getRequest().getParameter("fundId");
                if (fId == null || fId.trim().isEmpty()) {
                    fId = ServletActionContext.getRequest().getParameter("fund");
                }
                if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                    fundId = Long.valueOf(fId.trim());
                }
            }

            if (branchId != null && branchId > 0) {
                if (fundId != null && fundId > 0)
                    accNumList = getPersistenceService()
                            .findAllBy(
                                    "from Bankaccount ba where ba.bankbranch.id=? and ba.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and ba.isactive=true and ba.fund.id=?",
                                    branchId, fundId);
                else
                    accNumList = getPersistenceService()
                            .findAllBy(
                                    "from Bankaccount ba where ba.bankbranch.id=? and ba.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and ba.isactive=true",
                                    branchId);
            } else {
                accNumList = new ArrayList<Bankaccount>();
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank Account Number list size =  " + (accNumList != null ? accNumList.size() : 0));
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting bank account " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccFromBranch.");
        return "bankAccNum";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadRTGSChequeFromBankAcc")
    public String ajaxLoadRTGSChequeFromBankAcc() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadRTGSChequeFromBankAcc...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadRTGSChequeFromBankAcc");
        List<Object[]> resultList = new ArrayList<Object[]>();
        new ArrayList<Object>();
        // rtgsNumber=;
        instrumentHeaderList = new ArrayList<InstrumentHeader>();
        /*
         * if(!StringUtils.isEmpty(query)) { strquery=
         * "select appReq from ApplicationRequest appReq where upper(appReq.applicationNo) like '%'||?||'%' ";
         * params.add(query.toUpperCase()); if(!StringUtils.isEmpty(citizenId)) { strquery = strquery +
         * " and appReq.citizenDetails.id=? "; params.add(Long.parseLong(citizenId)); } applicationRequest =
         * getPersistenceService().findAllBy(strquery,params.toArray()); }
         */
        /*
         * try { queryStr= " FROM InstrumentHeader ih, InstrumentVoucher iv, Paymentheader ph "+
         * "WHERE ih.isPayCheque ='1' AND ih.bankAccountId.id = ? AND ih.statusId.description in ('New')" +
         * " AND ih.statusId.moduletype='Instrument' AND iv.instrumentHeaderId = ih.id and ih.bankAccountId is not null "+
         * "AND iv.voucherHeaderId     = ph.voucherheader AND ph.bankaccount = ih.bankAccountId AND ph.type = '"+
         * FinancialConstants.MODEOFPAYMENT_RTGS+"' "+" AND upper(ih.transactionNumber) like '%'||?||'%' "+
         * "GROUP BY ih.transactionNumber,ih.id"; params.add(bankaccountId); params.add(rtgsNumber.toUpperCase());
         * instrumentHeaderList= getPersistenceService().findAllBy(queryStr,params.toArray()); /*for(Object[] obj:resultList){
         * InstrumentHeader ih = new InstrumentHeader(); ih = (InstrumentHeader) persistenceService.find(
         * "from InstrumentHeader where id=?", (Long)obj[0]); instrumentHeaderList.add(ih); }
         */
        // instrumentHeaderList=new ArrayList<InstrumentHeader>();
        try {
            /*
             * Struts 7 / Hibernate 6 migration note:
             * RTGS lookup depends on bankaccountId, which older pages submit under
             * multiple names. Resolve it before running RTGSNUMBERSQUERY; if no valid
             * id is present, skip the query to avoid strict null parameter binding.
             */
            if (bankaccountId == null) {
                String bAccId = ServletActionContext.getRequest().getParameter("bankaccountId");
                if (bAccId == null || bAccId.trim().isEmpty()) {
                    bAccId = ServletActionContext.getRequest().getParameter("bankaccount.id");
                }
                if (bAccId == null || bAccId.trim().isEmpty()) {
                    bAccId = ServletActionContext.getRequest().getParameter("bankaccount");
                }
                if (bAccId != null && !bAccId.trim().isEmpty() && !"-1".equals(bAccId.trim())) {
                    bankaccountId = Long.valueOf(bAccId.trim());
                }
            }

            final Calendar calendar = Calendar.getInstance();
            calendar.get(Calendar.DATE);
            calendar.add(Calendar.DATE, -7);
            calendar.get(Calendar.DATE);
            final Date date = calendar.getTime();
            final SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy");
            final String date1 = sdf.format(date);

            if (bankaccountId != null && bankaccountId > 0) {
                resultList = getPersistenceService()
                        .findAllBy(RTGSNUMBERSQUERY, bankaccountId);
                for (final Object[] obj : resultList) {
                    InstrumentHeader ih = new InstrumentHeader();
                    ih = (InstrumentHeader) persistenceService.find("from InstrumentHeader where id=?", (Long) obj[0]);

                    instrumentHeaderList.add(ih);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting bank account " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadRTGSChequeFromBankAcc.");
        return "instrument";
    }

    /**
     * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade) — Bank to Bank Transfer
     * Account No dropdown after Bank is selected:
     * <ol>
     * <li>Struts 7 leaves {@code branchId}, {@code fundId}, and
     * {@code typeOfAccount} unbound on the AJAX GET. Read them from the
     * request (including the {@code bankbranch}/{@code fund} aliases used by
     * older JSPs).</li>
     * <li>{@code @SkipValidation} so the JSON {@code bankAccNum} result is
     * returned instead of the input/error page.</li>
     * <li>HQL must use {@code ba.fund.id} (not unqualified {@code fund.id})
     * and numbered parameters; Hibernate 6 rejects the legacy path.</li>
     * <li>If the type filter is empty, retry without type, then fall back to
     * active payment accounts. Unproxy nested bank/GL associations so Struts 7
     * OGNL can render {@code common-bankAccNum.jsp}.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadAccountNumbers")
    public String ajaxLoadAccountNumbers() {
        accNumList = new ArrayList<Bankaccount>();
        if (branchId == null) {
            String brId = ServletActionContext.getRequest().getParameter("branchId");
            if (brId == null || brId.trim().isEmpty())
                brId = ServletActionContext.getRequest().getParameter("bankbranch");
            if (brId != null && !brId.trim().isEmpty() && !"-1".equals(brId.trim())) {
                try {
                    branchId = Integer.valueOf(brId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid branchId for account numbers: " + brId);
                }
            }
        }
        if (fundId == null) {
            String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId == null || fId.trim().isEmpty())
                fId = ServletActionContext.getRequest().getParameter("fund");
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid fundId for account numbers: " + fId);
                }
            }
        }
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty())
            typeOfAccount = ServletActionContext.getRequest().getParameter("typeOfAccount");
        LOGGER.info("ajaxLoadAccountNumbers fundId=" + fundId + " branchId=" + branchId + " typeOfAccount=" + typeOfAccount);
        if (branchId == null || branchId <= 0)
            return "bankAccNum";
        try {
            accNumList = loadBankAccountsForBranch(true);
            if (accNumList == null || accNumList.isEmpty()) {
                LOGGER.warn("ajaxLoadAccountNumbers empty with type filter; retrying without type");
                accNumList = loadBankAccountsForBranch(false);
            }
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadAccountNumbers failed", e);
            try {
                accNumList = loadBankAccountsForBranch(false);
            } catch (final Exception retry) {
                LOGGER.error("ajaxLoadAccountNumbers retry failed", retry);
                accNumList = new ArrayList<Bankaccount>();
            }
        }
        if (accNumList == null || accNumList.isEmpty())
            loadActivePaymentAccountsForBranch();
        prepareAccountsForDropdown(accNumList);
        LOGGER.info("ajaxLoadAccountNumbers count=" + (accNumList != null ? accNumList.size() : 0));
        return "bankAccNum";
    }

    /**
     * LTS Migration Fix (Hibernate 6 Upgrade):
     * Account-number HQL for BTB / contra AJAX. Uses alias-qualified paths and
     * {@code ?1}/{@code ?2} so Hibernate 6 SQM accepts the query. Type filter
     * uses enum constants via {@link #appendBankAccountTypeFilter}.
     */
    @SuppressWarnings("unchecked")
    private List<Bankaccount> loadBankAccountsForBranch(final boolean applyTypeFilter) {
        final StringBuilder hql = new StringBuilder();
        hql.append("from Bankaccount ba where ba.bankbranch.id=?1 and ba.isactive=true ");
        if (fundId != null && fundId != -1 && fundId != 0)
            hql.append("and ba.fund.id=?2 ");
        if (applyTypeFilter)
            appendBankAccountTypeFilter(hql);
        hql.append("order by ba.chartofaccounts.glcode");
        if (fundId != null && fundId != -1 && fundId != 0)
            return getPersistenceService().findAllBy(hql.toString(), branchId, fundId);
        return getPersistenceService().findAllBy(hql.toString(), branchId);
    }

    /**
     * LTS Migration Fix (Hibernate 6 & Struts 7 Upgrade):
     * PersistenceService unproxies the {@link Bankaccount} row, but nested
     * {@code chartofaccounts} / {@code bankbranch.bank} stay ByteBuddy proxies.
     * Struts 7 OGNL allowlisting cannot read those proxies, so
     * {@code common-bankAccNum.jsp} rendered empty Text/Value. Initialize and
     * unproxy the graph while the session is open.
     */
    @SuppressWarnings("unchecked")
    private void prepareAccountsForDropdown(final List<Bankaccount> accounts) {
        if (accounts == null)
            return;
        for (int i = 0; i < accounts.size(); i++) {
            Bankaccount account = accounts.get(i);
            if (account == null)
                continue;
            Hibernate.initialize(account);
            account = (Bankaccount) Hibernate.unproxy(account);
            if (account.getChartofaccounts() != null) {
                Hibernate.initialize(account.getChartofaccounts());
                account.setChartofaccounts((CChartOfAccounts) Hibernate.unproxy(account.getChartofaccounts()));
            }
            if (account.getBankbranch() != null) {
                Hibernate.initialize(account.getBankbranch());
                final Bankbranch branch = (Bankbranch) Hibernate.unproxy(account.getBankbranch());
                account.setBankbranch(branch);
                if (branch.getBank() != null) {
                    Hibernate.initialize(branch.getBank());
                    branch.setBank((Bank) Hibernate.unproxy(branch.getBank()));
                }
            }
            accounts.set(i, account);
        }
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadDrawingOfficers")
    public String ajaxLoadDrawingOfficers() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadDrawingOfficers...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadDrawingOfficers");
        try {
            /*
             * Struts 7 migration note:
             * Department is posted under different parameter names by older JSPs. Read
             * both variants before querying so the drawing officer dropdown does not
             * fail with a null department filter.
             */
            if (departmentId == null) {
                departmentId = ServletActionContext.getRequest().getParameter("departmentId");
                if (departmentId == null || departmentId.trim().isEmpty()) {
                    departmentId = ServletActionContext.getRequest().getParameter("department");
                }
            }
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                drawingList = getPersistenceService()
                        .findAllBy(
                                "select do from DrawingOfficer do,Department dept,DepartmentDOMapping ddm where ddm.department.id = dept.id and ddm.drawingOfficer.id = do.id and dept.id = ?",
                                departmentId);
            else
                drawingList = new ArrayList<DrawingOfficer>();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Drawing officers  list size =  " + (drawingList != null ? drawingList.size() : 0));
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting Drawing officers " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadDrawingOfficers.");
        return "drawingOffcer";
    }

    @Action(value = "/voucher/common-ajaxLoadAccNumAndType")
    public String ajaxLoadAccNumAndType() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadAccNumAndType...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadAccNum");
        try {
            /*
             * Struts 7 migration note:
             * Account-number dropdowns are chained AJAX calls. Resolve branch, bank,
             * fund, and account type from request parameters when they were not bound
             * to the action, and return an empty list when mandatory filters are absent.
             */
            if (branchId == null) {
                String brId = ServletActionContext.getRequest().getParameter("branchId");
                if (brId == null || brId.trim().isEmpty()) {
                    brId = ServletActionContext.getRequest().getParameter("bankbranch");
                }
                if (brId != null && !brId.trim().isEmpty() && !"-1".equals(brId.trim())) {
                    branchId = Integer.valueOf(brId.trim());
                }
            }
            if (bankId == null) {
                String bkId = ServletActionContext.getRequest().getParameter("bankId");
                if (bkId == null || bkId.trim().isEmpty()) {
                    bkId = ServletActionContext.getRequest().getParameter("bank");
                }
                if (bkId != null && !bkId.trim().isEmpty() && !"-1".equals(bkId.trim())) {
                    bankId = Integer.valueOf(bkId.trim());
                }
            }
            if (fundId == null) {
                String fId = ServletActionContext.getRequest().getParameter("fundId");
                if (fId == null || fId.trim().isEmpty()) {
                    fId = ServletActionContext.getRequest().getParameter("fund");
                }
                if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                    fundId = Long.valueOf(fId.trim());
                }
            }
            if (typeOfAccount == null || typeOfAccount.trim().isEmpty()) {
                typeOfAccount = ServletActionContext.getRequest().getParameter("typeOfAccount");
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("typeOfAccount in  ajaxLoadBankAccounts method >>>>>>>" + typeOfAccount);
            if (typeOfAccount != null && !typeOfAccount.equals("")) {
                if (typeOfAccount.indexOf(",") != -1) {
                    final String[] strArray = typeOfAccount.split(",");
                    if (fundId != null && fundId > 0)
                        accNumList = getPersistenceService()
                                .findAllBy(
                                        "from Bankaccount ba where ba.bankbranch.id=? and ba.fund.id=? and ba.bankbranch.bank.id=? and isactive=true and type in (?, ?) order by ba.chartofaccounts.glcode",
                                        branchId, fundId, bankId, BankAccountType.valueOf(strArray[0].toUpperCase()),
                                        BankAccountType.valueOf(strArray[1].toUpperCase()));
                    else
                        accNumList = getPersistenceService()
                                .findAllBy(
                                        "from Bankaccount ba where ba.bankbranch.id=? and  ba.bankbranch.bank.id=? and isactive=true and type in (?, ?) order by ba.chartofaccounts.glcode",
                                        branchId, bankId, BankAccountType.valueOf(strArray[0]),
                                        BankAccountType.valueOf(strArray[1]));
                } else if (fundId != null && fundId > 0)
                    accNumList = getPersistenceService()
                            .findAllBy(
                                    "from Bankaccount ba where ba.bankbranch.id=? and ba.fund.id=? and ba.bankbranch.bank.id=? and isactive=true and type in (?) order by ba.chartofaccounts.glcode",
                                    branchId, fundId, bankId, typeOfAccount);
                else
                    accNumList = getPersistenceService()
                            .findAllBy(
                                    "from Bankaccount ba where ba.bankbranch.id=?  and ba.bankbranch.bank.id=? and isactive=true and type in (?) order by ba.chartofaccounts.glcode",
                                    branchId, bankId, typeOfAccount);
            } else if (fundId != null && fundId > 0)
                accNumList = getPersistenceService()
                        .findAllBy(
                                "from Bankaccount ba where ba.bankbranch.id=? and ba.fund.id=? and ba.bankbranch.bank.id=? and isactive=true order by ba.chartofaccounts.glcode",
                                branchId, fundId, bankId);
            else if (branchId != null && bankId != null)
                accNumList = getPersistenceService()
                        .findAllBy(
                                "from Bankaccount ba where ba.bankbranch.id=?  and ba.bankbranch.bank.id=? and isactive=true order by ba.chartofaccounts.glcode",
                                branchId, bankId);
            else
                accNumList = new ArrayList<Bankaccount>();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank account Number list size =  " + (accNumList != null ? accNumList.size() : 0));
        } catch (final Exception e) {
            LOGGER.error("Exception occured while getting bank account numbers " + e.getMessage(), e);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadAccNumAndType.");
        return "bankAccNumAndType";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-loadAccNumNarration")
    public String loadAccNumNarration() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting loadAccNumNarration...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | loadAccNumNarration");
        try {
            value = "";
            final String accountNumId = parameters.get("accnum")[0];
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank account number id received = " + accountNumId);
            value = (String) getPersistenceService().find("select narration from Bankaccount where id=?",
                    Long.valueOf(accountNumId));
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Naration value = " + value);
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting bank account narration " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting bank account narration " +
           * e.getMessage(), new HibernateException(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed loadAccNumNarration.");
        return "result";
    }

    @Action(value = "/voucher/common-loadAccNumNarrationAndFund")
    public String loadAccNumNarrationAndFund() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting loadAccNumNarrationAndFund...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | loadAccNumNarration");
        try {
            value = "";
            final String accountNumId = parameters.get("accnum")[0];
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank account number id received = " + accountNumId);
            value = (String) getPersistenceService().find(
                    "select concat(concat(narration,'-'),fund.id) from Bankaccount where id=?", Integer.valueOf(accountNumId));
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Naration value = " + value);
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting bank account narration " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting bank account narration " +
           * e.getMessage(), new HibernateException(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed loadAccNumNarrationAndFund.");
        return "result";
    }

    @Action(value = "/voucher/common-getDetailType")
    public String getDetailType() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting getDetailType...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Inside getDetailType method");
        value = "";
        final String accountCode = parameters.get("accountCode")[0];
        final String index = parameters.get("index")[0];
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Account code selected is : = " + accountCode);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("index is : = " + index);
        // LTS Hibernate 6: glCodeId/detailTypeId are associations — compare via .id
        final List<Accountdetailtype> list = getPersistenceService()
                .findAllBy(
                        " from Accountdetailtype where id in (select detailTypeId.id from CChartOfAccountDetail where glCodeId.id=(select id from CChartOfAccounts where glcode=?))  ",
                        accountCode);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(" list :" + list);
        for (final Accountdetailtype accountdetailtype : list)
            value = value + index + "~" + accountdetailtype.getDescription() + "~" + accountdetailtype.getId().toString() + "#";
        if (!value.equals(""))
            value = value.substring(0, value.length() - 1);

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("The Detail type Id is :" + value);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed getDetailType.");
        return "result";
    }

    /**
     * LTS Migration Fix (Struts 7): {@code fundId} is not bound on AJAX; read
     * from the request. {@code @SkipValidation} keeps the JSON result.
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankBranch")
    public String ajaxLoadBankBranch() {
        branchList = new ArrayList<Bankbranch>();
        resolveFundIdAndAsOnDate();
        if (fundId == null || fundId == -1 || fundId == 0)
            return "branch";
        try {
            branchList = persistenceService.findAllBy(
                    "from Bankbranch br where br.id in (select bankbranch.id from Bankaccount where fund.id=?1 ) and br.isactive=true order by br.bank.name asc",
                    fundId);
            if (branchList == null)
                branchList = new ArrayList<Bankbranch>();
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadBankBranch failed", e);
            branchList = new ArrayList<Bankbranch>();
        }
        return "branch";
    }

    /**
     * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade) — Manual Bank
     * Reconciliation Bank → Branch dropdown:
     * <ol>
     * <li>Struts 7 does not bind {@code bankId} from {@code populatebranchId({bankId})}.</li>
     * <li>{@code @SkipValidation} so {@code common-branchesByBank.jsp} is returned.</li>
     * <li>Numbered HQL parameter {@code ?1} for Hibernate 6.</li>
     * </ol>
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankBranchesByBank")
    public String ajaxLoadBankBranchesByBank() {
        branchList = new ArrayList<Bankbranch>();
        if (bankId == null) {
            String bId = ServletActionContext.getRequest().getParameter("bankId");
            if (bId == null || bId.trim().isEmpty())
                bId = ServletActionContext.getRequest().getParameter("bank");
            if (bId != null && !bId.trim().isEmpty() && !"-1".equals(bId.trim())) {
                try {
                    bankId = Integer.valueOf(bId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid bankId for BRS branches: " + bId);
                }
            }
        }
        LOGGER.info("ajaxLoadBankBranchesByBank bankId=" + bankId);
        if (bankId == null || bankId <= 0)
            return "branchesByBank";
        try {
            branchList = persistenceService.findAllBy(
                    "select distinct bb from Bankbranch bb, Bankaccount ba where bb.bank.id=?1 and ba.bankbranch=bb and bb.isactive=true",
                    bankId);
            if (branchList == null)
                branchList = new ArrayList<Bankbranch>();
        } catch (final Exception e) {
            LOGGER.error("ajaxLoadBankBranchesByBank failed", e);
            branchList = new ArrayList<Bankbranch>();
        }
        LOGGER.info("ajaxLoadBankBranchesByBank count=" + branchList.size());
        return "branchesByBank";
    }

    /**
     * LTS Migration Fix (Struts 7): {@code @SkipValidation} so YUI AJAX is not
     * redirected to the input result. Nested bank/GL associations are unproxied
     * for Struts 7 OGNL after the query.
     */
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankAccounts")
    public String ajaxLoadBankAccounts() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccounts...");

        /*
         * LTS Migration Fix (Struts 7 & Hibernate 6 Upgrade):
         * -----------------------------------------------------
         * 1. Struts 7 Parameter Sandboxing:
         *    Resolve branchId, bankId, fundId, and typeOfAccount from HttpServletRequest fallback.
         * 2. Parameter alignment:
         *    Fix bug in legacy code where fundId was passed to queries that didn't have fund.id filter.
         */
        if (branchId == null) {
            final String bId = ServletActionContext.getRequest().getParameter("branchId");
            if (bId != null && !bId.trim().isEmpty() && !"-1".equals(bId.trim())) {
                try {
                    branchId = Integer.valueOf(bId.trim());
                } catch (final NumberFormatException e) {
                    branchId = null;
                }
            }
        }
        if (fundId == null) {
            final String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (final NumberFormatException e) {
                    fundId = null;
                }
            }
        }
        if (typeOfAccount == null || typeOfAccount.trim().isEmpty()) {
            typeOfAccount = ServletActionContext.getRequest().getParameter("typeOfAccount");
        }
        if (billSubType == null || billSubType.trim().isEmpty()) {
            billSubType = ServletActionContext.getRequest().getParameter("billSubType");
        }

        try {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("typeOfAccount in ajaxLoadBankAccounts method >>>>>>> " + typeOfAccount);
            if (billSubType != null && !billSubType.equalsIgnoreCase("")) {
                String bankAccount = null;
                try {
                    final List<AppConfigValues> configValues = appConfigValuesService.getConfigValuesByModuleAndKey(
                            FinancialConstants.MODULE_NAME_APPCONFIG,
                            FinancialConstants.EB_VOUCHER_PROPERTY_BANKACCOUNT);

                    for (final AppConfigValues appConfigVal : configValues)
                        bankAccount = appConfigVal.getValue();
                } catch (final ObjectNotFoundException e) {
                    throw new ApplicationRuntimeException(
                            "Appconfig value for EB Voucher properties is not defined in the system");
                }
                accNumList = persistenceService
                        .findAllBy(
                                " from Bankaccount where accountnumber=? and isactive=true order by chartofaccounts.glcode ",
                                bankAccount);
            } else if (typeOfAccount != null && !typeOfAccount.trim().isEmpty()) {
                if (typeOfAccount.indexOf(",") != -1) {
                    final String[] strArray = typeOfAccount.split(",");
                    if (fundId != null && fundId != -1 && fundId != 0)
                        accNumList = persistenceService
                                .findAllBy(
                                        " from Bankaccount where fund.id=? and bankbranch.id=? and isactive=true and type in (?,?) order by chartofaccounts.glcode ",
                                        fundId, branchId, BankAccountType.valueOf(strArray[0].trim()),
                                        BankAccountType.valueOf(strArray[1].trim()));
                    else
                        accNumList = persistenceService
                                .findAllBy(
                                        " from Bankaccount where bankbranch.id=? and isactive=true and type in (?,?) order by chartofaccounts.glcode ",
                                        branchId, BankAccountType.valueOf(strArray[0].trim()),
                                        BankAccountType.valueOf(strArray[1].trim()));
                } else if (fundId != null && fundId != -1 && fundId != 0) {
                    accNumList = persistenceService
                            .findAllBy(
                                    " from Bankaccount where fund.id=? and bankbranch.id=? and isactive=true and type in (?) order by chartofaccounts.glcode ",
                                    fundId, branchId, BankAccountType.valueOf(typeOfAccount.trim()));
                } else {
                    accNumList = persistenceService
                            .findAllBy(
                                    " from Bankaccount where bankbranch.id=? and isactive=true and type in (?) order by chartofaccounts.glcode ",
                                    branchId, BankAccountType.valueOf(typeOfAccount.trim()));
                }
            } else if (fundId != null && fundId != -1 && fundId != 0) {
                accNumList = persistenceService
                        .findAllBy(
                                " from Bankaccount where fund.id=? and bankbranch.id=? and isactive=true order by chartofaccounts.glcode",
                                fundId, branchId);
            } else {
                accNumList = persistenceService.findAllBy(
                        " from Bankaccount where bankbranch.id=? and isactive=true order by chartofaccounts.glcode",
                        branchId);
            }
        } catch (final Exception e) {
            LOGGER.error("Exception while loading ajaxLoadBankAccounts: " + e.getMessage(), e);
            accNumList = Collections.emptyList();
        }
        if (accNumList == null)
            accNumList = new ArrayList<Bankaccount>();
        prepareAccountsForDropdown(accNumList);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccounts.");
        return "bankAccNum";
    }

    @Action(value = "/voucher/common-ajaxLoadBankAccountsBySubscheme")
    public String ajaxLoadBankAccountsBySubscheme() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsBySubscheme...");

        final SubScheme subScheme = (SubScheme) persistenceService.find("from SubScheme where id =? ", subSchemeId);
        fundId = subScheme.getScheme().getFund().getId();
        final String[] strArray = typeOfAccount.split(",");
        accNumList = persistenceService.findAllBy(
                " from Bankaccount where fund.id=? and isactive=true  and type in (?,?) order by chartofaccounts.glcode ",
                fundId,
                strArray[0], strArray[1]);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsBySubscheme.");
        return "bankAccNum";
    }

    public Long getFundId() {
        return fundId;
    }

    public void setFundId(final Long fundId) {
        this.fundId = fundId;
    }

    public String ajaxValidateDetailCode() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxValidateDetailCode...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Inside ajaxValidateDetailCode method");
        final String code = parameters.get("code")[0];
        final String index = parameters.get("index")[0];
        try {

            final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(" from Accountdetailtype where id=?",
                    Integer.valueOf(parameters.get("detailtypeid")[0]));
            if (adt == null) {
                value = index + "~" + ERROR;
                return "result";
            }
            if (adt.getTablename().equalsIgnoreCase("EG_EMPLOYEE")) {
                final PersonalInformation information = (PersonalInformation) getPersistenceService().find(
                        " from PersonalInformation where employeeCode=? and isActive=true", code);
                if (information == null)
                    value = index + "~" + ERROR;
                else
                    value = index + "~" + information.getIdPersonalInformation() + "~" + information.getEmployeeFirstName();
            } else if (adt.getTablename().equalsIgnoreCase("RELATION")) {
                final Relation relation = (Relation) getPersistenceService().find(
                        " from Relation where code=? and isactive=true",
                        code);
                if (relation == null)
                    value = index + "~" + ERROR;
                else
                    value = index + "~" + relation.getId() + "~" + relation.getName();
            } else if (adt.getTablename().equalsIgnoreCase("ACCOUNTENTITYMASTER")) {
                final AccountEntity accountEntity = (AccountEntity) getPersistenceService().find(
                        " from AccountEntity where code=? and isactive=true ", code);
                if (accountEntity == null)
                    value = index + "~" + ERROR;
                else
                    value = index + "~" + accountEntity.getId() + "~" + accountEntity.getCode();
            }
        } catch (final HibernateException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Exception occuerd while getting detail code " + e.getMessage());
            value = index + "~" + ERROR;
        } /*
           * catch (final Exception e) { if (LOGGER.isDebugEnabled())
           * LOGGER.debug("Exception occuerd while getting detail code " +
           * e.getMessage()); value = index + "~" + ERROR; }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxValidateDetailCode.");
        return "result";
    }

    @Action(value = "/voucher/common-getDetailCode")
    public String getDetailCode() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting getDetailCode...");
        value = "";
        final String arr[] = parameters.get("accountCodes")[0].split(",");
        final List<String> list = Arrays.asList(arr); // remove duplicate account codes.
        final Set<String> set = new HashSet<String>(list);
        final String[] accountCodes = new String[set.size()];
        set.toArray(accountCodes);
        for (final String accountCode : accountCodes) {

            // LTS Hibernate 6: glCodeId is CChartOfAccounts — compare via .id
            final CChartOfAccountDetail chartOfAccountDetail = (CChartOfAccountDetail) getPersistenceService().find(
                    " from CChartOfAccountDetail where glCodeId.id=(select id from CChartOfAccounts where glcode=?)",
                    accountCode);

            if (null != chartOfAccountDetail)
                if (value.trim().length() != 0)
                    value = value + "~" + accountCode + "~" + chartOfAccountDetail.getGlCodeId().getId().toString();
                else
                    value = accountCode + "~" + chartOfAccountDetail.getGlCodeId().getId().toString();

        } /*
           * if(values.trim().length()!=0){ values=index+"~"+values; }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("The account Detail  codes are :" + value);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed getDetailCode.");
        return "result";
    }

    public String ajaxGetDetailCode() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetDetailCode...");
        final String index = parameters.get("index")[0];
        try {
            final Accountdetailtype adt = (Accountdetailtype) getPersistenceService().find(" from Accountdetailtype where id=?",
                    Integer.valueOf(parameters.get("detailtypeid")[0]));
            if (adt == null) {
                value = index + "~" + ERROR;
                return "result";
            }
            if (adt.getTablename().equalsIgnoreCase("EG_EMPLOYEE")) {
                final List<PersonalInformation> information = getPersistenceService().findAllBy(
                        "from PersonalInformation where isActive=true order by employeeCode");
                if (information == null)
                    value = index + "~" + ERROR;
                else
                    for (final PersonalInformation personalInformation : information)
                        detailCodes.add(personalInformation.getIdPersonalInformation() + "-"
                                + personalInformation.getEmployeeFirstName());
            } else if (adt.getTablename().equalsIgnoreCase("RELATION")) {
                final List<Relation> relation = getPersistenceService().findAllBy(
                        "from Relation where isactive=true order by code");
                if (relation == null)
                    value = index + "~" + ERROR;
                else
                    for (final Relation rel : relation)
                        detailCodes.add(rel.getId() + "-" + rel.getName());
            } else if (adt.getTablename().equalsIgnoreCase("ACCOUNTENTITYMASTER")) {
                final List<AccountEntity> accountEntity = getPersistenceService().findAllBy(
                        " from AccountEntity where isactive=true order by code");
                if (accountEntity == null)
                    value = index + "~" + ERROR;
                else
                    for (final AccountEntity rel : accountEntity)
                        detailCodes.add(rel.getId() + "-" + rel.getCode());
            }
        } catch (final HibernateException e) {
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Exception occuerd while getting detail code " + e.getMessage());
            value = index + "~" + ERROR;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetDetailCode.");
        return "detailedCodes";
    }

    @Deprecated
    @Action(value = "/voucher/common-ajaxLoadVoucherNames")
    public String ajaxLoadVoucherNames() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting prepare...");
        final List<Object> voucherNameList = getPersistenceService().findAllBy(
                "select  distinct name from  CVoucherHeader where type=?", type);
        nameList = new ArrayList<Map<String, String>>();
        Map<String, String> voucherNamesMap;
        for (final Object voucherName : voucherNameList) {
            if (LOGGER.isInfoEnabled())
                LOGGER.info("..................................................................." + (String) voucherName);
            voucherNamesMap = new LinkedHashMap<String, String>();
            voucherNamesMap.put("key", (String) voucherName);
            voucherNamesMap.put("val", (String) voucherName);
            nameList.add(voucherNamesMap);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadVoucherNames.");
        return "voucherNames";

    }

    /*
     * Struts 7: CommonAction already has Integer bankaccount and Integer department,
     * so OGNL often fails to bind bankaccountId / departmentId from the AJAX query.
     * Cheque validation must read the same names from the request (and fall back to
     * bankaccount) before calling InstrumentService — otherwise every cheque number
     * looks unused/invalid.
     */
    private void bindChequeValidationParams() {
        if (StringUtils.isBlank(chequeNumber)) {
            chequeNumber = ServletActionContext.getRequest().getParameter("chequeNumber");
        }
        if (bankaccountId == null) {
            String bAccId = ServletActionContext.getRequest().getParameter("bankaccountId");
            if (StringUtils.isBlank(bAccId)) {
                bAccId = ServletActionContext.getRequest().getParameter("bankaccount.id");
            }
            if (StringUtils.isBlank(bAccId)) {
                bAccId = ServletActionContext.getRequest().getParameter("bankaccount");
            }
            if (StringUtils.isNotBlank(bAccId) && !"-1".equals(bAccId.trim())) {
                try {
                    bankaccountId = Long.valueOf(bAccId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid bank account id for cheque validation: " + bAccId);
                }
            }
        }
        if (bankaccountId == null && bankaccount != null && bankaccount > 0) {
            bankaccountId = bankaccount.longValue();
        }
        if (StringUtils.isBlank(departmentId)) {
            departmentId = ServletActionContext.getRequest().getParameter("departmentId");
            if (StringUtils.isBlank(departmentId)) {
                departmentId = ServletActionContext.getRequest().getParameter("department");
            }
        }
        if (StringUtils.isBlank(serialNo)) {
            serialNo = ServletActionContext.getRequest().getParameter("serialNo");
        }
        if (StringUtils.isBlank(serialNo)) {
            serialNo = null;
        }
    }

    private String chequeValidationIndex() {
        if (parameters != null && parameters.get("index") != null && parameters.get("index").length > 0
                && StringUtils.isNotBlank(parameters.get("index")[0])) {
            return parameters.get("index")[0];
        }
        final String index = ServletActionContext.getRequest().getParameter("index");
        return StringUtils.isNotBlank(index) ? index : "0";
    }

    @Action(value = "/voucher/common-ajaxValidateChequeNumber")
    public String ajaxValidateChequeNumber() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxValidateChequeNumber...");
        bindChequeValidationParams();
        final String index = chequeValidationIndex();
        boolean isValid = instrumentService.isChequeNumberValid(chequeNumber, bankaccountId, departmentId, serialNo);
        value = isValid ? index + "~true" : index + "~false";
        LOGGER.info("ajaxValidateChequeNumber: chequeNumber=" + chequeNumber + ", bankaccountId=" + bankaccountId + ", departmentId=" + departmentId + ", serialNo=" + serialNo + " -> result value=" + value);
        return "result";
    }

    public String ajaxValidateRtgsNumber() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxValidateRtgsNumber...");
        final String index = parameters.get("index")[0];
        value = instrumentService.isRtgsNumberValid(chequeNumber, bankaccountId) == true ? index + "~true" : index + "~false";
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxValidateRtgsNumber.");
        return "result";
    }

    @Action(value = "/voucher/common-ajaxValidateReassignSurrenderChequeNumber")
    public String ajaxValidateReassignSurrenderChequeNumber() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxValidateReassignSurrenderChequeNumber...");
        bindChequeValidationParams();
        final String index = chequeValidationIndex();
        value = instrumentService.isReassigningChequeNumberValid(chequeNumber, bankaccountId, departmentId, serialNo) == true
                ? index
                        + "~true"
                : index + "~false";
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxValidateReassignSurrenderChequeNumber.");
        return "result";
    }

    @Action(value = "/voucher/common-ajaxLoadUser")
    public String ajaxLoadUser() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadUser...");
        userList = new ArrayList<User>();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadUserByDesg | Start");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Functionar received : = " + functionaryName);
        String functionaryId = null;
        if (!"ANYFUNCTIONARY".equalsIgnoreCase(functionaryName)) {
            final Functionary functionary = (Functionary) persistenceService.find("from Functionary where name=?", functionaryName);
            functionaryId = functionary != null ? functionary.getId().toString() : null;
        }
        if (!departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0") && designationId != -1
                && null != functionaryName && functionaryName.trim().length() != 0) {
            final List<EmployeeView> empInfoList = voucherService.getUserByDeptAndDesgName(departmentId.toString(),
                    designationId.toString(), functionaryId);
            for (final EmployeeView employeeView : empInfoList)
                userList.add(employeeView.getEmployee());
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadUser.");
        return "users";
    }

    public String ajaxHodForDept(){
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxHodForDept...");
        userList = new ArrayList<User>();

        final List<PersonalInformation> listOfPI = null;// new EisUtilService().getAllHodEmpByDept(departmentId);
        for (final PersonalInformation personalInformation : listOfPI)
            userList.add(personalInformation.getUserMaster());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxHodForDept.");
        return "users";
    }

    /*
     * Struts 7: Integer accountDetailType often stays null on GET/AJAX
     * (sub-ledger search popup, autocomplete). Read it from the request the
     * same way as bankaccountId / departmentId.
     */
    private void bindAccountDetailType() {
        if (accountDetailType != null && accountDetailType != 0) {
            return;
        }
        String adt = ServletActionContext.getRequest().getParameter("accountDetailType");
        if (StringUtils.isBlank(adt)) {
            adt = ServletActionContext.getRequest().getParameter("accountDetailType.id");
        }
        if (StringUtils.isNotBlank(adt) && !"-1".equals(adt.trim()) && !"0".equals(adt.trim())) {
            try {
                accountDetailType = Integer.valueOf(adt.trim());
            } catch (final NumberFormatException e) {
                LOGGER.warn("Invalid accountDetailType: " + adt);
            }
        }
    }

    @Action(value = "/voucher/common-ajaxLoadCodesOfDetailType")
    public String ajaxLoadCodesOfDetailType() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadCodesOfDetailType...");
        bindAccountDetailType();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Detail type id  : " + accountDetailType);
        if (null == accountDetailType)
            accountCodesForDetailTypeList = egovCommon.getAllAccountCodesForAccountDetailType(-1);
        else
            accountCodesForDetailTypeList = egovCommon.getAllAccountCodesForAccountDetailType(accountDetailType);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme List size : " + accountCodesForDetailTypeList.size());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadCodesOfDetailType.");
        return "accountcodes";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadEntites")
    public String ajaxLoadEntites() throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadEntites...");
        bindAccountDetailType();
        if (accountDetailType == null)
            entitiesList = new ArrayList<EntityType>();
        else {
            final Accountdetailtype detailType = (Accountdetailtype) persistenceService.find(
                    "from Accountdetailtype where id=? order by name", accountDetailType);
            final String table = detailType.getFullQualifiedName();
            final Class<?> service = Class.forName(table);
            String simpleName = service.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

            final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
                    .getServletContext());
            final EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
            entitiesList = (List<EntityType>) entityService.getAllActiveEntities(accountDetailType);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadEntites.");
        return "entities";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadEntitesBy20")
    public String ajaxLoadEntitesBy20() throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadEntitesBy20...");
        bindAccountDetailType();
        if (accountDetailType == null || accountDetailType == 0)
            entitiesList = new ArrayList<EntityType>();
        else {
            final Accountdetailtype detailType = (Accountdetailtype) persistenceService.find(
                    "from Accountdetailtype where id=? order by name", accountDetailType);
            final String table = detailType.getFullQualifiedName();
            final Class<?> service = Class.forName(table);
            String simpleName = service.getSimpleName();
            simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

            final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
                    .getServletContext());
            final EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
            entitiesList = (List<EntityType>) entityService.filterActiveEntities(startsWith, 20, detailType.getId());
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadEntitesBy20.");
        return "entities";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadRTGSNumberBy20")
    public String ajaxLoadRTGSNumberBy20() throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadRTGSNumberBy20...");
        if (bankaccountId == null || bankaccountId == 0)
            numberList = new ArrayList<String>();
        else
            numberList = persistenceService
                    .findAllBy(
                            "SELECT ih.transactionNumber FROM InstrumentHeader ih where  ih.bankAccountId.id =? and ih.instrumentType.id=5 and upper(transactionNumber) like upper(?)",
                            bankaccountId, "%".concat(rtgsNumber).concat("%"));
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadRTGSNumberBy20.");
        return "rtgsNumbers";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadRTGSNumberByAccountId")
    public String ajaxLoadRTGSNumberByAccountId() throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadRTGSNumberBy20...");
        if (bankaccountId == null || bankaccountId == 0)
            numberList = new ArrayList<String>();
        else
            numberList = persistenceService
                    .findAllBy(
                            "SELECT ih.transactionNumber FROM InstrumentHeader ih where  ih.bankAccountId.id =?  and upper(transactionNumber) like upper(?)",
                            bankaccountId, rtgsNumber.concat("%"));
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadRTGSNumberBy20.");
        return "rtgsNumbers";
    }

    @Action(value = "/voucher/common-ajaxLoadCheckList")
    public String ajaxLoadCheckList() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadCheckList...");
        if (LOGGER.isInfoEnabled())
            LOGGER.info("..............................................................................ajaxLoadCheckList");
        final EgBillSubType egBillSubType = (EgBillSubType) persistenceService.find("from EgBillSubType where id=?",
                billSubtypeId.longValue());
        checkList = appConfigValuesService.getConfigValuesByModuleAndKey("EGF", egBillSubType.getName());
        if (checkList.size() == 0)
            checkList = appConfigValuesService
                    .getConfigValuesByModuleAndKey("EGF", FinancialConstants.CBILL_DEFAULTCHECKLISTNAME);

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadCheckList.");
        return "checkList";
    }

    @SuppressWarnings("unchecked")
    @Actions({
            @Action(value = "/voucher/common-searchEntites"),
            @Action(value = "/voucher/common-searchEntities")
    })
    public String searchEntites() throws ClassNotFoundException {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting searchEntites...");
        bindAccountDetailType();
        searchType = "EntitySearch";
        if (accountDetailType == null)
            entitiesList = new ArrayList<EntityType>();
        else {
            final Accountdetailtype detailType = (Accountdetailtype) persistenceService.find(
                    "from Accountdetailtype where id=? order by name", accountDetailType);
            LOGGER.info("searchEntites: accountDetailType=" + accountDetailType + ", detailType=" + (detailType == null ? "null" : detailType.getName()));
            if (detailType != null) {
                final String table = detailType.getFullQualifiedName();
                accountDetailTypeName = detailType.getName();
                try {
                    final Class<?> service = Class.forName(table);
                    String simpleName = service.getSimpleName();
                    simpleName = simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1) + "Service";

                    final WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(ServletActionContext
                            .getServletContext());
                    EntityTypeService entityService = (EntityTypeService) wac.getBean(simpleName);
                    entitiesList = (List<EntityType>) entityService.getAllActiveEntities(accountDetailType);
                    LOGGER.info("searchEntites: bean=" + simpleName + ", count=" + (entitiesList == null ? 0 : entitiesList.size()));
                } catch (final Exception e) {
                    LOGGER.error("searchEntites error: " + e.getMessage(), e);
                    entitiesList = new ArrayList<EntityType>();
                }
            } else {
                LOGGER.warn("searchEntites: detailType not found for id=" + accountDetailType);
                entitiesList = new ArrayList<EntityType>();
            }
        }
        return "searchResult";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-searchAccountCodes")
    public String searchAccountCodes() throws ClassNotFoundException {

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting accountCodes...");

        accountCodesList = persistenceService
                .findAllBy(
                        "select coa from CChartOfAccounts coa, CChartOfAccountDetail cod WHERE coa.id = cod.glCodeId AND  coa.classification = 4 order by coa.glcode asc");

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed accountCodes.");
        return "searchAccountCodes";
    }

    @Action(value = "/voucher/common-ajaxLoadBanksWithAssignedRTGS")
    public String ajaxLoadBanksWithAssignedRTGS() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithAssignedCheques...");
        try {
            final StringBuilder vouchersWithNewInstrumentsQuery = new StringBuilder("select voucherheaderid from egf_instrumentvoucher eiv,egf_instrumentheader ih,")
            		.append(" egw_status egws where eiv.instrumentheaderid=ih.id and egws.id=ih.id_status and egws.moduletype='Instrument' and egws.description='New' and ")
            		.append(" ih.transactionNumber is not null");
            StringBuffer queryString = new StringBuffer();
            queryString = queryString
                    .append("select DISTINCT concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),")
                    .append("bankBranch.branchname) as bankbranchname from  voucherheader vh,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount, ")
                    .append(" paymentheader ph where  ")
                    .append(" ph.voucherheaderid=vh.id and vh.id  in (")
                    .append(vouchersWithNewInstrumentsQuery)
                    .append( ") and bank.isactive=true  and bankBranch.isactive=true ")
                    .append(" and  bank.id = bankBranch.bankid and bankBranch.id = bankaccount.BRANCHID and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and vh.voucherdate <= :date")
                    .append(" and ph.bankaccountnumberid=bankaccount.id  and bankaccount.isactive=true order by 2");
            final List<Object[]> bankBranch = persistenceService.getSession().createNativeQuery(queryString.toString())
                    .setParameter("date", getAsOnDate())
                    .list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            Map<String, Object> bankBrmap;
            for (final Object[] element : bankBranch) {
                bankBrmap = new HashMap<String, Object>();
                bankBrmap.put("bankBranchId", element[0].toString());
                bankBrmap.put("bankBranchName", element[1].toString());
                bankBranchList.add(bankBrmap);
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithAssignedCheques.");
        return "bank";
    }

    /**
     * This method will load the bank and branch for which there are cheqeues assigned and the cheque status is "NEW"
     */
    @Action(value = "/voucher/common-ajaxLoadBanksWithAssignedCheques")
    public String ajaxLoadBanksWithAssignedCheques() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithAssignedCheques...");
        try {
            final StringBuilder vouchersWithNewInstrumentsQuery = new StringBuilder("select voucherheaderid from egf_instrumentvoucher eiv,egf_instrumentheader ih,")
            		.append(" egw_status egws where eiv.instrumentheaderid=ih.id and egws.id=ih.id_status and egws.moduletype='Instrument' and egws.description='New'  ");
            StringBuffer queryString = new StringBuffer();
            queryString = queryString
                    .append("select DISTINCT concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),")
                    .append("bankBranch.branchname) as bankbranchname from  voucherheader vh,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount, ")
                    .append(" paymentheader ph where  ")
                    .append(" ph.voucherheaderid=vh.id and vh.id  in (")
                    .append(vouchersWithNewInstrumentsQuery)
                    .append( ") and bank.isactive=true  and bankBranch.isactive=true ")
                    .append(" and  bank.id = bankBranch.bankid and bankBranch.id = bankaccount.BRANCHID and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and vh.voucherdate <= :date")
                    .append(" and ph.bankaccountnumberid=bankaccount.id  and bankaccount.isactive=true order by 2");
            final List<Object[]> bankBranch = persistenceService.getSession().createNativeQuery(queryString.toString())
                    .setParameter("date", getAsOnDate())
                    .list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            Map<String, Object> bankBrmap;
            for (final Object[] element : bankBranch) {
                bankBrmap = new HashMap<String, Object>();
                bankBrmap.put("bankBranchId", element[0].toString());
                bankBrmap.put("bankBranchName", element[1].toString());
                bankBranchList.add(bankBrmap);
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithAssignedCheques.");
        return "bank";
    }

    /**
     * This method is to get the list of bank accounts for a particular bank branch for which there are cheques assigned in "NEW"
     * status.
     * @return
     */
    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksAccountsWithAssignedRTGS")
    public String ajaxLoadBanksAccountsWithAssignedRTGS() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksAccountsWithAssignedCheques...");
        try {
            accNumList = new ArrayList<Bankaccount>();
            StringBuffer queryString = new StringBuffer();
            queryString = queryString
                    .append("select bankaccount.accountnumber as accountnumber,bankaccount.accounttype as accounttype,cast(bankaccount.id as integer) as id,coa.glcode as glCode ")
                    .append(" from  voucherheader vh,chartofaccounts coa,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,paymentheader ph,  ")
                    .append("egf_instrumentvoucher eiv,egf_instrumentheader ih,egw_status egws ")
                    .append("where ph.voucherheaderid=vh.id and coa.id=bankaccount.glcodeid and vh.id=eiv.VOUCHERHEADERID and ")
                    .append("  eiv.instrumentheaderid=ih.id and egws.id=ih.id_status and egws.moduletype='Instrument' and egws.description='New' and ih.transactionNumber is not null")
                    .append("and ih.instrumenttype=(select id from egf_instrumenttype where upper(type)='CHEQUE') and ispaycheque='1' ")
                    .append(" and bank.isactive=true  and bankBranch.isactive=true and bankaccount.isactive=true ")
                    .append(" and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.branchid=:branchId ")
                    .append("  and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and vh.voucherdate <= :date");

            queryString = queryString.append(" and ph.bankaccountnumberid=bankaccount.id  order by vh.voucherdate desc");
            final List<Object[]> bankAccounts = persistenceService.getSession().createNativeQuery(queryString.toString())
                    .setParameter("date", getAsOnDate())
                    .setParameter("branchId", branchId)
                    .list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankAccounts.size());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    bankaccount.setAccounttype(account[1].toString());
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    chartofaccounts.setGlcode(account[3].toString());
                    bankaccount.setChartofaccounts(chartofaccounts);
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksAccountsWithAssignedCheques.");
        return "bankAccNum";
    }

    /**
     * This method is to get the list of bank accounts for a particular bank branch for which there are cheques assigned in "NEW"
     * status.
     * @return
     */
    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBanksAccountsWithAssignedCheques")
    public String ajaxLoadBanksAccountsWithAssignedCheques() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksAccountsWithAssignedCheques...");
        accNumList = new ArrayList<Bankaccount>();
        resolveAssignedChequeAccountParams();
        if (branchId == null) {
            LOGGER.warn("ajaxLoadBanksAccountsWithAssignedCheques: branchId is missing");
            return "bankAccNum";
        }
        try {
            StringBuffer queryString = new StringBuffer();
            queryString = queryString
                    .append("select bankaccount.accountnumber as accountnumber,bankaccount.accounttype as accounttype,cast(bankaccount.id as integer) as id,coa.glcode as glCode ,bank.name as bankName")
                    .append(" from  voucherheader vh,chartofaccounts coa,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,paymentheader ph,  ")
                    .append("egf_instrumentvoucher eiv,egf_instrumentheader ih,egw_status egws ")
                    .append("where ph.voucherheaderid=vh.id and coa.id=bankaccount.glcodeid and vh.id=eiv.VOUCHERHEADERID and ")
                    .append("  eiv.instrumentheaderid=ih.id and egws.id=ih.id_status and egws.moduletype='Instrument' and egws.description='New' ")
                    .append("and ih.instrumenttype=(select id from egf_instrumenttype where upper(type)=:type) and ispaycheque='1' ")
                    .append(" and bank.isactive=true  and bankBranch.isactive=true and bankaccount.isactive=true ")
                    .append(" and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.branchid=:branchId ")
                    .append("  and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and vh.voucherdate <= :date");

            queryString = queryString.append(" and ph.bankaccountnumberid=bankaccount.id  order by vh.voucherdate desc");
            final List<Object[]> bankAccounts = persistenceService.getSession().createNativeQuery(queryString.toString())
                    .setParameter("date", asOnDate)
                    .setParameter("type", type)
                    .setParameter("branchId", branchId)
                    .list();
            LOGGER.info("Assigned-cheque bank accounts size=" + (bankAccounts != null ? bankAccounts.size() : 0)
                    + " branchId=" + branchId);
            final List<String> addedBanks = new ArrayList<String>();
            if (bankAccounts != null) {
                for (final Object[] account : bankAccounts) {
                    final String accountNumberAndType = account[0] != null ? account[0].toString()
                            : "" + "-" + account[4] != null ? account[4].toString() : "";
                    if (!addedBanks.contains(accountNumberAndType)) {
                        final Bankaccount bankaccount = new Bankaccount();
                        bankaccount.setAccountnumber(account[0] != null ? account[0].toString() : "");
                        bankaccount.setId(Long.valueOf(account[2] != null ? account[2].toString() : ""));
                        final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                        chartofaccounts.setGlcode(account[3] != null ? account[3].toString() : "");
                        final Bankbranch branch = new Bankbranch();
                        final Bank bank = new Bank();
                        bank.setName(account[4] != null ? account[4].toString() : "");
                        branch.setBank(bank);
                        bankaccount.setBankbranch(branch);
                        bankaccount.setChartofaccounts(chartofaccounts);
                        addedBanks.add(accountNumberAndType);
                        accNumList.add(bankaccount);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception occurred while loading assigned-cheque bank accounts", e);
        }
        if (accNumList.isEmpty())
            loadActivePaymentAccountsForBranch();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksAccountsWithAssignedCheques.");
        return "bankAccNum";
    }

    /**
     * Struts 7 may not bind AJAX query params (branchId, asOnDate=dd/MM/yyyy).
     */
    private void resolveAssignedChequeAccountParams() {
        if (branchId == null) {
            final String bId = ServletActionContext.getRequest().getParameter("branchId");
            if (bId != null && !bId.trim().isEmpty() && !"-1".equals(bId.trim())) {
                try {
                    branchId = Integer.valueOf(bId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid branchId for assigned-cheque accounts: " + bId);
                }
            }
        }
        if (bankId == null) {
            final String bkId = ServletActionContext.getRequest().getParameter("bankId");
            if (bkId != null && !bkId.trim().isEmpty() && !"-1".equals(bkId.trim())) {
                try {
                    bankId = Integer.valueOf(bkId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid bankId for assigned-cheque accounts: " + bkId);
                }
            }
        }
        if (type == null || type.trim().isEmpty()) {
            type = ServletActionContext.getRequest().getParameter("type");
        }
        if (type == null || type.trim().isEmpty())
            type = "CHEQUE";
        if (asOnDate == null) {
            final String asOn = ServletActionContext.getRequest().getParameter("asOnDate");
            if (asOn != null && !asOn.trim().isEmpty()) {
                try {
                    asOnDate = Constants.DDMMYYYYFORMAT2.parse(asOn.trim());
                } catch (final Exception e) {
                    asOnDate = new Date();
                }
            } else {
                asOnDate = new Date();
            }
        }
    }

    /**
     * Same fallback as surrender bank list: show active payment accounts for the
     * branch when the assigned-cheque native query is empty or fails.
     */
    @SuppressWarnings("unchecked")
    private void loadActivePaymentAccountsForBranch() {
        try {
            LOGGER.warn("No assigned-cheque accounts for branchId=" + branchId
                    + "; falling back to active payment accounts");
            if (bankId != null && bankId > 0) {
                accNumList = getPersistenceService().findAllBy(
                        "from Bankaccount ba where ba.bankbranch.id=? and ba.bankbranch.bank.id=? and ba.isactive=true "
                                + "and ba.type in (org.egov.commons.utils.BankAccountType.PAYMENTS, "
                                + "org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS) "
                                + "order by ba.chartofaccounts.glcode",
                        branchId, bankId);
            } else {
                accNumList = getPersistenceService().findAllBy(
                        "from Bankaccount ba where ba.bankbranch.id=? and ba.isactive=true "
                                + "and ba.type in (org.egov.commons.utils.BankAccountType.PAYMENTS, "
                                + "org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS) "
                                + "order by ba.chartofaccounts.glcode",
                        branchId);
            }
            if (accNumList == null)
                accNumList = new ArrayList<Bankaccount>();
            LOGGER.info("Fallback payment bank accounts size=" + accNumList.size());
        } catch (final Exception e) {
            LOGGER.error("Fallback load of payment bank accounts failed", e);
            accNumList = new ArrayList<Bankaccount>();
        }
    }

    public Integer getBranchId() {
        return branchId;
    }

    public void setBranchId(final Integer branchId) {
        this.branchId = branchId;
    }

    public List<Bankaccount> getAccNumList() {
        return accNumList;
    }

    public void setAccNumList(final List<Bankaccount> accNumList) {
        this.accNumList = accNumList;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public Integer getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(final Integer schemeId) {
        this.schemeId = schemeId;
    }

    public List<Scheme> getSchemeList() {
        return schemeList;
    }

    public void setSchemeList(final List<Scheme> schemeList) {
        this.schemeList = schemeList;
    }

    public List<SubScheme> getSubSchemes() {
        return subSchemes;
    }

    public void setSubSchemes(final List<SubScheme> subSchemes) {
        this.subSchemes = subSchemes;
    }

    public List<Map<String, Object>> getBankBranchList() {
        return bankBranchList;
    }

    public void setBankBranchList(final List<Map<String, Object>> bankBranchList) {
        this.bankBranchList = bankBranchList;
    }

    public Integer getBankId() {
        return bankId;
    }

    public void setBankId(final Integer bankId) {
        this.bankId = bankId;
    }

    public List<Bankbranch> getBranchList() {
        return branchList;
    }

    public void setBranchList(final List<Bankbranch> branchList) {
        this.branchList = branchList;
    }

    public Long getBankaccountId() {
        return bankaccountId;
    }

    public void setBankaccountId(final Long bankaccountId) {
        this.bankaccountId = bankaccountId;
    }

    public String getChequeNumber() {
        return chequeNumber;
    }

    public void setChequeNumber(final String chequeNumber) {
        this.chequeNumber = chequeNumber;
    }

    public void setInstrumentService(final InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(final String departmentId) {
        this.departmentId = departmentId;
    }

    public void setDetailCodes(final List<String> detailCodes) {
        this.detailCodes = detailCodes;
    }

    public List<String> getDetailCodes() {
        return detailCodes;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(final List<User> userList) {
        this.userList = userList;
    }

    public void setDesignationId(final Integer designationId) {
        this.designationId = designationId;
    }

    public void setFunctionaryName(final String functionaryName) {
        this.functionaryName = functionaryName;
    }

    public EgovCommon getEgovCommon() {
        return egovCommon;
    }

    public void setEgovCommon(final EgovCommon egovCommon) {
        this.egovCommon = egovCommon;
    }

    public List<CChartOfAccounts> getAccountCodesForDetailTypeList() {
        return accountCodesForDetailTypeList;
    }

    public void setAccountCodesForDetailTypeList(final List<CChartOfAccounts> accountCodesForDetailTypeList) {
        this.accountCodesForDetailTypeList = accountCodesForDetailTypeList;
    }

    public List<DrawingOfficer> getDrawingList() {
        return drawingList;
    }

    public void setDrawingList(final List<DrawingOfficer> drawingList) {
        this.drawingList = drawingList;
    }

    public List<EntityType> getEntitiesList() {
        return entitiesList;
    }

    public void setEntitiesList(final List<EntityType> entitiesList) {
        this.entitiesList = entitiesList;
    }

    public Integer getAccountDetailType() {
        return accountDetailType;
    }

    public void setAccountDetailType(final Integer accountDetailType) {
        this.accountDetailType = accountDetailType;
    }

    public Integer getBillSubtypeId() {
        return billSubtypeId;
    }

    public void setBillSubtypeId(final Integer billSubtypeId) {
        this.billSubtypeId = billSubtypeId;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(final String billType) {
        this.billType = billType;
    }

    public List<AppConfigValues> getCheckList() {
        return checkList;
    }

    public void setCheckList(final List<AppConfigValues> checkList) {
        this.checkList = checkList;
    }

    public VoucherService getVoucherService() {
        return voucherService;
    }

    public void setVoucherService(final VoucherService voucherService) {
        this.voucherService = voucherService;
    }

    public List getNameList() {
        return nameList;
    }

    public void setNameList(final List nameList) {
        this.nameList = (ArrayList<Map<String, String>>) nameList;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getSearchType() {
        return searchType;
    }

    public void setSearchType(final String searchType) {
        this.searchType = searchType;
    }

    public String getAccountDetailTypeName() {
        return accountDetailTypeName;
    }

    public void setAccountDetailTypeName(final String accountDetailTypeName) {
        this.accountDetailTypeName = accountDetailTypeName;
    }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(final String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBanksWithApprovedPayments")
    public String ajaxLoadBanksWithApprovedPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithApprovedPayments...");
        bankBranchList = new ArrayList<Map<String, Object>>();
        resolveFundIdAndAsOnDate();
        try {
            StringBuffer queryString = new StringBuffer();
            // query to fetch vouchers for which no cheque has been assigned
            queryString = queryString
                    .append("select distinct concat(cast(bank.id as varchar), '-', cast(bankBranch.id as varchar)) as bankbranchid, concat(bank.name, ' ',")
                    .append(" bankBranch.branchname) as bankbranchname " )
                    .append(" from Bank bank,  Bankbranch bankBranch,   Bankaccount bankaccount where bankaccount.id in ( " )
                    .append(" select DISTINCT ph.bankaccountnumberid from ")
                    .append(" paymentheader ph,egf_instrumentvoucher iv right outer join voucherheader vh on ")
                    .append(" vh.id =iv.VOUCHERHEADERID where ph.voucherheaderid=vh.id  and ")
                    .append(" vh.status=0  and  ph.voucherheaderid=vh.id and iv.VOUCHERHEADERID is null ");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and vh.fundid=:fundId ");

            queryString.append(" and vh.name NOT IN ( '").append(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE)
            .append("','").append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("')")
            .append(" and vh.voucherdate <= :date1 )")
            .append(" AND bank.id = bankBranch.bankid AND bank.isactive = true AND bankBranch.isactive = true")
            .append(" AND bankaccount.type IN ('RECEIPTS_PAYMENTS','PAYMENTS') AND bankBranch.id = bankaccount.branchid");
		    if (fundId != null && fundId != 0 && fundId != -1)
		        queryString.append(" and bankaccount.fundid = :fundId");
    
            // query to fetch vouchers for which cheque has been assigned and surrendered
            queryString = queryString
                    .append(" union select distinct concat(cast(bank.id as varchar), '-', cast(bankBranch.id as varchar)) as bankbranchid, concat(bank.name, ' ', bankBranch.branchname) as bankbranchname ")
                    .append(" from Bank bank,  Bankbranch bankBranch,   Bankaccount bankaccount where bankaccount.id in ( ")
                    .append(" select DISTINCT ph.bankaccountnumberid from egf_instrumentvoucher iv,voucherheader vh, paymentheader ph,egw_status egws,(select ih1.id,ih1.id_status from egf_instrumentheader ih1, ")
                    .append("(select bankid,bankaccountid,instrumentnumber,max(id) as id from egf_instrumentheader group by bankid,bankaccountid,")
                    .append("instrumentnumber) max_rec where max_rec.bankid=ih1.bankid and max_rec.bankaccountid=ih1.bankaccountid and max_rec.instrumentnumber=ih1.instrumentnumber ")
                    .append(" and max_rec.id=ih1.id) ih where ph.voucherheaderid=vh.id and vh.status=0  and ph.voucherheaderid=vh.id ")
                    .append(" and iv.voucherheaderid=vh.id and iv.instrumentheaderid=ih.id and ih.id_status=egws.id and egws.description in  ('Surrendered','Surrender_For_Reassign')");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and vh.fundid=:fundId ");

            queryString = queryString.append("  and vh.voucherdate <= :date2 and vh.name NOT IN ( '").append(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE).append("','")
            		.append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("' ) ) ");
            queryString = queryString
                    .append(" AND bank.id = bankBranch.bankid AND bank.isactive=true AND bankBranch.isactive=true ")
                    .append("AND bankaccount.type IN ('RECEIPTS_PAYMENTS','PAYMENTS') AND bankBranch.id = bankaccount.branchid");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=" + fundId.longValue());

            LOGGER.info("ajaxLoadBanksWithApprovedPayments fundId=" + fundId + " asOnDate=" + asOnDate);
            Query query =  persistenceService.getSession().createNativeQuery(queryString.toString());
            query.setParameter("date1", asOnDate, StandardBasicTypes.DATE)
                .setParameter("date2", asOnDate, StandardBasicTypes.DATE);
            if (fundId != null && fundId != 0 && fundId != -1)
                query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
            final List<Object[]> bankBranch = query.list();
            LOGGER.info("Approved-payment bank list size=" + (bankBranch != null ? bankBranch.size() : 0));
            final List<String> addedBanks = new ArrayList<String>();
            if (bankBranch != null) {
                for (final Object[] account : bankBranch) {
                    if (account == null || account.length < 2 || account[0] == null || account[1] == null)
                        continue;
                    final String bankBranchName = account[1].toString();
                    if (!addedBanks.contains(bankBranchName)) {
                        addedBanks.add(bankBranchName);
                        final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                        bankBrmap.put("bankBranchId", account[0].toString());
                        bankBrmap.put("bankBranchName", bankBranchName);
                        bankBranchList.add(bankBrmap);
                    }
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception occurred while loading approved-payment banks", e);
        }
        if (bankBranchList.isEmpty())
            loadActivePaymentBanksForFund();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithApprovedPayments.");
        return "bank";
    }

    /**
     * Struts 7 does not bind {@code asOnDate=dd/MM/yyyy} onto a Date field, so
     * {@code vh.voucherdate <= :date} was null and the bank AJAX returned empty.
     */
    private void resolveFundIdAndAsOnDate() {
        if (fundId == null) {
            final String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (fId != null && !fId.trim().isEmpty() && !"-1".equals(fId.trim())) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (final NumberFormatException e) {
                    LOGGER.warn("Invalid fundId for approved-payment banks: " + fId);
                }
            }
        }
        if (asOnDate == null) {
            final String asOn = ServletActionContext.getRequest().getParameter("asOnDate");
            if (asOn != null && !asOn.trim().isEmpty()) {
                try {
                    asOnDate = Constants.DDMMYYYYFORMAT2.parse(asOn.trim());
                } catch (final Exception e) {
                    asOnDate = new Date();
                }
            } else {
                asOnDate = new Date();
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void loadActivePaymentBanksForFund() {
        try {
            LOGGER.warn("No approved-payment banks for fundId=" + fundId
                    + "; falling back to active payment bank/branches");
            final StringBuilder query = new StringBuilder();
            query.append("select DISTINCT concat(cast(bank.id as string), '-', cast(bankBranch.id as string)), ")
                    .append("concat(bank.name, ' ', bankBranch.branchname) ")
                    .append("FROM Bank bank, Bankbranch bankBranch, Bankaccount bankaccount ")
                    .append("where bank.isactive=true and bankBranch.isactive=true and bankaccount.isactive=true ")
                    .append("and bank.id = bankBranch.bank.id and bankBranch.id = bankaccount.bankbranch.id ")
                    .append("and bankaccount.type in (org.egov.commons.utils.BankAccountType.PAYMENTS, ")
                    .append("org.egov.commons.utils.BankAccountType.RECEIPTS_PAYMENTS) ");
            final List<Object[]> rows;
            if (fundId != null && fundId != 0 && fundId != -1) {
                query.append("and bankaccount.fund.id=? order by 2");
                rows = getPersistenceService().findAllBy(query.toString(), fundId);
            } else {
                query.append("order by 2");
                rows = getPersistenceService().findAllBy(query.toString());
            }
            if (rows == null)
                return;
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] element : rows) {
                if (element == null || element.length < 2 || element[0] == null || element[1] == null)
                    continue;
                final String name = element[1].toString();
                if (addedBanks.contains(name))
                    continue;
                addedBanks.add(name);
                final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                bankBrmap.put("bankBranchId", element[0].toString());
                bankBrmap.put("bankBranchName", name);
                bankBranchList.add(bankBrmap);
            }
            LOGGER.info("Fallback payment bank list size=" + bankBranchList.size());
        } catch (final Exception e) {
            LOGGER.error("Fallback load of payment banks failed", e);
        }
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksWithRtgsPayments")
    public String ajaxLoadBanksWithRtgsPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithApprovedPayments...");
        try {
            StringBuffer queryString = new StringBuffer();
            // query to fetch vouchers for which no cheque has been assigned
            queryString = queryString
                    .append("select distinct concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),")
                    .append("bankBranch.branchname) as bankbranchname ")
                    .append("from voucherheader vh,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,vouchermis vmis, eg_department d,")
                    .append("generalledger gl,paymentheader ph,egf_instrumentvoucher iv right outer join voucherheader vh1 on ")
                    .append("vh1.id =iv.VOUCHERHEADERID,egw_status egws where ph.voucherheaderid=vh.id and vh.id= vmis.voucherheaderid and ")
                    .append("vmis.departmentid= d.id and vh.status=0 and gl.voucherheaderid=vh.id and ")
                    .append("ph.voucherheaderid=vh.id and bank.isactive=true  and bankBranch.isactive=true and bank.id = bankBranch.bankid and ")
                    .append("bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS')")
                    .append(" and  vh1.id=vh.id and iv.VOUCHERHEADERID is null ");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=:fundId ");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString = queryString.append(" and vmis.departmentcode=:departmentId ");
            queryString = queryString
                    .append(" and gl.debitamount!=0 and gl.debitamount is not null and vh.voucherdate <= :date1 ") 
                    .append(" and ph.bankaccountnumberid=bankaccount.id and vh.type='")
                    .append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append( "' and  vh.name NOT IN ( '")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE).append("','")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("' ) ");
            // query to fetch vouchers for which cheque has been assigned and surrendered
            queryString = queryString
                    .append(" union select distinct concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),")
                    .append("bankBranch.branchname) as bankbranchname from egf_instrumentvoucher iv,voucherheader vh,")
                    .append("Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,vouchermis vmis, eg_department d,generalledger gl,")
                    .append("paymentheader ph,egw_status egws,(select ih1.id,ih1.id_status from egf_instrumentheader ih1, ")
                    .append("(select bankid,bankaccountid,instrumentnumber,max(lastmodifieddate) as lastmodifieddate from egf_instrumentheader group by bankid,bankaccountid,")
                    .append("instrumentnumber) max_rec where max_rec.bankid=ih1.bankid and max_rec.bankaccountid=ih1.bankaccountid and max_rec.instrumentnumber=ih1.instrumentnumber ")
                    .append("and max_rec.lastmodifieddate=ih1.lastmodifieddate) ih where ph.voucherheaderid=vh.id and vh.id= vmis.voucherheaderid and ")
                    .append("vmis.departmentid= d.id and vh.status=0 and gl.voucherheaderid=vh.id and ph.voucherheaderid=vh.id ")
                    .append("and bank.isactive=true  and bankBranch.isactive=true and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and ")
                    .append("bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and  iv.voucherheaderid=vh.id and iv.instrumentheaderid=ih.id and ")
                    .append("ih.id_status=egws.id and egws.description in  ('Surrendered','Surrender_For_Reassign')");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=:fundId ");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString = queryString.append(" and vmis.departmentcode=:departmentId ");
            queryString = queryString
                    .append(" and gl.debitamount!=0 and gl.debitamount is not null and vh.voucherdate <= :date2 ")
                    .append(" and ph.bankaccountnumberid=bankaccount.id and vh.type='")
                    .append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("' and vh.name NOT IN ( '")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE).append("','")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("' ) order by 2 ");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank check dates are  " + getAsOnDate());
            Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
            query.setParameter("date1", getAsOnDate(), StandardBasicTypes.DATE)
                    .setParameter("date2", getAsOnDate(), StandardBasicTypes.DATE);
            if (fundId != null && fundId != 0 && fundId != -1)
                query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                query.setParameter("departmentId", departmentId, StandardBasicTypes.LONG);
            final List<Object[]> bankBranch = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankBranch) {
                final String bankBranchName = account[1].toString();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                    bankBrmap.put("bankBranchId", account[0].toString());
                    bankBrmap.put("bankBranchName", bankBranchName);
                    bankBranchList.add(bankBrmap);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithApprovedPayments.");
        return "bank";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksWithApprovedSalaryPayments")
    public String ajaxLoadBanksWithApprovedSalaryPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithApprovedSalaryPayments...");
        try {
            StringBuffer queryString = new StringBuffer();
            // query to fetch vouchers for which no cheque has been assigned
            queryString = queryString
                    .append("select distinct concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '), bankBranch.branchname) as bankbranchname ")
                    .append("from voucherheader vh,Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,vouchermis vmis, eg_department d,")
                    .append("generalledger gl,paymentheader ph,egf_instrumentvoucher iv right outer join voucherheader vh1 on ")
                    .append("vh1.id =iv.VOUCHERHEADERID,egw_status egws where ph.voucherheaderid=vh.id and vh.id= vmis.voucherheaderid and ")
                    .append("vmis.departmentid= d.id and vh.status=0 and gl.voucherheaderid=vh.id and ")
                    .append("ph.voucherheaderid=vh.id and bank.isactive=true  and bankBranch.isactive=true and bank.id = bankBranch.bankid and ")
                    .append("bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS')")
                    .append(" and  vh1.id=vh.id and iv.VOUCHERHEADERID is null ");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=:fundId");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString = queryString.append(" and vmis.departmentcode=:departmentId");
            queryString = queryString
                    .append(" and gl.debitamount!=0 and gl.debitamount is not null and vh.voucherdate <= :date1 ")
                    .append(" and ph.bankaccountnumberid=bankaccount.id and vh.type='")
                    .append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("' and vh.name='")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("'  ");
            // query to fetch vouchers for which cheque has been assigned and surrendered
            queryString = queryString
                    .append(" union select distinct concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '),")
                    .append("bankBranch.branchname) as bankbranchname from egf_instrumentvoucher iv,voucherheader vh,")
                    .append("Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,vouchermis vmis, eg_department d,generalledger gl,")
                    .append("paymentheader ph,egw_status egws,(select ih1.id,ih1.id_status from egf_instrumentheader ih1, ")
                    .append("(select bankid,bankaccountid,instrumentnumber,max(lastmodifieddate) as lastmodifieddate from egf_instrumentheader group by bankid,bankaccountid,")
                    .append("instrumentnumber) max_rec where max_rec.bankid=ih1.bankid and max_rec.bankaccountid=ih1.bankaccountid and max_rec.instrumentnumber=ih1.instrumentnumber ")
                    .append("and max_rec.lastmodifieddate=ih1.lastmodifieddate) ih where ph.voucherheaderid=vh.id and vh.id= vmis.voucherheaderid and ")
                    .append("vmis.departmentid= d.id and vh.status=0 and gl.voucherheaderid=vh.id and ph.voucherheaderid=vh.id ")
                    .append("and bank.isactive=true  and bankBranch.isactive=true and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and ")
                    .append("bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and  iv.voucherheaderid=vh.id and iv.instrumentheaderid=ih.id and ")
                    .append("ih.id_status=egws.id and egws.description in  ('Surrendered','Surrender_For_Reassign')");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=:fundId");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString = queryString.append(" and vmis.departmentcode=:departmentId");
            queryString = queryString
                    .append(" and gl.debitamount!=0 and gl.debitamount is not null and vh.voucherdate <= :date2 ")
                    .append(" and ph.bankaccountnumberid=bankaccount.id and vh.type='")
                    .append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("' and vh.name='")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("' order by 2  ");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank check dates are  " + getAsOnDate());
            Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
            query.setParameter("date1", getAsOnDate(), StandardBasicTypes.DATE);
            query.setParameter("date2", getAsOnDate(), StandardBasicTypes.DATE);
            if (fundId != null && fundId != 0 && fundId != -1)
                query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                query.setParameter("departmentId", departmentId, StandardBasicTypes.LONG);
            final List<Object[]> bankBranch = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankBranch) {
                final String bankBranchName = account[1].toString();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                    bankBrmap.put("bankBranchId", account[0].toString());
                    bankBrmap.put("bankBranchName", bankBranchName);
                    bankBranchList.add(bankBrmap);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithApprovedSalaryPayments.");
        return "bank";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksWithApprovedPensionPayments")
    public String ajaxLoadBanksWithApprovedPensionPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithApprovedSalaryPayments...");
        try {
            // query to fetch vouchers for which no cheque has been assigned
            StringBuilder queryString = new StringBuilder("select distinct concat(concat(bank.id,'-'), bankBranch.id) as bankbranchid, concat(concat(bank.name,' '), bankBranch.branchname) as bankbranchname")
                    .append(" from voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis, eg_department d, generalledger gl, paymentheader ph,")
                    .append(" egf_instrumentvoucher iv right outer join voucherheader vh1 on vh1.id = iv.VOUCHERHEADERID, egw_status egws")
                    .append(" where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid and vmis.departmentid = d.id and vh.status = 0 and gl.voucherheaderid = vh.id and")
                    .append(" ph.voucherheaderid = vh.id and bank.isactive = true and bankBranch.isactive = true and bank.id = bankBranch.bankid and")
                    .append(" bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and vh1.id = vh.id and iv.VOUCHERHEADERID is null ");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString.append(" and bankaccount.fundid = :fundId ");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString.append(" and vmis.departmentcode = :departmentId ");
            queryString.append(" and gl.debitamount != 0 and gl.debitamount is not null and vh.voucherdate <= :date1 and ph.bankaccountnumberid = bankaccount.id and vh.type = '")
                    .append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("' and vh.name = '")
                    .append(FinancialConstants.PAYMENTVOUCHER_NAME_PENSION).append("'  ");
            
            // query to fetch vouchers for which cheque has been assigned and surrendered
			queryString.append(
					" union select distinct concat(concat(bank.id,'-'), bankBranch.id) as bankbranchid, concat(concat(bank.name,' '), bankBranch.branchname) as bankbranchname")
					.append(" from egf_instrumentvoucher iv,voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis, eg_department d,generalledger gl,")
					.append(" paymentheader ph, egw_status egws, (select ih1.id, ih1.id_status from egf_instrumentheader ih1, (select bankid, bankaccountid, instrumentnumber,")
					.append(" max(lastmodifieddate) as lastmodifieddate from egf_instrumentheader group by bankid, bankaccountid, instrumentnumber) max_rec")
					.append(" where max_rec.bankid = ih1.bankid and max_rec.bankaccountid = ih1.bankaccountid and max_rec.instrumentnumber = ih1.instrumentnumber")
					.append(" and max_rec.lastmodifieddate = ih1.lastmodifieddate) ih where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid and")
					.append(" vmis.departmentid = d.id and vh.status = 0 and gl.voucherheaderid = vh.id and ph.voucherheaderid = vh.id")
					.append(" and bank.isactive = true  and bankBranch.isactive = true and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and")
					.append(" bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and iv.voucherheaderid = vh.id and iv.instrumentheaderid = ih.id and")
					.append(" ih.id_status = egws.id and egws.description in ('Surrendered','Surrender_For_Reassign')");
			if (fundId != null && fundId != 0 && fundId != -1)
				queryString.append(" and bankaccount.fundid = :fundId ");
			if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
				queryString.append(" and vmis.departmentcode = :departmentId ");
			queryString.append(" and gl.debitamount != 0 and gl.debitamount is not null and vh.voucherdate <= :date2")
					.append(" and ph.bankaccountnumberid = bankaccount.id and vh.type = '")
					.append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("' and vh.name = '")
					.append(FinancialConstants.PAYMENTVOUCHER_NAME_PENSION).append("' order by 2  ");
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Bank check dates are  " + getAsOnDate());
			Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
			query.setParameter("date1", getAsOnDate(), StandardBasicTypes.DATE).setParameter("date2", getAsOnDate(),
					StandardBasicTypes.DATE);
			if (fundId != null && fundId != 0 && fundId != -1)
				query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
			if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
				query.setParameter("departmentId", departmentId, StandardBasicTypes.LONG);
			final List<Object[]> bankBranch = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankBranch) {
                final String bankBranchName = account[1].toString();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                    bankBrmap.put("bankBranchId", account[0].toString());
                    bankBrmap.put("bankBranchName", bankBranchName);
                    bankBranchList.add(bankBrmap);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithApprovedPensionPayments.");
        return "bank";
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBanksWithApprovedRemittances")
    public String ajaxLoadBanksWithApprovedRemittances() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithApprovedRemittances...");
        resolveFundIdAndAsOnDate();
        try {
            StringBuilder queryString = new StringBuilder();
            queryString = queryString
                    .append("select distinct concat(concat(bank.id,'-'),bankBranch.id) as bankbranchid,concat(concat(bank.name,' '), bankBranch.branchname) as bankbranchname  ")
                    .append("from Bank bank,Bankbranch bankBranch,Bankaccount bankaccount ")
                    .append("where  bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') ");
            if (fundId != null && fundId != 0 && fundId != -1)
                queryString = queryString.append(" and bankaccount.fundid=:fundId ");

            Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
			if (fundId != null && fundId != 0 && fundId != -1)
				query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
			
            final List<Object[]> bankBranch = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<>();
            final List<String> addedBanks = new ArrayList<>();
            for (final Object[] account : bankBranch) {
                final String bankBranchName = account[1].toString();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    final Map<String, Object> bankBrmap = new HashMap<>();
                    bankBrmap.put("bankBranchId", account[0].toString());
                    bankBrmap.put("bankBranchName", bankBranchName);
                    bankBranchList.add(bankBrmap);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithApprovedRemittances.");
        return "bank";
    }

    /**
     * @param voucherStatusKey - The appconfig key which gives the voucher workflow status
     * @param asOnDate
     * @param fundId
     * @return
     */
    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksWithPaymentInWorkFlow")
    public String ajaxLoadBanksWithPaymentInWorkFlow() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithPaymentInWorkFlow...");
        try {
            final String voucherStatusKey = parameters.get("voucherStatusKey")[0];
            final List<AppConfigValues> appConfig = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                    voucherStatusKey);
            if (appConfig == null || appConfig.isEmpty())
                throw new ValidationException("", "VOUCHER_STATUS_TO_CHECK_BANK_BALANCE is not defined in AppConfig");

            String appConfigValue = "";
            boolean condtitionalAppConfigIsPresent = false;
            String designationName = null;
            String functionaryName = null;
            String stateWithoutCondition = "";
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Before appConfig Checking  -----");
            for (final AppConfigValues app : appConfig) {
                appConfigValue = app.getValue();
                if (appConfigValue.contains(FinancialConstants.DELIMITER_FOR_VOUCHER_STATUS_TO_CHECK_BANK_BALANCE)) {
                    condtitionalAppConfigIsPresent = true;
                    final String[] array = appConfigValue
                            .split(FinancialConstants.DELIMITER_FOR_VOUCHER_STATUS_TO_CHECK_BANK_BALANCE);
                    if (array.length != 2)
                        throw new ValidationException("", "VOUCHER_STATUS_TO_CHECK_BANK_BALANCE is invalid");
                    // Order assumed is first is designation Name, second functionary Name
                    designationName = array[0];
                    functionaryName = array[1];
                } else
                    stateWithoutCondition = appConfigValue;
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("After appConfig Checking  -----");
            List<Bankaccount> bankAccounts = null;
            if (condtitionalAppConfigIsPresent) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("condtitionalAppConfigIsPresent -----");
                final String ownerIdList = getCommaSeperatedListForDesignationNameAndFunctionaryName(designationName,
                        functionaryName);
                bankAccounts = persistenceService.findAllBy(new StringBuilder("select p.bankaccount")
                        .append(" from Paymentheader p")
                        .append(" where p.voucherheader.voucherDate <= ? and p.state.type='Paymentheader' and p.bankaccount.bankbranch.bank.isactive = true")
                        .append(" and p.bankaccount.bankbranch.isactive = true and p.bankaccount.fund.id = ? and p.state.owner in (?)")
                        .append(" order by p.bankaccount.bankbranch.bank.name, p.bankaccount.bankbranch.branchname").toString(),
                        Constants.DDMMYYYYFORMAT2.format(asOnDate), fundId, ownerIdList);
            } else
            	bankAccounts = persistenceService.findAllBy(new StringBuilder("select p.bankaccount")
                        .append(" from Paymentheader p")
                        .append(" where p.voucherheader.voucherDate <= ? and p.state.type='Paymentheader' and p.bankaccount.bankbranch.bank.isactive = true")
                        .append(" and p.bankaccount.bankbranch.isactive = true and p.bankaccount.fund.id = ? and p.state.value like '")
                        .append(stateWithoutCondition).append("' order by p.bankaccount.bankbranch.bank.name, p.bankaccount.bankbranch.branchname").toString(),
                        Constants.DDMMYYYYFORMAT2.format(asOnDate), fundId);
            
            bankBranchList = new ArrayList<Map<String, Object>>();
            final List<String> addedBanks = new ArrayList<String>();
            for (final Bankaccount account : bankAccounts) {
                final String bankBranchName = account.getBankbranch().getBank().getName() + "-"
                        + account.getBankbranch().getBranchname();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    final Map<String, Object> bankBrmap = new HashMap<String, Object>();
                    bankBrmap.put("bankBranchId", account.getBankbranch().getBank().getId() + "-"
                            + account.getBankbranch().getId());
                    bankBrmap.put("bankBranchName", bankBranchName);
                    bankBranchList.add(bankBrmap);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithPaymentInWorkFlow.");
        return "bank";
    }

    private String getCommaSeperatedListForDesignationNameAndFunctionaryName(final String designationName,
            final String functionaryName) {
        final StringBuilder qryString = new StringBuilder("select pos_id from eg_eis_employeeinfo empinfo, eg_designation desg, functionary func")
                .append(" where empinfo.functionary_id = func.id and empinfo.DESIGNATIONID = desg.DESIGNATIONID")
                .append(" and empinfo.isactive = true and desg.DESIGNATION_NAME like :designationName and func.NAME like :functionaryName ");
        final Query query = persistenceService.getSession().createNativeQuery(qryString.toString());
        query.setParameter("designationName", designationName, StandardBasicTypes.STRING)
            .setParameter("functionaryName", functionaryName, StandardBasicTypes.STRING);
        final List<BigDecimal> result = query.list();
        
        if (result == null || result.isEmpty())
            throw new ValidationException("", "No employee with functionary -" + functionaryName + " and designation - "
                    + designationName);
        final StringBuffer returnListSB = new StringBuffer();
        String commaSeperatedList = "";
        for (final BigDecimal posId : result)
            returnListSB.append(posId.toString() + ",");
        commaSeperatedList = returnListSB.substring(0, returnListSB.length() - 1);
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Commo seperated  list - " + commaSeperatedList);
        return commaSeperatedList;
    }

    /**
     * @param voucherStatusKey - The appconfig key which gives the voucher workflow status
     * @param asOnDate
     * @param fundId
     * @return
     */
    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithPaymentInWorkFlow")
    public String ajaxLoadBankAccountsWithPaymentInWorkFlow() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithPaymentInWorkFlow...");
        try {
            accNumList = new ArrayList<Bankaccount>();
            final String voucherStatusKey = parameters.get("voucherStatusKey")[0];
            final List<AppConfigValues> appConfig = appConfigValuesService.getConfigValuesByModuleAndKey(Constants.EGF,
                    voucherStatusKey);
            if (appConfig == null || appConfig.isEmpty())
                throw new ValidationException("", "VOUCHER_STATUS_TO_CHECK_BANK_BALANCE is not defined in AppConfig");

            String appConfigValue = "";
            boolean condtitionalAppConfigIsPresent = false;
            String designationName = null;
            String functionaryName = null;
            String stateWithoutCondition = "";
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Starting app Config checking");

            for (final AppConfigValues app : appConfig) {
                appConfigValue = app.getValue();
                if (appConfigValue.contains(FinancialConstants.DELIMITER_FOR_VOUCHER_STATUS_TO_CHECK_BANK_BALANCE)) {
                    condtitionalAppConfigIsPresent = true;
                    final String[] array = appConfigValue
                            .split(FinancialConstants.DELIMITER_FOR_VOUCHER_STATUS_TO_CHECK_BANK_BALANCE);
                    if (array.length != 2)
                        throw new ValidationException("", "VOUCHER_STATUS_TO_CHECK_BANK_BALANCE is invalid");
                    // Order assumed is first is designation Name, second functionary Name
                    designationName = array[0];
                    functionaryName = array[1];
                } else
                    stateWithoutCondition = appConfigValue;
            }
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Finished app Config checking");
            List<Bankaccount> bankAccounts = null;
            if (condtitionalAppConfigIsPresent) {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("condtitionalAppConfigIsPresent ....");
                final String ownerIdList = getCommaSeperatedListForDesignationNameAndFunctionaryName(designationName,
                        functionaryName);
                
                bankAccounts = persistenceService
                        .findAllBy(
                                new StringBuilder("select p.bankaccount")
                                        .append(" from Paymentheader p where p.voucherheader.voucherDate <= ?")
                                        .append(" and p.state.type = 'Paymentheader' and p.bankaccount.isactive = true and p.bankaccount.bankbranch.isactive = true")
                                        .append(" and p.bankaccount.bankbranch.id = ? and p.bankaccount.fund.id = ? and p.state.owner in (?)")
                                        .append(" order by p.bankaccount.bankbranch.bank.name, p.bankaccount.bankbranch.branchname").toString(),
                                        Constants.DDMMYYYYFORMAT2.format(asOnDate), branchId, fundId, ownerIdList);
            } else
            	bankAccounts = persistenceService
                .findAllBy(
                        new StringBuilder("select p.bankaccount")
                                .append(" from Paymentheader p where p.voucherheader.voucherDate <= ?")
                                .append(" and p.state.type = 'Paymentheader' and p.bankaccount.isactive = true and p.bankaccount.bankbranch.isactive = true")
                        .append(" and p.bankaccount.bankbranch.id = ? and p.bankaccount.fund.id = ? and p.state.value like '")
                                .append(stateWithoutCondition).append("' order by p.bankaccount.bankbranch.bank.name, p.bankaccount.bankbranch.branchname").toString(),
                        Constants.DDMMYYYYFORMAT2.format(asOnDate), branchId, fundId);
            
            final List<String> addedBanks = new ArrayList<String>();
            for (final Bankaccount account : bankAccounts) {
                final String bankBranchName = account.getAccountnumber() + "-" + account.getAccounttype();
                if (!addedBanks.contains(bankBranchName)) {
                    addedBanks.add(bankBranchName);
                    accNumList.add(account);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bankaccount dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bankaccount dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithPaymentInWorkFlow.");
        return "bankAccNum";
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithApprovedPayments")
    public String ajaxLoadBankAccountsWithApprovedPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithApprovedPayments...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting | ajaxLoadBankAccountsWithApprovedPayments ");
        accNumList = new ArrayList<Bankaccount>();
        resolveFundIdAndAsOnDate();
        resolveAssignedChequeAccountParams();
        try {
            StringBuffer queryString = new StringBuffer();
            // query to fetch vouchers for which no cheque has been assigned
			queryString.append(
					"SELECT bankaccount.accountnumber AS accountnumber, bank.name AS bankName, CAST(bankaccount.id AS INTEGER) AS id, coa.glcode AS glCode")
					.append(" FROM chartofaccounts coa, bankaccount bankaccount ,bankbranch branch, bank bank")
					.append(" WHERE bankaccount.ID IN (SELECT DISTINCT PH.bankaccountnumberid")
					.append(" FROM paymentheader ph, voucherheader vh left OUTER JOIN egf_instrumentvoucher iv ON vh.id = iv.VOUCHERHEADERID")
					.append(" WHERE ph.voucherheaderid = vh.id AND vh.status = 0 AND VH.FUNDID = :fundId AND ph.voucherheaderid = vh.id AND iv.VOUCHERHEADERID IS NULL")
					.append(" AND vh.name NOT IN ('Remittance Payment', 'Salary Bill Payment')) AND coa.id = bankaccount.glcodeid AND bankaccount.type IN ('RECEIPTS_PAYMENTS','PAYMENTS')")
					.append(" AND bankaccount.fundid =:fundId AND bankaccount.branchid = branch.id and branch.bankid = bank.id and bankaccount.branchid = :branchId")
					.append(" and bankaccount.isactive = true");
            // queryString =
            // queryString.append(" and ph.bankaccountnumberid=bankaccount.id and
            // vh.type='"+FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT+"' and vh.name NOT IN (
            // '"+FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE+"','"+FinancialConstants.PAYMENTVOUCHER_NAME_SALARY+"' ) ");
            // query to fetch vouchers for which cheque has been assigned and surrendered
			queryString.append(
					" union select bankaccount.accountnumber as accountnumber, bank.name as bankName, cast(bankaccount.id as integer) as id, coa.glcode as glCode ")
					.append(" from chartofaccounts coa, Bankaccount bankaccount, bankbranch branch, bank bank")
					.append(" where bankaccount.branchid = branch.id and branch.bankid = bank.id and bankaccount.id in (SELECT DISTINCT PH.bankaccountnumberid from")
					.append(" egf_instrumentvoucher iv, voucherheader vh, paymentheader ph, egw_status egws, (select ih1.id, ih1.id_status from egf_instrumentheader ih1,")
					.append(" (select bankid, bankaccountid, instrumentnumber, max(id) as id from egf_instrumentheader group by bankid,bankaccountid, instrumentnumber) max_rec")
					.append(" where max_rec.bankid = ih1.bankid and max_rec.bankaccountid = ih1.bankaccountid and max_rec.instrumentnumber = ih1.instrumentnumber")
					.append(" and max_rec.id = ih1.id) ih where ph.voucherheaderid = vh.id and vh.fundid = :fundId and vh.status = 0 and ph.voucherheaderid = vh.id")
					.append(" and iv.voucherheaderid = vh.id and iv.instrumentheaderid = ih.id and ph.bankaccountnumberid = bankaccount.id and vh.type = '")
					.append(FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT).append("'")
					.append(" and vh.name NOT IN ( '").append(FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE)
					.append("','").append(FinancialConstants.PAYMENTVOUCHER_NAME_SALARY).append("' )")
					.append(" and ih.id_status = egws.id")
					.append(" and egws.description in ('Surrendered','Surrender_For_Reassign')) and coa.id = bankaccount.glcodeid")
					.append(" and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and bankaccount.branchid = :branchId");
			if (fundId != null && fundId != 0 && fundId != -1)
				queryString.append(" and bankaccount.fundid = :fundId");
			Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
			query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG).setParameter("branchId", branchId,
					StandardBasicTypes.INTEGER);

    final List<Object[]> bankAccounts = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank accont list size is " + bankAccounts.size() + "and Query is " + queryString.toString());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    // bankaccount.setAccounttype(account[1].toString());
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    final Bankbranch branch = new Bankbranch();
                    final Bank bank = new Bank();
                    bank.setName(account[1].toString());
                    branch.setBank(bank);
                    bankaccount.setBankbranch(branch);
                    chartofaccounts.setGlcode(account[3].toString());
                    bankaccount.setChartofaccounts(chartofaccounts);
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final Exception e) {
            LOGGER.error("Exception occurred while loading approved-payment bank accounts", e);
        }
        if (accNumList.isEmpty() && branchId != null)
            loadActivePaymentAccountsForBranch();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Done | ajaxLoadBankAccountsWithApprovedPayments ");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithApprovedPayments.");
        return "bankAccNum";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithApprovedSalaryPayments")
    public String ajaxLoadBankAccountsWithApprovedSalaryPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithApprovedSalaryPayments...");
        try {
            accNumList = new ArrayList<Bankaccount>();
            // query to fetch vouchers for which no cheque has been assigned
            StringBuilder queryString = new StringBuilder("select distinct bankaccount.accountnumber as accountnumber, bankaccount.accounttype as accounttype,")
                    .append(" cast(bankaccount.id as integer) as id, coa.glcode as glCode")
                    .append(" from chartofaccounts coa, voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis, eg_department d,")
                    .append(" generalledger gl, paymentheader ph, egf_instrumentvoucher iv right outer join voucherheader vh1 on vh1.id = iv.VOUCHERHEADERID")
                    .append(" where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid and vmis.departmentid = d.id and vh.status = 0")
                    .append(" and coa.id = bankaccount.glcodeid and gl.voucherheaderid = vh.id and ph.voucherheaderid = vh.id and bank.isactive = true and bankBranch.isactive = true")
                    .append(" and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS')")
                    .append(" and bankaccount.branchid = :branchId and vh1.id = vh.id and iv.VOUCHERHEADERID is null");

            if (fundId != null && fundId != 0 && fundId != -1)
                queryString.append(" and bankaccount.fundid = :fundId");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString.append(" and vmis.departmentcode = :departmentId");
            queryString.append(" and ph.bankaccountnumberid = bankaccount.id and vh.type = :voucherType'")
                    .append(" and vh.name = :voucherName");
            // query to fetch vouchers for which cheque has been assigned and surrendered
			queryString.append(
					" union select bankaccount.accountnumber as accountnumber, bankaccount.accounttype as accounttype, cast(bankaccount.id as integer) as id, coa.glcode as glCode")
					.append(" from chartofaccounts coa, egf_instrumentvoucher iv, voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis,")
					.append(" eg_department d,generalledger gl, paymentheader ph, egw_status egws, (select ih1.id, ih1.id_status from egf_instrumentheader ih1,")
					.append(" (select bankid, bankaccountid, instrumentnumber, max(lastmodifieddate) as lastmodifieddate from egf_instrumentheader")
					.append(" group by bankid, bankaccountid, instrumentnumber) max_rec")
					.append(" where max_rec.bankid = ih1.bankid and max_rec.bankaccountid = ih1.bankaccountid and max_rec.instrumentnumber = ih1.instrumentnumber")
					.append(" and max_rec.lastmodifieddate = ih1.lastmodifieddate) ih where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid and vmis.departmentid = d.id")
					.append(" and coa.id = bankaccount.glcodeid and vh.status = 0 and gl.voucherheaderid = vh.id and ph.voucherheaderid = vh.id and bank.isactive = true")
					.append(" and bankBranch.isactive = true and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS')")
					.append(" and iv.voucherheaderid = vh.id and iv.instrumentheaderid = ih.id and bankaccount.branchid = :branchId and ih.id_status = egws.id")
					.append(" and egws.description in ('Surrendered','Surrender_For_Reassign')");
			if (fundId != null && fundId != 0 && fundId != -1)
				queryString.append(" and bankaccount.fundid = :fundId ");
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                queryString = queryString.append(" and vmis.departmentcode=:departmentId ");
            queryString.append(" and ph.bankaccountnumberid = bankaccount.id and vh.type = :voucherType and vh.name = :voucherName order by 4 ");

            Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
            query.setParameter("branchId", branchId, StandardBasicTypes.INTEGER);
            if (fundId != null && fundId != 0 && fundId != -1)
                query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
            if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
                query.setParameter("departmentId", departmentId, StandardBasicTypes.LONG);
            query.setParameter("voucherType", FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT, StandardBasicTypes.STRING)
                    .setParameter("voucherName", FinancialConstants.PAYMENTVOUCHER_NAME_SALARY, StandardBasicTypes.STRING);

            final List<Object[]> bankAccounts = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank accont list size is " + bankAccounts.size());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    bankaccount.setAccounttype(account[1].toString());
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    chartofaccounts.setGlcode(account[3].toString());
                    bankaccount.setChartofaccounts(chartofaccounts);
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithApprovedSalaryPayments.");
        return "bankAccNum";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithApprovedPensionPayments")
    public String ajaxLoadBankAccountsWithApprovedPensionPayments() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithApprovedPensionPayments...");
        try {
            accNumList = new ArrayList<Bankaccount>();
			StringBuffer queryString = new StringBuffer();
			// query to fetch vouchers for which no cheque has been assigned
			queryString.append(
					"select distinct bankaccount.accountnumber as accountnumber, bankaccount.accounttype as accounttype, cast(bankaccount.id as integer) as id, coa.glcode as glCode")
					.append(" from chartofaccounts coa, voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis, eg_department d,")
					.append(" generalledger gl, paymentheader ph, egf_instrumentvoucher iv right outer join voucherheader vh1 on vh1.id = iv.VOUCHERHEADERID")
					.append(" where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid and vmis.departmentid = d.id and vh.status = 0")
					.append(" and coa.id = bankaccount.glcodeid and gl.voucherheaderid = vh.id and ph.voucherheaderid = vh.id and bank.isactive = true and bankBranch.isactive = true")
					.append(" and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS', 'PAYMENTS')")
					.append(" and bankaccount.branchid = :branchId and vh1.id = vh.id and iv.VOUCHERHEADERID is null");

			if (fundId != null && fundId != 0 && fundId != -1)
				queryString.append(" and bankaccount.fundid = :fundId ");
			if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
				queryString.append(" and vmis.departmentcode = :departmentId ");
			queryString.append(
					" and ph.bankaccountnumberid = bankaccount.id and vh.type = :voucherType and vh.name = :voucherName");

			// query to fetch vouchers for which cheque has been assigned and surrendered
			queryString.append(
					" union select bankaccount.accountnumber as accountnumber, bankaccount.accounttype as accounttype, cast(bankaccount.id as integer) as id, coa.glcode as glCode")
					.append(" from chartofaccounts coa, egf_instrumentvoucher iv, voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis,")
					.append(" eg_department d, generalledger gl, paymentheader ph, egw_status egws, (select ih1.id, ih1.id_status from egf_instrumentheader ih1,")
					.append(" (select bankid, bankaccountid, instrumentnumber, max(lastmodifieddate) as lastmodifieddate")
					.append(" from egf_instrumentheader group by bankid, bankaccountid, instrumentnumber) max_rec")
					.append(" where max_rec.bankid = ih1.bankid and max_rec.bankaccountid = ih1.bankaccountid and max_rec.instrumentnumber = ih1.instrumentnumber")
					.append(" and max_rec.lastmodifieddate = ih1.lastmodifieddate) ih where ph.voucherheaderid = vh.id and vh.id = vmis.voucherheaderid")
					.append(" and vmis.departmentid = d.id and coa.id = bankaccount.glcodeid and vh.status = 0 and gl.voucherheaderid = vh.id and ph.voucherheaderid = vh.id")
					.append(" and bank.isactive = true and bankBranch.isactive = true and bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid")
					.append(" and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and iv.voucherheaderid = vh.id and iv.instrumentheaderid = ih.id")
					.append(" and bankaccount.branchid = :branchId and ih.id_status = egws.id and egws.description in ('Surrendered','Surrender_For_Reassign')");
			if (fundId != null && fundId != 0 && fundId != -1)
				queryString.append(" and bankaccount.fundid = :fundId");
			if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
				queryString.append(" and vmis.departmentcode = :departmentId");
			queryString.append(
					" and ph.bankaccountnumberid = bankaccount.id and vh.type = :voucherType and vh.name = :voucherName order by 4");
			Query query = persistenceService.getSession().createNativeQuery(queryString.toString());
			query.setParameter("branchId", branchId, StandardBasicTypes.INTEGER);
			if (fundId != null && fundId != 0 && fundId != -1)
				query.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG);
			if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
				query.setParameter("departmentId", departmentId, StandardBasicTypes.LONG);
			query.setParameter("voucherType", FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT, StandardBasicTypes.STRING)
					.setParameter("voucherName", FinancialConstants.PAYMENTVOUCHER_NAME_PENSION, StandardBasicTypes.STRING);

			final List<Object[]> bankAccounts = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank accont list size is " + bankAccounts.size());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    bankaccount.setAccounttype(account[1].toString());
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    chartofaccounts.setGlcode(account[3].toString());
                    bankaccount.setChartofaccounts(chartofaccounts);
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithApprovedPensionPayments.");
        return "bankAccNum";
    }

    @SuppressWarnings("unchecked")
    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithApprovedRemittances")
    public String ajaxLoadBankAccountsWithApprovedRemittances() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithApprovedRemittances...");
        /*
         * LTS Migration Fix (Struts 7): remittance search AJAX may not bind
         * branchId/fundId from the query string; resolve from request like the
         * approved-payments cheque-assignment path.
         */
        resolveFundIdAndAsOnDate();
        resolveAssignedChequeAccountParams();
        accNumList = new ArrayList<Bankaccount>();
        if (branchId == null || branchId <= 0) {
            LOGGER.warn("ajaxLoadBankAccountsWithApprovedRemittances missing branchId");
            return "bankAccNum";
        }
        try {
            StringBuffer queryString = new StringBuffer();
            queryString = queryString
                    .append("select distinct bankaccount.accountnumber as accountnumber,bank.name as bankName,cast(bankaccount.id as integer) as id,coa.glcode as glCode ")
                    .append("from Bank bank,Bankbranch bankBranch,Bankaccount bankaccount,chartofaccounts coa ")
                    .append("where  bank.id = bankBranch.bankid and bankBranch.id = bankaccount.branchid and bankaccount.type in ('RECEIPTS_PAYMENTS','PAYMENTS') and coa.id=bankaccount.glcodeid  and bankaccount.branchid=:branchId ");
            Query query = persistenceService.getSession().createNativeQuery(queryString.toString())
                    .setParameter("branchId", branchId, StandardBasicTypes.INTEGER);
            final List<Object[]> bankAccounts = query.list();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankAccounts.size());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    bankaccount.setAccounttype(account[1].toString());
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    chartofaccounts.setGlcode(account[3].toString());
                    final Bankbranch branch = new Bankbranch();
                    final Bank bank = new Bank();
                    bank.setName(account[1].toString());
                    branch.setBank(bank);
                    bankaccount.setBankbranch(branch);
                    bankaccount.setChartofaccounts(chartofaccounts);
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithApprovedRemittances.");
        return "bankAccNum";
    }

    public void setAsOnDate(final Date asOnDate) {
        this.asOnDate = asOnDate;
    }

    public Date getAsOnDate() {
        return asOnDate;
    }

    @Action(value = "/voucher/common-ajaxLoadDesg")
    public String ajaxLoadDesg() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadDesg...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadDesg | Start ");

        Map<String, Object> map = null;
        if (getBillRegisterId() != null) {
            final EgBillregister cbill = (EgBillregister) persistenceService.find(" from EgBillregister where id=?",
                    getBillRegisterId());
            map = voucherService.getDesgBYPassingWfItem(scriptName, cbill, departmentId);
        } else
            map = voucherService.getDesgBYPassingWfItem(scriptName, null, departmentId);

        designationList = (List<Map<String, Object>>) map.get("designationList");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadDesg | End ");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadDesg.");
        return "desg";
    }

    public String ajaxLoadDefaultDepartment() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadDefaultDepartment...");
        defaultDepartment = voucherService.getDefaultDepartment().toString();
        return "defaultDepartment";
    }

    @SkipValidation
    @Action(value = "/voucher/common-ajaxLoadFundSource")
    public String ajaxLoadFundSource() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadFundSource...");
        /*
         * LTS: Struts 7 often leaves subSchemeId unbound on YUI AJAX. The old
         * null-check then skipped the query, so Fund Source stayed on Choose.
         * -1 means all active fund sources (same as populatefundsourceId on scheme change).
         */
        if (subSchemeId == null) {
            final String sid = ServletActionContext.getRequest().getParameter("subSchemeId");
            if (sid != null && !sid.trim().isEmpty()) {
                try {
                    subSchemeId = Integer.valueOf(sid.trim());
                } catch (final NumberFormatException e) {
                    subSchemeId = -1;
                }
            } else {
                subSchemeId = -1;
            }
        }
        LOGGER.info("ajaxLoadFundSource subSchemeId=" + subSchemeId);
        try {
            fundSouceList = financingSourceService.getFinancialSourceBasedOnSubScheme(subSchemeId);
            if (fundSouceList == null)
                fundSouceList = new ArrayList<Fundsource>();
            LOGGER.info("ajaxLoadFundSource size=" + fundSouceList.size());
        } catch (final Exception e) {
            LOGGER.error("Failed to load fund sources", e);
            fundSouceList = new ArrayList<Fundsource>();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadFundSource.");
        return Constants.FUNDSOURCE;
    }

    @Action(value = "/voucher/common-ajaxLoadProjectCodesForSubScheme")
    public String ajaxLoadProjectCodesForSubScheme() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadProjectCodesForSubScheme...");
        final String sql = "select pc.id as id,pc.code as code,pc.name as name from egw_projectcode pc,egf_subscheme_project ssp where  pc.id=ssp.projectcodeid and ssp.subschemeid=:subSchemeId";
        final NativeQuery pcQuery = persistenceService.getSession().createNativeQuery(sql);
        pcQuery.addScalar("id", StandardBasicTypes.LONG)
                .addScalar("code")
                .addScalar("name")
                .setParameter("subSchemeId", subSchemeId, StandardBasicTypes.INTEGER)
                .setResultTransformer(Transformers.aliasToBean(LoanGrantBean.class));
        projectCodeList = pcQuery.list();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadProjectCodesForSubScheme.");
        return "projectcodes";
    }

    @Action(value = "/voucher/common-ajaxLoadUnmappedProjectCodesBy20")
    public String ajaxLoadUnmappedProjectCodesBy20() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadUnmappedProjectCodesBy20...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("schemeId Id received is :  " + subSchemeId + "   and Startswith   :" + startsWith);
        projectCodeStringList = new ArrayList<String>();
        startsWith = "%" + startsWith + "%";
        final StringBuilder qry = new StringBuilder(" select * from (SELECT pc.code || '`-`'|| pc.description|| '`~`' || pc.id FROM egw_projectcode pc LEFT OUTER JOIN")
                .append(" egf_subscheme_project sp ON pc.id = sp.projectcodeid WHERE sp.projectcodeid IS NULL and pc.code like :startsWith")
                .append(" ORDER BY pc.code) where rownum <= 20");
        if (null == subSchemeId) {

        } else {
        	Query query = persistenceService.getSession().createNativeQuery(qry.toString())
                    .setParameter("startsWith", startsWith, StandardBasicTypes.STRING);
            projectCodeStringList = query.list();
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Scheme List size : " + projectCodeStringList.size());
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadUnmappedProjectCodesBy20.");
        return "projectCodesBy20";
    }

    @Action(value = "/voucher/common-ajaxLoadDocumentNoAndDate")
    public String ajaxLoadDocumentNoAndDate() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadDocumentNoAndDate...");
        if (billVhId != null && billVhId != 0) {
            final CVoucherHeader vh = (CVoucherHeader) persistenceService.find("from CVoucherHeader where id=?", billVhId);
            if (vh != null) {
                final EgBillregister bill = (EgBillregister) persistenceService.find(
                        "select mis.egBillregister from EgBillregistermis  mis where mis.voucherHeader=?", vh);
                if (bill != null) {
                    final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    final String billDateStr = sdf.format(bill.getBilldate());
                    returnStream = bill.getBillnumber() + "$" + billDateStr;
                }
            }
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadDocumentNoAndDate.");
        return "AJAX_RESULT";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadChequeNoAndDate")
    public String ajaxLoadChequeNoAndDate() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadChequeNoAndDate...");
        if (billVhId != null && billVhId.intValue() != 0) {
        	final StringBuilder instrumentRelatedQry = new StringBuilder("select NVL(ih.id,0), NVL( NVL(ih.instrumentnumber, ih.transactionnumber),0), TO_CHAR(NVL(ih.instrumentdate," +
                    " ih.transactiondate),'dd/mm/yyyy'), NVL( ih.instrumentamount,0), NVL(ba.id,0), NVL(ba.accountnumber,0), NVL(bb.branchname ||'-' || b.name,0)" +
                    " from egf_instrumentvoucher iv, egf_instrumentheader ih LEFT OUTER JOIN bankaccount ba ON ih.bankaccountid = ba.id left outer JOIN bankbranch bb on ba.branchid = bb.id" +
                    " LEFT OUTER JOIN bank b ON b.id = bb.bankid where iv.instrumentheaderid = ih.id and iv.voucherheaderid = :billVhId");
            final String voucherDescriptionQry = "select NVL(vh.description,0) from voucherheader vh where vh.id = :billVhId";
            final StringBuilder fundingAgencyQry = new StringBuilder(" select nvl( fa.id,0), nvl(fa.name,0)")
                    .append(" from generalledger g LEFT OUTER JOIN generalledgerdetail gd on gd.generalledgerid= g.id, egf_fundingagency fa")
                    .append(" where gd.detailtypeid = (select id from accountdetailtype where accountdetailtype.name = 'FundingAgency')")
                    .append(" and fa.id = gd.detailkeyid and g.voucherheaderid = :billVhId");
            final List<Object[]> resultList1 = persistenceService.getSession().createNativeQuery(instrumentRelatedQry.toString())
                    .setParameter("billVhId", billVhId, StandardBasicTypes.LONG).list();
            final List<Object[]> resultList2 = persistenceService.getSession().createNativeQuery(voucherDescriptionQry)
                    .setParameter("billVhId", billVhId, StandardBasicTypes.LONG).list();
            final List<Object[]> resultList3 = persistenceService.getSession().createNativeQuery(fundingAgencyQry.toString())
                    .setParameter("billVhId", billVhId, StandardBasicTypes.LONG).list();
            String instrumentResult;
            if (resultList1.size() == 0)
                instrumentResult = "0$0$-$0$0$0$-";
            else
                instrumentResult = resultList1.get(0)[0] + "$" + resultList1.get(0)[1] + "$"
                        + (resultList1.get(0)[2] == null ? "-" : resultList1.get(0)[2]) +
                        "$" + resultList1.get(0)[3] + "$" + resultList1.get(0)[4] + "$" + resultList1.get(0)[5] + "$"
                        + resultList1.get(0)[6];
            final String voucherDescResult = resultList2.size() == 0 ? "$0" : "$" + resultList2.get(0);
            final String fundingAgencyResult = resultList3.size() == 0 ? "$0$0" : "$" + resultList3.get(0)[0] + "$" + resultList3
                    .get(0)[1];
            returnStream = instrumentResult + voucherDescResult + fundingAgencyResult;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadChequeNoAndDate.");
        return "AJAX_RESULT";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadVoucherAmount")
    public String ajaxLoadVoucherAmount() {
        final String chequeAmtQry = "select ih.instrumentamount, ih.id from egf_instrumentheader ih, egf_instrumentvoucher iv where ih.id= iv.instrumentheaderid and iv.voucherheaderid=?1";
        final List<Object[]> resultList2 = persistenceService.getSession().createNativeQuery(chequeAmtQry).setParameter(1, billVhId)
                .list();
        String chqAmtResult;
        if (resultList2.size() == 0)
            chqAmtResult = "0$0";
        else
            chqAmtResult = resultList2.get(0)[0] + "$" + resultList2.get(0)[1];
        new CommonBean();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadFundingAgencyAmount...");
        if (billVhId != null && billVhId.intValue() != 0) {
            final String grantAMountQry = "select sum(g.debitAmount) as accountBalance from generalledger g where g.voucherheaderid=?1 ";
            final Query qry = persistenceService.getSession().createNativeQuery(grantAMountQry)
                    .addScalar("accountBalance", StandardBasicTypes.BIG_DECIMAL);
            qry.setParameter(1, billVhId);
            qry.setResultTransformer(Transformers.aliasToBean(CommonBean.class));
            final List<CommonBean> resultList1 = qry.list();
            String grantAmountResult;
            if (resultList1.size() == 0)
                grantAmountResult = "0$0";
            else
                grantAmountResult = resultList1.get(0).getAccountBalance().toString();
            if (resultList2.size() == 0)
                returnStream = grantAmountResult;
            else
                returnStream = chqAmtResult;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadFundingAgencyAmount.");
        return "AJAX_RESULT";
    }

    @SuppressWarnings("unchecked")
    public String ajaxLoadGrantAmountSubledger() {
        if (billVhId != null && billVhId.intValue() != 0) {
            String amount = null;
            String subLedger = null;
            String amtSubledger = null;
            final Accountdetailtype detType = (Accountdetailtype) persistenceService
                    .find("from Accountdetailtype where name='Commercial Tax Officer'");
            final List<CGeneralLedger> glList = persistenceService.findAllBy(
                    "from CGeneralLedger where voucherHeaderId.id=?", billVhId);
            if (detType != null)
                for (final CGeneralLedger gl : glList) {
                    final Set<CGeneralLedgerDetail> generalLedgerDetails = gl.getGeneralLedgerDetails();
                    for (final CGeneralLedgerDetail gld : generalLedgerDetails)
                        if (detType.getId().toString().equals(gld.getDetailTypeId().getId().toString())) {
                            amount = gld.getAmount().toString();

                            final AccountEntity entity = (AccountEntity) persistenceService.find(
                                    "from AccountEntity where id=? and accountdetailtype=?", gld.getDetailKeyId(), detType);
                            subLedger = entity.getName();

                        }
                }
            if (amount == null && subLedger == null)
                amtSubledger = "0$0";
            else
                amtSubledger = amount + "$" + subLedger;
            returnStream = amtSubledger;
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadGrantAmountSubledger.");
        return "AJAX_RESULT";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBranchAccountNumbers")
    public String ajaxLoadBranchAccountNumbers() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBranchAccountNumbers...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadBranchAccountNumbers");
        try {
            accNumList = getPersistenceService()
                    .findAllBy(
                            "from Bankaccount ba where ba.bankbranch.id=? and isactive=true order by ba.chartofaccounts.glcode",
                            branchId);
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank account Number list size =  " + accNumList.size());
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting bank account numbers " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting bank account numbers " +
           * e.getMessage(), new HibernateException(e.getMessage())); }
           */
        final StringBuffer accountNumbers = new StringBuffer(256);
        for (final Bankaccount acc : accNumList)
            accountNumbers.append(acc.getChartofaccounts().getGlcode() + "-" + acc.getAccountnumber() + "~" + acc.getId() + "$");
        returnStream = accountNumbers.toString();
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBranchAccountNumbers.");
        return "AJAX_RESULT";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxloadcoa")
    public String ajaxLoadCOA() {
        /*
         * Hibernate 6 / Struts 7 Upgrade Fix for Chart of Accounts Tree Hierarchy:
         * 1. Struts Parameter Binding Fallback: If `glCode` field setter was not populated by Struts binder,
         *    retrieve `glCode` directly from `ServletActionContext.getRequest().getParameter("glCode")`.
         * 2. Root Node Classification Filter: When loading the top-level heads (glCode == null), filter by
         *    `where parentId is null and (classification = 0 or length(glcode) = 1)`.
         *    This prevents sample/test data Detailed Codes (classification = 4) that have null parentId in DB
         *    from being rendered at the root level alongside Income, Expenses, Assets, Liabilities.
         * 3. JPA Ordinal Parameter (`?1`): Hibernate 6 enforces strict JPA parameter indexing where un-indexed `?`
         *    is deprecated/rejected. Replaced `parentId=?` with `parentId=?1`.
         */
        try {
            if (glCode == null || glCode.trim().isEmpty() || "null".equalsIgnoreCase(glCode.trim())) {
                if (ServletActionContext.getRequest() != null) {
                    glCode = ServletActionContext.getRequest().getParameter("glCode");
                }
            }

            if (glCode == null || glCode.trim().isEmpty() || "null".equalsIgnoreCase(glCode.trim())) {
                coaList = (List<CChartOfAccounts>) persistenceService
                        .findAllBy("from CChartOfAccounts where parentId is null and (classification = 0 or length(glcode) = 1) order by glcode asc");
            } else {
                coaList = (List<CChartOfAccounts>) persistenceService.findAllBy(
                        "from CChartOfAccounts where parentId=?1 order by glcode asc",
                        Long.valueOf(glCode.trim()));
            }
        } catch (Exception e) {
            LOGGER.error("Error loading Chart of Accounts tree node for glCode: " + glCode, e);
            coaList = Collections.emptyList();
        }
        result = new StringBuffer();
        StringBuffer type = new StringBuffer();
        StringBuffer chartOfAccounts_ID = new StringBuffer();
        StringBuffer chartOfAccounts_name = new StringBuffer();
        StringBuffer chartOfAccounts_parentId = new StringBuffer();
        StringBuffer chartOfAccounts_glCode = new StringBuffer();
        int i = 0;
        for (CChartOfAccounts cc : coaList) {
            if (i > 0) {
                type.append("+");
                chartOfAccounts_ID.append("+");
                chartOfAccounts_name.append("+");
                chartOfAccounts_parentId.append("+");
                chartOfAccounts_glCode.append("+");

            }
            i++;
            type.append("null");
            chartOfAccounts_ID.append(cc.getId());
            chartOfAccounts_name.append(cc.getName());
            chartOfAccounts_parentId.append(cc.getParentId());
            chartOfAccounts_glCode.append(cc.getGlcode());

        }
        result.append(type.toString() + "^" + chartOfAccounts_ID.toString() + "^" + chartOfAccounts_name + "^" +
                chartOfAccounts_parentId.toString() + "^" + chartOfAccounts_glCode.toString() + "^");
        return "COA";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadGLreportCodes")
    public String ajaxLoadGLreportCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadGLreportCodes...");
        /*
         * Struts 7 / Hibernate 6 migration note:
         * Autocomplete requests may submit the search text as glCode or query, and
         * may send the literal "null". Normalize the input first. Parentheses around
         * the OR clause are required so Hibernate 6 preserves the intended predicate
         * precedence with the active/classification filters.
         */
        if (glCode == null || glCode.trim().isEmpty() || "null".equalsIgnoreCase(glCode.trim())) {
            glCode = ServletActionContext.getRequest().getParameter("glCode");
            if (glCode == null || glCode.trim().isEmpty()) {
                glCode = ServletActionContext.getRequest().getParameter("query");
            }
        }
        if (glCode == null || glCode.trim().isEmpty())
            glCodesList = new ArrayList<CChartOfAccounts>();
        else {
            
            String glCodeName = "%" + glCode.toLowerCase() + "%";
            glCodesList = persistenceService
                    .findAllBy(
                            new StringBuilder("select ca from CChartOfAccounts ca where ca.glcode not in (select glcode from CChartOfAccounts where glcode like '47%'")
                                    .append(" and glcode not like '471%' and glcode !='4741') and ca.glcode not in (select glcode from CChartOfAccounts where glcode = '471%') ")
                            .append(" and ca.isActiveForPosting = true and ca.classification = 4 and (ca.glcode like ? or lower (ca.name) like ?)").toString(), glCodeName, glCodeName);
        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadGLreportCodes.");
        return "glCodes";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSLreportCodes")
    public String ajaxLoadSLreportCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSLreportCodes...");
        /*
         * Struts 7 / Hibernate 6 migration note:
         * Accept both glCode and query parameter names from legacy autocomplete calls.
         * The grouped OR condition prevents the name match from bypassing the
         * classification/detail-code filters under Hibernate 6 query parsing.
         */
        if (glCode == null || glCode.trim().isEmpty() || "null".equalsIgnoreCase(glCode.trim())) {
            glCode = ServletActionContext.getRequest().getParameter("glCode");
            if (glCode == null || glCode.trim().isEmpty()) {
                glCode = ServletActionContext.getRequest().getParameter("query");
            }
        }
        if (glCode == null || glCode.trim().isEmpty())
            glCodesList = new ArrayList<CChartOfAccounts>();
        else
        {
            String glCodeName = "%" + glCode.toLowerCase() + "%";
            /*
             * LTS Migration Note [Account Code Autocomplete Query]:
             * Query CChartOfAccounts directly without inner-joining CChartOfAccountDetail
             * to allow autocomplete suggestions for all matching chart of accounts (including high-level and sub-ledger codes).
             */
            glCodesList = persistenceService.findAllBy(
                    "select ca from CChartOfAccounts ca where (ca.glcode like ? or lower(ca.name) like ?) order by ca.glcode",
                    glCodeName, glCodeName);
        }

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSLreportCodes.");
        return "glCodes";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadFunctionCodes")
    public String ajaxLoadFunctionCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadFunctionCodes...");
        if (function == null)
            functionCodesList = new ArrayList<CFunction>();
        else {
            String funCodeName = "%" + function.toLowerCase() + "%";
            functionCodesList = persistenceService.findAllBy("select f from CFunction f where isActive = true and isNotLeaf = false and lower(name) like ? or lower(code) like ? ", 
            		funCodeName, funCodeName);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadFunctionCodes.");
        return "functionCodes";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadSubLedgerTypesByGlCode")
    public String ajaxLoadSubLedgerTypesByGlCode() {

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadSubLedgerTypesByGlCode...");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("CommonAction | ajaxLoadSubLedgerTypesByGlCode");
        try {
            if (glCode == null || glCode.trim().isEmpty() || "null".equalsIgnoreCase(glCode.trim())) {
                glCode = ServletActionContext.getRequest().getParameter("glCode");
                if (glCode == null || glCode.trim().isEmpty()) {
                    glCode = ServletActionContext.getRequest().getParameter("glcode");
                }
            }
            if (glCode != null && !glCode.trim().isEmpty()) {
                subLedgerTypeList = getPersistenceService()
                        .findAllBy(
                                "select distinct adt from Accountdetailtype adt, CChartOfAccountDetail cad where cad.glCodeId.glcode = ? and cad.detailTypeId.id = adt.id",
                                glCode.trim());
            }
            LOGGER.info("ajaxLoadSubLedgerTypesByGlCode: glCode=" + glCode + ", resultSize=" + (subLedgerTypeList != null ? subLedgerTypeList.size() : 0));
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting Sub Ledger Type " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) {
           * LOGGER.error("Exception occured while getting Sub Ledger Type " +
           * e.getMessage(), new HibernateException(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadSubLedgerTypesByGlCode.");
        return "subLedgerType";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-showHistory")
    public String showHistory() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("stateId=" + stateId);
        return "workflowHistory";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllCoaCodes")
    public String ajaxGetAllCoaCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllCoaCodes...");

        /*
         * Hibernate 6 migration note:
         * HQL must use the Java property isActiveForPosting. The lowercase
         * isactiveforposting token refers to the database column style and is rejected
         * by strict entity attribute resolution.
         */
        coaList = persistenceService.findAllBy(
                " from CChartOfAccounts where classification=4 and isActiveForPosting = true order by glcode ");
        // String
        // query="select glcode||'`-`'||name||'`~`'||ID as \"code\" from chartofaccounts where classification=4 and
        // isactiveforposting = true order by glcode ";*/

        result = new StringBuffer();
        for (CChartOfAccounts cc : coaList) {
            result.append(cc.getGlcode() + "`-`");
            result.append(cc.getName() + "`~`");
            result.append(cc.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllCoaCodes.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllCoaCodesExceptCashBank")
    public String ajaxGetAllCoaCodesExceptCashBank() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllCoaCodesExceptCashBank...");

        coaList = persistenceService
                .findAllBy(new StringBuilder(" FROM CChartOfAccounts WHERE classification = 4 AND isActiveForPosting = true AND parentId not in")
                        .append(" (select id from CChartOfAccounts where purposeId in (SELECT id FROM EgfAccountcodePurpose WHERE UPPER(name) = UPPER('Cash In Hand')")
                        .append(" OR UPPER(name) = UPPER('Bank Codes') OR UPPER(name) = UPPER('Cheque In Hand'))) and id not in")
                        .append(" (select id from CChartOfAccounts where purposeId in (SELECT id FROM EgfAccountcodePurpose WHERE UPPER(name) = UPPER('Cash In Hand')")
                        .append(" OR UPPER(name) = UPPER('Bank Codes') OR UPPER(name) = UPPER('Cheque In Hand'))) and glcode not like '471%' ORDER BY glcode ").toString());

        result = new StringBuffer();
        for (CChartOfAccounts cc : coaList) {
            result.append(cc.getGlcode() + "`-`");
            result.append(cc.getName() + "`~`");
            result.append(cc.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllCoaCodesExceptCashBank.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllAssetCodes")
    public String ajaxGetAllAssetCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllAssetCodes...");

        /*
         * Hibernate 6 migration note:
         * Use the mapped Java property isActiveForPosting instead of the old
         * database-style token so HQL semantic validation succeeds.
         */
        coaList = persistenceService.findAllBy(
                " from CChartOfAccounts where classification=4 and isActiveForPosting = true and type = 'A' order by glcode  ");
        // String
        // query="select glcode||'`-`'||name|| '`-`' || ID as \"code\" from chartofaccounts where classification=4 and
        // isactiveforposting = true and type = 'A' order by glcode ";

        result = new StringBuffer();
        for (CChartOfAccounts cc : coaList) {
            result.append(cc.getGlcode() + "`-`");
            result.append(cc.getName() + "`-`");
            result.append(cc.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllAssetCodes.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllLiabCodes")
    public String ajaxGetAllLiabCodes() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllLiabCodes...");

        /*
         * Hibernate 6 migration note:
         * Use isActiveForPosting, the entity property name, because HQL no longer
         * resolves database-style attribute names implicitly.
         */
        coaList = persistenceService.findAllBy(
                " from CChartOfAccounts where classification=4 and isActiveForPosting = true and type = 'L' order by glcode  ");
        // String
        // query="select glcode||'`-`'||name|| '`-`' || ID as \"code\" from chartofaccounts where classification=4 and
        // isactiveforposting = true and type = 'L' order by glcode ";

        result = new StringBuffer();
        for (CChartOfAccounts cc : coaList) {
            result.append(cc.getGlcode() + "`-`");
            result.append(cc.getName() + "`-`");
            result.append(cc.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllLiabCodes.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllFunctionName")
    public String ajaxGetAllFunctionName() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllFunctionName...");

        /*
         * LTS Migration Fix (Hibernate 6 Upgrade):
         * Changed field names from lowercase 'isactive' and 'isnotleaf' to camelCase 'isActive' and 'isNotLeaf' for CFunction HQL.
         * The CFunction Java entity uses 'isActive' and 'isNotLeaf' property names.
         */
        functionCodesList = persistenceService
                .findAllBy("select f from CFunction f where  isActive = true AND isNotLeaf=false order by name");
        // String
        // query="select code||'`-`'||name||'`~`'||id as \"code\" from function where isactive = true AND isnotleaf=false order by
        // name ";

        result = new StringBuffer();
        for (CFunction cf : functionCodesList) {
            result.append(cf.getCode() + "`-`");
            result.append(cf.getName() + "`~`");
            result.append(cf.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllFunctionName.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllBankName")
    public String ajaxGetAllBankName() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllBankName...");

        bankList = persistenceService.findAllBy("from Bank where  isactive = true order by name");
        // String query="select name||'`-`'||id as \"code\" from bank where isactive = true order by name ";
        result = new StringBuffer();
        for (Bank b : bankList) {
            result.append(b.getName() + "`-`");
            result.append(b.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllBankName.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxGetAllCoaNames")
    public String ajaxGetAllCoaNames() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxGetAllCoaNames...");

        /*
         * Hibernate 6 migration note:
         * isActiveForPosting is the mapped Java property. Keeping this property name
         * avoids strict HQL attribute-resolution failures.
         */
        coaList = persistenceService.findAllBy(
                " from CChartOfAccounts where classification=4 and isActiveForPosting = true order by glcode ");
        // final String
        // query="select name||'`-`'||glcode||'`-`'||ID as \"code\" from chartofaccounts where classification=4 and
        // isactiveforposting = true order by glcode ";

        result = new StringBuffer();
        for (CChartOfAccounts cc : coaList) {
            result.append(cc.getName() + "`-`");
            result.append(cc.getGlcode() + "`-`");
            result.append(cc.getId() + "+");
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxGetAllCoaNames.");
        return "process";
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxCoaDetailCode")
    public String ajaxCoaDetailCode() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxCoaDetailCode...");
        if (glCode == null)
            glCodesList = new ArrayList<CChartOfAccounts>();
        else {
            String codeName = "%" + glCode + "%";
            /*
             * Hibernate 6 migration note:
             * Return CChartOfAccounts entities instead of selecting only glcode because
             * the code below iterates CChartOfAccounts and reads getGlcode().
             */
            glCodesList = persistenceService
                    .findAllBy("from CChartOfAccounts ca where classification=4 and isActiveForPosting = true and glcode like ? order by glcode", codeName);
        }

        result = new StringBuffer();
        if (glCodesList != null) {
            for (CChartOfAccounts cc : glCodesList) {
                result.append(cc.getGlcode() + "+");
            }
        }
        result.append("^");
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxCoaDetailCode.");
        return "process";
    }

    @Action(value = "/voucher/common-ajaxLoadEstimateBudgetDetailsByFundId")
    public String ajaxLoadEstimateBudgetDetailsByFundId() {
        /*
         * Struts 7 migration note:
         * Estimate-budget AJAX calls can submit fund as fundId or fund. Resolve both
         * names before calling the service and ignore invalid numeric input instead of
         * failing the entire dropdown response.
         */
        if (fundId == null) {
            String fId = ServletActionContext.getRequest().getParameter("fundId");
            if (StringUtils.isBlank(fId)) {
                fId = ServletActionContext.getRequest().getParameter("fund");
            }
            if (StringUtils.isNotBlank(fId)) {
                try {
                    fundId = Long.valueOf(fId.trim());
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid fundId: " + fId);
                }
            }
        }
        List<String> deptCodeList = null;
        if (fundId != null && fundId != 0)
            deptCodeList = budgetDetailService.getDepartmentFromBudgetDetailByFundId(fundId);
        listOfDepartments = new ArrayList<Department>();
        if(deptCodeList != null && !deptCodeList.isEmpty()){
            deptCodeList.stream().forEach(bd -> {
                /*
                 * Spring 6 migration note:
                 * Department lookup is now backed by a microservice call and can return
                 * null for stale budget detail codes. Skip null values to avoid JSP
                 * rendering failures.
                 */
                Department dept = microserviceUtils.getDepartmentByCode(bd);
                if (dept != null) {
                    listOfDepartments.add(dept);
                }
            });
        }
        return "estimateBudgetDetails";
    }

    @Action(value = "/voucher/common-ajaxLoadEstimateBudgetDetailsByDepartmentId")
    public String ajaxLoadEstimateBudgetDetailsByDepartmentId() {
        /*
         * Struts 7 migration note:
         * Department is submitted by legacy pages as either departmentId or department.
         * Normalize the value before loading function budget details.
         */
        if (departmentId == null || departmentId.trim().isEmpty()) {
            departmentId = ServletActionContext.getRequest().getParameter("departmentId");
            if (StringUtils.isBlank(departmentId)) {
                departmentId = ServletActionContext.getRequest().getParameter("department");
            }
        }
        if (departmentId != null && !departmentId.equalsIgnoreCase("-1") && !departmentId.equalsIgnoreCase("0"))
            budgetDetailList = budgetDetailService.getFunctionFromBudgetDetailByDepartmentId(departmentId);
        return "estimateBudgetDetails";
    }

    @Action(value = "/voucher/common-ajaxLoadEstimateBudgetDetailsByFuncId")
    public String ajaxLoadEstimateBudgetDetailsByFuncId() {
        /*
         * Struts 7 migration note:
         * Function can arrive as functionId or function. Parse it defensively so the
         * dependent budget-detail dropdown does not fail on blank or invalid input.
         */
        if (functionId == null || functionId == 0) {
            String funcStr = ServletActionContext.getRequest().getParameter("functionId");
            if (StringUtils.isBlank(funcStr)) {
                funcStr = ServletActionContext.getRequest().getParameter("function");
            }
            if (StringUtils.isNotBlank(funcStr)) {
                try {
                    functionId = Long.valueOf(funcStr.trim());
                } catch (NumberFormatException e) {
                    LOGGER.error("Invalid functionId: " + funcStr);
                }
            }
        }
        if (functionId != null && functionId != 0)
            budgetDetailList = budgetDetailService.getBudgetDetailByFunctionId(functionId);
        return "estimateBudgetDetails";
    }

    public String getStateId() {
        return stateId;
    }

    public void setStateId(final String stateId) {
        this.stateId = stateId;
    }

    public String getScriptName() {
        return scriptName;
    }

    public void setScriptName(final String scriptName) {
        this.scriptName = scriptName;
    }

    public List<Map<String, Object>> getDesignationList() {
        return designationList;
    }

    public void setDesignationList(final List<Map<String, Object>> designationList) {
        this.designationList = designationList;
    }

    public String getStartsWith() {
        return startsWith;
    }

    public void setStartsWith(final String startsWith) {
        this.startsWith = startsWith;
    }

    public Long getRecoveryId() {
        return recoveryId;
    }

    public void setRecoveryId(final Long recoveryId) {
        this.recoveryId = recoveryId;
    }

    public void setSubSchemeId(final Integer subSchemeId) {
        this.subSchemeId = subSchemeId;
    }

    public Integer getSubSchemeId() {
        return subSchemeId;
    }

    public List<Fundsource> getFundSouceList() {
        return fundSouceList;
    }

    public void setFundSouceList(final List<Fundsource> fundSouceList) {
        this.fundSouceList = fundSouceList;
    }

    public List<CChartOfAccounts> getAccountCodesList() {
        return accountCodesList;
    }

    public void setAccountCodesList(final List<CChartOfAccounts> accountCodesList) {
        this.accountCodesList = accountCodesList;
    }

    public Integer getDepartment() {
        return department;
    }

    public void setDepartment(final Integer department) {
        this.department = department;
    }

    public void setFinancingSourceService(
            final FinancingSourceService financingSourceService) {
        this.financingSourceService = financingSourceService;
    }

    public void setDefaultDepartment(final String defaultDepartment) {
        this.defaultDepartment = defaultDepartment;
    }

    public String getDefaultDepartment() {
        return defaultDepartment;
    }

    public Long getBillVhId() {
        return billVhId;
    }

    public void setBillVhId(final Long billVhId) {
        this.billVhId = billVhId;
    }

    public List<LoanGrantBean> getProjectCodeList() {
        return projectCodeList;
    }

    public void setProjectCodeList(final List<LoanGrantBean> projectCodeList) {
        this.projectCodeList = projectCodeList;
    }

    public List<String> getProjectCodeStringList() {
        return projectCodeStringList;
    }

    public void setProjectCodeStringList(final List<String> projectCodeStringList) {
        this.projectCodeStringList = projectCodeStringList;
    }

    public List<InstrumentHeader> getInstrumentHeaderList() {
        return instrumentHeaderList;
    }

    public void setInstrumentHeaderList(final List<InstrumentHeader> instrumentHeaderList) {
        this.instrumentHeaderList = instrumentHeaderList;
    }

    public String getRtgsNumber() {
        return rtgsNumber;
    }

    public void setRtgsNumber(final String rtgsNumber) {
        this.rtgsNumber = rtgsNumber;
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBanksWithPayGenAndRTGSNotAssigned")
    public String ajaxLoadBanksWithPayGenAndRTGSNotAssigned() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBanksWithPayGenAndRTGSNotAssigned...");
        List<Object[]> bankBranch;
        final StringBuilder bankQuery = new StringBuilder();
        try {
			bankQuery.append(
					"SELECT DISTINCT CONCAT(CONCAT(bank.id,'-'), bankBranch.id) AS bankbranchid, CONCAT(CONCAT(bank.name,' '), bankBranch.branchname) AS bankbranchname")
					.append(" FROM voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, paymentheader ph, egf_instrumentvoucher iv")
					.append(" right outer join voucherheader vh1 ON vh1.id = iv.VOUCHERHEADERID")
					.append(" WHERE ph.voucherheaderid = vh.id AND vh.status = 0 AND bank.isactive = true AND bankBranch.isactive = true AND bank.id = bankBranch.bankid")
					.append(" AND bankBranch.id = bankaccount.branchid AND bankaccount.TYPE IN ('RECEIPTS_PAYMENTS', 'PAYMENTS') AND vh1.id = vh.id AND iv.VOUCHERHEADERID IS NULL")
					.append(" AND ph.type = :paymentType AND vh.name = :voucherName").append(" UNION")
					.append(" SELECT DISTINCT CONCAT(CONCAT(bank.id,'-'), bankBranch.id) AS bankbranchid, CONCAT(CONCAT(bank.name,' '), bankBranch.branchname) AS bankbranchname")
					.append(" FROM egf_instrumentvoucher iv, voucherheader vh, Bank bank, Bankbranch bankBranch, Bankaccount bankaccount, vouchermis vmis, paymentheader ph,")
					.append(" egw_status egws, (SELECT ih1.id, ih1.id_status FROM egf_instrumentheader ih1, (SELECT bankid, bankaccountid, instrumentnumber,")
					.append(" MAX(lastmodifieddate) AS lastmodifieddate FROM egf_instrumentheader GROUP BY bankid, bankaccountid, instrumentnumber) max_rec")
					.append(" WHERE max_rec.bankid = ih1.bankid AND max_rec.bankaccountid = ih1.bankaccountid AND max_rec.instrumentnumber = ih1.instrumentnumber")
					.append(" AND max_rec.lastmodifieddate = ih1.lastmodifieddate) ih WHERE ph.voucherheaderid = vh.id AND vh.id = vmis.voucherheaderid")
					.append(" AND vh.status = 0 AND ph.voucherheaderid = vh.id AND bank.isactive = true AND bankBranch.isactive = true AND bank.id = bankBranch.bankid")
					.append(" AND bankBranch.id = bankaccount.branchid AND bankaccount.TYPE IN ('RECEIPTS_PAYMENTS','PAYMENTS') AND iv.voucherheaderid = vh.id")
					.append(" AND iv.instrumentheaderid = ih.id AND ih.id_status = egws.id AND egws.description IN ('Surrendered','Surrender_For_Reassign')")
					.append(" AND ph.type = :paymentType AND vh.name = :voucherName order by 2 ");

			bankBranch = persistenceService.getSession().createNativeQuery(bankQuery.toString())
					.setParameter("paymentType", FinancialConstants.MODEOFPAYMENT_RTGS, StandardBasicTypes.STRING)
					.setParameter("voucherName", FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE, StandardBasicTypes.STRING).list();

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank list size is " + bankBranch.size());
            bankBranchList = new ArrayList<Map<String, Object>>();
            Map<String, Object> bankBrmap;
            for (final Object[] element : bankBranch) {
                bankBrmap = new HashMap<String, Object>();
                bankBrmap.put("bankBranchId", element[0].toString());
                bankBrmap.put("bankBranchName", element[1].toString());
                bankBranchList.add(bankBrmap);
            }

        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));

        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBanksWithPayGenAndRTGSNotAssigned.");
        return "bank";

    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxLoadBankAccountsWithPayGenAndRTGSNotAssigned")
    public String ajaxLoadBankAccountsWithPayGenAndRTGSNotAssigned() {
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Starting ajaxLoadBankAccountsWithPayGenAndRTGSNotAssigned...");
        try {
            accNumList = new ArrayList<Bankaccount>();
            String bankaccountFundQuery = "";
            String voucherheaderFundQuery = "";
            if (fundId != null && fundId != 0 && fundId != -1) {
                bankaccountFundQuery = " and bankaccount.fundid =:fundId";
                voucherheaderFundQuery = " AND VH.FUNDID =:fundId";
            }
            // query to fetch vouchers for which RTGS not assigned
            StringBuilder queryString = new StringBuilder("SELECT bankaccount.accountnumber AS accountnumber, bankaccount.accounttype AS accounttype,")
                    .append(" CAST(bankaccount.id AS INTEGER) AS id, coa.glcode AS glCode")
                    .append(" FROM chartofaccounts coa, bankaccount bankaccount")
                    .append(" WHERE bankaccount.ID IN (SELECT DISTINCT PH.bankaccountnumberid")
                    .append(" FROM paymentheader ph, voucherheader vh left OUTER JOIN egf_instrumentvoucher iv ON vh.id = iv.VOUCHERHEADERID")
                    .append(" WHERE ph.voucherheaderid = vh.id AND vh.status = 0 ").append(voucherheaderFundQuery).append(" AND ph.voucherheaderid = vh.id")
                    .append(" AND iv.VOUCHERHEADERID IS NULL AND vh.name = :voucherName AND ph.type = :paymentType AND coa.id = bankaccount.glcodeid")
                    .append(" AND bankaccount.type IN ('RECEIPTS_PAYMENTS', 'PAYMENTS'))").append(bankaccountFundQuery)
                    .append(" AND bankaccount.branchid = :branchId and bankaccount.isactive = true ");
            
            // query to fetch vouchers for which cheque has been assigned and surrendered
			queryString.append(
					" union select bankaccount.accountnumber as accountnumber, bankaccount.accounttype as accounttype, cast(bankaccount.id as integer) as id,")
					.append(" coa.glcode as glCode from chartofaccounts coa, Bankaccount bankaccount")
					.append(" where bankaccount.id in (SELECT DISTINCT PH.bankaccountnumberid from egf_instrumentvoucher iv, voucherheader vh, paymentheader ph, egw_status egws,")
					.append(" (select ih1.id, ih1.id_status from egf_instrumentheader ih1, (select bankid, bankaccountid, instrumentnumber, max(id) as id")
					.append(" from egf_instrumentheader group by bankid, bankaccountid, instrumentnumber) max_rec")
					.append(" where max_rec.bankid = ih1.bankid and max_rec.bankaccountid = ih1.bankaccountid and max_rec.instrumentnumber = ih1.instrumentnumber ")
					.append(" and max_rec.id = ih1.id) ih where ph.voucherheaderid = vh.id")
					.append(voucherheaderFundQuery)
					.append(" and vh.status = 0 and ph.voucherheaderid = vh.id and iv.voucherheaderid = vh.id and iv.instrumentheaderid = ih.id and ph.bankaccountnumberid = bankaccount.id")
					.append(" and vh.type = :voucherType AND vh.name = :voucherName AND ph.type = :paymentType and ih.id_status = egws.id")
					.append(" and egws.description in ('Surrendered', 'Surrender_For_Reassign')) and coa.id = bankaccount.glcodeid and bankaccount.type in ('RECEIPTS_PAYMENTS', 'PAYMENTS')")
					.append(" and bankaccount.branchid = :branchId");

			queryString.append(bankaccountFundQuery);

			final List<Object[]> bankAccounts = persistenceService.getSession().createNativeQuery(queryString.toString())
					.setParameter("fundId", fundId.longValue(), StandardBasicTypes.LONG)
					.setParameter("voucherName", FinancialConstants.PAYMENTVOUCHER_NAME_REMITTANCE, StandardBasicTypes.STRING)
					.setParameter("paymentType", FinancialConstants.MODEOFPAYMENT_RTGS, StandardBasicTypes.STRING)
					.setParameter("branchId", branchId, StandardBasicTypes.INTEGER)
					.setParameter("voucherType", FinancialConstants.STANDARD_VOUCHER_TYPE_PAYMENT, StandardBasicTypes.STRING)
					.list();

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Bank accont list size is " + bankAccounts.size() + "and Query is " + queryString.toString());
            final List<String> addedBanks = new ArrayList<String>();
            for (final Object[] account : bankAccounts) {
                final String accountNumberAndType = account[0].toString() + "-" + account[1].toString();
                if (!addedBanks.contains(accountNumberAndType)) {
                    final Bankaccount bankaccount = new Bankaccount();
                    bankaccount.setAccountnumber(account[0].toString());
                    bankaccount.setAccounttype(account[1].toString());
                    final CChartOfAccounts chartofaccounts = new CChartOfAccounts();
                    chartofaccounts.setGlcode(account[3].toString());
                    bankaccount.setChartofaccounts(chartofaccounts);
                    bankaccount.setId(Long.valueOf(account[2].toString()));
                    addedBanks.add(accountNumberAndType);
                    accNumList.add(bankaccount);
                }
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting the data for bank dropdown " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) { LOGGER.
           * error("Exception occured while getting the data for bank dropdown "
           * + e.getMessage(), new Exception(e.getMessage())); }
           */
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("Completed ajaxLoadBankAccountsWithPayGenAndRTGSNotAssigned.");
        return "bankAccNum";
    }

    /*
     * Autocomplete for ARF Nos where Advance Payment not created and ARF type='Contractor'
     */
    @Action(value = "/voucher/common-searchARFNumbers")
    public String searchARFNumbers() {
        final ArrayList<Object> params = new ArrayList<Object>();
        if (!StringUtils.isEmpty(query)) {
        	StringBuilder strquery = new StringBuilder("select distinct(arf.advanceRequisitionNumber) from EgAdvanceRequisition arf where arf.status.code = ? ")
                    .append(" and arf.arftype = ? and NOT EXISTS (select 1 from CVoucherHeader vh where vh.id = arf.egAdvanceReqMises.voucherheader.id")
                    .append(" and arf.egAdvanceReqMises.voucherheader.status <> 4) and UPPER(arf.advanceRequisitionNumber) like '%'||?||'%' order by arf.advanceRequisitionNumber");
            params.add(ARF_STATUS_APPROVED);
            params.add(ARF_TYPE);
            params.add(query.toUpperCase());

            arfNumberSearchList = persistenceService.findAllBy(strquery.toString(), params.toArray());
        }
        return ARF_NUMBER_SEARCH_RESULTS;
    }

    @SuppressWarnings("unchecked")
    @Action(value = "/voucher/common-ajaxYearCode")
    public String ajaxYearCode() {
        try {
            if (bankaccount != null && departmentId != null) {
                yearCodeList = persistenceService
                        .findAllBy(new StringBuilder("select DISTINCT fs from AccountCheques ac, CFinancialYear fs, ChequeDeptMapping cd where ac.serialNo = fs.id")
                                .append(" and ac.bankAccountId.id = ? and ac.id = cd.accountCheque.id and cd.allotedTo =? order by fs.id desc ").toString(),
                                bankaccount.longValue(), departmentId.toString());
            }
        } catch (final HibernateException e) {
            LOGGER.error("Exception occured while getting year code " + e.getMessage(),
                    new HibernateException(e.getMessage()));
        } /*
           * catch (final Exception e) {
           * LOGGER.error("Exception occured while getting year code " +
           * e.getMessage(), new HibernateException(e.getMessage())); }
           */
        return "yearCode";
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(final String query) {
        this.query = query;
    }

    public List<String> getArfNumberSearchList() {
        return arfNumberSearchList;
    }

    public String getBillSubType() {
        return billSubType;
    }

    public void setBillSubType(final String billSubType) {
        this.billSubType = billSubType;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(final String glCode) {
        this.glCode = glCode;
    }

    public List<CChartOfAccounts> getGlCodesList() {
        return glCodesList;
    }

    public void setGlCodesList(final List<CChartOfAccounts> glCodesList) {
        this.glCodesList = glCodesList;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(final String function) {
        this.function = function;
    }

    public List<CFunction> getFunctionCodesList() {
        return functionCodesList;
    }

    public void setFunctionCodesList(final List<CFunction> functionCodesList) {
        this.functionCodesList = functionCodesList;
    }

    public List<Accountdetailtype> getSubLedgerTypeList() {
        return subLedgerTypeList;
    }

    public void setSubLedgerTypeList(final List<Accountdetailtype> subLedgerTypeList) {
        this.subLedgerTypeList = subLedgerTypeList;
    }

    public AppConfigValueService getAppConfigValuesService() {
        return appConfigValuesService;
    }

    public void setAppConfigValuesService(final AppConfigValueService appConfigValuesService) {
        this.appConfigValuesService = appConfigValuesService;
    }

    public List<CChartOfAccounts> getCoaList() {
        return coaList;
    }

    public void setCoaList(List<CChartOfAccounts> coaList) {
        this.coaList = coaList;
    }

    public StringBuffer getResult() {
        return result;
    }

    public void setResult(StringBuffer result) {
        this.result = result;
    }

    public Long getVouchHeaderId() {
        return vouchHeaderId;
    }

    public void setVouchHeaderId(Long vouchHeaderId) {
        this.vouchHeaderId = vouchHeaderId;
    }

    public String getGlcodeParam() {
        return glcodeParam;
    }

    public void setGlcodeParam(String glcodeParam) {
        this.glcodeParam = glcodeParam;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getFunctionName() {
        return functionName;
    }

    public void setFunctionName(String functionName) {
        this.functionName = functionName;
    }

    public Integer getBankaccount() {
        return bankaccount;
    }

    public void setBankaccount(Integer bankaccount) {
        this.bankaccount = bankaccount;
    }

    public List<CFinancialYear> getYearCodeList() {
        return yearCodeList;
    }

    public void setYearCodeList(List<CFinancialYear> yearCodeList) {
        this.yearCodeList = yearCodeList;
    }

    public Long getFunctionId() {
        return functionId;
    }

    public void setFunctionId(Long functionId) {
        this.functionId = functionId;
    }

    public List<BudgetDetail> getBudgetDetailList() {
        return budgetDetailList;
    }

    public void setBudgetDetailList(List<BudgetDetail> budgetDetailList) {
        this.budgetDetailList = budgetDetailList;
    }

    public ArrayList<Department> getListOfDepartments() {
        return listOfDepartments;
    }

    public void setListOfDepartments(ArrayList<Department> listOfDepartments) {
        this.listOfDepartments = listOfDepartments;
    }
}

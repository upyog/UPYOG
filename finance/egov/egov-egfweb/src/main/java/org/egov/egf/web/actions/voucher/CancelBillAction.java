/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
package org.egov.egf.web.actions.voucher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.EgwStatus;
import org.egov.commons.Fund;
import org.egov.egf.dashboard.event.FinanceEventType;
import org.egov.egf.dashboard.event.listener.FinanceDashboardService;
import org.egov.infra.admin.master.entity.Department;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.model.bills.EgBillregister;
import org.egov.services.bills.BillsService;
import org.egov.utils.Constants;
import org.egov.utils.FinancialConstants;
import org.hibernate.query.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.exilant.eGov.src.domain.BillRegisterBean;

/**
 * LTS Migration Notes:
 * 1. [Struts 7 & Jakarta EE] Nested form parameter resolution: Struts 7 restricts automatic OGNL nested
 *    parameter binding ('fund.id', 'deptImpl.code'). Added resolveSearchCriteriaFromRequest() to extract
 *    parameters from HttpServletRequest.
 * 2. [Struts 7 Batch Checkbox Binding] Struts 7 restricts dynamic indexed list binding ('billListDisplay[i].isSelected').
 *    Added bindSelectedBillsFromRequest() to process 'selectedBillIds' array posted from cancelBill-search.jsp.
 * 3. [Java 17 Null Safety] Reordered validateFund() to check fund.getId() == null before unboxing comparisons
 *    (fund.getId() == -1), preventing NullPointerExceptions in Java 17.
 * 4. [Hibernate 6] Migrated bill status updates to billsService.updateBillStatus() and fixed association null comparisons.
 */
@Results({ @Result(name = "search", location = "cancelBill-search.jsp") })
public class CancelBillAction extends BaseFormAction {
	private static final long serialVersionUID = 1L;
	private static final String CANCEL_QUERY_STR = " billstatus=:billStatus, statusid=:statusId ";
    private static final String STATUS_QUERY_STR = "moduletype=:moduleType and description=:description";
    private static final String BILL_STATUS = "billStatus";
    private static final String DESCRIPTION = "description";
    private static final String MODULE_TYPE = "moduleType";
	private static final Logger LOGGER = Logger.getLogger(CancelBillAction.class);
	@Autowired
	private BillsService billsService;
	private String billNumber;
	private String fromDate;
	private String toDate;
	private Fund fund = new Fund();
	private Department deptImpl = new Department();
	private String expType;
	private List<BillRegisterBean> billListDisplay = new ArrayList<BillRegisterBean>();
	private String[] selectedBillIds;
	private boolean afterSearch = false;
	Integer loggedInUser = ApplicationThreadLocals.getUserId().intValue();
	public final SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy", Constants.LOCALE);

	@Autowired
	@Qualifier("persistenceService")
	private PersistenceService persistenceService;
	@Autowired
	private EgovMasterDataCaching masterDataCache;
	@Autowired
	private CancelBillAndVoucher cancelBillAndVoucher;
	
	@Autowired
	FinanceDashboardService finDashboardService;

	@Override
	public Object getModel() {

		return null;
	}

	public void setBillNumber(final String billNumber) {
		this.billNumber = billNumber;
	}

	public String getBillNumber() {
		return billNumber;
	}

	public void setFromDate(final String fromBillDate) {
		fromDate = fromBillDate;
	}

	public String getFromDate() {
		return fromDate;
	}

	public void setToDate(final String toBillDate) {
		toDate = toBillDate;
	}

	public String getToDate() {
		return toDate;
	}

	public void setFund(final Fund fund) {
		this.fund = fund;
	}

	public Fund getFund() {
		return fund;
	}

	public void setExpType(final String expType) {
		this.expType = expType;
	}

	public String getExpType() {
		return expType;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void prepare() {
		super.prepare();
		if (LOGGER.isDebugEnabled())
			LOGGER.debug("Inside Prepare method");
		List<org.egov.infra.microservice.models.Department> departments = masterDataCache.get("egi-department");
		dropdownData.put("DepartmentList", departments);
		addDropdownData("fundList",
				persistenceService.findAllBy("from Fund where isactive=true and isnotleaf=false order by name"));
		// Important - Remove the like part of the query below to generalize the
		// LTS Migration Fix: Updated unlabelled '?' to numbered positional parameters '?1', '?2', '?3' for Hibernate 6 HQL compliance
		addDropdownData("expenditureList", persistenceService.findAllBy(
				"select distinct bill.expendituretype from EgBillregister bill where bill.expendituretype=?1 or bill.expendituretype=?2 or bill.expendituretype=?3 order by bill.expendituretype", 
				FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT, FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS, FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE));
	}

	public void prepareBeforeSearch() {
		fund.setId(null);
		billNumber = "";
		fromDate = "";
		toDate = "";
		expType = "";
		billListDisplay.clear();
	}

	@SkipValidation
	@Action(value = "/voucher/cancelBill-beforeSearch")
	public String beforeSearch() {
		return "search";
	}

	public Map<String, Map<String, Object>> filterQuery() {
		
		final Map<String, Map<String, Object>> queryMap = new HashMap<>();
        final Map<String, Object> params = new HashMap<>();
        final String userCond = " where ";
		final StringBuilder query = new StringBuilder(
				" select billmis.egBillregister.id, billmis.egBillregister.billnumber, billmis.egBillregister.billdate,")
						.append(" billmis.egBillregister.billamount, billmis.departmentcode ")
						.append("  from EgBillregistermis billmis ");
        query.append(userCond);
        
        if (fund != null && fund.getId() != null && fund.getId() != -1
                && fund.getId() != 0) {
            query.append(" billmis.fund.id=:fundId");
            params.put("fundId", fund.getId());
        }

        if (billNumber != null && billNumber.length() != 0) {
            query.append(" and billmis.egBillregister.billnumber =:billNumber");
            params.put("billNumber", billNumber);
        }
        // Struts 7 posts blank department as "" instead of null
        if (deptImpl != null && deptImpl.getCode() != null && deptImpl.getCode().length() != 0
                && !deptImpl.getCode().equals("-1")) {
            query.append(" and billmis.departmentcode =:deptCode");
            params.put("deptCode", deptImpl.getCode());
        }
        if (fromDate != null && fromDate.length() != 0) {
            Date fDate;
            try {
                fDate = formatter.parse(fromDate);
                query.append(" and billmis.egBillregister.billdate >= :fromDate");
                params.put("fromDate", fDate);
            } catch (final ParseException e) {
                LOGGER.error(" From Date parse error");
            }
        }
        if (toDate != null && toDate.length() != 0) {
            Date tDate;
            try {
                tDate = formatter.parse(toDate);
                query.append(" and billmis.egBillregister.billdate <= :toDate");
                params.put("toDate", tDate);
            } catch (final ParseException e) {
                LOGGER.error(" To Date parse error");
            }
        }
        
		if (expType == null || expType.equalsIgnoreCase("")) {
            query.append(" and billmis.egBillregister.status.description=:description");
            params.put("description", FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS);
		} else {
            query.append(" and billmis.egBillregister.expendituretype =:expenditureType");
            params.put("expenditureType", expType);
            if (FinancialConstants.STANDARD_EXPENDITURETYPE_SALARY
                    .equalsIgnoreCase(expType)) {
                query.append(" and billmis.egBillregister.status.moduletype=:moduleType");
                params.put("moduleType", FinancialConstants.SALARYBILL);
                query.append(" and billmis.egBillregister.status.description=:description");
                params.put("description", FinancialConstants.SALARYBILL_APPROVED_STATUS);
            } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT
                    .equalsIgnoreCase(expType)) {
                query.append(" and billmis.egBillregister.status.moduletype=:moduleType");
                params.put("moduleType", FinancialConstants.CONTINGENCYBILL_FIN);
                query.append(" and billmis.egBillregister.status.description=:description");
                params.put("description", FinancialConstants.CONTINGENCYBILL_APPROVED_STATUS);
            } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE
                    .equalsIgnoreCase(expType)) {
                query.append(" and billmis.egBillregister.status.moduletype=:moduleType");
                params.put("moduleType", FinancialConstants.SBILL);
                query.append(" and billmis.egBillregister.status.description=:description");
                params.put("description", FinancialConstants.SUPPLIERBILL_APPROVED_STATUS);
            } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS
                    .equalsIgnoreCase(expType)) {
                query.append(" and billmis.egBillregister.status.moduletype=:moduleType");
                params.put("moduleType", FinancialConstants.CONTRACTORBILL);
                query.append(" and billmis.egBillregister.status.code=:code");
                params.put("code", FinancialConstants.CONTRACTORBILL_APPROVED_STATUS);
            }
		}
		queryMap.put(query.toString(), params);
        return queryMap;
	}

	public Map<String, Map<String, Object>> query() {
        final Map<String, Map<String, Object>> queries = new HashMap<>();
        final Map.Entry<String, Map<String, Object>> mapQueryEntry = filterQuery().entrySet().iterator().next();
        String query = mapQueryEntry.getKey() + " and billmis.voucherHeader.id is null ";
        queries.put(query, mapQueryEntry.getValue());
        final Map<String, Object> params = new HashMap<>();
        params.putAll(mapQueryEntry.getValue());
        query = mapQueryEntry.getKey() + " and billmis.voucherHeader.status in (:vhStatus)";
        params.put("vhStatus", Arrays.asList(FinancialConstants.REVERSEDVOUCHERSTATUS, FinancialConstants.CANCELLEDVOUCHERSTATUS));
        queries.put(query, params);
        return queries;
    }
	
	public void prepareSearch() {
		billListDisplay.clear();
	}

	/**
	 * LTS Migration Fix [Struts 7 Parameter Extraction]:
	 * In Struts 7, form fields ('fund.id', 'deptImpl.code', 'billNumber', etc.) are not automatically
	 * injected into nested bean properties. This method reads them directly from HttpServletRequest.
	 */
	private void resolveSearchCriteriaFromRequest() {
		final HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null) {
			return;
		}

		if (fund == null) {
			fund = new Fund();
		}
		if (fund.getId() == null) {
			final String fundId = firstNonEmpty(request.getParameter("fund.id"), request.getParameter("fund"));
			if (StringUtils.isNotBlank(fundId) && !"-1".equals(fundId) && !"0".equals(fundId)) {
				fund.setId(Long.valueOf(fundId.trim()));
			}
		}

		if (deptImpl == null) {
			deptImpl = new Department();
		}
		if (StringUtils.isBlank(deptImpl.getCode())) {
			final String deptCode = firstNonEmpty(request.getParameter("deptImpl.code"),
					request.getParameter("department.code"), request.getParameter("department"));
			if (StringUtils.isNotBlank(deptCode) && !"-1".equals(deptCode) && !"0".equals(deptCode)) {
				deptImpl.setCode(deptCode.trim());
			}
		}

		if (StringUtils.isBlank(billNumber)) {
			billNumber = request.getParameter("billNumber");
		}
		if (StringUtils.isBlank(fromDate)) {
			fromDate = request.getParameter("fromDate");
		}
		if (StringUtils.isBlank(toDate)) {
			toDate = request.getParameter("toDate");
		}
		if (StringUtils.isBlank(expType)) {
			expType = request.getParameter("expType");
		}
	}

	private static String firstNonEmpty(final String... values) {
		if (values == null) {
			return null;
		}
		for (final String value : values) {
			if (StringUtils.isNotBlank(value)) {
				return value.trim();
			}
		}
		return null;
	}

    public void validateFund() throws ParseException {
        // Check getId() == null before unboxing comparisons (Struts 7 leaves fund.id unbound)
        if (fund == null || fund.getId() == null || fund.getId() == -1 || fund.getId() == 0)
            addFieldError("fund.id", getText("voucher.fund.mandatory"));
        if (StringUtils.isNotEmpty(fromDate) || StringUtils.isNotEmpty(toDate)) {
            boolean isDateFrom = false;
            boolean isDateTo = false;
            String fromDates = fromDate;
            String toDates = toDate;
            String datePattern = "\\d{1,2}/\\d{1,2}/\\d{4}";
            isDateFrom = fromDates != null && fromDates.matches(datePattern);
            isDateTo = toDates != null && toDates.matches(datePattern);
            if (!isDateFrom || !isDateTo) {
                addActionError(getText("msg.please.select.valid.date"));
            }
        }
        Date datefrom = null;
        Date dateto = null;
        if (StringUtils.isNotEmpty(fromDate) && StringUtils.isNotEmpty(toDate)) {
            datefrom = formatter.parse(fromDate);
            dateto = formatter.parse(toDate);
            if (datefrom.after(dateto)) {
                addFieldError("toDate", getText("msg.fromDate.cant.be.greater.than.toDate"));
            }
        }
    }

	@ValidationErrorPage(value = "search")
	@Action(value = "/voucher/cancelBill-search")
	public String search() throws ParseException {
		resolveSearchCriteriaFromRequest();
        validateFund();
        if (!hasErrors()) {
            billListDisplay.clear();
            final Map<String, Map<String, Object>> queries = query();
            final List<String> list = queries.keySet().stream().collect(Collectors.toList());
            final List<Object[]> tempBillList = new ArrayList<Object[]>();
            List<Object[]> billListWithNoVouchers, billListWithCancelledReversedVouchers;
            final Query queryOne = persistenceService.getSession().createQuery(list.get(0));
            persistenceService.populateQueryWithParams(queryOne, queries.get(list.get(0)));
            billListWithNoVouchers = queryOne.list();
            final Query queryTwo = persistenceService.getSession().createQuery(list.get(1));
            persistenceService.populateQueryWithParams(queryTwo, queries.get(list.get(1)));
            billListWithCancelledReversedVouchers = queryTwo.list();

            tempBillList.addAll(billListWithNoVouchers);
			tempBillList.addAll(billListWithCancelledReversedVouchers);

			BillRegisterBean billRegstrBean;
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Size of tempBillList - " + tempBillList.size());
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			List<org.egov.infra.microservice.models.Department> departments = masterDataCache.get("egi-department");
			Map<String, String> depMap = new HashMap<>();
			for (org.egov.infra.microservice.models.Department department : departments) {
				depMap.put(department.getCode(), department.getName());
			}
			for (final Object[] bill : tempBillList) {
				billRegstrBean = new BillRegisterBean();
				billRegstrBean.setId(bill[0].toString());
				billRegstrBean.setBillNumber(bill[1].toString());
				if (!bill[2].toString().equalsIgnoreCase(""))
					billRegstrBean.setBillDate(sdf.format(bill[2]));
				billRegstrBean.setBillAmount(Double.parseDouble(bill[3].toString()));
				billRegstrBean.setBillDeptName(depMap.get(bill[4]));
				billListDisplay.add(billRegstrBean);
			}
			afterSearch = true;
		}
		return "search";
	}

	@Action(value = "/voucher/cancelBill-cancelBill")
	public String cancelBill() {
		bindSelectedBillsFromRequest();
		resolveSearchCriteriaFromRequest();
        final Map<String, Object> map = cancelBills(billListDisplay, expType);
        ((List<String>) map.get("billNumbers")).forEach(rec -> addActionError(getText("msg.bill.cancel.creator", new String[] {rec})));
        if (!((List<Long>) map.get("ids")).isEmpty())
            addActionMessage(getText("Cancelled Successfully"));

        prepareBeforeSearch();
        return "search";
    }

	/**
	 * LTS Migration Fix [Struts 7 Batch Checkbox Binding]:
	 * Struts 7 does not bind dynamic indexed list checkboxes ('billListDisplay[i].isSelected').
	 * Selected bill ids are posted as an array ('selectedBillIds') from cancelBill-search.jsp.
	 * This method populates billListDisplay with isSelected=true for the matching bill IDs.
	 */
	private void bindSelectedBillsFromRequest() {
		final HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null) {
			return;
		}
		String[] ids = request.getParameterValues("selectedBillIds");
		if (ids == null || ids.length == 0) {
			ids = selectedBillIds;
		}
		if (ids == null || ids.length == 0) {
			final List<String> collected = new ArrayList<>();
			for (int i = 0; i < 500; i++) {
				final String selected = request.getParameter("billListDisplay[" + i + "].isSelected");
				final String id = request.getParameter("billListDisplay[" + i + "].id");
				if (id == null && selected == null) {
					if (i > 0) {
						break;
					}
					continue;
				}
				if (id != null && ("true".equalsIgnoreCase(selected) || "on".equalsIgnoreCase(selected)
						|| "true,false".equalsIgnoreCase(selected))) {
					collected.add(id);
				}
			}
			if (!collected.isEmpty()) {
				ids = collected.toArray(new String[0]);
			}
		}
		if (ids == null || ids.length == 0) {
			return;
		}
		selectedBillIds = ids;
		if (billListDisplay == null) {
			billListDisplay = new ArrayList<>();
		}
		if (billListDisplay.isEmpty()) {
			for (final String id : ids) {
				if (StringUtils.isBlank(id)) {
					continue;
				}
				final BillRegisterBean bean = new BillRegisterBean();
				bean.setId(id.trim());
				bean.setIsSelected(true);
				billListDisplay.add(bean);
			}
		} else {
			final java.util.Set<String> selected = new java.util.HashSet<>();
			for (final String id : ids) {
				if (StringUtils.isNotBlank(id)) {
					selected.add(id.trim());
				}
			}
			for (final BillRegisterBean bean : billListDisplay) {
				bean.setIsSelected(bean.getId() != null && selected.contains(bean.getId()));
			}
		}
	}
	
	public Map<String, Object> cancelBills(final List<BillRegisterBean> billListDisplay, final String expType) {
        final Map<String, Object> map = new HashMap<>();
        EgBillregister billRegister;
        final Long[] idList = new Long[billListDisplay.size()];
        int i = 0;
        int idListLength = 0;
        final List<Long> ids = new ArrayList<>();
        final List<String> billNumbers = new ArrayList<>();
        final Map<String, Object> statusQueryMap = new HashMap<>();
        final Map<String, Object> cancelQueryMap = new HashMap<>();
        final StringBuilder statusQuery = new StringBuilder(
                "from EgwStatus where ");
        final StringBuilder cancelQuery = new StringBuilder(
                "Update eg_billregister set ");
        for (final BillRegisterBean billRgistrBean : billListDisplay)
            if (billRgistrBean.getIsSelected()) {
                idList[i++] = Long.parseLong(billRgistrBean.getId());
                idListLength++;
            }
        if (expType == null || expType.equalsIgnoreCase("") || FinancialConstants.STANDARD_EXPENDITURETYPE_CONTINGENT.equalsIgnoreCase(expType)) {
            statusQuery.append(STATUS_QUERY_STR);
            statusQueryMap.put(MODULE_TYPE, FinancialConstants.CONTINGENCYBILL_FIN);
            statusQueryMap.put(DESCRIPTION, FinancialConstants.CONTINGENCYBILL_CANCELLED_STATUS);
            cancelQuery.append(CANCEL_QUERY_STR);
            cancelQueryMap.put(BILL_STATUS, FinancialConstants.CONTINGENCYBILL_CANCELLED_STATUS);
        } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_SALARY
                .equalsIgnoreCase(expType)) {
            statusQuery.append(STATUS_QUERY_STR);
            statusQueryMap.put(MODULE_TYPE, FinancialConstants.SALARYBILL);
            statusQueryMap.put(DESCRIPTION, FinancialConstants.SALARYBILL_CANCELLED_STATUS);
            cancelQuery.append(CANCEL_QUERY_STR);
            cancelQueryMap.put(BILL_STATUS, FinancialConstants.SALARYBILL_CANCELLED_STATUS);
        } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_PURCHASE.equalsIgnoreCase(expType)) {
            statusQuery.append(STATUS_QUERY_STR);
            statusQueryMap.put(MODULE_TYPE, FinancialConstants.SUPPLIERBILL);
            statusQueryMap.put(DESCRIPTION, FinancialConstants.SUPPLIERBILL_CANCELLED_STATUS);
            cancelQuery.append(CANCEL_QUERY_STR);
            cancelQueryMap.put(BILL_STATUS, FinancialConstants.SUPPLIERBILL_CANCELLED_STATUS);
        } else if (FinancialConstants.STANDARD_EXPENDITURETYPE_WORKS.equalsIgnoreCase(expType)) {
            statusQuery.append(STATUS_QUERY_STR);
            statusQueryMap.put(MODULE_TYPE, FinancialConstants.CONTRACTORBILL);
            statusQueryMap.put(DESCRIPTION, FinancialConstants.CONTRACTORBILL_CANCELLED_STATUS);
            cancelQuery.append(CANCEL_QUERY_STR);
            cancelQueryMap.put(BILL_STATUS, FinancialConstants.CONTRACTORBILL_CANCELLED_STATUS);
        }
        if (LOGGER.isDebugEnabled())
            LOGGER.debug(" Status Query - " + statusQuery.toString());
        final Query query = persistenceService.getSession().createQuery(statusQuery.toString());
        statusQueryMap.entrySet().forEach(entry -> query.setParameter(entry.getKey(), entry.getValue()));
        final EgwStatus status = (EgwStatus) query.uniqueResult();
        if (idListLength != 0) {
            for (i = 0; i < idListLength; i++) {
                billRegister = billsService.getBillRegisterById(idList[i].intValue());
                final boolean value = cancelBillAndVoucher.canCancelBill(billRegister);
                if (!value) {
                    billNumbers.add(billRegister.getBillnumber());
                    continue;
                }
                ids.add(idList[i]);
            }
            cancelQuery.append(" where id in (:ids)");
            if (LOGGER.isDebugEnabled())
                LOGGER.debug(" Cancel Query - " + cancelQuery.toString());
            if (!ids.isEmpty())
                billsService.updateBillStatus(ids, Long.valueOf(status.getId()),
                        (String) cancelQueryMap.get(BILL_STATUS));
        }
        map.put("ids", ids);
        map.put("billNumbers", billNumbers);
        return map;
    }

	public void setBillListDisplay(final List<BillRegisterBean> billListDisplay) {
		this.billListDisplay = billListDisplay;
	}

	public List<BillRegisterBean> getBillListDisplay() {
		return billListDisplay;
	}

	public void setAfterSearch(final boolean afterSearch) {
		this.afterSearch = afterSearch;
	}

	public boolean getAfterSearch() {
		return afterSearch;
	}

	public String[] getSelectedBillIds() {
		return selectedBillIds;
	}

	public void setSelectedBillIds(final String[] selectedBillIds) {
		this.selectedBillIds = selectedBillIds;
	}

	public Department getDeptImpl() {
		return deptImpl;
	}

	public void setDeptImpl(final Department deptImpl) {
		this.deptImpl = deptImpl;
	}

	public Integer getLoggedInUser() {
		return loggedInUser;
	}

	public void setLoggedInUser(final Integer loggedInUser) {
		this.loggedInUser = loggedInUser;
	}
}
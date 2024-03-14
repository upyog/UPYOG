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
package org.egov.egf.web.actions.report;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperPrint;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.egov.commons.CChartOfAccounts;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Functionary;
import org.egov.commons.Fund;
import org.egov.egf.model.Statement;
import org.egov.infra.admin.master.entity.Boundary;
import org.egov.infra.admin.master.service.CityService;
import org.egov.infra.config.persistence.datasource.routing.annotation.ReadOnly;
import org.egov.infra.microservice.models.Department;
import org.egov.infra.reporting.util.ReportUtil;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infstr.services.PersistenceService;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.report.CashFlowService;
import org.egov.utils.Constants;
import org.egov.utils.ReportHelper;
import org.hibernate.FlushMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@ParentPackage("egov")
@Results({
    @Result(name = "report", location = "cashFlowReport-report.jsp"),
    @Result(name = "PDF", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/pdf", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=CashFlowStatement.pdf" }),
    @Result(name = "XLS", type = "stream", location = Constants.INPUT_STREAM, params = { Constants.INPUT_NAME,
            Constants.INPUT_STREAM, Constants.CONTENT_TYPE, "application/xls", Constants.CONTENT_DISPOSITION,
    "no-cache;filename=CashFlowStatement.xls" })
})
public class CashFlowReportAction extends BaseFormAction {
    private static final long serialVersionUID = 91711010096900620L;
    private static final String CASH_FLOW_PDF = "PDF";
    private static final String CASH_FLOW_XLS = "XLS";
    private static SimpleDateFormat FORMATDDMMYYYY = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
    InputStream inputStream;
    ReportHelper reportHelper;
    Statement cashFlowStatement = new Statement();
    CashFlowService cashFlowService;
    private String financialYearId;
    private Date todayDate;
    private final StringBuffer heading = new StringBuffer();
    List<CChartOfAccounts> listChartOfAccounts;

    @Autowired
    @Qualifier("persistenceService")
    private PersistenceService persistenceService;
    
    @Autowired
    private EgovMasterDataCaching masterDataCache;
    
    @Autowired
    private CityService cityService;

    public void setCashFlowService(final CashFlowService cashFlowService) {
        this.cashFlowService = cashFlowService;
    }

    public void setReportHelper(final ReportHelper reportHelper) {
        this.reportHelper = reportHelper;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

    public Statement getCashFlowStatement() {
        return cashFlowStatement;
    }

    public CashFlowReportAction() {
        addRelatedEntity("financialYear", CFinancialYear.class);
        addRelatedEntity("fund", Fund.class);
    }

    @Override
    public void prepare() {
        persistenceService.getSession().setDefaultReadOnly(true);
        persistenceService.getSession().setFlushMode(FlushMode.MANUAL);
        super.prepare();
        if (!parameters.containsKey("showDropDown")) {
            addDropdownData("financialYearList",
                    getPersistenceService().findAllBy("from CFinancialYear where isActive=true  order by finYearRange desc "));
        }
    }

    protected void setRelatedEntitesOn() {
        setTodayDate(new Date());
        if (cashFlowStatement.getFinancialYear() != null
                && cashFlowStatement.getFinancialYear().getId() != null
                && cashFlowStatement.getFinancialYear().getId() != 0) {
            cashFlowStatement.setFinancialYear((CFinancialYear) getPersistenceService().find(
                    "from CFinancialYear where id=?", cashFlowStatement.getFinancialYear().getId()));
            heading.append(" for the Financial Year " + cashFlowStatement.getFinancialYear().getFinYearRange());
        }
    }

    private void setTodayDate(Date date) {
		// TODO Auto-generated method stub
		
	}

	public void setCashFlowStatement(final Statement cashFlowStatement) {
        this.cashFlowStatement = cashFlowStatement;
    }

    @Override
    public Object getModel() {
        return cashFlowStatement;
    }

    @Action(value = "/report/cashFlowReport-generateCashFlowReport")
    public String generateCashFlowReport() {
        return "report";
    }

   

}

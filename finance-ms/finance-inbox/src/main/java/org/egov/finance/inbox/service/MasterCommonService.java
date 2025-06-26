/**
 * Created on Jun 18, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.egov.finance.inbox.entity.AppConfigValues;
import org.egov.finance.inbox.entity.BudgetControlType;
import org.egov.finance.inbox.entity.CChartOfAccounts;
import org.egov.finance.inbox.entity.CVoucherHeader;
import org.egov.finance.inbox.entity.Department;
import org.egov.finance.inbox.entity.EgBillregister;
import org.egov.finance.inbox.entity.EgBillregistermis;
import org.egov.finance.inbox.entity.FinancialYear;
import org.egov.finance.inbox.entity.Function;
import org.egov.finance.inbox.entity.Fund;
import org.egov.finance.inbox.repository.AppConfigValuesRepository;
import org.egov.finance.inbox.repository.BudgetControlTypeRepository;
import org.egov.finance.inbox.repository.CVoucherHeaderRepository;
import org.egov.finance.inbox.repository.ChartOfAccountsRepository;
import org.egov.finance.inbox.repository.DepartmentRepository;
import org.egov.finance.inbox.repository.EgBillRegisterRepository;
import org.egov.finance.inbox.repository.EgBillregistermisRepository;
import org.egov.finance.inbox.repository.FinancialYearRepository;
import org.egov.finance.inbox.repository.FunctionRepository;
import org.egov.finance.inbox.repository.FundRepository;
import org.egov.finance.inbox.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MasterCommonService {
	
	@Autowired
	FundRepository fundRepository;
	@Autowired
	DepartmentRepository departmentRepository;
	
	@Autowired
	EgBillRegisterRepository egBillRegisterRepository;
	@Autowired
	EgBillregistermisRepository egBillregistermisRepository;
	
	@Autowired
	CVoucherHeaderRepository voucherHeaderRepo;
	
	@Autowired
	FinancialYearRepository financialYearRepository;
	
	@Autowired
	AppConfigValuesRepository appConfigValuesRepository;
	
	@Autowired
	ChartOfAccountsRepository chartOfAccountsRepository;
	
	@Autowired
	FunctionRepository functionRepository;
	
	@Autowired
	BudgetControlTypeRepository budgetControlTypeRepository;
	
	
	
	
	protected Fund getFundById(Long id) {
		 Specification<Fund> spec = SpecificationHelper.equal("id", id);
		 return fundRepository.findOne(spec).orElse(null);
	}
	
	
	protected Function getFunctionById(Long id) {
		 Specification<Function> spec = SpecificationHelper.equal("id", id);
		 return functionRepository.findOne(spec).orElse(null);
	}
	
	
	protected Department getDepartmenByCode(String code) {
		 Specification<Department> spec = SpecificationHelper.equal("code", code);
		 return departmentRepository.findOne(spec).orElse(null);
	}
	
	protected EgBillregister getEgBillRegisterByVoucherId(Long id) {
		 Specification<EgBillregister> spec = SpecificationHelper.equalNested("egBillregistermis.voucherHeader.id", id);
		 return egBillRegisterRepository.findOne(spec).orElse(null);
	}
	
	protected EgBillregistermis getEgBillregistermisByVoucherId(Long id) {
		 Specification<EgBillregistermis> spec = SpecificationHelper.equal("voucherHeader.id", id);
		 return egBillregistermisRepository.findOne(spec).orElse(null);
	}
	
	
	protected CVoucherHeader getVoucherById(Long id) {
		 Specification<CVoucherHeader> spec = SpecificationHelper.equal("id", id);
		 return voucherHeaderRepo.findOne(spec).orElse(null);
	}
	
	 public FinancialYear getFinancialYearByDate(Date date) {    
		 Specification<FinancialYear> spec = SpecificationHelper
				    .isDateWithinRangeWithFlag("startingDate", "endingDate", date, "isActiveForPosting", true);
		 return financialYearRepository.findOne(spec).orElse(null);
	        
	    }
	 
	 public List<AppConfigValues> getConfigValuesByModuleAndKey(String moduleName, String keyName) {
		    Specification<AppConfigValues> spec = Specification
		        .where(SpecificationHelper.<AppConfigValues, String>equal("config.keyName", keyName))
		        .and(SpecificationHelper.equalNested("config.module.name", moduleName)); // Don't specify <T, Y> here

		    return appConfigValuesRepository.findAll(spec);
		}
	 
	 public CChartOfAccounts getCChartOfAccountsByGlCode(final String glCode) {
		 Specification<CChartOfAccounts> spec = SpecificationHelper.equal("glcode", glCode);
	        return chartOfAccountsRepository.findOne(spec).orElse(null);
	    }
	 
	 public CChartOfAccounts getCChartOfAccountsById(final Long id) {
		 Specification<CChartOfAccounts> spec = SpecificationHelper.equal("id", id);
	        return chartOfAccountsRepository.findOne(spec).orElse(null);
	    }
	 
	 public List<BudgetControlType> getAllBudgetControlType() {
	        return budgetControlTypeRepository.findAll();
	    }

}


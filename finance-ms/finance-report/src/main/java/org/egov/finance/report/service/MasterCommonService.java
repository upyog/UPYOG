/**
 * Created on Jun 18, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.util.Optional;

import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.repository.DepartmentRepository;
import org.egov.finance.report.repository.FundRepository;
import org.egov.finance.report.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class MasterCommonService {
	
	@Autowired
	FundRepository fundRepository;
	@Autowired
	DepartmentRepository departmentRepository;
	
	protected Fund getFundById(Long id) {
		 Specification<Fund> spec = SpecificationHelper.equal("id", id);
		 return fundRepository.findOne(spec).orElse(null);
	}
	
	
	protected Department getDepartmenByCode(String code) {
		 Specification<Department> spec = SpecificationHelper.equal("code", code);
		 return departmentRepository.findOne(spec).orElse(null);
	}
}


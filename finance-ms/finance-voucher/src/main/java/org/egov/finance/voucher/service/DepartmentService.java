package org.egov.finance.voucher.service;

import org.egov.finance.voucher.entity.Department;
import org.egov.finance.voucher.exception.ApplicationRuntimeException;
import org.egov.finance.voucher.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;

    public void validateDepartmentCode(String code) {
        if (code == null || code.trim().isEmpty()) return;

        Department department = departmentRepository.findByCode(code);
        if (department == null) {
            throw new ApplicationRuntimeException("Not a valid Department");
        }
    }
}
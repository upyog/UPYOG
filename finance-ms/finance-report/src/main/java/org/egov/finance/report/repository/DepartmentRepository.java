/**
 * Created on May 30, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.repository;

import org.egov.finance.report.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> , JpaSpecificationExecutor<Department>{
	
	Optional<Department> findByCodeIgnoreCase(String code);
	 List<Department> findByCodeIn(List<String> codes);
   

}
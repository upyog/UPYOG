package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

//	@Query("SELECT new org.egov.finance.voucher.model.DepartmentModel(d.id, d.name, d.code) FROM Department d WHERE d.name = :name")
//	DepartmentModel findByName(@Param("name") String name);
//
//	@Query("SELECT new org.egov.finance.voucher.model.DepartmentModel(d.id, d.name, d.code) FROM Department d WHERE d.code = :code")
//	DepartmentModel findByCode(@Param("code") String code);

	
	 Department findByName(String name);

	    Department findByCode(String code);
}

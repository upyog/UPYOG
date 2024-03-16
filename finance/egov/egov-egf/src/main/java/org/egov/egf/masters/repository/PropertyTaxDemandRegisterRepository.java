package org.egov.egf.masters.repository;

import java.util.List;

import org.egov.egf.masters.model.PropertyTaxDemandRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyTaxDemandRegisterRepository extends JpaRepository<PropertyTaxDemandRegister, Long>{
	
	List<PropertyTaxDemandRegister> findAll();
}

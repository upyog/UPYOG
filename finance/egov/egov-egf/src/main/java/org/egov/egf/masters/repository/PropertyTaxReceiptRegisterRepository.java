package org.egov.egf.masters.repository;

import java.util.List;

import org.egov.egf.masters.model.PropertyTaxDemandRegister;
import org.egov.egf.masters.model.PropertyTaxReceiptRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyTaxReceiptRegisterRepository extends JpaRepository<PropertyTaxReceiptRegister,Long>{
	
	List<PropertyTaxReceiptRegister> findAll();

}

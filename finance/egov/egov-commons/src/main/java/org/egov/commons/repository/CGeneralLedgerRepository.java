package org.egov.commons.repository;

import java.util.List;

import org.egov.commons.CGeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CGeneralLedgerRepository extends JpaRepository<CGeneralLedger,Long>{
	List<CGeneralLedger> findAll();


}

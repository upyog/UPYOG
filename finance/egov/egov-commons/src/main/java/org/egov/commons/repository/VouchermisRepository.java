package org.egov.commons.repository;

import java.util.List;

import org.egov.commons.CVoucherHeader;
import org.egov.commons.Vouchermis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface VouchermisRepository extends JpaRepository<Vouchermis,Long>{
	
	List<Vouchermis> findAll();


}

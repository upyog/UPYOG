package org.egov.finance.voucher.repository;

import java.util.List;

import org.egov.finance.voucher.entity.Recovery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RecoveryRepository extends JpaRepository<Recovery, Long> {

	@Query("select r.id from Recovery r where r.chartofaccounts.glcode = :glcode")
	List<Long> findRecoveryIdsByGlcode(@Param("glcode") String glcode);
}

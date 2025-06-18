package org.egov.finance.voucher.repository;

import java.util.List;

import org.egov.finance.voucher.entity.EgSurrenderedCheques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgSurrenderedChequesRepository extends JpaRepository<EgSurrenderedCheques, Integer>, JpaSpecificationExecutor<EgSurrenderedCheques> {

    List<EgSurrenderedCheques> findByChequenumber(String chequenumber);
}

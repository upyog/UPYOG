package org.egov.finance.inbox.repository;

import java.util.List;

import org.egov.finance.inbox.entity.EgSurrenderedCheques;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgSurrenderedChequesRepository extends JpaRepository<EgSurrenderedCheques, Integer>, JpaSpecificationExecutor<EgSurrenderedCheques> {

    List<EgSurrenderedCheques> findByChequenumber(String chequenumber);
}

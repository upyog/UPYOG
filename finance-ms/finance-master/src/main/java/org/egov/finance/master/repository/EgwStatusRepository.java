package org.egov.finance.master.repository;

import org.egov.finance.master.entity.EgwStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgwStatusRepository extends JpaRepository<EgwStatus, Long>, JpaSpecificationExecutor<EgwStatus> {

}

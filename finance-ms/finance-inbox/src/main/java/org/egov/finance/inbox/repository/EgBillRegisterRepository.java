package org.egov.finance.inbox.repository;

import org.egov.finance.inbox.entity.EgBillregister;
import org.egov.finance.inbox.entity.EgwStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgBillRegisterRepository extends JpaRepository<EgBillregister, Long>, JpaSpecificationExecutor<EgBillregister> {

}

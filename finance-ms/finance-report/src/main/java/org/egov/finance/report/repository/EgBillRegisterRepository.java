package org.egov.finance.report.repository;

import org.egov.finance.report.entity.EgBillregister;
import org.egov.finance.report.entity.EgwStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface EgBillRegisterRepository extends JpaRepository<EgBillregister, Long>, JpaSpecificationExecutor<EgBillregister> {

}

package org.egov.finance.voucher.repository;

import java.util.Optional;

import org.egov.finance.voucher.entity.EgBillregister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EgBillregisterRepository extends JpaRepository<EgBillregister, Long> {

	Optional<EgBillregister> findById(Long billId);
}

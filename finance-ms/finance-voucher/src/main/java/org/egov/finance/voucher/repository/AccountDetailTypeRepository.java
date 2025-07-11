package org.egov.finance.voucher.repository;

import java.util.Optional;

import org.egov.finance.voucher.entity.AccountDetailType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountDetailTypeRepository
		extends JpaRepository<AccountDetailType, Long>, JpaSpecificationExecutor<AccountDetailType> {
	

	    boolean existsByNameIgnoreCase(String name);

	    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

	    Optional<AccountDetailType> findById(Long id); // optional, already exists in JpaRepository

	    Optional<AccountDetailType> findByName(String name);

	    
	    
	

}
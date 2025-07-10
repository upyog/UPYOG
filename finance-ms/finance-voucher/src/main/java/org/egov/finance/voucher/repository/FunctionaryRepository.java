package org.egov.finance.voucher.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.egov.finance.voucher.entity.Functionary;
import org.egov.finance.voucher.model.FunctionaryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FunctionaryRepository extends JpaRepository<Functionary, Integer> {
    Functionary findByCode(BigDecimal code);

	static FunctionaryModel getFunctionaryByCode(BigDecimal valueOf) {
		// TODO Auto-generated method stub
		return null;
	}
}
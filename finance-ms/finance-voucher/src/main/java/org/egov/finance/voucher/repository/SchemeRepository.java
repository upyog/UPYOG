package org.egov.finance.voucher.repository;

import org.egov.finance.voucher.entity.Scheme;
import org.egov.finance.voucher.model.SchemeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SchemeRepository extends JpaRepository<Scheme, Long>, JpaSpecificationExecutor<Scheme> {

	static SchemeModel getSchemeByCode(String schemecode) {
		// TODO Auto-generated method stub
		return null;
	}
	Scheme findByName(String name);

	Scheme findByCode(String code);
}

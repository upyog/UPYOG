/**
 * Created on Jun 20, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.util.List;

import org.egov.finance.report.entity.CGeneralLedger;
import org.egov.finance.report.repository.CGeneralLedgerRepository;
import org.egov.finance.report.util.SpecificationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class LedgerService {
	
	@Autowired
	CGeneralLedgerRepository ledgerRepo;

	public List<CGeneralLedger> getAllLedgerForvoucer(Long voucherId) {
		Specification<CGeneralLedger> spec = SpecificationHelper.equal("voucherHeader.id", voucherId);
		return ledgerRepo.findAll(spec);
	}
	
	public CGeneralLedger getLedgersByVoucherIdAndGlcodeAndLineId(Long voucherId, String glcode, Integer voucherLineId) {
		Specification<CGeneralLedger> spec = Specification.where(
	            SpecificationHelper.<CGeneralLedger, Long>equal("voucherHeaderId.id", voucherId))
	            .and(SpecificationHelper.<CGeneralLedger, String>equal("glcode", glcode))
	            .and(SpecificationHelper.<CGeneralLedger, Integer>equal("voucherlineId", voucherLineId));

	    return ledgerRepo.findOne(spec).orElse(null);
	}

}

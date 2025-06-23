package org.egov.finance.voucher.service;

import org.egov.finance.voucher.entity.CVoucherHeader;
import org.springframework.stereotype.Service;

@Service
public interface VouchernumberGenerator {

	public String getNextNumber(CVoucherHeader vh);
}

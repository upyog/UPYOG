package org.egov.finance.voucher.repository;

import org.springframework.stereotype.Service;

@Service
public interface BanNumberGenerator {
	public String getNextNumber();

}

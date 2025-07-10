package org.egov.finance.voucher.service;

import org.egov.finance.voucher.exception.TaskFailedException;
import org.egov.finance.voucher.repository.AccountDetailKeyRepository;
import org.egov.finance.voucher.repository.AccountDetailTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RequiredValidator {

	private static final Logger LOGGER = LoggerFactory.getLogger(RequiredValidator.class);

	private static int empDetailId = 0;

	@Autowired
	private AccountDetailTypeRepository accountDetailTypeRepository;

	@Autowired
	private AccountDetailKeyRepository accountDetailKeyRepository;

	public static void clearEmployeeMap() {
		if (LOGGER.isInfoEnabled())
			LOGGER.info("Clearing the employeeMap");
		empDetailId = 0;
	}

	public boolean isEmployee(final int detailId) {
		if (empDetailId == 0) {
			accountDetailTypeRepository.findByName("EMPLOYEE").ifPresent(type -> empDetailId = type.getId().intValue());
		}
		return empDetailId == detailId;
	}

	public boolean validateKey(final int detailId, final String keyToValidate) throws TaskFailedException {
		try {
			return accountDetailKeyRepository.existsByAccountDetailType_IdAndDetailkey(detailId,
					Integer.valueOf(keyToValidate));
		} catch (NumberFormatException e) {
			throw new TaskFailedException("Invalid key format: " + keyToValidate, e);
		}
	}
}

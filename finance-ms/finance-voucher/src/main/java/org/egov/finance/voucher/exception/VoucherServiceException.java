package org.egov.finance.voucher.exception;

import java.util.Map;

import org.egov.finance.voucher.util.MasterConstants;
import org.egov.finance.voucher.util.VoucherConstant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@EqualsAndHashCode(callSuper = false)
public class VoucherServiceException {

	private static final long serialVersionUID = -19921057227329617L;

	private final Map<String, String> errors;

	public VoucherServiceException(Map<String, String> errors) {
		super();
		this.errors = errors;
		log.error(VoucherConstant.EXCEPTION_FROM_VOUCHER_SERVICE_MSG, errors);
	}

}

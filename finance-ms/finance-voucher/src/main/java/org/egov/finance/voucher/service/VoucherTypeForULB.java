package org.egov.finance.voucher.service;

import java.util.List;

import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.util.FinancialConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class VoucherTypeForULB {

	private static final Logger LOGGER = LoggerFactory.getLogger(VoucherTypeForULB.class);
	@Autowired
	private AppConfigValueService appConfigValuesService;

	public String readVoucherTypes(final String vType) {
		String voucherType = "N";
		final String keyName = "Autogenerate_" + vType.toLowerCase() + "_vouchernumber";
		final List<AppConfigValues> configValues = appConfigValuesService
				.getConfigValuesByModuleAndKey(FinancialConstants.MODULE_NAME_APPCONFIG, keyName);

		for (final AppConfigValues appConfigVal : configValues)
			voucherType = appConfigVal.getValue();

		if (LOGGER.isInfoEnabled())
			LOGGER.info("VoucherType is-->" + voucherType);
		return "Y".equalsIgnoreCase(voucherType) ? FinancialConstants.AUTO : FinancialConstants.MANUAL;
	}

//	public String readIsDepartmentMandtory() {
//		final String deptMandatory = EGovConfig.getProperty("egf_config.xml", "deptRequired", "", "general");
//		return deptMandatory;
//	}

}

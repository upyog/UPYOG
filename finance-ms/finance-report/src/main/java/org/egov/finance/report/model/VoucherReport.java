/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.model;

import org.egov.finance.report.entity.CGeneralLedger;
import org.egov.finance.report.entity.Department;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class VoucherReport {
	    private CGeneralLedger generalLedger = new CGeneralLedger();
	    private CGeneralLedger voucherDetail = new CGeneralLedger();
	    private Department department;
	    private static final String MULTIPLE = "MULTIPLE";
}


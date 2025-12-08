/**
 * Created on Jun 20, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.model;

import org.egov.finance.inbox.entity.CGeneralLedger;

import lombok.Data;

@Data
public class VoucherReportModel {
	
	private CGeneralLedger generalLedger = new CGeneralLedger();
    private CGeneralLedger voucherDetail = new CGeneralLedger();
   // private PersistenceService persistenceService;
   // private Department department;
    private static final String MULTIPLE = "MULTIPLE";
    private String slCode;

}


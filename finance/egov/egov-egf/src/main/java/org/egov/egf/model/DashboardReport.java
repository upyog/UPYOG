package org.egov.egf.model;

import lombok.Getter;
import lombok.Setter;
import org.egov.model.masters.Contractor;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class DashboardReport {


    private Long totalExpenseBills;

    private Long totalContractorBills;

    private Long totalSupplierBills;

    private Long totalWorkOrders;

    private Long totalPurchaseOrders;

    private Long totalJournalVouchers;

    private Long totalFunds;

    private Long totalBankAccounts;

    private Long totalContractors;

    private Long totalSuppliers;

    private Long totalBillsPayment;

    private String startDate;
    private String endDate;


    public DashboardReport() {

    }


}

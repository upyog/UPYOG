package org.egov.pt.repository;

import org.egov.pt.repository.BillSmsView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import org.egov.pt.models.PtBillTracker;

import java.util.List;

@Repository
public interface PtBillTrackerRepository extends JpaRepository<PtBillTracker, String> {

    @Query(
        value = "SELECT " + 
                "pt.uuid AS uuid, " +
                "pt.propertyid AS grbgApplicationId, " +
                "pt.tenantid AS tenantId, " +
                "pt.financialyear AS financialYear, " +
                "pt.fromdate AS fromDate, " +
                "pt.todate AS toDate, " +
                "CAST(pt.propertytax AS numeric) AS grbgBillAmount, " +
                "pt.createdby AS createdBy, " +
                "pt.createdtime AS createdTime, " +
                "pt.lastmodifiedby AS lastModifiedBy, " +
                "pt.lastmodifiedtime AS lastModifiedTime, " +
                "pt.bill_id AS billId, " +
                "egbs.additionaldetails->>'ward' AS ward, " +
                "egbs.additionaldetails->>'ownerName' AS ownerName, " +
                "egbs.additionaldetails->>'contactNumber' AS contactNumber " +
                "FROM eg_pt_tax_calculator_tracker pt " +
                "JOIN egbs_billdetail_v1 egbs " +
                "ON pt.bill_id = egbs.billid " +
                "WHERE pt.bill_status = 'ACTIVE' " +
                "LIMIT ?1 OFFSET ?2",
        nativeQuery = true
    )
    List<BillSmsView> fetchActiveBillsForSms(int limit, int offset);
}


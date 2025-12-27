package org.egov.garbageservice.repository;

import org.egov.garbageservice.repository.BillSmsView;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

import org.egov.garbageservice.model.EgGrbgBillTracker;

import java.util.List;

@Repository
public interface EgGrbgBillTrackerRepository extends JpaRepository<EgGrbgBillTracker, String>{

    @Query(
        value = "SELECT " +
                "g.uuid AS uuid, " +
                "g.grbg_application_id AS grbgApplicationId, " +
                "g.tenant_id AS tenantId, " +
                "g.month AS month, " +
                "g.year AS year, " +
                "g.from_date AS fromDate, " +
                "g.to_date AS toDate, " +
                "g.bill_id AS billId, " +
                "g.grbg_bill_amount AS grbgBillAmount, " +
                "g.ward AS ward, " +
                "g.last_modified_by AS \"lastModifiedBy\"," +
                "b.mobilenumber AS \"mobileNumber\", " +
                "b.payername AS \"ownerName\" " +
                "FROM eg_grbg_bill_tracker g " +
                "JOIN egbs_bill_v1 b ON b.id = g.bill_id " +
                "WHERE b.status = 'ACTIVE' " +
                "LIMIT ?1 OFFSET ?2",
        nativeQuery = true
    )
    List<BillSmsView> fetchActiveBillsForSms(int limit, int offset);
}

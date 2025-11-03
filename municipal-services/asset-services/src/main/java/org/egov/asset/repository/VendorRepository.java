package org.egov.asset.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.kafka.Producer;
import org.egov.asset.web.models.Vendor;
import org.egov.asset.web.models.VendorRequest;
import org.egov.asset.web.models.VendorSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class VendorRepository {

    @Autowired
    private Producer producer;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Pushes the request on save vendor topic through kafka
     */
    public void saveVendor(VendorRequest vendorRequest) {
        String sql = "INSERT INTO eg_asset_vendor (vendor_id, vendor_number, vendor_name, contact_person, contact_number, contact_email, gstin, pan, vendor_address, tenant_id, status, created_by, created_time, last_modified_by, last_modified_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ON CONFLICT (vendor_id) DO NOTHING";

        Vendor vendor = vendorRequest.getVendor();
        jdbcTemplate.update(sql,
                vendor.getVendorId(),
                vendor.getVendorNumber(),
                vendor.getVendorName(),
                vendor.getContactPerson(),
                vendor.getContactNumber(),
                vendor.getContactEmail(),
                vendor.getGstin(),
                vendor.getPan(),
                vendor.getVendorAddress(),
                vendor.getTenantId(),
                vendor.getStatus(),
                vendor.getAuditDetails().getCreatedBy(),
                vendor.getAuditDetails().getCreatedTime(),
                vendor.getAuditDetails().getLastModifiedBy(),
                vendor.getAuditDetails().getLastModifiedTime()
        );
    }

    /**
     * Pushes the request on update vendor topic through kafka
     */
    public void updateVendor(VendorRequest vendorRequest) {
        producer.push("update-vendor", vendorRequest);
    }

    /**
     * Searches for vendor records
     */
    public List<Vendor> searchVendor(VendorSearchCriteria searchCriteria) {
        String sql = "SELECT * FROM eg_asset_vendor WHERE tenant_id = ?";
        List<Object> params = new ArrayList<>();
        params.add(searchCriteria.getTenantId());

        if (searchCriteria.getVendorName() != null) {
            sql += " AND vendor_name ILIKE ?";
            params.add("%" + searchCriteria.getVendorName() + "%");
        }

        return jdbcTemplate.query(sql, params.toArray(), (rs, rowNum) -> {
            Vendor vendor = new Vendor();
            vendor.setVendorId(rs.getString("vendor_id"));
            vendor.setVendorNumber(rs.getString("vendor_number"));
            vendor.setVendorName(rs.getString("vendor_name"));
            vendor.setContactPerson(rs.getString("contact_person"));
            vendor.setContactNumber(rs.getString("contact_number"));
            vendor.setContactEmail(rs.getString("contact_email"));
            vendor.setGstin(rs.getString("gstin"));
            vendor.setPan(rs.getString("pan"));
            vendor.setVendorAddress(rs.getString("vendor_address"));
            vendor.setTenantId(rs.getString("tenant_id"));
            vendor.setStatus(rs.getString("status"));
            return vendor;
        });
    }

    public Vendor findByVendorNumber(String vendorNumber, String tenantId) {
        String sql = "SELECT * FROM eg_asset_vendor WHERE vendor_number = ? AND tenant_id = ?";

        List<Vendor> vendors = jdbcTemplate.query(sql, new Object[]{vendorNumber, tenantId}, (rs, rowNum) -> {
            Vendor vendor = new Vendor();
            vendor.setVendorId(rs.getString("vendor_id"));
            vendor.setVendorNumber(rs.getString("vendor_number"));
            vendor.setVendorName(rs.getString("vendor_name"));
            return vendor;
        });

        return vendors.isEmpty() ? null : vendors.get(0);
    }
}
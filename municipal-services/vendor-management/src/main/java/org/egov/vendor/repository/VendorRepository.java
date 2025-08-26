package org.egov.vendor.repository;

import org.egov.vendor.web.models.vendorcontract.location.Address;
import org.egov.vendor.web.models.vendorcontract.vendor.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, String> {

}
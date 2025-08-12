
package org.egov.vendor.repository;

import org.egov.vendor.repository.dto.VendorDetailsDTO;
import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorAdditionalDetailsRepository extends JpaRepository<VendorAdditionalDetails, String>, JpaSpecificationExecutor<VendorAdditionalDetails> {


//    @Query("SELECT new org.egov.vendor.repository.dto.VendorDetailsDTO(v, vad) " +  // ✅ Pass entire objects
//            "FROM Vendor v " +
//            "JOIN VendorAdditionalDetails vad ON v.id = vad.vendorId " +
//            "WHERE vad.tenantId = :tenantId AND vad.vendorId = :vendorId")
//    List<VendorDetailsDTO> findVendorAndAdditionalDetailsJPQL(
//            @Param("tenantId") String tenantId,
//            @Param("vendorId") String vendorId);

    @Query("SELECT new org.egov.vendor.repository.dto.VendorDetailsDTO(v, vad) " +
            "FROM Vendor v " +
            "JOIN VendorAdditionalDetails vad ON v.id = vad.vendorId " + // ✅ Keep Join to avoid unnecessary objects
            "WHERE vad.tenantId = :tenantId AND vad.vendorId = :vendorId")
    List<VendorDetailsDTO> findVendorAndAdditionalDetailsJPQL(
            @Param("tenantId") String tenantId,
            @Param("vendorId") String vendorId);




}


package org.egov.vendor.repository;

import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<VendorAdditionalDetails, String>, JpaSpecificationExecutor<VendorAdditionalDetails> {


}

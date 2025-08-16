package org.egov.vendor.specifications;

import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.springframework.data.jpa.domain.Specification;

public class VendorSpecifications {

    public static Specification<VendorAdditionalDetails> hasVendorAdditionalDetailsId(String vendorAdditionalDetailsId) {
        return (root, query, criteriaBuilder) ->
                vendorAdditionalDetailsId != null ? criteriaBuilder.equal(root.get("vendorAdditionalDetailsId"), vendorAdditionalDetailsId) : null;
    }

    public static Specification<VendorAdditionalDetails> hasTenantId(String tenantId) {
        return (root, query, criteriaBuilder) ->
                tenantId != null ? criteriaBuilder.equal(root.get("tenantId"), tenantId) : null;
    }

    public static Specification<VendorAdditionalDetails> hasVendorId(String vendorId) {
        return (root, query, criteriaBuilder) ->
                vendorId != null ? criteriaBuilder.equal(root.get("vendorId"), vendorId) : null;
    }

    public static Specification<VendorAdditionalDetails> hasCategory(String category) {
        return (root, query, criteriaBuilder) ->
                category != null ? criteriaBuilder.equal(root.get("category"), category) : null;
    }

    public static Specification<VendorAdditionalDetails> hasStatus(String status) {
        return (root, query, criteriaBuilder) ->
                status != null ? criteriaBuilder.equal(root.get("status"), status) : null;
    }

    public static Specification<VendorAdditionalDetails> hasVendorGroup(String vendorGroup) {
        return (root, query, criteriaBuilder) ->
                vendorGroup != null ? criteriaBuilder.equal(root.get("vendorGroup"), vendorGroup) : null;
    }

    public static Specification<VendorAdditionalDetails> hasVendorType(String vendorType) {
        return (root, query, criteriaBuilder) ->
                vendorType != null ? criteriaBuilder.equal(root.get("vendorType"), vendorType) : null;
    }

    public static Specification<VendorAdditionalDetails> hasServiceType(String serviceType) {
        return (root, query, criteriaBuilder) ->
                serviceType != null ? criteriaBuilder.equal(root.get("serviceType"), serviceType) : null;
    }

    public static Specification<VendorAdditionalDetails> hasRegistrationNo(String registrationNo) {
        return (root, query, criteriaBuilder) ->
                registrationNo != null ? criteriaBuilder.equal(root.get("registrationNo"), registrationNo) : null;
    }
}

package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.VendorRepository;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.Vendor;
import org.egov.asset.web.models.VendorRequest;
import org.egov.asset.web.models.VendorSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class VendorService {

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private AssetUtil assetUtil;

    /**
     * Creates a new vendor record
     */
    public Vendor createVendor(@Valid VendorRequest vendorRequest) {
        log.debug("Vendor create service method called");

        if (vendorRequest.getVendor().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Vendor cannot be created at StateLevel");
        }

        enrichVendorCreateRequest(vendorRequest);
        vendorRepository.saveVendor(vendorRequest);

        return vendorRequest.getVendor();
    }

    /**
     * Updates an existing vendor record
     */
    public Vendor updateVendor(@Valid VendorRequest vendorRequest) {
        log.debug("Vendor update service method called");

        if (vendorRequest.getVendor().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Vendor cannot be updated at StateLevel");
        }

        // If vendorId is not provided but vendorNumber is, fetch vendorId
        if (vendorRequest.getVendor().getVendorId() == null && vendorRequest.getVendor().getVendorNumber() != null) {
            VendorSearchCriteria searchCriteria = VendorSearchCriteria.builder()
                    .tenantId(vendorRequest.getVendor().getTenantId())
                    .vendorNumbers(Arrays.asList(vendorRequest.getVendor().getVendorNumber()))
                    .build();
            List<Vendor> vendors = vendorRepository.searchVendor(searchCriteria);
            if (!vendors.isEmpty()) {
                vendorRequest.getVendor().setVendorId(vendors.get(0).getVendorId());
            } else {
                throw new CustomException("INVALID_VENDOR", "Vendor not found with number: " + vendorRequest.getVendor().getVendorNumber());
            }
        }

        enrichVendorUpdateRequest(vendorRequest);
        vendorRepository.updateVendor(vendorRequest);

        return vendorRequest.getVendor();
    }

    /**
     * Searches for vendor records
     */
    public List<Vendor> searchVendor(@Valid VendorSearchCriteria searchCriteria) {
        log.debug("Vendor search service method called");
        return vendorRepository.searchVendor(searchCriteria);
    }

    /**
     * Enriches vendor create request
     */
    private void enrichVendorCreateRequest(VendorRequest vendorRequest) {
        AuditDetails auditDetails = assetUtil.getAuditDetails(
                vendorRequest.getRequestInfo().getUserInfo().getUuid(), true);

        vendorRequest.getVendor().setVendorId(UUID.randomUUID().toString());
        vendorRequest.getVendor().setVendorNumber(generateVendorNumber());
        vendorRequest.getVendor().setAuditDetails(auditDetails);

        if (vendorRequest.getVendor().getStatus() == null) {
            vendorRequest.getVendor().setStatus("ACTIVE");
        }
    }

    /**
     * Enriches vendor update request
     */
    private void enrichVendorUpdateRequest(VendorRequest vendorRequest) {
        AuditDetails auditDetails = assetUtil.getAuditDetails(
                vendorRequest.getRequestInfo().getUserInfo().getUuid(), false);
        vendorRequest.getVendor().setAuditDetails(auditDetails);
    }

    /**
     * Generates unique vendor number in format UVINYYMMDDHHMMSS
     */
    private String generateVendorNumber() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "UVIN" + now.format(formatter);
    }
}
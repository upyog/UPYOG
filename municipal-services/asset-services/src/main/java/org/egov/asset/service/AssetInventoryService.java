package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.dto.AssetDTO;
import org.egov.asset.repository.AssetInventoryRepository;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.repository.AssetInventoryProcurementRepository;
import org.egov.asset.repository.VendorRepository;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.web.models.*;
import org.egov.tracer.model.CustomException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AssetInventoryService {

    @Autowired
    private AssetInventoryRepository assetInventoryRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private AssetInventoryProcurementRepository procurementRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Creates a new asset inventory record.
     *
     * @param assetRequest The request containing asset inventory details to be created.
     * @return The created Asset object.
     */
    public Asset createInventory(@Valid AssetRequest assetRequest) {
        log.debug("Asset inventory create service method called");

        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        // Validate procurement request and fetch asset details
        validateAndEnrichFromProcurement(assetRequest);

        enrichmentService.enrichAssetOtherOperationsCreateRequest(assetRequest);
        assetInventoryRepository.saveInventory(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Updates an existing asset inventory record.
     *
     * @param assetRequest The request containing asset inventory details to be updated.
     * @return The updated Asset object.
     */
    public Asset updateInventory(@Valid AssetRequest assetRequest) {
        log.debug("Asset inventory update service method called");

        if (assetRequest.getAsset().getTenantId().split("\\.").length == 1) {
            throw new CustomException(AssetErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        enrichmentService.enrichAssetOtherOperationsUpdateRequest(assetRequest);
        assetInventoryRepository.updateInventory(assetRequest);

        return assetRequest.getAsset();
    }

    /**
     * Searches for asset inventory records.
     *
     * @param assetRequest The request containing search criteria.
     * @return A list of Asset objects matching the search criteria.
     */
    public List<AssetDTO> searchInventory(@Valid AssetRequest assetRequest) {
        log.debug("Asset inventory search service method called");
        
        List<Asset> assets = assetInventoryRepository.searchInventory(assetRequest);
        return assets.stream()
                .map(asset -> modelMapper.map(asset, AssetDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Validates procurement request and fetches asset details from it.
     *
     * @param assetRequest The request containing asset inventory details.
     */
    private void validateAndEnrichFromProcurement(AssetRequest assetRequest) {
        AssetInventory assetInventory = assetRequest.getAsset().getAssetInventory();
        
        if (assetInventory != null && StringUtils.hasText(assetInventory.getProcurementRequestId())) {
            AssetInventoryProcurementRequest searchCriteria = AssetInventoryProcurementRequest.builder()
                    .requestId(assetInventory.getProcurementRequestId())
                    .tenantId(assetInventory.getTenantId())
                    .build();
            
            List<AssetInventoryProcurementRequest> procurementRequests = procurementRepository.search(searchCriteria);
            
            if (procurementRequests.isEmpty()) {
                throw new CustomException("INVALID_PROCUREMENT_REQUEST", 
                        "Procurement request not found: " + assetInventory.getProcurementRequestId());
            }
            
            AssetInventoryProcurementRequest procurementRequest = procurementRequests.get(0);
            if (!"APPROVED".equals(procurementRequest.getStatus())) {
                throw new CustomException("PROCUREMENT_REQUEST_NOT_APPROVED", 
                        "Procurement request must be approved before creating inventory. Current status: " + procurementRequest.getStatus());
            }
            
            // Set inventory ID from procurement request ID
            assetInventory.setInventoryId(procurementRequest.getRequestId());
            
            // Fetch and set asset details using assetApplicationNumber from procurement
            fetchAssetFromApplicationNumber(assetRequest, procurementRequest.getAssetApplicationNumber());
            
            // Fetch vendor details from procurement (if vendorId exists in procurement)
            fetchVendorFromProcurement(assetInventory, procurementRequest);
        }
    }
    
    /**
     * Fetches asset details using application number from procurement.
     */
    private void fetchAssetFromApplicationNumber(AssetRequest assetRequest, String applicationNumber) {
        AssetSearchCriteria searchCriteria = AssetSearchCriteria.builder()
                .applicationNo(applicationNumber)
                .tenantId(assetRequest.getAsset().getTenantId())
                .build();
                
        List<Asset> assets = assetRepository.getAssetData(searchCriteria);
        
        if (assets.isEmpty()) {
            throw new CustomException("ASSET_NOT_FOUND", 
                    "Asset not found for application number: " + applicationNumber);
        }
        
        Asset foundAsset = assets.get(0);
        assetRequest.getAsset().setId(foundAsset.getId());
        assetRequest.getAsset().setApplicationNo(foundAsset.getApplicationNo());
    }

    /**
     * Validates and enriches vendor details from vendor service.
     */
    private void fetchVendorFromProcurement(AssetInventory assetInventory, AssetInventoryProcurementRequest procurementRequest) {
        if (assetInventory != null && StringUtils.hasText(assetInventory.getVendorNumber())) {
            Vendor vendor = vendorRepository.findByVendorNumber(
                assetInventory.getVendorNumber(), 
                assetInventory.getTenantId());
            
            if (vendor == null) {
                throw new CustomException("INVALID_VENDOR_NUMBER", 
                        "Vendor number not found: " + assetInventory.getVendorNumber());
            }
            
            assetInventory.setVendorId(vendor.getVendorId());
            log.info("Set vendorId: {} for vendor number: {}", vendor.getVendorId(), assetInventory.getVendorNumber());
        }
    }
}
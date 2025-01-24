
package org.egov.vendor.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.vendor.config.VendorConfiguration;
import org.egov.vendor.kafka.Producer;
import org.egov.vendor.specifications.VendorSpecifications;
import org.egov.vendor.utils.MdmsUtil;
import org.egov.vendor.utils.VendorErrorConstants;
import org.egov.vendor.utils.VendorValidator;
import org.egov.vendor.web.models.SearchCriteria;
import org.egov.vendor.web.models.VendorAdditionalDetails;
import org.egov.vendor.repository.VendorRepository;
import org.egov.vendor.web.models.VendorAdditionalDetailsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Service
@Transactional
public class VendorService {

    private final VendorRepository vendorRepository;
    private final KafkaTemplate<String, VendorAdditionalDetails> kafkaTemplate;
    private final Producer producer;
    private final VendorConfiguration config;
    private final VendorValidator vendorValidator;
    private final EnrichmentService enrichmentService;
    @Autowired
    MdmsUtil util;

    public VendorService(VendorRepository contractorRepository, KafkaTemplate<String, VendorAdditionalDetails> kafkaTemplate, Producer producer, VendorConfiguration config, VendorValidator vendorValidator, EnrichmentService enrichmentService) {
        this.vendorRepository = contractorRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.producer = producer;
        this.config = config;
        this.vendorValidator = vendorValidator;
        this.enrichmentService = enrichmentService;
    }

    public List<VendorAdditionalDetails> getAllVendors() {
        return vendorRepository.findAll();
    }

    public VendorAdditionalDetails saveVendor(@Valid VendorAdditionalDetailsRequest vendorRequest) {
        log.debug("Asset update service method called");
        RequestInfo requestInfo = vendorRequest.getRequestInfo();
        String tenantId = vendorRequest.getRequestInfo().getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);
        VendorAdditionalDetails vendorAdditionalDetails = vendorRequest.getVendorAdditionalDetails();

        // Application cannot be created at the state level
        if (vendorRequest.getVendorAdditionalDetails().getTenantId().split("\\.").length == 1) {
            throw new CustomException(VendorErrorConstants.INVALID_TENANT,
                    "Application cannot be created at StateLevel");
        }

        vendorValidator.validateCreate(vendorRequest, mdmsData);

        // Enrich the asset creation request with necessary details
        enrichmentService.enrichAssetCreateRequest(vendorRequest, mdmsData);

        // Update the workflow for asset creation
        // workflowService.updateWorkflow(assetRequest, CreationReason.CREATE);

        //VendorAdditionalDetails savedVendor = contractorRepository.save(vendorAdditionalDetails);
        producer.push(config.getSaveVendorAdditionalDetails(), vendorRequest);
        return vendorRequest.getVendorAdditionalDetails();
    }

    public void deleteVendor(String id) {
        vendorRepository.deleteById(id);
    }

    public VendorAdditionalDetails updateVendor(VendorAdditionalDetailsRequest request) {

        log.debug("Asset update service method called");
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getRequestInfo().getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);
        VendorAdditionalDetails vendorAdditionalDetails = request.getVendorAdditionalDetails();

        // Enrich the asset creation request with necessary details
        enrichmentService.enrichAssetUpdateRequest(request, mdmsData);

        VendorAdditionalDetails updatedVendor = vendorRepository.save(vendorAdditionalDetails);
        producer.push(config.getUpdateVendorAdditionalDetails(), updatedVendor);
        return updatedVendor;
    }

    public List<VendorAdditionalDetails> searchVendors(SearchCriteria criteria, RequestInfo requestInfo) {
        Specification<VendorAdditionalDetails> spec = Specification.where(
                VendorSpecifications.hasVendorAdditionalDetailsId(criteria.getVendorAdditionalDetailsId())
                        .and(VendorSpecifications.hasTenantId(criteria.getTenantId()))
                        .and(VendorSpecifications.hasVendorId(criteria.getVendorId()))
                        .and(VendorSpecifications.hasCategory(criteria.getCategory()))
                        .and(VendorSpecifications.hasStatus(criteria.getStatus()))
                        .and(VendorSpecifications.hasVendorGroup(criteria.getVendorGroup()))
                        .and(VendorSpecifications.hasVendorType(criteria.getVendorType()))
                        .and(VendorSpecifications.hasServiceType(criteria.getServiceType()))
                        .and(VendorSpecifications.hasRegistrationNo(criteria.getRegistrationNo()))
        );

        // Define sorting if needed
        Sort sort = Sort.by(Sort.Direction.ASC, "vendorAdditionalDetailsId");

        return vendorRepository.findAll(spec, sort);
    }
}

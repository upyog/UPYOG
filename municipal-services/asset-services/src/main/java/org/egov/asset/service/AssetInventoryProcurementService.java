package org.egov.asset.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetInventoryProcurementRepository;
import org.egov.asset.util.AssetUtil;
import org.egov.asset.web.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class AssetInventoryProcurementService {

    @Autowired
    private AssetInventoryProcurementRepository procurementRepository;

    @Autowired
    private AssetUtil assetUtil;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public AssetInventoryProcurementRequest createProcurementRequest(AssetInventoryProcurementCreateRequest request) {
        AssetInventoryProcurementRequest procurementRequest = request.getProcurementRequest();
        
        // Generate unique request ID with format REQIDYYMMDDHHMMSS
        String requestId = generateRequestId();
        procurementRequest.setRequestId(requestId);
        
        // Set audit details
        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), true);
        procurementRequest.setAuditDetails(auditDetails);
        
        // Set default status
        procurementRequest.setStatus("PENDING");
        
        // Create wrapper for Kafka
        ProcurementRequestRequest kafkaRequest = ProcurementRequestRequest.builder()
                .requestInfo(request.getRequestInfo())
                .procurementRequest(procurementRequest)
                .build();
        
        // Send to Kafka for persistence
        kafkaTemplate.send("save-inventory-procurement-request", kafkaRequest);
        
        return procurementRequest;
    }

    public AssetInventoryProcurementRequest updateProcurementRequest(AssetInventoryProcurementUpdateRequest request) {
        AssetInventoryProcurementRequest procurementRequest = request.getProcurementRequest();
        
        // Update audit details
        AuditDetails auditDetails = assetUtil.getAuditDetails(request.getRequestInfo().getUserInfo().getUuid(), false);
        procurementRequest.setAuditDetails(auditDetails);
        
        // Status will be updated via request payload
        
        // Create wrapper for Kafka
        ProcurementRequestRequest kafkaRequest = ProcurementRequestRequest.builder()
                .requestInfo(request.getRequestInfo())
                .procurementRequest(procurementRequest)
                .build();
        
        // Send to Kafka for persistence
        kafkaTemplate.send("update-inventory-procurement-request", kafkaRequest);
        
        return procurementRequest;
    }

    public List<AssetInventoryProcurementRequest> searchProcurementRequest(AssetInventoryProcurementSearchRequest request) {
        return procurementRepository.search(request.getProcurementRequest());
    }

    private String generateRequestId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        return "REQID" + now.format(formatter);
    }
}
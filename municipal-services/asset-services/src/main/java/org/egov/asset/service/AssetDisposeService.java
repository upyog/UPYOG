package org.egov.asset.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.repository.AssetDisposeRepository;
import org.egov.asset.repository.AssetRepository;
import org.egov.asset.repository.querybuilder.AssetDisposalQueryBuilder;
import org.egov.asset.repository.rowmapper.AssetDisposalRowMapper;
import org.egov.asset.util.AssetErrorConstants;
import org.egov.asset.util.AssetValidator;
import org.egov.asset.util.MdmsUtil;
import org.egov.asset.web.models.AuditDetails;
import org.egov.asset.web.models.disposal.AssetDisposal;
import org.egov.asset.web.models.disposal.AssetDisposalRequest;
import org.egov.asset.web.models.disposal.AssetDisposalSearchCriteria;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AssetDisposeService {

    @Autowired
    private AssetDisposeRepository assetDisposeRepository;

    @Autowired
    private ObjectMapper objectMapper; // Inject ObjectMapper

    @Autowired
    MdmsUtil util;

    @Autowired
    AssetRepository assetRepository;

    @Autowired
    AssetValidator assetValidator;

    @Autowired
    EnrichmentService enrichmentService;

    @Autowired
    AssetDisposalQueryBuilder assetDisposalQueryBuilder;

    @Autowired
    AssetDisposalRowMapper assetDisposalRowMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public AssetDisposal createDisposal(AssetDisposalRequest request) {
        log.debug("Asset disposal service method create called");
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getRequestInfo().getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);

        // Ensure the disposalDate is valid (epoch time)
        if (request.getAssetDisposal() == null || request.getAssetDisposal().getDisposalDate() <= 0) {
            throw new IllegalArgumentException("Invalid disposal data");
        }

        AssetDisposal disposal = request.getAssetDisposal();

        // Enrich the disposal request with necessary details
        enrichmentService.enrichDisposalCreateOperations(request);
        //disposal.setAuditDetails(auditDetails);

        assetDisposeRepository.save(request);
        return disposal;
    }

    public AssetDisposal updateDisposal(@Valid AssetDisposalRequest request) {
        log.debug("Asset disposal service method update called");
        RequestInfo requestInfo = request.getRequestInfo();
        String tenantId = request.getRequestInfo().getUserInfo().getTenantId().split("\\.")[0];
        Object mdmsData = util.mDMSCall(requestInfo, tenantId);

        AssetDisposal assetDisposal = request.getAssetDisposal();

        // Check if the asset exists
        if (assetDisposal == null) {
            throw new CustomException(AssetErrorConstants.UPDATE_ERROR, "AssetDisposalRequest Not found in the System: " + assetDisposal);
        }

        // Enrich the disposal request with necessary details
        enrichmentService.enrichDisposalUpdateOperations(request);
        //assetDisposal.setAuditDetails(auditDetails);

        // Save the updated record
        assetDisposeRepository.update(request);
        return assetDisposal;
    }

    public List<AssetDisposal> searchDisposals(AssetDisposalSearchCriteria searchCriteria, RequestInfo requestInfo) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = null;
        if (searchCriteria != null) {
            query = assetDisposalQueryBuilder.getDisposalSearchQuery(searchCriteria, preparedStmtList);
            log.info("Final query: " + query);
            return jdbcTemplate.query(query, preparedStmtList.toArray(), assetDisposalRowMapper);
        }
        return null;
    }
}

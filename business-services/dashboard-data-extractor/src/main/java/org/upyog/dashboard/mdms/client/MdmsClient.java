package org.upyog.dashboard.mdms.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.dashboard.config.DashboardExtractorProperties;
import org.upyog.dashboard.constants.DashboardExtractorConstants;
import org.upyog.dashboard.mdms.model.MasterDetail;
import org.upyog.dashboard.mdms.model.MdmsCriteria;
import org.upyog.dashboard.mdms.model.MdmsCriteriaReq;
import org.upyog.dashboard.mdms.model.MdmsTenantResponse;
import org.upyog.dashboard.mdms.model.ModuleDetail;
import org.upyog.dashboard.model.RequestInfo;
import org.upyog.dashboard.service.OAuthTokenService;

/**
 * Client responsible for querying the eGov MDMS service to fetch tenant
 * metadata.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MdmsClient {

    private final RestTemplate restTemplate;
    private final OAuthTokenService oauthTokenService;
    private final DashboardExtractorProperties dashboardProperties;

    /**
     * Fetches all active ULB/City tenants for the specified state tenant from
     * MDMS.
     *
     * @param stateTenantId state identifier (e.g. {@code "pg"})
     * @return list of city/ULB tenant codes (e.g. {@code ["pb.amritsar", "pb.jalandhar"]})
     */
    public List<String> fetchCityTenants(String stateTenantId) {
        String rootTenantId = StringUtils.isNotBlank(dashboardProperties.getTenantId())
                ? dashboardProperties.getTenantId().trim().toLowerCase()
                : (StringUtils.isNotBlank(stateTenantId) ? stateTenantId.trim().toLowerCase() : null);

        if (StringUtils.isBlank(rootTenantId)) {
            log.error("State tenant ID is not provided or configured for MDMS tenant lookup");
            throw new IllegalArgumentException("State tenant ID must not be blank for MDMS tenant lookup");
        }

        String url = dashboardProperties.getMdmsHost() + dashboardProperties.getMdmsSearchEndpoint() + DashboardExtractorConstants.QUERY_PARAM_TENANT_ID + rootTenantId;

        log.info("Fetching tenant list from MDMS url: {} for tenantId: {}", url, rootTenantId);

        MdmsCriteriaReq criteriaReq = buildMdmsRequest(rootTenantId, stateTenantId);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<MdmsCriteriaReq> entity = new HttpEntity<>(criteriaReq, headers);

        try {
            ResponseEntity<MdmsTenantResponse> response = restTemplate.postForEntity(url, entity, MdmsTenantResponse.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MdmsTenantResponse body = response.getBody();
                if (body.getMdmsRes() != null && body.getMdmsRes().getTenant() != null) {
                    List<String> allTenants = body.getMdmsRes().getTenant().getEffectiveTenants();
                    List<String> cityTenants = allTenants.stream()
                            .filter(StringUtils::isNotBlank)
                            .collect(Collectors.toList());
                    log.info("Successfully fetched {} active city tenants from MDMS for state {}",
                            cityTenants.size(), stateTenantId);
                    return cityTenants;
                }
            }
            log.warn("MDMS returned empty tenant list or unexpected payload structure for state {}", stateTenantId);
        } catch (Exception exception) {
            log.error("Failed to fetch tenants from MDMS at {}: {}", url, exception.getMessage(), exception);
            throw new RuntimeException("Failed to fetch tenants from MDMS: " + exception.getMessage(), exception);
        }

        return new ArrayList<>();
    }

    private MdmsCriteriaReq buildMdmsRequest(String rootTenantId, String stateTenantId) {
        String authToken = null;
        try {
            authToken = oauthTokenService.getToken();
        } catch (Exception exception) {
            log.warn("Could not obtain OAuth access token for MDMS request: {}", exception.getMessage());
        }

        RequestInfo requestInfo = RequestInfo.builder()
                .apiId(DashboardExtractorConstants.DEFAULT_API_ID)
                .authToken(authToken)
                .msgId(System.currentTimeMillis() + DashboardExtractorConstants.PIPE_SEPARATOR + DashboardExtractorConstants.DEFAULT_LOCALE)
                .build();

        String filter = StringUtils.isNotBlank(stateTenantId)
                ? String.format(DashboardExtractorConstants.MDMS_NATIONAL_INFO_FILTER_FORMAT, stateTenantId)
                : null;

        MasterDetail masterDetail = MasterDetail.builder()
                .name(DashboardExtractorConstants.MDMS_MASTER_TENANTS)
                .filter(filter)
                .build();

        ModuleDetail moduleDetail = ModuleDetail.builder()
                .moduleName(DashboardExtractorConstants.MDMS_MODULE_TENANT)
                .masterDetails(Collections.singletonList(masterDetail))
                .build();

        MdmsCriteria criteria = MdmsCriteria.builder()
                .tenantId(rootTenantId)
                .moduleDetails(Collections.singletonList(moduleDetail))
                .build();

        return MdmsCriteriaReq.builder()
                .requestInfo(requestInfo)
                .mdmsCriteria(criteria)
                .build();
    }
}

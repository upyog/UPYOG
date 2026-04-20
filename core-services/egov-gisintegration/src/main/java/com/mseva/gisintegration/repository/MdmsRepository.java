package com.mseva.gisintegration.repository;

import com.mseva.gisintegration.config.MdmsConfig;
import com.mseva.gisintegration.model.*;
import com.mseva.gisintegration.model.MdmsRequest.MasterDetail;
import com.mseva.gisintegration.model.MdmsRequest.MdmsCriteria;
import com.mseva.gisintegration.model.MdmsRequest.ModuleDetail;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Repository
public class MdmsRepository {

    private static final Logger log = LoggerFactory.getLogger(MdmsRepository.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MdmsConfig mdmsConfig;

    public Map<String, String> getBoundaryByLocalityCode(String localityCode, String tenantId) {

        log.info("Fetching boundary for localityCode: {}, tenantId: {}", localityCode, tenantId);

        Map<String, String> result = getDefault();

        try {
            MdmsRequest request = buildRequest(tenantId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MdmsRequest> entity = new HttpEntity<>(request, headers);

            log.debug("Calling MDMS API at URL: {}", mdmsConfig.getUrl());

            ResponseEntity<MdmsResponse> response = restTemplate.exchange(
                    mdmsConfig.getUrl(),
                    HttpMethod.POST,
                    entity,
                    MdmsResponse.class
            );

            if (response.getBody() == null) {
                log.warn("MDMS response body is null");
                return result;
            }

            Object tbObj = response.getBody()
                    .getMdmsRes()
                    .get("egov-location")
                    .get("TenantBoundary");

            List<Map<String, Object>> tenantBoundary = new ArrayList<>();

            if (tbObj instanceof List<?>) {
                for (Object item : (List<?>) tbObj) {
                    if (item instanceof Map) {
                        tenantBoundary.add((Map<String, Object>) item);
                    }
                }
            } else {
                log.warn("TenantBoundary is not a list. Type: {}", tbObj != null ? tbObj.getClass() : "null");
            }

            for (Map<String, Object> hierarchy : tenantBoundary) {

                Map boundary = (Map) hierarchy.get("boundary");
                if (boundary == null) {
                    log.warn("Boundary object is null in hierarchy");
                    continue;
                }

                List zones = (List) boundary.get("children");

                for (Object z : zones) {
                    Map zone = (Map) z;

                    String zoneName = (String) zone.get("name");
                    String zoneCode = (String) zone.get("code");

                    List blocks = (List) zone.get("children");

                    for (Object b : blocks) {
                        Map block = (Map) b;

                        String blockName = (String) block.get("name");
                        String blockCode = (String) block.get("code");

                        List localities = (List) block.get("children");

                        for (Object l : localities) {
                            Map locality = (Map) l;

                            if (localityCode.equals(locality.get("code"))) {

                                log.info("Match found for localityCode: {}", localityCode);

                                result.put("zonename", zoneName);
                                result.put("zonecode", zoneCode);
                                result.put("blockname", blockName);
                                result.put("blockcode", blockCode);
                                result.put("localityname", (String) locality.get("name"));

                                return result;
                            }
                        }
                    }
                }
            }

            log.warn("No match found for localityCode: {}", localityCode);

        } catch (Exception e) {
            log.error("Error while fetching boundary for localityCode: {}, tenantId: {}", localityCode, tenantId, e);
        }

        return result;
    }

    private MdmsRequest buildRequest(String tenantId) {

        log.debug("Building MDMS request for tenantId: {}", tenantId);

        MasterDetail master = new MasterDetail();
        master.setName("TenantBoundary");

        ModuleDetail module = new ModuleDetail();
        module.setModuleName("egov-location");
        module.setMasterDetails(Collections.singletonList(master));

        MdmsCriteria criteria = new MdmsCriteria();
        criteria.setTenantId(tenantId);
        criteria.setModuleDetails(Collections.singletonList(module));

        MdmsRequest request = new MdmsRequest();
        request.setRequestInfo(new RequestInfo());
        request.setMdmsCriteria(criteria);

        return request;
    }

    private Map<String, String> getDefault() {
        Map<String, String> map = new HashMap<>();
        map.put("zonename", "UNKNOWN");
        map.put("zonecode", "UNKNOWN");
        map.put("blockname", "UNKNOWN");
        map.put("blockcode", "UNKNOWN");
        map.put("localityname", "UNKNOWN");
        return map;
    }
}
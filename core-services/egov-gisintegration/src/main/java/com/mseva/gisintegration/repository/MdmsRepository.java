package com.mseva.gisintegration.repository;

import com.mseva.gisintegration.config.MdmsConfig;
import com.mseva.gisintegration.model.*;
import com.mseva.gisintegration.model.MdmsRequest.MasterDetail;
import com.mseva.gisintegration.model.MdmsRequest.MdmsCriteria;
import com.mseva.gisintegration.model.MdmsRequest.ModuleDetail;

import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Repository
public class MdmsRepository {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MdmsConfig mdmsConfig;

    public Map<String, String> getBoundaryByLocalityCode(String localityCode, String tenantId) {

        Map<String, String> result = getDefault();

        try {
            MdmsRequest request = buildRequest(tenantId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<MdmsRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<MdmsResponse> response = restTemplate.exchange(
                    mdmsConfig.getUrl(),
                    HttpMethod.POST,
                    entity,
                    MdmsResponse.class
            );
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
            }
            for (Map<String, Object> hierarchy : tenantBoundary) {

                Map boundary = (Map) hierarchy.get("boundary");
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

        } catch (Exception e) {
            // log properly in prod
        }

        return result;
    }

    private MdmsRequest buildRequest(String tenantId) {

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
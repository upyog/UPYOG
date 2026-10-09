package org.upyog.dashboard.mdms.client;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.upyog.dashboard.config.DashboardExtractorProperties;
import org.upyog.dashboard.mdms.model.MdmsTenantResponse;
import org.upyog.dashboard.service.OAuthTokenService;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Unit tests for {@link MdmsClient} validating JSON parsing of MDMS nationalInfo response structure.
 */
@ExtendWith(MockitoExtension.class)
class MdmsClientTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private OAuthTokenService oauthTokenService;

    @Mock
    private DashboardExtractorProperties dashboardProperties;

    @InjectMocks
    private MdmsClient mdmsClient;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        lenient().when(dashboardProperties.getMdmsHost()).thenReturn("http://localhost:8181");
        lenient().when(dashboardProperties.getMdmsSearchEndpoint()).thenReturn("/egov-mdms-service/v1/_search");
    }

    @Test
    @DisplayName("fetchCityTenants builds request with JSONPath filter condition for stateCode")
    void fetchCityTenants_setsJsonPathFilterCondition() {
        when(dashboardProperties.getTenantId()).thenReturn("pb");
        org.mockito.ArgumentCaptor<org.springframework.http.HttpEntity<org.upyog.dashboard.mdms.model.MdmsCriteriaReq>> entityCaptor =
                org.mockito.ArgumentCaptor.forClass(org.springframework.http.HttpEntity.class);

        when(restTemplate.postForEntity(eq("http://localhost:8181/egov-mdms-service/v1/_search?tenantId=pb"), entityCaptor.capture(), eq(MdmsTenantResponse.class)))
                .thenReturn(new ResponseEntity<>(new MdmsTenantResponse(), HttpStatus.OK));

        mdmsClient.fetchCityTenants("Punjab");

        org.upyog.dashboard.mdms.model.MdmsCriteriaReq capturedReq = entityCaptor.getValue().getBody();
        assertThat(capturedReq).isNotNull();
        assertThat(capturedReq.getMdmsCriteria()).isNotNull();
        assertThat(capturedReq.getMdmsCriteria().getTenantId()).isEqualTo("pb");
        assertThat(capturedReq.getMdmsCriteria().getModuleDetails()).hasSize(1);
        org.upyog.dashboard.mdms.model.MasterDetail masterDetail = capturedReq.getMdmsCriteria().getModuleDetails().get(0).getMasterDetails().get(0);
        assertThat(masterDetail.getName()).isEqualTo("nationalInfo");
        assertThat(masterDetail.getFilter()).isEqualTo("$.[?(@.stateCode=='Punjab')].code");
    }

    @Test
    @DisplayName("fetchCityTenants successfully parses string array nationalInfo response from JSONPath filter")
    void fetchCityTenants_parsesStringArrayNationalInfoResponse() throws Exception {
        when(dashboardProperties.getTenantId()).thenReturn("pb");
        String jsonResponse = """
            {
                "ResponseInfo": null,
                "MdmsRes": {
                    "tenant": {
                        "nationalInfo": [
                            "pb.amritsar",
                            "pb.jalandhar",
                            "pb.phagwara"
                        ]
                    }
                }
            }
            """;

        MdmsTenantResponse responseObj = objectMapper.readValue(jsonResponse, MdmsTenantResponse.class);

        when(restTemplate.postForEntity(eq("http://localhost:8181/egov-mdms-service/v1/_search?tenantId=pb"), any(), eq(MdmsTenantResponse.class)))
                .thenReturn(new ResponseEntity<>(responseObj, HttpStatus.OK));

        List<String> result = mdmsClient.fetchCityTenants("Punjab");

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly("pb.amritsar", "pb.jalandhar", "pb.phagwara");
    }
}

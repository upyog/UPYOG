package org.egov.ndc.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.egov.ndc.service.NDCService;
import org.egov.ndc.web.model.RequestInfoWrapper;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.egov.ndc.web.model.ndc.NdcApplicationResponse;
import org.egov.ndc.web.model.ndc.NdcApplicationSearchCriteria;
import org.egov.ndc.web.model.ndc.NdcDeleteRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class NDCControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NDCService ndcService;

    @MockBean
    private ResponseInfoFactory  ResponseInfoFactory;

    @Autowired
    private ObjectMapper objectMapper;

    private RequestInfo mockRequestInfo;

    @BeforeEach
    void setup() {
        mockRequestInfo = new RequestInfo();
        // populate RequestInfo as needed
    }

    @Test
    void createNdcApplication_shouldReturnCreatedResponse() throws Exception {
        NdcApplicationRequest req = NdcApplicationRequest.builder()
                .requestInfo(mockRequestInfo)
                . applications(Collections.emptyList())
                .build();

        NdcApplicationResponse resp = NdcApplicationResponse.builder()
                . responseInfo(new ResponseInfo())
                . applications(req.getApplications())
                .build();

        when(ndcService.createNdcApplication(anyBoolean(), any())).thenReturn(req);
        when( ResponseInfoFactory.createResponseInfoFromRequestInfo(any(), eq(true))).thenReturn(new ResponseInfo());

        mockMvc.perform(post("/ndc/_create")
                        .param("skipWorkFlow", "false")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.Applications").isArray())
                .andExpect(jsonPath("$.ResponseInfo").exists());
    }

    @Test
    void updateNdcApplication_shouldReturnOkResponse() throws Exception {
        NdcApplicationRequest req = NdcApplicationRequest.builder()
                .requestInfo(mockRequestInfo)
                . applications(Collections.emptyList())
                .build();

        when(ndcService.updateNdcApplication(anyBoolean(), any())).thenReturn(req);
        when( ResponseInfoFactory.createResponseInfoFromRequestInfo(any(), eq(true))).thenReturn(new ResponseInfo());

        mockMvc.perform(put("/ndc/_update")
                        .param("skipWorkFlow", "true")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Applications").isArray())
                .andExpect(jsonPath("$.ResponseInfo").exists());
    }

    @Test
    void deleteNdcApplication_shouldReturnOkResponse() throws Exception {
        NdcDeleteRequest deleteReq = new NdcDeleteRequest();
        deleteReq.setUuid("uuid");
        deleteReq.setTenantId("tenantId");
        deleteReq.setRequestInfo(mockRequestInfo);
        deleteReq.setActive(false);

        NdcApplicationRequest responseRequest = NdcApplicationRequest.builder()
                .requestInfo(mockRequestInfo)
                .applications(Collections.emptyList())
                .build();

        when(ndcService.deleteNdcApplication(any())).thenReturn(responseRequest);
        when( ResponseInfoFactory.createResponseInfoFromRequestInfo(any(), eq(true))).thenReturn(new ResponseInfo());

        mockMvc.perform(put("/ndc/_delete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(deleteReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Applications").isArray())
                .andExpect(jsonPath("$.ResponseInfo").exists());
    }

    @Test
    void searchNdcApplications_shouldReturnOkResponse() throws Exception {
        RequestInfoWrapper wrapper = new RequestInfoWrapper();
        wrapper.setRequestInfo(mockRequestInfo);

        NdcApplicationSearchCriteria criteria = new NdcApplicationSearchCriteria();
        criteria.setTenantId("tenant1");

        when(ndcService.searchNdcApplications(any(), any())).thenReturn(Collections.emptyList());
        when( ResponseInfoFactory.createResponseInfoFromRequestInfo(any(), eq(true))).thenReturn(new ResponseInfo());

        mockMvc.perform(post("/ndc/_search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(wrapper))
                        .param("tenantId", criteria.getTenantId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.Applications").isArray())
                .andExpect(jsonPath("$.ResponseInfo").exists())
                .andExpect(jsonPath("$.totalCount").value(0));
    }
}

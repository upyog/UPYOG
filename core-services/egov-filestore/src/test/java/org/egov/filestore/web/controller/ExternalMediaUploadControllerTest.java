package org.egov.filestore.web.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filestore.domain.service.StorageService;
import org.egov.filestore.utils.StorageUtil;
import org.egov.filestore.web.contract.ExternalMediaUploadResponse;
import org.egov.filestore.web.contract.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.multipart.MultipartFile;

/**
 * Unit tests for {@code POST /v1/files/external-media} direct multipart upload.
 */
@ContextConfiguration(classes = {StorageController.class})
@ExtendWith(SpringExtension.class)
class ExternalMediaUploadControllerTest {

    @MockBean
    private StorageService storageService;

    @MockBean
    private ResponseFactory responseFactory;

    @MockBean
    private StorageUtil storageUtil;

    @Autowired
    private StorageController storageController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        HandlerExceptionResolver fallbackResolver = (request, response, handler, ex) -> {
            response.setStatus(500);
            return new ModelAndView();
        };

        mockMvc = MockMvcBuilders
                .standaloneSetup(storageController)
                .setHandlerExceptionResolvers(
                        new ResponseStatusExceptionResolver(),
                        new DefaultHandlerExceptionResolver(),
                        fallbackResolver)
                .build();
    }

    @Test
    void testUploadMedia_ValidVideoFile_Returns201() throws Exception {
        ExternalMediaUploadResponse mockResponse = ExternalMediaUploadResponse.builder()
                .fileStoreId("abc-123")
                .tenantId("pb.amritsar")
                .contentType("video/mp4")
                .fileSize("1024")
                .module("PGR")
                .tag("complaint-video")
                .build();

        when(storageService.saveMediaFile(any(MultipartFile.class), eq("PGR"), eq("complaint-video"),
                eq("pb.amritsar"), any(RequestInfo.class))).thenReturn(mockResponse);

        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "video-content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/files/external-media")
                        .file(file)
                        .param("tenantId", "pb.amritsar")
                        .param("module", "PGR")
                        .param("tag", "complaint-video"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fileStoreId").value("abc-123"))
                .andExpect(jsonPath("$.contentType").value("video/mp4"))
                .andExpect(jsonPath("$.fileSize").value("1024"));
    }

    @Test
    void testUploadMedia_MissingFile_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/files/external-media")
                        .param("tenantId", "pb.amritsar")
                        .param("module", "PGR"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUploadMedia_MissingTenantId_Returns400() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/files/external-media")
                        .file(file)
                        .param("module", "PGR"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testUploadMedia_WrongHttpMethod_Returns405() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/files/external-media"))
                .andExpect(status().is(405));
    }
}

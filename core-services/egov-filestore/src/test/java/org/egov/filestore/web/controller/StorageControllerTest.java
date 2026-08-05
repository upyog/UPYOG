package org.egov.filestore.web.controller;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.filestore.domain.model.FileInfo;
import org.egov.filestore.domain.model.Resource;
import org.egov.filestore.domain.service.StorageService;
import org.egov.filestore.utils.StorageUtil;
import org.egov.filestore.web.contract.ExternalMediaUploadResponse;
import org.egov.filestore.web.contract.GetFilesByTagResponse;
import org.egov.filestore.web.contract.ResponseFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {StorageController.class})
@ExtendWith(SpringExtension.class)
class StorageControllerTest {
    @MockBean
    private ResponseFactory responseFactory;

    @Autowired
    private StorageController storageController;

    @MockBean
    private StorageService storageService;

    @MockBean
    private StorageUtil storageUtil;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Standard Spring MVC resolvers handle framework-level exceptions (missing params -> 400,
        // wrong HTTP method -> 405, malformed JSON -> 400). A fallback resolver at the end catches
        // any other exception (e.g. CustomException from the service layer) and maps it to 500,
        // so tests can assert on status codes instead of unwrapped exceptions bubbling out.
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
    // -------------------------------------------------------------------------
    // Existing tests (unchanged)
    // -------------------------------------------------------------------------

    @Test
    void testGetFile() throws Exception {
        when(storageService.retrieve((String) any(), (String) any())).thenReturn(
                new Resource("text/plain", "foo.txt", new ByteArrayResource("AAAAAAAA".getBytes("UTF-8")), "42", "File Size"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files/id")
                .param("fileStoreId", "foo")
                .param("tenantId", "foo");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain"))
                .andExpect(MockMvcResultMatchers.content().string("AAAAAAAA"));
    }

    @Test
    void testGetFileContentTypeJson() throws Exception {
        when(storageService.retrieve((String) any(), (String) any())).thenReturn(
                new Resource("", "foo.txt", new ByteArrayResource("AAAAAAAA".getBytes("UTF-8")), "42", "File Size"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files/id")
                .param("fileStoreId", "foo")
                .param("tenantId", "foo");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("AAAAAAAA"));
    }

    @Test
    void testGetMetaData() throws Exception {
        when(storageService.retrieve((String) any(), (String) any())).thenReturn(
                new Resource("text/plain", "foo.txt", new ByteArrayResource("AAAAAAAA".getBytes("UTF-8")), "42", "File Size"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files/metadata")
                .param("fileStoreId", "foo")
                .param("tenantId", "foo");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"contentType\":\"text/plain\",\"fileName\":\"foo.txt\",\"resource\":null,\"tenantId\":\"42\",\"fileSize\":\"File"
                                        + " Size\"}"));
    }

    @Test
    void testGetUrlListByTag() throws Exception {
        when(storageService.retrieveByTag((String) any(), (String) any())).thenReturn(new ArrayList<>());
        when(responseFactory.getFilesByTagResponse((List<FileInfo>) any()))
                .thenReturn(new GetFilesByTagResponse(new ArrayList<>()));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files/tag")
                .param("tag", "foo")
                .param("tenantId", "foo");
        mockMvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.content().string("{\"files\":[]}"));
    }

    @Test
    void testGetUrls() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files/url").param("tenantId", "foo");
        ResultActions actualPerformResult = mockMvc.perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void testStoreFiles() throws Exception {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/v1/files")
                .param("module", "foo")
                .param("tenantId", "foo");
        ResultActions actualPerformResult = mockMvc.perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(405));
    }

    // -------------------------------------------------------------------------
    // New tests for POST /v1/files/external-media
    // -------------------------------------------------------------------------

    @Test
    void testUploadMedia_Returns201() throws Exception {
        ExternalMediaUploadResponse mockResponse = ExternalMediaUploadResponse.builder()
                .fileStoreId("store-id-001")
                .tenantId("pb.amritsar")
                .contentType("video/mp4")
                .fileSize("1024")
                .module("PGR")
                .tag("tag1")
                .build();

        when(storageService.saveMediaFile(any(MultipartFile.class), eq("PGR"), eq("tag1"),
                eq("pb.amritsar"), any(RequestInfo.class))).thenReturn(mockResponse);

        MockMultipartFile file = new MockMultipartFile("file", "video.mp4", "video/mp4", "data".getBytes());

        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/files/external-media")
                        .file(file)
                        .param("tenantId", "pb.amritsar")
                        .param("module", "PGR")
                        .param("tag", "tag1"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.fileStoreId").value("store-id-001"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.contentType").value("video/mp4"));
    }

    @Test
    void testUploadMedia_WrongMethod_Returns405() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/v1/files/external-media"))
                .andExpect(MockMvcResultMatchers.status().is(405));
    }

    @Test
    void testUploadMedia_MissingFile_Returns400() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/v1/files/external-media")
                        .param("tenantId", "pb.amritsar")
                        .param("module", "PGR"))
                .andExpect(MockMvcResultMatchers.status().is4xxClientError());
    }
}

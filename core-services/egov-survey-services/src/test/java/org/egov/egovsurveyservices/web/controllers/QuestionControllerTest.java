package org.egov.egovsurveyservices.web.controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.egovsurveyservices.service.QuestionService;
import org.egov.egovsurveyservices.web.models.QuestionRequest;
import org.egov.egovsurveyservices.web.models.QuestionSearchCriteria;
import org.egov.egovsurveyservices.web.models.RequestInfoWrapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(QuestionController.class)
public class QuestionControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    QuestionService  questionService;

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void testCreateQuestion() throws Exception {

        mockMvc.perform(post("/egov-ss/question/_create").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new QuestionRequest())))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateQuestion() throws Exception {

        mockMvc.perform(put("/egov-ss/question/_update").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new QuestionRequest())))
                .andExpect(status().isOk());
    }

    @Test
    public void testSearchQuestion() throws Exception {

        mockMvc.perform(post("/egov-ss/question/_search").contentType
                        (MediaType.APPLICATION_JSON).content(mapper.writeValueAsString(new QuestionSearchCriteria())))
                .andExpect(status().isOk());
    }

//    @Test
//    public void testUploadQuestions_success() throws Exception {
//        MockMultipartFile file = new MockMultipartFile("file", "file.xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "file content".getBytes());
//        MockMultipartFile requestInfo = new MockMultipartFile("requestInfo", "", "application/json", "{\"field1\": \"value1\", \"field2\": \"value2\"}".getBytes());
//        //   doNothing().when(questionService).uploadQuestions(any(RequestInfoWrapper.class), any(MultipartFile.class));
//
//        mockMvc.perform(multipart("/egov-ss/question/_upload")
//                        .file(file)
//                        .file(requestInfo))
//                .andExpect(status().isCreated())
//                .andExpect(content().string("Questions uploaded successfully!"));
//    }

//    @Test
//    public void testDownloadExcelTemplate() throws Exception {
//        byte[] templateBytes = "template content".getBytes();
//        //      when(questionService.downloadTemplate()).thenReturn(templateBytes);
//
//        String jsonData = "{\"field1\": \"value1\", \"field2\": \"value2\"}";
//
//        mockMvc.perform(get("/egov-ss/question/download-template")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(jsonData))
//                .andExpect(status().isOk())
//                .andExpect(content().bytes(templateBytes));
//    }
}

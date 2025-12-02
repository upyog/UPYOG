package org.egov.egovsurveyservices.web.controllers;


import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.common.contract.response.ResponseInfo;
import org.egov.egovsurveyservices.service.ScorecardSurveyService;
import org.egov.egovsurveyservices.utils.ResponseInfoFactory;
import org.egov.egovsurveyservices.web.models.AnswerFetchCriteria;
import org.egov.egovsurveyservices.web.models.AnswerRequestNew;
import org.egov.egovsurveyservices.web.models.ScorecardAnswerResponse;
import org.egov.egovsurveyservices.web.models.ScorecardSubmitResponse;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyEntity;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyRequest;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyResponse;
import org.egov.egovsurveyservices.web.models.ScorecardSurveySearchCriteria;
import org.egov.egovsurveyservices.web.models.SurveyResponseNew;
import org.egov.egovsurveyservices.web.models.UpdateSurveyActiveRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ScorecardSurveyController.class)
@RunWith(SpringRunner.class)
@ExtendWith(SpringExtension.class)
class ScorecardSurveyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScorecardSurveyService surveyService;

    @MockBean
    private ResponseInfoFactory responseInfoFactory;
    
    private static final String SEARCH_URL = "/egov-ss/survey/_search";

    private ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testCreateSurvey() throws Exception {
        String surveyJson = "{"
            + "\"requestInfo\": {\"apiId\": \"Rainmaker\", \"ver\": \".01\", \"msgId\": \"201703900|en_IN\", \"authToken\": \"ae9b231e16\"},"
            + "\"surveyEntity\": {\"tenantId\": \"pb.testing\", \"surveyTitle\": \"survey3\", \"surveyCategory\": \"sc survey testing\", \"surveyDescription\": \"survey about the citizen and people issues1\","
            + "\"startDate\": 1745173800000, \"endDate\": 1771372800000}}";

        when(surveyService.createSurvey(ArgumentMatchers.any(ScorecardSurveyRequest.class)))
            .thenReturn(new ScorecardSurveyEntity());

        mockMvc.perform(post("/egov-ss/survey/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(surveyJson))
            .andExpect(status().isOk());
    }


     @Test
     public void testSearchSurvey() throws Exception {
            // Mock request payload
            String requestJson = "{\"RequestInfo\":{\"apiId\":\"Rainmaker\",\"ver\":\".01\",\"authToken\":\"ae92a0ee-bb231e16\",\"userInfo\":{\"id\":296,\"type\":\"CITIZEN\"}}}";
            
            // Mock query params
            String uuid = "SS-1012/2024-25/000131";
            String tenantId = "pb.testing";
            String title = "survey1";

            ScorecardSurveyEntity survey = new ScorecardSurveyEntity();
            survey.setUuid(uuid);
            survey.setTenantId(tenantId);
            survey.setSurveyTitle(title);
            survey.setSurveyCategory("categorytest");
            survey.setSurveyDescription("survey about the citizens issues and problems");
            survey.setStartDate(1745173800000L);
            survey.setEndDate(1771372800000L);
            survey.setActive(false);
            survey.setAnswersCount(0L);
            survey.setHasResponded(false);
            survey.setCreatedTime(0L);
            survey.setLastModifiedTime(0L);

            List<ScorecardSurveyEntity> surveys = Collections.singletonList(survey);
            ScorecardSurveyResponse response = ScorecardSurveyResponse.builder()
                    .surveyEntities(surveys)
                    .totalCount(1)
                    .build();

            Mockito.when(surveyService.searchSurveys(Mockito.any(ScorecardSurveySearchCriteria.class)))
                    .thenReturn(surveys);

            mockMvc.perform(post(SEARCH_URL)
                    .param("uuid", uuid)
                    .param("tenantId", tenantId)
                    .param("title", title)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.TotalCount").value(1))
                    .andExpect(jsonPath("$.Surveys[0].uuid").value(uuid))
                    .andExpect(jsonPath("$.Surveys[0].tenantId").value(tenantId))
                    .andExpect(jsonPath("$.Surveys[0].surveyTitle").value(title));
      }

     @Test
     public void testUpdateActiveSurvey_WhenRequestIsValid_ShouldReturnSuccess() throws Exception {
         UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
         request.setUuid("SS-1012/2024-25/000131");
         request.setActive(true);

         mockMvc.perform(post("/egov-ss/survey/active/_update")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(new ObjectMapper().writeValueAsString(request)))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.message").value("Survey active status updated successfully!"));

         verify(surveyService, times(1)).updateSurveyActive(any(UpdateSurveyActiveRequest.class));
     }

     @Test
     public void testUpdateActiveSurvey_WhenUuidIsMissing_ShouldReturnBadRequest() throws Exception {
         UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
         request.setActive(true);  // Missing UUID

         mockMvc.perform(post("/egov-ss/survey/active/_update")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(new ObjectMapper().writeValueAsString(request)))
                 .andExpect(status().isBadRequest());
     }

     @Test
     public void testUpdateActiveSurvey_WhenActiveIsMissing_ShouldReturnBadRequest() throws Exception {
         UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
         request.setUuid("SS-1012/2024-25/000131");  // Missing active field

         mockMvc.perform(post("/egov-ss/survey/active/_update")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(new ObjectMapper().writeValueAsString(request)))
                 .andExpect(status().isBadRequest());
     }

     @Test
     public void testUpdateActiveSurvey_WhenUuidDoesNotExist_ShouldReturnBadRequest() throws Exception {
         UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
         request.setUuid("INVALID_UUID");
         request.setActive(true);

         doThrow(new IllegalArgumentException("UUID does not exist in database, Update failed!"))
             .when(surveyService).updateSurveyActive(any(UpdateSurveyActiveRequest.class));

         mockMvc.perform(post("/egov-ss/survey/active/_update")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(new ObjectMapper().writeValueAsString(request)))
                 .andExpect(status().isBadRequest());
     }
     
     @Test
     void testResponseSubmit() throws Exception {
         String requestJson = "{"
                 + "\"requestInfo\": {\"apiId\": \"Rainmaker\", \"ver\": \".01\", \"msgId\": \"201703900|en_IN\", \"authToken\": \"ae9b231e16\"},"
                 + "\"answers\": [{\"surveyUuid\": \"S1\", \"citizenId\": \"C1\", \"tenantId\": \"T1\"}]"
                 + "}";

         // Manually creating RequestInfo
         RequestInfo requestInfo = new RequestInfo();
         requestInfo.setApiId("");
         requestInfo.setVer(".01");
         requestInfo.setMsgId("201703900|en_IN");

         // Directly call static method
         ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);

         ScorecardAnswerResponse answerResponse = ScorecardAnswerResponse.builder()
                 .responseInfo(responseInfo)
                 .surveyUuid("S1")
                 .citizenId("C1")
                 .tenantId("T1")
                 .status("Submitted")
                 .build();

         ScorecardSubmitResponse mockResponse = ScorecardSubmitResponse.builder()
                 .responseInfo(responseInfo)
                 .scorecardAnswerResponse(answerResponse)
                 .build();

         // Mock surveyService response
         when(surveyService.submitResponse(any(AnswerRequestNew.class))).thenReturn(mockResponse);

         // Perform POST request
         mockMvc.perform(post("/egov-ss/survey/response/_submit")
                 .contentType(MediaType.APPLICATION_JSON)
                 .content(requestJson))
                 .andExpect(status().isOk())
                 .andExpect(jsonPath("$.ResponseInfo.apiId").value(""))
                 .andExpect(jsonPath("$.SubmitResponse.surveyUuid").value("S1"))
                 .andExpect(jsonPath("$.SubmitResponse.citizenId").value("C1"))
                 .andExpect(jsonPath("$.SubmitResponse.tenantId").value("T1"))
                 .andExpect(jsonPath("$.SubmitResponse.status").value("Submitted"));

         // Verify interaction
         verify(surveyService, times(1)).submitResponse(any(AnswerRequestNew.class));
     }
     
//     @Test
//     void testGetAnswers() throws Exception {
//         String requestJson = "{"
//                 + "\"requestInfo\": {\"apiId\": \"Rainmaker\", \"ver\": \".01\", \"msgId\": \"201703900|en_IN\", \"authToken\": \"ae9b231e16\"}"
//                 + "}";
//
//         // Manually creating RequestInfo
//         RequestInfo requestInfo = new RequestInfo();
//         requestInfo.setApiId("Rainmaker");
//         requestInfo.setVer(".01");
//         requestInfo.setMsgId("201703900|en_IN");
//
//         // Directly call static method
//         ResponseInfo responseInfo = ResponseInfoFactory.createResponseInfoFromRequestInfo(requestInfo, true);
//
//         // Prepare mock response
//         ScorecardAnswerResponse answerResponse = ScorecardAnswerResponse.builder()
//                 .responseInfo(responseInfo)
//                 .surveyUuid("S1")
//                 .citizenId("C1")
//                 .tenantId("T1")
//                 .status("Fetched")
//                 .build();
//
//         // Mock surveyService response
//         when(surveyService.getAnswers(any(AnswerFetchCriteria.class))).thenReturn(answerResponse);
//
//         // Perform POST request
//         mockMvc.perform(post("/egov-ss/survey/response/_answers")
//                 .contentType(MediaType.APPLICATION_JSON)
//                 .content(requestJson)
//                 .param("surveyUuid", "S1")
//                 .param("citizenId", "C1")
//                 .param("tenantId", "T1"))
//                 .andExpect(status().isOk())
////                 .andExpect(jsonPath("$.responseInfo.apiId").value("Rainmaker"))
//                 .andExpect(jsonPath("$.surveyUuid").value("S1"))
//                 .andExpect(jsonPath("$.citizenId").value("C1"))
//                 .andExpect(jsonPath("$.tenantId").value("T1"))
//                 .andExpect(jsonPath("$.status").value("Fetched"));
//
//         // Verify interaction
//         verify(surveyService, times(1)).getAnswers(any(AnswerFetchCriteria.class));
//     }


}



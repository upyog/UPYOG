package org.egov.egovsurveyservices.service;

import com.google.gson.Gson;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.producer.Producer;
import org.egov.egovsurveyservices.repository.ScorecardSurveyRepository;
import org.egov.egovsurveyservices.utils.ScorecardSurveyUtil;
import org.egov.egovsurveyservices.validators.ScorecardSurveyValidator;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.Type;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ScorecardSurveyServiceTest {
    @InjectMocks
    private ScorecardSurveyService scorecardSurveyService;

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private Producer producer;

    @Mock
    private ScorecardSurveyValidator surveyValidator;

    @Mock
    private EnrichmentService enrichmentService;

    @Mock
    private ScorecardSurveyRepository surveyRepository;

    @Mock
    private ScorecardSurveyUtil surveyUtil;

    private RequestInfo requestInfo;
    private Gson gson;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        requestInfo = RequestInfo.builder()
                .userInfo(User.builder().uuid("1").build())
                .build();
        gson = new Gson();
    }
    
    @Test
    public void testCreateSurvey_Success() {
        lenient().when(applicationProperties.getMaxCreateLimit()).thenReturn(5);
        lenient().when(applicationProperties.getCreateScorecardSurveyTopic()).thenReturn("save-survey");

        ScorecardSurveyEntity survey = getValidSurveyEntity();
        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();


        doNothing().when(surveyValidator).validateUserType(any());
        doNothing().when(enrichmentService).enrichScorecardSurveyEntity(any());
        when(surveyRepository.allQuestionsExist(anyList())).thenReturn(true);

        // Mock question existence check
        when(surveyRepository.allQuestionsExist(anyList())).thenReturn(true);

        // Mock surveyUtil to return a dummy UUID list
        when(surveyUtil.getIdList(any(), anyString(), anyString(), anyString(), anyInt()))
                .thenReturn(Collections.singletonList("SS-1012/2024-25/0020"));

        doNothing().when(producer).push(anyString(), any(Object.class));

        ScorecardSurveyEntity responseEntity = scorecardSurveyService.createSurvey(surveyRequest);

        verify(producer).push(anyString(), any(Object.class));

        assertNotNull(responseEntity);
        assertNotNull(responseEntity.getUuid());
    }

    @Test
    public void testCreateSurvey_Failure() {
        lenient().when(applicationProperties.getMaxCreateLimit()).thenReturn(5);
        lenient().when(applicationProperties.getCreateScorecardSurveyTopic()).thenReturn("save-survey");

        ScorecardSurveyEntity survey = getValidSurveyEntity();
        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();


        doNothing().when(surveyValidator).validateUserType(any());

        assertThrows(IllegalArgumentException.class, () ->
                scorecardSurveyService.createSurvey(surveyRequest));

    }


    @Test
    public void testCreateSurvey_EmptyTitle() {
    	ScorecardSurveyEntity survey = getValidSurveyEntity();
    	survey.setSurveyTitle(null);
        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();
        assertThrows(IllegalArgumentException.class, () -> scorecardSurveyService.createSurvey(surveyRequest));
    }
    
    @Test
    public void testCreateSurvey_NullStartDate() {
        ScorecardSurveyEntity survey = getValidSurveyEntity();
        survey.setStartDate(null); 

        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> scorecardSurveyService.createSurvey(surveyRequest));

        assertEquals("Survey startDate is required", exception.getMessage());
    }

    @Test
    public void testCreateSurvey_NullEndDate() {
        ScorecardSurveyEntity survey = getValidSurveyEntity();
        survey.setEndDate(null); 

        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();

        Exception exception = assertThrows(IllegalArgumentException.class, 
            () -> scorecardSurveyService.createSurvey(surveyRequest));

        assertEquals("Survey endDate is required", exception.getMessage());
    }

    @Test
    public void testCreateSurvey_InvalidDateRange() {
        ScorecardSurveyEntity survey = getValidSurveyEntity();
        survey.setStartDate(1704067200000L); 
        survey.setEndDate(1704067200000L); 

        ScorecardSurveyRequest surveyRequest = ScorecardSurveyRequest.builder()
                .requestInfo(requestInfo)
                .surveyEntity(survey)
                .build();

        Exception exception = assertThrows(CustomException.class,
            () -> scorecardSurveyService.createSurvey(surveyRequest));

        assertEquals("INVALID_DATE_RANGE", ((CustomException) exception).getCode());
        assertEquals("Start date must be before end date", exception.getMessage());
    }

    @Test
    public void testSearchSurvey_ByUuid() {
        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setUuid("SS-1012/2024-25/0019");
        
        List<ScorecardSurveyEntity> mockSurveys = Collections.singletonList(getValidSurveyEntity());
        when(surveyRepository.fetchSurveys(criteria)).thenReturn(mockSurveys);
        
        List<ScorecardSurveyEntity> result = scorecardSurveyService.searchSurveys(criteria);
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("SS-1012/2024-25/0019", result.get(0).getUuid());
    }

    @Test
    public void testSearchSurvey_ByTenantIdAndTitle() {
        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setTenantId("pb.testing");
        criteria.setTitle("survey1");
        
        List<ScorecardSurveyEntity> mockSurveys = Collections.singletonList(getValidSurveyEntity());
        when(surveyRepository.fetchSurveys(criteria)).thenReturn(mockSurveys);
        
        List<ScorecardSurveyEntity> result = scorecardSurveyService.searchSurveys(criteria);
        
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals("survey1", result.get(0).getSurveyTitle());
    }

    @Test
    public void testSearchSurvey_NoCriteria() {
        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        
        List<ScorecardSurveyEntity> result = scorecardSurveyService.searchSurveys(criteria);
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
    
    @Test
    public void testUpdateSurveyActive_WhenUuidIsNull_ShouldThrowException() {
        UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
        request.setUuid(null);
        request.setActive(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> scorecardSurveyService.updateSurveyActive(request));

        assertEquals("UUID must not be null or empty", exception.getMessage());
    }

    @Test
    public void testUpdateSurveyActive_WhenUuidIsEmpty_ShouldThrowException() {
        UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
        request.setUuid("");
        request.setActive(true);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> scorecardSurveyService.updateSurveyActive(request));

        assertEquals("UUID must not be null or empty", exception.getMessage());
    }

    @Test
    public void testUpdateSurveyActive_WhenActiveIsNull_ShouldThrowException() {
        UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
        request.setUuid("SS-1012/2024-25/000131");
        request.setActive(null);

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> scorecardSurveyService.updateSurveyActive(request));

        assertEquals("Active status must not be null", exception.getMessage());
    }

    @Test
    public void testUpdateSurveyActive_WhenUuidDoesNotExist_ShouldThrowException() {
        UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
        request.setUuid("SS-1012/2024-25/000999");
        request.setActive(true);

        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setUuid(request.getUuid());

        when(surveyRepository.fetchSurveys(criteria)).thenReturn(Collections.emptyList());

        Exception exception = assertThrows(IllegalArgumentException.class,
            () -> scorecardSurveyService.updateSurveyActive(request));

        assertEquals("UUID does not exist in database, Update failed!", exception.getMessage());
    }

    @Test
    public void testUpdateSurveyActive_WhenUuidExists_ShouldPublishToKafka() {
        UpdateSurveyActiveRequest request = new UpdateSurveyActiveRequest();
        request.setUuid("SS-1012/2024-25/000131");
        request.setActive(true);
        request.setRequestInfo(requestInfo);

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("SS-1012/2024-25/000131");

        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setUuid(request.getUuid());

        when(surveyRepository.fetchSurveys(criteria))
            .thenReturn(Collections.singletonList(surveyEntity));

        when(applicationProperties.getUpdateActiveSurveyTopic()).thenReturn("update-topic");

        scorecardSurveyService.updateSurveyActive(request);

        assertNotNull(request.getLastModifiedTime());

        verify(producer, times(1)).push("update-topic", request);
    }

    @Test
    public void testSearchSurvey_WithOpenSurveyFlag() {
        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setOpenSurveyFlag(true);

        List<ScorecardSurveyEntity> mockSurveys = Collections.singletonList(getValidSurveyEntity());
        when(surveyRepository.fetchSurveys(criteria)).thenReturn(mockSurveys);

        List<ScorecardSurveyEntity> result = scorecardSurveyService.searchSurveys(criteria);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    public void testSearchSurvey_WithoutOpenSurveyFlag() {
        ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();
        criteria.setOpenSurveyFlag(false);
        criteria.setTenantId("pb.testing");  // Ensure the repository method gets called

        List<ScorecardSurveyEntity> mockSurveys = Collections.singletonList(getValidSurveyEntity());
        when(surveyRepository.fetchSurveys(criteria)).thenReturn(mockSurveys);

        List<ScorecardSurveyEntity> result = scorecardSurveyService.searchSurveys(criteria);

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }


    /*** Helper Method to Create Valid Survey ***/
    private ScorecardSurveyEntity getValidSurveyEntity() {
        return ScorecardSurveyEntity.builder()
                .tenantId("pb.testing")
                .uuid("SS-1012/2024-25/0019")
                .surveyTitle("survey1")
                .surveyCategory("categorytest")
                .surveyDescription("survey about the citizen issues")
                .startDate(1745173800000L)
                .endDate(1771372800000L)
                .sections(Arrays.asList(
                        Section.builder()
                                .title("section1")
                                .weightage(new BigDecimal("50.00"))
                                .questions(Arrays.asList(
                                        QuestionWeightage.builder()
                                                .qorder(1L)
                                                .weightage(new BigDecimal("50.00"))
                                                .question(Question.builder()
                                                        .tenantId("pb.testing")
                                                        .questionStatement("How would you rate the service?")
                                                        .categoryId("3d75a2a5-33b9-4792-b948-087588a59b2f")
                                                        .type(Type.SHORT_ANSWER_TYPE)
                                                        .build())
                                                .build(),
                                        QuestionWeightage.builder()
                                                .qorder(2L)
                                                .weightage(new BigDecimal("50.00"))
                                                .question(Question.builder()
                                                        .tenantId("pb.testing")
                                                        .questionStatement("How would you rate the delivery?")
                                                        .categoryId("3d75a2a5-33b9-4792-b948-087588a59b2f")
                                                        .type(Type.SHORT_ANSWER_TYPE)
                                                        .build())
                                                .build()))
                                .build()
                ))
                .build();
    }

}


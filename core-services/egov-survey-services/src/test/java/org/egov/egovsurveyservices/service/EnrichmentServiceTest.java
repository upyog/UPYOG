package org.egov.egovsurveyservices.service;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EnrichmentServiceTest {

    @InjectMocks
    private EnrichmentService enrichmentService;

    @Mock
    private QuestionService questionService;

    private RequestInfo requestInfo;

    @BeforeEach
    void setUp() {
        requestInfo = RequestInfo.builder()
                .userInfo(User.builder()
                        .uuid("12345")
                        .name("Test User")
                        .build())
                .build();
    }

    @Test
    public void testEnrichSurveyEntity() {
        SurveyEntity surveyEntity = SurveyEntity.builder()
                .uuid("123")
                .build();
        List<Question> questions = new ArrayList<>();
        questions.add(Question.builder().build());
        questions.add(Question.builder().status(Status.ACTIVE).build());
        surveyEntity.setQuestions(questions);
        SurveyRequest surveyRequest = SurveyRequest.builder()
                .surveyEntity(surveyEntity)
                .requestInfo(requestInfo)
                .build();

        enrichmentService.enrichSurveyEntity(surveyRequest);

        assertEquals(Status.ACTIVE.toString(), surveyEntity.getStatus());
        assertTrue(surveyEntity.getActive());
        assertEquals("12345", surveyEntity.getAuditDetails().getCreatedBy());
        assertEquals("12345", surveyEntity.getAuditDetails().getLastModifiedBy());
        assertNotNull(surveyEntity.getAuditDetails().getCreatedTime());
        assertNotNull(surveyEntity.getAuditDetails().getLastModifiedTime());
        assertEquals("Test User", surveyEntity.getPostedBy());
        assertEquals(1L, surveyEntity.getQuestions().get(0).getQorder());
        assertEquals(2L, surveyEntity.getQuestions().get(1).getQorder());
        assertNotNull(surveyEntity.getQuestions().get(0).getUuid());
        assertNotNull(surveyEntity.getQuestions().get(1).getUuid());
        assertEquals(surveyEntity.getUuid(), surveyEntity.getQuestions().get(0).getSurveyId());
        assertEquals(surveyEntity.getUuid(), surveyEntity.getQuestions().get(1).getSurveyId());
        assertEquals(Status.ACTIVE, surveyEntity.getQuestions().get(0).getStatus());
        assertEquals("12345", surveyEntity.getQuestions().get(0).getAuditDetails().getCreatedBy());
        assertEquals("12345", surveyEntity.getQuestions().get(0).getAuditDetails().getLastModifiedBy());
        assertNotNull(surveyEntity.getQuestions().get(0).getAuditDetails().getCreatedTime());
        assertNotNull(surveyEntity.getQuestions().get(0).getAuditDetails().getLastModifiedTime());
    }

    @Test
    public void testEnrichAnswerEntity() {
        AnswerEntity answerEntity = AnswerEntity.builder()
                .build();
        List<Answer> answers = new ArrayList<>();
        answers.add(Answer.builder().build());
        answers.add(Answer.builder().build());
        answerEntity.setAnswers(answers);
        AnswerRequest answerRequest = AnswerRequest.builder()
                .answerEntity(answerEntity)
                .requestInfo(requestInfo)
                .build();

        enrichmentService.enrichAnswerEntity(answerRequest);

        assertNotNull(answerEntity.getAnswers().get(0).getAnswerUuid());
        assertNotNull(answerEntity.getAnswers().get(1).getAnswerUuid());
        assertEquals("12345", answerEntity.getAnswers().get(0).getCitizenId());
        assertEquals("12345", answerEntity.getAnswers().get(1).getCitizenId());
        assertEquals("12345", answerEntity.getAnswers().get(0).getAuditDetails().getCreatedBy());
        assertEquals("12345", answerEntity.getAnswers().get(0).getAuditDetails().getLastModifiedBy());
        assertNotNull(answerEntity.getAnswers().get(0).getAuditDetails().getCreatedTime());
        assertNotNull(answerEntity.getAnswers().get(0).getAuditDetails().getLastModifiedTime());
    }
    
    @Test
    void testEnrichScorecardSurveyEntity_InvalidSectionWeightage_ShouldThrowException() {
        Section section1 = new Section("section-001", "Section 1", new BigDecimal("10.00"), 1, new ArrayList<>());
        Section section2 = new Section("section-002", "Section 2", new BigDecimal("80.00"), 2, new ArrayList<>());
    	//Section section1 = Section.builder().uuid("section-001").title("Section 1");

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("survey-001");
        surveyEntity.setSections(Arrays.asList(section1, section2));

        ScorecardSurveyRequest request = new ScorecardSurveyRequest();
        request.setSurveyEntity(surveyEntity);
        request.setRequestInfo(requestInfo);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
            () -> enrichmentService.enrichScorecardSurveyEntity(request));

        assertEquals("Total section weightage in survey survey-001 must be 100, found: 90 after rounding off", thrown.getMessage());
    }
    
    @Test
    void testEnrichScorecardSurveyEntity_OverlimitInvalidSectionWeightage_ShouldThrowException() {
        Section section1 = new Section("section-001", "Section 1", new BigDecimal("20.00"), 1, new ArrayList<>());
        Section section2 = new Section("section-002", "Section 2", new BigDecimal("80.01"), 2, new ArrayList<>());

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("survey-001");
        surveyEntity.setSections(Arrays.asList(section1, section2));

        ScorecardSurveyRequest request = new ScorecardSurveyRequest();
        request.setSurveyEntity(surveyEntity);
        request.setRequestInfo(requestInfo);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, 
            () -> enrichmentService.enrichScorecardSurveyEntity(request));

        assertEquals("Total section weightage in survey survey-001 must be 100, found: 100.01", thrown.getMessage());
    }

    @Test
    void testEnrichScorecardSurveyEntity_InvalidQuestionWeightage_ShouldThrowException() {
        QuestionWeightage q1 = new QuestionWeightage("q-001", new Question(), new BigDecimal("10.00"));
        QuestionWeightage q2 = new QuestionWeightage("q-002", new Question(), new BigDecimal("70.00"));

        Section section = new Section("section-001", "Section 1", new BigDecimal("100.00"), 1, Arrays.asList(q1, q2));

        Question ques = Question.builder().questionStatement("ques").auditDetails(new AuditDetails()).uuid("q-001").build();
        QuestionResponse questionResponse = QuestionResponse.builder().questions(Collections.singletonList(ques)).build();

        when(questionService.searchQuestion(Mockito.any(QuestionSearchCriteria.class))).thenReturn(questionResponse);

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("survey-001");
        surveyEntity.setSections(Collections.singletonList(section));

        ScorecardSurveyRequest request = new ScorecardSurveyRequest();
        request.setSurveyEntity(surveyEntity);
        request.setRequestInfo(requestInfo);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
            () -> enrichmentService.enrichScorecardSurveyEntity(request));

        assertTrue(thrown.getMessage().contains("Total question weightage in section"));
    }
    
    @Test
    void testEnrichScorecardSurveyEntity_OverlimitInvalidQuestionWeightage_ShouldThrowException() {
        QuestionWeightage q1 = new QuestionWeightage("q-001", new Question(), new BigDecimal("30.30"));
        QuestionWeightage q2 = new QuestionWeightage("q-002", new Question(), new BigDecimal("70.10"));

        Section section = new Section("section-001", "Section 1", new BigDecimal("100.00"), 1, Arrays.asList(q1, q2));

        Question ques = Question.builder().questionStatement("ques").auditDetails(new AuditDetails()).uuid("q-001").build();
        QuestionResponse questionResponse = QuestionResponse.builder().questions(Collections.singletonList(ques)).build();

        when(questionService.searchQuestion(Mockito.any(QuestionSearchCriteria.class))).thenReturn(questionResponse);

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("survey-001");
        surveyEntity.setSections(Collections.singletonList(section));

        ScorecardSurveyRequest request = new ScorecardSurveyRequest();
        request.setSurveyEntity(surveyEntity);
        request.setRequestInfo(requestInfo);

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
            () -> enrichmentService.enrichScorecardSurveyEntity(request));

        assertTrue(thrown.getMessage().contains("Total question weightage in section"));
    }
    
    @Test
    void testEnrichScorecardSurveyEntity_ValidData() {
    	QuestionWeightage q1 = new QuestionWeightage("q-001", new Question(), new BigDecimal("40.00"));
        QuestionWeightage q2 = new QuestionWeightage("q-002", new Question(), new BigDecimal("60.00"));

        Section section1 = new Section("section-001", "Section 1", new BigDecimal("50.00"), 1, Arrays.asList(q1, q2));
        Section section2 = new Section("section-002", "Section 2", new BigDecimal("50.00"), 2, new ArrayList<>());

        Question ques = Question.builder().questionStatement("ques").auditDetails(new AuditDetails()).uuid("q-001").build();
        QuestionResponse questionResponse = QuestionResponse.builder().questions(Collections.singletonList(ques)).build();

        when(questionService.searchQuestion(Mockito.any(QuestionSearchCriteria.class)))
            .thenReturn(questionResponse);

        ScorecardSurveyEntity surveyEntity = new ScorecardSurveyEntity();
        surveyEntity.setUuid("survey-001");
        surveyEntity.setSections(Arrays.asList(section1, section2));

        ScorecardSurveyRequest request = new ScorecardSurveyRequest();
        request.setSurveyEntity(surveyEntity);
        request.setRequestInfo(requestInfo);

        assertDoesNotThrow(() -> enrichmentService.enrichScorecardSurveyEntity(request));

        assertEquals("survey-001", q1.getQuestion().getSurveyId());
        assertEquals(1L, q1.getQorder());
        assertEquals(2L, q2.getQorder());    
     }
    
}
package org.egov.egovsurveyservices.repository;

import org.egov.egovsurveyservices.repository.querybuilder.ScorecardSurveyQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.*;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ScorecardSurveyRepositoryTest {

    @InjectMocks
    private ScorecardSurveyRepository scorecardSurveyRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ScorecardSurveyRowMapper rowMapper;

    @Mock
    private ScorecardSurveyQueryBuilder surveyQueryBuilder;

    private ScorecardSurveyEntity surveyEntity;
    private ScorecardSurveySearchCriteria searchCriteria;

    @BeforeEach
    void setUp() {
        surveyEntity = ScorecardSurveyEntity.builder()
                .uuid("survey-uuid")
                .surveyTitle("Test Survey")
                .tenantId("default")
                .build();

        searchCriteria = ScorecardSurveySearchCriteria.builder().build();
    }

    @Test
    public void testFetchSurveys_Found() {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = "SELECT * FROM survey WHERE condition";
        when(surveyQueryBuilder.getSurveySearchQuery(searchCriteria, preparedStmtList)).thenReturn(query);

        List<ScorecardSurveyEntity> expectedSurveys = Collections.singletonList(surveyEntity);
        when(jdbcTemplate.query(eq(query), any(Object[].class), eq(rowMapper)))
                .thenReturn(expectedSurveys);

        List<ScorecardSurveyEntity> actualSurveys = scorecardSurveyRepository.fetchSurveys(searchCriteria);

        assertEquals(expectedSurveys, actualSurveys);
        verify(surveyQueryBuilder).getSurveySearchQuery(searchCriteria, preparedStmtList);
        verify(jdbcTemplate).query(query, preparedStmtList.toArray(), rowMapper);
    }

    @Test
    public void testFetchSurveys_NotFound() {
        List<Object> preparedStmtList = new ArrayList<>();

        String query = "SELECT * FROM survey WHERE condition";
        when(surveyQueryBuilder.getSurveySearchQuery(searchCriteria, preparedStmtList)).thenReturn(query);

        when(jdbcTemplate.query(eq(query), any(Object[].class), eq(rowMapper)))
                .thenReturn(Collections.emptyList());

        List<ScorecardSurveyEntity> actualSurveys = scorecardSurveyRepository.fetchSurveys(searchCriteria);

        assertEquals(Collections.emptyList(), actualSurveys);
        verify(surveyQueryBuilder).getSurveySearchQuery(searchCriteria, preparedStmtList);
        verify(jdbcTemplate).query(query, preparedStmtList.toArray(), rowMapper);
    }

    @Test
    public void testAllQuestionsExist_AllExist() {
        List<String> questionUuids = new ArrayList<>();
        questionUuids.add("uuid1");
        String query = "SELECT uuid FROM question WHERE uuid IN (?, ?, ?)";
        when(surveyQueryBuilder.allQuestionExistsQuery(anyString())).thenReturn(query);
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(RowMapper.class))).thenReturn(questionUuids);
        boolean result = scorecardSurveyRepository.allQuestionsExist(questionUuids);
        assertTrue(result);
    }

    @Test
    public void testAllQuestionsExist_NotAllExist() {
        List<String> questionUuids = Arrays.asList("uuid1", "uuid2", "uuid3");
        String placeholders = questionUuids.stream().map(uuid -> "?").collect(Collectors.joining(","));
        String query = "SELECT uuid FROM question WHERE uuid IN (" + placeholders + ")";
        when(surveyQueryBuilder.allQuestionExistsQuery(placeholders)).thenReturn(query);

        // Using any(RowMapper.class) here
        when(jdbcTemplate.query(eq(query), eq(questionUuids.toArray()), any(RowMapper.class))).thenReturn(Arrays.asList("uuid1", "uuid2"));

        boolean result = scorecardSurveyRepository.allQuestionsExist(questionUuids);

        assertFalse(result);
        verify(surveyQueryBuilder).allQuestionExistsQuery(placeholders);
        verify(jdbcTemplate).query(eq(query), eq(questionUuids.toArray()), any(RowMapper.class));
    }

    @Test
    public void testFetchSectionListBasedOnSurveyId_Success() {
        String surveyId = "survey1";
        String query = "SELECT * FROM section WHERE surveyid = ?";
        when(surveyQueryBuilder.fetchSectionListBasedOnSurveyId()).thenReturn(query);
        when(jdbcTemplate.query(eq(query), any(Object[].class), any(SectionRowMapper.class))).thenReturn(Collections.singletonList(Section.builder().build()));

        List<Section> result = scorecardSurveyRepository.fetchSectionListBasedOnSurveyId(surveyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surveyQueryBuilder).fetchSectionListBasedOnSurveyId();
        verify(jdbcTemplate).query(eq(query), any(Object[].class), any(SectionRowMapper.class));
    }

    @Test
    public void testFetchSectionListBasedOnSurveyId_NullSurveyId() {
        assertThrows(CustomException.class, () -> scorecardSurveyRepository.fetchSectionListBasedOnSurveyId(null));
    }

    @Test
    public void testFetchQuestionsWeightageListBySurveyAndSection_Success() {
        String surveyId = "survey1";
        String sectionId = "section1";
        String query = "SELECT * FROM question_weightage WHERE surveyid = ? AND sectionid = ?";
        when(surveyQueryBuilder.fetchQuestionsWeightageListBySurveyAndSection()).thenReturn(query);
        when(jdbcTemplate.query(eq(query), any(Object[].class), any(QuestionWeightageWithQuestionRowMapper.class))).thenReturn(Collections.singletonList(QuestionWeightage.builder().build()));

        List<QuestionWeightage> result = scorecardSurveyRepository.fetchQuestionsWeightageListBySurveyAndSection(surveyId, sectionId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surveyQueryBuilder).fetchQuestionsWeightageListBySurveyAndSection();
        verify(jdbcTemplate).query(eq(query), any(Object[].class), any(QuestionWeightageWithQuestionRowMapper.class));
    }

    @Test
    public void testFetchQuestionsWeightageListBySurveyAndSection_NullSurveyIdOrSectionId() {
        assertThrows(CustomException.class, () -> scorecardSurveyRepository.fetchQuestionsWeightageListBySurveyAndSection(null, "section1"));
        assertThrows(CustomException.class, () -> scorecardSurveyRepository.fetchQuestionsWeightageListBySurveyAndSection("survey1", null));
    }

    @Test
    public void testGetExistingAnswerUuid_Found() {
        String answerUuid = "answer1";
        String query = "SELECT uuid FROM answer WHERE uuid = ?";
        when(surveyQueryBuilder.getExistingAnswerUuid()).thenReturn(query);
        when(jdbcTemplate.queryForObject(eq(query), any(Object[].class), eq(String.class))).thenReturn(answerUuid);

        String result = scorecardSurveyRepository.getExistingAnswerUuid(answerUuid);

        assertEquals(answerUuid, result);
        verify(surveyQueryBuilder).getExistingAnswerUuid();
        verify(jdbcTemplate).queryForObject(eq(query), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetExistingAnswerUuid_NotFound() {
        String answerUuid = "answer1";
        String query = "SELECT uuid FROM answer WHERE uuid = ?";
        when(surveyQueryBuilder.getExistingAnswerUuid()).thenReturn(query);
        when(jdbcTemplate.queryForObject(eq(query), any(Object[].class), eq(String.class))).thenThrow(EmptyResultDataAccessException.class);

        String result = scorecardSurveyRepository.getExistingAnswerUuid(answerUuid);

        assertNull(result);
        verify(surveyQueryBuilder).getExistingAnswerUuid();
        verify(jdbcTemplate).queryForObject(eq(query), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetExistingAnswerUuid_SurveyResponseAndQuestion_Found() {
        String surveyResponseUuid = "surveyResponse1";
        String questionUuid = "question1";
        String answerUuid = "answer1";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn(answerUuid);

        String result = scorecardSurveyRepository.getExistingAnswerUuid(surveyResponseUuid, questionUuid);

        assertEquals(answerUuid, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetExistingAnswerUuid_SurveyResponseAndQuestion_NotFound() {
        String surveyResponseUuid = "surveyResponse1";
        String questionUuid = "question1";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenThrow(EmptyResultDataAccessException.class);

        String result = scorecardSurveyRepository.getExistingAnswerUuid(surveyResponseUuid, questionUuid);

        assertNull(result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetSurveyResponseUuidForAnswers_Found() {
        List<String> answerUuids = Arrays.asList("answer1", "answer2");
        String surveyResponseUuid = "surveyResponse1";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn(surveyResponseUuid);

        String result = scorecardSurveyRepository.getSurveyResponseUuidForAnswers(answerUuids);

        assertEquals(surveyResponseUuid, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetSurveyResponseUuidForAnswers_NotFound() {
        List<String> answerUuids = Arrays.asList("answer1", "answer2");
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenThrow(EmptyResultDataAccessException.class);

        String result = scorecardSurveyRepository.getSurveyResponseUuidForAnswers(answerUuids);

        assertNull(result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetExistingSurveyResponseUuid_Found() {
        String surveyUuid = "survey1";
        String citizenId = "citizen1";
        String tenantId = "tenant1";
        String surveyResponseUuid = "surveyResponse1";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenReturn(surveyResponseUuid);

        String result = scorecardSurveyRepository.getExistingSurveyResponseUuid(surveyUuid, citizenId, tenantId);

        assertEquals(surveyResponseUuid, result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetExistingSurveyResponseUuid_NotFound() {
        String surveyUuid = "survey1";
        String citizenId = "citizen1";
        String tenantId = "tenant1";
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(String.class))).thenThrow(EmptyResultDataAccessException.class);

        String result = scorecardSurveyRepository.getExistingSurveyResponseUuid(surveyUuid, citizenId, tenantId);

        assertNull(result);
        verify(jdbcTemplate).queryForObject(anyString(), any(Object[].class), eq(String.class));
    }

    @Test
    public void testGetAnswers_Found() {
        String surveyUuid = "survey1";
        String citizenId = "citizen1";
        String tenantId = "tenantId";
        String query = "SELECT * FROM answer WHERE surveyuuid = ? AND citizenid = ?";
        when(surveyQueryBuilder.getAnswers()).thenReturn(query);
        when(jdbcTemplate.query(eq(query), any(Object[].class), any(AnswerRowMapper.class))).thenReturn(Collections.singletonList(AnswerNew.builder().build()));

        List<AnswerNew> result = scorecardSurveyRepository.getAnswers(surveyUuid, citizenId, tenantId);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(surveyQueryBuilder).getAnswers();
        verify(jdbcTemplate).query(eq(query), any(Object[].class), any(AnswerRowMapper.class));
    }



}

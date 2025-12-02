package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.Category;
import org.egov.egovsurveyservices.web.models.ScorecardSurveyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.egovsurveyservices.repository.rowmapper.ScorecardSurveyRowMapper;
import org.egov.egovsurveyservices.web.models.*;


@ExtendWith(MockitoExtension.class)
public class ScorecardSurveyRowMapperTest {

    private ScorecardSurveyRowMapper rowMapper;

    @Mock
    private ResultSet resultSet;

    @BeforeEach
    void setUp() {
        rowMapper = new ScorecardSurveyRowMapper();
    }
        
    @Test
    void testExtractData_Success() throws SQLException {
        // Mock resultSet behavior
        when(resultSet.next()).thenReturn(true, false);

        // Survey details
        when(resultSet.getString("survey_uuid")).thenReturn("survey-123");
        when(resultSet.getString("survey_tenantid")).thenReturn("pb.testing");
        when(resultSet.getString("survey_title")).thenReturn("Customer Feedback");
        when(resultSet.getString("survey_category")).thenReturn("Service");
        when(resultSet.getString("survey_description")).thenReturn("Survey on service quality");
        when(resultSet.getLong("survey_startdate")).thenReturn(1672531200000L);
        when(resultSet.getLong("survey_enddate")).thenReturn(1675219600000L);
        when(resultSet.getString("survey_postedby")).thenReturn("admin");
        when(resultSet.getBoolean("survey_active")).thenReturn(true);
        when(resultSet.getLong("survey_answerscount")).thenReturn(50L);
        when(resultSet.getBoolean("survey_hasresponded")).thenReturn(false);
        when(resultSet.getString("survey_createdby")).thenReturn("system");
        when(resultSet.getString("survey_lastmodifiedby")).thenReturn("user1");
        when(resultSet.getLong("survey_createdtime")).thenReturn(1672531200000L);
        when(resultSet.getLong("survey_lastmodifiedtime")).thenReturn(1675219600000L);

        // Section details
        when(resultSet.getString("section_uuid")).thenReturn("section-123");
        when(resultSet.getString("section_title")).thenReturn("General Questions");
        when(resultSet.getBigDecimal("section_weightage")).thenReturn(new BigDecimal("10.00"));

        // Question details
        when(resultSet.getString("question_uuid")).thenReturn("question-123");
        when(resultSet.getString("question_surveyid")).thenReturn("survey-123");
        when(resultSet.getString("question_statement")).thenReturn("How satisfied are you?");
        when(resultSet.getString("question_type")).thenReturn("MCQ");
        when(resultSet.getString("question_status")).thenReturn("ACTIVE");
        when(resultSet.getBoolean("question_required")).thenReturn(true);
        when(resultSet.getString("question_categoryid")).thenReturn("cat-123");
        when(resultSet.getString("question_tenantid")).thenReturn("pb.testing");
        when(resultSet.getLong("question_createdtime")).thenReturn(1672531200000L);
        when(resultSet.getLong("question_lastmodifiedtime")).thenReturn(1675219600000L);
        when(resultSet.getString("question_createdby")).thenReturn("admin");
        when(resultSet.getString("question_lastmodifiedby")).thenReturn("user1");

        // Question Weightage details
        when(resultSet.getBigDecimal("question_weightage")).thenReturn(new BigDecimal("5.00"));
        when(resultSet.getLong("question_weightage_qorder")).thenReturn(1L);
        when(resultSet.getBoolean("question_weightage_required")).thenReturn(true);

        // Question Option details (Optional: Can be null if no options exist)
        when(resultSet.getString("option_uuid")).thenReturn("option-123", null);
        when(resultSet.getString("option_text")).thenReturn("Very Satisfied", null);
        when(resultSet.getDouble("option_weightage")).thenReturn(1.0, 0.0);
        when(resultSet.getLong("option_order")).thenReturn(1L);


        // Execute row mapper
        List<ScorecardSurveyEntity> result = rowMapper.extractData(resultSet);

        // Assertions
        assertNotNull(result);
        assertEquals(1, result.size());

        ScorecardSurveyEntity survey = result.get(0);
        assertEquals("survey-123", survey.getUuid());
        assertEquals("Customer Feedback", survey.getSurveyTitle());
        assertEquals("Service", survey.getSurveyCategory());
        assertEquals("Survey on service quality", survey.getSurveyDescription());
        assertEquals(1672531200000L, survey.getStartDate());
        assertEquals(1675219600000L, survey.getEndDate());
        assertEquals("admin", survey.getPostedBy());
        assertEquals(true, survey.getActive());
        assertEquals(50L, survey.getAnswersCount());
        assertEquals(false, survey.getHasResponded());

        assertEquals(1, survey.getSections().size());
        Section section = survey.getSections().get(0);
        assertEquals("section-123", section.getUuid());
        assertEquals("General Questions", section.getTitle());
        assertEquals(new BigDecimal("10.00"), section.getWeightage());

        assertEquals(1, section.getQuestions().size());
        QuestionWeightage questionWeightage = section.getQuestions().get(0);
        assertEquals("question-123", questionWeightage.getQuestionUuid());
        assertEquals(new BigDecimal("5.00"), questionWeightage.getWeightage());
        assertEquals(true, questionWeightage.getRequired());

        // Verify interactions
        verify(resultSet, atLeastOnce()).next();
        verify(resultSet, atLeastOnce()).getString("survey_uuid");
    }
    
    @Test
    void testExtractData_EmptyResultSet() throws SQLException {
        
        when(resultSet.next()).thenReturn(false);

        List<ScorecardSurveyEntity> result = rowMapper.extractData(resultSet);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testExtractData_ExceptionHandling() throws SQLException {
      
        when(resultSet.next()).thenThrow(new SQLException("Database Error"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> rowMapper.extractData(resultSet));

        assertTrue(exception.getMessage().contains("Error while extracting survey data"));
    }
}

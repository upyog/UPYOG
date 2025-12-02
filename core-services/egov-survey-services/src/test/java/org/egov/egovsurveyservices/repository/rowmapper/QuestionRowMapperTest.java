package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.Question;
import org.egov.egovsurveyservices.web.models.QuestionOption;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.egov.egovsurveyservices.web.models.enums.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionRowMapperTest {

    @Mock
    private ResultSet rs;

    @Test
    public void testExtractData_SingleQuestion1() throws SQLException, DataAccessException {
        String uuid = "test-uuid";
        String tenantId = "default";
        String surveyId = "survey-123";
        String questionStatement = "What is your name?";
        Status status = Status.ACTIVE;
        boolean required = true;
        String options = "option1,option2";
        Type type = Type.MULTIPLE_ANSWER_TYPE;
        String categoryId = "category-id";
        String categoryLabel = "Category Label";
        when(rs.getString("section_id")).thenReturn("1", "2","3","4");
        when(rs.getString("uuid")).thenReturn(uuid);
        when(rs.getString("tenantid")).thenReturn(tenantId);
        when(rs.getString("surveyid")).thenReturn(surveyId);
        when(rs.getString("questionstatement")).thenReturn(questionStatement);
        when(rs.getString("status")).thenReturn(status.toString());
        when(rs.getBoolean("required")).thenReturn(required);
        when(rs.getString("option_uuid")).thenReturn("optuuid1");
        when(rs.getString("option_text")).thenReturn("optext1");
        when(rs.getDouble("option_weightage")).thenReturn(2.0);
        when(rs.getLong("option_order")).thenReturn(1L);
        when(rs.getString("option_createdby")).thenReturn("admin1");
        when(rs.getLong("option_createdtime")).thenReturn(1L);
        when(rs.getString("option_lastmodifiedby")).thenReturn("admin1");
        when(rs.getLong("option_lastmodifiedtime")).thenReturn(1L);
        when(rs.getString("type")).thenReturn(type.toString());
        when(rs.getString("categoryid")).thenReturn(categoryId);
        when(rs.getString("category_id")).thenReturn(categoryId);
        when(rs.getString("category_label")).thenReturn(categoryLabel);
        when(rs.getString("createdby")).thenReturn("creator");
        when(rs.getLong("createdtime")).thenReturn(System.currentTimeMillis());
        when(rs.getString("lastmodifiedby")).thenReturn("creator");
        when(rs.getLong("lastmodifiedtime")).thenReturn(System.currentTimeMillis());
        when(rs.next()).thenReturn(true, false);
        QuestionRowMapper questionRowMapper = new QuestionRowMapper();
        List<Question> questions = questionRowMapper.extractData(rs);

        assertEquals(1, questions.size());

        Question question = questions.get(0);
        assertEquals(uuid, question.getUuid());
        assertEquals(tenantId, question.getTenantId());
        assertEquals(surveyId, question.getSurveyId());
        assertEquals(questionStatement, question.getQuestionStatement());
        assertEquals(status, question.getStatus());
        assertEquals(required, question.getRequired());
        assertEquals(type, question.getType());
        assertEquals(categoryId, question.getCategory().getId());
        assertEquals(categoryLabel, question.getCategory().getLabel());

        verify(rs,times(2)).getString("uuid");
        verify(rs).getString("tenantid");
        verify(rs).getString("surveyid");
        verify(rs).getString("questionstatement");
        verify(rs).getString("status");
        verify(rs).getBoolean("required");
        verify(rs).getString("type");
        verify(rs).getString("categoryid");
        verify(rs).getString("category_label");
    }
    
   
    @Test
    public void testExtractData_SQLException() throws SQLException {
        when(rs.next()).thenThrow(new SQLException("Database Error"));

        QuestionRowMapper questionRowMapper = new QuestionRowMapper();
        assertThrows(SQLException.class, () -> questionRowMapper.extractData(rs));
    }

    @Test
    public void testExtractData_NoRows() throws SQLException {
        when(rs.next()).thenReturn(false);

        QuestionRowMapper questionRowMapper = new QuestionRowMapper();
        List<Question> questions = questionRowMapper.extractData(rs);

        assertEquals(Collections.emptyList(), questions);
    }

    @Test
    public void testExtractData_NullCategoryId() throws SQLException, DataAccessException {
        String uuid = "test-uuid";
        String tenantId = "default";
        String surveyId = "survey-123";
        String questionStatement = "What is your name?";
        Status status = Status.ACTIVE;
        boolean required = true;
        Type type = Type.MULTIPLE_ANSWER_TYPE;

        when(rs.next()).thenReturn(true, false);
        when(rs.getString("uuid")).thenReturn(uuid);
        when(rs.getString("tenantid")).thenReturn(tenantId);
        when(rs.getString("surveyid")).thenReturn(surveyId);
        when(rs.getString("questionstatement")).thenReturn(questionStatement);
        when(rs.getString("status")).thenReturn(status.toString());
        when(rs.getBoolean("required")).thenReturn(required);
        when(rs.getString("type")).thenReturn(type.toString());
        when(rs.getString("categoryid")).thenReturn(null);
        when(rs.getString("category_id")).thenReturn(null);
        when(rs.getString("createdby")).thenReturn("creator");
        when(rs.getLong("createdtime")).thenReturn(1L);
        when(rs.getString("lastmodifiedby")).thenReturn("creator");
        when(rs.getLong("lastmodifiedtime")).thenReturn(1L);

        // Add stubs for option-related fields (even if null)
        lenient().when(rs.getString("option_uuid")).thenReturn(null);
        lenient().when(rs.getString("option_text")).thenReturn(null);
        lenient().when(rs.getDouble("option_weightage")).thenReturn(0.0);
        lenient().when(rs.getLong("option_order")).thenReturn(1L);
        lenient().when(rs.getString("option_createdby")).thenReturn(null);
        lenient().when(rs.getLong("option_createdtime")).thenReturn(0L);
        lenient().when(rs.getString("option_lastmodifiedby")).thenReturn(null);
        lenient().when(rs.getLong("option_lastmodifiedtime")).thenReturn(0L);

        QuestionRowMapper questionRowMapper = new QuestionRowMapper();
        List<Question> questions = questionRowMapper.extractData(rs);

        assertEquals(1, questions.size());
        assertNull(questions.get(0).getCategory());
        assertTrue(questions.get(0).getOptions().isEmpty());  // Ensure no options were mapped
    }

    
    @Test
    public void testExtractData_MultipleData() throws SQLException, DataAccessException {
        // Arrange
        String tenantId = "default";
        String surveyId = "survey-123";
        String questionStatement = "What is your name?";
        Status status = Status.ACTIVE;
        boolean required = true;
        Type type = Type.MULTIPLE_ANSWER_TYPE;

        when(rs.next()).thenReturn(true, true, true, false);

        when(rs.getString("section_id")).thenReturn("1", "2","3","4");
        when(rs.getString("uuid")).thenReturn("1", "1", "2", "2"); // Ensuring UUID consistency
        when(rs.getString("tenantid")).thenReturn(tenantId, tenantId, tenantId, tenantId);
        when(rs.getString("surveyid")).thenReturn(surveyId, surveyId, surveyId, surveyId);
        when(rs.getString("questionstatement")).thenReturn(questionStatement, questionStatement, "How old are you?", "How old are you?");
        when(rs.getString("status")).thenReturn(status.toString(), status.toString(), status.toString(), status.toString());
        when(rs.getBoolean("required")).thenReturn(required, required, required, required);
        when(rs.getString("type")).thenReturn(type.toString(), type.toString(), type.toString(), type.toString());
        when(rs.getString("category_id")).thenReturn("category1", "category1", "category2", "category2");
        when(rs.getString("categoryid")).thenReturn("someCategoryId1", "someCategoryId1", "someCategoryId2", "someCategoryId2");
        when(rs.getString("createdby")).thenReturn("user1", "user1", "user2", "user2");
        when(rs.getLong("createdtime")).thenReturn(1622547800000L, 1622547800000L, 1622547800001L, 1622547800001L);
        when(rs.getString("lastmodifiedby")).thenReturn("user2", "user2", "user3", "user3");
        when(rs.getLong("lastmodifiedtime")).thenReturn(1622547800000L, 1622547800000L, 1622547800001L, 1622547800001L);
        when(rs.getString("category_label")).thenReturn("Category 1", "Category 1", "Category 2", "Category 2");

        when(rs.getString("option_uuid")).thenReturn("option1", "option2", "option3", "option4");
        when(rs.getString("option_text")).thenReturn("Option 1", "Option 2", "Option 3", "Option 4");
        when(rs.getDouble("option_weightage")).thenReturn(10.0, 20.0, 30.0, 40.0);
        when(rs.getLong("option_order")).thenReturn(1L);
        when(rs.getString("option_createdby")).thenReturn("optionUser1", "optionUser2", "optionUser3", "optionUser4");
        when(rs.getLong("option_createdtime")).thenReturn(1622547800001L, 1622547800002L, 1622547800003L, 1622547800004L);
        when(rs.getString("option_lastmodifiedby")).thenReturn("optionUser3", "optionUser4", "optionUser5", "optionUser6");
        when(rs.getLong("option_lastmodifiedtime")).thenReturn(1622547800003L, 1622547800004L, 1622547800005L, 1622547800006L);

        QuestionRowMapper questionRowMapper = new QuestionRowMapper();
        List<Question> questions = questionRowMapper.extractData(rs);

        assertEquals(2, questions.size());

        Question question1 = questions.get(0);
        Question question2 = questions.get(1);

        assertEquals("1", question1.getUuid());
        assertEquals(tenantId, question1.getTenantId());
        assertEquals(surveyId, question1.getSurveyId());
        assertEquals(questionStatement, question1.getQuestionStatement());
        assertEquals(status, question1.getStatus());
        assertEquals(type, question1.getType());
        assertEquals("someCategoryId1", question1.getCategoryId());
        assertEquals("Category 1", question1.getCategory().getLabel());
        assertEquals("category1", question1.getCategory().getId());
        assertEquals(1, question1.getOptions().size());

        QuestionOption option1 = question1.getOptions().get(0);
        assertEquals("option1", option1.getUuid());
        assertEquals("Option 1", option1.getOptionText());
        assertEquals(10.0, option1.getWeightage());

        assertEquals("2", question2.getUuid());
        assertEquals("someCategoryId1", question2.getCategoryId());
        assertEquals("Category 1", question2.getCategory().getLabel());
        assertEquals("category1", question2.getCategory().getId());
        assertEquals(2, question2.getOptions().size());

        QuestionOption option3 = question2.getOptions().get(0);
        assertEquals("option2", option3.getUuid());
        assertEquals("Option 2", option3.getOptionText());
        assertEquals(20.0, option3.getWeightage());

        QuestionOption option4 = question2.getOptions().get(1);
        assertEquals("option3", option4.getUuid());
        assertEquals("Option 3", option4.getOptionText());
        assertEquals(30.0, option4.getWeightage());
    }

        @Test
        void testExtractData_SingleQuestionNoOptions() throws SQLException {
            when(rs.next()).thenReturn(true).thenReturn(false);
            when(rs.getString("uuid")).thenReturn("Q1");
            when(rs.getString("tenantid")).thenReturn("tenant1");
            when(rs.getString("surveyid")).thenReturn("survey1");
            when(rs.getString("questionstatement")).thenReturn("What is your name?");
            when(rs.getString("status")).thenReturn("ACTIVE");
            when(rs.getBoolean("required")).thenReturn(true);
            when(rs.getString("type")).thenReturn("TEXT");
            when(rs.getString("categoryid")).thenReturn("C1");
            when(rs.getString("category_id")).thenReturn("C1");
            when(rs.getString("category_label")).thenReturn("Personal Info");
            when(rs.getString("createdby")).thenReturn("admin");
            when(rs.getLong("createdtime")).thenReturn(123456789L);
            when(rs.getString("lastmodifiedby")).thenReturn("admin");
            when(rs.getLong("lastmodifiedtime")).thenReturn(987654321L);
            when(rs.getString("type")).thenReturn(Type.SHORT_ANSWER_TYPE.toString());

            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
            List<Question> questions = questionRowMapper.extractData(rs);

            assertNotNull(questions);
            assertEquals(1, questions.size());
            Question question = questions.get(0);
            assertEquals("Q1", question.getUuid());
            assertEquals("tenant1", question.getTenantId());
            assertEquals("survey1", question.getSurveyId());
            assertEquals("What is your name?", question.getQuestionStatement());
            assertEquals(Status.ACTIVE, question.getStatus());
            assertEquals(Type.SHORT_ANSWER_TYPE, question.getType());
            assertEquals("C1", question.getCategoryId());
            assertEquals("Personal Info", question.getCategory().getLabel());
        }
        
        @Test
        void testExtractData_SingleQuestion() throws SQLException {
            // Mock ResultSet behavior for a single question
            when(rs.next()).thenReturn(true, false);

            when(rs.getString("uuid")).thenReturn("Q1");
            when(rs.getString("tenantid")).thenReturn("tenant1");
            when(rs.getString("surveyid")).thenReturn("survey1");
            when(rs.getString("questionstatement")).thenReturn("What is your name?");
            when(rs.getString("status")).thenReturn("ACTIVE");
            when(rs.getString("type")).thenReturn("TEXT");
            when(rs.getString("categoryid")).thenReturn("C1");
            when(rs.getString("category_id")).thenReturn("C1");
            when(rs.getString("category_label")).thenReturn("Personal Info");
            when(rs.getString("createdby")).thenReturn("admin1");
            when(rs.getString("lastmodifiedby")).thenReturn("admin1");

            when(rs.getLong("createdtime")).thenReturn(123456789L);
            when(rs.getLong("lastmodifiedtime")).thenReturn(987654321L);

            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
            List<Question> questions = questionRowMapper.extractData(rs);

            assertNotNull(questions);
            assertEquals(1, questions.size(), "Expected one question, but got: " + questions.size());

            // Validate the question
            Question question = questions.get(0);
            assertEquals("Q1", question.getUuid());
            assertEquals("tenant1", question.getTenantId());
            assertEquals("survey1", question.getSurveyId());
            assertEquals("What is your name?", question.getQuestionStatement());
            assertEquals(Status.ACTIVE, question.getStatus());
            assertEquals("C1", question.getCategoryId());
            assertEquals("Personal Info", question.getCategory().getLabel());
        }
        
        @Test
        void testExtractData_QuestionWithOptions() throws SQLException {
            when(rs.next()).thenReturn(true, true, false);

            when(rs.getString("section_id")).thenReturn("1", "2","3","4");
            when(rs.getString("uuid")).thenReturn("Q1", "Q1");
            when(rs.getString("tenantid")).thenReturn("tenant1", "tenant1");
            when(rs.getString("surveyid")).thenReturn("survey1", "survey1");
            when(rs.getString("questionstatement")).thenReturn("What is your name?", "What is your name?");
            when(rs.getString("status")).thenReturn("ACTIVE", "ACTIVE");
            when(rs.getString("type")).thenReturn("TEXT", "TEXT");
            when(rs.getString("categoryid")).thenReturn("C1", "C1");
            when(rs.getString("category_id")).thenReturn("C1", "C1");
            when(rs.getString("category_label")).thenReturn("Personal Info", "Personal Info");
            when(rs.getString("createdby")).thenReturn("admin1", "admin1");
            when(rs.getString("lastmodifiedby")).thenReturn("admin1", "admin1");

            when(rs.getLong("createdtime")).thenReturn(123456789L, 123456789L);
            when(rs.getLong("lastmodifiedtime")).thenReturn(987654321L, 987654321L);

            // Mock Option Fields (Different for both rows)
            when(rs.getString("option_uuid")).thenReturn("O1", "O2");
            when(rs.getString("option_text")).thenReturn("Option 1", "Option 2");
            when(rs.getDouble("option_weightage")).thenReturn(1.0, 2.0);
            when(rs.getLong("option_order")).thenReturn(1L);
            when(rs.getString("option_createdby")).thenReturn("admin1", "admin2");
            when(rs.getLong("option_createdtime")).thenReturn(111111111L, 222222222L);
            when(rs.getString("option_lastmodifiedby")).thenReturn("admin3", "admin4");
            when(rs.getLong("option_lastmodifiedtime")).thenReturn(333333333L, 444444444L);

            // Execute row mapper
            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
            List<Question> questions = questionRowMapper.extractData(rs);

            // Assertions
            assertNotNull(questions);
            assertEquals(1, questions.size(), "Expected only one question");

            Question question = questions.get(0);
            assertEquals("Q1", question.getUuid());
            assertEquals("tenant1", question.getTenantId());
            assertEquals("survey1", question.getSurveyId());
            assertEquals("What is your name?", question.getQuestionStatement());
            assertEquals(Status.ACTIVE, question.getStatus());
            assertEquals("C1", question.getCategoryId());
            assertEquals("Personal Info", question.getCategory().getLabel());

            // Validate options
            assertNotNull(question.getOptions());
            assertEquals(2, question.getOptions().size(), "Expected two options");

            QuestionOption option1 = question.getOptions().get(0);
            assertEquals("O1", option1.getUuid());
            assertEquals("Option 1", option1.getOptionText());
            assertEquals(1.0, option1.getWeightage());

            QuestionOption option2 = question.getOptions().get(1);
            assertEquals("O2", option2.getUuid());
            assertEquals("Option 2", option2.getOptionText());
            assertEquals(2.0, option2.getWeightage());
        }
        
        @Test
        void testExtractData_NoOptions() throws SQLException {
            when(rs.next()).thenReturn(true).thenReturn(false);

            when(rs.getString("section_id")).thenReturn("1", "2","3","4");
            when(rs.getString("uuid")).thenReturn("Q1");
            when(rs.getString("tenantid")).thenReturn("tenant1");
            when(rs.getString("surveyid")).thenReturn("survey1");
            when(rs.getString("questionstatement")).thenReturn("What is your name?");
            when(rs.getString("status")).thenReturn("ACTIVE");
            when(rs.getBoolean("required")).thenReturn(true);
            when(rs.getString("type")).thenReturn("TEXT");
            when(rs.getString("categoryid")).thenReturn("C1");
            when(rs.getString("category_id")).thenReturn("C1");
            when(rs.getString("category_label")).thenReturn("Personal Info");
            when(rs.getString("createdby")).thenReturn("admin");
            when(rs.getLong("createdtime")).thenReturn(123456789L);
            when(rs.getString("lastmodifiedby")).thenReturn("admin");
            when(rs.getLong("lastmodifiedtime")).thenReturn(987654321L);
            
            // Stub options (no options case)
            when(rs.getString("option_uuid")).thenReturn(null);

            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
            List<Question> questions = questionRowMapper.extractData(rs);

            assertNotNull(questions);
            assertEquals(1, questions.size());
            assertTrue(questions.get(0).getOptions().isEmpty());
        }

//        @Test
//        void testExtractData_MissingCategoryFields() throws SQLException {
//            when(rs.next()).thenReturn(true).thenReturn(false);
//            when(rs.getString("uuid")).thenReturn("Q1");
//            when(rs.getString("category_id")).thenReturn(null);
//            when(rs.getString("category_label")).thenReturn(null);
//
//            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
//            List<Question> questions = questionRowMapper.extractData(rs);
//            assertNotNull(questions);
//            assertEquals(1, questions.size());
//            assertNull(questions.get(0).getCategory());
//        }
        
        @Test
        void testExtractData_MissingCategoryFields() throws SQLException {
            when(rs.next()).thenReturn(true).thenReturn(false); // Simulating a single row
            
            // Stubbing only the necessary fields
            when(rs.getString("uuid")).thenReturn("Q1");
            when(rs.getString("tenantid")).thenReturn("tenant1");
            when(rs.getString("surveyid")).thenReturn("survey1");
            when(rs.getString("questionstatement")).thenReturn("What is your name?");
            when(rs.getString("status")).thenReturn("ACTIVE");
            when(rs.getBoolean("required")).thenReturn(true);
            when(rs.getString("type")).thenReturn("TEXT");
            lenient().when(rs.getString("categoryid")).thenReturn(null); // Missing category fields
            lenient().when(rs.getString("category_id")).thenReturn(null);
            lenient().when(rs.getString("category_label")).thenReturn(null);
            
            // Mock audit details
            lenient().when(rs.getString("createdby")).thenReturn("admin");
            lenient().when(rs.getLong("createdtime")).thenReturn(123456789L);
            lenient().when(rs.getString("lastmodifiedby")).thenReturn("admin");
            lenient().when(rs.getLong("lastmodifiedtime")).thenReturn(987654321L);
            
         
            lenient().when(rs.getString("option_uuid")).thenReturn(null);

            QuestionRowMapper questionRowMapper = new QuestionRowMapper();
            List<Question> questions = questionRowMapper.extractData(rs);

            assertNotNull(questions);
            assertEquals(1, questions.size());
            assertNull(questions.get(0).getCategory()); // Ensure category remains null
        }

}

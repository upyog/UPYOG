package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.XSlf4j;
import org.egov.egovsurveyservices.repository.querybuilder.QuestionQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.CategoryRowMapper;
import org.egov.egovsurveyservices.repository.rowmapper.QuestionRowMapper;
import org.egov.egovsurveyservices.web.models.Question;
import org.egov.egovsurveyservices.web.models.QuestionSearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionRepositoryTest {

    @InjectMocks
    private QuestionRepository questionRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private QuestionRowMapper rowMapper;

    @Mock
    private QuestionQueryBuilder questionQueryBuilder;

    private Question question;
    private QuestionSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        question = new Question();
        question.setUuid("test-uuid");
        criteria = new QuestionSearchCriteria();
    }

    @Test
    public void testGetQuestionById_Found() {
        List<Question> expectedQuestions = Collections.singletonList(question);
        String sql = "SELECT * FROM questions WHERE uuid = ?";
        when(questionQueryBuilder.getQuestionByIdSql()).thenReturn(sql);
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(QuestionRowMapper.class)))
                .thenReturn(expectedQuestions);

        List<Question> actualQuestions = questionRepository.getQuestionById("test-uuid");

        assertEquals(expectedQuestions, actualQuestions);
        verify(jdbcTemplate).query(sql, new Object[]{"test-uuid"}, rowMapper);
    }

    @Test
    public void testGetQuestionById_NotFound() {
        String sql = "SELECT * FROM questions WHERE uuid = ?";
        when(questionQueryBuilder.getQuestionByIdSql()).thenReturn(sql);
        when(jdbcTemplate.query(anyString(), any(Object[].class), any(QuestionRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<Question> actualQuestions = questionRepository.getQuestionById("test-uuid");

        assertEquals(Collections.emptyList(), actualQuestions);
        verify(jdbcTemplate).query(sql, new Object[]{"test-uuid"}, rowMapper);
    }

    @Test
    public void testFetchQuestions_WithCriteria() {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE criteria = ?";
        when(questionQueryBuilder.getQuestionSearchQuery(criteria, preparedStmtList)).thenReturn(query);
        List<Question> expectedQuestions = Collections.singletonList(question);
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(QuestionRowMapper.class)))
                .thenReturn(expectedQuestions);

        List<Question> actualQuestions = questionRepository.fetchQuestions(criteria);

        assertEquals(expectedQuestions, actualQuestions);
        verify(questionQueryBuilder).getQuestionSearchQuery(criteria, preparedStmtList);
        verify(jdbcTemplate).query(query, preparedStmtList.toArray(), rowMapper);
    }
}
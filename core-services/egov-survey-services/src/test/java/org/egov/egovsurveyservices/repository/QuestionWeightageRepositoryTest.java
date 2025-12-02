package org.egov.egovsurveyservices.repository;

import org.egov.egovsurveyservices.repository.querybuilder.QuestionWeightageQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class QuestionWeightageRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    private QuestionWeightageRepository repository;

    @Test
    void testGetQuestionWeightage_ReturnsExpectedWeightage() {
        // Arrange
        String questionUuid = "q1";
        String sectionUuid = "s1";
        BigDecimal expectedWeightage = new BigDecimal("25.0");

        when(jdbcTemplate.queryForObject(
            eq(QuestionWeightageQueryBuilder.QUESTION_WEIGHTAGE_QUERY),
            any(Object[].class),
            eq(BigDecimal.class)
        )).thenReturn(expectedWeightage);

        // Act
        BigDecimal actualWeightage = repository.getQuestionWeightage(questionUuid, sectionUuid);

        // Assert
        assertNotNull(actualWeightage);
        assertEquals(expectedWeightage, actualWeightage);
    }

    @Test
    void testGetQuestionWeightage_WhenQueryReturnsNull() {
        String questionUuid = "q2";
        String sectionUuid = "s2";

        when(jdbcTemplate.queryForObject(
            eq(QuestionWeightageQueryBuilder.QUESTION_WEIGHTAGE_QUERY),
            any(Object[].class),
            eq(BigDecimal.class)
        )).thenReturn(null);

        BigDecimal result = repository.getQuestionWeightage(questionUuid, sectionUuid);

        assertNull(result);
    }
}

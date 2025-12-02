package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.QuestionWeightage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuestionWeightageWithQuestionRowMapperTest {

    private QuestionWeightageWithQuestionRowMapper rowMapper;

    @BeforeEach
    void setUp() {
        rowMapper = new QuestionWeightageWithQuestionRowMapper();
    }

    @Test
    void testExtractData_WithSingleRowAndOption() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, false);

        when(rs.getString("uuid")).thenReturn("q1");
        when(rs.getString("questionuuid")).thenReturn("q1");
        when(rs.getString("sectionuuid")).thenReturn("s1");
        when(rs.getBigDecimal("weightage")).thenReturn(BigDecimal.TEN);
        when(rs.getLong("qorder")).thenReturn(1L);
        when(rs.getString("questionstatement")).thenReturn("How satisfied are you?");
        when(rs.getString("status")).thenReturn("ACTIVE");
        when(rs.getString("type")).thenReturn("SHORT_ANSWER_TYPE");
        when(rs.getBoolean("required")).thenReturn(true);
        when(rs.getString("createdby")).thenReturn("admin");
        when(rs.getString("lastmodifiedby")).thenReturn("admin");
        when(rs.getLong("createdtime")).thenReturn(1L);
        when(rs.getLong("lastmodifiedtime")).thenReturn(2L);

        // Option fields
        when(rs.getString("option_uuid")).thenReturn("opt1");
        when(rs.getString("option_text")).thenReturn("Yes");
        when(rs.getDouble("option_weightage")).thenReturn(1.0);
        when(rs.getLong("option_order")).thenReturn(1L);
        when(rs.getString("option_createdby")).thenReturn("admin");
        when(rs.getString("option_lastmodifiedby")).thenReturn("admin");
        when(rs.getLong("option_createdtime")).thenReturn(3L);
        when(rs.getLong("option_lastmodifiedtime")).thenReturn(4L);

        List<QuestionWeightage> result = rowMapper.extractData(rs);

        assertEquals(1, result.size());
        QuestionWeightage qWeightage = result.get(0);
        assertEquals("q1", qWeightage.getQuestionUuid());
        assertEquals("s1", qWeightage.getSectionUuid());
        assertNotNull(qWeightage.getQuestion());
        assertEquals(1, qWeightage.getQuestion().getOptions().size());
        assertEquals("opt1", qWeightage.getQuestion().getOptions().get(0).getUuid());
    }
}

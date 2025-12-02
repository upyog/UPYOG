package org.egov.egovsurveyservices.repository;

import org.egov.egovsurveyservices.repository.querybuilder.SectionQueryBuilder;
import org.egov.egovsurveyservices.web.models.Section;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SectionRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private SectionQueryBuilder sectionQueryBuilder;

    @InjectMocks
    private SectionRepository sectionRepository;

    @Test
    void testGetSectionsBySurveyId() {
        String surveyId = "survey-123";
        String sqlQuery = "SELECT * FROM sections WHERE survey_id = ?";

        List<Section> mockSections = Collections.singletonList(new Section("uuid-1", null, new BigDecimal("50.0"), null, null));

        when(sectionQueryBuilder.getSectionBySurveyUuid()).thenReturn(sqlQuery);
        when(jdbcTemplate.query(eq(sqlQuery), any(RowMapper.class), eq(surveyId))).thenReturn(mockSections);

        List<Section> result = sectionRepository.getSectionsBySurveyId(surveyId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("uuid-1", result.get(0).getUuid());
    }

    @Test
    void testGetSectionById() {
        String sectionId = "section-321";
        String sqlQuery = "SELECT * FROM sections WHERE section_id = ?";

        Section mockSection = new Section("uuid-321",null, new BigDecimal("75.0"),null,null);

        when(sectionQueryBuilder.getSectionBySectionUuid()).thenReturn(sqlQuery);
        when(jdbcTemplate.queryForObject(eq(sqlQuery), any(RowMapper.class), eq(sectionId))).thenReturn(mockSection);

        Section result = sectionRepository.getSectionById(sectionId);

        assertNotNull(result);
        assertEquals("uuid-321", result.getUuid());
        assertEquals(new BigDecimal("75.0"), result.getWeightage());
    }
}

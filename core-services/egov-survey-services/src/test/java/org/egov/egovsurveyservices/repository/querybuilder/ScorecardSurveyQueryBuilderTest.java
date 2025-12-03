package org.egov.egovsurveyservices.repository.querybuilder;

import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
import org.egov.egovsurveyservices.web.models.ScorecardSurveySearchCriteria;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ScorecardSurveyQueryBuilderTest {

    @InjectMocks
    private ScorecardSurveyQueryBuilder queryBuilder;

    @Mock
    private ApplicationProperties config;

    private List<Object> preparedStmtList;
    private ScorecardSurveySearchCriteria criteria;

    @BeforeEach
    void setUp() {
        preparedStmtList = new ArrayList<>();
        criteria = new ScorecardSurveySearchCriteria();
    }

    @Test
    public void testGetSurveySearchQuery_WithUuid() {
        criteria.setUuid("test-uuid");
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("WHERE survey.uuid = ?"));
        assertEquals(1, preparedStmtList.size());
        assertEquals("test-uuid", preparedStmtList.get(0));
    }

    @Test
    public void testGetSurveySearchQuery_WithTenantId() {
        criteria.setTenantId("test-tenant");
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("WHERE ( survey.tenantid = ? or survey.tenantid = 'pb.punjab' )"));
        assertEquals(1, preparedStmtList.size());
        assertEquals("test-tenant", preparedStmtList.get(0));
    }

    @Test
    public void testGetSurveySearchQuery_WithTitle() {
        criteria.setTitle("test-title");
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("survey.title ILIKE ?"));
        assertEquals(1, preparedStmtList.size());
        assertEquals("%test-title%", preparedStmtList.get(0));
    }

    @Test
    public void testGetSurveySearchQuery_WithMultipleCriteria() {
        criteria.setUuid("test-uuid");
        criteria.setTenantId("test-tenant");
        criteria.setTitle("test-title");

        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("WHERE survey.uuid = ?"));
        assertTrue(query.contains("AND ( survey.tenantid = ? or survey.tenantid = 'pb.punjab' )"));
        assertTrue(query.contains("AND survey.title ILIKE ?"));
        assertEquals(3, preparedStmtList.size());
        assertEquals("test-uuid", preparedStmtList.get(0));
        assertEquals("test-tenant", preparedStmtList.get(1));
        assertEquals("%test-title%", preparedStmtList.get(2));
    }

    @Test
    public void testGetSurveyUuidsToCountMapQuery() {
        List<String> surveyIds = Arrays.asList("id1", "id2");
        String query = queryBuilder.getSurveyUuidsToCountMapQuery(surveyIds, preparedStmtList);

        assertTrue(query.contains("WHERE answer.surveyid IN"));
        assertTrue(query.contains("GROUP  BY surveyid"));
        assertEquals(2, preparedStmtList.size());
        assertEquals("id1", preparedStmtList.get(0));
        assertEquals("id2", preparedStmtList.get(1));
    }
    
    @Test
    public void testGetSurveySearchQuery_WithActiveTrue() {
        criteria.setActive(true);
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("survey.active = ?"));
        assertEquals(1, preparedStmtList.size());
        assertEquals(true, preparedStmtList.get(0));
    }

    @Test
    public void testGetSurveySearchQuery_WithActiveFalse() {
        criteria.setActive(false);
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("survey.active = ?"));
        assertEquals(1, preparedStmtList.size());
        assertEquals(false, preparedStmtList.get(0));
    }

    @Test
    public void testGetSurveySearchQuery_WithoutActiveFilter() {
        criteria.setActive(null);  // No active filter applied
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertFalse(query.contains("survey.active = ?")); // Active filter should not be present
        assertEquals(0, preparedStmtList.size()); // No parameters should be added
    }
    
    @Test
    public void testGetSurveySearchQuery_WithOpenSurveyFlag() {
        criteria.setOpenSurveyFlag(true);
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertTrue(query.contains("? BETWEEN survey.startdate AND survey.enddate"));

        assertEquals(1, preparedStmtList.size()); // Only one value (System.currentTimeMillis()) is added
    }


    @Test
    public void testGetSurveySearchQuery_WithoutOpenSurveyFlag() {
        criteria.setOpenSurveyFlag(false); // Should not add the open survey filter
        String query = queryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        assertFalse(query.contains("? BETWEEN survey.startdate AND survey.enddate"));

        // Ensure no extra parameters are added
        assertEquals(0, preparedStmtList.size());
    }


}
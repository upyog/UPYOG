package org.egov.egovsurveyservices.repository.querybuilder;

import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryQueryBuilderTest {

    @Mock
    private CategorySearchCriteria criteria;

    @InjectMocks
    private CategoryQueryBuilder queryBuilder;

    @Test
    public void testGetCategorySearchQuery_withAllCriteria() {

        String tenantId = "default";
        String id = "category-id";
        String label = "category label";
        String createdBy = "creator";
        boolean isActive = true;
        int pageNumber = 1;
        int pageSize = 10;

        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(tenantId);
        when(criteria.getId()).thenReturn(id);
        when(criteria.getLabel()).thenReturn(label);
        when(criteria.getCreatedBy()).thenReturn(createdBy);
        when(criteria.getIsActive()).thenReturn(isActive);
        when(criteria.getPageNumber()).thenReturn(pageNumber);
        when(criteria.getSize()).thenReturn(pageSize);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category WHERE  (category.tenantid = ? or category.tenantid = 'pb.punjab') AND  category.id = ?  AND  category.label ilike '%category label%' AND  category.createdby = ?  AND  category.isactive = ?  ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";
        assertEquals(expectedQuery, query);
        assertEquals(4, preparedStmtList.size());
        assertEquals(tenantId, preparedStmtList.get(0));
        assertEquals(id, preparedStmtList.get(1));
        assertEquals(createdBy, preparedStmtList.get(2));
        assertEquals(isActive, preparedStmtList.get(3));
    }

    @Test
    public void testGetCategorySearchQuery_withEmptyCriteria() {

        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getId()).thenReturn(null);
        when(criteria.getLabel()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getIsActive()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(0, preparedStmtList.size());
    }

    @Test
    public void testGetCategorySearchQuery_withTenantId() {

        String tenantId = "default";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(tenantId);
        when(criteria.getId()).thenReturn(null);
        when(criteria.getLabel()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getIsActive()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category WHERE  (category.tenantid = ? or category.tenantid = 'pb.punjab') ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(tenantId, preparedStmtList.get(0));
    }

    @Test
    public void testGetCategorySearchQuery_withId() {

        String id = "category-id";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getId()).thenReturn(id);
        when(criteria.getLabel()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getIsActive()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category WHERE  category.id = ?  ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(id, preparedStmtList.get(0));
    }

    @Test
    public void testGetCategorySearchQuery_withLabel() {

        String label = "category label";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getId()).thenReturn(null);
        when(criteria.getLabel()).thenReturn(label);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getIsActive()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category WHERE  category.label ilike '%category label%' ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(0, preparedStmtList.size());
    }

    @Test
    public void testGetCategorySearchQuery_withCreatedBy() {

        String createdBy = "creator";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getId()).thenReturn(null);
        when(criteria.getLabel()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(createdBy);
        when(criteria.getIsActive()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getCategorySearchQuery(criteria, preparedStmtList);

        String expectedQuery = "SELECT category.id, category.label, category.isactive, category.tenantid, category.createdby, category.lastmodifiedby, category.createdtime, category.lastmodifiedtime  FROM eg_ss_category category WHERE  category.createdby = ?  ORDER BY category.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(createdBy, preparedStmtList.get(0));
    }

    @Test
    public void testGetCheckDuplicateCategory() {
        assertEquals("SELECT COUNT(*) FROM eg_ss_category WHERE label = ? AND tenantid = ? AND id != ?", queryBuilder.getCheckDuplicateCategory());
    }

    @Test
    public void testGetIsUniqueCategorySql() {
        assertEquals("SELECT COUNT(*) FROM eg_ss_category WHERE label ILIKE ? AND tenantid = ?", queryBuilder.getIsUniqueCategorySql());
    }

    @Test
    public void testExistsById() {
        assertEquals("SELECT COUNT(*) FROM eg_ss_category WHERE id = ?", queryBuilder.existById());
    }
}
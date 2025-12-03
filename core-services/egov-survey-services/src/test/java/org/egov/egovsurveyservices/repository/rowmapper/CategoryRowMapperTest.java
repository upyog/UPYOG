package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import java.sql.ResultSet;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryRowMapperTest {

    @Mock
    private ResultSet rs;

    @Test
    public void testExtractData_withSingleRecord() throws SQLException, DataAccessException {

        when(rs.next()).thenReturn(true,false);
        when(rs.getString("id")).thenReturn("category-id");
        when(rs.getString("label")).thenReturn("category label");
        when(rs.getBoolean("isactive")).thenReturn(true);
        when(rs.getString("tenantid")).thenReturn("default");
        when(rs.getString("createdby")).thenReturn("creator");
        when(rs.getLong("createdtime")).thenReturn(1234567L);
        when(rs.getString("lastmodifiedby")).thenReturn("modifier");
        when(rs.getLong("lastmodifiedtime")).thenReturn(7890123L);

        CategoryRowMapper rowMapper = new CategoryRowMapper();
        List<Category> categories = rowMapper.extractData(rs);

        assertEquals(1, categories.size());

        Category expectedCategory = Category.builder()
                .id("category-id")
                .label("category label")
                .isActive(true)
                .tenantId("default")
                .auditDetails(AuditDetails.builder()
                        .createdBy("creator")
                        .createdTime(1234567L)
                        .lastModifiedBy("modifier")
                        .lastModifiedTime(7890123L)
                        .build())
                .build();

        assertEquals(expectedCategory, categories.get(0));
    }

    @Test
    public void testExtractData_withMultipleRecords() throws SQLException, DataAccessException {

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getString("id")).thenReturn("category-id-1","category-id-2");
        when(rs.getString("label")).thenReturn("category label 1","category label 2");
        when(rs.getBoolean("isactive")).thenReturn(true,false);
        when(rs.getString("tenantid")).thenReturn("default","other");
        when(rs.getString("createdby")).thenReturn("creator1","creator2");
        when(rs.getLong("createdtime")).thenReturn(1234567L,4567890L);
        when(rs.getString("lastmodifiedby")).thenReturn("modifier1","modifier2");
        when(rs.getLong("lastmodifiedtime")).thenReturn(7890123L,0123456L);

        CategoryRowMapper rowMapper = new CategoryRowMapper();
        List<Category> categories = rowMapper.extractData(rs);

        assertEquals(2, categories.size());
    }


    @Test
    public void testExtractData_withDuplicateRecords() throws SQLException, DataAccessException {

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getString("id")).thenReturn("category-id-1","category-id-1");
        when(rs.getString("label")).thenReturn("category label 1","category label 2");
        when(rs.getBoolean("isactive")).thenReturn(true,false);
        when(rs.getString("tenantid")).thenReturn("default","other");
        when(rs.getString("createdby")).thenReturn("creator1","creator2");
        when(rs.getLong("createdtime")).thenReturn(1234567L,4567890L);
        when(rs.getString("lastmodifiedby")).thenReturn("modifier1","modifier2");
        when(rs.getLong("lastmodifiedtime")).thenReturn(7890123L,0123456L);

        CategoryRowMapper rowMapper = new CategoryRowMapper();
        List<Category> categories = rowMapper.extractData(rs);

        assertEquals(1, categories.size());
    }

    @Test
    public void testExtractData_withNoRecords() throws SQLException, DataAccessException {
        CategoryRowMapper rowMapper = new CategoryRowMapper();
        ResultSet rs = Mockito.mock(ResultSet.class);

        when(rs.next()).thenReturn(false);

        List<Category> categories = rowMapper.extractData(rs);

        assertEquals(0, categories.size());
    }
}
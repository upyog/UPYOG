package org.egov.egovsurveyservices.repository;

import org.egov.egovsurveyservices.repository.querybuilder.CategoryQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.CategoryRowMapper;
import org.egov.egovsurveyservices.web.models.Category;
import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryRepositoryTest {

    @InjectMocks
    private CategoryRepository categoryRepository;

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private CategoryRowMapper rowMapper;

    @Mock
    private CategoryQueryBuilder categoryQueryBuilder;

    private Category category;
    private CategorySearchCriteria criteria;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("category-id")
                .label("Test Category")
                .tenantId("default")
                .build();
        criteria = CategorySearchCriteria.builder().build();
    }

    @Test
    public void testGetCategoryById_Found() {
        List<Category> expectedCategories = Collections.singletonList(category);
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(CategoryRowMapper.class)))
                .thenReturn(expectedCategories);

        List<Category> actualCategories = categoryRepository.getCategoryById("category-id");

        assertEquals(expectedCategories, actualCategories);
        verify(jdbcTemplate).query(any(String.class), any(Object[].class), any(CategoryRowMapper.class));
    }

    @Test
    public void testGetCategoryById_NotFound() {
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(CategoryRowMapper.class)))
                .thenReturn(Collections.emptyList());

        List<Category> actualCategories = categoryRepository.getCategoryById("category-id");

        assertEquals(Collections.emptyList(), actualCategories);
        verify(jdbcTemplate).query(any(String.class), any(Object[].class), any(CategoryRowMapper.class));
    }

    @Test
    public void testFetchCategories_WithCriteria() {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = "search query";
        when(categoryQueryBuilder.getCategorySearchQuery(criteria, preparedStmtList)).thenReturn(query);

        List<Category> expectedCategories = Collections.singletonList(category);
        when(jdbcTemplate.query(any(String.class), any(Object[].class), any(CategoryRowMapper.class)))
                .thenReturn(expectedCategories);

        List<Category> actualCategories = categoryRepository.fetchCategories(criteria);

        assertEquals(expectedCategories, actualCategories);
        verify(categoryQueryBuilder).getCategorySearchQuery(criteria, preparedStmtList);
        verify(jdbcTemplate).query(query, preparedStmtList.toArray(), rowMapper);
    }

    @Test
    public void testFetchDuplicateCategoryCount() {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add("Test Category");
        preparedStmtList.add("default");
        preparedStmtList.add("category-id");
        String query = "duplicate check query";
        when(categoryQueryBuilder.getCheckDuplicateCategory()).thenReturn(query);
        when(jdbcTemplate.queryForObject(any(String.class), any(Object[].class), eq(Integer.class))).thenReturn(1);

        int count = categoryRepository.fetchDuplicateCategoryCount(category);

        assertEquals(1, count);
        verify(categoryQueryBuilder).getCheckDuplicateCategory();
        verify(jdbcTemplate).queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    @Test
    public void testIsCategoryUnique_Unique() {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add("Test Category");
        preparedStmtList.add("default");
        String query = "is unique query";
        when(categoryQueryBuilder.getIsUniqueCategorySql()).thenReturn(query);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(0);

        int count = categoryRepository.isCategoryUnique(category);

        assertEquals(0, count);
        verify(categoryQueryBuilder).getIsUniqueCategorySql();
        verify(jdbcTemplate).queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    @Test
    public void testIsCategoryUnique_NotUnique() {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add("Test Category");
        preparedStmtList.add("default");
        String query = "is unique query";
        when(categoryQueryBuilder.getIsUniqueCategorySql()).thenReturn(query);
        when(jdbcTemplate.queryForObject(anyString(), any(Object[].class), eq(Integer.class))).thenReturn(1);

        Integer categoryUnique = categoryRepository.isCategoryUnique(category);

        assertNotEquals(0, (int) categoryUnique);
        verify(categoryQueryBuilder).getIsUniqueCategorySql();
        verify(jdbcTemplate).queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    @Test
    public void testIsCategoryExists() {
        String query = "exists by id";
        when(categoryQueryBuilder.existById()).thenReturn(query);
        when(jdbcTemplate.queryForObject(anyString(), eq(Integer.class),anyString())).thenReturn(1);

        Integer exists = categoryRepository.existsById("123");

        assertNotEquals(0, (int) exists);
        verify(categoryQueryBuilder).existById();
        verify(jdbcTemplate).queryForObject(query,Integer.class,"123");
    }
}
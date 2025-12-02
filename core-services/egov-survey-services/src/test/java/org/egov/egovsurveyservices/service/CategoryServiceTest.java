package org.egov.egovsurveyservices.service;

import com.google.gson.Gson;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.producer.Producer;
import org.egov.egovsurveyservices.repository.CategoryRepository;
import org.egov.egovsurveyservices.validators.CategoryValidator;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryValidator categoryValidator;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private Producer producer;

    @Mock
    private ApplicationProperties applicationProperties;

    private RequestInfo requestInfo;
    private Gson gson;

    @BeforeEach
    void setUp() {
        requestInfo = RequestInfo.builder()
                .userInfo(User.builder().uuid("1").build())
                .build();
        gson = new Gson();
    }

    @Test
    public void testCreateCategory_Success() {
        when(applicationProperties.getMaxCreateLimit()).thenReturn(5);
        Category category = Category.builder()
                .label("Test Category")
                .tenantId("default")
                .isActive(true)
                .build();
        List<Category> categories = Collections.singletonList(category);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();

        when(categoryValidator.isCategoryUnique(category)).thenReturn(true);
        when(applicationProperties.getSaveCategoryTopic()).thenReturn("save-category");

        CategoryResponse response = categoryService.createCategory(categoryRequest);

        verify(categoryValidator).isCategoryUnique(category);
        verify(producer).push(anyString(), any(Object.class));
        assertNotNull(response);
        assertNotNull(response.getCategories().get(0).getId());
    }

    @Test
    public void testCreateCategory_NonUniqueLabel() {
        when(applicationProperties.getMaxCreateLimit()).thenReturn(5);

        Category category = Category.builder()
                .label("Existing Label")
                .tenantId("default")
                .isActive(true)
                .build();
        List<Category> categories = Collections.singletonList(category);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();

        when(categoryValidator.isCategoryUnique(category)).thenReturn(false);

        assertThrows(CustomException.class, () -> categoryService.createCategory(categoryRequest));

        verify(categoryValidator).isCategoryUnique(category);
        verifyNoInteractions(producer);
    }

    @Test
    public void testUpdateCategory_Success() {
        String categoryId = "123";
        Category existingCategory = Category.builder()
                .id(categoryId)
                .label("Old Label")
                .tenantId("default")
                .isActive(true)
                .auditDetails(new AuditDetails())
                .build();
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .label("Updated Label")
                .tenantId("default")
                .isActive(true)
                .build();

        List<Category> categories = Collections.singletonList(updatedCategory);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        when(categoryRepository.getCategoryById(categoryId)).thenReturn(Collections.singletonList(existingCategory));
        doNothing().when(producer).push(anyString(), any(Object.class));
        when(applicationProperties.getUpdateCategoryTopic()).thenReturn("update-category");

        categoryService.updateCategory(categoryRequest);

        verify(categoryRepository).getCategoryById(categoryId);
        verify(producer).push(anyString(), any(Object.class));

    }

    @Test
    public void testUpdateCategory_NothingToUpdate() {
        String categoryId = "123";
        Category existingCategory = Category.builder()
                .id(categoryId)
                .label("Old Label")
                .tenantId("default")
                .isActive(true)
                .auditDetails(new AuditDetails())
                .build();
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .tenantId("default")
                .build();

        List<Category> categories = Collections.singletonList(updatedCategory);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        when(categoryRepository.getCategoryById(categoryId)).thenReturn(Collections.singletonList(existingCategory));

        RuntimeException thrown = assertThrows(CustomException.class, () -> categoryService.updateCategory(categoryRequest));

        assertEquals(thrown.getClass(),CustomException.class);
        assertEquals(thrown.getMessage(),"no content returned, nothing to update");
        verify(categoryRepository).getCategoryById(categoryId);
    }

    @Test
    public void testUpdateCategory_MissingDetails() throws Exception {
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(RequestInfo.builder().build())
                .categories(Collections.emptyList())
                .build();

        doThrow(
                new CustomException("EG_SS_UPDATE_CATEGORY_MISSING", "provide category details for update"))
                .when(categoryValidator).validateForUpdate(any(CategoryRequest.class));

        assertThrows(CustomException.class, () -> categoryService.updateCategory(categoryRequest));

        verifyNoInteractions(categoryRepository);
        verifyNoInteractions(producer);
    }

    @Test
    public void testUpdateCategory_EmptyResultFromRepo() {

        String categoryId = "123";
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .label("Updated Label")
                .tenantId("default")
                .isActive(true)
                .build();

        List<Category> categories = Collections.singletonList(updatedCategory);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        when(categoryRepository.getCategoryById(categoryId)).thenReturn(Collections.emptyList());

        RuntimeException thrown = assertThrows(CustomException.class,()->categoryService.updateCategory(categoryRequest));

        verify(categoryRepository).getCategoryById(categoryId);
        assertEquals(thrown.getMessage(),"category not found");
    }

    @Test
    public void testUpdateCategory_UpdateResultsInDuplicateRecordCreation() {

        String categoryId = "123";
        Category existingCategory = Category.builder()
                .id(categoryId)
                .label("Old Label")
                .tenantId("default")
                .isActive(true)
                .auditDetails(new AuditDetails())
                .build();
        Category updatedCategory = Category.builder()
                .id(categoryId)
                .tenantId("default")
                .label("New Label")
                .build();

        List<Category> categories = Collections.singletonList(updatedCategory);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        when(categoryRepository.getCategoryById(categoryId)).thenReturn(Collections.singletonList(existingCategory));
        when(categoryRepository.fetchDuplicateCategoryCount(any(Category.class))).thenReturn(1);

        RuntimeException thrown = assertThrows(CustomException.class,()->categoryService.updateCategory(categoryRequest));

        assertEquals(thrown.getMessage(),"Category with this label and tenantId already exists, cannot update.");
    }

    @Test
    public void testSearchCategory_ByUuid() {
        String categoryId = "123";
        Category category = Category.builder()
                .id(categoryId)
                .label("Test Category")
                .tenantId("default")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .id(categoryId)
                .pageNumber(1)
                .size(2)
                .build();

        when(categoryRepository.fetchCategories(criteria)).thenReturn(Collections.singletonList(category));

        CategoryResponse response = categoryService.searchCategory(criteria);
        assertEquals(categoryId, response.getCategories().get(0).getId());
    }

    @Test
    public void testSearchCategory_ByUuid_PgNumZero() {
        String categoryId = "123";
        Category category = Category.builder()
                .id(categoryId)
                .label("Test Category")
                .tenantId("default")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .id(categoryId)
                .pageNumber(0)
                .size(2)
                .build();

        RuntimeException thrown = assertThrows(IllegalArgumentException.class,()->categoryService.searchCategory(criteria));

        assertEquals(thrown.getMessage(),"Page number must be greater than or equal to 1");
    }

    @Test
    public void testSearchCategory_ByUuid_SizeZero() {
        String categoryId = "123";
        Category category = Category.builder()
                .id(categoryId)
                .label("Test Category")
                .tenantId("default")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .id(categoryId)
                .pageNumber(1)
                .size(0)
                .build();

        RuntimeException thrown = assertThrows(IllegalArgumentException.class,()->categoryService.searchCategory(criteria));

        assertEquals(thrown.getMessage(),"Page size must be greater than or equal to 1");
    }

    @Test
    public void testSearchCategory_ByTenantId() {
        String tenantId = "default";
        Category category = Category.builder()
                .tenantId(tenantId)
                .label("Test Category")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .tenantId(tenantId)
                .pageNumber(1)
                .size(2)
                .build();

        when(categoryRepository.fetchCategories(criteria)).thenReturn(Collections.singletonList(category));

        CategoryResponse response = categoryService.searchCategory(criteria);
        assertEquals(tenantId, response.getCategories().get(0).getTenantId());
    }

    @Test
    public void testSearchCategory_ByLabel() {
        String label = "Test Category";
        Category category = Category.builder()
                .label(label)
                .tenantId("default")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .label(label)
                .pageNumber(1)
                .size(2)
                .tenantId("default")
                .build();

        when(categoryRepository.fetchCategories(criteria)).thenReturn(Collections.singletonList(category));

        CategoryResponse response = categoryService.searchCategory(criteria);
        assertEquals(label, response.getCategories().get(0).getLabel());
    }

    @Test
    public void testSearchCategory_ByIsActive() {
        Category category = Category.builder()
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .isActive(true)
                .pageNumber(1)
                .size(2)
                .tenantId("default")
                .build();

        when(categoryRepository.fetchCategories(criteria)).thenReturn(Collections.singletonList(category));

        CategoryResponse response = categoryService.searchCategory(criteria);
        assertTrue(response.getCategories().get(0).getIsActive());
    }

    @Test
    public void testSearchCategory_NoResults() {
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .id("non-existent-id")
                .pageNumber(1)
                .size(2)
                .build();

        when(categoryRepository.fetchCategories(criteria)).thenReturn(Collections.emptyList());

        CategoryResponse response = categoryService.searchCategory(criteria);
        assertTrue(response.getCategories().isEmpty());
    }

    @Test
    public void testSearchCategory_CategoryId_And_TenantId_Missing() {
        Category category = Category.builder()
                .label("Test Category")
                .isActive(true)
                .build();
        CategorySearchCriteria criteria = CategorySearchCriteria.builder()
                .pageNumber(1)
                .size(2)
                .build();

        RuntimeException thrown = assertThrows(CustomException.class,()->categoryService.searchCategory(criteria));

        assertEquals(thrown.getMessage(),"either an id or a tenant id is required");
    }

    @Test
    public void testCreateCategories_Success_WithinLimit() {
        when(applicationProperties.getMaxCreateLimit()).thenReturn(5);
        List<Category> categories = createCategories(5);
        when(categoryValidator.isCategoryUnique(any(Category.class))).
                thenReturn(true);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        CategoryResponse createdCategories = categoryService.createCategory(categoryRequest);

        List<Category> categoriesList = createdCategories.getCategories();
        assertEquals(categories.size(), categoriesList.size());
        for (int i = 0; i < categories.size(); i++) {
            assertEquals(categories.get(i).getId(), categoriesList.get(i).getId());
        }
    }

    @Test
    public void testCreateCategories_Failure_ExceedsLimit() {
        List<Category> categories = createCategories(6);
        when(applicationProperties.getMaxCreateLimit()).thenReturn(5);
        CategoryRequest categoryRequest = CategoryRequest.builder()
                .requestInfo(requestInfo)
                .categories(categories)
                .build();
        assertThrows(IllegalArgumentException.class, () -> categoryService.createCategory(categoryRequest));
    }

    private List<Category> createCategories(int count) {
        List<Category> categories = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            categories.add(createCategory("category" + i));
        }
        return categories;
    }

    private Category createCategory(String id) {
        return Category.builder()
                .id(id)
                .tenantId("default")
                .label("Category " + id)
                .isActive(true)
                .build();
    }
}
package org.egov.egovsurveyservices.validators;

import org.egov.egovsurveyservices.repository.CategoryRepository;
import org.egov.egovsurveyservices.web.models.Category;
import org.egov.egovsurveyservices.web.models.CategoryRequest;
import org.egov.tracer.model.CustomException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CategoryValidatorTest {
    @InjectMocks
    private CategoryValidator myComponent;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    public void testComponentInjection() {
        assertNotNull(myComponent);
    }
    @Test
    void isCategoryUnique() {
        Mockito.when(categoryRepository.isCategoryUnique(Mockito.any(Category.class)))
                        .thenReturn(1);
        assertFalse(myComponent.isCategoryUnique(new Category()));
    }

    @Test
    void validateForUpdateCategoryMissing() {
        CategoryRequest categoryRequest = CategoryRequest.builder().build();
        RuntimeException thrown = Assertions.assertThrows(CustomException.class, () -> {
            myComponent.validateForUpdate(categoryRequest);
        });
        Assertions.assertEquals("provide category details for update", thrown.getMessage());
    }

    @Test
    void validateForUpdateCategoryPresentIdMissing() {
        CategoryRequest categoryRequest = CategoryRequest.builder().categories(Collections.singletonList(
                Category.builder().build()
        )).build();
        RuntimeException thrown = Assertions.assertThrows(CustomException.class, () -> {
            myComponent.validateForUpdate(categoryRequest);
        });
        Assertions.assertEquals("category id missing", thrown.getMessage());
    }

    @Test
    void validateForUpdateAllValid() {
        CategoryRequest categoryRequest = CategoryRequest.builder().categories(Collections.singletonList(
                Category.builder().id("1").build()
        )).build();

        myComponent.validateForUpdate(categoryRequest);
    }


}
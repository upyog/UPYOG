package org.egov.egovsurveyservices.validators;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.egov.egovsurveyservices.repository.CategoryRepository;
import org.egov.egovsurveyservices.web.models.Category;
import org.egov.egovsurveyservices.web.models.CategoryRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Slf4j
@Component
public class CategoryValidator {

    @Autowired
    CategoryRepository categoryRepository;

    public boolean isCategoryUnique(Category category) {
        int count = categoryRepository.isCategoryUnique(category);
        return count == 0;
    }

    public void validateForUpdate(CategoryRequest categoryRequest) {
        if(CollectionUtils.isEmpty(categoryRequest.getCategories())){
            throw new CustomException("EG_SS_UPDATE_CATEGORY_MISSING", "provide category details for update");
        }
        if(StringUtils.isBlank(categoryRequest.getCategories().get(0).getId())){
            throw new CustomException("EG_SS_UPDATE_CATEGORY_MISSING_ID", "category id missing");
        }
    }
}

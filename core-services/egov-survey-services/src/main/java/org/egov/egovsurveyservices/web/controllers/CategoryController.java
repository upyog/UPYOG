package org.egov.egovsurveyservices.web.controllers;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.service.CategoryService;
import org.egov.egovsurveyservices.web.models.CategoryRequest;
import org.egov.egovsurveyservices.web.models.CategoryResponse;
import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
import org.egov.egovsurveyservices.web.models.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/egov-ss")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @RequestMapping(value="/category/_create", method = RequestMethod.POST)
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.createCategory(categoryRequest);
        return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
    }

    @RequestMapping(value="/category/_update", method = RequestMethod.PUT)
    public ResponseEntity<CategoryResponse> update(@RequestBody CategoryRequest categoryRequest) {
        CategoryResponse categoryResponse = categoryService.updateCategory(categoryRequest);
        return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
    }


    @PostMapping("/category/_search")
    public ResponseEntity<CategoryResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,
                                                   @ModelAttribute CategorySearchCriteria criteria) {

        CategoryResponse response = categoryService.searchCategory(criteria);
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

}

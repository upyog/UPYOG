package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.repository.querybuilder.CategoryQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.CategoryRowMapper;
import org.egov.egovsurveyservices.web.models.Category;
import org.egov.egovsurveyservices.web.models.CategorySearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class CategoryRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CategoryRowMapper rowMapper;

    @Autowired
    private CategoryQueryBuilder categoryQueryBuilder;


    public List<Category> getCategoryById(String id) {
        String sql = "SELECT id, label, isactive, tenantid, createdby, lastmodifiedby, createdtime, lastmodifiedtime " +
                "FROM eg_ss_category WHERE id = ?";
        return jdbcTemplate.query(sql, new Object[]{id}, rowMapper);
    }


    public List<Category> fetchCategories(CategorySearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();

        String query = categoryQueryBuilder.getCategorySearchQuery(criteria, preparedStmtList);
        log.info("query for category search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    public Integer fetchDuplicateCategoryCount(Category category) {
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(category.getLabel());
        preparedStmtList.add(category.getTenantId());
        preparedStmtList.add(category.getId());
        String query = categoryQueryBuilder.getCheckDuplicateCategory();
        log.info("query for search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    public Integer isCategoryUnique(Category category){
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(category.getLabel().trim());
        preparedStmtList.add(category.getTenantId());
        String query = categoryQueryBuilder.getIsUniqueCategorySql();
        log.info("query for search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    public Integer existsById(String id) {
        return jdbcTemplate.queryForObject(categoryQueryBuilder.existById(), Integer.class, id);
    }

}

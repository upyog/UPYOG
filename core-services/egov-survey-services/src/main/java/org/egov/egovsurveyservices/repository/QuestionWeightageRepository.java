package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.repository.querybuilder.QuestionWeightageQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Slf4j
@Repository
public class QuestionWeightageRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    public BigDecimal getQuestionWeightage(String questionUuid, String sectionUuid) {
        String sql = QuestionWeightageQueryBuilder.QUESTION_WEIGHTAGE_QUERY;
        return jdbcTemplate.queryForObject(sql, new Object[]{questionUuid, sectionUuid}, BigDecimal.class);
    }
}
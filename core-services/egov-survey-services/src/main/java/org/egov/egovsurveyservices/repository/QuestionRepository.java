package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.repository.querybuilder.QuestionQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.QuestionRowMapper;
import org.egov.egovsurveyservices.web.models.Question;
import org.egov.egovsurveyservices.web.models.QuestionSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class QuestionRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private QuestionRowMapper rowMapper;

    @Autowired
    private QuestionQueryBuilder questionQueryBuilder;


    public List<Question> getQuestionById(String uuid) {
        String sql = questionQueryBuilder.getQuestionByIdSql();
        return jdbcTemplate.query(sql, new Object[]{uuid}, rowMapper);
    }

    public List<Question> fetchQuestions(QuestionSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();

        String query = questionQueryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);
        log.info("query for category search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }


    public List<Question> fetchQuestionsPlainSearch(QuestionSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();

        String query = questionQueryBuilder.getQuestionSearchQueryPlainSearch(criteria, preparedStmtList);
        log.info("query for category search: " + query + " params: " + preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}

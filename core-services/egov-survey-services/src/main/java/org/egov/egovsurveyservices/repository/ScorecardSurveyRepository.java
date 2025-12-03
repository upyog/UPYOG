package org.egov.egovsurveyservices.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.egovsurveyservices.repository.querybuilder.ScorecardSurveyQueryBuilder;
import org.egov.egovsurveyservices.repository.rowmapper.*;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.QuestionWeightage;
import org.egov.egovsurveyservices.web.models.Section;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ScorecardSurveyRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ScorecardSurveyRowMapper rowMapper;

    @Autowired
    private ScorecardSurveyQueryBuilder surveyQueryBuilder;

    public List<ScorecardSurveyEntity> fetchSurveys(ScorecardSurveySearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = surveyQueryBuilder.getSurveySearchQuery(criteria, preparedStmtList);

        log.info("Generated Query: " + query + " | Params: " + preparedStmtList);

        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    public boolean allQuestionsExist(List<String> questionUuids) {
        String placeholders = questionUuids.stream().map(uuid -> "?").collect(Collectors.joining(","));
        String query = surveyQueryBuilder.allQuestionExistsQuery(placeholders);
        List<String> foundUuids = jdbcTemplate.query(query, questionUuids.toArray(), (rs, rowNum) -> rs.getString("uuid"));
        return foundUuids.containsAll(questionUuids);
    }

    public List<Section> fetchSectionListBasedOnSurveyId(String surveyId) {
        List<Object> preparedStmtList = new ArrayList<>();
        if (ObjectUtils.isEmpty(surveyId))
            throw new CustomException("EG_SY_SURVEYID_ERR", "Cannot fetch section list without surveyId");
        String query = surveyQueryBuilder.fetchSectionListBasedOnSurveyId();
        preparedStmtList.add(surveyId);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SectionRowMapper());
    }

    public List<QuestionWeightage> fetchQuestionsWeightageListBySurveyAndSection(String surveyId, String sectionId) {
        List<Object> preparedStmtList = new ArrayList<>();
        if (ObjectUtils.isEmpty(surveyId) || ObjectUtils.isEmpty(sectionId))
            throw new CustomException("EG_SY_SURVEYID_SECTIONID_ERR", "Cannot fetch question list without surveyId and sectionId");
        String query = surveyQueryBuilder.fetchQuestionsWeightageListBySurveyAndSection();
        preparedStmtList.add(surveyId);
        preparedStmtList.add(sectionId);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new QuestionWeightageWithQuestionRowMapper());
    }

    public String getExistingAnswerUuid(String answerUuid) {
        String query = surveyQueryBuilder.getExistingAnswerUuid();
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{answerUuid}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getExistingAnswerUuid(String surveyResponseUuid, String questionUuid) {
        String query = "SELECT uuid FROM eg_ss_answer WHERE surveyresponseuuid = ? AND questionuuid = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{surveyResponseUuid, questionUuid}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getSurveyResponseUuidForAnswers(List<String> answerUuids) {
        String query = "SELECT surveyresponseuuid FROM eg_ss_answer WHERE uuid::uuid = ANY(?::uuid[]) LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{answerUuids.toArray(new String[0])}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public String getExistingSurveyResponseUuid(String surveyUuid, String citizenId, String tenantId) {
        String query = "SELECT uuid FROM eg_ss_survey_response WHERE surveyuuid = ? AND citizenid = ? AND tenantid = ? LIMIT 1";
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{surveyUuid, citizenId, tenantId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AnswerNew> getAnswers(String surveyUuid, String citizenId, String tenantId) {
        String query=surveyQueryBuilder.getAnswers();
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(surveyUuid);
        preparedStmtList.add(citizenId);
        preparedStmtList.add(tenantId);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new AnswerRowMapper());
    }

    public List<AnswerNew> getAnswersForPlainSearch(String surveyUuid, String citizenId, String tenantId) {
        String query=surveyQueryBuilder.getAnswersForPlainSearch();
        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(tenantId);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new AnswerRowMapper());
    }

    public List<SurveyResponseNew> getSurveyResponseDetails(String surveyUuid, String citizenId, String tenantId) {
        String query = surveyQueryBuilder.getSurveyResponseDetails();
        try {
            return jdbcTemplate.query(query, new Object[]{surveyUuid, citizenId, tenantId}, new SurveyResponseRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null; // No existing response found
        }
    }



    public String fetchTenantIdBasedOnSurveyId(String surveyId) {
        if (ObjectUtils.isEmpty(surveyId))
            throw new CustomException("EG_SS_SURVEYID_ERR", "please provide a valid surveyId");
        String query = surveyQueryBuilder.fetchTenantIdBasedOnSurveyId();
        try {
            return jdbcTemplate.queryForObject(query, new Object[]{surveyId}, String.class);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<AnswerDetail> getAnswerDetailsByAnswerUuid(String answerUuid) {
        String query = surveyQueryBuilder.getAnswerDetailsByAnswerUuid();
        return jdbcTemplate.query(query, new Object[]{answerUuid}, (rs, rowNum) ->
                AnswerDetail.builder().uuid(rs.getString("uuid")).build());
    }
}

package org.egov.egovsurveyservices.repository.querybuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class ScorecardQueryBuilder {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public String fetchSectionListBasedOnSurveyId() {
        return "SELECT uuid, surveyuuid, title, weightage, sectionorder FROM eg_ss_survey_section WHERE surveyuuid = ?";
    }

    public String getCitizenResponseExistsQuery() {
        return " SELECT EXISTS(SELECT uuid FROM eg_ss_answer WHERE surveyuuid = ? AND citizenid = ? ) ";
    }

    public String fetchQuestionsWeightageListBySurveyAndSection() {
        return "SELECT q.uuid as uuid, q.questionstatement as questionstatement, q.options as options, " +
                "q.status as status, q.type as type, q.required as required, " +
                "q.createdby as createdby, q.lastmodifiedby as lastmodifiedby, q.createdtime as createdtime, " +
                "q.lastmodifiedtime as lastmodifiedtime,qw.questionuuid as questionuuid,qw.sectionuuid as sectionuuid ," +
                "qw.weightage as weightage,qw.qorder as qorder " +
                "FROM eg_ss_question q " +
                "JOIN eg_ss_question_weightage qw ON q.uuid = qw.questionuuid " +
                "JOIN eg_ss_survey_section ss ON qw.sectionuuid = ss.uuid " +
                "WHERE ss.surveyuuid = ? AND ss.uuid = ?";
    }
}

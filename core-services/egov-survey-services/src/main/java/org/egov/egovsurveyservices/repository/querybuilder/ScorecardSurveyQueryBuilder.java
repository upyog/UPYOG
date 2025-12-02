package org.egov.egovsurveyservices.repository.querybuilder;

import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.web.models.ScorecardSurveySearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ScorecardSurveyQueryBuilder {

    @Autowired
    private ApplicationProperties config;

    private static final String SELECT = " SELECT ";
    private static final String INNER_JOIN = " INNER JOIN ";
    private static final String LEFT_JOIN  =  " LEFT OUTER JOIN ";
    private static final String AND_QUERY = " AND ";
    private final String ORDERBY_CREATEDTIME = " ORDER BY survey.createdtime DESC ";

    private static final String SURVEY_SELECT_VALUES = "survey.uuid as suuid, survey.tenantid as stenantid, survey.title as stitle, survey.category as scategory, survey.description as sdescription, survey.startdate as sstartdate, survey.enddate as senddate, survey.postedby as spostedby, survey.active as sactive, survey.answerscount as sanswerscount, survey.hasresponded as shasresponded, survey.createdtime as screatedtime, survey.lastmodifiedtime as slastmodifiedtime, survey.status as sstatus";

    private static final String QUESTION_SELECT_VALUES = " question.uuid as quuid, question.surveyid as qsurveyid, question.questionstatement as qstatement, question.options as qoptions, question.status as qstatus, question.type as qtype, question.required as qrequired, question.createdby as qcreatedby, question.lastmodifiedby as qlastmodifiedby, question.createdtime as qcreatedtime, question.lastmodifiedtime as qlastmodifiedtime, question.qorder as qorder";

    public static final String SURVEY_COUNT_WRAPPER = " SELECT COUNT(uuid) FROM ({INTERNAL_QUERY}) AS count ";

    public static final String SURVEY_UUIDS_QUERY_WRAPPER = " SELECT uuid FROM ({HELPER_TABLE}) temp ";
    
//    private static final String BASE_QUERY = "SELECT * FROM eg_ss_survey_entity survey";

    private static final String BASE_QUERY =
            "SELECT DISTINCT " +
                    // Survey fields
                    "survey.uuid AS survey_uuid, " +
                    "survey.tenantid AS survey_tenantid, " +
                    "survey.title AS survey_title, " +
                    "survey.category AS survey_category, " +
                    "survey.description AS survey_description, " +
                    "survey.startdate AS survey_startdate, " +
                    "survey.enddate AS survey_enddate, " +
                    "survey.postedby AS survey_postedby, " +
                    "survey.active AS survey_active, " +
                    "survey.answerscount AS survey_answerscount, " +
                    "survey.hasresponded AS survey_hasresponded, " +
                    "survey.createdtime AS survey_createdtime, " +
                    "survey.lastmodifiedtime AS survey_lastmodifiedtime, " +
                    "survey.createdby AS survey_createdby, " +
                    "survey.lastmodifiedby AS survey_lastmodifiedby, " +

                    // Section fields
                    "section.uuid AS section_uuid, " +
                    "section.title AS section_title, " +
                    "section.weightage AS section_weightage, " +
                    "section.sectionorder AS section_order, " +

                    // Question fields
                    "question.uuid AS question_uuid, " +
                    "question.surveyid AS question_surveyid, " +
                    "question.questionstatement AS question_statement, " +
                    "question.type AS question_type, " +
                    "question.status AS question_status, " +
                    "question.required AS question_required, " +
                    "question.createdby AS question_createdby, " +
                    "question.lastmodifiedby AS question_lastmodifiedby, " +
                    "question.createdtime AS question_createdtime, " +
                    "question.lastmodifiedtime AS question_lastmodifiedtime, " +
                    "question.categoryid AS question_categoryid, " +
                    "question.tenantid AS question_tenantid, " +

                    // Question weightage fields
                    "questionWeightage.qorder AS question_weightage_qorder, " +
                    "questionWeightage.weightage AS question_weightage, " +
                    "questionWeightage.required AS question_weightage_required, " +

                    // Question option fields
                    "questionOption.uuid AS option_uuid, " +
                    "questionOption.optiontext AS option_text, " +
                    "questionOption.optionorder as option_order, " +
                    "questionOption.weightage AS option_weightage " +

            "FROM eg_ss_survey_entity AS survey " +
            "LEFT JOIN eg_ss_survey_section AS section " +
            "ON survey.uuid = section.surveyuuid " +
            "LEFT JOIN eg_ss_question_weightage AS questionWeightage " +
            "ON section.uuid = questionWeightage.sectionuuid " +
            "LEFT JOIN eg_ss_question AS question " +
            "ON questionWeightage.questionuuid = question.uuid "+
            "LEFT JOIN eg_ss_question_option AS questionOption " +
            "ON question.uuid = questionOption.questionuuid ";

    /**
     * Generates query to fetch surveys dynamically based on UUID, tenantId, and title.
     */
    public String getSurveySearchQuery(ScorecardSurveySearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(BASE_QUERY);
        boolean whereAdded = false;

        if (criteria.getUuid() != null) {
            query.append(" WHERE survey.uuid = ? ");
            preparedStmtList.add(criteria.getUuid());
            whereAdded = true;
        }

        if (criteria.getTenantId() != null) {
            query.append(whereAdded ? " AND " : " WHERE ");
            query.append("( survey.tenantid = ? or survey.tenantid = 'pb.punjab' )");
            preparedStmtList.add(criteria.getTenantId());
            whereAdded = true;
        }

        if (criteria.getTitle() != null) {
            query.append(whereAdded ? " AND " : " WHERE ");
            query.append("survey.title ILIKE ? ");
            preparedStmtList.add("%" + criteria.getTitle() + "%");
        }

        if (criteria.getActive() != null) {
            query.append(whereAdded ? " AND " : " WHERE ");
            query.append("survey.active = ? ");
            preparedStmtList.add(criteria.getActive());
        }

        if (Boolean.TRUE.equals(criteria.getOpenSurveyFlag())) {
            query.append(whereAdded ? " AND " : " WHERE ");
            query.append(" ? BETWEEN survey.startdate AND survey.enddate ");
            preparedStmtList.add(System.currentTimeMillis());
        }
        
        return query.toString();
    }


    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList){
        if(preparedStmtList.isEmpty()){
            query.append(" WHERE ");
        }else{
            query.append(" AND ");
        }
    }

    public String allQuestionExistsQuery(String placeholders) {
        return "SELECT uuid FROM public.eg_ss_question WHERE uuid IN (" + placeholders + ")";
    }

    private String createQuery(List<String> ids) {
        StringBuilder builder = new StringBuilder();
        int length = ids.size();
        for (int i = 0; i < length; i++) {
            builder.append(" ?");
            if (i != length - 1)
                builder.append(",");
        }
        return builder.toString();
    }

    private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
        ids.forEach(id -> {
            preparedStmtList.add(id);
        });
    }

    public String getSurveyUuidsToCountMapQuery(List<String> listOfSurveyIds, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(" SELECT surveyid, COUNT(DISTINCT citizenid) FROM eg_ss_answer answer ");
        query.append(" WHERE answer.surveyid IN ( ").append(createQuery(listOfSurveyIds)).append(" )");
        addToPreparedStatement(preparedStmtList, listOfSurveyIds);
        query.append(" GROUP  BY surveyid ");
        return query.toString();
    }

    public String fetchSectionListBasedOnSurveyId() {
        return "SELECT uuid, surveyuuid, title, weightage,sectionorder FROM eg_ss_survey_section WHERE surveyuuid = ?";
    }

    public String getCitizenResponseExistsQuery() {
        return " SELECT EXISTS(SELECT uuid FROM eg_ss_answer WHERE surveyuuid = ? AND citizenid = ? ) ";
    }

    public String fetchQuestionsWeightageListBySurveyAndSection() {
        return "SELECT q.uuid as uuid, q.questionstatement as questionstatement, " +
                "q.status as status, q.type as type, q.required as required, " +
                "q.createdby as createdby, q.lastmodifiedby as lastmodifiedby, q.createdtime as createdtime, " +
                "q.lastmodifiedtime as lastmodifiedtime,qw.questionuuid as questionuuid,qw.sectionuuid as sectionuuid ," +
                "qw.weightage as weightage,qw.qorder as qorder, " +
                "option.uuid AS option_uuid, "+
                "option.optiontext AS option_text, "+
                "option.weightage AS option_weightage, "+
                "option.optionorder as option_order,option.createdby AS option_createdby, option.lastmodifiedby AS option_lastmodifiedby, option.createdtime AS option_createdtime, option.lastmodifiedtime AS option_lastmodifiedtime " +
                "FROM eg_ss_question q " +
                "JOIN eg_ss_question_weightage qw ON q.uuid = qw.questionuuid " +
                "JOIN eg_ss_survey_section ss ON qw.sectionuuid = ss.uuid " +
                "LEFT JOIN eg_ss_question_option AS option ON q.uuid = option.questionuuid "+
                "WHERE ss.surveyuuid = ? AND ss.uuid = ?";
    }

    public String getExistingAnswerUuid() {
        return "SELECT uuid FROM public.eg_ss_answer WHERE uuid = ?";
    }

    public String getWhetherCitizenHasRespondedQuery(List<String> listOfSurveyIds, String citizenId, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(" SELECT surveyid FROM eg_ss_answer answer ");
        query.append(" WHERE answer.surveyid IN ( ").append(createQuery(listOfSurveyIds)).append(" )");
        addToPreparedStatement(preparedStmtList, listOfSurveyIds);
        addClauseIfRequired(query, preparedStmtList);
        query.append(" answer.citizenid = ? ");
        preparedStmtList.add(citizenId);
        return query.toString();
    }

    public String getAnswers() {
        return "SELECT " +
                "answer.uuid, " +
                "answer.questionuuid, " +
                "answer.sectionuuid, " +
                "answer.comments, " +
                "answer.createdby, " +
                "answer.lastmodifiedby, " +
                "answer.createdtime, " +
                "answer.lastmodifiedtime, " +
                "question.questionstatement, " +
                "ansdetail.uuid AS answer_detail_uuid,"+
                "ansdetail.answeruuid AS answer_detail_answeruuid," +
                "ansdetail.answertype AS answer_detail_type," +
                "ansdetail.answercontent AS answer_detail_content," +
                "ansdetail.weightage AS answer_detail_weightage," +
                "surveyresponse.uuid AS survey_response_uuid," +
                "surveyresponse.surveyuuid AS survey_uuid," +
                "surveyresponse.citizenid AS survey_citizen_id," +
                "surveyresponse.tenantid AS survey_tenant_id " +
                "FROM public.eg_ss_answer AS answer " +
                "LEFT JOIN public.eg_ss_question AS question " +
                "ON answer.questionuuid = question.uuid " +
                "JOIN public.eg_ss_answer_detail AS ansdetail " +
                "ON answer.uuid = ansdetail.answeruuid " +
                "JOIN public.eg_ss_survey_response AS surveyresponse " +
                "ON answer.surveyresponseuuid = surveyresponse.uuid " +  
                "WHERE surveyresponse.surveyuuid = ? " +
                "AND surveyresponse.citizenid = ? " +
                "AND surveyresponse.tenantid = ?";
    }

    public String getSurveyResponseDetails() {
        return "SELECT uuid, surveyuuid, citizenid, tenantid, locality, coordinates, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime " +
                "FROM eg_ss_survey_response WHERE surveyuuid = ? AND citizenid = ? AND tenantid = ? LIMIT 1";
    }

    public String fetchTenantIdBasedOnSurveyId() {
        return "SELECT tenantid FROM eg_ss_survey_entity WHERE uuid = ?";

    }

    public String getAnswerDetailsByAnswerUuid() {
        return "SELECT uuid FROM eg_ss_answer_detail WHERE answeruuid = ?";
    }

    public String getAnswersForPlainSearch() {
        return "SELECT " +
                "answer.uuid, " +
                "answer.questionuuid, " +
                "answer.sectionuuid, " +
                "answer.comments, " +
                "answer.createdby, " +
                "answer.lastmodifiedby, " +
                "answer.createdtime, " +
                "answer.lastmodifiedtime, " +
                "question.questionstatement, " +
                "ansdetail.uuid AS answer_detail_uuid,"+
                "ansdetail.answeruuid AS answer_detail_answeruuid," +
                "ansdetail.answertype AS answer_detail_type," +
                "ansdetail.answercontent AS answer_detail_content," +
                "ansdetail.weightage AS answer_detail_weightage," +
                "surveyresponse.uuid AS survey_response_uuid," +
                "surveyresponse.surveyuuid AS survey_uuid," +
                "surveyresponse.citizenid AS survey_citizen_id," +
                "surveyresponse.tenantid AS survey_tenant_id " +
                "FROM public.eg_ss_answer AS answer " +
                "LEFT JOIN public.eg_ss_question AS question " +
                "ON answer.questionuuid = question.uuid " +
                "JOIN public.eg_ss_answer_detail AS ansdetail " +
                "ON answer.uuid = ansdetail.answeruuid " +
                "JOIN public.eg_ss_survey_response AS surveyresponse " +
                "ON answer.surveyresponseuuid = surveyresponse.uuid " +
                "WHERE surveyresponse.tenantid = ?";
    }

}

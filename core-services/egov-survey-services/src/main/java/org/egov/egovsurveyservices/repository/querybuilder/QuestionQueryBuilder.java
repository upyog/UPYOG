package org.egov.egovsurveyservices.repository.querybuilder;

import org.apache.commons.lang3.StringUtils;
import org.egov.egovsurveyservices.web.models.QuestionSearchCriteria;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QuestionQueryBuilder {

    public static final String SELECT_QUESTION_WITH_CATEGORY = "SELECT question.uuid, question.tenantid, question.surveyid, question.questionstatement, question.status, " +
            "question.type, question.required, question.qorder, question.categoryid, " +
            "question.createdby, question.lastmodifiedby, question.createdtime, question.lastmodifiedtime, " +
            "category.id as category_id, category.label as category_label, category.isactive as category_isactive, category.tenantid as category_tenantid, "+
            "option.uuid AS option_uuid, option.optiontext AS option_text, option.weightage AS option_weightage, " +
            "option.optionorder as option_order, option.createdby AS option_createdby, option.lastmodifiedby AS option_lastmodifiedby, option.createdtime AS option_createdtime, option.lastmodifiedtime AS option_lastmodifiedtime " +
            "FROM eg_ss_question question "+
            "INNER JOIN eg_ss_category category ON question.categoryid = category.id " +
            "LEFT JOIN eg_ss_question_option option ON question.uuid = option.questionuuid";

    public String getQuestionByIdSql(){
        return "SELECT question.uuid, question.tenantid, question.surveyid, question.questionstatement, question.status, " +
        "question.type, question.required, question.qorder, question.categoryid, " +
                "question.createdby, question.lastmodifiedby, question.createdtime, question.lastmodifiedtime, "+
                "option.uuid AS option_uuid, option.optiontext AS option_text, option.weightage AS option_weightage, " +
                "option.optionorder as option_order,option.createdby AS option_createdby, option.lastmodifiedby AS option_lastmodifiedby, option.createdtime AS option_createdtime, option.lastmodifiedtime AS option_lastmodifiedtime " +
                "FROM eg_ss_question question " +
                "LEFT JOIN eg_ss_question_option option ON question.uuid = option.questionuuid " +
                "WHERE question.uuid = ?";
    }

    public String getQuestionSearchQueryPlainSearch(QuestionSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(SELECT_QUESTION_WITH_CATEGORY);
        if (!StringUtils.isBlank(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" (question.tenantid = ? or question.tenantid ='pb.punjab' )");
            preparedStmtList.add(criteria.getTenantId());
        }
        query.append(" ORDER BY question.createdtime DESC ");
//        int offset = (criteria.getPageNumber() - 1) * criteria.getSize();
//        query.append(" LIMIT ").append(criteria.getSize()).append(" OFFSET ").append(offset);
        return query.toString();
    }

    public String getQuestionSearchQuery(QuestionSearchCriteria criteria, List<Object> preparedStmtList) {
        StringBuilder query = new StringBuilder(SELECT_QUESTION_WITH_CATEGORY);
        if (!StringUtils.isBlank(criteria.getTenantId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" (question.tenantid = ? or question.tenantid ='pb.punjab' )");
            preparedStmtList.add(criteria.getTenantId());
        }

        if (!StringUtils.isBlank(criteria.getUuid())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" question.uuid = ? ");
            preparedStmtList.add(criteria.getUuid());
        }

        if (!StringUtils.isBlank(criteria.getQuestionStatement())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" question.questionstatement ilike " + "'%").append(criteria.getQuestionStatement().trim()).append("%'");
        }

        if (!StringUtils.isBlank(criteria.getCreatedBy())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" question.createdby = ? ");
            preparedStmtList.add(criteria.getCreatedBy());
        }

        if (null!=(criteria.getStatus())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" question.status = ? ");
            preparedStmtList.add(criteria.getStatus().toString());
        }

        if (null!=(criteria.getCategoryId())) {
            addClauseIfRequired(query, preparedStmtList);
            query.append(" question.categoryid = ? ");
            preparedStmtList.add(criteria.getCategoryId());
        }

        query.append(" ORDER BY question.createdtime DESC ");
        int offset = (criteria.getPageNumber() - 1) * criteria.getSize();
        query.append(" LIMIT ").append(criteria.getSize()).append(" OFFSET ").append(offset);
        return query.toString();
    }

    public String getCheckDuplicateCategory() {
        return "SELECT COUNT(*) FROM eg_ss_question WHERE questionstatement = ? AND tenantid = ? AND id != ?";
    }

    public String getIsUniqueQuestionSql(){
        return "SELECT COUNT(*) FROM eg_ss_question WHERE questionstatement = ? AND tenantid = ? AND categoryid = ?";
    }

    private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
        if (preparedStmtList.isEmpty()) {
            query.append(" WHERE ");
        } else {
            query.append(" AND ");
        }
    }

}

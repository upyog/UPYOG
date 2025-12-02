package org.egov.egovsurveyservices.repository.querybuilder;

import org.egov.egovsurveyservices.web.models.QuestionSearchCriteria;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionQueryBuilderTest {

    @Mock
    private QuestionSearchCriteria criteria;

    @InjectMocks
    private QuestionQueryBuilder queryBuilder;
    
    @Test
    public void testGetQuestionSearchQuery_withAllCriteria() {
        String tenantId = "default";
        String uuid = "question-uuid";
        String questionStatement = "question statement";
        String createdBy = "creator";
        Status status = Status.ACTIVE;
        String categoryId = "category-id";
        int pageNumber = 1;
        int pageSize = 10;

        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(tenantId);
        when(criteria.getUuid()).thenReturn(uuid);
        when(criteria.getQuestionStatement()).thenReturn(questionStatement);
        when(criteria.getCreatedBy()).thenReturn(createdBy);
        when(criteria.getStatus()).thenReturn(status);
        when(criteria.getCategoryId()).thenReturn(categoryId);
        when(criteria.getPageNumber()).thenReturn(pageNumber);
        when(criteria.getSize()).thenReturn(pageSize);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);
        
        String expectedQuery = "SELECT question.uuid, question.tenantid, question.surveyid, " +
                "question.questionstatement, question.status, question.type, question.required, " +
                "question.qorder, question.categoryid, question.createdby, question.lastmodifiedby, " +
                "question.createdtime, question.lastmodifiedtime, category.id as category_id, " +
                "category.label as category_label, category.isactive as category_isactive, " +
                "category.tenantid as category_tenantid, option.uuid AS option_uuid, " +
                "option.optiontext AS option_text, option.weightage AS option_weightage, " +
                "option.optionorder as option_order, " +
                "option.createdby AS option_createdby, option.lastmodifiedby AS option_lastmodifiedby, " +
                "option.createdtime AS option_createdtime, option.lastmodifiedtime AS option_lastmodifiedtime " +
                "FROM eg_ss_question question INNER JOIN eg_ss_category category " +
                "ON question.categoryid = category.id " +
                "LEFT JOIN eg_ss_question_option option ON question.uuid = option.questionuuid " +
                "WHERE (question.tenantid = ? or question.tenantid ='pb.punjab' ) AND question.uuid = ? " +
                "AND question.questionstatement ilike '%question statement%' " +
                "AND question.createdby = ? AND question.status = ? " +
                "AND question.categoryid = ? ORDER BY question.createdtime DESC LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery.replaceAll("\\s+", " ").trim(), query.replaceAll("\\s+", " ").trim());

        assertEquals(5, preparedStmtList.size());
        assertEquals(tenantId, preparedStmtList.get(0));
        assertEquals(uuid, preparedStmtList.get(1));
        assertEquals(createdBy, preparedStmtList.get(2));
        assertEquals(status.toString(), preparedStmtList.get(3));
        assertEquals(categoryId, preparedStmtList.get(4));
    }

    
    @Test
    public void testGetQuestionSearchQuery_withEmptyCriteria() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getUuid()).thenReturn(null);
        when(criteria.getQuestionStatement()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getStatus()).thenReturn(null);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(0, preparedStmtList.size());
    }

    @Test
    public void testGetQuestionSearchQuery_withTenantId() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        String tenantId = "default";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(tenantId);
        when(criteria.getUuid()).thenReturn(null);
        when(criteria.getQuestionStatement()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getStatus()).thenReturn(null);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " WHERE  (question.tenantid = ? or question.tenantid ='pb.punjab' ) ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(tenantId, preparedStmtList.get(0));
    }

    @Test
    public void testGetQuestionSearchQuery_withUuid() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        String uuid = "question-uuid";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getUuid()).thenReturn(uuid);
        when(criteria.getQuestionStatement()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getStatus()).thenReturn(null);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " WHERE  question.uuid = ?  ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(uuid, preparedStmtList.get(0));
    }

    @Test
    public void testGetQuestionSearchQuery_withQuestionStatement() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        String questionStatement = "question statement";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getUuid()).thenReturn(null);
        when(criteria.getQuestionStatement()).thenReturn(questionStatement);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getStatus()).thenReturn(null);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " WHERE  question.questionstatement ilike '%question statement%' ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(0, preparedStmtList.size());
    }

    @Test
    public void testGetQuestionSearchQuery_withCreatedBy() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        String createdBy = "creator";
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getUuid()).thenReturn(null);
        when(criteria.getQuestionStatement()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(createdBy);
        when(criteria.getStatus()).thenReturn(null);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " WHERE  question.createdby = ?  ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(createdBy, preparedStmtList.get(0));
    }

    @Test
    public void testGetQuestionSearchQuery_withStatus() {
        QuestionQueryBuilder queryBuilder = new QuestionQueryBuilder();

        Status status = Status.ACTIVE;
        List<Object> preparedStmtList = new ArrayList<>();

        when(criteria.getTenantId()).thenReturn(null);
        when(criteria.getUuid()).thenReturn(null);
        when(criteria.getQuestionStatement()).thenReturn(null);
        when(criteria.getCreatedBy()).thenReturn(null);
        when(criteria.getStatus()).thenReturn(status);
        when(criteria.getCategoryId()).thenReturn(null);
        when(criteria.getPageNumber()).thenReturn(1);
        when(criteria.getSize()).thenReturn(10);

        String query = queryBuilder.getQuestionSearchQuery(criteria, preparedStmtList);

        String expectedQuery = QuestionQueryBuilder.SELECT_QUESTION_WITH_CATEGORY + " WHERE  question.status = ?  ORDER BY question.createdtime DESC  LIMIT 10 OFFSET 0";

        assertEquals(expectedQuery, query);
        assertEquals(1, preparedStmtList.size());
        assertEquals(status.toString(), preparedStmtList.get(0));
    }

    
    @Test
    public void testGetQuestionByIdSql() {assertEquals("SELECT question.uuid, question.tenantid, question.surveyid, question.questionstatement, question.status, question.type, question.required, question.qorder, question.categoryid, question.createdby, question.lastmodifiedby, question.createdtime, question.lastmodifiedtime, option.uuid AS option_uuid, option.optiontext AS option_text, option.weightage AS option_weightage, option.optionorder as option_order,option.createdby AS option_createdby, option.lastmodifiedby AS option_lastmodifiedby, option.createdtime AS option_createdtime, option.lastmodifiedtime AS option_lastmodifiedtime FROM eg_ss_question question LEFT JOIN eg_ss_question_option option ON question.uuid = option.questionuuid WHERE question.uuid = ?", queryBuilder.getQuestionByIdSql());
    }


    @Test
    public void testGetCheckDuplicateCategory() {
        assertEquals("SELECT COUNT(*) FROM eg_ss_question WHERE questionstatement = ? AND tenantid = ? AND id != ?",
                queryBuilder.getCheckDuplicateCategory());
    }

    @Test
    public void testGetIsUniqueQuestionSql(){
        assertEquals("SELECT COUNT(*) FROM eg_ss_question WHERE questionstatement = ? AND tenantid = ? AND categoryid = ?",
                queryBuilder.getIsUniqueQuestionSql());
    }
}
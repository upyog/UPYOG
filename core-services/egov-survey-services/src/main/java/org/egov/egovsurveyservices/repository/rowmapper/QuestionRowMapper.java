package org.egov.egovsurveyservices.repository.rowmapper;

import lombok.extern.slf4j.Slf4j;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.egov.egovsurveyservices.web.models.enums.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Component
public class QuestionRowMapper implements ResultSetExtractor<List<Question>>{

    public List<Question> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,Question> questionMap = new LinkedHashMap<>();
        Map<String, List<QuestionOption>> questionOptionsMap = new HashMap<>(); // To store options for each question


        while (rs.next()){
            String uuid = rs.getString("uuid");
            Question question = questionMap.get(uuid);

            if(question == null) {

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                question =  Question.builder()
                        .uuid(rs.getString("uuid"))
                        .tenantId(rs.getString("tenantid"))
                        .surveyId(rs.getString("surveyid"))
                        .questionStatement(rs.getString("questionstatement"))
                        .status(Status.fromValue(rs.getString("status")))
                        .required(rs.getBoolean("required"))
                        .type(Type.fromValue(rs.getString("type")))
                        .categoryId(rs.getString("categoryid"))
                        .auditDetails(auditdetails)
                        .build();

                    try {
                        String categoryId = rs.getString("category_id");
                        if (categoryId != null) {
                            Category category = new Category();
                            category.setId(categoryId);
                            category.setLabel(rs.getString("category_label"));
                            question.setCategory(category);
                        }
                    } catch (SQLException e) {
                        log.debug("Category information not found in result set.", e);
                    }
                try {
                    String sectionId = rs.getString("section_id");
                    if (sectionId != null) {
                        Section section = new Section();
                        section.setUuid(sectionId);
                        section.setWeightage(rs.getBigDecimal("section_weightage"));
                    }
                } catch (SQLException e) {
                    log.debug("Section information not found in result set.", e);
                }

            }
            questionMap.put(uuid, question);

            // Handle QuestionOption
            String optionUuid = rs.getString("option_uuid");
            if (optionUuid != null) {
                AuditDetails optionAuditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("option_createdby"))
                        .createdTime(rs.getLong("option_createdtime"))
                        .lastModifiedBy(rs.getString("option_lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("option_lastmodifiedtime"))
                        .build();
                QuestionOption questionOption = QuestionOption.builder()
                        .uuid(optionUuid)
                        .questionUuid(uuid)
                        .optionText(rs.getString("option_text"))
                        .weightage(rs.getDouble("option_weightage"))
                        .optionOrder(rs.getLong("option_order"))
                        .auditDetails(optionAuditdetails)
                        .build();

                // Add the option to the question's option list
                questionOptionsMap.computeIfAbsent(uuid, k -> new ArrayList<>()).add(questionOption);
            }
        }
        // Add options to the respective questions
        for (Map.Entry<String, Question> entry : questionMap.entrySet()) {
            String questionUuid = entry.getKey();
            Question question = entry.getValue();
            List<QuestionOption> options = questionOptionsMap.getOrDefault(questionUuid, new ArrayList<>());
            question.setOptions(options);
        }
        return new ArrayList<>(questionMap.values());
    }


}

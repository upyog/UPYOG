package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.Question;
import org.egov.egovsurveyservices.web.models.QuestionOption;
import org.egov.egovsurveyservices.web.models.QuestionWeightage;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.egov.egovsurveyservices.web.models.enums.Type;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class QuestionWeightageWithQuestionRowMapper implements ResultSetExtractor<List<QuestionWeightage>> {

    public List<QuestionWeightage> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, QuestionWeightage> questionWeightageMap = new LinkedHashMap<>();
        Map<String, List<QuestionOption>> questionOptionsMap = new HashMap<>(); // To store options for each question

        while (rs.next()) {
            String uuid = rs.getString("uuid");
            QuestionWeightage questionWeightage = questionWeightageMap.get(uuid);

            if (questionWeightage == null) {
                AuditDetails auditDetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                Question question = Question.builder()
                        .uuid(rs.getString("uuid"))
                        .questionStatement(rs.getString("questionstatement"))
                        .status(Status.valueOf(rs.getString("status")))
                        .type(Type.valueOf(rs.getString("type")))
                        .required(rs.getBoolean("required"))
                        .auditDetails(auditDetails)
                        .build();


                questionWeightage = QuestionWeightage.builder()
                        .questionUuid(rs.getString("questionuuid"))
                        .sectionUuid(rs.getString("sectionuuid"))
                        .weightage(rs.getBigDecimal("weightage"))
                        .qorder(rs.getLong("qorder"))
                        .question(question)
                        .build();
            }
            questionWeightageMap.put(uuid, questionWeightage);
            // Handle QuestionOption
            String optionUuid = rs.getString("option_uuid");
            if (optionUuid != null) { // Check if there is an option in the row
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

                // Add the option to the options map for the respective question UUID
                questionOptionsMap.computeIfAbsent(uuid, k -> new ArrayList<>()).add(questionOption);
            }
        }
        for (Map.Entry<String, QuestionWeightage> entry : questionWeightageMap.entrySet()) {
            String questionUuid = entry.getKey();
            QuestionWeightage questionWeightage = entry.getValue();
            List<QuestionOption> options = questionOptionsMap.getOrDefault(questionUuid, new ArrayList<>());
            questionWeightage.getQuestion().setOptions(options);
        }
        return new ArrayList<>(questionWeightageMap.values());
    }
}

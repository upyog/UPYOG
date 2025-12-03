package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.*;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class AnswerRowMapper implements ResultSetExtractor<List<AnswerNew>> {
    public List<AnswerNew> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,AnswerNew> answerMap = new LinkedHashMap<>();
        while (rs.next()) {
            String answerUuid = rs.getString("uuid");

            // Retrieve or create AnswerNew object
            AnswerNew answer = answerMap.computeIfAbsent(answerUuid, uuid -> {
                try {
                    Long lastModifiedTime = rs.getLong("lastmodifiedtime");
                    if (rs.wasNull()) {
                        lastModifiedTime = null;
                    }

                    AuditDetails auditDetails = AuditDetails.builder()
                            .createdBy(rs.getString("createdby"))
                            .createdTime(rs.getLong("createdtime"))
                            .lastModifiedBy(rs.getString("lastmodifiedby"))
                            .lastModifiedTime(lastModifiedTime)
                            .build();

                    AnswerNew answerNew =  AnswerNew.builder()
                            .uuid(answerUuid)
                            .sectionUuid(rs.getString("sectionuuid"))
                            .questionUuid(rs.getString("questionuuid"))
                            .comments(rs.getString("comments"))
                            .auditDetails(auditDetails)
                            .answerDetails(new ArrayList<>())
                            .build();
                    if(rs.getString("questionstatement")!=null){
                        answerNew.setQuestionStatement(rs.getString("questionstatement"));
                    }
                    return answerNew;
                } catch (SQLException e) {
                    throw new RuntimeException("Error while extracting answer data", e);
                }
            });

            // Add AnswerDetail under the existing AnswerNew object
            String answerDetailUuid = rs.getString("answer_detail_uuid");
            if (answerDetailUuid != null) {
                AnswerDetail answerDetail = AnswerDetail.builder()
                        .uuid(answerDetailUuid)
                        .answerUuid(answerUuid)
                        .answerType(rs.getString("answer_detail_type"))
                        .answerContent(rs.getString("answer_detail_content"))
                        .weightage(rs.getBigDecimal("answer_detail_weightage"))
                        .auditDetails(AuditDetails.builder()
                                .createdBy(rs.getString("createdby"))
                                .lastModifiedBy(rs.getString("lastmodifiedby"))
                                .createdTime(rs.getLong("createdtime"))
                                .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                                .build())
                        .build();

                answer.getAnswerDetails().add(answerDetail);
            }
        }
        return new ArrayList<>(answerMap.values());
    }

}

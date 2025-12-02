package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.*;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ScorecardSurveyRowMapper implements ResultSetExtractor<List<ScorecardSurveyEntity>> {

    @Override
    public List<ScorecardSurveyEntity> extractData(ResultSet rs) throws SQLException {
        Map<String, ScorecardSurveyEntity> surveyMap = new HashMap<>();
        Map<String, Map<String, Question>> sectionQuestionMap = new HashMap<>();
        Map<String, List<QuestionOption>> questionOptionsMap = new HashMap<>(); // Map to store options by question UUID

        try {
            while (rs.next()) {
                String surveyUuid = rs.getString("survey_uuid");

                surveyMap.computeIfAbsent(surveyUuid, uuid -> {
                    try {
                        return ScorecardSurveyEntity.builder()
                                .uuid(uuid)
                                .tenantId(rs.getString("survey_tenantid"))
                                .surveyTitle(rs.getString("survey_title"))
                                .surveyCategory(rs.getString("survey_category"))
                                .surveyDescription(rs.getString("survey_description"))
                                .startDate(rs.getLong("survey_startdate"))
                                .endDate(rs.getLong("survey_enddate"))
                                .postedBy(rs.getString("survey_postedby"))
                                .active(rs.getBoolean("survey_active"))
                                .answersCount(rs.getLong("survey_answerscount"))
                                .hasResponded(rs.getBoolean("survey_hasresponded"))
                                .auditDetails(AuditDetails.builder()
                                        .createdBy(rs.getString("survey_createdby"))
                                        .lastModifiedBy(rs.getString("survey_lastmodifiedby"))
                                        .createdTime(rs.getLong("survey_createdtime"))
                                        .lastModifiedTime(rs.getLong("survey_lastmodifiedtime"))
                                        .build())
                                .createdTime(rs.getLong("survey_createdtime"))
                                .lastModifiedTime(rs.getLong("survey_lastmodifiedtime"))
                                .sections(new ArrayList<>())
                                .build();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                });

                String sectionUuid = rs.getString("section_uuid");
                if (sectionUuid != null) {
                    Section section = surveyMap.get(surveyUuid).getSections().stream()
                            .filter(s -> s.getUuid().equals(sectionUuid))
                            .findFirst()
                            .orElseGet(() -> {
                                try {
                                    Section newSection = Section.builder()
                                            .uuid(sectionUuid)
                                            .title(rs.getString("section_title"))
                                            .weightage(rs.getBigDecimal("section_weightage"))
                                            .sectionOrder(rs.getInt("section_order"))
                                            .questions(new ArrayList<>())
                                            .build();
                                    surveyMap.get(surveyUuid).getSections().add(newSection);
                                    return newSection;
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                    sectionQuestionMap.computeIfAbsent(sectionUuid, k -> new HashMap<>());
                    String questionUuid = rs.getString("question_uuid");
                    if (questionUuid != null) {
                        Question question = sectionQuestionMap.get(sectionUuid).computeIfAbsent(questionUuid, k -> {
                            Question newQuestion;
                            try {
                                newQuestion = Question.builder()
                                        .uuid(questionUuid)
                                        .options(new ArrayList<>())
                                        .surveyId(rs.getString("question_surveyid"))
                                        .questionStatement(rs.getString("question_statement"))
                                        //                                .options(Arrays.asList(rs.getString("question_options").split(",")))
                                        .type(org.egov.egovsurveyservices.web.models.enums.Type.fromValue(rs.getString("question_type")))
                                        .status(org.egov.egovsurveyservices.web.models.enums.Status.fromValue(rs.getString("question_status")))
                                        .required(rs.getBoolean("question_required"))
                                        .auditDetails(AuditDetails.builder()
                                                .createdBy(rs.getString("question_createdby"))
                                                .lastModifiedBy(rs.getString("question_lastmodifiedby"))
                                                .createdTime(rs.getLong("question_createdtime"))
                                                .lastModifiedTime(rs.getLong("question_lastmodifiedtime"))
                                                .build())
                                        .categoryId(rs.getString("question_categoryid"))
                                        .tenantId(rs.getString("question_tenantid"))
                                        .build();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            QuestionWeightage questionWeightage;
                            try {
                                questionWeightage = QuestionWeightage.builder()
                                        .questionUuid(questionUuid)
                                        .sectionUuid(sectionUuid)
                                        .qorder(rs.getLong("question_weightage_qorder"))
                                        .weightage(rs.getBigDecimal("question_weightage"))
                                        .required(rs.getBoolean("question_weightage_required"))
                                        .question(newQuestion)
                                        .build();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }

                            section.getQuestions().add(questionWeightage);
                            return newQuestion;
                        });

                        // Handle QuestionOption
                        String optionUuid = rs.getString("option_uuid");
                        if (optionUuid != null) {
                            QuestionOption questionOption = QuestionOption.builder()
                                    .uuid(optionUuid)
                                    .questionUuid(questionUuid)
                                    .optionText(rs.getString("option_text"))
                                    .weightage(rs.getDouble("option_weightage"))
                                    .optionOrder(rs.getLong("option_order"))
                                    .build();

                            question.getOptions().add(questionOption);

//                            questionOptionsMap.computeIfAbsent(questionUuid, k -> new ArrayList<>()).add(questionOption);
                        }
                    }
                }

                // Add options to their respective questions
//                surveyMap.values().forEach(survey -> {
//                    survey.getSections().forEach(section -> {
//                        section.getQuestions().forEach(questionWeightage -> {
//                            Question question = questionWeightage.getQuestion();
//                            List<QuestionOption> options = questionOptionsMap.getOrDefault(question.getUuid(), new ArrayList<>());
//                            question.setOptions(options);
//                        });
//                    });
//                });
            }
        }catch (SQLException e) {
            throw new RuntimeException("Error while extracting survey data", e);
        }

        return new ArrayList<>(surveyMap.values());
    }
}

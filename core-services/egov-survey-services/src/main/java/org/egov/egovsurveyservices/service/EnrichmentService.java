package org.egov.egovsurveyservices.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.egovsurveyservices.repository.QuestionRepository;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import static org.egov.egovsurveyservices.utils.SurveyServiceConstants.*;


@Service
@Slf4j
public class EnrichmentService {

    @Autowired
    QuestionService questionService;

    public void enrichSurveyEntity(SurveyRequest surveyRequest) {
        SurveyEntity surveyEntity = surveyRequest.getSurveyEntity();
        surveyEntity.setStatus(ACTIVE);
        surveyEntity.setActive(Boolean.TRUE);
        surveyEntity.setAuditDetails(AuditDetails.builder()
                .createdBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                .lastModifiedBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedTime(System.currentTimeMillis())
                .build());
        surveyEntity.setPostedBy(surveyRequest.getRequestInfo().getUserInfo().getName());

        for(int i = 0; i < surveyEntity.getQuestions().size(); i++) {
            Question question = surveyEntity.getQuestions().get(i);
            question.setQorder((long)i+1);
            question.setUuid(UUID.randomUUID().toString());
            question.setSurveyId(surveyEntity.getUuid());
            if(ObjectUtils.isEmpty(question.getStatus()))
                question.setStatus(Status.ACTIVE);
            question.setAuditDetails(AuditDetails.builder()
                    .createdBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                    .lastModifiedBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                    .createdTime(System.currentTimeMillis())
                    .lastModifiedTime(System.currentTimeMillis())
                    .build());
        }
    }
    
    public void enrichScorecardSurveyEntity(ScorecardSurveyRequest surveyRequest) {
        ScorecardSurveyEntity surveyEntity = surveyRequest.getSurveyEntity();
        surveyEntity.setActive(Boolean.TRUE);
        surveyEntity.setAuditDetails(AuditDetails.builder()
                .createdBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                .lastModifiedBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                .createdTime(System.currentTimeMillis())
                .lastModifiedTime(System.currentTimeMillis())
                .build());
        surveyEntity.setPostedBy(surveyRequest.getRequestInfo().getUserInfo().getName());

        List<Section> sections = surveyEntity.getSections();
        if (CollectionUtils.isEmpty(sections)) {
            log.warn("No sections found in survey: {}", surveyEntity.getUuid());
            return;
        }

        BigDecimal totalSectionWeightage = BigDecimal.ZERO;

        for (int i = 0; i < sections.size(); i++) {
            Section section = sections.get(i);
            section.setUuid(UUID.randomUUID().toString());
            section.setSectionOrder(i + 1);
            totalSectionWeightage = totalSectionWeightage.add(section.getWeightage());

            List<QuestionWeightage> questionWeightages = section.getQuestions();
            if (CollectionUtils.isEmpty(questionWeightages)) {
                log.warn("No questions found in section: {}", section.getUuid());
                continue;
            }

            BigDecimal totalQuestionWeightage = BigDecimal.ZERO;

            for (int j = 0; j < questionWeightages.size(); j++) {
                QuestionWeightage questionWeightage = questionWeightages.get(j);
                questionWeightage.setSectionUuid(section.getUuid());
                questionWeightage.setQorder((long) j + 1);

                QuestionSearchCriteria criteria = new QuestionSearchCriteria();
                criteria.setUuid(questionWeightage.getQuestionUuid());
                QuestionResponse questionResponse = questionService.searchQuestion(criteria);
                Question question = questionResponse.getQuestions().get(0);
                questionWeightage.setQuestion(question);

                totalQuestionWeightage = totalQuestionWeightage.add(questionWeightage.getWeightage());

                if (question == null) {
                    log.warn("Skipping null question in section: {}", section.getUuid());
                    continue;
                }

//                question.setQorder(questionWeightage.getQorder());
                question.setUuid(questionWeightage.getQuestionUuid());
                question.setSurveyId(surveyEntity.getUuid());

                if (ObjectUtils.isEmpty(question.getStatus())) {
                    question.setStatus(Status.ACTIVE);
                }

                question.setAuditDetails(AuditDetails.builder()
                        .createdBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                        .lastModifiedBy(surveyRequest.getRequestInfo().getUserInfo().getUuid())
                        .createdTime(System.currentTimeMillis())
                        .lastModifiedTime(System.currentTimeMillis())
                        .build());
            }
            if (totalQuestionWeightage.compareTo(BigDecimal.valueOf(100)) > 0) {
                throw new IllegalArgumentException("Total question weightage in section " + section.getUuid() + " must be 100, found: " + totalQuestionWeightage);
            }
            totalQuestionWeightage = totalQuestionWeightage.setScale(0, RoundingMode.HALF_UP);
            if (totalQuestionWeightage.compareTo(BigDecimal.valueOf(100)) != 0) {
                throw new IllegalArgumentException("Total question weightage in section " + section.getUuid() + " must be 100, found: " + totalQuestionWeightage + " after rounding off");
            }
        }
        if (totalSectionWeightage.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("Total section weightage in survey " + surveyEntity.getUuid() + " must be 100, found: " + totalSectionWeightage);
        }
        totalSectionWeightage = totalSectionWeightage.setScale(0, RoundingMode.HALF_UP);
        if (totalSectionWeightage.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new IllegalArgumentException("Total section weightage in survey " + surveyEntity.getUuid() + " must be 100, found: " + totalSectionWeightage + " after rounding off");
        }

        log.info("Survey enrichment completed for survey: {}", surveyEntity.getUuid());
    }
    
    public void enrichAnswerEntity(AnswerRequest answerRequest) {
        RequestInfo requestInfo = answerRequest.getRequestInfo();
        AnswerEntity answerEntity = answerRequest.getAnswerEntity();
        answerEntity.getAnswers().forEach(answer -> {
            answer.setAnswerUuid(UUID.randomUUID().toString());
            answer.setCitizenId(requestInfo.getUserInfo().getUuid());
            answer.setAuditDetails(AuditDetails.builder()
                    .createdBy(requestInfo.getUserInfo().getUuid())
                    .lastModifiedBy(requestInfo.getUserInfo().getUuid())
                    .createdTime(System.currentTimeMillis())
                    .lastModifiedTime(System.currentTimeMillis())
                    .build());
        });
    }
}
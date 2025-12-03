package org.egov.egovsurveyservices.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.producer.Producer;
import org.egov.egovsurveyservices.repository.CategoryRepository;
import org.egov.egovsurveyservices.repository.QuestionRepository;
import org.egov.egovsurveyservices.utils.ResponseInfoFactory;
import org.egov.egovsurveyservices.validators.QuestionValidator;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.Status;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class QuestionService {

    @Autowired
    QuestionValidator questionValidator;

    @Autowired
    QuestionRepository questionRepository;

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private ApplicationProperties applicationProperties;

    public QuestionResponse createQuestion(QuestionRequest questionRequest) {
        RequestInfo requestInfo = questionRequest.getRequestInfo();
        
        if (questionRequest.getQuestions().size() > applicationProperties.getMaxCreateLimit()) {
            throw new IllegalArgumentException("Maximum " + applicationProperties.getMaxCreateLimit() + " questions allowed per request.");
        }

        long counter = 0;
        for (Question question : questionRequest.getQuestions()) {
            categoryExistsById(question.getCategoryId());
            enrichCreateRequest(question, requestInfo, counter++);
            // Validate options length
//            if (question.getOptions() != null && question.getOptions().stream().anyMatch(opt -> opt.length() > 200)) {
//                throw new IllegalArgumentException("Maximum 200 characters allowed only for a question's option");
//            }
        }

        producer.push(applicationProperties.getSaveQuestionTopic(), questionRequest);
        return generateResponse(questionRequest);
    }

    public QuestionResponse updateQuestion(QuestionRequest questionRequest) {
        questionValidator.validateForUpdate(questionRequest);
        Question question = questionRequest.getQuestions().get(0);
        List<Question> existingQuesList = questionRepository.getQuestionById(question.getUuid());
        if (CollectionUtils.isEmpty(existingQuesList)) {
            throw new CustomException("EG_SS_QUESTION_NOT_FOUND", "question not found");
        }
        Question existingQuesFromDb = existingQuesList.get(0);

        if (question.getStatus() != null) {
            existingQuesFromDb.setStatus(question.getStatus());
        }

        // Update audit details
        String uuid = questionRequest.getRequestInfo().getUserInfo().getUuid();
        existingQuesFromDb.getAuditDetails().setLastModifiedBy(uuid);
        existingQuesFromDb.getAuditDetails().setLastModifiedTime(System.currentTimeMillis());

        // Save the updated question
        questionRequest.setQuestions(Collections.singletonList(existingQuesFromDb));
        producer.push(applicationProperties.getUpdateQuestionTopic(), questionRequest);
        return generateResponse(questionRequest);

    }

    private void enrichCreateRequest(Question question, RequestInfo requestInfo,long counter) {
        long currentTime = System.currentTimeMillis() + counter;
        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(requestInfo.getUserInfo().getUuid())
                .lastModifiedBy(requestInfo.getUserInfo().getUuid())
                .createdTime(currentTime)
                .lastModifiedTime(currentTime)
                .build();
        question.setUuid(UUID.randomUUID().toString());
        question.setAuditDetails(auditDetails);
        question.setStatus(Optional.ofNullable(question.getStatus()).orElse(Status.ACTIVE));
        List<QuestionOption> options = question.getOptions();
        if (options == null || options.isEmpty()) {
            // If options are null or empty, create a default "NA" option
            QuestionOption defaultOption = QuestionOption.builder()
                    .uuid(UUID.randomUUID().toString())
                    .questionUuid(question.getUuid())
                    .optionText("NA")
                    .weightage(0.0)
                    .auditDetails(auditDetails)
                    .optionOrder(1L)
                    .build();
            question.setOptions(Collections.singletonList(defaultOption));
        } else {
            for (int i = 0; i < options.size(); i++) {
                QuestionOption option = options.get(i);
                option.setUuid(UUID.randomUUID().toString());
                option.setQuestionUuid(question.getUuid());
                option.setAuditDetails(auditDetails);

                // Set optionOrder if not provided by the user
                if (option.getOptionOrder() == null) {
                    option.setOptionOrder((long) (i + 1));
                }
            }
            question.setOptions(options);
        }
    }

    private QuestionResponse generateResponse(QuestionRequest questionRequest) {
        return QuestionResponse.builder().responseInfo(ResponseInfoFactory.createResponseInfoFromRequestInfo(questionRequest.getRequestInfo(), true)).questions(questionRequest.getQuestions()).build();
    }

    public QuestionResponse searchQuestion(QuestionSearchCriteria criteria) {

        if (StringUtils.isBlank(criteria.getUuid()) && (StringUtils.isBlank(criteria.getTenantId())||(StringUtils.isBlank(criteria.getCategoryId())))) {
            throw new CustomException("EG_SS_TENANT_ID_REQUIRED_QUESTION_SEARCH", "either a (uuid) or a (tenant id and category id) is required.");
        }
        if (criteria.getPageNumber() < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestions(questionRepository.fetchQuestions(criteria));
        return generateResponse(questionRequest);
    }


    public QuestionResponse searchQuestionPlainSearch(QuestionSearchCriteria criteria) {

        if (StringUtils.isBlank(criteria.getTenantId())) {
            throw new CustomException("EG_SS_TENANT_ID_REQUIRED_QUESTION_PLAIN_SEARCH", "tenantId is required.");
        }
        if (criteria.getPageNumber() < 1) {
            throw new IllegalArgumentException("Page number must be greater than or equal to 1");
        }

        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestions(questionRepository.fetchQuestionsPlainSearch(criteria));
        return generateResponse(questionRequest);
    }

    public void categoryExistsById(String id){
        if (categoryRepository.existsById(id) == 0) {
            throw new CustomException("CATEGORY_DOES_NOT_EXIST","Category with ID " + id + " does not exist.");
        }
    }

}

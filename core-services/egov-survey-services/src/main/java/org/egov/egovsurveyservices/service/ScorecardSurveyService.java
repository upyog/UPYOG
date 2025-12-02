package org.egov.egovsurveyservices.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import javax.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.egovsurveyservices.config.ApplicationProperties;
import org.egov.egovsurveyservices.producer.Producer;
import org.egov.egovsurveyservices.repository.QuestionRepository;
import org.egov.egovsurveyservices.repository.QuestionWeightageRepository;
import org.egov.egovsurveyservices.repository.ScorecardSurveyRepository;
import org.egov.egovsurveyservices.repository.SectionRepository;
import org.egov.egovsurveyservices.utils.ScorecardSurveyUtil;
import org.egov.egovsurveyservices.validators.ScorecardSurveyValidator;
import org.egov.egovsurveyservices.web.models.*;
import org.egov.egovsurveyservices.web.models.enums.SurveyStatus;
import org.egov.egovsurveyservices.web.models.user.UserSearchRequest;
import org.egov.egovsurveyservices.web.models.user.UserSearchResponseContent;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ScorecardSurveyService {
    @Autowired
    private ScorecardSurveyValidator surveyValidator;

    @Autowired
    private Producer producer;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionWeightageRepository questionWeightageRepository;

    @Autowired
    private SectionRepository sectionRepository;

    @Autowired
    private EnrichmentService enrichmentService;

    @Autowired
    private ScorecardSurveyRepository surveyRepository;

    @Autowired
    private ScorecardSurveyUtil surveyUtil;

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ObjectMapper mapper;
    /**
     * Validates the request object and checks if all questions exist in the database
     *
     * @param surveyRequest Request object containing details of survey

    /**
     * Creates the survey based on the request object and pushes to Create Scorecard Survey Topic
     *
     * @param surveyRequest Request object containing details of survey
     */
    public ScorecardSurveyEntity createSurvey(ScorecardSurveyRequest surveyRequest) {
        ScorecardSurveyEntity surveyEntity = surveyRequest.getSurveyEntity();

        if (surveyEntity.getStartDate() == null) {
        	throw new IllegalArgumentException("Survey startDate is required");
    	}
        if (surveyEntity.getEndDate() == null) {
        	throw new IllegalArgumentException("Survey endDate is required");
    	}
        if (surveyEntity.getStartDate() >= surveyEntity.getEndDate()) {
            throw new CustomException("INVALID_DATE_RANGE", "Start date must be before end date");
        }
        surveyValidator.validateUserType(surveyRequest.getRequestInfo());

        if (surveyEntity.getSurveyTitle() == null || surveyEntity.getSurveyTitle().isEmpty()) {
            throw new IllegalArgumentException("Survey title is empty");
        }

        String tenantId = surveyEntity.getTenantId();

        List<String> questionUuids = surveyEntity.getSections().stream()
                .flatMap(section -> section.getQuestions().stream())
                .map(QuestionWeightage::getQuestionUuid)
                .collect(Collectors.toList());

        if (!allQuestionsExist(questionUuids)) {
            throw new IllegalArgumentException("One or more questions do not exist in the database.");
        }

        List<String> listOfSurveyIds = surveyUtil.getIdList(surveyRequest.getRequestInfo(), tenantId, "ss.surveyid", "SY-[cy:yyyy-MM-dd]-[SEQ_EG_DOC_ID]", 1);
        log.info(listOfSurveyIds.toString());

        surveyEntity.setUuid(listOfSurveyIds.get(0));
        surveyEntity.setTenantId(tenantId);

        enrichmentService.enrichScorecardSurveyEntity(surveyRequest);
        log.info(surveyRequest.getSurveyEntity().toString());

        producer.push(applicationProperties.getCreateScorecardSurveyTopic(), surveyRequest);

        return surveyEntity;
    }

    /**
     * Searches surveys based on the criteria request and fetches details
     * @param criteria Request object containing criteria filters of survey to be searched
     */
    
    public List<ScorecardSurveyEntity> searchSurveys(ScorecardSurveySearchCriteria criteria) {
        // If UUID is present
        if (StringUtils.isNotBlank(criteria.getUuid())) {
            List<ScorecardSurveyEntity> surveyEntities = surveyRepository.fetchSurveys(criteria);
            return !surveyEntities.isEmpty() ? surveyEntities : new ArrayList<>();
        }

        //check either if tenantId or title or active or openSurveyFlag true is provided
        if (StringUtils.isNotBlank(criteria.getTenantId()) || StringUtils.isNotBlank(criteria.getTitle()) || Boolean.TRUE.equals(criteria.getOpenSurveyFlag()) || criteria.getActive() != null) {
            return surveyRepository.fetchSurveys(criteria); // Fetch based on tenantId, title, or both
        }

        return new ArrayList<>();
    }

	public void updateSurveyActive(@Valid UpdateSurveyActiveRequest request) {
		// Validate UUID
	    if (request.getUuid() == null || request.getUuid().trim().isEmpty()) {
	        throw new IllegalArgumentException("UUID must not be null or empty");
	    }

	    // Validate Active status
	    if (request.getActive() == null) {
	        throw new IllegalArgumentException("Active status must not be null");
	    }
		ScorecardSurveySearchCriteria criteria = new ScorecardSurveySearchCriteria();

		//check uuid is present in database
		criteria.setUuid(request.getUuid());
		List<ScorecardSurveyEntity> surveyEntities = surveyRepository.fetchSurveys(criteria);
		if(surveyEntities.isEmpty()) {
			log.warn("No survey found in database for this uuid: {}", request.getUuid());
			throw new IllegalArgumentException("UUID does not exist in database, Update failed!");
		}
		else {
			request.setLastModifiedTime(System.currentTimeMillis());
			request.setLastModifiedBy(request.getRequestInfo().getUserInfo().getUuid());
			producer.push(applicationProperties.getUpdateActiveSurveyTopic(), request);
		}
	}

	private boolean allQuestionsExist(List<String> questionUuids) {
        return surveyRepository.allQuestionsExist(questionUuids);
    }

    public ScorecardSubmitResponse submitResponse(AnswerRequestNew answerRequest) {
        SurveyResponseNew surveyResponse = answerRequest.getSurveyResponse();
        String tenantIdBasedOnSurveyId = fetchTenantIdBasedOnSurveyId(surveyResponse.getSurveyUuid());
        surveyResponse.setUuid(UUID.randomUUID().toString());
        List<String> answerUuids = surveyResponse.getAnswers().stream()
                .map(AnswerNew::getUuid)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        String existingSurveyResponseUuid = surveyRepository.getSurveyResponseUuidForAnswers(answerUuids);
        if (existingSurveyResponseUuid != null) {
            surveyResponse.setUuid(existingSurveyResponseUuid);

        }else{
            existingSurveyResponseUuid = surveyRepository.getExistingSurveyResponseUuid(
                    surveyResponse.getSurveyUuid(), answerRequest.getUser().getUuid(), surveyResponse.getTenantId());

            if (existingSurveyResponseUuid != null) {
                throw new CustomException("EG_SS_SURVEYUUID_CITIZEN_TENANTID_UNIQUE_KEY_VIOLATION","unique key violation - survey already answered by citizen in the tenant");
            } else {
                surveyResponse.setUuid(UUID.randomUUID().toString()); // Generate new UUID
            }
        }

        if (answerRequest.getSurveyResponse().getStatus()==null) {
            answerRequest.getSurveyResponse().setStatus(SurveyStatus.DRAFT);
        }
        // Validate tenant ID based on survey response
        if(StringUtils.equalsIgnoreCase(tenantIdBasedOnSurveyId,"pb.punjab")){
            surveyValidator.validateCityIsProvided(surveyResponse.getTenantId());
            surveyResponse.setTenantId(surveyResponse.getCity());
        }
        surveyValidator.validateUserTypeForAnsweringScorecardSurvey(answerRequest);
        String uuid = answerRequest.getUser().getUuid();
        surveyValidator.validateAnswers(surveyResponse);

        List<Section> sections = sectionRepository.getSectionsBySurveyId(answerRequest.getSurveyResponse().getSurveyUuid());
        Map<String, BigDecimal> sectionWeightageMap = sections.stream()
                .collect(Collectors.toMap(Section::getUuid, Section::getWeightage));

        Map<String, UserSearchResponseContent> stringUserMap=null;
        UserSearchResponseContent user;
        try {
            stringUserMap = userService.searchUser(answerRequest.getRequestInfo(), Collections.singletonList(uuid));
            user = stringUserMap.get(uuid);
        } catch (Exception e) {
            user = new UserSearchResponseContent();
            user.setUuid(uuid);
        }
        surveyResponse.setUserDetails(mapper.convertValue(user, JsonNode.class));

        AuditDetails auditDetails = AuditDetails.builder()
                .createdBy(uuid)
                .lastModifiedBy(uuid)
                .createdTime(System.currentTimeMillis())
                .lastModifiedTime(System.currentTimeMillis())
                .build();
        UserSearchResponseContent finalUser = user;
        List<ScorecardSectionResponse> enrichedSectionResponses = surveyResponse.getAnswers().stream()
                .collect(Collectors.groupingBy(AnswerNew::getSectionUuid)).entrySet().stream()
                .map(entry -> {
                    List<ScorecardQuestionResponse> enrichedQuestionResponses = entry.getValue().stream()
                            .map(answer -> {
                                List<Question> questionById = questionRepository.getQuestionById(answer.getQuestionUuid());
                                Question question = questionById.get(0);
                                String existingAnswerUuid = getExistingAnswerUuid(answer.getUuid());
                                String uuidToUse;
                                if(existingAnswerUuid!=null){
                                    uuidToUse = existingAnswerUuid;
                                }else{
                                    String existingAnswerUuidMethodTwo = surveyRepository.getExistingAnswerUuid(surveyResponse.getUuid(), answer.getQuestionUuid());
                                    if(existingAnswerUuidMethodTwo!=null){
                                        uuidToUse = existingAnswerUuidMethodTwo;
                                    }else{
                                        uuidToUse = UUID.randomUUID().toString();
                                    }
                                }

                                if (existingAnswerUuid != null) {
                                    answer.setUuid(existingAnswerUuid);
                                } else {
                                    answer.setUuid(UUID.randomUUID().toString());
                                }
                                if (StringUtils.isBlank(question.getQuestionStatement())) {
                                    throw new CustomException("EG_SS_QUESTION_NOT_FOUND", "question not found with id " + answer.getQuestionUuid());
                                }
                                answer.setQuestionStatement(question.getQuestionStatement());
                                answer.setSectionUuid(entry.getKey());
                                BigDecimal sectionWeightage = sectionWeightageMap.getOrDefault(answer.getSectionUuid(), BigDecimal.valueOf(0.0));
                                answer.setSectionWeightage(sectionWeightage);
                                answer.setUuid(uuidToUse);
                                answer.setAuditDetails(auditDetails);

                                BigDecimal questionWeightage = questionWeightageRepository.getQuestionWeightage(answer.getQuestionUuid(), answer.getSectionUuid());
                                answer.setQuestionWeightage(BigDecimal.valueOf(questionWeightage != null ? questionWeightage.doubleValue() : 0.0));
                                if (answer.getAnswerDetails() != null) {
                                    answer.getAnswerDetails().forEach(detail -> {
                                        detail.setAnswerUuid(answer.getUuid());
                                        detail.setAuditDetails(auditDetails);
                                        detail.setAnswerType(question.getType().toString());
                                    });
                                }
                                if (answer.getAnswerDetails() != null) {
                                    answer.getAnswerDetails().forEach(detail -> {
                                        String existingAnswerDetailUuid = getExistingAnswerDetailUuid(detail.getAnswerUuid());
                                        String detailUuidToUse = existingAnswerDetailUuid != null ? existingAnswerDetailUuid : UUID.randomUUID().toString();

                                        detail.setUuid(detailUuidToUse);
                                        detail.setAnswerUuid(answer.getUuid());
                                        detail.setAuditDetails(auditDetails);
                                        detail.setAnswerType(question.getType().toString());
                                        detail.setUserDetails(mapper.convertValue(finalUser, JsonNode.class));
                                    });
                                }

                                surveyResponse.setCitizenId(uuid);
                                return ScorecardQuestionResponse.builder()
                                        .questionUuid(answer.getQuestionUuid())
                                        .questionStatement(question.getQuestionStatement())
                                        .answerUuid(answer.getUuid())
                                        .comments(answer.getComments())
                                        .answerResponse(answer)
                                        .build();
                            }).collect(Collectors.toList());
                    producer.push(applicationProperties.getSubmitAnswerScorecardSurveyTopic(), answerRequest);
                    return ScorecardSectionResponse.builder()
                            .sectionUuid(entry.getKey())
                            .questionResponses(enrichedQuestionResponses)
                            .build();
                }).collect(Collectors.toList());

        ScorecardAnswerResponse scorecardAnswerResponse = ScorecardAnswerResponse.builder()
                .locality(surveyResponse.getLocality())
                .coordinates(surveyResponse.getCoordinates())
                .tenantId(surveyResponse.getTenantId())
                .status(surveyResponse.getStatus().toString())
                .surveyUuid(surveyResponse.getSurveyUuid())
                .citizenId(surveyResponse.getCitizenId())
                .sectionResponses(enrichedSectionResponses)
                .auditDetails(auditDetails)
                .build();

        return ScorecardSubmitResponse.builder().scorecardAnswerResponse(scorecardAnswerResponse).build();

    }

    public List<Section> fetchSectionListBasedOnSurveyId(String surveyId) {
        List<Section> sectionList = surveyRepository.fetchSectionListBasedOnSurveyId(surveyId);
        if (CollectionUtils.isEmpty(sectionList))
            return new ArrayList<>();
        return sectionList;
    }

    public List<QuestionWeightage> fetchQuestionsWeightageListBySurveyAndSection(String surveyId, String sectionId) {
        List<QuestionWeightage> questionList = surveyRepository.fetchQuestionsWeightageListBySurveyAndSection(surveyId, sectionId);
        if (CollectionUtils.isEmpty(questionList))
            return new ArrayList<>();
        return questionList;
    }

    private String getExistingAnswerUuid(String answerUuid) {
       return surveyRepository.getExistingAnswerUuid(answerUuid);
    }

    private String getExistingAnswerDetailUuid(String answerUuid) {
        List<AnswerDetail> existingDetails = surveyRepository.getAnswerDetailsByAnswerUuid(answerUuid);
        return existingDetails.isEmpty() ? null : existingDetails.get(0).getUuid();
    }

    public String fetchTenantIdBasedOnSurveyId(String surveyId) {
        return surveyRepository.fetchTenantIdBasedOnSurveyId(surveyId);
    }

    public ScorecardAnswerResponse getAnswers(AnswerFetchCriteria criteria) {
        if(criteria.getSurveyUuid()==null || criteria.getCitizenId()==null){
            throw new CustomException("EG_SS_SURVEY_UUID_CITIZEN_UUID_ERR","surveyUuid and citizenUuid cannot be null");
        }
            List<AnswerNew> answers = surveyRepository.getAnswers(criteria.getSurveyUuid(),criteria.getCitizenId(),criteria.getTenantId());
        List<SurveyResponseNew> surveyResponseStatus = surveyRepository.getSurveyResponseDetails(criteria.getSurveyUuid(), criteria.getCitizenId(), criteria.getTenantId());
        SurveyResponseNew surveyResponseNew;
        if (surveyResponseStatus.isEmpty()) {
            surveyResponseNew = null;
        }
        else {
            surveyResponseNew = surveyResponseStatus.get(0);
        }
        ScorecardAnswerResponse scorecardAnswerResponse = buildScorecardAnswerResponse(answers, criteria);
        if(surveyResponseNew!=null) {
            scorecardAnswerResponse.setTenantId(surveyResponseNew.getTenantId());
            scorecardAnswerResponse.setStatus(surveyResponseNew.getStatus().toString());
            scorecardAnswerResponse.setLocality(surveyResponseNew.getLocality());
            scorecardAnswerResponse.setCoordinates(surveyResponseNew.getCoordinates());
        }
        return scorecardAnswerResponse;
    }

    private ScorecardAnswerResponse buildScorecardAnswerResponse(List<AnswerNew> answers, AnswerFetchCriteria criteria) {
        Map<String, List<ScorecardQuestionResponse>> sectionResponsesMap = new HashMap<>();
        for (AnswerNew answer : answers) {
            ScorecardQuestionResponse questionResponse = ScorecardQuestionResponse.builder()
                    .questionUuid(answer.getQuestionUuid())
                    .questionStatement(answer.getQuestionStatement())
                    .answerUuid(answer.getUuid())
                    .comments(answer.getComments())
                    .answerResponse(answer)
                    .build();

            sectionResponsesMap.computeIfAbsent(answer.getSectionUuid(), k -> new ArrayList<>()).add(questionResponse);
        }

        List<ScorecardSectionResponse> sectionResponses = sectionResponsesMap.entrySet().stream()
                .map(entry -> ScorecardSectionResponse.builder()
                        .sectionUuid(entry.getKey())
                        .questionResponses(entry.getValue())
                        .build())
                .collect(Collectors.toList());

        return ScorecardAnswerResponse.builder()
                .surveyUuid(criteria.getSurveyUuid())
                .citizenId(criteria.getCitizenId())
                .sectionResponses(sectionResponses)
                .build();
    }

    public ScorecardAnswerResponse getAnswersForPlainSearch(AnswerFetchCriteria criteria) {

        List<AnswerNew> answers = surveyRepository.getAnswersForPlainSearch(criteria.getSurveyUuid(),criteria.getCitizenId(),criteria.getTenantId());
        List<SurveyResponseNew> surveyResponseStatus = surveyRepository.getSurveyResponseDetails(criteria.getSurveyUuid(), criteria.getCitizenId(), criteria.getTenantId());
        SurveyResponseNew surveyResponseNew;
        if (surveyResponseStatus.isEmpty()) {
            surveyResponseNew = null;
        }
        else {
            surveyResponseNew = surveyResponseStatus.get(0);
        }
        ScorecardAnswerResponse scorecardAnswerResponse = buildScorecardAnswerResponse(answers, criteria);
        if(surveyResponseNew!=null) {
            scorecardAnswerResponse.setTenantId(surveyResponseNew.getTenantId());
            scorecardAnswerResponse.setStatus(surveyResponseNew.getStatus().toString());
            scorecardAnswerResponse.setLocality(surveyResponseNew.getLocality());
            scorecardAnswerResponse.setCoordinates(surveyResponseNew.getCoordinates());
        }
        return scorecardAnswerResponse;
    }

}
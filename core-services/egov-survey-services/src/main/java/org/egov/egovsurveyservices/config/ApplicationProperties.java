package org.egov.egovsurveyservices.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class ApplicationProperties {

    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.search.endpoint}")
    private String localizationEndpoint;

    @Value("${ss.notification.fallback.locale}")
    private String fallBackLocale;

    @Value("${ss.notification.ui.host}")
    private String notificationUiHost;

    @Value("${ss.notification.ui.redirect.url}")
    private String notificationEndpoint;

    @Value("${egov.ss.default.limit}")
    private Integer defaultLimit;

    @Value("${egov.ss.default.offset}")
    private Integer defaultOffset;

    @Value("${egov.ss.max.limit}")
    private Integer maxSearchLimit;

    @Value("${ss.notification.action.code}")
    private String surveyActionCode;

    @Value("${ss.notification.event.topic}")
    private String userEventTopic;

    @Value("${persister.save.survey.topic}")
    private String saveSurveyTopic;

    @Value("${persister.update.document.topic}")
    private String updateDocumentTopic;

    @Value("${persister.delete.document.topic}")
    private String deleteDocumentTopic;

    @Value("${egov.ss.survey.save.answer}")
    private String saveAnswerTopic ;

    @Value("${egov.ss.category.save}")
    private String saveCategoryTopic ;

    @Value("${egov.ss.category.update}")
    private String updateCategoryTopic ;

    @Value("${egov.ss.question.save}")
    private String saveQuestionTopic ;

    @Value("${egov.ss.question.update}")
    private String updateQuestionTopic ;

    @Value("${egov.ss.max.create.limit}")
    private Integer maxCreateLimit ;
    
    @Value("${egov.ss.createsurvey.create.topic}")
    private String createScorecardSurveyTopic ;

    @Value("${egov.ss.updatesurvey.active.topic}")
    private String updateActiveSurveyTopic ;

    @Value("${egov.ss.scorecardsurvey.submitanswer.topic}")
    private String submitAnswerScorecardSurveyTopic ;

    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.search.endpoint}")
    private String userSearchEndpoint;
}

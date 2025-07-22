package org.upyog.tp.config;

import javax.annotation.PostConstruct;

import org.egov.tracer.config.TracerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Component
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Import({ TracerConfiguration.class })
public class TreePruningConfiguration {

    @Value("${egov.workflow.host}")
    private String wfHost;

    @Value("${egov.workflow.transition.path}")
    private String wfTransitionPath;

    @Value("${egov.workflow.businessservice.search.path}")
    private String wfBusinessServiceSearchPath;

    @Value("${egov.workflow.processinstance.search.path}")
    private String wfProcessSearchPath;

    @Value("${egov.workflow.processinstance.search.path}")
    private String wfProcessInstanceSearchPath;

    /** Used for application no creation **/

    @Value("${egov.idgen.tree.pruning.booking.id.name}")
    private String treePruningApplicationKey;

    @Value("${egov.usr.events.pay.link}")
    private String payLink;

    @Value("${egov.idgen.tree.pruning.booking.id.format}")
    private String treePruningApplicationFormat;

    // Kafka topics for saving water tanker data
    @Value("${persister.create.tree-pruning.topic}")
    private String treePruningApplicationSaveTopic;

    @Value("${persister.create.tree-pruning.with.profile.topic}")
    private String treePruningApplicationWithProfileSaveTopic;

    @Value("${isUserProfileEnabled}")
    private Boolean isUserProfileEnabled;

    @Value("${persister.update.tree-pruning.topic}")
    private String treePruningApplicationUpdateTopic;

    // Idgen Config
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${egov.url.shortner.host}")
    private String urlShortnerHost;

    @Value("${egov.url.shortner.endpoint}")
    private String urShortnerPath;

    @Value("${egov.url.shortner.endpoint}")
    private String shortenerEndpoint;

    // User config
    @Value("${egov.user.host}")
    private String userHost;

    @Value("${egov.user.context.path}")
    private String userContextPath;

    @Value("${egov.user.create.path}")
    private String userCreateEndpoint;

    @Value("${egov.user.search.path}")
    private String userSearchEndpoint;

    @Value("${egov.user.update.path}")
    private String userUpdateEndpoint;

    @Value("${egov.user.v2.create.path}")
    private String userCreateEndpointV2;

    @Value("${egov.user.create.address.endpointv2}")
    private String userCreateAddressEndpointV2;

    @Value("${egov.user.v2.search.path}")
    private String userSearchEndpointV2;

    // Pagination config for search results
    @Value("${upyog.request.service.default.limit}")
    private Integer defaultLimit;

    @Value("${upyog.request.service.default.offset}")
    private Integer defaultOffset;

    @Value("${upyog.request.service.max.limit}")
    private Integer maxSearchLimit;

    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsPath;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsEndpoint;

    @Value("${upyog.mdms.v2.host}")
    private String mdmsV2Host;

    @Value("${upyog.mdms.v2.search.endpoint}")
    private String mdmsV2Endpoint;

    @Value("${upyog.mdms.v2.enabled}")
    private boolean mdmsV2Enabled;

    @PostConstruct
    public void init() {
        if (mdmsV2Enabled) {
            mdmsHost = mdmsV2Host;
            mdmsEndpoint = mdmsV2Endpoint;
        }
    }

    // Billing Service configs
    @Value("${egov.billingservice.host}")
    private String billingHost;

    @Value("${egov.demand.create.endpoint}")
    private String demandCreateEndpoint;

    @Value("${egov.demand.update.endpoint}")
    private String demandUpdateEndpoint;

    @Value("${egov.demand.search.endpoint}")
    private String demandSearchEndpoint;

    @Value("${egov.msg.pay.link}")
    private String payLinkSMS;

    // NOTIFICATION TOPICS
    @Value("${kafka.topics.notification.sms}")
    private String smsNotifTopic;

    @Value("${kafka.topics.notification.email}")
    private String emailNotifTopic;

    @Value("${kafka.topics.receipt.create}")
    private String receiptCreateTopic;

    @Value("${egov.localization.statelevel}")
    private Boolean isStateLevel;

    @Value("${notif.sms.enabled}")
    private Boolean isSMSNotificationEnabled;

    @Value("${notif.email.enabled}")
    private Boolean isEmailNotificationEnabled;

    @Value("${egov.localization.statelevel}")
    private Boolean isLocalizationStateLevel;

    // USER EVENTS
    @Value("${egov.ui.app.host}")
    private String uiAppHost;

    @Value("${egov.usr.events.create.topic}")
    private String saveUserEventsTopic;

    @Value("${egov.user.event.notification.enabled}")
    private Boolean isUserEventsNotificationEnabled;

    // Localization
    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    @Value("${egov.localization.fallback.locale}")
    private String fallBackLocale;

    @Value("${egov.application.view.link}")
    private String viewApplicationLink;

    @Value("${rs.tp.business.service.name}")
    private String treePruningBusinessService;

    @Value("${rs.module.name}")
    private String moduleName;

    @Value("${rs.tp.module.name}")
    private String tpModuleName;

    @Value("${egov.rs.avg.rating.comment.mandatory}")
    private String averageRatingCommentMandatory;

}

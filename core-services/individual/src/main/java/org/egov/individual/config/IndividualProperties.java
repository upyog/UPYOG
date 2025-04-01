package org.egov.individual.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Component
public class IndividualProperties {

    @Value("${individual.producer.save.topic}")
    private String saveIndividualTopic;

    @Value("${individual.producer.update.topic}")
    private String updateIndividualTopic;

    @Value("${individual.producer.delete.topic}")
    private String deleteIndividualTopic;

    @Value("${individual.producer.update.user.id.topic}")
    private String updateUserIdTopic;

    @Value("${individual.consumer.bulk.create.topic}")
    private String bulkSaveIndividualTopic;

    @Value("${individual.consumer.bulk.update.topic}")
    private String bulkUpdateIndividualTopic;

    @Value("${individual.consumer.bulk.delete.topic}")
    private String bulkDeleteIndividualTopic;

    @Value("${idgen.individual.id.format}")
    private String individualId;

    @Value("${aadhaar.pattern}")
    private String aadhaarPattern;

    @Value("${mobile.pattern}")
    private String mobilePattern;

    @Value(("${state.level.tenant.id}"))
    private String stateLevelTenantId;

    @Value(("${user.sync.enabled}"))
    private boolean userSyncEnabled;

    @Value(("${user.service.user.type}"))
    private String userServiceUserType;

    @Value(("${user.service.account.locked}"))
    private boolean userServiceAccountLocked;

    //SMS notification
    @Value("${notification.sms.enabled}")
    private Boolean isSMSEnabled;

    @Value("${kafka.topics.notification.sms}")
    private String smsNotifTopic;

    @Value("${notification.sms.disabled.roles}")
    private List<String> smsDisabledRoles;

    //Localization
    @Value("${egov.localization.host}")
    private String localizationHost;

    @Value("${egov.localization.context.path}")
    private String localizationContextPath;

    @Value("${egov.localization.search.endpoint}")
    private String localizationSearchEndpoint;

    @Value("${egov.localization.statelevel}")
    private Boolean isLocalizationStateLevel;
}

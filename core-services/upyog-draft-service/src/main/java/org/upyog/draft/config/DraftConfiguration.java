package org.upyog.draft.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;

@Component
@Getter
public class DraftConfiguration {

    @Value("${app.timezone}")
    private String timeZone;

    @Value("${draft.active.ttl.days:90}")
    private int activeTtlDays;

    @Value("${draft.submitted.retention.days:7}")
    private int submittedRetentionDays;

    @Value("${draft.discarded.retention.days:7}")
    private int discardedRetentionDays;

    @Value("${draft.orphan.reconciliation.enabled:true}")
    private boolean orphanReconciliationEnabled;

    @Value("${tl.services.host:http://localhost:8079}")
    private String tlServicesHost;

    @Value("${tl.services.search.path:/tl-services/v1/_search}")
    private String tlServicesSearchPath;

    @Value("${persister.save.draft.topic}")
    private String saveDraftTopic;

    @Value("${persister.update.draft.topic}")
    private String updateDraftTopic;

    @Value("${persister.update.draft.status.topic}")
    private String updateDraftStatusTopic;

    @Value("${persister.delete.draft.topic}")
    private String deleteDraftTopic;

    @PostConstruct
    public void initialize() {
        TimeZone.setDefault(TimeZone.getTimeZone(timeZone));
    }
}

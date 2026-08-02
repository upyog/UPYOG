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

    @Value("${draft.active.ttl.days}")
    private int activeTtlDays;

    @Value("${draft.submitted.retention.days}")
    private int submittedRetentionDays;

    @Value("${draft.discarded.retention.days}")
    private int discardedRetentionDays;

    @Value("${draft.orphan.reconciliation.enabled}")
    private boolean orphanReconciliationEnabled;

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

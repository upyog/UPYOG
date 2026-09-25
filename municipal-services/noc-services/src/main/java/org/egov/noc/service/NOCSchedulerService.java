package org.egov.noc.service;

import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.UserResponse;
import org.egov.noc.web.model.aai.AAIStatusResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.List;

/**
 * Scheduler for syncing NOC statuses with AAI NOCAS
 */
@Slf4j
@Service
public class NOCSchedulerService {

    @Autowired
    private NOCConfiguration config;

    @Autowired
    private NOCService nocService;

    @Autowired
    private AAINOCASIntegrationService aaiIntegrationService;

    @Autowired
    private NOCStatusUpdateService nocStatusUpdateService;

    @Autowired
    private UserService userService;

    /**
     * Scheduled job to sync NOC statuses with AAI NOCAS
     * shedlock is used to prevent multiple instances from executing simultaneously
     * pending aai nocs applications are fetched and their statuses are updated
     */
    @Scheduled(cron = "${scheduler.aai.noc.status.sync.cron}")
    @SchedulerLock(
        name = "NOCSchedulerService_syncAAINOCStatus", 
        lockAtLeastFor = "${scheduler.aai.noc.status.sync.lock.at.least.for}",
        lockAtMostFor = "${scheduler.aai.noc.status.sync.lock.at.most.for}"
    )
    public void syncAAINOCStatus() {
        if (!config.getSchedulerEnabled() || !config.getAaiNocasEnabled()) {
            return;
        }
        log.info("------AAI sync: started-------");
        try {
            RequestInfo requestInfo = createSystemRequestInfo();
            // below method call just checks if there are any pending applications, otherwise returns
            List<Noc> pendingNocs = nocService.fetchNewAAINOCs(null);
            
            if (CollectionUtils.isEmpty(pendingNocs)) {
                log.info("AAI sync: no pending applications");
                return;
            }

            AAIStatusResponse aaiResponse = aaiIntegrationService.fetchNOCStatusFromAAI();
            if (aaiResponse == null || !Boolean.TRUE.equals(aaiResponse.getSuccess())) {
                log.error("AAI sync: failed to fetch status");
                return;
            }

            List<Noc> updatedNocs = nocStatusUpdateService.updateNOCStatusFromAAI(aaiResponse, requestInfo);
            log.info("AAI sync: completed, updated {}", updatedNocs.size());

        } catch (Exception e) {
            log.error("AAI sync: error", e);
        }
    }

    /**
     * Creates system RequestInfo for scheduler operations
     * 
     * @return RequestInfo object
     */
    private RequestInfo createSystemRequestInfo() {
        String internalUserName = config.getInternalMicroserviceUserName();
        String stateLevelTenant = config.getAssamStateCode();
        UserResponse userResponse = userService.searchByUserName(internalUserName, stateLevelTenant);

        if (userResponse == null || userResponse.getUser() == null || userResponse.getUser().isEmpty()) {
            throw new RuntimeException("Internal microservice user not found for scheduler operations");
        }

        User systemUser = userResponse.getUser().get(0);
        long currentTime = Instant.now().toEpochMilli();
        
        return RequestInfo.builder()
                .apiId("noc-scheduler")
                .ver("1.0")
                .ts(currentTime)
                .action("sync")
                .did("internal")
                .key("internal")
                .msgId("noc-scheduler-" + currentTime)
                .authToken("SYSTEM_INTERNAL_AUTH")
                .userInfo(systemUser)
                .build();
    }

    /**
     * Manually triggers status sync
     */
    public void triggerManualSync() {
        syncAAINOCStatus();
    }

    /**
     * Manually triggers status sync for a specific UNIQUE ID
     * 
     * @param uniqueId UNIQUE ID (BPA application number / sourceRefId) to sync
     */
    public void triggerManualSyncByUniqueId(String uniqueId) {
        if (!config.getAaiNocasEnabled() || uniqueId == null || uniqueId.trim().isEmpty()) {
            return;
        }

        try {
            RequestInfo requestInfo = createSystemRequestInfo();
            AAIStatusResponse aaiResponse = aaiIntegrationService.fetchNOCStatusByUniqueId(uniqueId, requestInfo);

            if (aaiResponse == null || !Boolean.TRUE.equals(aaiResponse.getSuccess())) {
                log.error("AAI sync: failed for {}", uniqueId);
                return;
            }

            List<Noc> updatedNocs = nocStatusUpdateService.updateNOCStatusFromAAI(aaiResponse, requestInfo);
            log.info("AAI sync: manual trigger for {}, updated {}", uniqueId, updatedNocs.size());
        } catch (Exception e) {
            log.error("AAI sync: manual trigger failed for {}", uniqueId, e);
        }
    }

    /**
     * Gets count of pending applications
     * 
     * @return Application count
     */
    public int getPendingApplicationsCount() {
        try {
            List<Noc> pendingNocs = nocService.fetchNewAAINOCs(null);
            return pendingNocs != null ? pendingNocs.size() : 0;
        } catch (Exception e) {
            log.error("Error getting count", e);
            return -1;
        }
    }
}


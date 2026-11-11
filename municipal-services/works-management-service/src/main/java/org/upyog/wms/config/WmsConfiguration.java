package org.upyog.wms.config;

import java.time.ZoneId;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/** All externalised properties of the service in one place (DIGIT style). */
@Getter
@Component
public class WmsConfiguration {

    // Kafka topics (egov-persister)
    @Value("${wms.kafka.topic.save-action-plan}")
    private String saveActionPlanTopic;

    @Value("${wms.kafka.topic.update-action-plan}")
    private String updateActionPlanTopic;

    @Value("${wms.kafka.topic.update-action-plan-workflow}")
    private String updateActionPlanWorkflowTopic;

    // Business
    @Value("${wms.business.timezone}")
    private String businessTimezone;

    @Value("${wms.search.default-limit}")
    private Integer defaultLimit;

    @Value("${wms.search.max-limit}")
    private Integer maxLimit;

    // Workflow
    @Value("${wms.workflow.enabled}")
    private Boolean workflowEnabled;

    @Value("${wms.workflow.business-service}")
    private String businessService;

    @Value("${wms.workflow.module-name}")
    private String moduleName;

    @Value("${egov.workflow.host}")
    private String workflowHost;

    @Value("${egov.workflow.transition.path}")
    private String workflowTransitionPath;

    // IdGen
    @Value("${egov.idgen.host}")
    private String idGenHost;

    @Value("${egov.idgen.path}")
    private String idGenPath;

    @Value("${wms.idgen.application-number.name}")
    private String applicationNumberIdName;

    @Value("${wms.idgen.action-plan-number.name}")
    private String actionPlanNumberIdName;

    @Value("${wms.idgen.component-number.name}")
    private String componentNumberIdName;

    // MDMS
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.path}")
    private String mdmsSearchPath;

    @Value("${wms.mdms.cache.ttl-seconds}")
    private Long mdmsCacheTtlSeconds;

    // Calculator
    @Value("${wms.calculator.enabled}")
    private Boolean calculatorEnabled;

    @Value("${wms.calculator.host}")
    private String calculatorHost;

    @Value("${wms.calculator.calculate.path}")
    private String calculatorCalculatePath;

    public ZoneId businessZone() {
        return ZoneId.of(businessTimezone);
    }
}

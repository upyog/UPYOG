package org.egov.ndc.config;

import static org.junit.jupiter.api.Assertions.*;

import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "app.timezone=Asia/Kolkata",
        "kafka.topics.notification.sms=test-sms-topic",
        "notification.sms.enabled=true",
        "egov.localization.host=http://localhost:8080",
        "egov.localization.context.path=/localization",
        "egov.localization.search.endpoint=/search",
        "egov.localization.statelevel=true",
        "egov.idgen.host=http://localhost:8081",
        "egov.idgen.path=/idgen",
        "egov.idgen.ndc.application.id=NDC-APP-ID",
        "workflow.context.path=/workflow",
        "workflow.transition.path=/transition",
        "workflow.businessservice.search.path=/businessservice",
        "workflow.process.path=/process",
        "egov.mdms.host=http://localhost:8082",
        "egov.mdms.search.endpoint=/mdms/search",
        "persister.save.ndc.topic=save-topic",
        "persister.update.ndc.topic=update-topic",
        "persister.delete.ndc.topic=delete-topic",
        "egov.ndc.pagination.default.limit=10",
        "egov.ndc.pagination.default.offset=0",
        "egov.ndc.pagination.max.limit=100",
        "ndc.offline.doc.required=true",
        "ndc.module.code=NDC",
        "ndc.taxhead.master.code=NDC_TAX",
        "egov.billingservice.host=http://localhost:8083",
        "egov.demand.create.endpoint=/demand/create",
        "egov.billingservice.fetch.bill=/bill/fetch",
        "egov.ndccalculator.host=http://localhost:8084",
        "egov.ndccalculator.endpoint=/calculate",
        "spring.kafka.consumer.group-id=ndc-group"
})
class NDCConfigurationTest {

    @Autowired
    private NDCConfiguration config;

    @Test
    void testTimeZoneInitialization() {
        assertEquals("Asia/Kolkata", config.getTimeZone());
        assertEquals(TimeZone.getTimeZone("Asia/Kolkata"), TimeZone.getDefault());
    }

    @Test
    void testKafkaProperties() {
        assertEquals("test-sms-topic", config.getSmsNotifTopic());
        assertTrue(config.getIsSMSEnabled());
        assertEquals("ndc-group", config.getKafkaGroupId());
    }

    @Test
    void testLocalizationProperties() {
        assertEquals("http://localhost:8080", config.getLocalizationHost());
        assertEquals("/localization", config.getLocalizationContextPath());
        assertEquals("/search", config.getLocalizationSearchEndpoint());
        assertTrue(config.getIsLocalizationStateLevel());
    }

    @Test
    void testPaginationProperties() {
        assertEquals(10, config.getDefaultLimit());
        assertEquals(0, config.getDefaultOffset());
        assertEquals(100, config.getMaxSearchLimit());
    }

    @Test
    void testBillingProperties() {
        assertEquals("http://localhost:8083", config.getBillingServiceHost());
        assertEquals("/demand/create", config.getDemandCreateEndpoint());
        assertEquals("/bill/fetch", config.getFetchBillEndpoint());
    }

    @Test
    void testCalculatorProperties() {
        assertEquals("http://localhost:8084", config.getNdcCalculatorHost());
        assertEquals("/calculate", config.getNdcCalculatorEndpoint());
    }

    @Test
    void testIdGenProperties() {
        assertEquals("http://localhost:8081", config.getIdGenHost());
        assertEquals("/idgen", config.getIdGenPath());
        assertEquals("NDC-APP-ID", config.getApplicationNoIdgenName());
    }

    @Test
    void testWorkflowProperties() {
        assertEquals("/workflow", config.getWfHost());
        assertEquals("/transition", config.getWfTransitionPath());
        assertEquals("/businessservice", config.getWfBusinessServiceSearchPath());
        assertEquals("/process", config.getWfProcessPath());
    }

    @Test
    void testMdmsProperties() {
        assertEquals("http://localhost:8082", config.getMdmsHost());
        assertEquals("/mdms/search", config.getMdmsEndPoint());
    }

    @Test
    void testPersisterTopics() {
        assertEquals("save-topic", config.getSaveTopic());
        assertEquals("update-topic", config.getUpdateTopic());
        assertEquals("delete-topic", config.getDeleteTopic());
    }

    @Test
    void testWfProcessSearchPath() {
        assertEquals("/process", config.getWfProcessSearchPath());
    }

    @Test
    void testNdcModuleProperties() {
        assertTrue(config.getNdcOfflineDocRequired());
        assertEquals("NDC", config.getModuleCode());
        assertEquals("NDC_TAX", config.getTaxHeadMasterCode());
    }

    @Test
    void testAllArgsConstructor() {
        NDCConfiguration config = new NDCConfiguration(
                "Asia/Kolkata", "sms-topic", true, "loc-host", "loc-path", "loc-endpoint", true,
                "idgen-host", "idgen-path", "app-id", "wf-host", "wf-trans", "wf-biz", "wf-process",
                "mdms-host", "mdms-endpoint", "save-topic", "update-topic", "delete-topic",
                10, 0, 100, true, "module-code", "taxhead-code", "billing-host", "demand-endpoint",
                "bill-endpoint", "calc-host", "calc-endpoint", "group-id", "wf-process-search"
        );
        assertEquals("Asia/Kolkata", config.getTimeZone());
        assertEquals("sms-topic", config.getSmsNotifTopic());
    }

    @Test
    void testBuilderPattern() {
        NDCConfiguration config = NDCConfiguration.builder()
                .timeZone("Asia/Kolkata")
                .build();

        assertEquals("Asia/Kolkata", config.getTimeZone());
    }

    @Test
    void testSetterMethods() {
        NDCConfiguration config = new NDCConfiguration();

        config.setTimeZone("Asia/Kolkata");
        config.setIdGenHost("http://localhost:8081");
        config.setDefaultLimit(25);

        assertEquals("Asia/Kolkata", config.getTimeZone());
        assertEquals("http://localhost:8081", config.getIdGenHost());
        assertEquals(25, config.getDefaultLimit());
    }


}
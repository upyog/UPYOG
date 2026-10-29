package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.egov.common.contract.request.RequestInfo;
import upyog.notification.model.NotificationConfig;
import upyog.notification.model.NotificationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class NotificationContextBuilderTest {

    private final NotificationContextBuilder builder = new NotificationContextBuilder(new ObjectMapper());

    @Test
    public void build_extractsRecipientsAndVariablesFromMdmsPaths() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("RequestInfo", new RequestInfo());
        payload.put("Allotments", List.of(Map.of(
                "tenantId", "pg.citya",
                "alloteeName", "Ravi",
                "allotmentNo", "EST-AL-001",
                "mobileNo", "9999999999",
                "userUuid", "user-1",
                "emailId", "ravi@example.com"
        )));

        NotificationConfig config = NotificationConfig.builder()
                .module("EstateManagement")
                .action("ALLOTMENT_CREATED")
                .triggerTopic("save-allotment-details")
                .localizationModule("rainmaker-est")
                .recipientMobilePath("$.Allotments[0].mobileNo")
                .recipientUuidPath("$.Allotments[0].userUuid")
                .recipientEmailPath("$.Allotments[0].emailId")
                .variables(Map.of(
                        "alloteeName", "$.Allotments[0].alloteeName",
                        "allotmentNo", "$.Allotments[0].allotmentNo"))
                .messages(Map.of(
                        "SMS", "Dear {alloteeName}, allotment {allotmentNo} is created."))
                .build();

        NotificationContext context = builder.build(payload, config, new RequestInfo());

        assertNotNull(context);
        assertEquals("pg.citya", context.getTenantId());
        assertTrue(context.getMobileNumbers().contains("9999999999"));
        assertEquals("Ravi", context.getTemplateVariables().get("alloteeName"));
        assertEquals("Dear {alloteeName}, allotment {allotmentNo} is created.",
                context.getMessageTemplates().get("SMS"));
    }

    @Test
    public void matchesTrigger_requiresStatusWhenConfigured() throws Exception {
        NotificationConfig config = NotificationConfig.builder()
                .triggerTopic("update-allotment-details")
                .triggerField("$.Allotments[0].status")
                .triggerValue("PAID")
                .build();

        Map<String, Object> paidPayload = Map.of(
                "Allotments", List.of(Map.of("status", "PAID")));

        assertTrue(builder.matchesTrigger(paidPayload, config, "update-allotment-details"));
        assertTrue(!builder.matchesTrigger(paidPayload, config, "save-allotment-details"));
    }
}

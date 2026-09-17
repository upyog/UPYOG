package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.common.contract.request.RequestInfo;
import org.junit.Test;
import upyog.notification.config.NotificationProperties;
import upyog.notification.model.NotificationChannel;
import upyog.notification.model.NotificationContext;
import upyog.repository.ServiceRequestRepository;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;

public class NotificationMessageServiceTest {

    @Test
    public void resolveMessage_usesMdmsTemplateWhenConfigured() {
        NotificationProperties properties = new NotificationProperties();
        properties.setMdmsMessagesEnabled(true);
        properties.setLocalizationFallbackEnabled(false);

        NotificationMessageService service = new NotificationMessageService(
                properties,
                new LocalizationMessageService(properties, mock(ServiceRequestRepository.class), new ObjectMapper())
        );

        NotificationContext context = NotificationContext.builder()
                .requestInfo(new RequestInfo())
                .module("EstateManagement")
                .action("ALLOTMENT_CREATED")
                .messageTemplates(Map.of(
                        "SMS", "Dear {alloteeName}, allotment {allotmentNo} is created."))
                .templateVariables(Map.of(
                        "alloteeName", "Ravi",
                        "allotmentNo", "EST-AL-001"))
                .build();

        String message = service.resolveMessage(context, NotificationChannel.SMS);
        assertEquals("Dear Ravi, allotment EST-AL-001 is created.", message);
    }

    @Test
    public void resolveMessage_returnsNullWhenMdmsTemplateMissingAndFallbackDisabled() {
        NotificationProperties properties = new NotificationProperties();
        properties.setMdmsMessagesEnabled(true);
        properties.setLocalizationFallbackEnabled(false);

        NotificationMessageService service = new NotificationMessageService(
                properties,
                new LocalizationMessageService(properties, mock(ServiceRequestRepository.class), new ObjectMapper())
        );

        NotificationContext context = NotificationContext.builder()
                .requestInfo(new RequestInfo())
                .action("ALLOTMENT_CREATED")
                .build();

        assertNull(service.resolveMessage(context, NotificationChannel.SMS));
    }
}

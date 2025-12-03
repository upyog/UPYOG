package org.egov.ndc.consumer;

import org.egov.ndc.service.notification.NDCNotificationService;
import org.egov.ndc.web.model.ndc.Application;
import org.egov.ndc.web.model.ndc.NdcApplicationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import java.lang.reflect.Field;


class NDCConsumerTest {

    private NDCConsumer consumer;
    private NDCNotificationService notificationService;

    public void setNotificationService(NDCNotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @BeforeEach
    void setUp() throws Exception {
        notificationService = mock(NDCNotificationService.class);
        consumer = new NDCConsumer();

        Field field = NDCConsumer.class.getDeclaredField("notificationService");
        field.setAccessible(true);
        field.set(consumer, notificationService);

    }

    @Test
    void testListenWithValidMessage() throws Exception {
        String topic = "persister.save.ndc.topic";
        String record = "{"
                + "\"RequestInfo\": {"
                + "\"apiId\": \"ndc-api\","
                + "\"ver\": \"1.0\","
                + "\"ts\": 123456789,"
                + "\"msgId\": \"msg-001\""
                + "},"
                + "\"Applications\": ["
                + "{"
                + "\"applicationNo\": \"NDC-2025-001\","
                + "\"tenantId\": \"tenant-1\""
                + "}"
                + "]"
                + "}";
        consumer.listen(record, topic);

        ArgumentCaptor<NdcApplicationRequest> captor = ArgumentCaptor.forClass(NdcApplicationRequest.class);
        verify(notificationService, times(1)).process(captor.capture());

        NdcApplicationRequest capturedRequest = captor.getValue();
        assertNotNull(capturedRequest);
        assertEquals(1, capturedRequest.getApplications().size());
        Application app = capturedRequest.getApplications().get(0);
        assertEquals("NDC-2025-001", app.getApplicationNo());
        assertEquals("tenant-1", app.getTenantId());
    }

    @Test
    void testListenWithInvalidJson() {
        String topic = "persister.update.ndc.topic";
        String invalidRecord = "{ invalid json }";

        consumer.listen(invalidRecord, topic);

        verify(notificationService, times(1)).process(any(NdcApplicationRequest.class));
    }


    }
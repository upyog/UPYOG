package upyog.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import upyog.notification.config.NotificationProperties;
import upyog.repository.ServiceRequestRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class LocalizationMessageServiceTest {

    @Test
    public void injectVariables_replacesPlaceholders() {
        LocalizationMessageService service = new LocalizationMessageService(
                new NotificationProperties(),
                mock(ServiceRequestRepository.class),
                new ObjectMapper()
        );

        Map<String, String> vars = new HashMap<>();
        vars.put("alloteeName", "Ravi");
        vars.put("allotmentNo", "EST-AL-001");

        String result = service.injectVariables(
                "Dear {alloteeName}, allotment {allotmentNo} is created.", vars);

        assertEquals("Dear Ravi, allotment EST-AL-001 is created.", result);
    }

    @Test
    public void injectVariables_keepsUnknownPlaceholders() {
        LocalizationMessageService service = new LocalizationMessageService(
                new NotificationProperties(),
                mock(ServiceRequestRepository.class),
                new ObjectMapper()
        );

        String result = service.injectVariables("Hello {missing}", Map.of());
        assertEquals("Hello {missing}", result);
    }
}

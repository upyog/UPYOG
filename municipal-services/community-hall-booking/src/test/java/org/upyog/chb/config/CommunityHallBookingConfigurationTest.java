package org.upyog.chb.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.test.util.ReflectionTestUtils;

class CommunityHallBookingConfigurationTest {

    private CommunityHallBookingConfiguration configuration;

    @BeforeEach
    void setUp() {
        configuration = new CommunityHallBookingConfiguration();
    }

    @Test
    void testInitializeSetsDefaultTimeZone() {
        // Arrange
        String timeZone = "Asia/Kolkata";
        ReflectionTestUtils.setField(configuration, "timeZone", timeZone);

        try (MockedStatic<TimeZone> mockedTimeZone = mockStatic(TimeZone.class)) {
            // Act
            configuration.initialize();

            // Assert
            mockedTimeZone.verify(() -> TimeZone.setDefault(TimeZone.getTimeZone(timeZone)));
        }
    }

    @Test
    void testInitUpdatesMdmsHostAndPathWhenMdmsV2Enabled() {
        // Arrange
        ReflectionTestUtils.setField(configuration, "mdmsV2Enabled", true);
        ReflectionTestUtils.setField(configuration, "mdmsV2Host", "http://mdms-v2-host");
        ReflectionTestUtils.setField(configuration, "mdmsV2Path", "/v2/mdms");

        // Act
        configuration.init();

        // Assert
        assertEquals("http://mdms-v2-host", configuration.getMdmsHost());
        assertEquals("/v2/mdms", configuration.getMdmsPath());
    }

    @Test
    void testInitDoesNotUpdateMdmsHostAndPathWhenMdmsV2Disabled() {
        // Arrange
        ReflectionTestUtils.setField(configuration, "mdmsV2Enabled", false);
        ReflectionTestUtils.setField(configuration, "mdmsHost", "http://mdms-host");
        ReflectionTestUtils.setField(configuration, "mdmsPath", "/mdms");

        // Act
        configuration.init();

        // Assert
        assertEquals("http://mdms-host", configuration.getMdmsHost());
        assertEquals("/mdms", configuration.getMdmsPath());
    }
}
package org.upyog.chb.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class CommunityHallBookingConstantsTest {

    @Test
    void testConstants() {
        // User Messages
        assertEquals("Your community hall booking details saved successfully", CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_INIT_CREATED);
        assertEquals("Your community hall booking created successfully", CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_CREATED);
        assertEquals("Your community hall booking updated successfully", CommunityHallBookingConstants.COMMUNITY_HALL_BOOKING_UPDATED);

        // JSON Path Codes
        assertEquals("$.MdmsRes.CHB", CommunityHallBookingConstants.CHB_JSONPATH_CODE);
        assertEquals("$.MdmsRes.common-masters", CommunityHallBookingConstants.COMMON_MASTER_JSONPATH_CODE);

        // Notification Placeholders
        assertEquals("{APPLICANT_NAME}", CommunityHallBookingConstants.APPLICANT_NAME);
        assertEquals("{CHB_PERMISSION_LETTER_LINK}", CommunityHallBookingConstants.CHB_PERMISSION_LETTER_LINK);

        // Workflow Constants
        assertEquals("BOOKED", CommunityHallBookingConstants.CHB_STATUS_BOOKED);
        assertEquals("MOVETOEMPLOYEE", CommunityHallBookingConstants.CHB_ACTION_MOVETOEMPLOYEE);

        // BigDecimal Constants
        assertEquals(0, CommunityHallBookingConstants.ONE_HUNDRED.compareTo(new java.math.BigDecimal(100)));
    }
}
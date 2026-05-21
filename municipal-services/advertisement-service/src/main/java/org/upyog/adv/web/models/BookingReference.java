package org.upyog.adv.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Lightweight DTO returned by repository scheduler queries.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingReference {
    private String bookingId;
    private String bookingNo;
    private String tenantId;
}

package org.upyog.dashboard.pt.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

/**
 * Root DTO carrying all extracted DB data for the Property Tax (PT) module.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PTDTO {
    private String date;
    private String module;
    private String ward;
    private String ulb;
    private String region;
    private String state;
    private PTAggregatedData combinedMetrics;
    private List<PTCollectionDTO> collectionMetrics;
}

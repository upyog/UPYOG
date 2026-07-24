package org.upyog.adapter.pt.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Root DTO carrying all extracted DB data for the Property Tax (PT) module.
 */
@Data
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
    private PTCombinedDTO combinedMetrics;
    private List<PTCollectionDTO> collectionMetrics;
}

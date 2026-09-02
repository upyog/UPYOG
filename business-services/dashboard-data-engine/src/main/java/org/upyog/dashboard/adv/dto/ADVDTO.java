package org.upyog.dashboard.adv.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ADVDTO {
    private String date;
    private String module;
    private String ward;
    private String ulb;
    private String region;
    private String state;
    private ADVAggregatedData combinedMetrics;
    private List<ADVCollectionDTO> collectionMetrics;
}

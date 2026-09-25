package upyog.web.models;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssetSearchCriteria {
    private String tenantId;
    private String estateNo;
    private String refAssetNo;
    private String buildingName;
    private String locality;
    private String assetStatus;
    private Integer limit;
    private Integer offset;
}
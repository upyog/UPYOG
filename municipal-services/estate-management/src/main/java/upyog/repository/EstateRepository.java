package upyog.repository;


import upyog.web.models.Allotment;
import upyog.web.models.AllotmentSearchCriteria;
import upyog.web.models.Asset;
import upyog.web.models.AssetSearchCriteria;

import java.util.List;

public interface EstateRepository {
    void save(String topic, Object value);

    List<Asset> searchAssets(AssetSearchCriteria criteria);
    
    List<Allotment> searchAllotments(AllotmentSearchCriteria criteria);
}

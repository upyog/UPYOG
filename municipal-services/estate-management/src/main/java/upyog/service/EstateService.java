package upyog.service;

import jakarta.validation.Valid;
import upyog.web.models.*;

public interface EstateService {
    Asset createAsset(@Valid AssetRequest request);

    AllotmentResponse createAllotment(AllotmentRequest request);

    AllotmentResponse searchAllotments(AllotmentSearchCriteria criteria);

    AssetResponse searchAssets(AssetSearchRequest searchRequest);

    Asset updateAsset(@Valid AssetRequest request);
}

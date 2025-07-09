package org.egov.infra.mdms.repository;


import org.egov.infra.mdms.model.*;
import org.json.JSONArray;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface MdmsDataRepository {
    public void create(MdmsRequest mdmsRequest);

    public void update(MdmsRequest mdmsRequest);

    public  List<Mdms> searchV2(MdmsCriteriaV2 mdmsCriteriaV2);

    public Map<String, Map<String, JSONArray>> search(MdmsCriteria mdmsCriteria);
}

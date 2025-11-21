package org.egov.asset.util;

import com.jayway.jsonpath.JsonPath;
import lombok.extern.slf4j.Slf4j;
import org.egov.asset.web.models.AssetRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

@Component
@Slf4j
public class MDMSValidator {

    /**
     * method to validate the mdms data in the request
     *
     * @param assetRequest
     */
    public void validateMdmsData(AssetRequest assetRequest, Object mdmsData) {

        Map<String, List<String>> masterData = getAttributeValues(mdmsData);
        String[] masterArray = {AssetConstants.ASSET_CLASSIFICATION, AssetConstants.ASSET_PARENT_CATEGORY,
                AssetConstants.ASSET_CATEGORY, AssetConstants.ASSET_SUB_CATEGORY};
        validateIfMasterPresent(masterArray, masterData);
    }


    /**
     * Fetches all the values of particular attribute as map of field name to
     * list
     * <p>
     * takes all the masters from each module and adds them in to a single map
     * <p>
     * note : if two masters from different modules have the same name then it
     * <p>
     * will lead to overriding of the earlier one by the latest one added to the
     * map
     *
     * @return Map of MasterData name to the list of code in the MasterData
     */
    public Map<String, List<String>> getAttributeValues(Object mdmsData) {

        List<String> modulepaths = Collections.singletonList(AssetConstants.ASSET_JSONPATH_CODE);
        final Map<String, List<String>> mdmsResMap = new HashMap<>();
        modulepaths.forEach(modulepath -> {
            try {
                mdmsResMap.putAll(JsonPath.read(mdmsData, modulepath));
            } catch (Exception e) {
                throw new CustomException(AssetErrorConstants.INVALID_TENANT_ID_MDMS_KEY,
                        AssetErrorConstants.INVALID_TENANT_ID_MDMS_MSG);
            }
        });
        return mdmsResMap;
    }

    /**
     * Validates if MasterData is properly fetched for the given MasterData
     * names
     *
     * @param masterNames
     * @param codes
     */
    private void validateIfMasterPresent(String[] masterNames, Map<String, List<String>> codes) {
        Map<String, String> errorMap = new HashMap<>();
        for (String masterName : masterNames) {
            if (CollectionUtils.isEmpty(codes.get(masterName))) {
                errorMap.put("MDMS DATA ERROR ", "Unable to fetch " + masterName + " codes from MDMS");
            }
        }
        if (!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

}

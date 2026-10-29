package org.egov.noc.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.egov.noc.util.NOCConstants;
import org.egov.noc.web.model.Noc;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AAINOCValidationService {

    /**
     * Validates and filters NOC records before fetching BPA details.
     * This prevents unnecessary BPA service calls for invalid NOCs.
     * If a NOC has coordinates, they must be in valid DMS format.
     * If invalid, the NOC is excluded and a warning is logged.
     * 
     * @param nocList List of NOC objects to validate
     * @return List of valid NOC objects
     */
    public List<Noc> validateNOCs(List<Noc> nocList) {
        if (CollectionUtils.isEmpty(nocList)) {
            return new ArrayList<>();
        }

        List<Noc> validNOCs = new ArrayList<>();

        for (Noc noc : nocList) {
            if (areCoordinatesValid(noc)) {
                validNOCs.add(noc);
            } else {
                String sourceRefId = noc.getSourceRefId();
                log.warn("Excluding AAI NOC - sourceRefId: {} - Invalid or Missing Coordinate Format", sourceRefId);
            }
        }
        return validNOCs;
    }

    /**
     * Iterates through all known coordinate keys (N, S, E, W, CENTER).
     * If a key exists, its Lat/Lon MUST be in valid DMS format.
     */
    private boolean areCoordinatesValid(Noc noc) {
        if (noc.getAdditionalDetails() == null) return true; // Optional fields

        @SuppressWarnings("unchecked")
        Map<String, Object> details = (Map<String, Object>) noc.getAdditionalDetails();

        // 1. Filter details to only include keys we care about (N, S, E, W, Center)
        // 2. If the key points to a Map, validate that Map
        // 3. Return false if ANY present coordinate is invalid
        return NOCConstants.ALL_COORDINATE_KEYS.stream()
                .map(details::get)
                .filter(obj -> obj instanceof Map)
                .allMatch(this::isCoordinateMapValid);
    }

    /**
     * Validates a single coordinate map (e.g., the map inside "EAST").
     * Must contain both Lat and Lon, and both must be DMS.
     */
    @SuppressWarnings("unchecked")
    private boolean isCoordinateMapValid(Object coordinateObj) {
        Map<String, Object> coordMap = (Map<String, Object>) coordinateObj;
        String lat = getSafeString(coordMap.get(NOCConstants.KEY_LAT));
        String lon = getSafeString(coordMap.get(NOCConstants.KEY_LON));

        // If data exists, it must be valid. If null, we assume incomplete/invalid for AAI.
        return lat != null && lon != null && isDMSFormat(lat) && isDMSFormat(lon);
    }

    private boolean isDMSFormat(String coordinate) {
        if (coordinate == null) {
            return false;
        }
        return NOCConstants.DMS_PATTERN.matcher(coordinate.trim()).matches();
    }

    private String getSafeString(Object obj) {
        return ObjectUtils.isEmpty(obj) ? null : String.valueOf(obj);
    }

}
package org.upyog.sv.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.MdmsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MdmsCacheService {

	@Autowired
	private MdmsUtil mdmsUtil;

	private final Map<String, Map<String, String>> localityCache = new HashMap<>();
	private final Map<String, Map<String, String>> vendingZoneCache = new HashMap<>();

	/**
	 * Retrieves the locality name corresponding to a given locality code for a specific tenant.
	 * <p>
	 * If the locality data is not already cached for the tenant, it will fetch the locality and vending zone data
	 * from the external MDMS/location service and cache it for future use.
	 *
	 * @param tenantId    the tenant ID for which the locality name is to be fetched
	 * @param code        the code of the locality whose name is required
	 * @param requestInfo the RequestInfo object containing user and request context
	 * @return the name of the locality corresponding to the given code, or {@code null} if not found
	 */
	
	public String getLocalityName(String tenantId, String code, RequestInfo requestInfo) {
		Map<String, String> localityMap = localityCache.get(tenantId);

		if (localityMap == null) {
			localityMap = new HashMap<>();
			Map<String, String> vendingZoneMap = new HashMap<>();
			loadLocalityAndVendingZoneMaps(tenantId, requestInfo, localityMap, vendingZoneMap);

			localityCache.put(tenantId, localityMap);
			vendingZoneCache.put(tenantId, vendingZoneMap);
		}

		return localityMap.getOrDefault(code, null); // <- fixed from `tenantId` to `code`
	}
	
	/**
	 * Retrieves the vending zone name corresponding to a given zone code for a specific tenant.
	 * <p>
	 * If the vending zone data is not already cached for the tenant, this method fetches both
	 * locality and vending zone mappings from the external MDMS/location service and caches them
	 * for future use.
	 *
	 * @param tenantId    the tenant ID for which the vending zone name is to be fetched
	 * @param code        the code of the vending zone whose name is required
	 * @param requestInfo the RequestInfo object containing user and request context
	 * @return the name of the vending zone corresponding to the given code, or {@code null} if not found
	 */
	
	public String getVendingZoneName(String tenantId, String code, RequestInfo requestInfo) {
        Map<String, String> vendingZoneMap = vendingZoneCache.get(tenantId);

        if (vendingZoneMap == null) {
            Map<String, String> localityMap = new HashMap<>();
            vendingZoneMap = new HashMap<>();
            loadLocalityAndVendingZoneMaps(tenantId, requestInfo, localityMap, vendingZoneMap);

            localityCache.put(tenantId, localityMap);
            vendingZoneCache.put(tenantId, vendingZoneMap);
        }

        return vendingZoneMap.getOrDefault(code, null);
    }

	/**
	 * Loads locality and vending zone mappings for a given tenant by retrieving boundary data from MDMS.
	 * <p>
	 * This method interacts with the MDMS Location API to fetch boundary information,
	 * extracts locality and vending zone names and codes, and populates the provided maps accordingly.
	 * It assumes the data structure follows the expected MDMS response format.
	 * <p>
	 * If the structure of the response is invalid or unexpected, the method logs a warning and exits silently.
	 *
	 * @param tenantId        the tenant ID for which boundary data is to be fetched
	 * @param requestInfo     the RequestInfo containing user and request context
	 * @param localityMap     the map to be populated with locality code-to-name mappings
	 * @param vendingZoneMap  the map to be populated with vending zone code-to-name mappings
	 */

	@SuppressWarnings("unchecked")
	private void loadLocalityAndVendingZoneMaps(String tenantId, RequestInfo requestInfo,
			Map<String, String> localityMap, Map<String, String> vendingZoneMap) {
		Object result = mdmsUtil.getLocationData(requestInfo, tenantId);

		if (!(result instanceof Map)) {
			System.out.println("MDMS result is not a Map: " + result);
			return;
		}

		Map<String, Object> resultMap = (Map<String, Object>) result;
		Object tenantBoundaryObj = resultMap.get(StreetVendingConstants.TENANTBOUNDARY);

		if (!(tenantBoundaryObj instanceof List)) {
			System.out.println("TenantBoundary is not a List: " + tenantBoundaryObj);
			return;
		}

		List<Object> tenantBoundaries = (List<Object>) tenantBoundaryObj;

		for (Object boundaryObj : tenantBoundaries) {
			if (!(boundaryObj instanceof Map))
				continue;

			Map<String, Object> boundaryMap = (Map<String, Object>) boundaryObj;
			processBoundaries(boundaryMap, localityMap, vendingZoneMap);
		}
	}
	
	/**
	 * Processes a boundary map to extract and populate locality and vending zone mappings.
	 * <p>
	 * This method iterates over a list of boundaries and maps locality codes to names.
	 * It also processes the "children" of each boundary, which represent vending zones,
	 * and maps their codes to names.
	 * <p>
	 * It assumes the structure of the boundary map follows the expected MDMS location hierarchy format.
	 *
	 * @param boundaryMap     the map representing a tenant's boundary level data from MDMS
	 * @param localityMap     the map to be populated with locality code-to-name mappings
	 * @param vendingZoneMap  the map to be populated with vending zone code-to-name mappings
	 */

	@SuppressWarnings("unchecked")
	private void processBoundaries(Map<String, Object> boundaryMap, Map<String, String> localityMap,
			Map<String, String> vendingZoneMap) {

		Object boundaryListObj = boundaryMap.get(StreetVendingConstants.BOUNDARY);
		if (!(boundaryListObj instanceof List)) {
			System.out.println("boundary is not a List: " + boundaryListObj);
			return;
		}

		List<Object> boundaryList = (List<Object>) boundaryListObj;

		for (Object b : boundaryList) {
			if (!(b instanceof Map))
				continue;

			Map<String, Object> boundary = (Map<String, Object>) b;

			// Locality
			String localityCode = boundary.get(StreetVendingConstants.CODE) != null
					? boundary.get(StreetVendingConstants.CODE).toString()
					: null;
			String localityName = boundary.get(StreetVendingConstants.NAME) != null
					? boundary.get(StreetVendingConstants.NAME).toString()
					: null;

			if (localityCode != null && localityName != null) {
				localityMap.put(localityCode, localityName);
			}

			// Vending Zones
			Object childrenObj = boundary.get("children");
			if (childrenObj instanceof List) {
				List<Object> children = (List<Object>) childrenObj;
				for (Object childObj : children) {
					if (!(childObj instanceof Map))
						continue;

					Map<String, Object> child = (Map<String, Object>) childObj;
					String zoneCode = child.get(StreetVendingConstants.CODE) != null
							? child.get(StreetVendingConstants.CODE).toString()
							: null;
					String zoneName = child.get(StreetVendingConstants.NAME) != null
							? child.get(StreetVendingConstants.NAME).toString()
							: null;

					if (zoneCode != null && zoneName != null) {
						vendingZoneMap.put(zoneCode, zoneName);
					}
				}
			}
		}
	}

}
			

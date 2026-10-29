package org.upyog.sv.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.springframework.stereotype.Service;
import org.upyog.sv.constants.StreetVendingConstants;
import org.upyog.sv.util.MdmsUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MdmsCacheService {

	private static final String CHILDREN_KEY = "children";

	private final MdmsUtil mdmsUtil;

	private final Map<String, Map<String, String>> localityCache = new HashMap<>();
	private final Map<String, Map<String, String>> vendingZoneCache = new HashMap<>();

	public MdmsCacheService(MdmsUtil mdmsUtil) {
		this.mdmsUtil = mdmsUtil;
	}

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
		Map<String, String> localityMap = getOrLoadLocalityMap(tenantId, requestInfo);
		return localityMap.getOrDefault(code, null);
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
		Map<String, String> vendingZoneMap = vendingZoneCache.computeIfAbsent(tenantId, key -> {
			Map<String, String> localityMap = new HashMap<>();
			Map<String, String> zoneMap = new HashMap<>();
			loadLocalityAndVendingZoneMaps(tenantId, requestInfo, localityMap, zoneMap);
			localityCache.put(tenantId, localityMap);
			return zoneMap;
		});
		return vendingZoneMap.getOrDefault(code, null);
	}

	private Map<String, String> getOrLoadLocalityMap(String tenantId, RequestInfo requestInfo) {
		return localityCache.computeIfAbsent(tenantId, key -> {
			Map<String, String> localityMap = new HashMap<>();
			Map<String, String> vendingZoneMap = new HashMap<>();
			loadLocalityAndVendingZoneMaps(tenantId, requestInfo, localityMap, vendingZoneMap);
			vendingZoneCache.put(tenantId, vendingZoneMap);
			return localityMap;
		});
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
			log.warn("MDMS result is not a Map: {}", result);
			return;
		}

		Map<String, Object> resultMap = (Map<String, Object>) result;
		Object tenantBoundaryObj = resultMap.get(StreetVendingConstants.TENANTBOUNDARY);

		if (!(tenantBoundaryObj instanceof List)) {
			log.warn("TenantBoundary is not a List: {}", tenantBoundaryObj);
			return;
		}

		List<Object> tenantBoundaries = (List<Object>) tenantBoundaryObj;

		for (Object boundaryObj : tenantBoundaries) {
			if (boundaryObj instanceof Map) {
				processBoundaries((Map<String, Object>) boundaryObj, localityMap, vendingZoneMap);
			}
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
			log.warn("boundary is not a List: {}", boundaryListObj);
			return;
		}

		List<Object> boundaryList = (List<Object>) boundaryListObj;

		for (Object b : boundaryList) {
			if (b instanceof Map) {
				processBoundary((Map<String, Object>) b, localityMap, vendingZoneMap);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void processBoundary(Map<String, Object> boundary, Map<String, String> localityMap,
			Map<String, String> vendingZoneMap) {
		putCodeNameMapping(boundary, localityMap);
		processVendingZoneChildren(boundary, vendingZoneMap);
	}

	private void putCodeNameMapping(Map<String, Object> source, Map<String, String> targetMap) {
		String code = getStringValue(source, StreetVendingConstants.CODE);
		String name = getStringValue(source, StreetVendingConstants.NAME);
		if (code != null && name != null) {
			targetMap.put(code, name);
		}
	}

	private String getStringValue(Map<String, Object> map, String key) {
		Object value = map.get(key);
		return value != null ? value.toString() : null;
	}

	@SuppressWarnings("unchecked")
	private void processVendingZoneChildren(Map<String, Object> boundary, Map<String, String> vendingZoneMap) {
		Object childrenObj = boundary.get(CHILDREN_KEY);
		if (!(childrenObj instanceof List)) {
			return;
		}

		List<Object> children = (List<Object>) childrenObj;
		for (Object childObj : children) {
			if (childObj instanceof Map) {
				putCodeNameMapping((Map<String, Object>) childObj, vendingZoneMap);
			}
		}
	}

}

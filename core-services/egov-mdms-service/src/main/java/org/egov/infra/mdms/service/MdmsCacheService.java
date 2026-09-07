package org.egov.infra.mdms.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.MDMSApplicationRunnerImpl;
import org.egov.infra.mdms.repository.MdmsDataRepository;
import org.egov.infra.mdms.utils.MDMSConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;

/**
 * Unified service for managing the MDMS in-memory cache (tenantMap).
 *
 * Responsibilities: 1. Load all active MDMS data from the database at
 * application startup and replace the in-memory cache with a DB snapshot. 2.
 * Process Kafka messages (create/update) to keep the cache in sync at runtime.
 *
 * Merge strategy (both DB load and Kafka updates): - Records with matching
 * "code" are updated in place. - New records are added. - Inactive records are
 * removed (Kafka updates only).
 */
@Service
@Slf4j
public class MdmsCacheService {

	private final MdmsDataRepository mdmsDataRepository;

	@Value("${egov.mdms.load.from.db.enabled}")
	private boolean dbLoadEnabled;

	@Autowired
	public MdmsCacheService(MdmsDataRepository mdmsDataRepository) {
		this.mdmsDataRepository = mdmsDataRepository;
	}

	/**
	 * Resolves tenantId to root state tenantId if the master is state-level or
	 * exists at state level.
	 */
	public static String getEffectiveTenantId(String tenantId, String moduleName, String masterName) {
		if (tenantId == null || !tenantId.contains(".")) {
			return tenantId;
		}

		String stateTenantId = tenantId.split("\\.")[0];

		Map<String, Map<String, Object>> masterConfigMap = MDMSApplicationRunnerImpl.getMasterConfigMap();
		if (masterConfigMap != null && masterConfigMap.containsKey(moduleName)) {
			Map<String, Object> moduleData = masterConfigMap.get(moduleName);
			if (moduleData != null && moduleData.containsKey(masterName)) {
				Object masterConfig = moduleData.get(masterName);
				if (masterConfig != null) {
					try {
						ObjectMapper mapper = new ObjectMapper();
						Boolean isStateLevel = JsonPath.read(mapper.writeValueAsString(masterConfig),
								MDMSConstants.STATE_LEVEL_JSONPATH);
						if (Boolean.TRUE.equals(isStateLevel)) {
							return stateTenantId;
						}
					} catch (Exception e) {
						// ignore
					}
				}
			}
		}

		return tenantId;
	}

	/**
	 * Loads all active MDMS data from the database and replaces the in-memory
	 * tenantMap cache with the DB snapshot. Called after file-based loading is
	 * complete so the runtime cache contains only the data that exists in DB when
	 * DB loading is enabled.
	 */
	/**
	 * Loads all active MDMS data from the database and merges individual records
	 * into the in-memory tenantMap cache (which was initialized from files).
	 */
	public void loadAndMergeDbData() {
		if (!dbLoadEnabled) {
			log.info("MDMS DB loading is disabled (egov.mdms.load.from.db.enabled=false). Skipping.");
			return;
		}

		log.info("Starting to load MDMS data from database...");

		try {
			List<Map<String, Object>> rows = mdmsDataRepository.searchAll();
			int recordCount = 0;
			Set<String> clearedMasters = new HashSet<>();

			for (Map<String, Object> row : rows) {
				try {
					String tenantId = (String) row.get("tenantid");
					String schemaCode = (String) row.get("schemacode");
					Object dataObj = row.get("data");
					String dbId = row.get("id") != null ? String.valueOf(row.get("id")) : null;
					String dbUniqueIdentifier = row.get("uniqueidentifier") != null
							? String.valueOf(row.get("uniqueidentifier"))
							: null;

					if (tenantId == null || schemaCode == null || dataObj == null) {
						log.warn("Skipping DB row with null tenantId/schemaCode/data: {}", row);
						continue;
					}

					String[] parts = schemaCode.split("\\.", 2);
					if (parts.length != 2) {
						log.warn("Invalid schemaCode format (expected ModuleName.MasterName): {}", schemaCode);
						continue;
					}

					String moduleName = parts[0];
					String masterName = parts[1];

					String effectiveTenantId = getEffectiveTenantId(tenantId, moduleName, masterName);

					JSONArray masterData = getOrCreateMasterData(effectiveTenantId, moduleName, masterName);

					// Clear file-loaded data for this specific master on first DB record encounter
					String masterKey = effectiveTenantId + "." + moduleName + "." + masterName;
					if (clearedMasters.add(masterKey)) {
						masterData.clear();
						log.info("Cleared file-loaded master data for {}.{} under tenant {} to replace with DB data",
								moduleName, masterName, effectiveTenantId);
					}

					if (dataObj instanceof List) {
						for (Object rec : (List<?>) dataObj) {
							upsertDbRecord(masterData, rec, moduleName, masterName, dbId, dbUniqueIdentifier);
							recordCount++;
						}
					} else {
						upsertDbRecord(masterData, dataObj, moduleName, masterName, dbId, dbUniqueIdentifier);
						recordCount++;
					}

					MDMSApplicationRunnerImpl.refreshMasterTopLevelIdState(effectiveTenantId, moduleName, masterName,
							masterData);
				} catch (Exception e) {
					log.error("Error processing DB row: {}", row, e);
				}
				
			}

			log.info("Merged {} DB records into in-memory MDMS cache.", recordCount);

		} catch (Exception e) {
			log.error("Error loading MDMS data from database. File-based cache remains intact.", e);
		}
	}

	/**
	 * Processes a Kafka message (create or update) and updates the in-memory cache.
	 */
	@SuppressWarnings("unchecked")
	public synchronized void updateCache(Map<String, Object> message) {
		Map<String, Object> mdms = (Map<String, Object>) message.get("Mdms");
		if (mdms == null) {
			log.warn("Mdms object not found in Kafka message");
			return;
		}

		String tenantId = String.valueOf(mdms.get("tenantId"));
		String schemaCode = String.valueOf(mdms.get("schemaCode"));

		String uniqueIdentifier = mdms.get("uniqueIdentifier") != null ? String.valueOf(mdms.get("uniqueIdentifier"))
				: null;

		String id = mdms.get("id") != null ? String.valueOf(mdms.get("id")) : null;

		Boolean isActive = mdms.get("isActive") == null ? Boolean.TRUE
				: Boolean.valueOf(String.valueOf(mdms.get("isActive")));

		Object data = mdms.get("data");

		if (tenantId == null || schemaCode == null) {
			log.warn("Invalid Kafka message missing tenantId/schemaCode");
			return;
		}

		String[] parts = schemaCode.split("\\.", 2);
		if (parts.length != 2) {
			log.warn("Invalid schemaCode in Kafka message: {}", schemaCode);
			return;
		}

		String moduleName = parts[0];
		String masterName = parts[1];

		String effectiveTenantId = getEffectiveTenantId(tenantId, moduleName, masterName);
		JSONArray masterData = getOrCreateMasterData(effectiveTenantId, moduleName, masterName);

		// If DB loading is enabled, verify persistence in DB before updating in-memory
		// cache
		if (dbLoadEnabled && (uniqueIdentifier != null || id != null)) {
			Map<String, Object> dbRow = fetchRecordFromDbWithRetry(tenantId, schemaCode, uniqueIdentifier, id, data, isActive);
			if (dbRow == null) {
				log.error(
						"Record for tenantId: {}, schemaCode: {}, uniqueIdentifier: {}, id: {} NOT found or data did not reflect update in DB after retries. Persistence failed or pending. Skipping cache update.",
						tenantId, schemaCode, uniqueIdentifier, id);
				return;
			}
			if (dbRow.get("data") != null) {
				data = dbRow.get("data");
			}
			if (dbRow.get("isactive") != null) {
				isActive = Boolean.valueOf(String.valueOf(dbRow.get("isactive")));
			}
			log.info("Successfully verified updated record in DB for {}.{} under tenant {}. Updating cache.", moduleName,
					masterName, effectiveTenantId);
		}

		if (!isActive) {
			removeKafkaRecord(masterData, id, uniqueIdentifier, moduleName, masterName);
			MDMSApplicationRunnerImpl.refreshMasterTopLevelIdState(effectiveTenantId, moduleName, masterName,
					masterData);
			return;
		}

		if (data instanceof List) {
			for (Object record : (List<?>) data) {
				upsertKafkaRecord(masterData, record, moduleName, masterName, id, uniqueIdentifier);
			}
		} else {
			upsertKafkaRecord(masterData, data, moduleName, masterName, id, uniqueIdentifier);
		}

		MDMSApplicationRunnerImpl.refreshMasterTopLevelIdState(effectiveTenantId, moduleName, masterName, masterData);
	}

	private Map<String, Object> fetchRecordFromDbWithRetry(String tenantId, String schemaCode, String uniqueIdentifier,
			String id, Object expectedData, Boolean expectedIsActive) {
		int maxRetries = 5;
		int retryDelayMs = 250;
		Map<String, Object> lastFoundDbRow = null;

		for (int attempt = 1; attempt <= maxRetries; attempt++) {
			try {
				List<Map<String, Object>> dbRows = mdmsDataRepository.search(tenantId, schemaCode, uniqueIdentifier,
						id);
				if (dbRows != null && !dbRows.isEmpty()) {
					lastFoundDbRow = dbRows.get(0);
					Object dbData = lastFoundDbRow.get("data");
					Object dbIsActiveObj = lastFoundDbRow.get("isactive");
					Boolean dbIsActive = dbIsActiveObj != null ? Boolean.valueOf(String.valueOf(dbIsActiveObj)) : Boolean.TRUE;

					boolean isActiveMatches = (expectedIsActive == null || expectedIsActive.equals(dbIsActive));
					boolean isDataMatches = isDbDataUpdated(dbData, expectedData);

					if (isActiveMatches && isDataMatches) {
						log.info("DB record update verified on attempt {}/{} for tenant: {}, schemaCode: {}, uniqueIdentifier: {}",
								attempt, maxRetries, tenantId, schemaCode, uniqueIdentifier);
						return lastFoundDbRow;
					} else {
						log.info("DB record found for {}.{} (id: {}), but DB data/isActive does not reflect Kafka update yet. Retrying ({}/{})...",
								schemaCode, uniqueIdentifier, id, attempt, maxRetries);
					}
				}
			} catch (Exception e) {
				log.error(
						"Error searching DB for record (tenantId: {}, schemaCode: {}, uniqueIdentifier: {}, id: {}): {}",
						tenantId, schemaCode, uniqueIdentifier, id, e.getMessage());
			}

			if (attempt < maxRetries) {
				try {
					Thread.sleep(retryDelayMs);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		}
		return lastFoundDbRow;
	}

	private boolean isDbDataUpdated(Object dbData, Object kafkaData) {
		if (dbData == null && kafkaData == null) {
			return true;
		}
		if (dbData == null || kafkaData == null) {
			return false;
		}
		if (kafkaData instanceof Map && dbData instanceof Map) {
			Map<?, ?> kafkaMap = (Map<?, ?>) kafkaData;
			Map<?, ?> dbMap = (Map<?, ?>) dbData;
			for (Map.Entry<?, ?> entry : kafkaMap.entrySet()) {
				Object key = entry.getKey();
				Object kafkaVal = entry.getValue();
				Object dbVal = dbMap.get(key);
				if (!isDeepEqual(kafkaVal, dbVal)) {
					return false;
				}
			}
			return true;
		}
		return isDeepEqual(dbData, kafkaData);
	}

	/**
	 * Gets or creates the JSONArray for a given tenant/module/master path in the
	 * tenantMap.
	 */
	private JSONArray getOrCreateMasterData(String tenantId, String moduleName, String masterName) {
		return getOrCreateMasterData(MDMSApplicationRunnerImpl.getTenantMap(), tenantId, moduleName, masterName);
	}

	private JSONArray getOrCreateMasterData(Map<String, Map<String, Map<String, JSONArray>>> tenantMap, String tenantId,
			String moduleName, String masterName) {
		Map<String, Map<String, JSONArray>> moduleMap = tenantMap.computeIfAbsent(tenantId, key -> new HashMap<>());
		Map<String, JSONArray> masterMap = moduleMap.computeIfAbsent(moduleName, key -> new HashMap<>());
		return masterMap.computeIfAbsent(masterName, key -> new JSONArray());
	}

	/**
	 * Direct addition of database records to in-memory cache without code matching
	 * or merging. Replaces master array content directly.
	 */
	@SuppressWarnings("unchecked")
	private void upsertDbRecord(JSONArray masterData, Object dbRecord, String moduleName, String masterName,
			String dbId, String dbUniqueIdentifier) {
		if (dbRecord == null || !(dbRecord instanceof Map)) {
			return;
		}

		Map<String, Object> newRecordMap = new LinkedHashMap<>();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) dbRecord).entrySet()) {
			newRecordMap.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		if (dbId != null && !dbId.trim().isEmpty() && !newRecordMap.containsKey("id")) {
			newRecordMap.put("id", dbId);
		}
		masterData.add(newRecordMap);
	}

	/**
	 * Generic Kafka update message merge strategy (used during real-time Kafka
	 * events). Searches masterData array using configured uniqueKeys or generic
	 * identity matching. Updates existing record in-place via deep-merge or appends
	 * new record.
	 */
	@SuppressWarnings("unchecked")
	private void upsertKafkaRecord(JSONArray masterData, Object kafkaRecord, String moduleName, String masterName,
			String kafkaId, String kafkaUniqueIdentifier) {
		if (kafkaRecord == null || !(kafkaRecord instanceof Map)) {
			return;
		}

		Map<String, Object> newRecordMap = new LinkedHashMap<>();
		for (Map.Entry<?, ?> entry : ((Map<?, ?>) kafkaRecord).entrySet()) {
			newRecordMap.put(String.valueOf(entry.getKey()), entry.getValue());
		}
		if (kafkaId != null && !kafkaId.trim().isEmpty() && !newRecordMap.containsKey("id")) {
			newRecordMap.put("id", kafkaId);
		}

		for (int i = 0; i < masterData.size(); i++) {
			Object existing = masterData.get(i);
			if (!(existing instanceof Map))
				continue;

			Map<?, ?> existingMap = (Map<?, ?>) existing;

			if (isRecordMatching(existingMap, newRecordMap, moduleName, masterName, kafkaId, kafkaUniqueIdentifier)) {
				Map<String, Object> mergedRecord = deepMergeMaps(existingMap, newRecordMap);
				masterData.set(i, mergedRecord);
				log.info("Merged Kafka message update into cache for {}.{}", moduleName, masterName);
				return;
			}
		}

		log.info("Added new Kafka record to cache for {}.{}", moduleName, masterName);
		masterData.add(newRecordMap);
	}

	private static final java.util.regex.Pattern UUID_PATTERN = java.util.regex.Pattern
			.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");

	@SuppressWarnings("unchecked")
	private Map<String, Object> deepMergeMaps(Map<?, ?> existingMap, Map<?, ?> newMap) {
		Map<String, Object> merged = new LinkedHashMap<>();

		for (Map.Entry<?, ?> entry : existingMap.entrySet()) {
			merged.put(String.valueOf(entry.getKey()), entry.getValue());
		}

		for (Map.Entry<?, ?> entry : newMap.entrySet()) {
			String key = String.valueOf(entry.getKey());
			Object newVal = entry.getValue();
			Object existingVal = merged.get(key);

			if (existingVal instanceof Map && newVal instanceof Map) {
				merged.put(key, deepMergeMaps((Map<?, ?>) existingVal, (Map<?, ?>) newVal));
			} else {
				merged.put(key, newVal);
			}
		}

		return merged;
	}

	private boolean isRecordMatching(Map<?, ?> existingMap, Map<?, ?> newRecordMap, String moduleName,
			String masterName, String topLevelId, String topLevelUniqueIdentifier) {

		// 1. Match by 'id' if present in both and not a random UUID
		Object existingId = existingMap.get("id");
		Object newId = newRecordMap.get("id") != null ? newRecordMap.get("id") : topLevelId;
		if (newId != null && existingId != null && !isRandomUuid(newId) && !isRandomUuid(existingId)) {
			return isDeepEqual(newId, existingId);
		}

		// 2. Match by top-level 'code' field
		Object existingCode = existingMap.get("code");
		Object newCode = newRecordMap.get("code");
		if (existingCode != null && newCode != null) {
			String exCodeStr = String.valueOf(existingCode).trim();
			String newCodeStr = String.valueOf(newCode).trim();
			if (!exCodeStr.isEmpty() && !newCodeStr.isEmpty()) {
				if (exCodeStr.equalsIgnoreCase(newCodeStr)) {
					return true;
				}
			}
		}

		// 3. Match by uniqueIdentifier
		Object existingUnique = existingMap.get("uniqueIdentifier");
		Object newUnique = newRecordMap.get("uniqueIdentifier") != null ? newRecordMap.get("uniqueIdentifier")
				: topLevelUniqueIdentifier;
		if (existingUnique != null && newUnique != null) {
			if (isDeepEqual(existingUnique, newUnique)) {
				return true;
			}
		}

		// 4. Match by configured uniqueKeys from mdms-masters-config.json
		List<String> configuredKeys = getUniqueKeysFromConfig(moduleName, masterName);
		if (configuredKeys != null && !configuredKeys.isEmpty()) {
			for (String k : configuredKeys) {
				Object exVal = getNestedValue(existingMap, k);
				Object newVal = getNestedValue(newRecordMap, k);
				if (exVal != null && newVal != null && isDeepEqual(exVal, newVal)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean isRandomUuid(Object idObj) {
		if (idObj == null)
			return false;
		String str = String.valueOf(idObj).trim();
		return UUID_PATTERN.matcher(str).matches();
	}

	public void removeKafkaRecord(JSONArray masterData, String id, String uniqueIdentifier, String moduleName,
			String masterName) {
		if (id == null && uniqueIdentifier == null) {
			return;
		}
		List<String> configuredKeys = getUniqueKeysFromConfig(moduleName, masterName);

		for (int i = 0; i < masterData.size(); i++) {
			Object record = masterData.get(i);
			if (!(record instanceof Map))
				continue;

			Map<?, ?> recordMap = (Map<?, ?>) record;

			Object recId = recordMap.get("id");
			Object recUnique = recordMap.get("uniqueIdentifier");
			Object recCode = recordMap.get("code");

			if (id != null && recId != null && isDeepEqual(id, recId)) {
				masterData.remove(i);
				log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
				return;
			}

			if (uniqueIdentifier != null) {
				if (recUnique != null && isDeepEqual(uniqueIdentifier, recUnique)) {
					masterData.remove(i);
					log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
					return;
				}
				if (recId != null && isDeepEqual(uniqueIdentifier, recId)) {
					masterData.remove(i);
					log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
					return;
				}
				if (recCode != null && isDeepEqual(uniqueIdentifier, recCode)) {
					masterData.remove(i);
					log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
					return;
				}
			}

			if (configuredKeys != null && !configuredKeys.isEmpty()) {
				String firstKey = configuredKeys.get(0);
				Object existingValue = getNestedValue(recordMap, firstKey);
				if (existingValue != null
						&& (uniqueIdentifier != null && isDeepEqual(uniqueIdentifier, existingValue))) {
					masterData.remove(i);
					log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
					return;
				}
			} else {
				for (Object val : recordMap.values()) {
					if (val != null && (uniqueIdentifier != null && isDeepEqual(uniqueIdentifier, val))) {
						masterData.remove(i);
						log.info("Removed inactive MDMS cache record for {}.{}", moduleName, masterName);
						return;
					}
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<String> getUniqueKeysFromConfig(String moduleName, String masterName) {
		Map<String, Map<String, Object>> configMap = MDMSApplicationRunnerImpl.getMasterConfigMap();
		if (configMap != null && configMap.containsKey(moduleName)
				&& configMap.get(moduleName).containsKey(masterName)) {
			Object masterConfig = configMap.get(moduleName).get(masterName);
			if (masterConfig instanceof Map) {
				List<String> configKeys = (List<String>) ((Map<?, ?>) masterConfig).get("uniqueKeys");
				if (configKeys != null && !configKeys.isEmpty()) {
					List<String> cleanedKeys = new ArrayList<>();
					for (String k : configKeys) {
						cleanedKeys.add(k.replace("$.", ""));
					}
					return cleanedKeys;
				}
			}
		}
		return null;
	}

	private Object getNestedValue(Map<?, ?> map, String path) {
		if (path == null || map == null)
			return null;
		String[] parts = path.split("\\.");
		Object current = map;
		for (String part : parts) {
			if (current instanceof Map) {
				current = ((Map<?, ?>) current).get(part);
			} else {
				return null;
			}
		}
		return current;
	}

	private boolean isDeepEqual(Object obj1, Object obj2) {
		if (obj1 == obj2) {
			return true;
		}
		if (obj1 == null || obj2 == null) {
			return false;
		}
		if (obj1 instanceof Map && obj2 instanceof Map) {
			Map<?, ?> map1 = (Map<?, ?>) obj1;
			Map<?, ?> map2 = (Map<?, ?>) obj2;
			if (map1.size() != map2.size()) {
				return false;
			}
			for (Object key : map1.keySet()) {
				if (!map2.containsKey(key)) {
					return false;
				}
				if (!isDeepEqual(map1.get(key), map2.get(key))) {
					return false;
				}
			}
			return true;
		}
		if (obj1 instanceof List && obj2 instanceof List) {
			List<?> list1 = (List<?>) obj1;
			List<?> list2 = (List<?>) obj2;
			if (list1.size() != list2.size()) {
				return false;
			}
			for (int i = 0; i < list1.size(); i++) {
				if (!isDeepEqual(list1.get(i), list2.get(i))) {
					return false;
				}
			}
			return true;
		}
		return String.valueOf(obj1).equalsIgnoreCase(String.valueOf(obj2));
	}
}

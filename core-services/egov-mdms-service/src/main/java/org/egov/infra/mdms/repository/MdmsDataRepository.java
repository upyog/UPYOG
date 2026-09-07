package org.egov.infra.mdms.repository;

import java.util.List;
import java.util.Map;

public interface MdmsDataRepository {

	List<Map<String, Object>> searchAll();

	List<Map<String, Object>> search(String tenantId, String schemaCode, String uniqueIdentifier, String id);
}

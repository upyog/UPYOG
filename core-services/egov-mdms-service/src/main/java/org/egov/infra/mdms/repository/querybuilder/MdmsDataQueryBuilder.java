package org.egov.infra.mdms.repository.querybuilder;

import org.springframework.stereotype.Component;

@Component
public class MdmsDataQueryBuilder {

	private static final String LOAD_ALL_ACTIVE_MDMS_DATA_QUERY = "SELECT data.id, data.tenantid, data.uniqueidentifier, data.schemacode, data.data, data.isactive, data.createdby,"
			+ " data.lastmodifiedby, data.createdtime, data.lastmodifiedtime FROM eg_mdms_data data";

	public String getMdmsDataSearchAllQuery() {
		return LOAD_ALL_ACTIVE_MDMS_DATA_QUERY;
	}

	public String getMdmsDataSearchQuery(String tenantId, String schemaCode, String uniqueIdentifier, String id, java.util.List<Object> preparedStmtList) {
		StringBuilder query = new StringBuilder(LOAD_ALL_ACTIVE_MDMS_DATA_QUERY);
		query.append(" WHERE data.tenantid = ? AND data.schemacode = ? ");
		preparedStmtList.add(tenantId);
		preparedStmtList.add(schemaCode);

		if (uniqueIdentifier != null && !uniqueIdentifier.trim().isEmpty() && id != null && !id.trim().isEmpty()) {
			query.append(" AND (data.uniqueidentifier = ? OR data.id = ?) ");
			preparedStmtList.add(uniqueIdentifier);
			preparedStmtList.add(id);
		} else if (uniqueIdentifier != null && !uniqueIdentifier.trim().isEmpty()) {
			query.append(" AND data.uniqueidentifier = ? ");
			preparedStmtList.add(uniqueIdentifier);
		} else if (id != null && !id.trim().isEmpty()) {
			query.append(" AND data.id = ? ");
			preparedStmtList.add(id);
		}

		return query.toString();
	}
}

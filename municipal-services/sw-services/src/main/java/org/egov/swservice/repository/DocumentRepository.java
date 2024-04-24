package org.egov.swservice.repository;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.swservice.web.models.DocumentDetails;
import org.egov.swservice.web.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DocumentRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String SW_APP_DOC_INSERT_QUERY = "Insert into eg_sw_applicationdocument(id, tenantid,documenttype ,filestoreid,swid,active ,documentuid ,createdby,lastmodifiedby,createdtime ,lastmodifiedtime) values(:id, :tenantid,:documenttype ,:filestoreid,:swid,:active ,:documentuid ,:createdby,:lastmodifiedby,:createdtime ,:lastmodifiedtime);";

	private static final String SW_CONNECTION_UUID_QUERY = "select id,connectionno from eg_sw_connection where connectionno in(:connectionNo) and tenantid=:tenantId";

	public void saveDocuments(List<DocumentDetails> documents) {

		enrichDocumentsWithDetails(documents);
		List<Map<String, Object>> documentBatchValues = new ArrayList<>(documents.size());
		for (DocumentDetails document : documents) {
			documentBatchValues.add(new MapSqlParameterSource("id", UUID.randomUUID().toString())
					.addValue("documenttype", document.getDocumentType())
					.addValue("filestoreid", document.getFileStoreId())
					.addValue("swid", document.getConnectionUid())
					.addValue("active", document.getStatus().toString())
					.addValue("documentuid", null)
					.addValue("createdby", document.getUserUid())
					.addValue("lastmodifiedby", document.getUserUid())
					.addValue("createdtime", new Date().getTime())
					.addValue("lastmodifiedtime", new Date().getTime())
					.addValue("tenantid", document.getTenantId()).getValues());
		}
		try {
			namedParameterJdbcTemplate.batchUpdate(SW_APP_DOC_INSERT_QUERY,
					documentBatchValues.toArray(new Map[documents.size()]));
		} catch (Exception e) {
			log.error("Error in inserting migration documents of sewerage connection ", e);
		}
	}

	private void enrichDocumentsWithDetails(List<DocumentDetails> documents) {

		Set<String> connNos = documents.stream().map(conn -> conn.getConnectionNo()).collect(Collectors.toSet());
		Optional<String> tenantId = documents.stream().map(conn -> conn.getTenantId()).findFirst();

		Map<String, String> resultMap = new HashMap<>();
		Map<String, Object> params = new HashMap<>();
		params.put("connectionNo", connNos);
		params.put("tenantId", tenantId.get());
		List<Map<String, Object>> res = namedParameterJdbcTemplate.queryForList(SW_CONNECTION_UUID_QUERY, params);

		for (Map<String, Object> map : res) {
			resultMap.put((String) map.get("connectionno"), (String) map.get("id"));
		}
		for (DocumentDetails doc : documents) {

			doc.setConnectionUid(resultMap.get(doc.getConnectionNo()));
			doc.setStatus(Status.ACTIVE);
		}

	}
}

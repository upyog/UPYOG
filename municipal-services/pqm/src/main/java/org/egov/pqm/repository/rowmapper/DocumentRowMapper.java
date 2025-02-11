package org.egov.pqm.repository.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pqm.web.model.AuditDetails;
import org.egov.pqm.web.model.Document;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class DocumentRowMapper implements ResultSetExtractor<List<Document>> {
	
	@Autowired
	private ObjectMapper mapper;
	
	@SuppressWarnings("rawtypes")
	@Override
	public List<Document> extractData(ResultSet rs) throws SQLException {

		Map<String, Document> documentMap = new LinkedHashMap<>();
		while (rs.next()) {
			String id = rs.getString("id");
			Document currentDocument = documentMap.get(id);
			if (currentDocument == null) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
						.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
						.lastModifiedTime(rs.getLong("lastModifiedTime")).build();

				currentDocument = Document.builder().id(id).testId(rs.getString("testId")).documentType(rs.getString("documentType"))
						.documentUid(rs.getString("documentUid")).documentUri(rs.getString("documentUri"))
						.fileStoreId(rs.getString("fileStoreId"))
						.isActive(rs.getBoolean("isActive")).auditDetails(auditdetails)
						.additionalDetails(getAdditionalDetail("additionalDetails", rs)).build();

				documentMap.put(id, currentDocument);
			}
		}

		// Set the list of documents to the test object outside the loop
		return new ArrayList<>(documentMap.values());

	}
	
	private JsonNode getAdditionalDetail(String columnName, ResultSet rs) {

		JsonNode additionalDetail = null;
		try {
			PGobject pgObj = (PGobject) rs.getObject(columnName);
			if (pgObj != null) {
				additionalDetail = mapper.readTree(pgObj.getValue());
			}
		} catch (IOException | SQLException e) {
			throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
		}
		return additionalDetail;
	}

}

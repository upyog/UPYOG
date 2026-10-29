package org.egov.ndc.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.ndc.web.model.AuditDetails;
import org.egov.ndc.web.model.OwnerInfo;
import org.egov.ndc.web.model.ndc.*;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@SuppressWarnings("java:S2638")
@Component
public class NdcRowMapper implements ResultSetExtractor<List<Application>> {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<Application> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Application> applicationHashMap = new LinkedHashMap<>();
		Map<String, Set<String>> details = new HashMap<>();
		Map<String, Set<String>> documents = new HashMap<>();

		while (rs.next()) {
			String applicationId = rs.getString("a_uuid");
			Application application = applicationHashMap.get(applicationId);
			if (application == null) {
				application = Application.builder()
						.uuid(applicationId)
						.applicationNo(rs.getString("applicationno"))
						.tenantId(rs.getString("tenantid"))
						.reason(rs.getString("reason"))
						.applicationStatus(rs.getString("applicationstatus"))
						.active(rs.getBoolean("active"))
						.auditDetails(AuditDetails.builder()
						.createdBy(rs.getString("a_createdby"))
						.lastModifiedBy(rs.getString("a_lastmodifiedby"))
						.createdTime(rs.getLong("a_createdtime"))
						.lastModifiedTime(rs.getLong("a_lastmodifiedtime")).build())
						.build();
				applicationHashMap.put(applicationId, application);
				details.put(applicationId, new HashSet<>());
				documents.put(applicationId, new HashSet<>());
			}
			addNdcDetails(rs, application, details.get(applicationId));
			addDocuments(rs, application, documents.get(applicationId));
			addOwnerUuids(rs, application);
		}
		return new ArrayList<>(applicationHashMap.values());
	}

	/**
	 * Adds NdcDetails to the Application object from the result set.
	 *
	 * @param rs the result set containing ndc detail columns
	 * @param application the application to enrich with ndc details
	 * @param detailsPresent set tracking already-added detail UUIDs
	 * @throws SQLException when reading from the result set fails
	 */
	private void addNdcDetails(ResultSet rs, Application application, Set<String> detailsPresent) throws SQLException {
		String ndcDetailsId = rs.getString("d_uuid");
		if (StringUtils.isEmpty(ndcDetailsId) || !detailsPresent.add(ndcDetailsId)) {
			return;
		}

		NdcDetailsRequest ndcDetails = NdcDetailsRequest.builder()
				.uuid(ndcDetailsId)
				.applicationId(rs.getString("d_applicationid"))
				.businessService(rs.getString("businessservice"))
				.consumerCode(rs.getString("consumercode"))
				.dueAmount(rs.getBigDecimal("dueamount"))
				.duePending(rs.getBoolean("isduepending"))
				.status(rs.getString("status"))
				.additionalDetails(getJsonValue((PGobject) rs.getObject("additionaldetails")))
				.build();

		application.addDetail(ndcDetails);
	}

	private void addOwnerUuids(ResultSet rs, Application application) throws SQLException {
		String ownerUuid = rs.getString("owner_uuid");

		if (StringUtils.isEmpty(ownerUuid)) {
			return;
		}

		if (application.getOwners() == null) {
			application.setOwners(new ArrayList<>());
		}

		boolean alreadyPresent = application.getOwners().stream()
				.anyMatch(o -> ownerUuid.equals(o.getUuid()));

		if (alreadyPresent) {
			return;
		}

		OwnerInfo ownerInfo = OwnerInfo.builder()
				.uuid(ownerUuid)
				.isPrimaryOwner(rs.getBoolean("owner_isprimaryowner"))
				.ownerShipPercentage(rs.getBigDecimal("owner_ownershippercentage"))
				.build();
		application.addOwner(ownerInfo);
	}

	/**
	 * Adds Documents to the Application object from the result set.
	 *
	 * @param rs the result set containing document columns
	 * @param application the application to enrich with documents
	 * @param documentsPresent set tracking already-added document UUIDs
	 * @throws SQLException when reading from the result set fails
	 */
	private void addDocuments(ResultSet rs, Application application, Set<String> documentsPresent) throws SQLException {
		String documentId = rs.getString("doc_uuid");
		if (StringUtils.isEmpty(documentId) || !documentsPresent.add(documentId)) {
			return;
		}

		DocumentRequest document = DocumentRequest.builder()
				.uuid(documentId)
				.applicationId(rs.getString("doc_applicationid"))
				.documentType(rs.getString("documenttype"))
				.documentAttachment(rs.getString("documentattachment"))
				.build();
		application.addDocument(document);
	}

	public JsonNode getJsonValue(PGobject pGobject) {
		try {
			if (Objects.isNull(pGobject) || Objects.isNull(pGobject.getValue())) {
				return null;
			}
			return objectMapper.readTree(pGobject.getValue());
		} catch (Exception e) {
			throw new CustomException("JSON_EXCEPTION", "json exception");
		}
	}

}

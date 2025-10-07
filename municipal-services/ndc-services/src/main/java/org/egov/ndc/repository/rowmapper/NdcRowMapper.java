package org.egov.ndc.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.core.JsonProcessingException;
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

@Component
public class NdcRowMapper implements ResultSetExtractor<List<Application>> {

	private ObjectMapper objectMapper = new ObjectMapper();

	@Override
	public List<Application> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Application> applicationHashMap = new LinkedHashMap<>();
		Map<String, Set<String>> details = new HashMap<>();
		Map<String, Set<String>> documents = new HashMap<>();
		Map<String, Set<String>> owners = new HashMap<>();

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
				owners.put(applicationId, new HashSet<>());
			}
            try {
                addNdcDetails(rs, application,details.get(applicationId));
				addDocuments(rs, application,documents.get(applicationId));
				addOwnerUuids(rs, application);

            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

		}
		return new ArrayList<>(applicationHashMap.values());
	}

	/**
	 * Adds NdcDetails to the NdcApplicationRequest object from the result set.
	 * @param rs
	 * @param ndcApplicationRequest
	 * @throws SQLException
	 */
	private void addNdcDetails(ResultSet rs, Application ndcApplicationRequest,Set<String> detailsPresent) throws SQLException, JsonProcessingException {
		String ndcDetailsId = rs.getString("d_uuid");
		if (!StringUtils.isEmpty(ndcDetailsId) && detailsPresent.add(ndcDetailsId)) {

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

				ndcApplicationRequest.addDetail(ndcDetails);

		}
	}

	private void addOwnerUuids(ResultSet rs, Application application) throws SQLException, JsonProcessingException {
		String ownerUuid = rs.getString("owner_uuid");

		if (!StringUtils.isEmpty(ownerUuid)) {
			if (application.getOwners() == null) {
				application.setOwners(new ArrayList<>());
			}

			boolean alreadyPresent = application.getOwners().stream()
					.anyMatch(o -> ownerUuid.equals(o.getUuid()));

			if (!alreadyPresent) {
				OwnerInfo ownerInfo = OwnerInfo.builder()
						.uuid(ownerUuid)
						.build();
				application.addOwner(ownerInfo);
			}
		}

	}

	/**
	 * Adds Documents to the NdcApplicationRequest object from the result set.
	 *
	 * @param rs
	 * @param ndcApplicationRequest
	 * @throws SQLException
	 */
	private void addDocuments(ResultSet rs, Application ndcApplicationRequest, Set<String> documentsPresent) throws SQLException {
		String documentId = rs.getString("doc_uuid");
		if (!StringUtils.isEmpty(documentId) && documentsPresent.add(documentId)) {
			DocumentRequest document = DocumentRequest.builder()
						.uuid(documentId)
						.applicationId(rs.getString("doc_applicationid"))
						.documentType(rs.getString("documenttype"))
						.documentAttachment(rs.getString("documentattachment"))
						.build();
				ndcApplicationRequest.addDocument(document);
			}
	}

	public JsonNode getJsonValue(PGobject pGobject){
		try {
			if(Objects.isNull(pGobject) || Objects.isNull(pGobject.getValue()))
				return null;
			else
				return objectMapper.readTree( pGobject.getValue());
		} catch (Exception e) {
			throw new CustomException("JSON_EXCEPTION","json exception");
		}
	}

}

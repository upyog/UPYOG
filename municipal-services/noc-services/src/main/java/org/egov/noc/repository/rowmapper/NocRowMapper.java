package org.egov.noc.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.egov.noc.web.model.AuditDetails;
import org.egov.noc.web.model.Document;
import org.egov.noc.web.model.Noc;
import org.egov.noc.web.model.NocDetails;
import org.egov.noc.web.model.enums.ApplicationType;
import org.egov.noc.web.model.enums.Status;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

import static org.reflections.Reflections.log;

@Component
public class NocRowMapper implements ResultSetExtractor<List<Noc>> {
	/**
	 * extracts the data from the resultSet and populate the NOC Objects
	 * @see ResultSetExtractor#extractData(ResultSet)
	 */
	@Override
	public List<Noc> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Noc> nocListMap = new HashMap<>();
		Noc noc = new Noc();
		while (rs.next()) {
			String Id = rs.getString("id");
			if (nocListMap.getOrDefault(Id, null) == null) {
				noc = new Noc();
				noc.setTenantId(rs.getString("tenantid"));
				noc.setId(rs.getString("id"));
				noc.setApplicationNo(rs.getString("applicationNo"));
                noc.setNocNo(rs.getString("nocNo"));
                noc.setNocType(rs.getString("nocType"));
                noc.setApplicationStatus(rs.getString("applicationStatus"));
                noc.setApplicationType(ApplicationType.fromValue(rs.getString("applicationType")));
                noc.setStatus(Status.fromValue(rs.getString("status")));
//                noc.setLandId(rs.getString("landId"));
//                noc.setSource(rs.getString("source"));
//                noc.getNocDetails().getAdditionalDetails().setSourceRefId(rs.getString("sourceRefId"));
                noc.setAccountId(rs.getString("AccountId"));

//                Object additionalDetails = new Gson().fromJson(rs.getString("additionalDetails").equals("{}")
//						|| rs.getString("additionalDetails").equals("null") ? null : rs.getString("additionalDetails"),
//						Object.class);
//                noc.getNocDetails().setAdditionalDetails(additionalDetails);
                
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdBy"))
                        .createdTime(rs.getLong("createdTime"))
                        .lastModifiedBy(rs.getString("lastModifiedBy"))
                        .lastModifiedTime(rs.getLong("lastModifiedTime"))
                        .build();
			    noc.setAuditDetails(auditdetails);

			    nocListMap.put(Id, noc);
			}
			addChildrenToProperty(rs, noc);

		}




		Map<String, Noc> sortedMap = nocListMap.entrySet()
				.stream()
				.sorted((e1, e2) -> {
					Long time1 = e1.getValue().getAuditDetails() != null ? e1.getValue().getAuditDetails().getLastModifiedTime() : null;
					Long time2 = e2.getValue().getAuditDetails() != null ? e2.getValue().getAuditDetails().getLastModifiedTime() : null;
					return Comparator.nullsLast(Long::compareTo).reversed().compare(time1, time2);
				})
				.collect(Collectors.toMap(
						Map.Entry::getKey,
						Map.Entry::getValue,
						(e1, e2) -> e1,
						LinkedHashMap::new
				));



		return new ArrayList<>(sortedMap.values());
	}
	/**
	 * add the child objects like document to the NOC object from the result set.
	 * @param rs
	 * @param noc
	 * @throws SQLException
	 */
//	@SuppressWarnings("unused")
//	private void addChildrenToProperty(ResultSet rs, Noc noc) throws SQLException {
//		String documentId = rs.getString("uuid");
//		String tenantId = noc.getTenantId();
//		if (!StringUtils.isEmpty(documentId)) {
//			Document applicationDocument = new Document();
////		     Object additionalDetails = new Gson().fromJson(rs.getString("doc_details").equals("{}")
////						|| rs.getString("doc_details").equals("null") ? null : rs.getString("doc_details"),
////						Object.class);
//			applicationDocument.setUuid(documentId);
//			applicationDocument.setDocumentType(rs.getString("documentType"));
//			applicationDocument.setDocumentAttachment(rs.getString("documentAttachment"));
//			applicationDocument.setDocumentUid(rs.getString("documentUid"));
////			applicationDocument.setAdditionalDetails(additionalDetails);
//			noc.addDocumentsItem(applicationDocument);
//		}
//	}
//	private void addChildrenToProperty(ResultSet rs, Noc noc) throws SQLException {
//		String id = rs.getString("noc_details_id");
//		String tenantId = rs.getString("noc_details_tenantid");
//		if (!StringUtils.isEmpty(id)) {
//			NocDetails nocdetails = new NocDetails();
//			Object additionalDetails = new Gson().fromJson(rs.getString("noc_details_additionaldetails").equals("{}")
//							|| rs.getString("noc_details_additionaldetails").equals("null") ? null : rs.getString("noc_details_additionaldetails"),
//					Object.class);
//			nocdetails.setId(id);
//
//			nocdetails.setNocId(rs.getString("noc_details_nocid"));
//			nocdetails.setAdditionalDetails(rs.getString("noc_details_additionaldetails"));
//			nocdetails.setTenantId(rs.getString("noc_details_tenantid"));
//			noc.nocDetails(nocdetails);
//
//		}
//	}


	private void addChildrenToProperty(ResultSet rs, Noc noc) throws SQLException {
		String documentsJson = rs.getString("documents");

		if (!StringUtils.isEmpty(documentsJson)) {
			try {
				List<Document> documents = new Gson().fromJson(documentsJson, new TypeToken<List<Document>>() {}.getType());
				for (Document doc : documents) {
					// Optional: set tenantId or other fields if needed
					doc.setDocumentUid(doc.getUuid()); // if you need to copy uuid to documentUid
					doc.setNocId(noc.getId());
					noc.addDocumentsItem(doc);
				}
			} catch (JsonSyntaxException e) {
				log.error("Failed to parse documents JSON", e);
			}



			String nocDetailsJson = rs.getString("nocDetails");

			if (!StringUtils.isEmpty(nocDetailsJson) && !"null".equals(nocDetailsJson)) {
				try {
					NocDetails details = new Gson().fromJson(nocDetailsJson, NocDetails.class);
					details.setAuditDetails(noc.getAuditDetails());
					details.setNocId(noc.getId());
					details.setTenantId(noc.getTenantId());
					noc.nocDetails(details);


				} catch (JsonSyntaxException e) {
					log.error("Failed to parse nocDetails JSON", e);
				}
			}

		}
	}

}

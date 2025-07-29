package org.egov.bpa.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.bpa.web.model.AuditDetails;
import org.egov.bpa.web.model.Document;
import org.egov.bpa.web.model.PreapprovedPlan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

@Component
public class PreapprovedPlanMapper implements ResultSetExtractor<List<PreapprovedPlan>> {

	@Autowired
	private ObjectMapper mapper;

	/**
	 * extract the data from the resultset and prepare the PreapprovedPlan Object
	 * 
	 * @see org.springframework.jdbc.core.ResultSetExtractor#extractData(java.sql.ResultSet)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List<PreapprovedPlan> extractData(ResultSet rs) throws SQLException, DataAccessException {

		Map<String, PreapprovedPlan> preapprovedPlanMap = new LinkedHashMap<String, PreapprovedPlan>();

		while (rs.next()) {
			String id = rs.getString("id");
			PreapprovedPlan currentPreapprovedPlan = preapprovedPlanMap.get(id);
			String tenantId = rs.getString("tenantid");
			if (currentPreapprovedPlan == null) {
				Long lastModifiedTime = rs.getLong("lastModifiedTime");
				if (rs.wasNull()) {
					lastModifiedTime = null;
				}

				Object additionalDetails = new Gson().fromJson(rs.getString("additionalDetails").equals("{}")
						|| rs.getString("additionalDetails").equals("null") ? null : rs.getString("additionalDetails"),
						Object.class);

				Object drawingDetail = new Gson().fromJson(
						rs.getString("drawingDetail").equals("{}") || rs.getString("drawingDetail").equals("null")
								? null
								: rs.getString("drawingDetail"),
						Object.class);

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdBy"))
						.createdTime(rs.getLong("createdTime")).lastModifiedBy(rs.getString("lastModifiedBy"))
						.lastModifiedTime(lastModifiedTime).build();

				currentPreapprovedPlan = PreapprovedPlan.builder().id(id).drawingNo(rs.getString("drawingNo"))
						.tenantId(tenantId).plotLength(rs.getBigDecimal("plotLength"))
						.plotWidth(rs.getBigDecimal("plotWidth")).roadWidth(rs.getBigDecimal("roadWidth"))
						.drawingDetail(drawingDetail).active(rs.getBoolean("active"))
						.additionalDetails(additionalDetails)
						.auditDetails(auditdetails)
						.plotLengthInFeet(rs.getBigDecimal("plot_length_in_feet")).plotWidthInFeet(rs.getBigDecimal("plot_width_in_feet")).
						preApprovedCode(rs.getString("preapproved_code")).
						build();
				preapprovedPlanMap.put(id, currentPreapprovedPlan);
			}

			addChildrenToProperty(rs, currentPreapprovedPlan);
		}

		return new ArrayList<>(preapprovedPlanMap.values());

	}

	/**
	 * add child objects to the PreapprovedPlan from the results set
	 * 
	 * @param rs
	 * @param bpa
	 * @throws preapprovedPlan
	 */
	@SuppressWarnings("unused")
	private void addChildrenToProperty(ResultSet rs, PreapprovedPlan preapprovedPlan) throws SQLException {

		String tenantId = preapprovedPlan.getTenantId();

		// Documents-
		String documentId = rs.getString("ebpapd_id");
		Object docDetails = null;
		if (rs.getString("ebpapd_additionalDetails") != null) {
			docDetails = new Gson().fromJson(rs.getString("ebpapd_additionalDetails").equals("{}")
					|| rs.getString("ebpapd_additionalDetails").equals("null") ? null
							: rs.getString("ebpapd_additionalDetails"),
					Object.class);
		}
		if (documentId != null) {
			Document document = Document.builder().documentType(rs.getString("ebpapd_documenttype"))
					.fileStoreId(rs.getString("ebpapd_filestoreid")).id(documentId).additionalDetails(docDetails)
					.documentUid(rs.getString("ebpapd_documentuid")).build();
			preapprovedPlan.addDocumentsItem(document);
		}
	}

}

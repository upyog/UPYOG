package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.DocumentDetail;

/**
 * This class is responsible for mapping the result set from the database to a list of
 * DocumentDetail objects.
 * 
 * Purpose:
 * - To extract data from the ResultSet and populate DocumentDetail objects.
 * - To handle the mapping of database fields to the corresponding fields in the DocumentDetail model.
 * 
 * Features:
 * - Implements the ResultSetExtractor interface to process the ResultSet.
 * - Iterates through the ResultSet and maps each row to a DocumentDetail object.
 * - Handles nested objects such as AuditDetails for capturing audit-related information.
 * 
 * Dependencies:
 * - DocumentDetail: The model class representing document details.
 * - AuditDetails: The model class representing audit information for the document.
 * 
 * Fields Mapped:
 * - document_detail_id: Maps to the documentDetailId field in DocumentDetail.
 * - booking_id: Maps to the bookingId field in DocumentDetail.
 * - document_type: Maps to the documentType field in DocumentDetail.
 * - filestore_id: Maps to the fileStoreId field in DocumentDetail.
 * - createdby: Maps to the createdBy field in AuditDetails.
 * - createdtime: Maps to the createdTime field in AuditDetails.
 * - lastmodifiedby: Maps to the lastModifiedBy field in AuditDetails.
 * - lastmodifiedtime: Maps to the lastModifiedTime field in AuditDetails.
 * 
 * Usage:
 * - This class is used by the repository layer to map database query results to DocumentDetail objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 */
@Component
public class DocumentDetailsRowMapper implements ResultSetExtractor<List<DocumentDetail>> {

	@Override
	public List<DocumentDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<DocumentDetail> documentDetails = new ArrayList<DocumentDetail>();
		while (rs.next()) {
			/**
			 * document_detail_id, booking_id, document_type, filestore_id, createdby,
			 * lastmodifiedby, createdtime, lastmodifiedtime
			 */
			AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("createdby"))
					.createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby"))
					.lastModifiedTime(rs.getLong("lastmodifiedtime")).build();
			DocumentDetail details = DocumentDetail.builder().documentDetailId(rs.getString("document_detail_id"))
					.bookingId(rs.getString("booking_id")).documentType(rs.getString("document_type"))
					.fileStoreId(rs.getString("filestore_id")).auditDetails(auditdetails).build();

			documentDetails.add(details);

		}
		return documentDetails;
	}

}

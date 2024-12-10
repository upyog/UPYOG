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

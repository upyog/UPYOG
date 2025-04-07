package org.upyog.cdwm.repository.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.cdwm.web.models.DocumentDetail;;

@Component
public class DocumentDetailsRowMapper implements ResultSetExtractor<List<DocumentDetail>> {

	@Override
	public List<DocumentDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<DocumentDetail> documentDetails = new ArrayList<DocumentDetail>();
		while (rs.next()) {
		
			DocumentDetail details = DocumentDetail.builder().documentDetailId(rs.getString("documentDetailId"))
					.applicationId(rs.getString("documentDetailApplicationId")).documentType(rs.getString("documentType"))
					.uploadedByUserType(rs.getString("uploadedByUserType"))
					.fileStoreId(rs.getString("fileStoreId")).build();

			documentDetails.add(details);

		}
		return documentDetails;
	}

}

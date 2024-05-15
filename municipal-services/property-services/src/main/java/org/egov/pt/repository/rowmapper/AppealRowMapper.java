package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.Appeal;
import org.egov.pt.models.Document;
import org.egov.pt.models.Property;
import org.egov.pt.models.enums.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class AppealRowMapper implements ResultSetExtractor<List<Appeal>>{

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Appeal> extractData(ResultSet rs) throws SQLException, DataAccessException {
		// TODO Auto-generated method stub
		Map<String, Appeal> appealMap = new LinkedHashMap<>();
		while(rs.next())
		{
			String appealUuId = rs.getString("appealid");
			Appeal currentAppeal = appealMap.get(appealUuId);
			String tenanId = rs.getString("tenantid");

			if(null==currentAppeal)
			{
				currentAppeal=Appeal.builder()
						.id(appealUuId)
						.propertyId(rs.getString("propertyid"))
						.status(Status.fromValue(rs.getString("status")))
						.acknowldgementNumber(rs.getString("acknowldgementnumber"))
						.tenantId(tenanId)
						.build();

				addDocToAppeal(rs, currentAppeal);
				appealMap.put(appealUuId, currentAppeal);

			}
		}
		return new ArrayList<>(appealMap.values());
	}
	
	private void addDocToAppeal(ResultSet rs, Appeal appeal) throws SQLException {

		String docId = rs.getString("pdocid");
		String entityId = rs.getString("pdocentityid");
		List<Document> docs = appeal.getDocuments();
		
		if (!(docId != null && entityId.equals(appeal.getId())))
			return;

		if (!CollectionUtils.isEmpty(docs))
			for (Document doc : docs) {
				if (doc.getId().equals(docId))
					return;
			}

		Document doc =  Document.builder()
			.status(Status.fromValue(rs.getString("pdocstatus")))
			.documentType(rs.getString("pdoctype"))
			.fileStoreId(rs.getString("pdocfileStore"))
			.documentUid(rs.getString("pdocuid"))
			.id(docId)
			.build();
		
		appeal.addDocumentsItem(doc);
	}
}

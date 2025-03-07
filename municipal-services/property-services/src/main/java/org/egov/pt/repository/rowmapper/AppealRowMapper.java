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
import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.Document;
import org.egov.pt.models.Property;
import org.egov.pt.models.enums.CreationReason;
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
			String appealUuId = rs.getString("id");
			Appeal currentAppeal = appealMap.get(appealUuId);
			String tenanId = rs.getString("tenantid");

			if(null==currentAppeal)
			{
				AuditDetails auditdetails = getauditdetails(rs, "Appeal");
				
				currentAppeal=Appeal.builder()
						.id(appealUuId)
						.propertyId(rs.getString("propertyid"))
						.status(Status.fromValue(rs.getString("status")))
						.acknowldgementNumber(rs.getString("acknowldgementnumber"))
						.tenantId(tenanId)
						.appealId(rs.getString("appealid"))
						.propertyaddress(rs.getString("propertyaddress"))
						.assesmnetyear(rs.getString("assesmnetyear"))
						.nameofassigningofficer(rs.getString("nameofassigningofficer"))
						.designation(rs.getString("designation"))
						.ruleunderorderpassed(rs.getString("ruleunderorderpassed"))
						.dateoforder(rs.getString("dateoforder"))
						.dateofservice(rs.getString("dateofservice"))
						.dateofpayment(rs.getString("dateofpayment"))
						.ownername(rs.getString("ownername"))
						.applicantaddress(rs.getString("applicantaddress"))
						.reliefclaimed("reliefclaimed")
						.statementoffacts("statementoffacts")
						.groundofappeal(rs.getString("groundofappeal"))
						.creationReason(CreationReason.fromValue(rs.getString("creationReason")))
						.auditDetails(auditdetails)
						.build();

				addDocToAppeal(rs, currentAppeal);
				appealMap.put(appealUuId, currentAppeal);

			}
			else if(null!=currentAppeal)
			{
				addDocToAppeal(rs, currentAppeal);
				//appealMap.put(appealUuId, currentAppeal);
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

		AuditDetails auditdetails = getauditdetails(rs, "Appeal");
		
		Document doc =  Document.builder()
				.status(Status.fromValue(rs.getString("pdocstatus")))
				.documentType(rs.getString("pdoctype"))
				.fileStoreId(rs.getString("pdocfileStore"))
				.documentUid(rs.getString("pdocuid"))
				.id(docId)
				.auditDetails(auditdetails)
				.build();

		appeal.addDocumentsItem(doc);
	}

	public AuditDetails getauditdetails(ResultSet rs, String source) throws SQLException
	{
		switch (source) {

		case "Appeal":

			Long lastModifiedTime = rs.getLong("lastmodifiedtime");
			if (rs.wasNull()) {
				lastModifiedTime = null;
			}

			return AuditDetails.builder().createdBy(rs.getString("createdby"))
					.createdTime(rs.getLong("createdtime")).lastModifiedBy(rs.getString("lastmodifiedby"))
					.lastModifiedTime(lastModifiedTime).build();

		default: 
			return null;

		}
	}
}

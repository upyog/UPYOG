package org.egov.swservice.repository.rowmapper;

import org.egov.swservice.util.SWConstants;
import org.egov.swservice.web.models.*;
import org.egov.swservice.web.models.Connection.StatusEnum;
import org.egov.swservice.web.models.workflow.ProcessInstance;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class OpenSewerageRowMapperForTable implements ResultSetExtractor<List<SewerageConnection>> {

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<SewerageConnection> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, SewerageConnection> connectionListMap = new LinkedHashMap<>();
		SewerageConnection sewarageConnection = new SewerageConnection();
		while (rs.next()) {
			String Id = rs.getString("connection_Id");
			if (connectionListMap.getOrDefault(Id, null) == null) {
				sewarageConnection = new SewerageConnection();
				sewarageConnection.setTenantId(rs.getString("tenantid"));
				sewarageConnection.setId(rs.getString("connection_Id"));
				sewarageConnection.setApplicationNo(rs.getString("applicationNo"));
				sewarageConnection.setApplicationStatus(rs.getString("applicationstatus"));
				sewarageConnection.setStatus(StatusEnum.fromValue(rs.getString("status")));
				sewarageConnection.setConnectionNo(rs.getString("connectionNo"));
				sewarageConnection.setOldConnectionNo(rs.getString("oldConnectionNo"));
				sewarageConnection.setOldApplication(rs.getBoolean("isoldapplication"));
				// get property id and get property object
//                HashMap<String, Object> addtionalDetails = new HashMap<>();
//				PGobject pgObj = (PGobject) rs.getObject("additionaldetails");

				ObjectNode addtionalDetails = null;
//				if (pgObj != null) {
//
//					try {
//						addtionalDetails = mapper.readValue(pgObj.getValue(), ObjectNode.class);
//					} catch (IOException ex) {
//						// TODO Auto-generated catch block
//						throw new CustomException("PARSING ERROR", "The additionalDetail json cannot be parsed");
//					}
//				} else {
				addtionalDetails = mapper.createObjectNode();
//				}
				addtionalDetails.put(SWConstants.APP_CREATED_DATE, rs.getBigDecimal("appCreatedDate"));
				addtionalDetails.put(SWConstants.LOCALITY, rs.getString("locality"));
				sewarageConnection.setAdditionalDetails(addtionalDetails);
				sewarageConnection.processInstance(ProcessInstance.builder().action((rs.getString("action"))).build());
				sewarageConnection.setApplicationType(rs.getString("applicationType"));
				sewarageConnection.setChannel(rs.getString("channel"));
				sewarageConnection.setDateEffectiveFrom(rs.getLong("dateEffectiveFrom"));
				sewarageConnection.setPropertyId(rs.getString("property_id"));
				sewarageConnection.setConnectionType(rs.getString("connectionType"));

				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("sw_createdBy"))
						.createdTime(rs.getLong("sw_createdTime")).lastModifiedBy(rs.getString("sw_lastModifiedBy"))
						.lastModifiedTime(rs.getLong("sw_lastModifiedTime")).build();
				sewarageConnection.setAuditDetails(auditdetails);

				// Add documents id's
				connectionListMap.put(Id, sewarageConnection);
			}

		}
		return new ArrayList<>(connectionListMap.values());
	}

}

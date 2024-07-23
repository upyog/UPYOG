package org.egov.waterconnection.repository.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.egov.waterconnection.constants.WCConstants;
import org.egov.waterconnection.web.models.*;
import org.egov.waterconnection.web.models.Connection.StatusEnum;
import org.egov.waterconnection.web.models.workflow.ProcessInstance;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Component
public class WaterRowMapperForTable implements ResultSetExtractor<List<WaterConnection>> {

	@Autowired
	private ObjectMapper mapper;

//	private int full_count=0;
//
//	public int getFull_count() {
//		return full_count;
//	}
//
//	public void setFull_count(int full_count) {
//		this.full_count = full_count;
//	}
	
	@Override
	public List<WaterConnection> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, WaterConnection> connectionListMap = new LinkedHashMap<>();
		WaterConnection currentWaterConnection = new WaterConnection();
		while (rs.next()) {
			String Id = rs.getString("connection_Id");
			if (connectionListMap.getOrDefault(Id, null) == null) {
				currentWaterConnection = new WaterConnection();
				currentWaterConnection.setTenantId(rs.getString("tenantid"));
				currentWaterConnection.setConnectionCategory(rs.getString("connectionCategory"));
				currentWaterConnection.setConnectionType(rs.getString("connectionType"));
				currentWaterConnection.setWaterSource(rs.getString("waterSource"));
				currentWaterConnection.setMeterId(rs.getString("meterId"));
				currentWaterConnection.setMeterInstallationDate(rs.getLong("meterInstallationDate"));
				currentWaterConnection.setId(rs.getString("connection_Id"));
				currentWaterConnection.setApplicationNo(rs.getString("applicationNo"));
				currentWaterConnection.setApplicationStatus(rs.getString("applicationstatus"));
				currentWaterConnection.setStatus(StatusEnum.fromValue(rs.getString("status")));
				currentWaterConnection.setConnectionNo(rs.getString("connectionNo"));
				currentWaterConnection.setOldConnectionNo(rs.getString("oldConnectionNo"));
				currentWaterConnection.setPipeSize(rs.getDouble("pipeSize"));
				currentWaterConnection.setNoOfTaps(rs.getInt("noOfTaps"));
				currentWaterConnection.setProposedPipeSize(rs.getDouble("proposedPipeSize"));
				currentWaterConnection.setProposedTaps(rs.getInt("proposedTaps"));
				currentWaterConnection.setRoadCuttingArea(rs.getFloat("roadcuttingarea"));
				currentWaterConnection.setRoadType(rs.getString("roadtype"));
				currentWaterConnection.setDisconnectionReason(rs.getString("disconnectionReason"));
				currentWaterConnection.setIsDisconnectionTemporary(rs.getBoolean("isDisconnectionTemporary"));

//				PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
//				this.setFull_count(rs.getInt("full_count"));
				ObjectNode additionalDetails = null;
//				if (pgObj != null) {
//
//					try {
//						additionalDetails = mapper.readValue(pgObj.getValue(), ObjectNode.class);
//					} catch (IOException ex) {
//						// TODO Auto-generated catch block
//						throw new CustomException("PARSING ERROR", "The additionalDetail json cannot be parsed");
//					}
//				} else {
					additionalDetails = mapper.createObjectNode();
//				}
				// HashMap<String, Object> additionalDetails = new HashMap<>();
				additionalDetails.put(WCConstants.ADHOC_PENALTY, rs.getBigDecimal("adhocpenalty"));
				additionalDetails.put(WCConstants.ADHOC_REBATE, rs.getBigDecimal("adhocrebate"));
				additionalDetails.put(WCConstants.ADHOC_PENALTY_REASON, rs.getString("adhocpenaltyreason"));
				additionalDetails.put(WCConstants.ADHOC_PENALTY_COMMENT, rs.getString("adhocpenaltycomment"));
				additionalDetails.put(WCConstants.ADHOC_REBATE_REASON, rs.getString("adhocrebatereason"));
				additionalDetails.put(WCConstants.ADHOC_REBATE_COMMENT, rs.getString("adhocrebatecomment"));
				additionalDetails.put(WCConstants.INITIAL_METER_READING_CONST, rs.getBigDecimal("initialmeterreading"));
				additionalDetails.put(WCConstants.APP_CREATED_DATE, rs.getBigDecimal("appCreatedDate"));
				additionalDetails.put(WCConstants.DETAILS_PROVIDED_BY, rs.getString("detailsprovidedby"));
				additionalDetails.put(WCConstants.ESTIMATION_FILESTORE_ID, rs.getString("estimationfileStoreId"));
				additionalDetails.put(WCConstants.SANCTION_LETTER_FILESTORE_ID, rs.getString("sanctionfileStoreId"));
				additionalDetails.put(WCConstants.ESTIMATION_DATE_CONST, rs.getBigDecimal("estimationLetterDate"));
				additionalDetails.put(WCConstants.LOCALITY, rs.getString("locality"));

				currentWaterConnection.setAdditionalDetails(additionalDetails);
				currentWaterConnection
						.processInstance(ProcessInstance.builder().action((rs.getString("action"))).build());
				currentWaterConnection.setPropertyId(rs.getString("property_id"));
				// Add documents id's
				currentWaterConnection.setConnectionExecutionDate(rs.getLong("connectionExecutionDate"));
				currentWaterConnection.setApplicationType(rs.getString("applicationType"));
				currentWaterConnection.setChannel(rs.getString("channel"));
				currentWaterConnection.setDateEffectiveFrom(rs.getLong("dateEffectiveFrom"));
				currentWaterConnection.setDisconnectionExecutionDate(rs.getLong("disconnectionExecutionDate"));


				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("ws_createdBy"))
						.createdTime(rs.getLong("ws_createdTime")).lastModifiedBy(rs.getString("ws_lastModifiedBy"))
						.lastModifiedTime(rs.getLong("ws_lastModifiedTime")).build();
				currentWaterConnection.setAuditDetails(auditdetails);

				connectionListMap.put(Id, currentWaterConnection);
			}
			
		}
		return new ArrayList<>(connectionListMap.values());
	}




}
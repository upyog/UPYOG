package org.egov.garbagecollection.repository.rowmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.web.models.Connection.StatusEnum;
import org.egov.garbagecollection.web.models.workflow.ProcessInstance;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Component
public class OpenGCRowMapper implements ResultSetExtractor<List<GarbageConnection>> {

    @Autowired
    private ObjectMapper mapper;
	
	private int full_count=0;

	public int getFull_count() {
		return full_count;
	}

	public void setFull_count(int full_count) {
		this.full_count = full_count;
	}
	
	@Override
    public List<GarbageConnection> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, GarbageConnection> connectionListMap = new LinkedHashMap<>();
        GarbageConnection garbageConnection = new GarbageConnection();
        while (rs.next()) {
            String Id = rs.getString("connection_Id");
            if (connectionListMap.getOrDefault(Id, null) == null) {
                garbageConnection = new GarbageConnection();
                garbageConnection.setTenantId(rs.getString("tenantid"));
                garbageConnection.setConnectionCategory(rs.getString("connectionCategory"));
                garbageConnection.setConnectionType(rs.getString("connectionType"));
//                garbageConnection.setWaterSource(rs.getString("waterSource"));
//                garbageConnection.setMeterId(rs.getString("meterId"));
//                garbageConnection.setMeterInstallationDate(rs.getLong("meterInstallationDate"));
                garbageConnection.setId(rs.getString("connection_Id"));
                garbageConnection.setApplicationNo(rs.getString("applicationNo"));
                garbageConnection.setApplicationStatus(rs.getString("applicationstatus"));
                garbageConnection.setStatus(StatusEnum.fromValue(rs.getString("status")));
                garbageConnection.setConnectionNo(rs.getString("connectionNo"));
                garbageConnection.setOldConnectionNo(rs.getString("oldConnectionNo"));
                garbageConnection.setOldApplication(rs.getBoolean("isoldapplication"));
                Map<String, Object> additionalDetails = new LinkedHashMap<>();
                PGobject pgObj = (PGobject) rs.getObject("additionaldetails");
                if (pgObj != null && !StringUtils.isEmpty(pgObj.getValue())) {
                    try {
                        additionalDetails = mapper.readValue(pgObj.getValue(), new TypeReference<Map<String, Object>>() {
                        });
                    } catch (IOException ignored) {
                        additionalDetails = new LinkedHashMap<>();
                    }
                }
//                additionalDetails.put(GCConstants.INITIAL_METER_READING_CONST, rs.getBigDecimal("initialmeterreading"));
                additionalDetails.put(GCConstants.APP_CREATED_DATE, rs.getBigDecimal("appCreatedDate"));
                additionalDetails.put(GCConstants.LOCALITY, rs.getString("locality"));
                garbageConnection.setAdditionalDetails(additionalDetails);
                garbageConnection
                        .processInstance(ProcessInstance.builder().action((rs.getString("action"))).build());
                garbageConnection.setPropertyId(rs.getString("property_id"));
                garbageConnection.setUnitId(rs.getString("unit_id"));
                garbageConnection.setPropertyType(rs.getString("property_type"));
                garbageConnection.setPlotSize(rs.getString("plot_size"));
                garbageConnection.setLocation(rs.getString("location"));
                garbageConnection.setFrequency_of_garbage_collection(rs.getString("frequency_of_garbage_collection"));
                garbageConnection.setTypeOfWaste(rs.getString("type_of_waste"));

                garbageConnection.setConnectionExecutionDate(rs.getLong("connectionExecutionDate"));
                garbageConnection.setApplicationType(rs.getString("applicationType"));
                garbageConnection.setChannel(rs.getString("channel"));
                garbageConnection.setDateEffectiveFrom(rs.getLong("dateEffectiveFrom"));
                this.setFull_count(rs.getInt("full_count")); 
                AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("gc_createdBy"))
                        .createdTime(rs.getLong("gc_createdTime")).lastModifiedBy(rs.getString("gc_lastModifiedBy"))
                        .lastModifiedTime(rs.getLong("gc_lastModifiedTime")).build();
                garbageConnection.setAuditDetails(auditdetails);

                connectionListMap.put(Id, garbageConnection);
            }
            addChildrenToProperty(rs, garbageConnection);
        }
        return new ArrayList<>(connectionListMap.values());
    }

    private void addChildrenToProperty(ResultSet rs, GarbageConnection garbageConnection) throws SQLException {
        addHoldersDeatilsToGarbageConnection(rs, garbageConnection);
        addDocumentToGarbageConnecti(rs, garbageConnection);
    }
    private void addDocumentToGarbageConnecti(ResultSet rs, GarbageConnection garbageConnection) throws SQLException {
        String document_Id = rs.getString("doc_Id");
        String isActive = rs.getString("doc_active");
        boolean documentActive = false;
        if (!StringUtils.isEmpty(isActive)) {
            documentActive = Status.ACTIVE.name().equalsIgnoreCase(isActive);
        }
        if (!StringUtils.isEmpty(document_Id) && documentActive) {
            Document applicationDocument = new Document();
            applicationDocument.setId(document_Id);
            applicationDocument.setDocumentType(rs.getString("documenttype"));
            applicationDocument.setFileStoreId(rs.getString("filestoreid"));
            applicationDocument.setApplicationId(rs.getString("gc_Id"));
            applicationDocument.setStatus(Status.fromValue(isActive));
            garbageConnection.addDocumentsItem(applicationDocument);
        }
    }



    private void addHoldersDeatilsToGarbageConnection(ResultSet rs, GarbageConnection garbageConnection) throws SQLException {
        String uuid = rs.getString("userid");
        List<OwnerInfo> connectionHolders = garbageConnection.getConnectionHolders();
        if (!CollectionUtils.isEmpty(connectionHolders)) {
            for (OwnerInfo connectionHolderInfo : connectionHolders) {
                if (!StringUtils.isEmpty(connectionHolderInfo.getUuid()) && !StringUtils.isEmpty(uuid) && connectionHolderInfo.getUuid().equals(uuid))
                    return;
            }
        }
        if(!StringUtils.isEmpty(uuid)){
            Boolean isPrimaryOwner = rs.getBoolean("isprimaryholder");
            if (rs.wasNull()) {
                isPrimaryOwner = null;
            }
            OwnerInfo connectionHolderInfo = OwnerInfo.builder()
                    .relationship(rs.getString("holderrelationship"))
                    .status(Status.fromValue(rs.getString("holderstatus")))
                    .tenantId(rs.getString("holdertenantid")).ownerType(rs.getString("connectionholdertype"))
                    .isPrimaryOwner(isPrimaryOwner).uuid(uuid).build();
            garbageConnection.addConnectionHolderInfo(connectionHolderInfo);
        }
    }
}


package org.egov.waterconnection.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.waterconnection.config.WSConfiguration;
import org.egov.waterconnection.constants.WCConstants;
import org.egov.waterconnection.repository.rowmapper.EncryptionCountRowMapper;
import org.egov.waterconnection.repository.rowmapper.OpenWaterRowMapper;
import org.egov.waterconnection.web.models.*;
import org.egov.waterconnection.producer.WaterConnectionProducer;
import org.egov.waterconnection.repository.builder.WsQueryBuilder;
import org.egov.waterconnection.repository.rowmapper.WaterRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.ZoneId;
import org.threeten.bp.format.DateTimeFormatter;

import lombok.extern.slf4j.Slf4j;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Slf4j
@Repository
public class WaterDaoImpl implements WaterDao {

	@Autowired
	private WaterConnectionProducer waterConnectionProducer;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private WsQueryBuilder wsQueryBuilder;

	@Autowired
	private WaterRowMapper waterRowMapper;

	@Autowired
	private OpenWaterRowMapper openWaterRowMapper;
	
	@Autowired
	private WSConfiguration wsConfiguration;

	@Autowired
	private EncryptionCountRowMapper encryptionCountRowMapper;

	@Value("${egov.waterservice.createwaterconnection.topic}")
	private String createWaterConnection;

	@Value("${egov.waterservice.updatewaterconnection.topic}")
	private String updateWaterConnection;

	@Value("${egov.waterservice.oldDataEncryptionStatus.topic}")
	private String encryptionStatusTopic;

	@Value("${egov.waterservice.update.oldData.topic}")
	private String updateOldDataEncTopic;

	@Override
	public void saveWaterConnection(WaterConnectionRequest waterConnectionRequest) {
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		waterConnectionProducer.push(createWaterConnection, key, waterConnectionRequest);
	}

	@Override
	public List<WaterConnection> getWaterConnectionList(SearchCriteria criteria,
			RequestInfo requestInfo) {
		
		List<WaterConnection> waterConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		// String query = wsQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo);]
		Boolean iscitizenSearch=iscitizenSearch(requestInfo.getUserInfo());
		String query = wsQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo, iscitizenSearch);

		log.info("Search Query" + query);
		log.info("Parameters for search Query:: " + preparedStatement.toString());
		if (query == null)
			return Collections.emptyList();
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		
		// if(isOpenSearch)
		 if(iscitizenSearch)
		 {
			 waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
						openWaterRowMapper);
			 
		 }else if(isOpenSearch)
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
					openWaterRowMapper);
		else
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
				waterRowMapper);
		if (waterConnectionList == null)
			return Collections.emptyList();
		return waterConnectionList;
	}

	public Integer getWaterConnectionsCount(SearchCriteria criteria, RequestInfo requestInfo) {
		List<Object> preparedStatement = new ArrayList<>();
		// String query = wsQueryBuilder.getSearchCountQueryString(criteria, preparedStatement, requestInfo);
		Boolean iscitizenSearch=iscitizenSearch(requestInfo.getUserInfo());
		String query = wsQueryBuilder.getSearchCountQueryString(criteria, preparedStatement, requestInfo,iscitizenSearch);
		
		if (query == null)
			return 0;

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}
	
	@Override
	public void updateWaterConnection(WaterConnectionRequest waterConnectionRequest, boolean isStateUpdatable) {
		String reqAction = waterConnectionRequest.getWaterConnection().getProcessInstance().getAction();
		
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		if (isStateUpdatable) 
		{
			if (WCConstants.EXECUTE_DISCONNECTION.equalsIgnoreCase(reqAction)) 
			{
				waterConnectionRequest.getWaterConnection().setStatus(Connection.StatusEnum.INACTIVE);
			}
			if ((waterConnectionRequest.isReconnectRequest() || waterConnectionRequest.getWaterConnection().getApplicationType().equalsIgnoreCase(WCConstants.WATER_RECONNECTION)) && WCConstants.ACTIVATE_CONNECTION_CONST.equalsIgnoreCase(reqAction)) {
				waterConnectionRequest.getWaterConnection().setStatus(Connection.StatusEnum.ACTIVE);
			}
			else if(waterConnectionRequest.getWaterConnection().isIsworkflowdisabled())
			{
			// For meter number and rest details addition before payment (02-08-2024)
				waterConnectionProducer.push(updateWaterConnection, key, waterConnectionRequest);
			
			}
			else
				waterConnectionProducer.push(updateWaterConnection, key, waterConnectionRequest);
		} 
		
		
		else {
			waterConnectionProducer.push(wsConfiguration.getWorkFlowUpdateTopic(), key, waterConnectionRequest);
		}
	}
	
	/**
	 * push object to create meter reading
	 * 
	 * @param waterConnectionRequest
	 */
	public void postForMeterReading(WaterConnectionRequest waterConnectionRequest) {
		log.info("Posting request to kafka topic - " + wsConfiguration.getCreateMeterReading());
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		waterConnectionProducer.push(wsConfiguration.getCreateMeterReading(), key , waterConnectionRequest);
	}

	/**
	 * push object for edit notification
	 * 
	 * @param waterConnectionRequest
	 */
	public void pushForEditNotification(WaterConnectionRequest waterConnectionRequest, boolean isStateUpdatable) {
		if (!WCConstants.EDIT_NOTIFICATION_STATE
				.contains(waterConnectionRequest.getWaterConnection().getProcessInstance().getAction())) {
			String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
			waterConnectionProducer.push(wsConfiguration.getEditNotificationTopic(), key, waterConnectionRequest);
		}
	}
	
	/**
	 * Enrich file store Id's
	 * 
	 * @param waterConnectionRequest
	 */
	public void enrichFileStoreIds(WaterConnectionRequest waterConnectionRequest) {
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		waterConnectionProducer.push(wsConfiguration.getFileStoreIdsTopic(), key , waterConnectionRequest);
	}
	
	/**
	 * Save file store Id's
	 * 
	 * @param waterConnectionRequest
	 */
	public void saveFileStoreIds(WaterConnectionRequest waterConnectionRequest) {
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		waterConnectionProducer.push(wsConfiguration.getSaveFileStoreIdsTopic(), key , waterConnectionRequest);
	}

	public Boolean isSearchOpen(User userInfo) {

		return userInfo.getType().equalsIgnoreCase("SYSTEM")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("ANONYMOUS");
	}

	public Boolean iscitizenSearch(User userInfo) {

		return userInfo.getType().equalsIgnoreCase("CITIZEN")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("CITIZEN");
	}
	
	public void updateWaterApplicationStatus(String id, String status) {
		
		Object[] params = { status, id};
		
		int[] types = {Types.VARCHAR, Types.VARCHAR};
		
		jdbcTemplate.update(WsQueryBuilder.UPDATE_DISCONNECT_STATUS, params, types);
		 
	}
	
	@Override
	public WaterConnectionResponse getWaterConnectionListForPlainSearch(SearchCriteria criteria, RequestInfo requestInfo) {

		List<WaterConnection> waterConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		
		Set<String> ids = new HashSet<String>();
		List<String> connectionIds = null;
		if (criteria.getIds() != null && !criteria.getIds().isEmpty())
			ids = criteria.getIds();
		else
			connectionIds = fetchWaterConIds(criteria);

		if(connectionIds!=null && connectionIds.size()>0) {
//		for (String id : connectionIds) {
			ids.addAll(connectionIds);
//		}
		}
		if (ids.isEmpty())
			return new WaterConnectionResponse();

		criteria.setIds(ids);
		
		String query = wsQueryBuilder.getSearchQueryStringForPlainSearch(criteria, preparedStatement, requestInfo);

		if (query == null)
			return null;
		
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		WaterConnectionResponse connectionResponse = new WaterConnectionResponse();
		if (isOpenSearch) {
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), openWaterRowMapper);
			for (WaterConnection waterConnection : waterConnectionList) {
            	convertMeterMakeToString(waterConnection);
            	convertLastMeterDateFormat(waterConnection);

            }
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(openWaterRowMapper.getFull_count()).build();
		} else {
			waterConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), waterRowMapper);
			for (WaterConnection waterConnection : waterConnectionList) {
				convertMeterMakeToString(waterConnection);
				convertLastMeterDateFormat(waterConnection);

			}
			connectionResponse = WaterConnectionResponse.builder().waterConnection(waterConnectionList)
					.totalCount(waterRowMapper.getFull_count()).build();
		}
		return connectionResponse;
	}
	


	private void convertLastMeterDateFormat(WaterConnection wc) {

	    if (wc.getAdditionalDetails() == null)
	        return;

	    if (!(wc.getAdditionalDetails() instanceof ObjectNode))
	        return;

	    ObjectNode additionalDetails = (ObjectNode) wc.getAdditionalDetails();

	    JsonNode node = additionalDetails.get("last_meter_date");

	    // ✅ Dynamic default → connectionExecutionDate
	    String defaultDate = null;
	    try {
	        if (wc.getConnectionExecutionDate() != null && wc.getConnectionExecutionDate() > 0) {
	            LocalDate date = Instant.ofEpochMilli(wc.getConnectionExecutionDate())
	                    .atZone(ZoneId.systemDefault())
	                    .toLocalDate();

	            defaultDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
	        } else {
	            defaultDate = "01-01-2023"; // fallback
	        }
	    } catch (Exception e) {
	        defaultDate = "01-01-2023";
	    }

	    try {

	        // NULL case
	        if (node == null || node.isNull()) {
	            additionalDetails.put("last_meter_date", defaultDate);
	            return;
	        }

	        String value = node.asText().trim();

	        // invalid values → default
	        if (value.equalsIgnoreCase("null") ||
	            value.equalsIgnoreCase("N/A") ||
	            value.equals("0") ||
	            value.isEmpty()) {

	            additionalDetails.put("last_meter_date", defaultDate);
	            return;
	        }

	        String formattedDate = null;

	        // ✅ already epoch
	        if (value.matches("\\d{12,}")) {
	            LocalDate date = Instant.ofEpochMilli(Long.parseLong(value))
	                    .atZone(ZoneId.systemDefault())
	                    .toLocalDate();

	            formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
	        }
	        // dd-MM-yyyy
	        else if (value.matches("\\d{2}-\\d{2}-\\d{4}")) {
	            LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("dd-MM-yyyy"));
	            formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
	        }
	        // yyyy-MM-dd
	        else if (value.matches("\\d{4}-\\d{2}-\\d{2}")) {
	            LocalDate date = LocalDate.parse(value, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
	            formattedDate = date.format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));
	        }
	        // invalid numeric like 3402
	        else if (value.matches("\\d+")) {
	            additionalDetails.put("last_meter_date", defaultDate);
	            return;
	        }

	        // final set
	        if (formattedDate != null)
	            additionalDetails.put("last_meter_date", formattedDate);
	        else
	            additionalDetails.put("last_meter_date", defaultDate);

	    } catch (Exception e) {
	        additionalDetails.put("last_meter_date", defaultDate);
	    }
	}

    public List<String> fetchWaterConIds(SearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();

        StringBuilder query = new StringBuilder("SELECT id FROM eg_ws_connection ");

        boolean hasWhereClause = false;

        // Add fromDate filter if present
        if (criteria.getFromDate() != null) {
            query.append("WHERE createdtime >= ? ");
            preparedStmtList.add(criteria.getFromDate());
            hasWhereClause = true;
        }

        // Add toDate filter if present
        if (criteria.getToDate() != null) {
            if (hasWhereClause) {
                query.append("AND createdtime <= ? ");
            } else {
                query.append("WHERE createdtime <= ? ");
            }
            preparedStmtList.add(criteria.getToDate());
        }

        query.append("ORDER BY createdtime OFFSET ? LIMIT ?");
        preparedStmtList.add(criteria.getOffset());
        preparedStmtList.add(criteria.getLimit());

        List<String> ids = jdbcTemplate.query(query.toString(),
                preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
        return ids;
    }

	/* Method to push the encrypted data to the 'update' topic  */
	@Override
	public void updateOldWaterConnections(WaterConnectionRequest waterConnectionRequest) {
		String key = waterConnectionRequest.getWaterConnection().getConnectionNo();
		waterConnectionProducer.push(updateOldDataEncTopic, key, waterConnectionRequest);
	}

	/* Method to find the total count of applications present in dB */
	@Override
	public Integer getTotalApplications(SearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = wsQueryBuilder.getTotalApplicationsCountQueryString(criteria, preparedStatement);
		if (query == null)
			return 0;
		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	/* Method to push the old data encryption status to the 'ws-enc-audit' topic  */
	@Override
	public void updateEncryptionStatus(EncryptionCount encryptionCount) {
		String key = encryptionCount.getId();
		waterConnectionProducer.push(encryptionStatusTopic, key, encryptionCount);
	}
	@Override
	public List<WaterConnection> getPlainWaterConnectionSearch(SearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = wsQueryBuilder.getWCPlainSearchQuery(criteria, preparedStmtList);
        log.info("Query: " + query +  "\n preparedStmtList:"+ preparedStmtList);
      
        List<WaterConnection> waterconnection =  jdbcTemplate.query(query, preparedStmtList.toArray(), waterRowMapper);
        return waterconnection;
    }
	/* Method to find the last execution details in dB */
	@Override
	public EncryptionCount getLastExecutionDetail(SearchCriteria criteria) {

		List<Object> preparedStatement = new ArrayList<>();
		String query = wsQueryBuilder.getLastExecutionDetail(criteria, preparedStatement);

		log.info("\nQuery executed:" + query);
		if (query == null)
			return null;
		EncryptionCount encryptionCount = jdbcTemplate.query(query, preparedStatement.toArray(), encryptionCountRowMapper);
		return encryptionCount;
	}
	
	/**
	 * Convert meterMake to String in additionalDetails if it exists and is not already a String
	 * Handles both "meterMake" and "metermake" field names
	 * @param waterConnection The water connection object to process
	 */
	private void convertMeterMakeToString(WaterConnection waterConnection) {
		if (waterConnection == null || waterConnection.getAdditionalDetails() == null) {
			return;
		}

		Object additionalDetails = waterConnection.getAdditionalDetails();

		// Handle both Map and ObjectNode types
		if (additionalDetails instanceof Map) {
			Map<String, Object> detailsMap = (Map<String, Object>) additionalDetails;
			// Handle both "meterMake" and "metermake"
			convertMeterMakeFieldInMap(detailsMap, "meterMake");
			convertMeterMakeFieldInMap(detailsMap, "metermake");
		} else if (additionalDetails instanceof com.fasterxml.jackson.databind.node.ObjectNode) {
			com.fasterxml.jackson.databind.node.ObjectNode detailsNode = (com.fasterxml.jackson.databind.node.ObjectNode) additionalDetails;
			// Handle both "meterMake" and "metermake"
			convertMeterMakeFieldInObjectNode(detailsNode, "meterMake");
			convertMeterMakeFieldInObjectNode(detailsNode, "metermake");
		}
	}

	/**
	 * Convert a specific field to String in a Map
	 */
	private void convertMeterMakeFieldInMap(Map<String, Object> detailsMap, String fieldName) {
		if (detailsMap.containsKey(fieldName)) {
			Object fieldValue = detailsMap.get(fieldName);
			if (fieldValue != null && !(fieldValue instanceof String)) {
				detailsMap.put(fieldName, String.valueOf(fieldValue));
				log.debug("Converted {} from {} to String: {}", fieldName, fieldValue.getClass().getSimpleName(), fieldValue);
			}
		}
	}

	/**
	 * Convert a specific field to String in an ObjectNode
	 */
	private void convertMeterMakeFieldInObjectNode(com.fasterxml.jackson.databind.node.ObjectNode detailsNode, String fieldName) {
		if (detailsNode.has(fieldName)) {
			com.fasterxml.jackson.databind.JsonNode fieldNode = detailsNode.get(fieldName);
			if (fieldNode != null && !fieldNode.isNull() && !fieldNode.isTextual()) {
				String fieldValue = fieldNode.asText();
				detailsNode.put(fieldName, fieldValue);
				log.debug("Converted {} from {} to String: {}", fieldName, fieldNode.getNodeType(), fieldValue);
			}
		}
	}

	@Override
	public List<String> fetchWaterConnectionIds(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}

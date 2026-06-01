package org.egov.garbagecollection.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.Role;
import org.egov.common.contract.request.User;
import org.egov.garbagecollection.config.GCConfiguration;
import org.egov.garbagecollection.constants.GCConstants;
import org.egov.garbagecollection.repository.builder.GcQueryBuilder;
import org.egov.garbagecollection.repository.rowmapper.GcRowMapper;
import org.egov.garbagecollection.web.models.GarbageConnectionRequest;
import org.egov.garbagecollection.repository.rowmapper.EncryptionCountRowMapper;
import org.egov.garbagecollection.repository.rowmapper.OpenGCRowMapper;
import org.egov.garbagecollection.web.models.*;
import org.egov.garbagecollection.producer.GCProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class GcDaoImpl implements GcDao {

	@Autowired
	private GCProducer gcProducer;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private GcQueryBuilder gcQueryBuilder;

	@Autowired
	private GcRowMapper gcRowMapper;

	@Autowired
	private OpenGCRowMapper openGcRowMapper;
	
	@Autowired
	private GCConfiguration gcConfiguration;

	@Autowired
	private EncryptionCountRowMapper encryptionCountRowMapper;

	@Value("${egov.garbageservice.creategarbageconnection.topic}")
	private String createWaterConnection;

	@Value("${egov.garbageservice.updategarbageconnection.topic}")
	private String updateWaterConnection;

	@Value("${egov.garbageservice.oldDataEncryptionStatus.topic}")
	private String encryptionStatusTopic;

	@Value("${egov.garbageservice.update.oldData.topic}")
	private String updateOldDataEncTopic;

	@Override
	public void saveGarbageConnection(GarbageConnectionRequest garbageConnectionRequest) {
		String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
		gcProducer.push(createWaterConnection, applicationNo, garbageConnectionRequest);
	}

	@Override
	public List<GarbageConnection> getGarbageConnectionList(SearchCriteria criteria,
			RequestInfo requestInfo) {
		
		List<GarbageConnection> garbageConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		// String query = wsQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo);]
		Boolean iscitizenSearch=iscitizenSearch(requestInfo.getUserInfo());
		String query = gcQueryBuilder.getSearchQueryString(criteria, preparedStatement, requestInfo, iscitizenSearch);

		log.info("Search Query" + query);
		log.info("Parameters for search Query:: " + preparedStatement.toString());
		if (query == null)
			return Collections.emptyList();
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		
		// if(isOpenSearch)
		 if(iscitizenSearch)
		 {
			 garbageConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
						openGcRowMapper);
			 
		 }else if(isOpenSearch)
			garbageConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
					openGcRowMapper);
		else
			garbageConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(),
				gcRowMapper);
		if (garbageConnectionList == null)
			return Collections.emptyList();
		return garbageConnectionList;
	}

	public Integer getGarbageConnectionsCount(SearchCriteria criteria, RequestInfo requestInfo) {
		List<Object> preparedStatement = new ArrayList<>();
		// String query = wsQueryBuilder.getSearchCountQueryString(criteria, preparedStatement, requestInfo);
		Boolean iscitizenSearch=iscitizenSearch(requestInfo.getUserInfo());
		String query = gcQueryBuilder.getSearchCountQueryString(criteria, preparedStatement, requestInfo,iscitizenSearch);
		
		if (query == null)
			return 0;

		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}
	
	@Override
	public void updateGarbageConnection(GarbageConnectionRequest garbageConnectionRequest, boolean isStateUpdatable) {
		String reqAction = garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction();
		
		if (isStateUpdatable) 
		{
			if (GCConstants.EXECUTE_DISCONNECTION.equalsIgnoreCase(reqAction))
			{
				garbageConnectionRequest.getGarbageConnection().setStatus(Connection.StatusEnum.INACTIVE);
			}
			if ((garbageConnectionRequest.isReconnectRequest() || garbageConnectionRequest.getGarbageConnection().getApplicationType().equalsIgnoreCase(GCConstants.GARBAGE_RECONNECTION)) && GCConstants.ACTIVATE_CONNECTION_CONST.equalsIgnoreCase(reqAction)) {
				garbageConnectionRequest.getGarbageConnection().setStatus(Connection.StatusEnum.ACTIVE);
			}
			else if(garbageConnectionRequest.getGarbageConnection().isIsworkflowdisabled())
			{
			// For meter number and rest details addition before payment (02-08-2024)
				String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
				gcProducer.push(updateWaterConnection, applicationNo, garbageConnectionRequest);

			}
			else {
				String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
				gcProducer.push(updateWaterConnection, applicationNo, garbageConnectionRequest);
			}
		}


		else {
			String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
			// For NewGC trimmed flow: PAY → CONNECTION_ACTIVATED directly.
			// Must push to the main update topic so the connectionNo is persisted.
			if (GCConstants.ACTION_PAY.equalsIgnoreCase(reqAction)
					&& GCConstants.STATUS_APPROVED.equalsIgnoreCase(
							garbageConnectionRequest.getGarbageConnection().getApplicationStatus())) {
				garbageConnectionRequest.getGarbageConnection().setStatus(Connection.StatusEnum.ACTIVE);
				gcProducer.push(updateWaterConnection, applicationNo, garbageConnectionRequest);
			} else {
				gcProducer.push(gcConfiguration.getWorkFlowUpdateTopic(), applicationNo, garbageConnectionRequest);
			}
		}
	}
	
//	/**
//	 * push object to create meter reading
//	 *
//	 * @param waterConnectionRequest
//	 */
//	public void postForMeterReading(GarbageConnectionRequest garbageConnectionRequest) {
//		log.info("Posting request to kafka topic - " + gcConfiguration.getCreateMeterReading());
//		gcProducer.push(gcConfiguration.getCreateMeterReading(), garbageConnectionRequest);
//	}

	/**
	 * push object for edit notification
	 * 
	 * @param garbageConnectionRequest
	 */
	public void pushForEditNotification(GarbageConnectionRequest garbageConnectionRequest, boolean isStateUpdatable) {
		if (!GCConstants.EDIT_NOTIFICATION_STATE
				.contains(garbageConnectionRequest.getGarbageConnection().getProcessInstance().getAction())) {
			String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
			gcProducer.push(gcConfiguration.getEditNotificationTopic(), applicationNo, garbageConnectionRequest);
		}
	}
	
	/**
	 * Enrich file store Id's
	 * 
	 * @param garbageConnectionRequest
	 */
	public void enrichFileStoreIds(GarbageConnectionRequest garbageConnectionRequest) {
		String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
		gcProducer.push(gcConfiguration.getFileStoreIdsTopic(), applicationNo, garbageConnectionRequest);
	}
	
	/**
	 * Save file store Id's
	 * 
	 * @param garbageConnectionRequest
	 */
	public void saveFileStoreIds(GarbageConnectionRequest garbageConnectionRequest) {
		String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
		gcProducer.push(gcConfiguration.getSaveFileStoreIdsTopic(), applicationNo, garbageConnectionRequest);
	}

	public Boolean isSearchOpen(User userInfo) {

		return userInfo.getType().equalsIgnoreCase("SYSTEM")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("ANONYMOUS");
	}

	public Boolean iscitizenSearch(User userInfo) {

		return userInfo.getType().equalsIgnoreCase("CITIZEN")
				&& userInfo.getRoles().stream().map(Role::getCode).collect(Collectors.toSet()).contains("CITIZEN");
	}
	
	public void updateGarbageApplicationStatus(String id, String status) {
		
		Object[] params = { status, id};
		
		int[] types = {Types.VARCHAR, Types.VARCHAR};
		
		jdbcTemplate.update(gcQueryBuilder.UPDATE_DISCONNECT_STATUS, params, types);
		 
	}
	
	@Override
	public GarbageConnectionResponse getGarbageConnectionListForPlainSearch(SearchCriteria criteria, RequestInfo requestInfo) {

		List<GarbageConnection> garbageConnectionList = new ArrayList<>();
		List<Object> preparedStatement = new ArrayList<>();
		
		Set<String> ids = new HashSet<String>();
		List<String> connectionIds = null;
		if (criteria.getIds() != null && !criteria.getIds().isEmpty())
			ids = criteria.getIds();
		else
			connectionIds = fetchGarbageConIds(criteria);

		if(connectionIds!=null && connectionIds.size()>0) {
//		for (String id : connectionIds) {
			ids.addAll(connectionIds);
//		}
		}
		if (ids.isEmpty())
			return new GarbageConnectionResponse();

		criteria.setIds(ids);
		
		String query = gcQueryBuilder.getSearchQueryStringForPlainSearch(criteria, preparedStatement, requestInfo);

		if (query == null)
			return null;
		
		Boolean isOpenSearch = isSearchOpen(requestInfo.getUserInfo());
		GarbageConnectionResponse connectionResponse = new GarbageConnectionResponse();
		if (isOpenSearch) {
			garbageConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), openGcRowMapper);
			connectionResponse = GarbageConnectionResponse.builder().garbageConnections(garbageConnectionList)
					.totalCount(openGcRowMapper.getFull_count()).build();
		} else {
			garbageConnectionList = jdbcTemplate.query(query, preparedStatement.toArray(), gcRowMapper);
			connectionResponse = GarbageConnectionResponse.builder().garbageConnections(garbageConnectionList)
					.totalCount(gcRowMapper.getFull_count()).build();
		}
		return connectionResponse;
	}
	
	public List<String> fetchGarbageConIds(SearchCriteria criteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(criteria.getOffset());
		preparedStmtList.add(criteria.getLimit());

		List<String> ids = jdbcTemplate.query("SELECT id from eg_gc_connection ORDER BY createdtime offset " +
						" ? " +
						"limit ? ",
				preparedStmtList.toArray(),
				new SingleColumnRowMapper<>(String.class));
		return ids;
	}

	/* Method to push the encrypted data to the 'update' topic  */
		@Override
	public void updateOldGarbageConnections(GarbageConnectionRequest garbageConnectionRequest) {
            String applicationNo = garbageConnectionRequest.getGarbageConnection().getApplicationNo();
		gcProducer.push(updateOldDataEncTopic,applicationNo, garbageConnectionRequest);
	}

	/* Method to find the total count of applications present in dB */
	@Override
	public Integer getTotalApplications(SearchCriteria criteria) {
		List<Object> preparedStatement = new ArrayList<>();
		String query = gcQueryBuilder.getTotalApplicationsCountQueryString(criteria, preparedStatement);
		if (query == null)
			return 0;
		Integer count = jdbcTemplate.queryForObject(query, preparedStatement.toArray(), Integer.class);
		return count;
	}

	/* Method to push the old data encryption status to the 'ws-enc-audit' topic  */
	@Override
	public void updateEncryptionStatus(EncryptionCount encryptionCount) {

		gcProducer.push(encryptionStatusTopic,encryptionCount.getId(), encryptionCount);
	}
	@Override
	public List<GarbageConnection> getPlainGarbageConnectionSearch(SearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = gcQueryBuilder.getGCPlainSearchQuery(criteria, preparedStmtList);
        log.info("Query: " + query +  "\n preparedStmtList:"+ preparedStmtList);
      
        List<GarbageConnection> garbageconnection =  jdbcTemplate.query(query, preparedStmtList.toArray(), gcRowMapper);
        return garbageconnection;
    }
	/* Method to find the last execution details in dB */
	@Override
	public EncryptionCount getLastExecutionDetail(SearchCriteria criteria) {

		List<Object> preparedStatement = new ArrayList<>();
		String query = gcQueryBuilder.getLastExecutionDetail(criteria, preparedStatement);

		log.info("\nQuery executed:" + query);
		if (query == null)
			return null;
		EncryptionCount encryptionCount = jdbcTemplate.query(query, preparedStatement.toArray(), encryptionCountRowMapper);
		return encryptionCount;
	}

	@Override
	public List<String> fetchGarbageConnectionIds(SearchCriteria criteria) {
		// TODO Auto-generated method stub
		return null;
	}
	
}

package org.egov.pgr.repository;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.pgr.repository.rowmapper.CountRowMapper;
import org.egov.pgr.repository.rowmapper.PGRNotificationRowMapper;
import org.egov.pgr.repository.rowmapper.PGRQueryBuilder;
import org.egov.pgr.repository.rowmapper.PGRRowMapper;
import org.egov.pgr.util.PGRConstants;
import org.egov.pgr.web.models.ServiceWrapper;
import org.egov.pgr.web.models.CountStatusRequest;
import org.egov.pgr.web.models.CountStatusUpdate;
import org.egov.pgr.web.models.PGRNotification;
import org.egov.pgr.web.models.PgrNotificationSearchCriteria;
import org.egov.pgr.web.models.RequestSearchCriteria;
import org.egov.pgr.web.models.Service;
import org.egov.pgr.web.models.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

@Repository
@Slf4j
public class PGRRepository {


    private PGRQueryBuilder queryBuilder;

    private PGRRowMapper rowMapper;

    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    private CountRowMapper countRowMapper;
    
    @Autowired
    private PGRNotificationRowMapper pgrNotificationRowMapper;
    
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    
    @Autowired
    public PGRRepository(PGRQueryBuilder queryBuilder, PGRRowMapper rowMapper, JdbcTemplate jdbcTemplate) {
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * searches services based on search criteria and then wraps it into serviceWrappers
     * @param criteria
     * @return
     */
    public List<ServiceWrapper> getServiceWrappers(RequestSearchCriteria criteria){
        List<Service> services = getServices(criteria);
        List<String> serviceRequestids = services.stream().map(Service::getServiceRequestId).collect(Collectors.toList());
        Map<String, Workflow> idToWorkflowMap = new HashMap<>();
        List<ServiceWrapper> serviceWrappers = new ArrayList<>();

        for(Service service : services){
            ServiceWrapper serviceWrapper = ServiceWrapper.builder().service(service).workflow(idToWorkflowMap.get(service.getServiceRequestId())).build();
            serviceWrappers.add(serviceWrapper);
        }
        return serviceWrappers;
    }

    /**
     * searches services based on search criteria
     * @param criteria
     * @return
     */
    public List<Service> getServices(RequestSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getPGRSearchQuery(criteria, preparedStmtList);
        List<Service> services =  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
        return services;
    }

    /**
     * Returns the count based on the search criteria
     * @param criteria
     * @return
     */
    public Integer getCount(RequestSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getCountQuery(criteria, preparedStmtList);
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }


	public Map<String, Integer> fetchDynamicData(String tenantId) {
		List<Object> preparedStmtListCompalintsResolved = new ArrayList<>();
		String query = queryBuilder.getResolvedComplaints(tenantId,preparedStmtListCompalintsResolved );

		int complaintsResolved = jdbcTemplate.queryForObject(query,preparedStmtListCompalintsResolved.toArray(),Integer.class);

		List<Object> preparedStmtListAverageResolutionTime = new ArrayList<>();
		query = queryBuilder.getAverageResolutionTime(tenantId, preparedStmtListAverageResolutionTime);

		int averageResolutionTime = jdbcTemplate.queryForObject(query, preparedStmtListAverageResolutionTime.toArray(),Integer.class);

		Map<String, Integer> dynamicData = new HashMap<String,Integer>();
		dynamicData.put(PGRConstants.COMPLAINTS_RESOLVED, complaintsResolved);
		dynamicData.put(PGRConstants.AVERAGE_RESOLUTION_TIME, averageResolutionTime);

		return dynamicData;
	}


	public List<CountStatusUpdate> countSearch(@Valid CountStatusRequest request) throws JsonProcessingException {
		List<String> preparedStatementValues = new ArrayList<>();
        //StringBuilder searchQuery = new StringBuilder(queryBuilder.COUNT_QUERY);
        StringBuilder searchQuery = new StringBuilder(queryBuilder.COUNT_APPLCATIONSTATUS_SUMMARY); 
        log.info(searchQuery.toString());
        searchQuery = addWhereClause(searchQuery, preparedStatementValues, request);
        return jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), countRowMapper);
	}


	private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
			@Valid CountStatusRequest request) throws JsonProcessingException {
		
		boolean isValid = request.getCountStatusUpdate().stream()
			    .allMatch(i -> StringUtils.isEmpty(i.getTenantId()) &&
			                   StringUtils.isEmpty(i.getServiceCode()) &&
			                   (i.getAdditionalDetails() == null) &&
			                   StringUtils.isEmpty(i.getDateRange()) &&
			                   (i.getEndDate() == null && i.getEndDate() == null));

			if (isValid) {
			    return searchQuery;
			}
		
		 searchQuery.append(" WHERE");
	        boolean isAppendAndClause = false;
	        
	        for(CountStatusUpdate i : request.getCountStatusUpdate()) {
	        	 if(!StringUtils.isEmpty(i.getTenantId())){
	 	        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	 	        	searchQuery.append(" pt.tenantid = ? ");
	 	        	preparedStatementValues.add(i.getTenantId());
	 	        }
	 	        if(!StringUtils.isEmpty(i.getServiceCode())) {
	 	        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	 	        	searchQuery.append(" pt.servicecode = ? ");
	 	        	 preparedStatementValues.add(i.getServiceCode());
	 	        }
	 			/*
	 			 * if(!StringUtils.isEmpty(request.getCountStatusUpdate().getDateRange())){
	 			 * isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	 			 * searchQuery.append(" pt.servicecode = ? "); }
	 			 */
	 	        if(null != i.getEndDate() && null != i.getEndDate()) {
	 	        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	 	        	searchQuery.append(" pt.lastmodifiedtime between ? AND ? ");
	 	        	 preparedStatementValues.add(i.getFromDate());
	 	        	preparedStatementValues.add(i.getEndDate());
	 	        }
	 	        if(null!=(i.getAdditionalDetails())) {
	 	        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
	 	            searchQuery.append(" pt.additionaldetails @> ?::jsonb ");
	 	            preparedStatementValues.add(new ObjectMapper().writeValueAsString(i.getAdditionalDetails()));
	 	        }
	 	        
	 		
	        }
	        return searchQuery; 
	}


	private boolean addAndClauseIfRequired(boolean b, StringBuilder searchQuery) {
		  if (b)
			  searchQuery.append(" AND");

	        return true;
	}


	public List<PGRNotification> getPgrNotifications(PgrNotificationSearchCriteria pgrNotificationSearchCriteria) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = queryBuilder.getPGRNotificationSearchQuery(pgrNotificationSearchCriteria, preparedStmtList);
		List<PGRNotification> pgrNotifications = jdbcTemplate.query(query, preparedStmtList.toArray(),
				pgrNotificationRowMapper);
		return pgrNotifications;
	}


	public void deletePgrNotifications(List<String> uuidList) {
		final Map<String, Object> uuidInputs = new HashMap<String, Object>();
		uuidInputs.put("uuid", uuidList);
		String query = queryBuilder.getPGRNotificationDeleteQuery();
		if (!CollectionUtils.isEmpty(uuidList)) {
			namedParameterJdbcTemplate.update(query, uuidInputs);
		}
	}



}

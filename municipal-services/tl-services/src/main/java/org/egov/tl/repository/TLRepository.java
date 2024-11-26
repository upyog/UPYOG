package org.egov.tl.repository;

import static org.egov.tl.util.TLConstants.ACTION_ADHOC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.producer.Producer;
import org.egov.tl.repository.builder.TLQueryBuilder;
import org.egov.tl.repository.rowmapper.TLRowMapper;
import org.egov.tl.util.TLConstants;
import org.egov.tl.web.models.Accessory;
import org.egov.tl.web.models.ApplicationStatusChangeRequest;
import org.egov.tl.web.models.Document;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseRequest;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.TradeUnit;
import org.egov.tl.web.models.User;
import org.egov.tl.workflow.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Repository
public class TLRepository {

    private JdbcTemplate jdbcTemplate;

    private TLQueryBuilder queryBuilder;

    private TLRowMapper rowMapper;

    private Producer producer;

    private TLConfiguration config;

    private WorkflowService workflowService;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public TLRepository(JdbcTemplate jdbcTemplate, TLQueryBuilder queryBuilder, TLRowMapper rowMapper,
                        Producer producer, TLConfiguration config, WorkflowService workflowService) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.producer = producer;
        this.config = config;
        this.workflowService = workflowService;
    }


    /**
     * Searhces license in databse
     *
     * @param criteria The tradeLicense Search criteria
     * @return List of TradeLicense from seach
     */
    public List<TradeLicense> getLicenses(TradeLicenseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTLSearchQuery(criteria, preparedStmtList,false);
        List<TradeLicense> licenses =  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
        sortChildObjectsById(licenses);
        return licenses;
    }
    
    public int getLicenseCount(TradeLicenseSearchCriteria criteria) {
    	List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTLSearchQuery(criteria, preparedStmtList,true);
        int licenseCount = jdbcTemplate.queryForObject(query,preparedStmtList.toArray(),Integer.class);
        return licenseCount;
    }
    
    public Map<String,Integer> getApplicationsCount(TradeLicenseSearchCriteria criteria) {
    	List<Object> preparedStmtListIssued = new ArrayList<>();
        String query = queryBuilder.getApplicationsCountQuery(criteria, preparedStmtListIssued, TLConstants.APPLICATION_TYPE_NEW);
        int issuedCount = jdbcTemplate.queryForObject(query,preparedStmtListIssued.toArray(),Integer.class);
        
        List<Object> preparedStmtListRenewal = new ArrayList<>();
        query = queryBuilder.getApplicationsCountQuery(criteria, preparedStmtListRenewal, TLConstants.APPLICATION_TYPE_RENEWAL);
        int renewedCount = jdbcTemplate.queryForObject(query,preparedStmtListRenewal.toArray(),Integer.class);
        
        Map<String,Integer> countsMap = new HashMap<String,Integer>();
        countsMap.put(TLConstants.ISSUED_COUNT, issuedCount);
        countsMap.put(TLConstants.RENEWED_COUNT, renewedCount);
        
        return countsMap;
    }

    /**
     * Pushes the request on save topic
     *
     * @param tradeLicenseRequest The tradeLciense create request
     */
    public void save(TradeLicenseRequest tradeLicenseRequest) {
    	log.info("going to persist " +tradeLicenseRequest);
        producer.push(config.getSaveTopic(), tradeLicenseRequest);
    }
    /**
     * Pushes the update request to update topic or on workflow topic depending on the status
     *
     * @param tradeLicenseRequest The update requuest
     */
    public void update(TradeLicenseRequest tradeLicenseRequest,Map<String,Boolean> idToIsStateUpdatableMap) {
        RequestInfo requestInfo = tradeLicenseRequest.getRequestInfo();
        List<TradeLicense> licenses = tradeLicenseRequest.getLicenses();

        List<TradeLicense> licesnsesForStatusUpdate = new LinkedList<>();
        List<TradeLicense> licensesForUpdate = new LinkedList<>();
        List<TradeLicense> licensesForAdhocChargeUpdate = new LinkedList<>();


        for (TradeLicense license : licenses) {
            if (null != idToIsStateUpdatableMap
            		&& BooleanUtils.isTrue(idToIsStateUpdatableMap.get(license.getId()))) {
                licensesForUpdate.add(license);
            }
            else if(license.getAction().equalsIgnoreCase(ACTION_ADHOC))
                licensesForAdhocChargeUpdate.add(license);
            else {
                licesnsesForStatusUpdate.add(license);
            }
        }

        if (!CollectionUtils.isEmpty(licensesForUpdate))
            producer.push(config.getUpdateTopic(), new TradeLicenseRequest(requestInfo, licensesForUpdate));

        if (!CollectionUtils.isEmpty(licesnsesForStatusUpdate))
            producer.push(config.getUpdateWorkflowTopic(), new TradeLicenseRequest(requestInfo, licesnsesForStatusUpdate));

        if(!licensesForAdhocChargeUpdate.isEmpty())
            producer.push(config.getUpdateAdhocTopic(),new TradeLicenseRequest(requestInfo,licensesForAdhocChargeUpdate));

    }




    /**
     * Sorts the child objects by  there ids
     * @param tradeLicenses The list of tradeLicense
     */
    private void sortChildObjectsById(List<TradeLicense> tradeLicenses){
        if(CollectionUtils.isEmpty(tradeLicenses))
            return;
        tradeLicenses.forEach(license -> {
            license.getTradeLicenseDetail().getOwners().sort(Comparator.comparing(User::getUuid));
            license.getTradeLicenseDetail().getTradeUnits().sort(Comparator.comparing(TradeUnit::getId));
            if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getAccessories()))
                license.getTradeLicenseDetail().getAccessories().sort(Comparator.comparing(Accessory::getId));

            List<Document> applnDocuments = license.getTradeLicenseDetail().getApplicationDocuments();
            if(!CollectionUtils.isEmpty(applnDocuments)) {
                Collections.reverse(applnDocuments);
                license.getTradeLicenseDetail().setApplicationDocuments(applnDocuments);
            }
            if(!CollectionUtils.isEmpty(license.getTradeLicenseDetail().getVerificationDocuments()))
                license.getTradeLicenseDetail().getVerificationDocuments().sort(Comparator.comparing(Document::getId));
        });
    }

    public List<TradeLicense> getPlainLicenseSearch(TradeLicenseSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getTLPlainSearchQuery(criteria, preparedStmtList);
        log.info("Query: " + query);
        List<TradeLicense> licenses =  jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
        sortChildObjectsById(licenses);
        return licenses;
    }

    public List<String> fetchTradeLicenseIds(TradeLicenseSearchCriteria criteria){

        List<Object> preparedStmtList = new ArrayList<>();
        preparedStmtList.add(criteria.getOffset());
        preparedStmtList.add(criteria.getLimit());

        return jdbcTemplate.query("SELECT id from eg_tl_tradelicense ORDER BY createdtime offset " +
                        " ? " +
                        "limit ? ",
                preparedStmtList.toArray(),
                new SingleColumnRowMapper<>(String.class));
    }
    
    public List <String> fetchTradeLicenseTenantIds(){
    	List<Object> preparedStmtList = new ArrayList<>();
    	return jdbcTemplate.query(queryBuilder.TENANTIDQUERY,preparedStmtList.toArray(),new SingleColumnRowMapper<>(String.class));
    	
    }


	public void updateTlStatus(String businessId, String action) {
		
		Map<String, Object> inputs = new HashMap<>();
		inputs.put("status", action);
		inputs.put("applicationNumber", businessId);
		
		String updateQuery = "UPDATE eg_tl_tradelicense set status =:status WHERE applicationnumber =:applicationNumber ";
		
		namedParameterJdbcTemplate.update(updateQuery,inputs);
		
	}


	public void updateStateOfApplicationApplied(ApplicationStatusChangeRequest applicationStatusChangeRequest) {
		Map<String, Object> inputs = new HashMap<>();

		inputs.put("status", applicationStatusChangeRequest.getApplicationStatus());
		inputs.put("applicationNumber", applicationStatusChangeRequest.getApplicationNumber());
		
		String updateQuery = "UPDATE eg_tl_tradelicense set status =:status WHERE applicationnumber =:applicationNumber ";
		
		namedParameterJdbcTemplate.update(updateQuery,inputs);
	}


	public List<String> getTypesOfAllApplications(Boolean isHistoryCall, String tenantId) {
		List<String> statusList = null;
		String query = null;
    	List<Object> preparedStmtList = new ArrayList<>();
		
		if (BooleanUtils.isTrue(isHistoryCall)) {
			query = "select applicationtype from eg_tl_tradelicense_audit where \"action\" = 'APPROVE' and  status = 'APPROVED'";
		}else {
			if (StringUtils.isEmpty(tenantId)) {
				query = "select applicationtype from eg_tl_tradelicense";
			} else {
				query = "select applicationtype from eg_tl_tradelicense WHERE tenantid = ?";
				preparedStmtList.add(tenantId);
			} 
		}
		statusList = jdbcTemplate.query(query,preparedStmtList.toArray(),(rs, rowNum) -> rs.getString("applicationtype"));
		
		return statusList;
	}
    

}

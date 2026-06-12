package org.egov.swcalculation.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.swcalculation.config.SWCalculationConfiguration;
import org.egov.swcalculation.producer.SWCalculationProducer;
import org.egov.swcalculation.repository.builder.DemandQueryBuilder;
import org.egov.swcalculation.repository.rowMapper.DemandRowMapper;
import org.egov.swcalculation.web.models.Demand;
import org.egov.swcalculation.web.models.DemandNotificationObj;
import org.egov.swcalculation.web.models.DemandRequest;
import org.egov.swcalculation.web.models.DemandResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.CollectionUtils;

@Repository
public class DemandRepository {

	   @Autowired
	    private ServiceRequestRepository serviceRequestRepository;
	
	    @Autowired
	    private SWCalculationConfiguration config;
	
	    @Autowired
	    private ObjectMapper mapper;
	    
	    @Autowired
	    private DemandQueryBuilder demandQueryBuilder;

	    @Autowired
	    private DemandRowMapper demandRowMapper;

		@Autowired
		private JdbcTemplate jdbcTemplate;

		@Autowired
		private SWCalculationProducer swCalculationProducer;


	/**
	     * Creates demand
	     * @param requestInfo The RequestInfo of the calculation Request
	     * @param demands The demands to be created
	     * @return The list of demand created
	     */
	    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands, DemandNotificationObj notificationObj){
	        DemandRequest request = new DemandRequest(requestInfo,demands);
	        try{
				swCalculationProducer.push(config.getSaveDemand(), request);
				notificationObj.setSuccess(true);
				String key = demands.get(0).getConsumerCode();
				swCalculationProducer.push(config.getOnDemandSuccess(), key, notificationObj);
				return demands;
	        }
	        catch(Exception e){
				notificationObj.setSuccess(false);
				String key = demands.get(0).getConsumerCode();
				swCalculationProducer.push(config.getOnDemandFailed(), key, notificationObj);
				throw new CustomException("EG_SW_KAFKA_ERROR","Failed to push demands to Kafka save-demand topic: " + e.getMessage());
	        }
	    }
	    
	    /**
	     * Updates the demand
	     * @param requestInfo The RequestInfo of the calculation Request
	     * @param demands The demands to be updated
	     * @return The list of demand updated
	     */
	    public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands){
	        DemandRequest request = new DemandRequest(requestInfo,demands);
	        try{
	            swCalculationProducer.push(config.getUpdateDemand(), request);
	            return demands;
	        }
	        catch(Exception e){
	            throw new CustomException("EG_SW_KAFKA_ERROR","Failed to push demands to Kafka update-demand topic: " + e.getMessage());
	        }
	    }
	    
	    
		/**
		 * Fetches demand from DB based on a map of business code and set of consumer codes
		 * 
		 * @param businessConsumercodeMap
		 * @param tenantId
		 * @return
		 */
		public List<Demand> getDemandsForConsumerCodes(Set<String> businessConsumercodes, String tenantId) {

			List<Object> presparedStmtList = new ArrayList<>();
			String sql = demandQueryBuilder.getDemandQueryForConsumerCodes(businessConsumercodes, presparedStmtList,
					tenantId);
			return jdbcTemplate.query(sql, presparedStmtList.toArray(), demandRowMapper);
		}
	
	

}

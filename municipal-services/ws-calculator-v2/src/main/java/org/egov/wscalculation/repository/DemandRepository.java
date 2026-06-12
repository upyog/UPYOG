package org.egov.wscalculation.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.egov.wscalculation.config.WSCalculationConfiguration;
import org.egov.wscalculation.constants.WSCalculationConstant;
import org.egov.wscalculation.web.models.Demand;
import org.egov.wscalculation.web.models.DemandNotificationObj;
import org.egov.wscalculation.web.models.DemandRequest;
import org.egov.wscalculation.web.models.DemandResponse;
import org.egov.wscalculation.producer.WSCalculationProducer;
import org.egov.wscalculation.repository.builder.DemandQueryBuilder;
import org.egov.wscalculation.repository.rowmapper.DemandRowMapper;
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
	private WSCalculationConfiguration config;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private WSCalculationProducer wsCalculationProducer;

	@Autowired
	private DemandQueryBuilder demandQueryBuilder;

	@Autowired
	private DemandRowMapper demandRowMapper;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Creates demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be created
	 * @return The list of demand created
	 */
	public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands,
			DemandNotificationObj notificationObj) {
		DemandRequest request = new DemandRequest(requestInfo, demands);
		try {
			wsCalculationProducer.push(config.getSaveDemand(), request);
			notificationObj.setSuccess(true);
			wsCalculationProducer.push(config.getOnDemandsSaved(), notificationObj);
			return demands;
		} catch (Exception e) {
			notificationObj.setSuccess(false);
			wsCalculationProducer.push(config.getOnDemandsFailure(), notificationObj);
			throw new CustomException("EG_WS_KAFKA_ERROR", "Failed to push demands to Kafka save-demand topic: " + e.getMessage());
		}
	}
	 /**
      * Creates demand
      * @param requestInfo The RequestInfo of the calculation Request
      * @param demands The demands to be created
      * @return The list of demand created
      */
    public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demands){
        DemandRequest request = new DemandRequest(requestInfo,demands);
        try{
            wsCalculationProducer.push(config.getSaveDemand(), request);
            return demands;
        }
        catch(Exception e){
            throw new CustomException("EG_WS_KAFKA_ERROR","Failed to push demands to Kafka save-demand topic: " + e.getMessage());
        }
    }

	/**
	 * Updates the demand
	 * 
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be updated
	 * @return The list of demand updated
	 */
	public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {
		DemandRequest request = new DemandRequest(requestInfo, demands);
		try {
			wsCalculationProducer.push(config.getUpdateDemand(), request);
			return demands;
		} catch (Exception e) {
			throw new CustomException("EG_WS_KAFKA_ERROR", "Failed to push demands to Kafka update-demand topic: " + e.getMessage());
		}
	}

	/**
	 * Fetches demand from DB based on a map of business code and set of consumer
	 * codes
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

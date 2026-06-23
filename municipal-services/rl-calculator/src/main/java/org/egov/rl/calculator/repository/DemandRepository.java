package org.egov.rl.calculator.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.rl.calculator.repository.rowmapper.DemandDetailRowMapper;
import org.egov.rl.calculator.repository.rowmapper.DemandRowMapper;
import org.egov.rl.calculator.util.Configurations;
import org.egov.rl.calculator.web.models.demand.Demand;
import org.egov.rl.calculator.web.models.demand.DemandDetail;
import org.egov.rl.calculator.web.models.demand.DemandRequest;
import org.egov.rl.calculator.web.models.demand.DemandResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class DemandRepository {

	@Autowired
	private org.egov.rl.calculator.repository.Repository serviceRequestRepository;

	@Autowired
	private Configurations config;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private DemandRowMapper demandRowMapper;

	@Autowired
	private DemandDetailRowMapper demandDetailRowMapper;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/**
	 * Creates demand
	 *
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @return The list of demand created
	 */
	public List<Demand> saveDemand(RequestInfo requestInfo, List<Demand> demand) {
		StringBuilder url = new StringBuilder(config.getBillingServiceHost());
		url.append(config.getDemandCreateEndPoint());
		DemandRequest request = new DemandRequest(requestInfo, demand);
		log.info("Request object for fetchResult: " + request);
		log.info("URL for fetchResult: " + url);
		Object result = serviceRequestRepository.fetchResult(url, request);
		log.info("Result from fetchResult method: " + result);
		DemandResponse response = null;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
			log.info("Demand response mapper: " + response);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of create demand");
		}
		return response.getDemands();
	}

	/**
	 * Updates the demand
	 *
	 * @param requestInfo The RequestInfo of the calculation Request
	 * @param demands     The demands to be updated
	 * @return The list of demand updated
	 */
	public List<Demand> updateDemand(RequestInfo requestInfo, List<Demand> demands) {
		StringBuilder url = new StringBuilder(config.getBillingServiceHost());
		url.append(config.getDemandUpdateEndPoint());
		DemandRequest request = DemandRequest.builder().demands(demands).requestInfo(requestInfo).build();

		Object result = serviceRequestRepository.fetchResult(url, request);
		DemandResponse response = null;
		try {
			response = mapper.convertValue(result, DemandResponse.class);
		} catch (IllegalArgumentException e) {
			throw new CustomException("PARSING ERROR", "Failed to parse response of update demand");
		}
		return response.getDemands();

	}

	public List<Demand> getDemandsByConsumerCode(List<String> rentableIds) {
		if (rentableIds == null || rentableIds.isEmpty()) {
			return Collections.emptyList(); // avoid "IN ()" SQL
		}
		List<Object> preparedStmtList = new ArrayList<>();
		List<Object> subQueryParams = new ArrayList<>();

		String consumerCode = rentableIds.stream().map(id ->id).collect(Collectors.joining(", "));

		String sql = "SELECT * FROM egbs_demand_v1 WHERE consumercode IN (?) ORDER BY createdtime DESC LIMIT 1";
		log.info("consumerCode :: " + consumerCode);
		subQueryParams.add(consumerCode);
		try {
			preparedStmtList.addAll(subQueryParams);
			return jdbcTemplate.query(sql, preparedStmtList.toArray(), demandRowMapper);
		} catch (NoSuchElementException e) {
			return Collections.emptyList();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching demands for rentable IDs and period", e);
			throw new CustomException("DEMAND_FETCH_ERROR",
					"Failed to fetch demands for the given rentable IDs and period");
		}
	}
	
	public List<Demand> getDemandsNotiByConsumerCode(List<String> rentableIds) {
		if (rentableIds == null || rentableIds.isEmpty()) {
			return Collections.emptyList(); // avoid "IN ()" SQL
		}
		List<Object> preparedStmtList = new ArrayList<>();
		List<Object> subQueryParams = new ArrayList<>();

		String consumerCode = rentableIds.stream().map(id ->id).collect(Collectors.joining(", "));

		String sql = "SELECT * FROM egbs_demand_v1 WHERE consumercode IN (?) AND ispaymentcompleted=false ORDER BY createdtime DESC LIMIT 1";
		log.info("consumerCode :: " + consumerCode);
		subQueryParams.add(consumerCode);
		try {
			preparedStmtList.addAll(subQueryParams);
			return jdbcTemplate.query(sql, preparedStmtList.toArray(), demandRowMapper);
		} catch (NoSuchElementException e) {
			return Collections.emptyList();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("Error while fetching demands for rentable IDs and period", e);
			throw new CustomException("DEMAND_FETCH_ERROR",
					"Failed to fetch demands for the given rentable IDs and period");
		}
	}

	public List<String> getDistinctTenantIds() {
		String sql = "SELECT tenant_id FROM eg_rl_allotment GROUP BY tenant_id";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public Demand getDemandsByConsumerCodeAndPerioud(String consumercode, long startDate, long endDate) {

		List<Object> preparedStmtList = new ArrayList<>();
		List<Object> subQueryParams = new ArrayList<>();

		if (consumercode == null) {
			return null; // avoid "IN ()" SQL
		}

		String sql = "SELECT * FROM egbs_demand_v1 WHERE consumercode IN (?) AND taxperiodfrom=? and taxperiodto=?";

		subQueryParams.add(consumercode);
		subQueryParams.add(startDate);
		subQueryParams.add(endDate);
		try {

			preparedStmtList.addAll(subQueryParams);
			return jdbcTemplate.query(sql, preparedStmtList.toArray(), demandRowMapper).stream().findFirst()
					.orElse(null);
		} catch (NoSuchElementException e) {
			return null;
		} catch (Exception e) {
			log.error("Error while fetching demands for rentable IDs and period", e);
			throw new CustomException("DEMAND_FETCH_ERROR",
					"Failed to fetch demands for the given rentable IDs and period");
		}
	}

	public List<Demand> getDemandsByConsumerCodeByOrderBy(List<String> applicationNumber) {
		if (applicationNumber == null || applicationNumber.isEmpty()) {
			return Collections.emptyList(); // avoid "IN ()" SQL
		}
		List<Object> preparedStmtList = new ArrayList<>();
		List<Object> subQueryParams = new ArrayList<>();
		String consumerCode = applicationNumber.stream().map(id ->id).collect(Collectors.joining(", "));

		String sql = "SELECT * FROM egbs_demand_v1 WHERE consumercode IN (?) ORDER BY textperiodto DESC limit 1";
		subQueryParams.add(consumerCode);
		try {

			preparedStmtList.addAll(subQueryParams);
			return jdbcTemplate.query(sql, preparedStmtList.toArray(), demandRowMapper);
		} catch (NoSuchElementException e) {
			return Collections.emptyList();
		}  catch (Exception e) {
			log.error("Error while fetching demands for rentable IDs and period", e);
			throw new CustomException("DEMAND_FETCH_ERROR",
					"Failed to fetch demands for the given rentable IDs and period");
		}
	}

	public List<DemandDetail> getDemandsDetailsByDemandId(List<String> dId) {
		if (dId == null || dId.isEmpty()) {
			return Collections.emptyList();
		}
		List<Object> preparedStmtList = new ArrayList<>();
		List<Object> subQueryParams = new ArrayList<>();
		String demandId = dId.stream().map(id ->id).collect(Collectors.joining(", "));

		String query = "SELECT * FROM egbs_demanddetail_v1 WHERE demandid IN (?)";

		subQueryParams.add(demandId);
		try {
			preparedStmtList.addAll(subQueryParams);
			return jdbcTemplate.query(query, preparedStmtList.toArray(), demandDetailRowMapper);
		} catch (NoSuchElementException e) {
			return Collections.emptyList();
		} catch (Exception e) {
			log.error("Error while fetching demands for rentable IDs and period", e);
			throw new CustomException("DEMAND_FETCH_ERROR",
					"Failed to fetch demands for the given rentable IDs and period");
		}
	}

	public List<Demand> getExpiredUnpaidDemands(String tenantId, long currentTime, String consumerCode) {
		List<Object> preparedStmtList = new ArrayList<>();
		StringBuilder queryBuilder = new StringBuilder("SELECT * FROM egbs_demand_v1 WHERE tenantid = ? AND (createdtime + billexpirytime) <= ? AND ispaymentcompleted = false AND businessservice = 'rl-services' AND status = 'ACTIVE'");
		preparedStmtList.add(tenantId);
		preparedStmtList.add(currentTime);

		if (consumerCode != null && !consumerCode.trim().isEmpty()) {
			queryBuilder.append(" AND consumercode = ?");
			preparedStmtList.add(consumerCode);
		}

		try {
			return jdbcTemplate.query(queryBuilder.toString(), preparedStmtList.toArray(), demandRowMapper);
		} catch (Exception e) {
			log.error("Error while fetching expired unpaid demands", e);
			throw new CustomException("DEMAND_FETCH_ERROR", "Failed to fetch expired unpaid demands");
		}
	}

	public List<Demand> getAllUnpaidDemands(String tenantId, String consumerCode) {
		List<Object> preparedStmtList = new ArrayList<>();
		String query = "SELECT * FROM egbs_demand_v1 WHERE tenantid = ? AND consumercode = ? AND businessservice = 'rl-services' AND status = 'ACTIVE' AND ispaymentcompleted = false";
		preparedStmtList.add(tenantId);
		preparedStmtList.add(consumerCode);
		try {
			return jdbcTemplate.query(query, preparedStmtList.toArray(), demandRowMapper);
		} catch (Exception e) {
			log.error("Error while fetching unpaid demands for tenant: {} and consumerCode: {}", tenantId, consumerCode, e);
			throw new CustomException("DEMAND_FETCH_ERROR", "Failed to fetch unpaid demands");
		}
	}
}

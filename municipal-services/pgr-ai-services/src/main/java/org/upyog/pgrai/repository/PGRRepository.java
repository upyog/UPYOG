package org.upyog.pgrai.repository;

import lombok.extern.slf4j.Slf4j;
import org.upyog.pgrai.repository.rowmapper.PGRQueryBuilder;
import org.upyog.pgrai.repository.rowmapper.PGRRowMapper;
import org.upyog.pgrai.util.PGRUtils;
import org.upyog.pgrai.util.PGRConstants;
import org.upyog.pgrai.web.models.ServiceWrapper;
import org.upyog.pgrai.web.models.RequestSearchCriteria;
import org.upyog.pgrai.web.models.Service;
import org.upyog.pgrai.web.models.Workflow;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Repository class for handling database operations related to PGR services.
 * Provides methods to search, count, and fetch dynamic data for services.
 */
@Repository
@Slf4j
public class PGRRepository {

    private PGRQueryBuilder queryBuilder;
    private PGRRowMapper rowMapper;
    private JdbcTemplate jdbcTemplate;
    private PGRUtils utils;

    /**
     * Constructor for `PGRRepository`.
     *
     * @param queryBuilder The query builder for constructing SQL queries.
     * @param rowMapper    The row mapper for mapping database rows to objects.
     * @param jdbcTemplate The JDBC template for executing database queries.
     * @param utils        Utility class for common operations.
     */
    @Autowired
    public PGRRepository(PGRQueryBuilder queryBuilder, PGRRowMapper rowMapper, JdbcTemplate jdbcTemplate, PGRUtils utils) {
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.jdbcTemplate = jdbcTemplate;
        this.utils = utils;
    }

    /**
     * Searches services based on search criteria and wraps them into `ServiceWrapper` objects.
     *
     * @param criteria The search criteria for filtering services.
     * @return A list of `ServiceWrapper` objects containing services and their workflows.
     */
    public List<ServiceWrapper> getServiceWrappers(RequestSearchCriteria criteria) {
        List<Service> services = getServices(criteria);
        List<String> serviceRequestIds = services.stream().map(Service::getServiceRequestId).collect(Collectors.toList());
        Map<String, Workflow> idToWorkflowMap = new HashMap<>();
        List<ServiceWrapper> serviceWrappers = new ArrayList<>();

        for (Service service : services) {
            ServiceWrapper serviceWrapper = ServiceWrapper.builder()
                    .service(service)
                    .workflow(idToWorkflowMap.get(service.getServiceRequestId()))
                    .build();
            serviceWrappers.add(serviceWrapper);
        }
        return serviceWrappers;
    }

    /**
     * Searches services based on search criteria.
     *
     * @param criteria The search criteria for filtering services.
     * @return A list of `Service` objects matching the search criteria.
     */
    public List<Service> getServices(RequestSearchCriteria criteria) {
        String tenantId = criteria.getTenantId();
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getPGRSearchQuery(criteria, preparedStmtList);
        try {
            query = utils.replaceSchemaPlaceholder(query, tenantId);
        } catch (Exception e) {
            throw new CustomException("PGR_UPDATE_ERROR",
                    "TenantId length is not sufficient to replace query schema in a multi-state instance");
        }
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    /**
     * Returns the count of services based on the search criteria.
     *
     * @param criteria The search criteria for filtering services.
     * @return The count of services matching the search criteria.
     */
    public Integer getCount(RequestSearchCriteria criteria) {
        String tenantId = criteria.getTenantId();
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getCountQuery(criteria, preparedStmtList);
        try {
            query = utils.replaceSchemaPlaceholder(query, tenantId);
        } catch (Exception e) {
            throw new CustomException("PGR_REQUEST_COUNT_ERROR",
                    "TenantId length is not sufficient to replace query schema in a multi-state instance");
        }
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    /**
     * Fetches dynamic data such as the count of resolved complaints and average resolution time.
     *
     * @param tenantId The tenant ID for filtering data.
     * @return A map containing dynamic data with keys for resolved complaints and average resolution time.
     */
    public Map<String, Integer> fetchDynamicData(String tenantId) {
        List<Object> preparedStmtListComplaintsResolved = new ArrayList<>();
        String query = queryBuilder.getResolvedComplaints(tenantId, preparedStmtListComplaintsResolved);
        try {
            query = utils.replaceSchemaPlaceholder(query, tenantId);
        } catch (Exception e) {
            throw new CustomException("PGR_SEARCH_ERROR",
                    "TenantId length is not sufficient to replace query schema in a multi-state instance");
        }
        int complaintsResolved = jdbcTemplate.queryForObject(query, preparedStmtListComplaintsResolved.toArray(), Integer.class);

        List<Object> preparedStmtListAverageResolutionTime = new ArrayList<>();
        query = queryBuilder.getAverageResolutionTime(tenantId, preparedStmtListAverageResolutionTime);
        try {
            query = utils.replaceSchemaPlaceholder(query, tenantId);
        } catch (Exception e) {
            throw new CustomException("PGR_SEARCH_ERROR",
                    "TenantId length is not sufficient to replace query schema in a multi-state instance");
        }
        int averageResolutionTime = 0;
        if (complaintsResolved > 0) {
            averageResolutionTime = jdbcTemplate.queryForObject(query, preparedStmtListAverageResolutionTime.toArray(), Integer.class);
        }
        Map<String, Integer> dynamicData = new HashMap<>();
        dynamicData.put(PGRConstants.COMPLAINTS_RESOLVED, complaintsResolved);
        dynamicData.put(PGRConstants.AVERAGE_RESOLUTION_TIME, averageResolutionTime);

        return dynamicData;
    }
}
package org.egov.wf.repository;


import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.wf.repository.querybuilder.WorkflowQueryBuilder;
import org.egov.wf.repository.rowmapper.WorkflowRowMapper;
import org.egov.wf.util.WorkflowUtil;
import org.egov.wf.web.models.ProcessInstance;
import org.egov.wf.web.models.ProcessInstanceSearchCriteria;
import org.egov.wf.web.models.DashboardProcessInstance;
import org.egov.wf.web.models.DashboardProcessInstanceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Repository
@Slf4j
public class WorKflowRepository {

    private WorkflowQueryBuilder queryBuilder;

    private JdbcTemplate jdbcTemplate;

    private WorkflowRowMapper rowMapper;


    @Autowired
    public WorKflowRepository(WorkflowQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate, WorkflowRowMapper rowMapper) {
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    /**
     * Executes the search criteria on the db
     * @param criteria The object containing the params to search on
     * @return The parsed response from the search query
     */
    public List<ProcessInstance> getProcessInstances(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        List<String> ids = getProcessInstanceIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);

        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }



    /**
     *
     * @param criteria
     * @return
     */
    public List<ProcessInstance> getProcessInstancesForUserInbox(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getStatus()) && CollectionUtils.isEmpty(criteria.getTenantSpecifiStatus()))
            return new LinkedList<>();

        List<String> ids = getInboxSearchIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    public Integer getProcessInstancesForUserInboxCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        criteria.setIsAssignedToMeCount(true);
        String query = queryBuilder.getInboxIdCount(criteria, (ArrayList<Object>) preparedStmtList);
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }

    /**
     * Returns the count based on the search criteria
     * @param criteria
     * @return
     */
    public Integer getInboxCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getInboxCount(criteria, preparedStmtList,Boolean.FALSE);
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }

    public Integer getProcessInstancesCount(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceCount(criteria, preparedStmtList,Boolean.FALSE);
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    /**
     * Returns the count based on the search criteria
     * @param criteria
     * @return
     */
    public List getInboxStatusCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getInboxCount(criteria, preparedStmtList,Boolean.TRUE);
        log.info(query);
        return jdbcTemplate.queryForList(query, preparedStmtList.toArray());
    }

    public List getProcessInstancesStatusCount(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceCount(criteria, preparedStmtList,Boolean.TRUE);
        return  jdbcTemplate.queryForList(query, preparedStmtList.toArray());
    }



    private List<String> getInboxSearchIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        criteria.setIsAssignedToMeCount(false);
        String query = queryBuilder.getInboxIdQuery(criteria,preparedStmtList,true);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }

    private List<String> getProcessInstanceIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceIds(criteria,preparedStmtList);
        log.info(query);
        log.info(preparedStmtList.toString());
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }


    public List<String> fetchEscalatedApplicationsBusinessIdsFromDb(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria) {
        ArrayList<Object> preparedStmtList = new ArrayList<>();

        // 1st step is to fetch businessIds based on the assignee and the module.
        /*

        String query = queryBuilder.getInboxApplicationsBusinessIdsQuery(criteria, preparedStmtList);
        List<String> inboxApplicationsBusinessIds = jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
        log.info(inboxApplicationsBusinessIds.toString());
        preparedStmtList.clear();

        // (DONE) 2nd step is to fetch businessIds of inbox applications which have been autoEscalated at least once in their wf
        // (DONE) For this step, fetch AUTO_ESCALATION_EMPLOYEES uuids based on role codes by doing a call to user service
        // (PENDING) Also, add the call to mdms service for filtering out states which need to be excluded

        criteria.setBusinessIds(inboxApplicationsBusinessIds);
         */
        String query = queryBuilder.getAutoEscalatedApplicationsFinalQuery(requestInfo,criteria, preparedStmtList);
        log.info(query);
        List<String> escalatedApplicationsBusinessIds = jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
        preparedStmtList.clear();
        log.info(escalatedApplicationsBusinessIds.toString());
        // 3rd step is to do a simple search on these business ids(DONE IN WORKFLOW SERVICE)

        return escalatedApplicationsBusinessIds;
    }

    public Integer getEscalatedApplicationsCount(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getEscalatedApplicationsCount(requestInfo,criteria, (ArrayList<Object>) preparedStmtList);
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }

    /**
     * Retrieves the count of process instances matching dashboard criteria.
     *
     * @param criteria search criteria filter containing tenantId, statuses, etc.
     * @return count of matching process instances
     */
    public Integer getDashboardApplicationCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getDashboardProcessInstanceCount(criteria, preparedStmtList);
        log.info("Query : "+query+" , preparedStatementList : "+preparedStmtList );
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
     }

    /**
     * Retrieves dashboard process instances matching the search criteria.
     *
     * @param criteria search criteria filter containing tenantId, statuses, etc.
     * @return List of matching ProcessInstance records
     */
    public List<ProcessInstance> getDashboardApplications(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        List<String> ids = getDashboardProcessInstanceIds(criteria);
        if (CollectionUtils.isEmpty(ids)) {
            return new LinkedList<>();
        }
        String query = queryBuilder.getDashboardProcessInstanceSearchQueryById(ids, preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    /**
     * Helper method to fetch list of process instance IDs matching the dashboard search criteria.
     *
     * @param criteria search criteria filter
     * @return List of matching String IDs
     */
    private List<String> getDashboardProcessInstanceIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getDashboardProcessInstanceIds(criteria, preparedStmtList, true);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }

    /**
     * Efficiently fetches both dashboard process instances and total count using 1 or 2 DB round-trips.
     */
    public DashboardProcessInstanceResponse getDashboardApplicationsWithCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtListForIds = new ArrayList<>();
        // Fetch all matching IDs without pagination to get exact total count
        String idsQuery = queryBuilder.getDashboardProcessInstanceIds(criteria, preparedStmtListForIds, false);
        List<String> allIds = jdbcTemplate.query(idsQuery, preparedStmtListForIds.toArray(), new SingleColumnRowMapper<>(String.class));

        if (CollectionUtils.isEmpty(allIds)) {
            return DashboardProcessInstanceResponse.builder()
                    .processInstances(new LinkedList<>())
                    .totalCount(0)
                    .build();
        }

        int totalCount = allIds.size();

        // Perform Java pagination (offset and limit)
        int offset = criteria.getOffset() != null ? criteria.getOffset() : 0;
        int limit = criteria.getLimit() != null ? criteria.getLimit() : 10;

        if (offset >= totalCount) {
            return DashboardProcessInstanceResponse.builder()
                    .processInstances(new LinkedList<>())
                    .totalCount(totalCount)
                    .build();
        }

        int toIndex = Math.min(offset + limit, totalCount);
        List<String> slicedIds = allIds.subList(offset, toIndex);

        List<Object> preparedStmtListForDetails = new ArrayList<>();
        String detailsQuery = queryBuilder.getDashboardProcessInstanceSearchQueryById(slicedIds, preparedStmtListForDetails);
        List<ProcessInstance> processInstances = jdbcTemplate.query(detailsQuery, preparedStmtListForDetails.toArray(), rowMapper);

        List<DashboardProcessInstance> dashboardInstances = new ArrayList<>();
        for (ProcessInstance pi : processInstances) {
            dashboardInstances.add(DashboardProcessInstance.builder()
                    .id(pi.getId())
                    .tenantId(pi.getTenantId())
                    .businessService(pi.getBusinessService())
                    .businessId(pi.getBusinessId())
                    .action(pi.getAction())
                    .moduleName(pi.getModuleName())
                    .build());
        }

        return DashboardProcessInstanceResponse.builder()
                .processInstances(dashboardInstances)
                .totalCount(totalCount)
                .build();
    }
}
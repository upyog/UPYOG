package org.egov.wf.repository;


import org.egov.wf.repository.querybuilder.EscalationQueryBuilder;
import org.egov.wf.web.models.EscalationSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component
@Slf4j
public class EscalationRepository {



    private JdbcTemplate jdbcTemplate;

    private EscalationQueryBuilder queryBuilder;

    @Autowired
    public EscalationRepository(JdbcTemplate jdbcTemplate, EscalationQueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
    }


    /**
     * Fetches uuids that haas to be escalated
     * @param criteria
     * @return
     */
    public List<String> getBusinessIds(EscalationSearchCriteria criteria){

        List<Object> preparedStmtList = new ArrayList<>();
        String query;
        //String query = queryBuilder.getEscalationQuery(criteria, preparedStmtList);
        if(criteria.getBusinessService().equalsIgnoreCase("BPA"))
        	query=queryBuilder.getEscalationQueryForBPA(criteria, preparedStmtList);
        else {
        	query = queryBuilder.getEscalationQuery(criteria, preparedStmtList);
        }
        log.info("query is"+query);
        log.info("params are " +preparedStmtList.toString());
        List<String> businessIds = jdbcTemplate.query(query, preparedStmtList.toArray(),  new SingleColumnRowMapper<>(String.class));
        return  businessIds;

    }
    
    public List<String> getBusinessFilteredIds(EscalationSearchCriteria criteria,List<String> businessIds){

        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getEscalationQueryFiltered(criteria,businessIds, preparedStmtList);
        List<String> businessIdsFiltered = jdbcTemplate.query(query, preparedStmtList.toArray(),  new SingleColumnRowMapper<>(String.class));
        return  businessIdsFiltered;

    }
    
    public List<String> getBusinessSMSIds(EscalationSearchCriteria criteria,List<String> businessIds){

        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getSMSQueryFiltered(criteria,businessIds, preparedStmtList);
        List<String> businessIdsFiltered = jdbcTemplate.query(query, preparedStmtList.toArray(),  new SingleColumnRowMapper<>(String.class));
        return  businessIdsFiltered;

    }


}

package org.egov.pt.repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.pt.models.PetApplicationSearchCriteria;
import org.egov.pt.models.PetRegistrationApplication;
import org.egov.pt.repository.builder.PetApplicationQueryBuilder;
import org.egov.pt.repository.rowmapper.PetApplicationRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Repository
public class PetRegistrationRepository {

    @Autowired
    private PetApplicationQueryBuilder queryBuilder;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PetApplicationRowMapper rowMapper;

    public List<PetRegistrationApplication>getApplications(PetApplicationSearchCriteria searchCriteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getPetApplicationSearchQuery(searchCriteria, preparedStmtList);
        log.info("Final query: " + query);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }
}
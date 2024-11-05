package org.egov.nationaldashboardingest.service;

import org.egov.nationaldashboardingest.repository.querybuilder.NSSStateListQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class StateListDB {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NSSStateListQuery nssStateListQuery;

    public Set<String> findifrecordexists(){
        String sql = nssStateListQuery.getYesterdayDataQuery();

        List<String> stateList = jdbcTemplate.query( sql , (rs,rowNum) -> rs.getString("state"));

        return  new HashSet<>(stateList);
    };
}

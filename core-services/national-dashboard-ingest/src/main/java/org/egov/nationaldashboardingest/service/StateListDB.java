package org.egov.nationaldashboardingest.service;

import org.egov.nationaldashboardingest.repository.querybuilder.NSSStateListQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class StateListDB {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NSSStateListQuery nssStateListQuery;

    public Map<String, Map<String, List<String>>> findIfRecordExists() {
        String sql = nssStateListQuery.getYesterdayDataQuery();
        Map<String, Map<String, List<String>>> resultMap = new HashMap<>();


        jdbcTemplate.query(sql, (rs, rowNum) -> {
            String state = rs.getString("state");
            String module = rs.getString("module");
            String ulb = rs.getString("ulb");


            if (!resultMap.containsKey(state)) {
                resultMap.put(state, new HashMap<>());
            }


            if (!resultMap.get(state).containsKey(ulb)) {
                resultMap.get(state).put(ulb, new ArrayList<>());
            }


            resultMap.get(state).get(ulb).add(module);

            return null;
        });


        return resultMap;
    }

}
package org.egov.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.web.models.ScheduleOfRateApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class SORApplicationRowMapper implements ResultSetExtractor<List<ScheduleOfRateApplication>> {
    public List<ScheduleOfRateApplication> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer,ScheduleOfRateApplication> sorApplicationMap = new LinkedHashMap<>();

        while (rs.next()){
            int sorId = rs.getInt("sSorId");
            ScheduleOfRateApplication sorApplication = sorApplicationMap.get(sorId);

            if(sorApplication == null) {

                Date lastModifiedTime = rs.getDate("sstartDate");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                sorApplication = ScheduleOfRateApplication.builder()
                        .sorId(rs.getInt("sSorId"))
                        .sorName(rs.getString("ssorName"))
                        .startDate(rs.getString("sstartDate"))
                        .endDate(rs.getString("sendDate"))
                        .chapter(rs.getString("schapter"))
                        .itemNo(rs.getString("sitemNo"))
                        .descOfItem(rs.getString("sdescOfItem"))
                        .unit(rs.getInt("sunit"))
                        .rate(rs.getLong("srate"))
                        .build();
            }
            //addChildrenToProperty(rs, sorApplication);
            sorApplicationMap.put(sorId, sorApplication);
        }
        return new ArrayList<>(sorApplicationMap.values());
    }

}

package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.Section;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class SectionRowMapper implements ResultSetExtractor<List<Section>> {

    public List<Section> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Section> sectionMap = new LinkedHashMap<>();

        while (rs.next()) {
            String uuid = rs.getString("uuid");
            Section section = sectionMap.get(uuid);

            if (section == null) {
                section = Section.builder()
                        .uuid(rs.getString("uuid"))
                        .weightage(rs.getBigDecimal("weightage"))
                        .title(rs.getString("title"))
                        .sectionOrder(rs.getInt("sectionorder"))
                        .build();
            }
            sectionMap.put(uuid, section);
        }
        return new ArrayList<>(sectionMap.values());
    }
}

package org.egov.egovsurveyservices.repository.rowmapper;

import org.egov.egovsurveyservices.web.models.AuditDetails;
import org.egov.egovsurveyservices.web.models.Category;
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
public class CategoryRowMapper implements ResultSetExtractor<List<Category>> {
    public List<Category> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, Category> categoryMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            Category category = categoryMap.get(id);

            if (category == null) {
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                        .build();

                category = Category.builder()
                        .id(rs.getString("id"))
                        .label(rs.getString("label"))
                        .isActive(rs.getBoolean("isactive"))
                        .tenantId(rs.getString("tenantid"))
                        .auditDetails(auditdetails)
                        .build();
            }
            categoryMap.put(id, category);
        }
        return new ArrayList<>(categoryMap.values());
    }

}

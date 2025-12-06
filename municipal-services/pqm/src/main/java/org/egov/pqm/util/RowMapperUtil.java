package org.egov.pqm.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.egov.pqm.web.model.AuditDetails;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

@UtilityClass
public class RowMapperUtil {
    public static JsonNode getAdditionalDetail(String columnName, ResultSet rs, ObjectMapper mapper) {

        JsonNode additionalDetail = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if (pgObj != null) {
                additionalDetail = mapper.readTree(pgObj.getValue());
            }
        } catch (IOException | SQLException e) {
            throw new CustomException("PARSING_ERROR", "Failed to parse additionalDetail object");
        }
        return additionalDetail;
    }

    public static AuditDetails getAuditDetails(ResultSet rs) throws SQLException {
        return AuditDetails.builder()
                .createdBy(rs.getString("createdby"))
                .createdTime(rs.getLong("createdtime"))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                .build();
    }
}

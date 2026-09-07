package org.egov.infra.mdms.repository.rowmapper;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import org.postgresql.util.PGobject;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MdmsDataRowMapper implements RowMapper<Map<String, Object>> {

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        Map<String, Object> map = new HashMap<>();
        map.put("tenantid", rs.getString("tenantid"));
        map.put("schemacode", rs.getString("schemacode"));
        map.put("id", rs.getString("id"));
        map.put("uniqueidentifier", rs.getString("uniqueidentifier"));
        try {
            map.put("isactive", rs.getObject("isactive") != null ? rs.getBoolean("isactive") : Boolean.TRUE);
        } catch (Exception e) {
            map.put("isactive", Boolean.TRUE);
        }
        
        Object dataObj = rs.getObject("data");
        try {
            if (dataObj instanceof PGobject) {
                map.put("data", objectMapper.readValue(((PGobject) dataObj).getValue(), Object.class));
            } else if (dataObj instanceof String) {
                map.put("data", objectMapper.readValue((String) dataObj, Object.class));
            } else {
                map.put("data", objectMapper.convertValue(dataObj, Object.class));
            }
        } catch (Exception e) {
            log.error("Error parsing JSON data in row mapper", e);
            map.put("data", null);
        }
        
        return map;
    }
}

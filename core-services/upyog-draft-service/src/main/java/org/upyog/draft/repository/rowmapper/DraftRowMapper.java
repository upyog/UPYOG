package org.upyog.draft.repository.rowmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.upyog.draft.web.models.DraftDetail;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@Component
public class DraftRowMapper implements RowMapper<DraftDetail> {

    private final ObjectMapper objectMapper;

    public DraftRowMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public DraftDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
        Object draftData = null;
        try {
            String json = rs.getString("draft_data");
            if (json != null) {
                draftData = objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
            }
        } catch (Exception ignored) {
            draftData = rs.getString("draft_data");
        }

        return DraftDetail.builder()
                .draftId(rs.getString("draft_id"))
                .tenantId(rs.getString("tenant_id"))
                .businessService(rs.getString("business_service"))
                .moduleName(rs.getString("module_name"))
                .moduleEntityId(rs.getString("module_entity_id"))
                .creatorType(rs.getString("creator_type"))
                .draftData(draftData)
                .completionPct(rs.getBigDecimal("completion_pct"))
                .status(rs.getString("status"))
                .createdBy(rs.getString("createdby"))
                .lastModifiedBy(rs.getString("lastmodifiedby"))
                .createdTime(rs.getLong("createdtime"))
                .lastModifiedTime(rs.getLong("lastmodifiedtime"))
                .build();
    }
}

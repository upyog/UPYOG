package org.upyog.draft.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.draft.config.DraftConfiguration;
import org.upyog.draft.producer.Producer;
import org.upyog.draft.repository.query.DraftQueryBuilder;
import org.upyog.draft.repository.rowmapper.DraftRowMapper;
import org.upyog.draft.util.DraftConstants;
import org.upyog.draft.web.models.DraftDetail;
import org.upyog.draft.web.models.DraftRequest;
import org.upyog.draft.web.models.DraftSearchCriteria;

import java.util.List;

@Repository
@Slf4j
public class DraftRepository {

    private final JdbcTemplate jdbcTemplate;
    private final DraftQueryBuilder queryBuilder;
    private final DraftRowMapper rowMapper;
    private final Producer producer;
    private final DraftConfiguration configuration;

    public DraftRepository(JdbcTemplate jdbcTemplate, DraftQueryBuilder queryBuilder,
            DraftRowMapper rowMapper, Producer producer,
            DraftConfiguration configuration) {
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
        this.rowMapper = rowMapper;
        this.producer = producer;
        this.configuration = configuration;
    }

    public void save(DraftRequest request, boolean isCreate) {
        String topic = isCreate
                ? configuration.getSaveDraftTopic()
                : configuration.getUpdateDraftTopic();
        producer.push(topic, request);
    }

    public void updateStatus(DraftRequest request) {
        producer.push(configuration.getUpdateDraftStatusTopic(), request);
    }

    public void delete(DraftRequest request) {
        producer.push(configuration.getDeleteDraftTopic(), request);
    }

    public List<DraftDetail> search(DraftSearchCriteria criteria) {
        List<Object> params = new java.util.ArrayList<>();
        String query = queryBuilder.getSearchQuery(criteria, params);
        return jdbcTemplate.query(query, rowMapper, params.toArray());
    }

    public DraftDetail findByDraftId(String draftId, String tenantId, String createdBy) {
        List<DraftDetail> results = jdbcTemplate.query(
                "SELECT draft_id, tenant_id, business_service, module_name, module_entity_id, creator_type, "
                + "draft_data, completion_pct, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime "
                + "FROM ug_draft_detail WHERE draft_id = ? AND tenant_id = ? AND createdby = ?",
                rowMapper, draftId, tenantId, createdBy);
        return results.isEmpty() ? null : results.get(0);
    }

    public int count(DraftSearchCriteria criteria) {
        List<Object> params = new java.util.ArrayList<>();
        String query = queryBuilder.getCountQuery(criteria, params);
        Integer count = jdbcTemplate.queryForObject(query, Integer.class, params.toArray());
        return count != null ? count : 0;
    }

    public List<DraftDetail> findActiveDraftsOlderThan(long cutoffTime) {
        return jdbcTemplate.query(
                "SELECT draft_id, tenant_id, business_service, module_name, module_entity_id, creator_type, draft_data, "
                + "completion_pct, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime "
                + "FROM ug_draft_detail WHERE status = ? AND lastmodifiedtime < ?",
                rowMapper, DraftConstants.STATUS_ACTIVE, cutoffTime);
    }

    public List<DraftDetail> findSubmittedOrDiscardedOlderThan(String status, long cutoffTime) {
        return jdbcTemplate.query(
                "SELECT draft_id, tenant_id, business_service, module_name, module_entity_id, creator_type, draft_data, "
                + "completion_pct, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime "
                + "FROM ug_draft_detail WHERE status = ? AND lastmodifiedtime < ?",
                rowMapper, status, cutoffTime);
    }

    public List<DraftDetail> findActiveDraftsWithModuleEntity() {
        return jdbcTemplate.query(
                "SELECT draft_id, tenant_id, business_service, module_name, module_entity_id, creator_type, draft_data, "
                + "completion_pct, status, createdby, lastmodifiedby, createdtime, lastmodifiedtime "
                + "FROM ug_draft_detail WHERE status = ? AND module_entity_id IS NOT NULL",
                rowMapper, DraftConstants.STATUS_ACTIVE);
    }
}

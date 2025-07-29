package org.egov.enc.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.enc.models.AsymmetricKey;
import org.egov.enc.models.AuditData;
import org.egov.enc.models.SymmetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.egov.enc.utils.DateTimeUtil;

/**
 * Repository for inserting audit records into encryption audit tables.
 * Handles audit logging for both symmetric and asymmetric key operations (insert/update).
 * Keeps audit logic separate from main repository for maintainability and clarity.
 */
@Repository
@Slf4j
public class AuditRepository {
    private static final String insertSymmetricKeyAuditQuery = "INSERT INTO eg_enc_symmetric_keys_audit (operation, tenant_id, changed_at, old_row, new_row) VALUES (?, ?, ?, ?::jsonb, ?::jsonb)";
    private static final String insertAsymmetricKeyAuditQuery = "INSERT INTO eg_enc_asymmetric_keys_audit (operation, tenant_id, changed_at, old_row, new_row) VALUES (?, ?, ?, ?::jsonb, ?::jsonb)";
    private static final String selectActiveSymmetricKeysByTenantQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE active='true' AND tenant_id=?";
    private static final String selectActiveAsymmetricKeysByTenantQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE active='true' AND tenant_id=?";
    private static final String selectActiveSymmetricKeysQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE active='true'";
    private static final String selectActiveAsymmetricKeysQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE active='true'";
    private static final String selectSymmetricKeyByIdQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE key_id=?";
    private static final String selectAsymmetricKeyByIdQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE key_id=?";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * Batch inserts audit records for symmetric key operations.
     *
     * @param auditData List of audit data objects containing operation, tenantId, oldRow, and newRow
     * @param timestamp The timestamp to use for all audit records (should match main table timestamp)
     */
    public void insertSymmetricKeyAuditBatch(List<AuditData> auditData, long timestamp) {
        if (auditData == null || auditData.isEmpty()) {
            return;
        }

        try {
            jdbcTemplate.batchUpdate(insertSymmetricKeyAuditQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AuditData data = auditData.get(i);
                    ps.setString(1, data.getOperation());
                    ps.setString(2, data.getTenantId());
                    ps.setLong(3, timestamp);
                    ps.setString(4, data.getOldRowJson());
                    ps.setString(5, data.getNewRowJson());
                }

                @Override
                public int getBatchSize() {
                    return auditData.size();
                }
            });
        } catch (Exception e) {
            log.error("Failed to batch insert symmetric key audit records", e);
            throw new RuntimeException("Symmetric key batch audit insertion failed", e);
        }
    }

    /**
     * Batch inserts audit records for asymmetric key operations.
     *
     * @param auditData List of audit data objects containing operation, tenantId, oldRow, and newRow
     * @param timestamp The timestamp to use for all audit records (should match main table timestamp)
     */
    public void insertAsymmetricKeyAuditBatch(List<AuditData> auditData, long timestamp) {
        if (auditData == null || auditData.isEmpty()) {
            log.warn("No audit data provided for asymmetric key batch insertion");
            return;
        }

        try {
            jdbcTemplate.batchUpdate(insertAsymmetricKeyAuditQuery, new BatchPreparedStatementSetter() {
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    AuditData data = auditData.get(i);
                    ps.setString(1, data.getOperation());
                    ps.setString(2, data.getTenantId());
                    ps.setLong(3, timestamp);
                    ps.setString(4, data.getOldRowJson());
                    ps.setString(5, data.getNewRowJson());
                }

                @Override
                public int getBatchSize() {
                    return auditData.size();
                }
            });
        } catch (Exception e) {
            log.error("Failed to batch insert asymmetric key audit records", e);
            throw new RuntimeException("Asymmetric key batch audit insertion failed", e);
        }
    }

    /**
     * Handles the full audit process for symmetric key deactivation (update):
     * - Fetches the single active symmetric key for the tenant
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches the updated key
     * - Inserts audit record for the key using batch operation
     *
     * @param tenantId        The tenant ID
     * @param updateOperation The update operation to perform (should return the update count)
     * @param timestamp       The timestamp to use for audit records (should match main table timestamp)
     * @return The update count
     */
    public int auditDeactivateSymmetricKeyForTenant(String tenantId, Supplier<Integer> updateOperation, long timestamp) {
        try {
        List<SymmetricKey> oldKeys = jdbcTemplate.query(selectActiveSymmetricKeysByTenantQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), tenantId);
            if (oldKeys.isEmpty()) {
                log.warn("No active symmetric key found for tenant: {}", tenantId);
                return 0;
            }

            SymmetricKey oldKey = oldKeys.get(0);
        int result = updateOperation.get();
        if (result > 0) {
                log.info("Symmetric key deactivation audit for tenant: {}, keyId: {}", tenantId, oldKey.getKeyId());
                try {
                SymmetricKey newKey = jdbcTemplate.queryForObject(selectSymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), oldKey.getKeyId());
                    String oldRowJson = objectMapper.writeValueAsString(oldKey);
                    String newRowJson = objectMapper.writeValueAsString(newKey);
                    
                    List<AuditData> auditDataList = new ArrayList<>();
                    auditDataList.add(AuditData.builder()
                            .operation("UPDATE")
                            .tenantId(oldKey.getTenantId())
                            .oldRowJson(oldRowJson)
                            .newRowJson(newRowJson)
                            .build());
                    
                    insertSymmetricKeyAuditBatch(auditDataList, timestamp);
                } catch (Exception e) {
                    log.error("Failed to create audit data for symmetric key - keyId: {}, tenantId: {}", oldKey.getKeyId(), oldKey.getTenantId(), e);
                    throw new RuntimeException("Symmetric key audit data creation failed", e);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to perform symmetric key deactivation audit for tenant: {}", tenantId, e);
            throw new RuntimeException("Symmetric key deactivation audit failed for tenant: " + tenantId, e);
        }
    }

    /**
     * Handles the full audit process for asymmetric key deactivation (update):
     * - Fetches the single active asymmetric key for the tenant
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches the updated key
     * - Inserts audit record for the key using batch operation
     *
     * @param tenantId        The tenant ID
     * @param updateOperation The update operation to perform (should return the update count)
     * @param timestamp       The timestamp to use for audit records (should match main table timestamp)
     * @return The update count
     */
    public int auditDeactivateAsymmetricKeyForTenant(String tenantId, Supplier<Integer> updateOperation, long timestamp) {
        try {
        List<AsymmetricKey> oldKeys = jdbcTemplate.query(selectActiveAsymmetricKeysByTenantQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), tenantId);
            if (oldKeys.isEmpty()) {
                log.warn("No active asymmetric key found for tenant: {}", tenantId);
                return 0;
            }

            AsymmetricKey oldKey = oldKeys.get(0);
        int result = updateOperation.get();
        if (result > 0) {
                log.info("Asymmetric key deactivation audit for tenant: {}, keyId: {}", tenantId, oldKey.getKeyId());
                try {
                AsymmetricKey newKey = jdbcTemplate.queryForObject(selectAsymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), oldKey.getKeyId());
                    String oldRowJson = objectMapper.writeValueAsString(oldKey);
                    String newRowJson = objectMapper.writeValueAsString(newKey);
                    
                    List<AuditData> auditDataList = new ArrayList<>();
                    auditDataList.add(AuditData.builder()
                            .operation("UPDATE")
                            .tenantId(oldKey.getTenantId())
                            .oldRowJson(oldRowJson)
                            .newRowJson(newRowJson)
                            .build());
                    
                    insertAsymmetricKeyAuditBatch(auditDataList, timestamp);
                } catch (Exception e) {
                    log.error("Failed to create audit data for asymmetric key - keyId: {}, tenantId: {}", oldKey.getKeyId(), oldKey.getTenantId(), e);
                    throw new RuntimeException("Asymmetric key audit data creation failed", e);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to perform asymmetric key deactivation audit for tenant: {}", tenantId, e);
            throw new RuntimeException("Asymmetric key deactivation audit failed for tenant: " + tenantId, e);
        }
    }

    /**
     * Handles the full audit process for deactivating all symmetric keys (update):
     * - Fetches all old active keys
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key using batch operation
     *
     * @param updateOperation The update operation to perform (should return the update count)
     * @param timestamp       The timestamp to use for audit records (should match main table timestamp)
     * @return The update count
     */
    public int auditDeactivateAllSymmetricKeys(Supplier<Integer> updateOperation, long timestamp) {
        try {
        List<SymmetricKey> oldKeys = jdbcTemplate.query(selectActiveSymmetricKeysQuery, new BeanPropertyRowMapper<>(SymmetricKey.class));
            if (oldKeys.isEmpty()) {
                log.warn("No active symmetric keys found for deactivation");
                return 0;
            }
            
        int result = updateOperation.get();
        if (result > 0) {
                log.info("Deactivating Symmetric keys is successful");
                List<AuditData> auditDataList = new ArrayList<>();
            for (SymmetricKey oldKey : oldKeys) {
                    try {
                SymmetricKey newKey = jdbcTemplate.queryForObject(selectSymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), oldKey.getKeyId());
                        String oldRowJson = objectMapper.writeValueAsString(oldKey);
                        String newRowJson = objectMapper.writeValueAsString(newKey);
                        auditDataList.add(AuditData.builder()
                                .operation("UPDATE")
                                .tenantId(oldKey.getTenantId())
                                .oldRowJson(oldRowJson)
                                .newRowJson(newRowJson)
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to create audit data for symmetric key - keyId: {}, tenantId: {}", oldKey.getKeyId(), oldKey.getTenantId(), e);
                        throw new RuntimeException("Symmetric key audit data creation failed", e);
                    }
                }
                log.info("Size of AuditDataList: {}", auditDataList.size());
                insertSymmetricKeyAuditBatch(auditDataList, timestamp);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to perform bulk symmetric key deactivation audit", e);
            throw new RuntimeException("Bulk symmetric key deactivation audit failed", e);
        }
    }

    /**
     * Handles the full audit process for deactivating all asymmetric keys (update):
     * - Fetches all old active keys
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key using batch operation
     *
     * @param updateOperation The update operation to perform (should return the update count)
     * @param timestamp       The timestamp to use for audit records (should match main table timestamp)
     * @return The update count
     */
    public int auditDeactivateAllAsymmetricKeys(Supplier<Integer> updateOperation, long timestamp) {
        try {
        List<AsymmetricKey> oldKeys = jdbcTemplate.query(selectActiveAsymmetricKeysQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class));
            if (oldKeys.isEmpty()) {
                log.warn("No active asymmetric keys found for deactivation");
                return 0;
            }
            
        int result = updateOperation.get();
        if (result > 0) {
                log.info("Deactivating Asymmetric keys is successful");
                List<AuditData> auditDataList = new ArrayList<>();
            for (AsymmetricKey oldKey : oldKeys) {
                    try {
                AsymmetricKey newKey = jdbcTemplate.queryForObject(selectAsymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), oldKey.getKeyId());
                        String oldRowJson = objectMapper.writeValueAsString(oldKey);
                        String newRowJson = objectMapper.writeValueAsString(newKey);
                        auditDataList.add(AuditData.builder()
                                .operation("UPDATE")
                                .tenantId(oldKey.getTenantId())
                                .oldRowJson(oldRowJson)
                                .newRowJson(newRowJson)
                                .build());
                    } catch (Exception e) {
                        log.error("Failed to create audit data for asymmetric key - keyId: {}, tenantId: {}", oldKey.getKeyId(), oldKey.getTenantId(), e);
                        throw new RuntimeException("Asymmetric key audit data creation failed", e);
                    }
                }
                log.info("Size of AuditDataList: {}", auditDataList.size());
                insertAsymmetricKeyAuditBatch(auditDataList, timestamp);
            }
            return result;
        } catch (Exception e) {
            log.error("Failed to perform bulk asymmetric key deactivation audit", e);
            throw new RuntimeException("Bulk asymmetric key deactivation audit failed", e);
        }
    }


}

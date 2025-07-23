package org.egov.enc.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.enc.models.AsymmetricKey;
import org.egov.enc.models.SymmetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.function.Supplier;

/**
 * Repository for inserting audit records into encryption audit tables.
 * Handles audit logging for both symmetric and asymmetric key operations (insert/update).
 * Keeps audit logic separate from main repository for maintainability and clarity.
 */
@Repository
public class AuditRepository {
    private static final String insertSymmetricKeyAuditQuery = "INSERT INTO eg_enc_symmetric_keys_audit (operation, tenant_id, changed_at, old_row, new_row) VALUES (?, ?, ?, ?::jsonb, ?::jsonb)";
    private static final String insertAsymmetricKeyAuditQuery = "INSERT INTO eg_enc_asymmetric_keys_audit (operation, tenant_id, changed_at, old_row, new_row) VALUES (?, ?, ?, ?::jsonb, ?::jsonb)";
    private static final String selectActiveSymmetricKeysByTenantQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE active='true' AND tenant_id=?";
    private static final String selectActiveAsymmetricKeysByTenantQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE active='true' AND tenant_id=?";
    private static final String selectActiveSymmetricKeysQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE active='true'";
    private static final String selectActiveAsymmetricKeysQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE active='true'";
    private static final String selectSymmetricKeyByIdQuery = "SELECT * FROM eg_enc_symmetric_keys WHERE key_id=?";
    private static final String selectAsymmetricKeyByIdQuery = "SELECT * FROM eg_enc_asymmetric_keys WHERE key_id=?";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final JdbcTemplate jdbcTemplate;

    /**
     * Constructs the AuditRepository with the required JdbcTemplate.
     *
     * @param jdbcTemplate Spring JdbcTemplate for DB operations
     */
    @Autowired
    public AuditRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Inserts an audit record for a symmetric key operation.
     *
     * @param operation The operation type (e.g., "INSERT", "UPDATE")
     * @param tenantId  The tenant ID associated with the key
     * @param oldRow    The previous state of the key (null for insert)
     * @param newRow    The new state of the key (null for delete)
     * @param changedAt The timestamp of the operation
     */
    public void insertSymmetricKeyAudit(String operation, String tenantId, Object oldRow, Object newRow, Timestamp changedAt) {
        try {
            String oldRowJson = oldRow != null ? objectMapper.writeValueAsString(oldRow) : null;
            String newRowJson = newRow != null ? objectMapper.writeValueAsString(newRow) : null;
            jdbcTemplate.update(insertSymmetricKeyAuditQuery, operation, tenantId, changedAt, oldRowJson, newRowJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Inserts an audit record for an asymmetric key operation.
     *
     * @param operation The operation type (e.g., "INSERT", "UPDATE")
     * @param tenantId  The tenant ID associated with the key
     * @param oldRow    The previous state of the key (null for insert)
     * @param newRow    The new state of the key (null for delete)
     * @param changedAt The timestamp of the operation
     */
    public void insertAsymmetricKeyAudit(String operation, String tenantId, Object oldRow, Object newRow, Timestamp changedAt) {
        try {
            String oldRowJson = oldRow != null ? objectMapper.writeValueAsString(oldRow) : null;
            String newRowJson = newRow != null ? objectMapper.writeValueAsString(newRow) : null;
            jdbcTemplate.update(insertAsymmetricKeyAuditQuery, operation, tenantId, changedAt, oldRowJson, newRowJson);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the full audit process for symmetric key deactivation (update):
     * - Fetches old active keys for the tenant
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key
     *
     * @param tenantId        The tenant ID
     * @param now             The epoch millis timestamp
     * @param updateOperation The update operation to perform (should return the update count)
     * @return The update count
     */
    public int auditDeactivateSymmetricKeyForTenant(String tenantId, long now, Supplier<Integer> updateOperation) {
        List<SymmetricKey> oldKeys = jdbcTemplate.query(selectActiveSymmetricKeysByTenantQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), tenantId);
        int result = updateOperation.get();
        if (result > 0) {
            for (SymmetricKey oldKey : oldKeys) {
                SymmetricKey newKey = jdbcTemplate.queryForObject(selectSymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), oldKey.getKeyId());
                insertSymmetricKeyAudit("UPDATE", oldKey.getTenantId(), oldKey, newKey, new Timestamp(now));
            }
        }
        return result;
    }

    /**
     * Handles the full audit process for asymmetric key deactivation (update):
     * - Fetches old active keys for the tenant
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key
     *
     * @param tenantId        The tenant ID
     * @param now             The epoch millis timestamp
     * @param updateOperation The update operation to perform (should return the update count)
     * @return The update count
     */
    public int auditDeactivateAsymmetricKeyForTenant(String tenantId, long now, Supplier<Integer> updateOperation) {
        List<AsymmetricKey> oldKeys = jdbcTemplate.query(selectActiveAsymmetricKeysByTenantQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), tenantId);
        int result = updateOperation.get();
        if (result > 0) {
            for (AsymmetricKey oldKey : oldKeys) {
                AsymmetricKey newKey = jdbcTemplate.queryForObject(selectAsymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), oldKey.getKeyId());
                insertAsymmetricKeyAudit("UPDATE", oldKey.getTenantId(), oldKey, newKey, new Timestamp(now));
            }
        }
        return result;
    }

    /**
     * Handles the full audit process for deactivating all symmetric keys (update):
     * - Fetches all old active keys
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key
     *
     * @param now             The epoch millis timestamp
     * @param updateOperation The update operation to perform (should return the update count)
     * @return The update count
     */
    public int auditDeactivateAllSymmetricKeys(long now, Supplier<Integer> updateOperation) {
        List<SymmetricKey> oldKeys = jdbcTemplate.query(selectActiveSymmetricKeysQuery, new BeanPropertyRowMapper<>(SymmetricKey.class));
        int result = updateOperation.get();
        if (result > 0) {
            for (SymmetricKey oldKey : oldKeys) {
                SymmetricKey newKey = jdbcTemplate.queryForObject(selectSymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(SymmetricKey.class), oldKey.getKeyId());
                insertSymmetricKeyAudit("UPDATE", oldKey.getTenantId(), oldKey, newKey, new Timestamp(now));
            }
        }
        return result;
    }

    /**
     * Handles the full audit process for deactivating all asymmetric keys (update):
     * - Fetches all old active keys
     * - Executes the update operation (provided as a lambda/callback)
     * - Fetches new keys by keyId
     * - Inserts audit records for each affected key
     *
     * @param now             The epoch millis timestamp
     * @param updateOperation The update operation to perform (should return the update count)
     * @return The update count
     */
    public int auditDeactivateAllAsymmetricKeys(long now, Supplier<Integer> updateOperation) {
        List<AsymmetricKey> oldKeys = jdbcTemplate.query(selectActiveAsymmetricKeysQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class));
        int result = updateOperation.get();
        if (result > 0) {
            for (AsymmetricKey oldKey : oldKeys) {
                AsymmetricKey newKey = jdbcTemplate.queryForObject(selectAsymmetricKeyByIdQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class), oldKey.getKeyId());
                insertAsymmetricKeyAudit("UPDATE", oldKey.getTenantId(), oldKey, newKey, new Timestamp(now));
            }
        }
        return result;
    }
} 
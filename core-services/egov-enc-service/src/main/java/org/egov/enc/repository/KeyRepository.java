package org.egov.enc.repository;

import org.egov.enc.models.AsymmetricKey;
import org.egov.enc.models.SymmetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class KeyRepository {

    private JdbcTemplate jdbcTemplate;
    private final AuditRepository auditRepository;

    private static final String selectSymmetricKeyQuery = "SELECT * FROM eg_enc_symmetric_keys";
    private static final String selectAsymmetricKeyQuery = "SELECT * FROM eg_enc_asymmetric_keys";

    private static final String insertSymmetricKeyQuery = "INSERT INTO eg_enc_symmetric_keys (key_id, secret_key, " +
            "initial_vector, active, tenant_id) VALUES (? ,?, ?, ?, ?)";
    private static final String insertAsymmetricKeyQuery = "INSERT INTO eg_enc_asymmetric_keys (key_id, public_key, " +
            "private_key, active, tenant_id) VALUES (? ,?, ?, ?, ?)";

    private static final String deactivateSymmetricKeyQuery = "UPDATE eg_enc_symmetric_keys SET active='false'";
    private static final String deactivateAsymmetricKeyQuery = "UPDATE eg_enc_asymmetric_keys SET active='false'";

    private static final String deactivateSymmetricKeyForGivenTenantQuery = "UPDATE eg_enc_symmetric_keys SET " +
            "active='false' WHERE active='true' AND tenant_id=?";
    private static final String deactivateAsymmetricKeyForGivenTenantQuery = "UPDATE eg_enc_asymmetric_keys SET " +
            "active='false' WHERE active='true' AND tenant_id=?";

    private static final String distinctTenantIdsQuery = "SELECT DISTINCT tenant_id FROM eg_enc_symmetric_keys WHERE active='true'";


    @Autowired
    public KeyRepository(JdbcTemplate jdbcTemplate, AuditRepository auditRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.auditRepository = auditRepository;
    }

    public int insertSymmetricKey(SymmetricKey symmetricKey) {
        long now = java.time.Instant.now().toEpochMilli();
        int result = jdbcTemplate.update(
                "INSERT INTO eg_enc_symmetric_keys (key_id, secret_key, initial_vector, active, tenant_id, createdtime, lastmodifiedtime) VALUES (?, ?, ?, ?, ?, ?, ?)",
                symmetricKey.getKeyId(),
                symmetricKey.getSecretKey(),
                symmetricKey.getInitialVector(),
                symmetricKey.isActive(),
                symmetricKey.getTenantId(),
                now,
                now
        );
        if (result == 1) {
            auditRepository.insertSymmetricKeyAudit(
                    "INSERT",
                    symmetricKey.getTenantId(),
                    null,
                    symmetricKey,
                    new java.sql.Timestamp(now)
            );
        }
        return result;
    }

    public int insertAsymmetricKey(AsymmetricKey asymmetricKey) {
        long now = java.time.Instant.now().toEpochMilli();
        int result = jdbcTemplate.update(
                "INSERT INTO eg_enc_asymmetric_keys (key_id, public_key, private_key, active, tenant_id, createdtime, lastmodifiedtime) VALUES (?, ?, ?, ?, ?, ?, ?)",
                asymmetricKey.getKeyId(),
                asymmetricKey.getPublicKey(),
                asymmetricKey.getPrivateKey(),
                asymmetricKey.isActive(),
                asymmetricKey.getTenantId(),
                now,
                now
        );
        if (result == 1) {
            auditRepository.insertAsymmetricKeyAudit(
                    "INSERT",
                    asymmetricKey.getTenantId(),
                    null,
                    asymmetricKey,
                    new java.sql.Timestamp(now)
            );
        }
        return result;
    }

    public int deactivateSymmetricKeyForGivenTenant(String tenantId) {
        long now = java.time.Instant.now().toEpochMilli();
        return auditRepository.auditDeactivateSymmetricKeyForTenant(
                tenantId,
                now,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_symmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true' AND tenant_id=?",
                        now, tenantId)
        );
    }

    public int deactivateAsymmetricKeyForGivenTenant(String tenantId) {
        long now = java.time.Instant.now().toEpochMilli();
        return auditRepository.auditDeactivateAsymmetricKeyForTenant(
                tenantId,
                now,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_asymmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true' AND tenant_id=?",
                        now, tenantId)
        );
    }

    public int deactivateSymmetricKeys() {
        long now = java.time.Instant.now().toEpochMilli();
        return auditRepository.auditDeactivateAllSymmetricKeys(
                now,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_symmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true'",
                        now)
        );
    }

    public int deactivateAsymmetricKeys() {
        long now = java.time.Instant.now().toEpochMilli();
        return auditRepository.auditDeactivateAllAsymmetricKeys(
                now,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_asymmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true'",
                        now)
        );
    }

    public List<SymmetricKey> fetchSymmetricKeys() {
        return jdbcTemplate.query(selectSymmetricKeyQuery, new BeanPropertyRowMapper<>(SymmetricKey.class));
    }

    public List<AsymmetricKey> fetchAsymmtericKeys() {
        return jdbcTemplate.query(selectAsymmetricKeyQuery, new BeanPropertyRowMapper<>(AsymmetricKey.class));
    }

    public List<String> fetchDistinctTenantIds() {
        return jdbcTemplate.queryForList(distinctTenantIdsQuery, String.class);
    }

}

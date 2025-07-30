package org.egov.enc.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.egov.enc.models.AsymmetricKey;
import org.egov.enc.models.AuditData;
import org.egov.enc.models.SymmetricKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.egov.enc.utils.DateTimeUtil;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class KeyRepository {

    private JdbcTemplate jdbcTemplate;
    private final AuditRepository auditRepository;
    @Autowired
    private ObjectMapper objectMapper;

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
        long now = DateTimeUtil.getCurrentEpochMillis();
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
            log.info("Symmetric key inserted successfully - keyId: {}, tenantId: {}", symmetricKey.getKeyId(), symmetricKey.getTenantId());
            try {
                String newRowJson = objectMapper.writeValueAsString(symmetricKey);
                List<AuditData> auditDataList = new ArrayList<>();
                auditDataList.add(AuditData.builder()
                        .operation("INSERT")
                        .tenantId(symmetricKey.getTenantId())
                        .oldRowJson(null)
                        .newRowJson(newRowJson)
                        .build());
                auditRepository.insertSymmetricKeyAuditBatch(auditDataList, now);
            } catch (Exception e) {
                log.error("Failed to create audit data for symmetric key insert - keyId: {}, tenantId: {}", symmetricKey.getKeyId(), symmetricKey.getTenantId(), e);
                throw new RuntimeException("Symmetric key insert audit data creation failed", e);
            }
        }
        return result;
    }

    public int insertAsymmetricKey(AsymmetricKey asymmetricKey) {
        long now = DateTimeUtil.getCurrentEpochMillis();
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
            try {
                log.info("Asymmetric key inserted successfully - keyId: {}, tenantId: {}", asymmetricKey.getKeyId(), asymmetricKey.getTenantId());
                String newRowJson = objectMapper.writeValueAsString(asymmetricKey);
                List<AuditData> auditDataList = new ArrayList<>();
                auditDataList.add(AuditData.builder()
                        .operation("INSERT")
                        .tenantId(asymmetricKey.getTenantId())
                        .oldRowJson(null)
                        .newRowJson(newRowJson)
                        .build());
                auditRepository.insertAsymmetricKeyAuditBatch(auditDataList, now);
            } catch (Exception e) {
                log.error("Failed to create audit data for asymmetric key insert - keyId: {}, tenantId: {}", asymmetricKey.getKeyId(), asymmetricKey.getTenantId(), e);
                throw new RuntimeException("Asymmetric key insert audit data creation failed", e);
            }
        }
        return result;
    }

    public int deactivateSymmetricKeyForGivenTenant(String tenantId) {
        long now = DateTimeUtil.getCurrentEpochMillis();
        return auditRepository.auditDeactivateSymmetricKeyForTenant(
                tenantId,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_symmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true' AND tenant_id=?",
                        now, tenantId),
                now
        );
    }

    public int deactivateAsymmetricKeyForGivenTenant(String tenantId) {
        long now = DateTimeUtil.getCurrentEpochMillis();
        return auditRepository.auditDeactivateAsymmetricKeyForTenant(
                tenantId,
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_asymmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true' AND tenant_id=?",
                        now, tenantId),
                now
        );
    }

    public int deactivateSymmetricKeys() {
        long now = DateTimeUtil.getCurrentEpochMillis();
        return auditRepository.auditDeactivateAllSymmetricKeys(
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_symmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true'",
                        now),
                now
        );
    }

    public int deactivateAsymmetricKeys() {
        long now = DateTimeUtil.getCurrentEpochMillis();
        return auditRepository.auditDeactivateAllAsymmetricKeys(
                () -> jdbcTemplate.update(
                        "UPDATE eg_enc_asymmetric_keys SET active='false', lastmodifiedtime=? WHERE active='true'",
                        now),
                now
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

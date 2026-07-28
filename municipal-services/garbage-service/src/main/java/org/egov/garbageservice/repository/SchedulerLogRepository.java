package org.egov.garbageservice.repository;

import java.util.HashMap;
import java.util.Map;
import org.egov.garbageservice.model.SchedulerLog;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SchedulerLogRepository {

    private static final String INSERT_SCHEDULER_LOG = "INSERT INTO eg_grbg_scheduler_log (id, garbage_account_id, tenant_id, billing_date, billing_period_from, billing_period_to, amount, penalty_amount, payment_type, status, created_by, created_time, last_modified_by, last_modified_time) VALUES (:id, :garbageAccountId, :tenantId, :billingDate, :billingPeriodFrom, :billingPeriodTo, :amount, :penaltyAmount, :paymentType, :status, :createdBy, :createdTime, :lastModifiedBy, :lastModifiedTime)";

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SchedulerLogRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public void save(SchedulerLog schedulerLog) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", schedulerLog.getId());
        params.put("garbageAccountId", schedulerLog.getGarbageAccountId());
        params.put("tenantId", schedulerLog.getTenantId());
        params.put("billingDate", schedulerLog.getBillingDate());
        params.put("billingPeriodFrom", schedulerLog.getBillingPeriodFrom());
        params.put("billingPeriodTo", schedulerLog.getBillingPeriodTo());
        params.put("amount", schedulerLog.getAmount());
        params.put("penaltyAmount", schedulerLog.getPenaltyAmount());
        params.put("paymentType", schedulerLog.getPaymentType());
        params.put("status", schedulerLog.getStatus());
        params.put("createdBy", schedulerLog.getCreatedBy());
        params.put("createdTime", schedulerLog.getCreatedTime());
        params.put("lastModifiedBy", schedulerLog.getLastModifiedBy());
        params.put("lastModifiedTime", schedulerLog.getLastModifiedTime());

        namedParameterJdbcTemplate.update(INSERT_SCHEDULER_LOG, params);
    }
}
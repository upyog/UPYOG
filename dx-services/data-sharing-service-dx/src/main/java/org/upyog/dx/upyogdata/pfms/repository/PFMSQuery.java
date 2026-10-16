package org.upyog.dx.upyogdata.pfms.repository;

public class PFMSQuery {

    private PFMSQuery() {}

    public static final String FIND_EXISTING_VOUCHER_NUMBERS =
            "SELECT voucher_number FROM public.ug_pfms_transactions" +
                    " WHERE voucher_number = ANY(?) AND agencyCode = ?";

    public static final String FETCH_INITIATED_TRANSACTION_IDS =
            "SELECT id FROM public.ug_pfms_transactions" +
                    " WHERE status = 'INITIATED'" +
                    " ORDER BY created_time ASC" +
                    " LIMIT ?";

    public static final String FETCH_FAILED_TRANSACTION_IDS =
            "SELECT id FROM public.ug_pfms_transactions" +
                    " WHERE status = 'FAILED'" +
                    " ORDER BY last_modified_time ASC" +
                    " LIMIT ?";

    public static final String FETCH_TRANSACTION_BY_ID =
            "SELECT * FROM public.ug_pfms_transactions WHERE id = ?";

    public static final String UPDATE_TRANSACTION_STATUS =
            "UPDATE public.ug_pfms_transactions" +
                    " SET status = ?, ingestion_date = ?, last_modified_time = CURRENT_TIMESTAMP" +
                    " WHERE id = ?";

    public static final String INSERT_SCHEDULER_LOG =
            "INSERT INTO public.ug_pfms_scheduler_log" +
                    " (scheduler_type, started_at, ended_at, duration_ms, status," +
                    " total_picked, success_count, failed_count, created_by, last_modified_time)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
}

package org.egov.pg.constants;

public class PgConstants {

    public static final String TXN_INITIATED = "Transaction initiated";
    public static final String TXN_SUCCESS = "Transaction successful";
    public static final String TXN_FAILURE_GATEWAY = "Transaction failed at gateway";
    public static final String TXN_FAILURE_AMT_MISMATCH = "Transaction failed, amount mismatch";
    public static final String TXN_RECEIPT_GEN_FAILED = "Receipt generation failed";
    public static final String PG_TXN_IN_LABEL = "eg_pg_txnid";
    public static final String NOTIFICATION_LOCALE = "en_IN";
    public static final String PG_NOTIFICATION = "PG_NOTIFICATION";
    public static final String PG_MODULE = "egov-pg";
    
    public static final String INVALID_TENANT_ID_MDMS_KEY = "INVALID TENANTID";
    public static final String INVALID_TENANT_ID_MDMS_MSG = "No data found for this tenentID";
    public static final String TXN_PENDING_GATEWAY = "Transaction Pending at gateway";

    private PgConstants() {
    }

}

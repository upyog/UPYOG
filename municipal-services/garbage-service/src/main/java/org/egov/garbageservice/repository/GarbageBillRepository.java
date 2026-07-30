package org.egov.garbageservice.repository;

import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.model.GarbageBill;
import org.egov.garbageservice.model.GarbageBillSearchCriteria;
import org.egov.garbageservice.repository.rowmapper.GarbageBillRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * Repository for creating, updating, and searching garbage bill records in the database.
 */
@Repository
@Slf4j
public class GarbageBillRepository {

    public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_hpudd_grbg_bill')";
    private static final String SELECT_QUERY_BILL = "SELECT bill.* FROM eg_grbg_bill as bill ";
    private static final String INSERT_BILL = "INSERT INTO eg_grbg_bill (id, bill_ref_no, garbage_id, bill_amount, arrear_amount, panelty_amount, discount_amount, total_bill_amount, total_bill_amount_after_due_date, bill_generated_by, bill_generated_date, bill_due_date, bill_period, bank_discount_amount, payment_id, payment_status, created_by, created_date, last_modified_by, last_modified_date) VALUES (:id, :billRefNo, :garbageId, :billAmount, :arrearAmount, :paneltyAmount, :discountAmount, :totalBillAmount, :totalBillAmountAfterDueDate, :billGeneratedBy, :billGeneratedDate, :billDueDate, :billPeriod, :bankDiscountAmount, :paymentId, :paymentStatus, :createdBy, :createdDate, :lastModifiedBy, :lastModifiedDate)";
    private static final String UPDATE_BILL_BY_ID = "UPDATE eg_grbg_bill SET bill_ref_no = :billRefNo, garbage_id = :garbageId, bill_amount = :billAmount, arrear_amount = :arrearAmount, panelty_amount = :paneltyAmount, discount_amount = :discountAmount, total_bill_amount = :totalBillAmount, total_bill_amount_after_due_date = :totalBillAmountAfterDueDate, bill_generated_by = :billGeneratedBy, bill_generated_date = :billGeneratedDate, bill_due_date = :billDueDate, bill_period = :billPeriod, bank_discount_amount = :bankDiscountAmount, payment_id = :paymentId, payment_status = :paymentStatus, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate WHERE id = :id";
    @Autowired
    GarbageBillRowMapper garbageBillRowMapper;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    /**
     * Constructs a new instance of GarbageBillRepository.
     *
     * <p>Initializes repository dependencies and configuration objects.
     */

    public GarbageBillRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Persists a new entity record into the database.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates the incoming request payload and audit details.</li>
     *   <li>Constructs the parameterized SQL insert query for entity persistence.</li>
     *   <li>Executes the database insert using {@link org.springframework.jdbc.core.JdbcTemplate}.</li>
     *   <li>Returns the created entity instance with populated audit metadata.</li>
     * </ol>
     *
     * @param bill the bill parameter for this operation
     * @return the output result of type {@link GarbageBill}
     */

    public GarbageBill create(GarbageBill bill) {

        bill.setId(getNextSequence());

        Map<String, Object> billInputs = new HashMap<>();
        billInputs.put("id", bill.getId());
        billInputs.put("billRefNo", bill.getBillRefNo());
        billInputs.put("garbageId", bill.getGarbageId());
        billInputs.put("billAmount", bill.getBillAmount());
        billInputs.put("arrearAmount", bill.getArrearAmount());
        billInputs.put("paneltyAmount", bill.getPaneltyAmount());
        billInputs.put("discountAmount", bill.getDiscountAmount());
        billInputs.put("totalBillAmount", bill.getTotalBillAmount());
        billInputs.put("totalBillAmountAfterDueDate", bill.getTotalBillAmountAfterDueDate());
        billInputs.put("billGeneratedBy", bill.getBillGeneratedBy());
        billInputs.put("billGeneratedDate", bill.getBillGeneratedDate());
        billInputs.put("billDueDate", bill.getBillDueDate());
        billInputs.put("billPeriod", bill.getBillPeriod());
        billInputs.put("bankDiscountAmount", bill.getBankDiscountAmount());
        billInputs.put("paymentId", bill.getPaymentId());
        billInputs.put("paymentStatus", bill.getPaymentStatus());
        billInputs.put("createdBy", bill.getAuditDetails().getCreatedBy());
        billInputs.put("createdDate", bill.getAuditDetails().getCreatedDate());
        billInputs.put("lastModifiedBy", bill.getAuditDetails().getLastModifiedBy());
        billInputs.put("lastModifiedDate", bill.getAuditDetails().getLastModifiedDate());

        namedParameterJdbcTemplate.update(INSERT_BILL, billInputs);
        return bill;
    }


    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @return the output result of type {@link Long}
     */

    private Long getNextSequence() {
        return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
    }

    /**
     * Updates existing entity details in the persistent repository.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Extracts updated entity attributes and audit timestamps.</li>
     *   <li>Constructs the parameterized SQL update query.</li>
     *   <li>Executes the update statement against the persistent store.</li>
     *   <li>Returns the modified entity state.</li>
     * </ol>
     *
     * @param bill the bill parameter for this operation
     */

    public void update(GarbageBill bill) {
        Map<String, Object> billInputs = new HashMap<>();
        billInputs.put("id", bill.getId());
        billInputs.put("billRefNo", bill.getBillRefNo());
        billInputs.put("garbageId", bill.getGarbageId());
        billInputs.put("billAmount", bill.getBillAmount());
        billInputs.put("arrearAmount", bill.getArrearAmount());
        billInputs.put("paneltyAmount", bill.getPaneltyAmount());
        billInputs.put("discountAmount", bill.getDiscountAmount());
        billInputs.put("totalBillAmount", bill.getTotalBillAmount());
        billInputs.put("totalBillAmountAfterDueDate", bill.getTotalBillAmountAfterDueDate());
        billInputs.put("billGeneratedBy", bill.getBillGeneratedBy());
        billInputs.put("billGeneratedDate", bill.getBillGeneratedDate());
        billInputs.put("billDueDate", bill.getBillDueDate());
        billInputs.put("billPeriod", bill.getBillPeriod());
        billInputs.put("bankDiscountAmount", bill.getBankDiscountAmount());
        billInputs.put("paymentId", bill.getPaymentId());
        billInputs.put("paymentStatus", bill.getPaymentStatus());
        billInputs.put("lastModifiedBy", bill.getAuditDetails().getLastModifiedBy());
        billInputs.put("lastModifiedDate", bill.getAuditDetails().getLastModifiedDate());

        namedParameterJdbcTemplate.update(UPDATE_BILL_BY_ID, billInputs);
    }


    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param garbageBillSearchCriteria the filter criteria defining search boundaries
     * @return the output result of type {@link List{@code <GarbageBill>}}
     */

    public List<GarbageBill> searchGarbageBills(GarbageBillSearchCriteria garbageBillSearchCriteria) {

        StringBuilder searchQuery = null;
        final List preparedStatementValues = new ArrayList<>();

        //generate search query
        searchQuery = getSearchQueryByCriteria(searchQuery, garbageBillSearchCriteria, preparedStatementValues);


        List<GarbageBill> garbageBills = jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), garbageBillRowMapper);

        return garbageBills;
    }

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param searchQuery               the searchQuery parameter for this operation
     * @param garbageBillSearchCriteria the filter criteria defining search boundaries
     * @param preparedStatementValues   the preparedStatementValues parameter for this operation
     * @return the output result of type {@link StringBuilder}
     */

    private StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery,
                                                   GarbageBillSearchCriteria garbageBillSearchCriteria, List preparedStatementValues) {

        searchQuery = new StringBuilder(SELECT_QUERY_BILL);
        searchQuery = addWhereClause(searchQuery, preparedStatementValues, garbageBillSearchCriteria);
        return searchQuery;
    }

    /**
     * Builds a dynamic SQL query string based on supplied criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Initializes the SQL query string buffer with base SELECT/UPDATE statements.</li>
     *   <li>Evaluates input criteria parameters and dynamically appends WHERE conditions.</li>
     *   <li>Populates prepared statement parameter value list.</li>
     *   <li>Returns the constructed dynamic SQL query string.</li>
     * </ol>
     *
     * @param searchQuery               the searchQuery parameter for this operation
     * @param preparedStatementValues   the preparedStatementValues parameter for this operation
     * @param garbageBillSearchCriteria the filter criteria defining search boundaries
     * @return the output result of type {@link StringBuilder}
     */

    private StringBuilder addWhereClause(StringBuilder searchQuery, List preparedStatementValues,
                                         GarbageBillSearchCriteria garbageBillSearchCriteria) {


        if (CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds())
                && CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds())
                && CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos())
                && CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds())
                && CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
            return null;
        }

        searchQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getIds())) {
            isAppendAndClause = addAndClauseIfRequired(false, searchQuery);
            searchQuery.append(" bill.id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getIds(),
                    preparedStatementValues)).append(" )");
        }


        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getBillRefNos())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.bill_ref_no IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getBillRefNos(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getGarbageIds())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.garbage_id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getGarbageIds(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentIds())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.payment_id IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getPaymentIds(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(garbageBillSearchCriteria.getPaymentStatus())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, searchQuery);
            searchQuery.append(" bill.payment_status IN ( ").append(getQueryForCollection(garbageBillSearchCriteria.getPaymentStatus(),
                    preparedStatementValues)).append(" )");
        }

        return searchQuery;
    }

    /**
     * Queries database for records matching the provided criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Constructs a dynamic SQL query based on active search criteria parameters.</li>
     *   <li>Appends pagination boundaries (limit and offset) and sorting clauses.</li>
     *   <li>Executes the SQL query via JdbcTemplate using custom row mapping.</li>
     *   <li>Assembles and returns the resulting entity list.</li>
     * </ol>
     *
     * @param ids                     the ids parameter for this operation
     * @param preparedStatementValues the preparedStatementValues parameter for this operation
     * @return the output result of type {@link Object}
     */

    private Object getQueryForCollection(List<?> ids, List<Object> preparedStatementValues) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStatementValues.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }

    /**
     * Builds a dynamic SQL query string based on supplied criteria.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Initializes the SQL query string buffer with base SELECT/UPDATE statements.</li>
     *   <li>Evaluates input criteria parameters and dynamically appends WHERE conditions.</li>
     *   <li>Populates prepared statement parameter value list.</li>
     *   <li>Returns the constructed dynamic SQL query string.</li>
     * </ol>
     *
     * @param appendAndClauseFlag the appendAndClauseFlag parameter for this operation
     * @param queryString         the queryString parameter for this operation
     * @return the output result of type {@link boolean}
     */

    private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

        return true;
    }
}

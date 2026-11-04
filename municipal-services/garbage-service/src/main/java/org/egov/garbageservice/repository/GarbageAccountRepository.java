package org.egov.garbageservice.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.egov.garbageservice.web.models.GarbageAccount;
import org.egov.garbageservice.web.models.GrbgCollectionUnit;
import org.egov.garbageservice.web.models.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.kafka.Producer;
import org.egov.garbageservice.repository.builder.GarbageAccountQueryBuilder;
import org.egov.garbageservice.repository.rowmapper.GarbageAccountRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.sql.Types;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Repository for managing garbage account data, including creation, update, deletion, and flexible search operations.
 */
@Repository
@Slf4j
public class GarbageAccountRepository {
    private final Producer producer;
    private final GarbageAccountQueryBuilder queryBuilder;
    @Autowired
    GarbageAccountRowMapper garbageAccountRowMapper;
    @Autowired
    private ObjectMapper objectMapper;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    /**
     * Constructs a new instance of GarbageAccountRepository.
     *
     * <p>Initializes repository dependencies and configuration objects.
     */

    public GarbageAccountRepository(Producer producer, NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                                    JdbcTemplate jdbcTemplate, GarbageAccountQueryBuilder queryBuilder) {
        this.producer = producer;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
        this.queryBuilder = queryBuilder;
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
     * @param account the account parameter for this operation
     * @return the output result of type {@link GarbageAccount}
     */

    public GarbageAccount create(GarbageAccount account) {
        log.info("Persisting garbage account to database. GarbageId: {}, Name: {}", account.getGarbageId(), account.getName());

        Map<String, Object> accountInputs = new HashMap<>();
        accountInputs.put("id", account.getId());
        accountInputs.put("uuid", account.getUuid());
        accountInputs.put("garbageId", account.getGarbageId());
        accountInputs.put("propertyId", account.getPropertyId());
        accountInputs.put("type", account.getType());
        accountInputs.put("name", account.getName());
        accountInputs.put("mobileNumber", account.getMobileNumber());
        accountInputs.put("isOwner", account.getIsOwner());
        accountInputs.put("userUuid", account.getUserUuid());
        accountInputs.put("declarationUuid", account.getDeclarationUuid());
        accountInputs.put("status", account.getStatus());
        accountInputs.put("gender", account.getGender());
        accountInputs.put("emailId", account.getEmailId());
        accountInputs.put("additionalDetail", null == account.getAdditionalDetail() ? null : objectMapper.convertValue(account.getAdditionalDetail(), ObjectNode.class).toString());
        accountInputs.put("createdBy", account.getAuditDetails().getCreatedBy());
        accountInputs.put("createdDate", account.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", account.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", account.getAuditDetails().getLastModifiedDate());
        accountInputs.put("tenantId", account.getTenantId());
        accountInputs.put("parentAccount", account.getParentAccount());
        accountInputs.put("businessService", account.getBusinessService());
        accountInputs.put("approvalDate", account.getApprovalDate());
        accountInputs.put("channel", account.getChannel());
        accountInputs.put("isActive", account.getIsActive());

        namedParameterJdbcTemplate.update(GarbageAccountQueryBuilder.INSERT_ACCOUNT, accountInputs);

        createGarbageAccountAudit(account);

        log.info("Successfully persisted garbage account. GarbageId: {}", account.getGarbageId());
        return account;
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
     * @param account the account parameter for this operation
     */

    private void createGarbageAccountAudit(GarbageAccount account) {
        Map<String, Object> accountAuditInputs = new HashMap<>();
        accountAuditInputs.put("grbgApplicationNo", account.getGrbgApplication().getApplicationNo());
        accountAuditInputs.put("status", account.getStatus());
        accountAuditInputs.put("type", account.getType());
        SqlParameterSource parameters = new MapSqlParameterSource(accountAuditInputs).addValue("grbgAccountDetails",
                objectMapper.convertValue(account, JsonNode.class).toString(), Types.OTHER);
        namedParameterJdbcTemplate.update(GarbageAccountQueryBuilder.INSERT_ACCOUNT_AUDIT, parameters);
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

    public Long getNextSequence() {
        return jdbcTemplate.queryForObject(GarbageAccountQueryBuilder.SELECT_NEXT_SEQUENCE, Long.class);
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

    public Long getNextGarbageId() {
        return jdbcTemplate.queryForObject(GarbageAccountQueryBuilder.SELECT_NEXT_GARBAGE_ID, Long.class);
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
     * @param newGarbageAccount the newGarbageAccount parameter for this operation
     */

    public void update(GarbageAccount newGarbageAccount) {
        log.info("Updating garbage account in database. GarbageId: {}, Status: {}", newGarbageAccount.getGarbageId(), newGarbageAccount.getStatus());

        Map<String, Object> accountInputs = new HashMap<>();
        accountInputs.put("id", newGarbageAccount.getId());
        accountInputs.put("uuid", newGarbageAccount.getUuid());
        accountInputs.put("garbageId", newGarbageAccount.getGarbageId());
        accountInputs.put("propertyId", newGarbageAccount.getPropertyId());
        accountInputs.put("type", newGarbageAccount.getType());
        accountInputs.put("name", newGarbageAccount.getName());
        accountInputs.put("mobileNumber", newGarbageAccount.getMobileNumber());
        accountInputs.put("isOwner", newGarbageAccount.getIsOwner());
        accountInputs.put("userUuid", newGarbageAccount.getUserUuid());
        accountInputs.put("declarationUuid", newGarbageAccount.getDeclarationUuid());
        accountInputs.put("status", newGarbageAccount.getStatus());
        accountInputs.put("gender", newGarbageAccount.getGender());
        accountInputs.put("emailId", newGarbageAccount.getEmailId());
        accountInputs.put("additionalDetail", null == newGarbageAccount.getAdditionalDetail() ? null : objectMapper.convertValue(newGarbageAccount.getAdditionalDetail(), ObjectNode.class).toString());
        accountInputs.put("lastModifiedBy", newGarbageAccount.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", newGarbageAccount.getAuditDetails().getLastModifiedDate());
        accountInputs.put("tenantId", newGarbageAccount.getTenantId());
        accountInputs.put("businessService", newGarbageAccount.getBusinessService());
        accountInputs.put("channel", newGarbageAccount.getChannel());
        accountInputs.put("approvalDate", newGarbageAccount.getApprovalDate());
        accountInputs.put("dueDate", newGarbageAccount.getDueDate());

        namedParameterJdbcTemplate.update(GarbageAccountQueryBuilder.UPDATE_ACCOUNT_BY_ID, accountInputs);

        createGarbageAccountAudit(newGarbageAccount);

        log.info("Successfully updated garbage account in database. GarbageId: {}", newGarbageAccount.getGarbageId());
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
     * @param searchCriteriaGarbageAccount the filter criteria defining search boundaries
     * @param garbageCriteriaMap           the filter criteria defining search boundaries
     * @return the output result of type {@link List{@code <GarbageAccount>}}
     */

    public List<GarbageAccount> searchGarbageAccount(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
                                                     Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {

        StringBuilder searchQuery = null;
        final List<Object> preparedStatementValues = new ArrayList<>();

        //generate search query
        searchQuery = queryBuilder.getSearchQueryByCriteria(searchQuery, searchCriteriaGarbageAccount, preparedStatementValues, garbageCriteriaMap);

        log.info("Executing database search with query: {} and parameters: {}", searchQuery, preparedStatementValues);

        List<GarbageAccount> garbageAccounts = jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), garbageAccountRowMapper);

        if (!CollectionUtils.isEmpty(garbageAccounts) && searchCriteriaGarbageAccount.getIsActiveAccount() != null) {
            // Filter garbage accounts based on the active account criteria
            garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> searchCriteriaGarbageAccount
                    .getIsActiveAccount().equals(garbageAccount.getIsActive())).collect(Collectors.toList());
        }

        if (searchCriteriaGarbageAccount.getIsMonthlyBilling() != null) {
            garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> searchCriteriaGarbageAccount
                    .getIsMonthlyBilling().equals(garbageAccount.getGrbgCollectionUnits().get(0).getIsmonthlybilling())).collect(Collectors.toList());
        }

        garbageAccounts = garbageAccounts.stream().filter(Objects::nonNull).map(garbageAccount -> {
            // If sub-account filtering is enabled, filter child garbage accounts
            if (searchCriteriaGarbageAccount.getIsActiveSubAccount() != null) {
                Optional.ofNullable(garbageAccount.getChildGarbageAccounts())
                        .filter(childAccounts -> !childAccounts.isEmpty()).ifPresent(childAccounts -> {
                            List<GarbageAccount> filteredChildren = childAccounts.stream()
                                    .filter(child -> searchCriteriaGarbageAccount.getIsActiveSubAccount()
                                            .equals(child.getIsActive()))
                                    .collect(Collectors.toList());
                            garbageAccount.setChildGarbageAccounts(filteredChildren);
                        });
            }
            if (searchCriteriaGarbageAccount.getIsMonthlyBilling() != null) {
                Optional.ofNullable(garbageAccount.getChildGarbageAccounts())
                        .filter(childAccounts -> !childAccounts.isEmpty()).ifPresent(childAccounts -> {
                            List<GarbageAccount> filteredChildren = childAccounts.stream()
                                    .filter(child -> {
                                        List<GrbgCollectionUnit> units = child.getGrbgCollectionUnits();

                                        return units != null && !units.isEmpty() &&
                                                searchCriteriaGarbageAccount.getIsMonthlyBilling()
                                                        .equals(units.get(0).getIsmonthlybilling());
                                    })
                                    .collect(Collectors.toList());
                            garbageAccount.setChildGarbageAccounts(filteredChildren);
                        });
            }
            return garbageAccount;
        }).collect(Collectors.toList());

        log.info("Database search returned {} accounts.", garbageAccounts != null ? garbageAccounts.size() : 0);
        return garbageAccounts;
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
     * @param searchCriteriaGarbageAccount the filter criteria defining search boundaries
     * @param garbageCriteriaMap           the filter criteria defining search boundaries
     * @return the output result of type {@link List{@code <GarbageAccount>}}
     */

    public List<GarbageAccount> searchGarbageAccountIndex(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
                                                          Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {

        StringBuilder searchQuery = null;
        final List<Object> preparedStatementValues = new ArrayList<>();

        //generate search query
        searchQuery = queryBuilder.getSearchQueryByCriteriaForIndex(searchQuery, searchCriteriaGarbageAccount, preparedStatementValues, garbageCriteriaMap);

        log.info("Executing database search with query: {} and parameters: {}", searchQuery, preparedStatementValues);

        List<GarbageAccount> garbageAccounts = jdbcTemplate.query(searchQuery.toString(), preparedStatementValues.toArray(), garbageAccountRowMapper);

        if (!CollectionUtils.isEmpty(garbageAccounts) && searchCriteriaGarbageAccount.getIsActiveAccount() != null) {
            // Filter garbage accounts based on the active account criteria
            garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> searchCriteriaGarbageAccount
                    .getIsActiveAccount().equals(garbageAccount.getIsActive())).collect(Collectors.toList());
        }

        if (searchCriteriaGarbageAccount.getIsMonthlyBilling() != null) {
            garbageAccounts = garbageAccounts.stream().filter(garbageAccount -> searchCriteriaGarbageAccount
                    .getIsMonthlyBilling().equals(garbageAccount.getGrbgCollectionUnits().get(0).getIsmonthlybilling())).collect(Collectors.toList());
        }

        garbageAccounts = garbageAccounts.stream().filter(Objects::nonNull).map(garbageAccount -> {
            // If sub-account filtering is enabled, filter child garbage accounts
            if (searchCriteriaGarbageAccount.getIsActiveSubAccount() != null) {
                Optional.ofNullable(garbageAccount.getChildGarbageAccounts())
                        .filter(childAccounts -> !childAccounts.isEmpty()).ifPresent(childAccounts -> {
                            List<GarbageAccount> filteredChildren = childAccounts.stream()
                                    .filter(child -> searchCriteriaGarbageAccount.getIsActiveSubAccount()
                                            .equals(child.getIsActive()))
                                    .collect(Collectors.toList());
                            garbageAccount.setChildGarbageAccounts(filteredChildren);
                        });
            }
            log.info("Problem account {}", garbageAccount);

            if (searchCriteriaGarbageAccount.getIsMonthlyBilling() != null) {
                Optional.ofNullable(garbageAccount.getChildGarbageAccounts())
                        .filter(childAccounts -> !childAccounts.isEmpty()).ifPresent(childAccounts -> {
                            List<GarbageAccount> filteredChildren = childAccounts.stream()
                                    .filter(child -> searchCriteriaGarbageAccount.getIsMonthlyBilling()
                                            .equals(child.getGrbgCollectionUnits().get(0).getIsmonthlybilling()))
                                    .collect(Collectors.toList());
                            garbageAccount.setChildGarbageAccounts(filteredChildren);
                        });
            }
            return garbageAccount;
        }).collect(Collectors.toList());

        log.info("Database search returned {} accounts.", garbageAccounts != null ? garbageAccounts.size() : 0);
        return garbageAccounts;
    }
    /**
     * Executes the delete database operation.
     *
     * <p>The operation performs the following steps:
     * <ol>
     *   <li>Validates method parameters.</li>
     *   <li>Executes repository database operation.</li>
     *   <li>Processes and returns the resulting output.</li>
     * </ol>
     *
     * @param garbageAccount the garbageAccount parameter for this operation
     */

    public void delete(GarbageAccount garbageAccount) {
        jdbcTemplate.update(GarbageAccountQueryBuilder.DELETE_QUERY, garbageAccount.getGarbageId());
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
     * @param searchCriteriaGarbageAccount the filter criteria defining search boundaries
     * @return the output result of type {@link List<GarbageAccount>}
     */

    public List<GarbageAccount> searchV2(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
        return searchGarbageAccount(searchCriteriaGarbageAccount, null);
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
     * @param topic the topic parameter for this operation
     * @param value the value parameter for this operation
     */

    public void save(String topic, Object value) {
        log.info("Saving data to Kafka topic: {}", topic);
        log.info("Value to be saved: {}", value);
        producer.push(topic, value);
    }
}
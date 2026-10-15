package org.egov.garbageservice.repository;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.BeanPropertyRowMapper;


import org.apache.commons.lang3.StringUtils;
import org.egov.garbageservice.model.DdpWorkflowUpdateRequest;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GrbgCollectionUnit;
import org.egov.garbageservice.model.ApplicationBillDTO;
import org.egov.garbageservice.model.SearchCriteriaGarbageAccount;
import org.egov.garbageservice.model.TotalCountRequest;
import org.egov.garbageservice.repository.rowmapper.GarbageAccountRowMapper;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.garbageservice.model.DdpVerificationCount;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class GarbageAccountRepository {
	
	@Autowired
	GarbageAccountRowMapper garbageAccountRowMapper;
	
	@Autowired
	private ObjectMapper objectMapper;

	private static final String SELECT_GRBG_ACC = " SELECT acc.* FROM eg_grbg_account acc"
			+ " LEFT JOIN eg_grbg_old_details old_dtl ON old_dtl.garbage_id = acc.garbage_id"
			+ " JOIN eg_grbg_collection_unit unit ON unit.garbage_id = acc.garbage_id"
			+ " JOIN eg_grbg_address address ON address.garbage_id = acc.garbage_id"
			+ " JOIN eg_grbg_application app ON app.garbage_id = acc.garbage_id"
			+ " LEFT JOIN eg_grbg_account_ddp ddp_dtl ON ddp_dtl.garbage_account_uuid = acc.uuid";

	private static final String DDP_DETAIL_COLUMNS = ", ddp_dtl.is_ready_for_printing as ddp_dtl_is_ready_for_printing"
			+ ", ddp_dtl.vendor_print_verified as ddp_dtl_vendor_print_verified"
			+ ", ddp_dtl.ulb_verified as ddp_dtl_ulb_verified"
			+ ", ddp_dtl.installation_done as ddp_dtl_installation_done"
			+ ", ddp_dtl.ddp_latitude as ddp_dtl_ddp_latitude"
			+ ", ddp_dtl.ddp_longitude as ddp_dtl_ddp_longitude"
			+ ", ddp_dtl.ddp_printing_done as ddp_dtl_ddp_printing_done"
			+ ", ddp_dtl.ddp_dispatched as ddp_dtl_ddp_dispatched"
			+ ", ddp_dtl.ddp_rejection_reason as ddp_dtl_ddp_rejection_reason"
			+ ", ddp_dtl.remarks as ddp_dtl_remarks ";

	private static final String DDP_DETAIL_JOIN = " LEFT OUTER JOIN eg_grbg_account_ddp as ddp_dtl ON ddp_dtl.garbage_account_uuid = acc.uuid";

	private static final String SELECT_QUERY_ACCOUNT = "SELECT acc.* "
			+ DDP_DETAIL_COLUMNS
			+ ", old_dtl.uuid as old_dtl_uuid, old_dtl.garbage_id as old_dtl_garbage_id, old_dtl.old_garbage_id as old_dtl_old_garbage_id"
			+ ", address.uuid as address_uuid, address.address_type as address_address_type, address.address1 as address_address1, address.address2 as address_address2, address.city as address_city, address.state as address_state, address.pincode as address_pincode, address.is_active as address_is_active, address.zone as address_zone, address.ulb_name as address_ulb_name, address.ulb_type as address_ulb_type, address.ward_name as address_ward_name, address.additional_detail as address_additional_detail, address.garbage_id as address_garbage_id"
			+ ", unit.uuid as unit_uuid, unit.unit_name as unit_unit_name, unit.unit_ward as unit_unit_ward, unit.ulb_name as unit_ulb_name, unit.type_of_ulb as unit_type_of_ulb, unit.garbage_id as unit_garbage_id, unit.unit_type as unit_unit_type, unit.category as unit_category, unit.sub_category as unit_sub_category, unit.sub_category_type as unit_sub_category_type, unit.is_active as unit_is_active,unit.isbplunit as unit_isbplunit,unit.isbulkgeneration as unit_isbulkgeneration,unit.isvariablecalculation as unit_isvariablecalculation,unit.no_of_units as unit_no_of_units,unit.ismonthlybilling as unit_is_monthly_billing"
			+ ", sub_acc.id as sub_acc_id, sub_acc.uuid as sub_acc_uuid, sub_acc.garbage_id as sub_acc_garbage_id, sub_acc.system_property_id as sub_acc_system_property_id, sub_acc.him_parivar_id as sub_acc_him_parivar_id, sub_acc.ddp_verified as sub_acc_ddp_verified, sub_acc.ddp_modified_date as sub_acc_ddp_modified_date, sub_acc.ddp_print_verified as sub_acc_ddp_print_verified, sub_acc.property_id as sub_acc_property_id, sub_acc.type as sub_acc_type "
			+ ", sub_acc.name as sub_acc_name, sub_acc.mobile_number as sub_acc_mobile_number, sub_acc.gender as sub_acc_gender, sub_acc.email_id as sub_acc_email_id, sub_acc.is_owner as sub_acc_is_owner"
			+ ", sub_acc.user_uuid as sub_acc_user_uuid, sub_acc.declaration_uuid as sub_acc_declaration_uuid, sub_acc.status as sub_acc_status, sub_acc.business_service as sub_acc_business_service"
			+ ", sub_acc.approval_date as sub_acc_approval_date, sub_acc.channel as sub_acc_channel"
			+ ", sub_acc.created_by as sub_acc_created_by, sub_acc.created_date as sub_acc_created_date, sub_acc.last_modified_by as sub_acc_last_modified_by"
			+ ", sub_acc.last_modified_date as sub_acc_last_modified_date, sub_acc.additional_detail as sub_acc_additional_detail, sub_acc.tenant_id as sub_acc_tenant_id, sub_acc.parent_account as sub_acc_parent_account, sub_acc.is_active as sub_acc_is_active, sub_acc.sub_account_count as sub_acc_sub_account_count"
			+ ", sub_old_dtl.uuid as sub_old_dtl_uuid, sub_old_dtl.garbage_id as sub_old_dtl_garbage_id, sub_old_dtl.old_garbage_id as sub_old_dtl_old_garbage_id"
			+ ", sub_address.uuid as sub_address_uuid, sub_address.address_type as sub_address_address_type, sub_address.address1 as sub_address_address1, sub_address.address2 as sub_address_address2, sub_address.city as sub_address_city, sub_address.state as sub_address_state, sub_address.pincode as sub_address_pincode, sub_address.is_active as sub_address_is_active, sub_address.zone as sub_address_zone, sub_address.ulb_name as sub_address_ulb_name, sub_address.ulb_type as sub_address_ulb_type, sub_address.ward_name as sub_address_ward_name, sub_address.additional_detail as sub_address_additional_detail, sub_address.garbage_id as sub_address_garbage_id"
		    + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id " 
		    + ", sub_app.uuid as sub_app_uuid, sub_app.application_no as sub_app_application_no , sub_app.status as sub_app_status, sub_app.garbage_id as sub_app_garbage_id "
			+ ", sub_unit.uuid as sub_unit_uuid, sub_unit.unit_name as sub_unit_unit_name, sub_unit.unit_ward as sub_unit_unit_ward, sub_unit.ulb_name as sub_unit_ulb_name, sub_unit.type_of_ulb as sub_unit_type_of_ulb, sub_unit.garbage_id as sub_unit_garbage_id, sub_unit.unit_type as sub_unit_unit_type, sub_unit.category as sub_unit_category, sub_unit.sub_category as sub_unit_sub_category, sub_unit.sub_category_type as sub_unit_sub_category_type, sub_unit.is_active as sub_unit_is_active,sub_unit.isbplunit as sub_unit_isbplunit,sub_unit.isbulkgeneration as sub_unit_isbulkgeneration,sub_unit.isvariablecalculation as sub_unit_isvariablecalculation,sub_unit.no_of_units as sub_unit_no_of_units,sub_unit.ismonthlybilling as sub_unit_is_monthly_billing"
		    + " FROM filtered_acc as acc"
		    + " LEFT OUTER JOIN eg_grbg_application as app ON app.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_old_details as old_dtl ON old_dtl.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_collection_unit as unit ON unit.garbage_id = acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_address as address ON address.garbage_id = acc.garbage_id"
			+ DDP_DETAIL_JOIN
			+ " LEFT OUTER JOIN eg_grbg_account sub_acc ON acc.uuid = sub_acc.parent_account"
		    + " LEFT OUTER JOIN eg_grbg_application as sub_app ON sub_app.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_old_details as sub_old_dtl ON sub_old_dtl.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_collection_unit as sub_unit ON sub_unit.garbage_id = sub_acc.garbage_id"
		    + " LEFT OUTER JOIN eg_grbg_address as sub_address ON sub_address.garbage_id = sub_acc.garbage_id";

	  private static final String SELECT_QUERY_ACCOUNT_INDEX = "SELECT acc.* "
	            + DDP_DETAIL_COLUMNS
	            + ", old_dtl.uuid as old_dtl_uuid, old_dtl.garbage_id as old_dtl_garbage_id, old_dtl.old_garbage_id as old_dtl_old_garbage_id"
	            + ", address.uuid as address_uuid, address.address_type as address_address_type, address.address1 as address_address1, address.address2 as address_address2, address.city as address_city, address.state as address_state, address.pincode as address_pincode, address.is_active as address_is_active, address.zone as address_zone, address.ulb_name as address_ulb_name, address.ulb_type as address_ulb_type, address.ward_name as address_ward_name, address.additional_detail as address_additional_detail, address.garbage_id as address_garbage_id"
	            + ", unit.uuid as unit_uuid, unit.unit_name as unit_unit_name, unit.unit_ward as unit_unit_ward, unit.ulb_name as unit_ulb_name, unit.type_of_ulb as unit_type_of_ulb, unit.garbage_id as unit_garbage_id, unit.unit_type as unit_unit_type, unit.category as unit_category, unit.sub_category as unit_sub_category, unit.sub_category_type as unit_sub_category_type, unit.is_active as unit_is_active,unit.isbplunit as unit_isbplunit,unit.isbulkgeneration as unit_isbulkgeneration,unit.isvariablecalculation as unit_isvariablecalculation,unit.no_of_units as unit_no_of_units,unit.ismonthlybilling as unit_is_monthly_billing"
	            + ", app.uuid as app_uuid, app.application_no as app_application_no , app.status as app_status, app.garbage_id as app_garbage_id "
	            + " FROM filtered_acc as acc"
	            + DDP_DETAIL_JOIN
	            + " LEFT OUTER JOIN eg_grbg_application as app ON app.garbage_id = acc.garbage_id"
	            + " LEFT OUTER JOIN eg_grbg_old_details as old_dtl ON old_dtl.garbage_id = acc.garbage_id"
	            + " LEFT OUTER JOIN eg_grbg_collection_unit as unit ON unit.garbage_id = acc.garbage_id"
	            + " LEFT OUTER JOIN eg_grbg_address as address ON address.garbage_id = acc.garbage_id";
    
    private static final String INSERT_ACCOUNT = "INSERT INTO eg_grbg_account (id, uuid, garbage_id, property_id, type, name"
    		+ ", mobile_number, gender, email_id, is_owner, user_uuid, declaration_uuid, status, additional_detail, created_by, created_date, "
    		+ "last_modified_by, last_modified_date, tenant_id, parent_account, business_service, approval_date, is_active, channel) "
    		+ "VALUES (:id, :uuid, :garbageId, :propertyId, :type, :name, :mobileNumber, :gender, :emailId, :isOwner, :userUuid, :declarationUuid, "
    		+ ":status, :additionalDetail :: JSONB, :createdBy, :createdDate, "
    		+ ":lastModifiedBy, :lastModifiedDate, :tenantId, :parentAccount, :businessService, :approvalDate, :isActive, :channel)";
    
    private static final String UPDATE_ACCOUNT_BY_ID = "UPDATE eg_grbg_account SET garbage_id = :garbageId, uuid =:uuid"
    		+ ", property_id = :propertyId, type = :type, name = :name, mobile_number = :mobileNumber, is_owner = :isOwner"
    		+ ", user_uuid = :userUuid, declaration_uuid = :declarationUuid, status = :status"
    		+ ", gender = :gender, email_id = :emailId, additional_detail = :additionalDetail :: JSONB, last_modified_by = :lastModifiedBy, last_modified_date = :lastModifiedDate,"
    		+ " tenant_id = :tenantId, business_service = :businessService, approval_date = :approvalDate , channel= :channel , system_property_id = :systemPropertyId , ddp_verified = :isDdpVerified , him_parivar_id = :himParivarId WHERE id = :id";

	public static final String SELECT_NEXT_SEQUENCE = "select nextval('seq_id_hpudd_grbg_account')";
	
	public static final String DELETE_QUERY = "UPDATE eg_grbg_account SET is_active = false WHERE garbage_id = ?";
	
	private static final String COUNT_STATUS_BASED_QUERY =  "SELECT COUNT(distinct grbg.id) as count, " +
		    "COUNT(distinct case when grbg.status = 'INITIATED' then grbg.id end) as applicationInitiated, " +
		    "COUNT(distinct case when grbg.status = 'PENDINGFORVERIFICATION' then grbg.id end) as applicationPendingForVerification, " +
		    "COUNT(distinct case when grbg.status = 'PENDINGFORMODIFICATION' then grbg.id end) as applicationPendingForModification, " +
		    "COUNT(distinct case when grbg.status = 'PENDINGFORAPPROVAL' then grbg.id end) as applicationPendingForApproval, " +
		    "COUNT(distinct case when grbg.status = 'APPROVED' then grbg.id end) as applicationApproved, " +
		    "COUNT(distinct case when grbg.status = 'REJECTED' then grbg.id end) as applicationRejected, " +
		    "COUNT(distinct case when grbg.status = 'PENDINGFORPAYMENT' then grbg.id end) as applicationPendingPayment, " +
		    "COUNT(distinct case when grbg.status = 'CLOSED' then grbg.id end) as applicationClosed, " +
		    "COUNT(distinct case when grbg.status = 'TEMPERORYCLOSED' then grbg.id end) as applicationTemporaryClosed " +
		    "from eg_grbg_account as grbg";
	
	private static final String INSERT_ACCOUNT_AUDIT = "INSERT INTO eg_grbg_account_audit (auditid, grbg_application_no, status, type"
			+ ", grbg_account_details, auditcreatedtime) VALUES ((select nextval('seq_eg_grbg_account_audit')), :grbgApplicationNo, :status"
			+ ", :type, :grbgAccountDetails, (SELECT extract(epoch from now())))";
	
	private static final String DDP_VERIFICATION_COUNT =
	        "SELECT " +
	        "COUNT(*) AS total_approved_accounts, " +
	        "COUNT(CASE WHEN acc.ddp_verified = true THEN 1 END) AS total_ddp_verified, " +
	        "COUNT(CASE WHEN acc.ddp_verified IS NULL OR acc.ddp_verified = false THEN 1 END) AS remaining_for_ddp_verification " +
	        "FROM eg_grbg_account acc " +
	        "WHERE acc.parent_account IS NULL " +
	        "AND acc.tenant_id = ? " +
	        "AND acc.is_active = true " +
	        "AND acc.status = 'APPROVED'";
	
	private static final String TOTAL_APPROVED_ACTIVE_ACCOUNTS =
	        "SELECT COUNT(*) " +
	        "FROM eg_grbg_account " +
	        "WHERE tenant_id = ? " +
	        "AND is_active = true " +
	        "AND status = 'APPROVED'";
	
	public static final String SELECT_NEXT_GARBAGE_ID = "select nextval('seq_eg_grbg_account_id')";
	
	public static final String WITH_SUB_QUERY = " WITH filtered_acc AS ({replace}) "
			+ SELECT_QUERY_ACCOUNT;
	
	public static final String WITH_SUB_QUERY_INDEX = " WITH filtered_acc AS ({replace}) "
			+ SELECT_QUERY_ACCOUNT_INDEX;

	public static final String REPLACE_STRING =  "{replace}";
	
	public static final String GET_APPROVER_FOR_TENANT = "select code from eg_hrms_employee ehe "
			+ "join eg_userrole_v1 eur on eur.user_id = ehe.id WHERE role_tenantid = ? AND role_code = 'GB_APPROVER'";

	/**
	 * property-services' own /property/_search overwrites owners[].mobileNumber
	 * with egov-user's stored number during enrichment (its search query
	 * doesn't even select eg_pt_owner.mobile_number), so the DDP printing
	 * search reads the column straight from eg_pt_owner instead, joined via
	 * eg_pt_property (both services share the same physical database).
	 */
	private static final String SELECT_PT_OWNER_MOBILE_NUMBERS = "SELECT p.propertyid AS property_id, "
			+ "o.mobile_number AS mobile_number, o.isprimaryowner AS isprimaryowner "
			+ "FROM eg_pt_property p JOIN eg_pt_owner o ON o.propertyid = p.id "
			+ "WHERE p.propertyid IN (%s) AND p.tenantid = ? AND o.status = 'ACTIVE'";

	private static final String UPDATE_DDP_DETAILS_BY_ID = "UPDATE eg_grbg_account "
			+ "SET ddp_print_verified = :ddpPrintVerified, " + "    ddp_modified_date = :ddpModifiedDate "
			+ "WHERE id = :id";

	private static final String UPDATE_ACCOUNT_LAST_MODIFIED_BY_IDS = "UPDATE eg_grbg_account "
			+ "SET last_modified_by = ?, last_modified_date = ? "
			+ "WHERE id IN (%s)";

	private static final String UPSERT_READY_FOR_PRINTING = "INSERT INTO eg_grbg_account_ddp "
			+ "(garbage_account_uuid, garbage_id, is_ready_for_printing) "
			+ "SELECT acc.uuid, acc.garbage_id, true FROM eg_grbg_account acc "
			+ "WHERE acc.id IN (%s) AND acc.uuid IS NOT NULL "
			+ "ON CONFLICT (garbage_account_uuid) DO UPDATE SET is_ready_for_printing = true";

	private static final String UPDATE_DDP_PRINT_VERIFIED_BY_UUID = "UPDATE eg_grbg_account SET "
	        + "ddp_print_verified = COALESCE(:ddpPrintVerified, ddp_print_verified), "
	        + "last_modified_by = :lastModifiedBy, "
	        + "last_modified_date = :lastModifiedDate "
	        + "WHERE uuid = :uuid AND tenant_id = :tenantId";

	private static final String UPSERT_DDP_DETAILS = "INSERT INTO eg_grbg_account_ddp "
	        + "(garbage_account_uuid, garbage_id, is_ready_for_printing, vendor_print_verified, ulb_verified, "
	        + "installation_done, ddp_latitude, ddp_longitude, ddp_printing_done, ddp_dispatched, "
	        + "ddp_rejection_reason, remarks) "
	        + "VALUES (:uuid, :garbageId, COALESCE(:isReadyForPrinting, false), CAST(:vendorPrintVerified AS VARCHAR), "
	        + "COALESCE(:ulbVerified, false), COALESCE(:installationDone, false), CAST(:ddpLatitude AS VARCHAR), "
	        + "CAST(:ddpLongitude AS VARCHAR), COALESCE(:ddpPrintingDone, false), COALESCE(:ddpDispatched, false), "
	        + "CAST(:ddpRejectionReason AS VARCHAR), CAST(:remarks AS VARCHAR)) "
	        + "ON CONFLICT (garbage_account_uuid) DO UPDATE SET "
	        + "is_ready_for_printing = COALESCE(:isReadyForPrinting, eg_grbg_account_ddp.is_ready_for_printing), "
	        + "vendor_print_verified = COALESCE(:vendorPrintVerified, eg_grbg_account_ddp.vendor_print_verified), "
	        + "ulb_verified = COALESCE(:ulbVerified, eg_grbg_account_ddp.ulb_verified), "
	        + "installation_done = COALESCE(:installationDone, eg_grbg_account_ddp.installation_done), "
	        + "ddp_latitude = COALESCE(:ddpLatitude, eg_grbg_account_ddp.ddp_latitude), "
	        + "ddp_longitude = COALESCE(:ddpLongitude, eg_grbg_account_ddp.ddp_longitude), "
	        + "ddp_printing_done = COALESCE(:ddpPrintingDone, eg_grbg_account_ddp.ddp_printing_done), "
	        + "ddp_dispatched = COALESCE(:ddpDispatched, eg_grbg_account_ddp.ddp_dispatched), "
	        + "ddp_rejection_reason = COALESCE(:ddpRejectionReason, eg_grbg_account_ddp.ddp_rejection_reason), "
	        + "remarks = COALESCE(:remarks, eg_grbg_account_ddp.remarks)";

	private static final String UPSERT_DDP_WORKFLOW_BY_UUID ="INSERT INTO eg_grbg_account_ddp "
	        + "(garbage_account_uuid, garbage_id, vendor_print_verified, ulb_verified, installation_done, "
	        + "ddp_latitude, ddp_longitude, ddp_printing_done, ddp_dispatched, ddp_rejection_reason, remarks) "
	        + "SELECT acc.uuid, acc.garbage_id, CAST(:vendorPrintVerified AS VARCHAR), COALESCE(:ulbVerified, false), "
	        + "COALESCE(:installationDone, false), CAST(:ddpLatitude AS VARCHAR), CAST(:ddpLongitude AS VARCHAR), "
	        + "COALESCE(:ddpPrintingDone, false), COALESCE(:ddpDispatched, false), "
	        + "CAST(:ddpRejectionReason AS VARCHAR), CAST(:remarks AS VARCHAR) "
	        + "FROM eg_grbg_account acc WHERE acc.uuid = :uuid AND acc.tenant_id = :tenantId "
	        + "ON CONFLICT (garbage_account_uuid) DO UPDATE SET "
	        + "vendor_print_verified = COALESCE(:vendorPrintVerified, eg_grbg_account_ddp.vendor_print_verified), "
	        + "ulb_verified = COALESCE(:ulbVerified, eg_grbg_account_ddp.ulb_verified), "
	        + "installation_done = COALESCE(:installationDone, eg_grbg_account_ddp.installation_done), "
	        + "ddp_latitude = COALESCE(:ddpLatitude, eg_grbg_account_ddp.ddp_latitude), "
	        + "ddp_longitude = COALESCE(:ddpLongitude, eg_grbg_account_ddp.ddp_longitude), "
	        + "ddp_printing_done = COALESCE(:ddpPrintingDone, eg_grbg_account_ddp.ddp_printing_done), "
	        + "ddp_dispatched = COALESCE(:ddpDispatched, eg_grbg_account_ddp.ddp_dispatched), "
	        + "ddp_rejection_reason = COALESCE(:ddpRejectionReason, eg_grbg_account_ddp.ddp_rejection_reason), "
	        + "remarks = COALESCE(:remarks, eg_grbg_account_ddp.remarks)";

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private JdbcTemplate jdbcTemplate;

    public GarbageAccountRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, JdbcTemplate jdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcTemplate = jdbcTemplate;
    }

    public GarbageAccount create(GarbageAccount account) {
    	
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
//        accountInputs.put("grbgCollectionAddressUuid", account.getGrbgCollectionAddressUuid());
        accountInputs.put("status", account.getStatus());
        accountInputs.put("gender", account.getGender());
        accountInputs.put("emailId", account.getEmailId());
        accountInputs.put("additionalDetail", null == account.getAdditionalDetail() ? null : objectMapper.convertValue(account.getAdditionalDetail(), ObjectNode.class).toString());
//        accountInputs.put("parentId", account.getParentId());
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

        namedParameterJdbcTemplate.update(INSERT_ACCOUNT, accountInputs);
        
        createGarbageAccountAudit(account);
        
        return account;
    }
    
    private void createGarbageAccountAudit(GarbageAccount account) {
		Map<String, Object> accountAuditInputs = new HashMap<>();
		accountAuditInputs.put("grbgApplicationNo", account.getGrbgApplication().getApplicationNo());
		accountAuditInputs.put("status", account.getStatus());
		accountAuditInputs.put("type", account.getType());
		SqlParameterSource parameters = new MapSqlParameterSource(accountAuditInputs).addValue("grbgAccountDetails",
				objectMapper.convertValue(account, JsonNode.class).toString(), Types.OTHER);
		namedParameterJdbcTemplate.update(INSERT_ACCOUNT_AUDIT, parameters);
	}

    public Long getNextSequence() {
    	return jdbcTemplate.queryForObject(SELECT_NEXT_SEQUENCE, Long.class);
	}
    
    public Long getNextGarbageId() {
    	return jdbcTemplate.queryForObject(SELECT_NEXT_GARBAGE_ID, Long.class);
    }

	public void update(GarbageAccount newGarbageAccount) {
        Map<String, Object> accountInputs = new HashMap<>();
        accountInputs.put("id", newGarbageAccount.getId());
        accountInputs.put("uuid", newGarbageAccount.getUuid());
        accountInputs.put("garbageId", newGarbageAccount.getGarbageId());
        accountInputs.put("propertyId", newGarbageAccount.getPropertyId());
        accountInputs.put("isDdpVerified", newGarbageAccount.getIsDdpVerified());
        accountInputs.put("systemPropertyId",newGarbageAccount.getSystemPropertyId());
        accountInputs.put("himParivarId",newGarbageAccount.getHimParivarId());
        accountInputs.put("type", newGarbageAccount.getType());
        accountInputs.put("name", newGarbageAccount.getName());
        accountInputs.put("mobileNumber", newGarbageAccount.getMobileNumber());
        accountInputs.put("isOwner", newGarbageAccount.getIsOwner());
        accountInputs.put("userUuid", newGarbageAccount.getUserUuid());
        accountInputs.put("declarationUuid", newGarbageAccount.getDeclarationUuid());
//        accountInputs.put("grbgCollectionAddressUuid", newGarbageAccount.getGrbgCollectionAddressUuid());
        accountInputs.put("status", newGarbageAccount.getStatus());
        accountInputs.put("gender", newGarbageAccount.getGender());
        accountInputs.put("emailId", newGarbageAccount.getEmailId());
        accountInputs.put("additionalDetail", null == newGarbageAccount.getAdditionalDetail() ? null : objectMapper.convertValue(newGarbageAccount.getAdditionalDetail(), ObjectNode.class).toString());
//        accountInputs.put("parentId", newGarbageAccount.getParentId());
//        accountInputs.put("createdBy", newGarbageAccount.getAuditDetails().getCreatedBy());
//        accountInputs.put("createdDate", newGarbageAccount.getAuditDetails().getCreatedDate());
        accountInputs.put("lastModifiedBy", newGarbageAccount.getAuditDetails().getLastModifiedBy());
        accountInputs.put("lastModifiedDate", newGarbageAccount.getAuditDetails().getLastModifiedDate());
        accountInputs.put("tenantId", newGarbageAccount.getTenantId());
        accountInputs.put("businessService", newGarbageAccount.getBusinessService());
        accountInputs.put("channel", newGarbageAccount.getChannel());
        accountInputs.put("approvalDate", newGarbageAccount.getApprovalDate());

        namedParameterJdbcTemplate.update(UPDATE_ACCOUNT_BY_ID, accountInputs);
        
        createGarbageAccountAudit(newGarbageAccount);
    }

	public List<GarbageAccount> searchGarbageAccount(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
			Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {
    	
    	StringBuilder searchQuery = null;
		final List<Object> preparedStatementValues = new ArrayList<>();

		//generate search query
    	searchQuery = getSearchQueryByCriteria(searchQuery, searchCriteriaGarbageAccount, preparedStatementValues, garbageCriteriaMap);
        
        log.info("### search garbage account: "+searchQuery.toString() + " {}",preparedStatementValues);

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
        
        return garbageAccounts;
    }
	
	public DdpVerificationCount getDdpVerificationCount(String tenantId) {

	    String query = DDP_VERIFICATION_COUNT;
	    return jdbcTemplate.queryForObject(query, new Object[] { tenantId },
	            (rs, rowNum) -> DdpVerificationCount.builder()
	                    .totalApprovedOwnerAccounts(rs.getInt("total_approved_accounts"))
	                    .totalDdpVerified(rs.getInt("total_ddp_verified"))
	                    .remainingForDdpVerification(rs.getInt("remaining_for_ddp_verification"))
	                    .build());
	}
	
	public Integer getTotalApprovedActiveAccounts(String tenantId) {
		return jdbcTemplate.queryForObject(TOTAL_APPROVED_ACTIVE_ACCOUNTS, new Object[] { tenantId }, Integer.class);
	}
	
	public List<GarbageAccount> searchGarbageAccountIndex(SearchCriteriaGarbageAccount searchCriteriaGarbageAccount,
			Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {
    	
    	StringBuilder searchQuery = null;
		final List<Object> preparedStatementValues = new ArrayList<>();

		//generate search query
    	searchQuery = getSearchQueryByCriteriaForIndex(searchQuery, searchCriteriaGarbageAccount, preparedStatementValues, garbageCriteriaMap);

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
        
        return garbageAccounts;
    }
	

	private StringBuilder getSearchQueryByCriteria(StringBuilder searchQuery,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, List<Object> preparedStatementValues,
			Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {

//		searchQuery = new StringBuilder(SELECT_QUERY_ACCOUNT);

		searchQuery = new StringBuilder(SELECT_GRBG_ACC);

		searchQuery.append(" WHERE");
		searchQuery.append(" 1=1 ");

		String whereClause = "";
		if (null != garbageCriteriaMap && !garbageCriteriaMap.isEmpty()) {
			List<String> clause = new ArrayList<>();
			garbageCriteriaMap.entrySet().forEach(garbageCriteriaValue -> {
				clause.add("(" + addWhereClause(preparedStatementValues, garbageCriteriaValue.getValue()) + ")");
			});
			if (!CollectionUtils.isEmpty(clause) && !clause.contains("()")) {
				addAndClauseIfRequired(true, searchQuery);
				whereClause = String.join(" OR ", clause);
			}
		} else {
			addAndClauseIfRequired(true, searchQuery);
			whereClause = addWhereClause(preparedStatementValues, searchCriteriaGarbageAccount);
		}

		searchQuery.append(whereClause);

		String withClauseQuery = WITH_SUB_QUERY.replace(REPLACE_STRING, searchQuery);

		StringBuilder sb = new StringBuilder(withClauseQuery);

		searchQuery = addOrderByClause(sb, searchCriteriaGarbageAccount);

		if (!searchCriteriaGarbageAccount.getIsSchedulerCall()) {
			searchQuery = addPaginationWrapper(sb, preparedStatementValues, searchCriteriaGarbageAccount);
		}
		return searchQuery;
	}
	
	private StringBuilder getSearchQueryByCriteriaForIndex(StringBuilder searchQuery,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount, List<Object> preparedStatementValues,
			Map<Integer, SearchCriteriaGarbageAccount> garbageCriteriaMap) {

//		searchQuery = new StringBuilder(SELECT_QUERY_ACCOUNT);

		searchQuery = new StringBuilder(SELECT_GRBG_ACC);

		searchQuery.append(" WHERE");

		String whereClause = "";
		if (null != garbageCriteriaMap && !garbageCriteriaMap.isEmpty()) {
			searchQuery.append(" acc.parent_account IS NULL ");
			List<String> clause = new ArrayList<>();
			garbageCriteriaMap.entrySet().forEach(garbageCriteriaValue -> {
				clause.add("(" + addWhereClause(preparedStatementValues, garbageCriteriaValue.getValue()) + ")");
			});
			if (!CollectionUtils.isEmpty(clause) && !clause.contains("()")) {
				addAndClauseIfRequired(true, searchQuery);
		        whereClause = "(" + String.join(" OR ", clause) + ")";

//				whereClause = String.join(" OR ", clause);
			}
		} else {
			searchQuery.append(" 1=1 ");
			addAndClauseIfRequired(true, searchQuery);
			whereClause = addWhereClause(preparedStatementValues, searchCriteriaGarbageAccount);
		}

		searchQuery.append(whereClause);

		String withClauseQuery = WITH_SUB_QUERY_INDEX.replace(REPLACE_STRING, searchQuery);

		StringBuilder sb = new StringBuilder(withClauseQuery);

		searchQuery = addOrderByClause(sb, searchCriteriaGarbageAccount);

		if (!searchCriteriaGarbageAccount.getIsSchedulerCall()) {
			searchQuery = addPaginationWrapper(sb, preparedStatementValues, searchCriteriaGarbageAccount);
		}
		return searchQuery;
	}
	
	private StringBuilder addPaginationWrapper(StringBuilder searchQuery, List<Object> preparedStatementValues,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {

//		Long limit = config.getDefaultLimit();
		Long limit = 5000L;
//		Long offset = config.getDefaultOffset();
		Long offset = 0L;

		if (null != searchCriteriaGarbageAccount.getLimit()) {
			limit = searchCriteriaGarbageAccount.getLimit();
		}
		if (null != searchCriteriaGarbageAccount.getOffset()) {
			offset = searchCriteriaGarbageAccount.getOffset();
		}

		searchQuery.append(" limit ? ");
		searchQuery.append(" offset ? ");

		preparedStatementValues.add(limit + offset);
		preparedStatementValues.add(offset);

		return searchQuery;
	}

	private StringBuilder addOrderByClause(StringBuilder searchQuery, SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {
		
		if(StringUtils.isNotEmpty(searchCriteriaGarbageAccount.getOrderBy())) {
			searchQuery = searchQuery.append(" ORDER BY acc.id "+searchCriteriaGarbageAccount.getOrderBy());
			return searchQuery;
		}
		
		return searchQuery;
	}

	private String addWhereClause(List<Object> preparedStatementValues,
			SearchCriteriaGarbageAccount searchCriteriaGarbageAccount) {

		StringBuilder whereClause = new StringBuilder();

		if (!searchCriteriaGarbageAccount.getIsSchedulerCall()
				&& (CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUuid())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getApplicationNumber())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid())
						&& CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getStatus())
						&& StringUtils.isEmpty(searchCriteriaGarbageAccount.getTenantId()))) {
			throw new CustomException("INCORRECT_SEARCH_CRITERIA", "Provide criteria to search garbage account.");
		}

//        searchQuery.append(" WHERE");
//        searchQuery.append(" 1=1 ");
        
//        boolean isAppendAndClause = addAndClauseIfRequired(false, whereClause);
        boolean isAppendAndClause = false;

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getId())) {
            isAppendAndClause = addAndClauseIfRequired(false, whereClause);
            whereClause.append(" acc.id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getId(),
                    preparedStatementValues)).append(" )");
        }
        
        if(searchCriteriaGarbageAccount.getUserType() != null) {
        if(searchCriteriaGarbageAccount.getUserType().equalsIgnoreCase(GrbgConstants.USER_TYPE_EMPLOYEE)) {
            if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
                isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
                whereClause.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
                        preparedStatementValues)).append(" )");
            }
            
            if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid())) {
            	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            	whereClause.append(" acc.user_uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUser_uuid(),
            			preparedStatementValues)).append(" )");
            }
        }else {
        	 if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
                 isAppendAndClause = addORClauseIfRequired(isAppendAndClause, whereClause);
                 whereClause.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
                         preparedStatementValues)).append(" )");
             }
             
             if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid())) {
             	isAppendAndClause = addORClauseIfRequired(isAppendAndClause, whereClause);
             	whereClause.append(" acc.user_uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUser_uuid(),
             			preparedStatementValues)).append(" )");
             }
        }
        }
        

//        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getCreatedBy())) {
//            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
//            whereClause.append(" acc.created_by IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getCreatedBy(),
//                    preparedStatementValues)).append(" )");
//        }
        

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getGarbageId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.garbage_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getGarbageId(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getPropertyId())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.property_id IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getPropertyId(),
                    preparedStatementValues)).append(" )");
        }
        
        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUuid())) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
        	whereClause.append(" acc.uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUuid(),
        			preparedStatementValues)).append(" )");
        }
        
        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUser_uuid())) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
        	whereClause.append(" acc.user_uuid IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getUser_uuid(),
        			preparedStatementValues)).append(" )");
        }


        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getType())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.type IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getType(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getName())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.name IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getName(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getMobileNumber())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.mobile_number IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getMobileNumber(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getApplicationNumber())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" app.application_no IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getApplicationNumber(),
                    preparedStatementValues)).append(" )");
        }

        if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getStatus())) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.status IN ( ").append(getQueryForCollection(searchCriteriaGarbageAccount.getStatus(),
                    preparedStatementValues)).append(" )");
        }
        
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getStatusList())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" acc.status IN ( ").append(
					getQueryForCollection(searchCriteriaGarbageAccount.getStatusList(), preparedStatementValues))
					.append(" )");
		}
        
        if (null != searchCriteriaGarbageAccount.getTenantId()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.tenant_id = ").append("'"+searchCriteriaGarbageAccount.getTenantId()+"'");
        }
        
        if (null != searchCriteriaGarbageAccount.getIsOwner()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.is_owner = ").append(searchCriteriaGarbageAccount.getIsOwner());
        }
        
        if (null != searchCriteriaGarbageAccount.getParentAccount()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.parent_account = ").append(searchCriteriaGarbageAccount.getParentAccount());
        }
        
        if (null != searchCriteriaGarbageAccount.getStartId()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.id >= ").append(+searchCriteriaGarbageAccount.getStartId());
        }
        
        if (null != searchCriteriaGarbageAccount.getEndId()) {
        	isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
            whereClause.append(" acc.id <= ").append(+searchCriteriaGarbageAccount.getEndId());
        }
        
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getChannels())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" acc.channel IN ( ")
					.append(getQueryForCollection(searchCriteriaGarbageAccount.getChannels(), preparedStatementValues))
					.append(" )");
		}
		
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getWardNames())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" address.ward_name IN ( ")
					.append(getQueryForCollection(searchCriteriaGarbageAccount.getWardNames(), preparedStatementValues))
					.append(" )");
		}
		
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getOldGarbageIds())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" old_dtl.old_garbage_id IN ( ").append(
					getQueryForCollection(searchCriteriaGarbageAccount.getOldGarbageIds(), preparedStatementValues))
					.append(" )");
		}
		
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUnitCategories())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" unit.category IN ( ").append(
					getQueryForCollection(searchCriteriaGarbageAccount.getUnitCategories(), preparedStatementValues))
					.append(" )");
		}
		
		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getUnitTypes())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" unit.unit_type IN ( ")
					.append(getQueryForCollection(searchCriteriaGarbageAccount.getUnitTypes(), preparedStatementValues))
					.append(" )");
		}
        
		if (searchCriteriaGarbageAccount.getIsUserUuidNull() != null) {
		    isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
		    
		    if (searchCriteriaGarbageAccount.getIsUserUuidNull()) {
		        whereClause.append(" acc.user_uuid IS NULL ");
		    } else {
		        whereClause.append(" acc.user_uuid IS NOT NULL ");
		    }
		}
		
		if (searchCriteriaGarbageAccount.getIsDdpVerified() != null) {
		    isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
		    whereClause.append(" acc.ddp_verified = ? ");
		    preparedStatementValues.add(searchCriteriaGarbageAccount.getIsDdpVerified());
		}
		
		if (searchCriteriaGarbageAccount.getDdpPrintVerified() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" acc.ddp_print_verified = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getDdpPrintVerified());
		}

		if (searchCriteriaGarbageAccount.getIsReadyForPrinting() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" COALESCE(ddp_dtl.is_ready_for_printing, false) = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getIsReadyForPrinting());
		}

		if (!CollectionUtils.isEmpty(searchCriteriaGarbageAccount.getVendorPrintVerified())) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" ddp_dtl.vendor_print_verified IN ( ").append(getQueryForCollection(
					searchCriteriaGarbageAccount.getVendorPrintVerified(), preparedStatementValues)).append(" )");
		}

		if (searchCriteriaGarbageAccount.getUlbVerified() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" COALESCE(ddp_dtl.ulb_verified, false) = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getUlbVerified());
		}

		if (searchCriteriaGarbageAccount.getInstallationDone() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" COALESCE(ddp_dtl.installation_done, false) = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getInstallationDone());
		}

		if (searchCriteriaGarbageAccount.getDdpPrintingDone() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" COALESCE(ddp_dtl.ddp_printing_done, false) = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getDdpPrintingDone());
		}

		if (searchCriteriaGarbageAccount.getDdpDispatched() != null) {
			isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, whereClause);
			whereClause.append(" COALESCE(ddp_dtl.ddp_dispatched, false) = ? ");
			preparedStatementValues.add(searchCriteriaGarbageAccount.getDdpDispatched());
		}

        return whereClause.toString();
	}
	
	private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND ");

        return true;
    }
	
    private String getQueryForCollection(List<?> ids, List<Object> preparedStmtList) {
        StringBuilder builder = new StringBuilder();
        Iterator<?> iterator = ids.iterator();
        while (iterator.hasNext()) {
            builder.append(" ?");
            preparedStmtList.add(iterator.next());

            if (iterator.hasNext())
                builder.append(",");
        }
        return builder.toString();
    }

	public void delete(GarbageAccount garbageAccount) {
		jdbcTemplate.update(DELETE_QUERY, garbageAccount.getGarbageId());
	}
	
	public List<Map<String, Object>> getStatusCounts(TotalCountRequest totalCountRequest) {
		List<Object> preparedStmtList = new ArrayList<>();
//		String query = queryBuilder.getStatusBasedCountQuery(totalCountRequest, preparedStmtList);
		StringBuilder builder = new StringBuilder();
		builder.append(COUNT_STATUS_BASED_QUERY);
		builder.append(" WHERE 1 = 1 ");
		if (!StringUtils.isEmpty(totalCountRequest.getTenantId())) {
		    // Add "AND" clause if required
		    addAndClauseIfRequired(true, builder);

		    // Append tenant ID condition
		    builder.append(" grbg.tenant_id = ? ");
		    preparedStmtList.add(totalCountRequest.getTenantId());

		    // Add "AND" clause before the next condition
		    addAndClauseIfRequired(true, builder);

		    // Append status condition
		    builder.append(" grbg.is_active = ? ");
		    preparedStmtList.add(true); // Assuming status is a boolean 'true'
		}

//		return builder.toString();
		System.out.println(builder.toString());
		return jdbcTemplate.queryForList(builder.toString(), preparedStmtList.toArray());
//		return null;
		
		
	}
	
	public String getApproverUserNameForTenant(String tenantId) {
    	StringBuilder searchQuery = new StringBuilder(GET_APPROVER_FOR_TENANT);
		List<Object> preparedStmtList = new ArrayList<>();
	    preparedStmtList.add(tenantId);
        List<String> userNames = jdbcTemplate.query(searchQuery.toString(), preparedStmtList.toArray(),(rs, rowNum) -> rs.getString("code"));
		return userNames.get(0);
	}
	
	private boolean addORClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" OR ");
        return true;
    }
	
	public void updateDdpDetails(GarbageAccount garbageAccount) {
		Map<String, Object> accountInputs = new HashMap<>();
		accountInputs.put("id", garbageAccount.getId());
		accountInputs.put("ddpPrintVerified", garbageAccount.getDdpPrintVerified());
		accountInputs.put("ddpModifiedDate", garbageAccount.getDdpModifiedDate());

		namedParameterJdbcTemplate.update(UPDATE_DDP_DETAILS_BY_ID, accountInputs);
	}

	/**
	 * Bulk-marks the given account ids as ready for printing. Called by the
	 * DDP printing scheduler in id batches (see
	 * {@code GarbageAccountSchedulerService.markReadyForPrinting}) so a single
	 * ULB/ward's matching accounts don't require one UPDATE per row.
	 */
	@Transactional
	public int markReadyForPrinting(List<Long> ids, String userUuid, Long lastModifiedDate) {
		if (CollectionUtils.isEmpty(ids)) {
			return 0;
		}

		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			if (i > 0) {
				placeholders.append(",");
			}
			placeholders.append("?");
		}

		List<Object> accountParams = new ArrayList<>();
		accountParams.add(userUuid);
		accountParams.add(lastModifiedDate);
		accountParams.addAll(ids);
		jdbcTemplate.update(String.format(UPDATE_ACCOUNT_LAST_MODIFIED_BY_IDS, placeholders), accountParams.toArray());

		return jdbcTemplate.update(String.format(UPSERT_READY_FOR_PRINTING, placeholders), ids.toArray());
	}

	/**
	 * Writes the eg_grbg_account_ddp columns carried on the given account
	 * (creating the row on first write). Null fields keep their existing value.
	 * Used by the generic garbage account update flow.
	 */
	public void upsertDdpDetails(String accountUuid, Long garbageId, GarbageAccount account) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", accountUuid);
		params.put("garbageId", garbageId);
		params.put("isReadyForPrinting", account.getIsReadyForPrinting());
		params.put("vendorPrintVerified", account.getVendorPrintVerified());
		params.put("ulbVerified", account.getUlbVerified());
		params.put("installationDone", account.getInstallationDone());
		params.put("ddpLatitude", account.getDdpLatitude());
		params.put("ddpLongitude", account.getDdpLongitude());
		params.put("ddpPrintingDone", account.getDdpPrintingDone());
		params.put("ddpDispatched", account.getDdpDispatched());
		params.put("ddpRejectionReason", account.getDdpRejectionReason());
		params.put("remarks", account.getRemarks());

		namedParameterJdbcTemplate.update(UPSERT_DDP_DETAILS, params);
	}

	/**
	 * Partially updates the DDP workflow fields for one account, identified by
	 * uuid: ddp_print_verified stays on eg_grbg_account, everything else lives
	 * in eg_grbg_account_ddp (created on first write). Any field left null on
	 * the request keeps its existing value (see the COALESCEs in
	 * {@link #UPSERT_DDP_WORKFLOW_BY_UUID}), so each role-specific caller only
	 * needs to send the field(s) it owns.
	 *
	 * @return the number of accounts updated (0 if no account matched the
	 *         uuid/tenantId).
	 */
	@Transactional
	public int updateDdpWorkflowFields(DdpWorkflowUpdateRequest request, String userUuid, Long now) {
		Map<String, Object> params = new HashMap<>();
		params.put("uuid", request.getUuid());
		params.put("tenantId", request.getTenantId());
		params.put("vendorPrintVerified", request.getVendorPrintVerified());
		params.put("ulbVerified", request.getUlbVerified());
		params.put("installationDone", request.getInstallationDone());
		params.put("ddpLatitude", request.getDdpLatitude());
		params.put("ddpLongitude", request.getDdpLongitude());
		params.put("ddpPrintingDone", request.getDdpPrintingDone());
		params.put("ddpDispatched", request.getDdpDispatched());
		params.put("ddpRejectionReason", request.getDdpRejectionReason());
		params.put("remarks", request.getRemarks());
		params.put("lastModifiedBy", userUuid);
		params.put("lastModifiedDate", now);
		params.put("ddpPrintVerified", request.getDdpPrintVerified());

		int updatedAccounts = namedParameterJdbcTemplate.update(UPDATE_DDP_PRINT_VERIFIED_BY_UUID, params);
		if (updatedAccounts == 0) {
			return 0;
		}

		namedParameterJdbcTemplate.update(UPSERT_DDP_WORKFLOW_BY_UUID, params);
		return updatedAccounts;
	}

	/**
	 * Looks up the raw eg_pt_owner.mobile_number for each of the given
	 * property-services propertyIds (the garbage account's systemPropertyId),
	 * preferring the primary owner's number and falling back to any other
	 * active owner's if there's no primary. See {@link #SELECT_PT_OWNER_MOBILE_NUMBERS}
	 * for why this reads the column directly rather than going through
	 * property-services' /property/_search response.
	 */
	public Map<String, String> getOwnerMobileNumbersBySystemPropertyIds(List<String> systemPropertyIds,
			String tenantId) {

		if (CollectionUtils.isEmpty(systemPropertyIds)) {
			return Collections.emptyMap();
		}

		StringBuilder placeholders = new StringBuilder();
		for (int i = 0; i < systemPropertyIds.size(); i++) {
			if (i > 0) {
				placeholders.append(",");
			}
			placeholders.append("?");
		}

		String query = String.format(SELECT_PT_OWNER_MOBILE_NUMBERS, placeholders);
		List<Object> params = new ArrayList<>(systemPropertyIds);
		params.add(tenantId);

		List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, params.toArray());

		Map<String, String> primaryMobileByPropertyId = new HashMap<>();
		Map<String, String> fallbackMobileByPropertyId = new HashMap<>();

		for (Map<String, Object> row : rows) {
			String propertyId = (String) row.get("property_id");
			String mobileNumber = (String) row.get("mobile_number");
			Boolean isPrimary = (Boolean) row.get("isprimaryowner");

			if (StringUtils.isEmpty(propertyId) || StringUtils.isEmpty(mobileNumber)) {
				continue;
			}
			if (Boolean.TRUE.equals(isPrimary)) {
				primaryMobileByPropertyId.putIfAbsent(propertyId, mobileNumber);
			} else {
				fallbackMobileByPropertyId.putIfAbsent(propertyId, mobileNumber);
			}
		}

		fallbackMobileByPropertyId.forEach(primaryMobileByPropertyId::putIfAbsent);
		return primaryMobileByPropertyId;
	}
}
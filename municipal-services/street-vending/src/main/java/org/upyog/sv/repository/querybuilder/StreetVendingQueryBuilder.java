package org.upyog.sv.repository.querybuilder;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.upyog.sv.web.models.StreetVendingSearchCriteria;

import java.util.List;

@Component
public class StreetVendingQueryBuilder {

	private static final String BASE_SV_QUERY = " SELECT sv.application_id as svApplicationId, sv.tenant_id as svTenantId, sv.application_no as svApplicationNo, "
			+ "sv.application_date as svApplicationDate, sv.certificate_no as svCertificateNo, sv.approval_date as svApprovalDate, "
			+ "sv.application_status as svApplicationStatus, sv.trade_license_no as svTradeLicenseNo, sv.vending_activity as svVendingActivity, "
			+ "sv.vending_zone as svVendingZone, sv.cart_latitude as svCartLatitude, sv.cart_longitude as svCartLongitude, sv.vending_area as svVendingArea, "
			+ "sv.vending_license_certificate_id as svVendingLicenseCertificateId, sv.payment_receipt_id as svPaymentReceiptId , sv.vending_license_id as svVendingLicenseId, "
			+ "sv.local_authority_name as svLocalAuthorityName, sv.locality as svLocality, "
			+ "sv.disability_status as svDisabilityStatus, sv.application_created_by as svApplicationCreatedBy, "
			+ "sv.terms_and_condition as svTermsAndCondition, sv.createdby as svCreatedBy, sv.lastmodifiedby as svLastModifiedBy, "
			+ "sv.createdtime as svCreatedTime, sv.lastmodifiedtime as svLastModifiedTime, "
			+ "sv.expire_flag as svExpireFlag, sv.renewal_status as svRenewalStatus, sv.validity_date as svValidityDate, "
			+ "sv.old_application_no as svOldApplicationNo, sv.vendor_payment_frequency as svPayFrequency ";

	private static final String VENDOR_SELECT_QUERY = " ,vendor.id as vendorId, vendor.application_id as vendorApplicationId, vendor.vendor_id as vendorVendorId, "
			+ "vendor.name as vendorName, vendor.father_name as vendorFatherName, vendor.date_of_birth as vendorDateOfBirth, "
			+ "vendor.email_id as vendorEmailId, vendor.mobile_no as vendorMobileNo, vendor.gender as vendorGender,  "
			+ "vendor.relationship_type as vendorRelationshipType, vendor.user_category as vendorusercategory, vendor.special_category as vendorspecialcategory, "
			+ "vendor.is_involved as vendorisinvolved ";

	private static final String ADDRESS_SELECT_QUERY = " ,address.address_id as addressId, address.address_type as addressType, "
			+ "address.vendor_id as addressVendorId, address.house_no as addressHouseNo, address.address_line_1 as addressLine1, "
			+ "address.address_line_2 as addressLine2, address.landmark as addressLandmark, address.city as addressCity, "
			+ "address.city_code as addressCityCode, address.locality as addressLocality, address.locality_code as addressLocalityCode, address.pincode as addressPincode ";

	private static final String DOCUMENT_SELECT_QUERY = " ,doc.document_detail_id as documentDetailId, doc.application_id as documentApplicationId, "
			+ "doc.document_type as documentType, doc.filestore_id as documentFilestoreId ";

	private static final String BANK_SELECT_QUERY = " ,bank.bank_detail_id as bankDetailId, bank.application_id as bankApplicationId, "
			+ "bank.account_no as bankAccountNo, bank.ifsc_code as bankIfscCode, bank.bank_name as bankName, bank.bank_branch_name as bankBranchName, "
			+ "bank.account_holder_name as bankAccountHolderName ";

	private static final String OPERATION_TIME_SELECT_QUERY = " ,opTime.id as operationTimeId, opTime.application_id as operationTimeApplicationId, "
			+ "opTime.day_of_week as operationTimeDayOfWeek, opTime.from_time as operationTimeFromTime, opTime.to_time as operationTimeToTime ";
	
	private static final String BENEFICIARY_SCHEME_SELECT_QUERY = " ,scheme.id as beneficiaryId, scheme.application_id as beneficiarySchemeApplicationId, "
			+ "scheme.scheme_name as beneficiarySchemeName, scheme.enrollment_id as beneficiaryEnrollmentId ";
	
	private static final String PAYMENT_STATUS_SELECT_QUERY = 
		    " ,ps.status AS payment_status ";
	
	public static final String PAYMENT_SCHEDULE = "SELECT * FROM eg_sv_vendor_payment_schedule WHERE due_date = ? AND status = ?";
	
	public static final String VENDOR_PAYMENT_SCHEDULE = "SELECT * FROM eg_sv_vendor_payment_schedule WHERE application_no = ? AND status = ?";

	private static final String FROM_TABLES = " FROM eg_sv_street_vending_detail sv "
			+ "LEFT JOIN eg_sv_vendor_detail vendor ON sv.application_id = vendor.application_id "
			+ "LEFT JOIN eg_sv_address_detail address ON vendor.id = address.vendor_id "
			+ "LEFT JOIN eg_sv_document_detail doc ON sv.application_id = doc.application_id "
			+ "LEFT JOIN eg_sv_bank_detail bank ON sv.application_id = bank.application_id "
			+ "LEFT JOIN eg_sv_operation_time_detail opTime ON sv.application_id = opTime.application_id "
	        + "LEFT JOIN eg_sv_beneficiary_schemes_detail scheme ON sv.application_id = scheme.application_id "
	        + "LEFT JOIN eg_sv_vendor_payment_schedule ps ON sv.application_no = ps.application_no " ;

	private final String ORDERBY_CREATEDTIME = " ORDER BY sv.createdtime DESC ";

	private static final String applicationsCount = "SELECT count(sv.application_id) "
			+ " FROM eg_sv_street_vending_detail sv "
			+ " join eg_sv_vendor_detail vendor on sv.application_id = vendor.application_id  \n";

	public String getStreetVendingSearchQuery(StreetVendingSearchCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder query;
		if (!criteria.isCountCall()) {
			query = new StringBuilder(BASE_SV_QUERY);
			query.append(VENDOR_SELECT_QUERY);
			query.append(ADDRESS_SELECT_QUERY);
			query.append(DOCUMENT_SELECT_QUERY);
			query.append(BANK_SELECT_QUERY);
			query.append(OPERATION_TIME_SELECT_QUERY);
			query.append(BENEFICIARY_SCHEME_SELECT_QUERY);
			query.append(PAYMENT_STATUS_SELECT_QUERY);
			query.append(FROM_TABLES);
		} else {
			query = new StringBuilder(applicationsCount);
			  if (!ObjectUtils.isEmpty(criteria.getPaymentStatus())) {
		            query.append(" LEFT JOIN eg_sv_vendor_payment_schedule ps ON sv.application_no = ps.application_no ");
		        }
		}

		if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.tenant_id LIKE ? ");
			preparedStmtList.add("%" + criteria.getTenantId() + "%");
		}
		if (!ObjectUtils.isEmpty(criteria.getStatus())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.application_status = ? ");
			preparedStmtList.add(criteria.getStatus());
		}
		if (!ObjectUtils.isEmpty(criteria.getApplicationNumber())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.application_no = ? ");
			preparedStmtList.add(criteria.getApplicationNumber());
		}
		if (!ObjectUtils.isEmpty(criteria.getFromDate())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.application_date >= CAST(? AS bigint) ");
			preparedStmtList.add(criteria.getFromDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getToDate())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.application_date <= CAST(? AS bigint) ");
			preparedStmtList.add(criteria.getToDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getCreatedBy())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.createdby = ? ");
			preparedStmtList.add(criteria.getToDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getMobileNumber())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" vendor.mobile_no = ? ");
			preparedStmtList.add(criteria.getMobileNumber());
		}
		if (!ObjectUtils.isEmpty(criteria.getVendingType())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.vending_activity = ? ");
			preparedStmtList.add(criteria.getVendingType());
		}
		if (!ObjectUtils.isEmpty(criteria.getVendingZone())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.vending_zone = ? ");
			preparedStmtList.add(criteria.getVendingZone());
		}
		if (!ObjectUtils.isEmpty(criteria.getCreatedBy())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.createdby = ? ");
			preparedStmtList.add(criteria.getToDate());
		}
		if (!ObjectUtils.isEmpty(criteria.getValidityDate())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.validity_date <= ? ");
			preparedStmtList.add(criteria.getValidityDate());
		}
		
		if (!ObjectUtils.isEmpty(criteria.getVendorPaymentFrequency())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.vendor_payment_frequency <= ? ");
			preparedStmtList.add(criteria.getVendorPaymentFrequency());
		}
		
		if (!ObjectUtils.isEmpty(criteria.getCertificateNo())) {
			addClauseIfRequired(query, preparedStmtList);
			query.append(" sv.certificate_no <= ? ");
			preparedStmtList.add(criteria.getCertificateNo());
		}
		
		if (!ObjectUtils.isEmpty(criteria.getPaymentStatus())) {
		    addClauseIfRequired(query, preparedStmtList);
		    query.append(" ps.status = ? ");
		    preparedStmtList.add(criteria.getPaymentStatus());
		}
		
		if (!ObjectUtils.isEmpty(criteria.getRenewalStatus())) {
		    addClauseIfRequired(query, preparedStmtList);
		    query.append(" sv.renewal_status = ? ");
		    preparedStmtList.add(criteria.getRenewalStatus());
		}

		
		if (!criteria.isCountCall()) {
			query.append(ORDERBY_CREATEDTIME);
		}

		return query.toString();
	}

	private void addClauseIfRequired(StringBuilder query, List<Object> preparedStmtList) {
		if (preparedStmtList.isEmpty()) {
			query.append(" WHERE ");
		} else {
			query.append(" AND ");
		}
	}

	private String createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(preparedStmtList::add);
	}
}

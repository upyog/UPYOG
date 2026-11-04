package org.upyog.chb.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.chb.util.CommunityHallBookingUtil;
import org.upyog.chb.web.models.Address;
import org.upyog.chb.web.models.ApplicantDetail;
import org.upyog.chb.web.models.BookingPurpose;
import org.upyog.chb.web.models.VenueBookingDetail;
import org.upyog.chb.web.models.SpecialCategory;

/**
 * This class is responsible for mapping the result set from the database to a list of
 * CommunityHallBookingDetail objects.
 * 
 * Purpose:
 * - To extract data from the ResultSet and populate CommunityHallBookingDetail objects.
 * - To handle the mapping of database fields to the corresponding fields in the CommunityHallBookingDetail model.
 * 
 * Features:
 * - Implements the ResultSetExtractor interface to process the ResultSet.
 * - Iterates through the ResultSet and maps each row to a CommunityHallBookingDetail object.
 * - Uses a LinkedHashMap to ensure that booking details are mapped uniquely by booking ID.
 * - Handles nested objects such as ApplicantDetail, Address, BookingPurpose, and SpecialCategory.
 * - Uses utility methods from CommunityHallBookingUtil for parsing and audit details.
 * 
 * Dependencies:
 * - CommunityHallBookingUtil: Provides utility methods for parsing dates and extracting audit details.
 * - CommunityHallBookingDetail: The model class representing booking details.
 * - ApplicantDetail, Address, BookingPurpose, SpecialCategory: Nested model classes for detailed booking information.
 * 
 * Fields Mapped:
 * - booking_id: Maps to the bookingId field in CommunityHallBookingDetail.
 * - booking_no: Maps to the bookingNo field in CommunityHallBookingDetail.
 * - tenant_id: Maps to the tenantId field in CommunityHallBookingDetail.
 * - applicant details: Maps to the ApplicantDetail object in CommunityHallBookingDetail.
 * - address details: Maps to the Address object in CommunityHallBookingDetail.
 * - booking purpose: Maps to the BookingPurpose object in CommunityHallBookingDetail.
 * - special category: Maps to the SpecialCategory object in CommunityHallBookingDetail.
 * - Audit details: Extracted using CommunityHallBookingUtil and mapped to the auditDetails field.
 * 
 * Usage:
 * - This class is used by the repository layer to map database query results to CommunityHallBookingDetail objects.
 * - It ensures consistency and reusability of mapping logic across the application.
 */
@Component
public class CommunityHallBookingRowmapper implements ResultSetExtractor<List<VenueBookingDetail>> {

	/**
	 * Maps booking header rows and de-duplicates them by {@code booking_id}.
	 *
	 * @param rs JDBC result set positioned before the first row
	 * @return de-duplicated booking header rows, never {@code null}
	 */
	@Override
	@SuppressWarnings("java:S2638")
	public List<VenueBookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, VenueBookingDetail> bookingDetailMap = new LinkedHashMap<>();
		List<VenueBookingDetail> bookingDetails = new ArrayList<>();
		while (rs.next()) {
			String bookingId = rs.getString("booking_id");
			String bookingNo = rs.getString("booking_no");
			String tenantId = rs.getString("tenant_id");
			VenueBookingDetail currentBooking = bookingDetailMap.get(bookingId);

			if (currentBooking == null) {

				SpecialCategory specialCategory = SpecialCategory.builder().category(rs.getString("special_category"))
						.build();

				BookingPurpose bookingPurpose = BookingPurpose.builder().purpose(rs.getString("purpose")).build();
				
				currentBooking = VenueBookingDetail.builder().bookingId(bookingId).bookingNo(bookingNo)
						.applicationDate(rs.getLong("application_date"))
						.tenantId(tenantId)
						.venueCode(rs.getString("venue_code"))
						.venueType(rs.getString("venue_type"))
						.bookingStatus(rs.getString("booking_status"))
						.specialCategory(specialCategory).purpose(bookingPurpose)
						.purposeDescription(rs.getString("purpose_description"))
						.paymentDate(rs.getLong("payment_date"))
						.receiptNo(rs.getString("receipt_no"))
						.permissionLetterFilestoreId(rs.getString("permission_letter_filestore_id"))
						.paymentReceiptFilestoreId(rs.getString("payment_receipt_filestore_id"))
						.auditDetails(CommunityHallBookingUtil.getAuditDetails(rs))
						.build();

				bookingDetailMap.put(bookingId, currentBooking);
			} else {
				currentBooking = bookingDetailMap.get(bookingId);
			}

			if (bookingDetailMap.isEmpty()) {
				return bookingDetails;
			}

			currentBooking.setApplicantDetail(addApplicantDetail(rs));
			currentBooking.setAddress(addApplicantAddress(rs));

		}
		bookingDetails.addAll(bookingDetailMap.values());
		return bookingDetails;

	}

	
	private ApplicantDetail addApplicantDetail(ResultSet rs) throws SQLException {
		return ApplicantDetail.builder().applicantDetailId(rs.getString("applicant_detail_id"))
				.bookingId(rs.getString("booking_id"))
				.applicantName(rs.getString("applicant_name"))
				.applicantMobileNo(rs.getString("applicant_mobile_no"))
				.applicantAlternateMobileNo(rs.getString("applicant_alternate_mobile_no"))
				.applicantEmailId(rs.getString("applicant_email_id"))
				.accountNumber(rs.getString("account_no"))
				.ifscCode(rs.getString("ifsc_code")).bankName(rs.getString("bank_name"))
				.bankBranchName(rs.getString("bank_branch_name"))
				.accountHolderName(rs.getString("account_holder_name"))
				.auditDetails(CommunityHallBookingUtil.getAuditDetails(rs)).build();
	}
	
    private Address addApplicantAddress(ResultSet rs) throws SQLException {
		return Address.builder()
				.addressId(rs.getString("address_id"))
				.applicantDetailId(rs.getString("applicant_detail_id"))
				.doorNo(rs.getString("door_no"))
				.houseNo(rs.getString("house_no"))
				.addressLine1(rs.getString("address_line_1"))
				.landmark(rs.getString("landmark"))
				.city(rs.getString("city"))
				.cityCode(rs.getString("city_code"))
				.pincode(rs.getString("pincode"))
				.streetName(rs.getString("street_name"))
				.locality(rs.getString("locality"))
				.localityCode(rs.getString("locality_code"))
				.build();
	}

}

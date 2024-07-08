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
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.BookingPurpose;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.SpecialCategory;

@Component
public class CommunityHallBookingRowmapper implements ResultSetExtractor<List<CommunityHallBookingDetail>> {

	@Override
	public List<CommunityHallBookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		//TODO: Remove this map
		Map<String, CommunityHallBookingDetail> bookingDetailMap = new LinkedHashMap<>();
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();
		while (rs.next()) {
			String bookingId = rs.getString("booking_id");
			String bookingNo = rs.getString("booking_no");
			String tenantId = rs.getString("tenant_id");
			CommunityHallBookingDetail currentBooking = bookingDetailMap.get(bookingId);

			if (currentBooking == null) {

				SpecialCategory specialCategory = SpecialCategory.builder().category(rs.getString("special_category"))
						.build();

				BookingPurpose bookingPurpose = BookingPurpose.builder().purpose(rs.getString("purpose")).build();
				
				currentBooking = CommunityHallBookingDetail.builder().bookingId(bookingId).bookingNo(bookingNo)
						.applicationDate(rs.getLong("application_date"))
						.tenantId(tenantId)
						//TODO : check payment_date
						.communityHallCode(rs.getString("community_hall_code"))
						.bookingStatus(rs.getString("booking_status"))
						.specialCategory(specialCategory).purpose(bookingPurpose)
						.purposeDescription(rs.getString("purpose_description"))
						.auditDetails(CommunityHallBookingUtil.getAuditDetails(rs))
						.build();

				bookingDetailMap.put(bookingId, currentBooking);
			} else {
				currentBooking = bookingDetailMap.get(bookingId);
			}

			if (bookingDetailMap.values().size() == 0) {
				return bookingDetails;
			}

			currentBooking.setApplicantDetail(addApplicantDetail(rs));
			currentBooking.setAddress(addApplicantAddress(rs));

		}
		bookingDetails.addAll(bookingDetailMap.values());
		return bookingDetails;

	}

	
	private ApplicantDetail addApplicantDetail(ResultSet rs) throws SQLException {
		
		ApplicantDetail applicantDetail = ApplicantDetail.builder().applicantDetailId(rs.getString("applicant_detail_id"))
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
		

		
		return applicantDetail;
		
	}
	
    private Address addApplicantAddress(ResultSet rs) throws SQLException {
		
		/**
		 * address_id, applicant_detail_id, door_no, house_no, address_line_1, 
	landmark, city, pincode, street_name, locality_code
		 */
		
		Address address = Address.builder()
				.addressId(rs.getString("address_id"))
				.applicantDetailId(rs.getString("applicant_detail_id"))
				.doorNo(rs.getString("door_no"))
				.houseNo(rs.getString("house_no"))
				.addressLine1(rs.getString("address_line_1"))
				.landmark(rs.getString("landmark"))
				.city(rs.getString("city"))
				.pincode(rs.getString("pincode"))
				.streetName(rs.getString("applicant_detail_id"))
				.localityCode(rs.getString("applicant_detail_id"))
				.build();
		
		return address;
		
	}

}

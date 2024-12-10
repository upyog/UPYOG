package org.upyog.adv.repository.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.upyog.adv.repository.impl.BookingRepositoryImpl;
import org.upyog.adv.util.BookingUtil;
import org.upyog.adv.web.models.Address;
import org.upyog.adv.web.models.ApplicantDetail;
import org.upyog.adv.web.models.BookingDetail;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class BookingDetailRowmapper implements ResultSetExtractor<List<BookingDetail>> {

	@Override
	public List<BookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		//TODO: Remove this map
		log.info("Fetched booking details sizhgvghjyh : " + rs);
		Map<String, BookingDetail> bookingDetailMap = new LinkedHashMap<>();
		List<BookingDetail> bookingDetails = new ArrayList<BookingDetail>();
		while (rs.next()) {
			String bookingId = rs.getString("booking_id");
			String bookingNo = rs.getString("booking_no");
			String tenantId = rs.getString("tenant_id");
			BookingDetail currentBooking = bookingDetailMap.get(bookingId);
			if (currentBooking == null) {
				currentBooking = BookingDetail.builder().bookingId(bookingId).bookingNo(bookingNo)
						.applicationDate(rs.getLong("application_date"))
						.tenantId(tenantId)
						//TODO : check payment_date
						.bookingStatus(rs.getString("booking_status"))
						.paymentDate(rs.getLong("payment_date"))
						.receiptNo(rs.getString("receipt_no"))
						.permissionLetterFilestoreId(rs.getString("permission_letter_filestore_id"))
						.paymentReceiptFilestoreId(rs.getString("payment_receipt_filestore_id"))
						.auditDetails(BookingUtil.getAuditDetails(rs))
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
				.auditDetails(BookingUtil.getAuditDetails(rs)).build();
		
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
				.addressLine2(rs.getString("address_line_2"))
				.landmark(rs.getString("landmark"))
				.city(rs.getString("city"))
				.cityCode(rs.getString("city_code"))
				.pincode(rs.getString("pincode"))
				.streetName(rs.getString("street_name"))
				.locality(rs.getString("locality"))
				.localityCode(rs.getString("locality_code"))
				.build();
		
		return address;
		
	}

}

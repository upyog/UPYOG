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
import org.upyog.chb.web.models.AuditDetails;
import org.upyog.chb.web.models.BookingPurpose;
import org.upyog.chb.web.models.BookingSlotDetail;
import org.upyog.chb.web.models.CommunityHallBookingDetail;
import org.upyog.chb.web.models.ResidentType;
import org.upyog.chb.web.models.SpecialCategory;

@Component
public class CommunityHallBookingRowmapper implements ResultSetExtractor<List<CommunityHallBookingDetail>> {

	@Override
	public List<CommunityHallBookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, CommunityHallBookingDetail> bookingDetailMap = new LinkedHashMap<>();
		while (rs.next()) {
			String bookingId = rs.getString("booking_id");
			String bookingNo = rs.getString("booking_no");
			String tenantId = rs.getString("tenant_id");
			CommunityHallBookingDetail currentBooking = bookingDetailMap.get(bookingId);

			/**
			 * chbd.booking_id, chbd.booking_no, chbd.booking_date, chbd.approval_no,
			 * chbd.approval_date, chbd.tenant_id,
			 * chbd.community_hall_id,chbd.booking_status, chbd.resident_type,
			 * chbd.special_category, chbd.purpose, chbd.purpose_description,
			 * chbd.event_name, chbd.event_organized_by, chbd.createdby as
			 * booking_created_by, chbd.createdtime as booking_created_time,
			 * chbd.lastmodifiedby as booking_last_modified_by, chbd.lastmodifiedtime as
			 * booking_last_modified_time
			 */
			if (currentBooking == null) {
				AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("booking_created_by"))
						.createdTime(rs.getLong("booking_created_time"))
						.lastModifiedBy(rs.getString("booking_last_modified_by"))
						.lastModifiedTime(rs.getLong("booking_last_modified_time")).build();

				ResidentType residentType = ResidentType.builder().type(rs.getString("resident_type")).build();

				SpecialCategory specialCategory = SpecialCategory.builder().category(rs.getString("special_category"))
						.build();

				BookingPurpose bookingPurpose = BookingPurpose.builder().purpose(rs.getString("purpose")).build();
				currentBooking = CommunityHallBookingDetail.builder().bookingId(bookingId).bookingNo(bookingNo)
						.bookingDate(rs.getLong("booking_date")).approvalNo(rs.getString("approval_no"))
						.approvalDate(rs.getLong("approval_date")).tenantId(tenantId)
						.communityHallId(rs.getInt("community_hall_id")).bookingStatus(rs.getString("booking_status"))
						.residentType(residentType).specialCategory(specialCategory).purpose(bookingPurpose)
						.purposeDescription(rs.getString("purpose_description")).eventName(rs.getString("event_name"))
						.eventOrganisedBy(rs.getString("event_organized_by")).auditDetails(auditdetails).build();

				bookingDetailMap.put(bookingId, currentBooking);
			} else {
				currentBooking = bookingDetailMap.get(bookingId);
			}
			
			BookingSlotDetail slotDetail = prepareSlotDetail(rs);
			currentBooking.addBookingSlots(slotDetail);

		}
		List<CommunityHallBookingDetail> bookingDetails = new ArrayList<CommunityHallBookingDetail>();
		bookingDetails.addAll(bookingDetailMap.values());
		return bookingDetails;

	}

	private BookingSlotDetail prepareSlotDetail(ResultSet rs) throws SQLException {
		/**
		 * bsd.slot_id, bsd.booking_id as slot_booking_id,bsd.hallcode,
		 * bsd.booking_slot_datetime, bsd.status as slot_status, bsd.createdby as
		 * slot_created_by , bsd.createdtime as slot_created_time, bsd.lastmodifiedby as
		 * slot_last_modified_by, bsd.lastmodifiedtime as slot_last_modified_time
		 */

		AuditDetails auditdetails = AuditDetails.builder().createdBy(rs.getString("slot_created_by"))
				.createdTime(rs.getLong("slot_created_time")).lastModifiedBy(rs.getString("slot_last_modified_by"))
				.lastModifiedTime(rs.getLong("slot_last_modified_time")).build();

		BookingSlotDetail slotDetail = BookingSlotDetail.builder().slotId(rs.getString("slot_id"))
				.bookingId(rs.getString("slot_booking_id")).hallCode(rs.getString("hallcode"))
				.bookingSlotDatetime(rs.getLong("booking_slot_datetime")).status(rs.getString("slot_status"))
				.auditDetails(auditdetails).build();
		
		return slotDetail;
	}

}

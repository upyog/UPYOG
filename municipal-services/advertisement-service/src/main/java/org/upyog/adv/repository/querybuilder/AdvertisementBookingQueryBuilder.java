package org.upyog.adv.repository.querybuilder;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.adv.config.BookingConfiguration;
import org.upyog.adv.web.models.AdvertisementSlotSearchCriteria;

@Component
public class AdvertisementBookingQueryBuilder {

		
	private static final String slotDetailsQuery = "select * from public.eg_adv_cart_detail where booking_id in (";

	private static final String documentDetailsQuery = "select * from public.eg_adv_document_detail  where booking_id in (";

	private static final String ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY = "SELECT eabd.tenant_id, eacd.add_type, eacd.face_area, eacd.location, eacd.night_light, eacd.status, eacd.booking_date \n"
	        + "FROM eg_adv_booking_detail eabd, eg_adv_cart_detail eacd\n"
	        + "WHERE eabd.booking_id = eacd.booking_id AND eabd.tenant_id = ? \n"
	        + "AND eacd.status IN ('BOOKED', 'PENDING_FOR_PAYMENT') AND \n"
	        + "eacd.booking_date >= ?::DATE AND eacd.booking_date <= ?::DATE \n";
	 
	private Object createQueryParams(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	

	public String getSlotDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(slotDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();

	}

	public String getDocumentDetailsQuery(List<String> bookingIds) {
		StringBuilder builder = new StringBuilder(documentDetailsQuery);
		builder.append(createQueryParams(bookingIds)).append(")");
		return builder.toString();
	}

	public StringBuilder getAdvertisementSlotAvailabilityQuery(AdvertisementSlotSearchCriteria searchCriteria, List<Object> paramsList) {
	    StringBuilder builder = new StringBuilder(ADVERTISEMENT_SLOTS_AVAILABILITY_QUERY);
	    paramsList.add(searchCriteria.getTenantId());
	    paramsList.add(searchCriteria.getBookingStartDate());
	    paramsList.add(searchCriteria.getBookingEndDate());


	    return builder;
	}


}

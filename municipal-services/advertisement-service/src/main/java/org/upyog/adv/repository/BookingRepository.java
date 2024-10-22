package org.upyog.adv.repository;

import org.upyog.adv.web.models.BookingRequest;

public interface BookingRepository {

	void saveBooking(BookingRequest bookingRequest);
	
	
}

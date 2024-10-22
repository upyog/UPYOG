package org.upyog.adv.service;

import javax.validation.Valid;

import org.upyog.adv.web.models.BookingDetail;
import org.upyog.adv.web.models.BookingRequest;

public interface BookingService {

	BookingDetail createBooking(@Valid BookingRequest bookingRequest);

}

package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.upyog.adv.web.models.BookingDetail;
/**
 * This class implements the ResultSetExtractor interface to map rows from a database ResultSet
 * to a list of BookingDetail objects. It focuses on extracting only the booking ID
 * from each row in the ResultSet and populating it into BookingDetail objects.
 */
public class BookingDetailIdRowmapper implements ResultSetExtractor<List<BookingDetail>> {

    @Override
    public List<BookingDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<BookingDetail> bookingList = new ArrayList<>();
        while (rs.next()) {
            // Mapping only booking_id
            BookingDetail bookingDetail = new BookingDetail();
            bookingDetail.setBookingId(rs.getString("booking_id"));
            bookingList.add(bookingDetail);
        }

        return bookingList;
    }
}

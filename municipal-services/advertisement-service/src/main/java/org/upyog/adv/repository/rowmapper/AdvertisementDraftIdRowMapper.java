package org.upyog.adv.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.upyog.adv.web.models.AdvertisementDraftDetail;
/**
 * This class implements the ResultSetExtractor interface to map rows from a database ResultSet
 * to a list of AdvertisementDraftDetail objects. It focuses on extracting only the draft ID
 * from each row in the ResultSet and populating it into AdvertisementDraftDetail objects.
 */
@SuppressWarnings("java:S2638")
public class AdvertisementDraftIdRowMapper implements ResultSetExtractor<List<AdvertisementDraftDetail>> {

    @Override
    public List<AdvertisementDraftDetail> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<AdvertisementDraftDetail> draftList = new ArrayList<>();
        while (rs.next()) {
            AdvertisementDraftDetail draftDetail = new AdvertisementDraftDetail();
            draftDetail.setDraftId(rs.getString("draft_id"));
            draftList.add(draftDetail);
        }

        return draftList;
    }
}

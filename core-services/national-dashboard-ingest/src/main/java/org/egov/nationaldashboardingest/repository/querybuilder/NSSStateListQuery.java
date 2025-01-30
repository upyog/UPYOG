package org.egov.nationaldashboardingest.repository.querybuilder;

import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component

public class NSSStateListQuery {

    public String getYesterdayDataQuery() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        String formattedDate = yesterday.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

        String query = "SELECT split_part(datakey, ':', '1') AS date, "
                + "split_part(datakey, ':', '6') AS state, "
                + "split_part(datakey, ':', '2') AS module, "
                + "split_part(datakey, ':', '4') AS ulb "
                + "FROM nss_ingest_data "
                + "WHERE split_part(datakey, ':', '1') = '" + formattedDate + "';";

        return query;
    }
}
 
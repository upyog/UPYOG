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
                + "split_part(datakey, ':', '6') AS state "
                + "FROM nss_ingest_data "
                + "WHERE split_part(datakey, ':', '1') = '" + formattedDate + "';";

        return query;
    }
}
 
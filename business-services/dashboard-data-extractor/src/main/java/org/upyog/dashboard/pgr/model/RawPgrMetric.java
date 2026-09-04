package org.upyog.dashboard.pgr.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Raw data class for PGR combined metrics fetched directly via
 * BeanPropertyRowMapper.
 * <p>
 * This class maps JSON string columns natively from the database query result,
 * eliminating manual map extraction and type casting inside the extractor
 * service.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPgrMetric {

    private String tenantid;
    private String slaachievementjson;
    private String completionratejson;
    private Integer uniquecitizens;
    private String complaintsbystatusjson;
    private String complaintsbychanneljson;
    private String complaintsbydepartmentjson;
    private String complaintsbycategoryjson;
    private String todaysreopenedcomplaintsjson;
    private String todaysopencomplaintsjson;
    private String todaysassignedcomplaintsjson;
    private String averagesolutiontimejson;
    private String todaysrejectedcomplaintsjson;
    private String todaysreassignedcomplaintsjson;
    private String todaysreassignrequestedcomplaintsjson;
    private String todaysclosedcomplaintsjson;
    private String todaysresolvedcomplaintsjson;
}

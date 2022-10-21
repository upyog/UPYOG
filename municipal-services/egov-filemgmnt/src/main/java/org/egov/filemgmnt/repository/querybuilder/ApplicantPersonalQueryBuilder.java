package org.egov.filemgmnt.repository.querybuilder;

import lombok.extern.slf4j.Slf4j;
import java.util.*;
import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
 
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplicantPersonalQueryBuilder extends BaseQueryBuilder {
	 private static final String INNER_JOIN_STRING = " INNER JOIN ";

    private static final String QUERY = new StringBuilder().append(" SELECT ap.id, ap.aadhaarno, ap.email, ap.firstname, ap.lastname, ap.title, ap.mobileno, ap.tenantid")
                                                           .append("     , ap.createdby, ap.createdtime, ap.lastmodifiedby, ap.lastmodifiedtime")
                                                           .append(" FROM eg_fm_applicantpersonal ap")
                                                           .append(INNER_JOIN_STRING)
                                                           .append(" eg_fm_applicantaddress apa ON apa.applicantpersonalid = ap.id")
                                                           .append(INNER_JOIN_STRING)
                                                           .append(" eg_fm_applicantdocuments ad ON ad.applicantpersonalid = ap.id")
                                                           .append(INNER_JOIN_STRING)
                                                           .append(" eg_fm_applicantservicedocuments asd ON asd.applicantid = ap.id")
                                                           .append(INNER_JOIN_STRING)
                                                           .append(" eg_fm_servicedetails sd ON sd.applicantpersonalid = ap.id")
                                                           .toString();

    public String getApplicantPersonalSearchQuery(@NotNull ApplicantPersonalSearchCriteria criteria,
                                                  @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);
       
        if(criteria.getIds()!=null){
        	addIdsFilter("ap.id", criteria.getIds(), query, preparedStmtValues);
        }
        if(criteria.getFilecode()!=null){
        	
            addIdsFilter("sd.filecode", criteria.getFilecode(), query, preparedStmtValues);
        }
       
        return query.toString();
    }
}

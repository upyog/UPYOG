package org.egov.filemgmnt.repository.querybuilder;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPersonalQueryBuilder extends BaseQueryBuilder {

    private static final String QUERY = new StringBuilder().append(" SELECT ap.id, ap.aadhaarno, ap.email, ap.firstname, ap.lastname, ap.title, ap.mobileno, ap.tenantid")
                                                           .append("     , ap.createdby, ap.createdat, ap.lastmodifiedby, ap.lastmodifiedat")
                                                           .append(" FROM eg_fm_applicantpersonal ap")
                                                           .toString();

    public String getApplicantPersonalSearchQuery(@NotNull ApplicantPersonalSearchCriteria criteria,
                                                  @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);

        addIdsFilter("ap.id", criteria.getIds(), query, preparedStmtValues);

        return query.toString();
    }
}

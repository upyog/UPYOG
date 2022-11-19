package org.egov.filemgmnt.repository.querybuilder;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class ApplicantPersonalQueryBuilder extends BaseQueryBuilder {
    private static final String QUERY = new StringBuilder().append(" SELECT ap.id, ap.aadhaarno, ap.email, ap.firstname, ap.lastname, ap.title, ap.mobileno, ap.tenantid")
                                                           .append("   , ap.createdby, ap.createdtime, ap.lastmodifiedby, ap.lastmodifiedtime")
                                                           .append(" FROM eg_fm_applicantpersonal ap")
                                                           .append(" INNER JOIN eg_fm_applicantaddress apa ON apa.applicantpersonalid = ap.id")
                                                           .append(" INNER JOIN eg_fm_applicantdocument ad ON ad.applicantpersonalid = ap.id")
                                                           .append(" INNER JOIN eg_fm_applicantservicedocument asd ON asd.applicantpersonalid = ap.id")
                                                           .append(" INNER JOIN eg_fm_servicedetail sd ON sd.applicantpersonalid = ap.id")
                                                           .append(" INNER JOIN eg_fm_filedetail fd ON fd.servicedetailsid = sd.id")
                                                           .toString();

    public String getApplicantPersonalSearchQuery(@NotNull ApplicantPersonalSearchCriteria criteria,
                                                  @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);

        addFilter("ap.id", criteria.getId(), query, preparedStmtValues);
        addFilter("fd.filecode", criteria.getFileCode(), query, preparedStmtValues);
        addFilter("ap.tenantid", criteria.getTenantId(), query, preparedStmtValues);
        addFilter("ap.aadhaarno", criteria.getAadhaarNo(), query, preparedStmtValues);
        addDateRangeFilter("fd.filearisingdate",
                           criteria.getFromDate(),
                           criteria.getToDate(),
                           query,
                           preparedStmtValues);

//        if (criteria.getFromDate() != null) {
//            addWhereClause(preparedStmtValues, query);
//            query.append(" fd.filearisingdate >= ?");
//            preparedStmtValues.add(criteria.getFromDate());
//        }

        return query.toString();
    }

}

package org.egov.filemgmnt.repository.querybuilder;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.ServiceDetailsSearchCriteria;
import org.springframework.stereotype.Component;

@Component

public class ServiceDetailsQueryBuilder extends BaseQueryBuilder {

    private static final String QUERY = new StringBuilder().append(" SELECT aps.id, aps.applicantpersonalid, aps.serviceid, aps.servicecode, aps.businessservice, aps.workflowcode")
                                                           .append("   , aps.createdby, aps.createddate, aps.lastmodifiedby, aps.lastmodifieddate")
                                                           .append(" FROM eg_fm_servicedetails aps")
                                                           .toString();

    public String getServiceDetailsSearchQuery(@NotNull ServiceDetailsSearchCriteria criteria,
                                               @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);

        addFilters("ap.id", criteria.getIds(), query, preparedStmtValues);

        return query.toString();
    }

}

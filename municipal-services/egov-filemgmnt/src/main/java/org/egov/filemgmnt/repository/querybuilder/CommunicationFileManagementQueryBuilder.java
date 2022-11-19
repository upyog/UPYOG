package org.egov.filemgmnt.repository.querybuilder;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.CommunicationFileSearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class CommunicationFileManagementQueryBuilder extends BaseQueryBuilder {

    private static final String QUERY = new StringBuilder().append(" id, subjecttypeid, senderid, priorityid, filestoreid")
                                                           .append("     , details, createdby, createddate, lastmodifiedby, lastmodifieddate")
                                                           .append(" FROM eg_fm_communicationfile cf")
                                                           .toString();

    public String getCommunicationFileSearchQuery(@NotNull CommunicationFileSearchCriteria criteria,
                                                  @NotNull List<Object> preparedStmtValues, Boolean isCount) {

        StringBuilder query = new StringBuilder(QUERY);

        addFilters("cf.id", criteria.getIds(), query, preparedStmtValues);
        return query.toString();
    }

}

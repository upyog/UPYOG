package org.ksmart.birth.common.repository.builder;

import lombok.extern.slf4j.Slf4j;
import org.ksmart.birth.birthcommon.model.certificate.CertificateCriteria;
import org.ksmart.birth.web.model.SearchCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
@Slf4j
@Component
public class CertificateQueryBuilder extends BaseQueryBuilder {
    private static final String QUERY=new StringBuilder().append("SELECT id, registrationno, createdby, createdtime, registrydetailsid, " )
            .append("lastmodifiedtime, lastmodifiedby, filestoreid, status, additionaldetail, embeddedurl, dateofissue, tenantid, applicationid, ack_no, certificateno")
            .append(" FROM public.eg_birth_certificate_request ").toString();


    public String getCertificateSearchQuery(@NotNull CertificateCriteria criteria, @NotNull List<Object> preparedStmtValues) {
        StringBuilder query = new StringBuilder().append(QUERY);
        prepareSearchCriteria(criteria, query, preparedStmtValues);
        prepareOrderBy(criteria, query, preparedStmtValues);
        return query.toString();
    }

    public StringBuilder prepareSearchCriteria(@NotNull CertificateCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
        addFilter("id", criteria.getId(), query, preparedStmtValues);
        addFilter("registrationno", criteria.getRegistrationNo(), query, preparedStmtValues);
        addFilter("registrydetailsid", criteria.getRegistrationId(), query, preparedStmtValues);
        addFilter("ack_no", criteria.getApplicationNumber(), query, preparedStmtValues);
        return query;

    }

    public StringBuilder prepareOrderBy(@NotNull CertificateCriteria criteria, StringBuilder query, @NotNull List<Object> preparedStmtValues) {
        StringBuilder orderBy = new StringBuilder();
        addOrderByColumns("createdtime",null, orderBy);

        addOrderToQuery(orderBy, query);
        addLimitAndOffset(criteria.getOffset(),criteria.getLimit(), query, preparedStmtValues);
        return query;
    }
}

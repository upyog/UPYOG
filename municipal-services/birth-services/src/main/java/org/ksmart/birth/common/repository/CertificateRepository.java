package org.ksmart.birth.common.repository;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.ksmart.birth.birthcommon.model.certificate.CertificateCriteria;
import org.ksmart.birth.birthregistry.model.BirthCertificate;
import org.ksmart.birth.common.repository.builder.CertificateQueryBuilder;
import org.ksmart.birth.common.repository.rowmapper.CertificateRowMapper;
import org.ksmart.birth.newbirth.repository.rowmapper.BirthApplicationRowMapper;
import org.ksmart.birth.utils.BirthConstants;
import org.ksmart.birth.utils.enums.ErrorCodes;
import org.ksmart.birth.web.model.SearchCriteria;
import org.ksmart.birth.web.model.birthnac.certificate.CertificateDetails;
import org.ksmart.birth.web.model.newbirth.NewBirthApplication;
import org.ksmart.birth.web.model.newbirth.NewBirthDetailRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class CertificateRepository {
    private final CertificateRowMapper rowMapper;
    private final JdbcTemplate jdbcTemplate;
    private final CertificateQueryBuilder queryBuilder;

    @Autowired
    CertificateRepository(JdbcTemplate jdbcTemplate, CertificateRowMapper rowMapper, CertificateQueryBuilder queryBuilder) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
        this.queryBuilder = queryBuilder;
    }

    public List<BirthCertificate> searchBirthDetails(CertificateCriteria criteria) {
        List<Object> preparedStmtValues = new ArrayList<>();
        String query = queryBuilder.getCertificateSearchQuery(criteria, preparedStmtValues);
        if (preparedStmtValues.size() == 0) {
            throw new CustomException(ErrorCodes.NOT_FOUND.getCode(), "No result found.");
        } else {
            List<BirthCertificate> result = jdbcTemplate.query(query, preparedStmtValues.toArray(), rowMapper);
            return result;
        }
    }
}

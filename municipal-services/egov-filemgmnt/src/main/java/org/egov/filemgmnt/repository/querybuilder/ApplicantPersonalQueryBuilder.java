package org.egov.filemgmnt.repository.querybuilder;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.egov.filemgmnt.web.models.ApplicantPersonalSearchCriteria;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApplicantPersonalQueryBuilder extends BaseQueryBuilder {
	private static final String QUERY = new StringBuilder().append(
			" SELECT ap.id, ap.aadhaarno, ap.email, ap.firstname, ap.lastname, ap.title, ap.mobileno, ap.tenantid")
			.append("     , ap.createdby, ap.createdtime, ap.lastmodifiedby, ap.lastmodifiedtime")
			.append(" FROM eg_fm_applicantpersonal ap")
			.append(" INNER JOIN eg_fm_applicantaddress apa ON apa.applicantpersonalid = ap.id")
			.append(" INNER JOIN eg_fm_applicantdocuments ad ON ad.applicantpersonalid = ap.id")
			.append(" INNER JOIN eg_fm_applicantservicedocuments asd ON asd.applicantid = ap.id")
			.append(" INNER JOIN eg_fm_servicedetails sd ON sd.applicantpersonalid = ap.id")
			.append(" INNER JOIN eg_fm_filedetails fd ON fd.servicedetailsid = sd.id").toString();

	public String getApplicantPersonalSearchQuery(@NotNull ApplicantPersonalSearchCriteria criteria,
			@NotNull List<Object> preparedStmtValues, Boolean isCount) {

		StringBuilder query = new StringBuilder(QUERY);

		addFilters("ap.id", criteria.getIds(), query, preparedStmtValues);
		addFilters("fd.filecode", criteria.getFileCodes(), query, preparedStmtValues);
		log.info("query  " + query);
		return query.toString();
	}
}

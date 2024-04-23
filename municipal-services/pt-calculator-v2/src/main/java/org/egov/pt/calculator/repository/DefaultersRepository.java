package org.egov.pt.calculator.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.calculator.repository.rowmapper.DefaultersRowMapper;
import org.egov.pt.calculator.service.MasterDataService;
import org.egov.pt.calculator.web.models.DefaultersInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;

@Repository
@Slf4j
public class DefaultersRepository {

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private DefaultersRowMapper defaultersRowMapper;

	@Autowired
	private MasterDataService masterDataService;

	private static final String INNER_QUERY = "select pt.propertyid,usr.name ownername,usr.mobilenumber,sum(dd.taxamount - dd.collectionamount) balance from eg_pt_property pt,eg_pt_owner ownr,eg_user usr,egbs_demanddetail_v1 dd, egbs_demand_v1 d "
			+ " where ownr.propertyid = pt.id and ownr.tenantid=pt.tenantid and usr.uuid=ownr.userid and dd.demandid=d.id and d.consumercode = pt.propertyid and d.tenantid = pt.tenantid and pt.status='ACTIVE' and d.status = 'ACTIVE'";
	private static final String OUTER_QUERY = "select result.propertyid,result.ownername,result.mobilenumber,result.balance from ({duequery}) as result where result.balance > 0";

	private static final String GROUP_BY_CLAUSE = " group by pt.propertyid,usr.name,usr.mobilenumber";


	public List<DefaultersInfo> fetchAllDefaulterDetailsForFY(Long fromDate, Long toDate, String tenantId) {

		final Map<String, Object> params = new HashMap<>();
		List<DefaultersInfo> defaultersInfo = new ArrayList<>();
		StringBuilder dueQuery = new StringBuilder(INNER_QUERY);
		if (fromDate != null && toDate != null) {
			dueQuery.append(" and d.taxperiodfrom >=:fromDate and d.taxperiodto <=:toDate ");
			params.put("fromDate", fromDate);
			params.put("toDate", toDate);
		}
		dueQuery.append(" and pt.tenantId=:tenantId");
		params.put("tenantId", tenantId);
		dueQuery.append(GROUP_BY_CLAUSE);

		log.info("Due query " + dueQuery.toString());
		log.info("Params " + params);

		String mainQuery = OUTER_QUERY.replace("{duequery}", dueQuery);
		try {
			defaultersInfo = namedParameterJdbcTemplate.query(mainQuery, params, defaultersRowMapper);
		} catch (Exception ex) {
			log.info("exception while fetching PT defaulters-Due SMS " + ex.getMessage());
		}
		return defaultersInfo;
	}

}

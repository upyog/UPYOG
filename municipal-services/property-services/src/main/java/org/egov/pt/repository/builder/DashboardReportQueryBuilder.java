package org.egov.pt.repository.builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.ObjectUtils;
import org.egov.pt.models.DashboardDataSearch;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DashboardReportQueryBuilder {

	public static final String TOTAL_PROPERTIES_REGISTERED = "SELECT \r\n"
			+ "    epp.propertyid,\r\n"
			+ "    epp.tenantid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.status IN ('ACTIVE', 'INWORKFLOW')\r\n"
			+ "";

	public static final String PROPERTIES_PENDING_WITH = "WITH latest_actions AS (\r\n" + "  SELECT \r\n"
			+ "    ewpv.businessid,\r\n" + "    ewpv.\"action\",\r\n" + "    ewpv.tenantid,\r\n"
			+ "    epp.id AS property_id,\r\n" + "    epp.propertyid AS propertyid,\r\n" + "    epadd.ward_no,\r\n"
			+ "    ROW_NUMBER() OVER (\r\n" + "      PARTITION BY ewpv.businessid \r\n"
			+ "      ORDER BY ewpv.lastmodifiedtime DESC\r\n" + "    ) AS rn\r\n"
			+ "  FROM eg_wf_processinstance_v2 ewpv\r\n" + "  JOIN eg_pt_property epp \r\n"
			+ "    ON epp.acknowldgementnumber = ewpv.businessid\r\n" + "  JOIN eg_pt_address epadd \r\n"
			+ "    ON epp.id = epadd.propertyid\r\n" + "  WHERE ewpv.\"action\" != '' and epp.status = 'INWORKFLOW' \r\n "
			+ "  /*FILTER_CONDITIONS*/\r\n" + ")\r\n" + "\r\n" + "SELECT \r\n" + "  CASE\r\n"
			+ "    WHEN action IN ('SENDBACKTOCITIZEN') THEN 'PENDINGWITHCITIZEN'\r\n"
			+ "    WHEN action IN ('REOPEN', 'SENDBACKTODOCKVERIFIER', 'SENDBACKTODOCVERIFIER', 'OPEN') THEN 'PENDINGWITHDOCVERIFIER'\r\n"
			+ "    WHEN action = 'VERIFY' THEN 'PENDINGWITHFILEDVERIFIER'\r\n"
			+ "    WHEN action = 'REJECT' THEN 'REJECTED'\r\n"
			+ "    WHEN action = 'FORWARD' THEN 'PENDINGWITHAPPROVER'\r\n"
			+ "    WHEN action = 'APPROVE' THEN 'APPROVED'\r\n" + "  END AS action_st,\r\n"
			+ "  STRING_AGG(latest_actions.propertyid || ':' || latest_actions.tenantid,', ') AS property_ids\r\n" + "FROM latest_actions\r\n"
			+ "WHERE rn = 1\r\n" + "GROUP BY action_st";
	
	public static final String PROPERTIES_PENDING_WITH_NEW = 
	        "WITH latest_actions AS (\r\n"
	        	      + "  SELECT \r\n"
	        	      + "    ewpv.businessid,\r\n"
	        	      + "    ewpv.\"action\",\r\n"
	        	      + "    ewpv.tenantid,\r\n"
	        	      + "    epp.id AS property_id,\r\n"
	        	      + "    epp.propertyid AS propertyid,\r\n"
	        	      + "    epadd.ward_no,\r\n"
	        	      + "    ROW_NUMBER() OVER (\r\n"
	        	      + "      PARTITION BY ewpv.businessid \r\n"
	        	      + "      ORDER BY ewpv.lastmodifiedtime DESC\r\n"
	        	      + "    ) AS rn\r\n"
	        	      + "  FROM eg_wf_processinstance_v2 ewpv\r\n"
	        	      + "  JOIN eg_pt_property epp \r\n"
	        	      + "    ON epp.acknowldgementnumber = ewpv.businessid\r\n"
	        	      + "  JOIN eg_pt_address epadd \r\n"
	        	      + "    ON epp.id = epadd.propertyid\r\n"
	        	      + "  WHERE ewpv.\"action\" != '' AND epp.status = 'INWORKFLOW'\r\n"
	        	      + "  /*FILTER_CONDITIONS*/\r\n"
	        	      + "  order by ewpv.lastmodifiedtime desc\r\n"
	        	      + "),\r\n"
	        	      + "mapped_actions AS (\r\n"
	        	      + "  SELECT \r\n"
	        	      + "    CASE\r\n"
	        	      + "      WHEN action = 'SENDBACKTOCITIZEN' THEN 'PENDINGWITHCITIZEN'\r\n"
	        	      + "      WHEN action IN ('REOPEN', 'SENDBACKTODOCKVERIFIER', 'SENDBACKTODOCVERIFIER', 'OPEN') THEN 'PENDINGWITHDOCVERIFIER'\r\n"
	        	      + "      WHEN action = 'VERIFY' THEN 'PENDINGWITHFILEDVERIFIER'\r\n"
	        	      + "      WHEN action = 'REJECT' THEN 'REJECTED'\r\n"
	        	      + "      WHEN action = 'FORWARD' THEN 'PENDINGWITHAPPROVER'\r\n"
	        	      + "      WHEN action = 'APPROVE' THEN 'APPROVED'\r\n"
	        	      + "    END AS action_st,\r\n"
	        	      + "    propertyid,\r\n"
	        	      + "    tenantid,\r\n"
	        	      + "    ROW_NUMBER() OVER (\r\n"
	        	      + "      PARTITION BY\r\n"
	        	      + "        CASE\r\n"
	        	      + "          WHEN action = 'SENDBACKTOCITIZEN' THEN 'PENDINGWITHCITIZEN'\r\n"
	        	      + "          WHEN action IN ('REOPEN', 'SENDBACKTODOCKVERIFIER', 'SENDBACKTODOCVERIFIER', 'OPEN') THEN 'PENDINGWITHDOCVERIFIER'\r\n"
	        	      + "          WHEN action = 'VERIFY' THEN 'PENDINGWITHFILEDVERIFIER'\r\n"
	        	      + "          WHEN action = 'REJECT' THEN 'REJECTED'\r\n"
	        	      + "          WHEN action = 'FORWARD' THEN 'PENDINGWITHAPPROVER'\r\n"
	        	      + "          WHEN action = 'APPROVE' THEN 'APPROVED'\r\n"
	        	      + "        END\r\n"
	        	      + "      ORDER BY propertyid\r\n"
	        	      + "    ) AS action_rn\r\n"
	        	      + "  FROM latest_actions\r\n"
	        	      + "  WHERE rn = 1\r\n"
	        	      + ")\r\n"
	        	      + "SELECT \r\n"
	        	      + "  action_st,\r\n"
	        	      + "  STRING_AGG(propertyid || ':' || tenantid, ', ') AS property_ids\r\n"
	        	      + "FROM mapped_actions\r\n"
	        	      + "WHERE action_rn > :offset\r\n"
	        	      + "  AND action_rn <= (:offset + :limit)\r\n"
	        	      + "GROUP BY action_st";

	public static final String PROPERTIES_APPROVED = "select\r\n"
			+ "	ep.propertyid,\r\n"
			+ "	ep.tenantid\r\n"
			+ "from\r\n"
			+ "	eg_wf_processinstance_v2 ewpv\r\n"
			+ "join eg_pt_property ep\r\n"
			+ "    on\r\n"
			+ "	ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "join eg_pt_address epadd\r\n"
			+ "    on\r\n"
			+ "	ep.id = epadd.propertyid\r\n"
			+ "where\r\n"
			+ "	ewpv.\"action\" = 'APPROVE'\r\n"
			+ "	and ep.status = 'ACTIVE'\r\n"
			+ "  /*FILTER_CONDITIONS*/\r\n"
			+ "group by\r\n"
			+ "	ep.propertyid,\r\n"
			+ "	ep.tenantid,\r\n"
			+ "	ep.lastmodifiedtime\r\n"
			+ "order by\r\n"
			+ "	ep.lastmodifiedtime desc\r\n"
			+ "";
	
	public static final String PROPERTIES_REJECTED = "SELECT\r\n"
			+ "    ep.propertyid,\r\n"
			+ "    ep.tenantid\r\n"
			+ "FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "JOIN eg_pt_property ep\r\n"
			+ "    ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "JOIN eg_pt_address epadd\r\n"
			+ "    ON ep.id = epadd.propertyid\r\n"
			+ "WHERE ewpv.\"action\" = 'REJECT'\r\n"
			+ "  AND ep.status = 'INACTIVE'\r\n"
			+ "  /*FILTER_CONDITIONS*/\r\n"
			+ "GROUP BY\r\n"
			+ "    ep.propertyid,\r\n"
			+ "    ep.tenantid,\r\n"
			+ "    ewpv.lastmodifiedtime \r\n"
			+ "order by ewpv.lastmodifiedtime desc\r\n"
			+ "";

	public static final String PROPERTIES_SELF_ASSESSED = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid\r\n"
			+ "FROM eg_pt_asmt_assessment epaa\r\n" + "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE epp.status = 'ACTIVE'";

	public static final String PROPERTIES_PENDING_SELF_ASSESSMENT = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.propertyid NOT IN (\r\n" + "    SELECT DISTINCT epaa.propertyid\r\n"
			+ "    FROM eg_pt_asmt_assessment epaa AND epp.status = 'ACTIVE'\r\n" + ")";

	public static final String PROPERTIES_PAID = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid,ept.txn_id as txn_id ,ept.txn_amount as txn_amount\r\n"
			+ "FROM eg_pg_transactions ept\r\n" + "JOIN eg_pt_property epp ON ept.consumer_code = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE epp.status = 'ACTIVE' ";

	public static final String PROPERTIES_WITH_APPEAL_SUBMITTED = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pt_property_appeal eppa ON epp.propertyid = eppa.propertyid\r\n"
			+ "JOIN eg_wf_processinstance_v2 ewpv ON epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "WHERE ewpv.\"action\" = 'OPEN' AND epp.status = 'ACTIVE'\r\n" + "";

	public static final String APPEALS_PENDING = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pt_property_appeal eppa ON epp.propertyid = eppa.propertyid\r\n"
			+ "JOIN eg_wf_processinstance_v2 ewpv ON epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "WHERE ewpv.\"action\" = 'OPEN' AND epp.status = 'ACTIVE'\r\n" + "";

	public static final String TOTAL_TAX_COLLECTED = "SELECT epp.propertyid as propertyid,epp.tenantid as tenantid\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS' AND epp.status = 'ACTIVE'";

	public static final String PROPERTY_TAX_SHARE = "SELECT SUM(ep.totalamountpaid) * 5.0 / 8 as PROPERTY_TAX\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PENALTY_SHARE = "WITH paid_bills AS (\r\n"
			+ "    SELECT \r\n"
			+ "        bd.demandid, \r\n"
			+ "        SUM(bd.totalamount) AS billsum,b.consumercode,b.tenantid,pay.lastmodifiedtime\r\n"
			+ "    FROM egbs_bill_v1 b\r\n"
			+ "    JOIN egbs_billdetail_v1 bd ON b.id = bd.billid\r\n"
			+ "    JOIN eg_pt_property p ON p.propertyid = bd.consumercode\r\n"
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "    WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "      AND p.status = 'ACTIVE'\r\n"
			+ "      AND b.status = 'PAID'\r\n"
			+ "     {DATE_FILTER}\r\n"
			+ "      {TENANT_FILTER}\r\n"
			+ "      {WARD_FILTER} \r\n"
			+ "    GROUP BY bd.demandid,b.consumercode,b.tenantid,pay.lastmodifiedtime\r\n"
			+ "    HAVING SUM(bd.totalamount) > 0\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "total_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        d.id AS demandid, \r\n"
			+ "        SUM(dd.taxamount) AS demandsum\r\n"
			+ "    FROM egbs_demand_v1 d\r\n"
			+ "    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid\r\n"
			+ "    JOIN eg_pt_property p ON p.propertyid = d.consumercode\r\n"
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "    WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "      AND p.status = 'ACTIVE'\r\n"
			+ "     {DATE_FILTER}\r\n"
			+ "      {TENANT_FILTER}\r\n"
			+ "      {WARD_FILTER} \r\n"
			+ "    GROUP BY d.id\r\n"
			+ "    HAVING SUM(dd.taxamount) > 0\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "penalty_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        d.id AS demandid, \r\n"
			+ "        SUM(dd.taxamount) AS penalty_amount\r\n"
			+ "    FROM egbs_demand_v1 d\r\n"
			+ "    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid\r\n"
			+ "    JOIN eg_pt_property p ON p.propertyid = d.consumercode\r\n"
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY')\r\n"
			+ "      AND dd.taxamount > 0\r\n"
			+ "      AND t.txn_status = 'SUCCESS'\r\n"
			+ "      AND p.status = 'ACTIVE'\r\n"
			+ "      {DATE_FILTER}\r\n"
			+ "      {TENANT_FILTER}\r\n"
			+ "      {WARD_FILTER}\r\n"
			+ "    GROUP BY d.id\r\n"
			+ ")\r\n"
			+ "\r\n"
			+ "SELECT filtered.consumercode AS propertyid, filtered.tenantid, todaypenaltycollection\r\n"
			+ "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "        SUM(pd.penalty_amount) AS todaypenaltycollection,\r\n"
			+ "        pd.demandid,\r\n"
			+ "        pb.demandid,\r\n"
			+ "        td.demandid,\r\n"
			+ "        pb.consumercode,\r\n"
			+ "        pb.tenantid,pb.lastmodifiedtime\r\n"
			+ "    FROM penalty_demand pd\r\n"
			+ "    JOIN paid_bills pb ON pd.demandid = pb.demandid\r\n"
			+ "    JOIN total_demand td ON pd.demandid = td.demandid\r\n"
			+ "    GROUP BY pd.demandid, pb.demandid, td.demandid ,pb.consumercode,pb.tenantid,pb.lastmodifiedtime\r\n"
			+ "    HAVING SUM(pb.billsum) >= SUM(td.demandsum) order by pb.lastmodifiedtime\r\n"
			+ ") AS filtered";

	public static final String INTEREST_SHARE = "SELECT ebv2.consumercode AS consumercode\r\n"
			+ "FROM egbs_bill_v1 ebv\r\n" + "JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n" + "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n" + "  AND ebv2.interestonamount > 0 AND epp.status = 'ACTIVE'";

	public static final String ADVANCE_SHARE = "SELECT edv.consumercode as consumercode\r\n"
			+ "FROM egbs_demand_v1 edv\r\n" + "JOIN egbs_billdetail_v1 ebv2 ON edv.id = ebv2.demandid\r\n"
			+ "JOIN egbs_bill_v1 ebv ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n" + "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n" + "  AND edv.advanceamount > 0 AND epp.status = 'ACTIVE'";

	public String getTotalPropertyRegisteredQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(TOTAL_PROPERTIES_REGISTERED);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND epp.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append(" order by epp.lastmodifiedtime desc ");
		if(!ObjectUtils.isEmpty(dashboardDataSearch.getLimit()) && !ObjectUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET  "+dashboardDataSearch.getOffset());
			filter.append(" LIMIT "+dashboardDataSearch.getLimit());
		}
		
		return filter.toString();
	}

	public String getTotalPropertyPendingWithQuery(DashboardDataSearch dashboardDataSearch) {
		StringBuilder filterBuilder = new StringBuilder();

		long fromEpoch, toEpoch;
		if (StringUtils.hasText(dashboardDataSearch.getFromDate())
				&& StringUtils.hasText(dashboardDataSearch.getToDate())) {
			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");
			String today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
			toEpoch = getEndOfDayEpochMillis(today);
		}
		filterBuilder.append(" AND ewpv.lastmodifiedtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (StringUtils.hasText(dashboardDataSearch.getTenantid())) {
			filterBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (StringUtils.hasText(dashboardDataSearch.getWard())) {
			filterBuilder.append(" AND epadd.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filterBuilder.append(" AND epadd.ward_no != ''");
		}
		
		String finalQuery = PROPERTIES_PENDING_WITH_NEW.replace("/*FILTER_CONDITIONS*/", filterBuilder.toString());
		if(!ObjectUtils.isEmpty(dashboardDataSearch.getLimit()) && !ObjectUtils.isEmpty(dashboardDataSearch.getOffset()))
		finalQuery = finalQuery
		        .replace(":offset", String.valueOf(dashboardDataSearch.getOffset()))
		        .replace(":limit", String.valueOf(dashboardDataSearch.getLimit()));

		
		
		return finalQuery;
	}

	public String getTotalPropertyApprovedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder();

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ewpv.lastmodifiedtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epadd.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epadd.ward_no != ''");
		}

		
		 StringBuilder finalQuery = new StringBuilder(
		            PROPERTIES_APPROVED.replace("/*FILTER_CONDITIONS*/", filter.toString())
		    );

		   
		    if (dashboardDataSearch.getLimit() != null
		            && dashboardDataSearch.getOffset() != null) {

		        finalQuery.append(" OFFSET ").append(dashboardDataSearch.getOffset());
		        finalQuery.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		    }

		    return finalQuery.toString();
	}
	
	public String getTotalPropertyRejectedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder();

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ewpv.lastmodifiedtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epadd.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epadd.ward_no != ''");
		}
		
		 StringBuilder finalQuery = new StringBuilder(
		            PROPERTIES_REJECTED.replace("/*FILTER_CONDITIONS*/", filter.toString())
		    );

		   
		    if (dashboardDataSearch.getLimit() != null
		            && dashboardDataSearch.getOffset() != null) {

		        finalQuery.append(" OFFSET ").append(dashboardDataSearch.getOffset());
		        finalQuery.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		    }
		return finalQuery.toString();
	}

	public String getTotalPropertySelfassessedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(PROPERTIES_SELF_ASSESSED);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND epaa.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append(" order by epaa.lastmodifiedtime desc ");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}
		
		return filter.toString();
	}

	public String getTotalPropertyPendingselfAssessmentQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(PROPERTIES_PENDING_SELF_ASSESSMENT);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND epp.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append("order by epp.lastmodifiedtime desc");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getTotalPropertyPaidQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(PROPERTIES_PAID);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append("order by ep.lastmodifiedtime desc");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getTotalPropertyAppealSubmitedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(PROPERTIES_WITH_APPEAL_SUBMITTED);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ewpv.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append("order by eppa.lastmodifiedtime desc");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getTotalPropertyAppealPendingQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(APPEALS_PENDING);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ewpv.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append("order by eppa.lastmodifiedtime desc");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getTotalTaxCollectedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(TOTAL_TAX_COLLECTED);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append("order by ep.lastmodifiedtime desc");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getPropertyTaxShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(PROPERTY_TAX_SHARE);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getPenaltyShareQuery(DashboardDataSearch dashboardDataSearch) {

		String query = PENALTY_SHARE;

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {
			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");
			toEpoch = getEndOfDayEpochMillis(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		}

		String dateFilter = " AND pay.createdtime BETWEEN " + fromEpoch + " AND " + toEpoch;
		String tenantFilter = !StringUtils.isEmpty(dashboardDataSearch.getTenantid())
				? " AND p.tenantid = '" + dashboardDataSearch.getTenantid() + "'"
				: "";
		String wardFilter = !StringUtils.isEmpty(dashboardDataSearch.getWard())
				? " AND a.ward_no = '" + dashboardDataSearch.getWard() + "'"
				: " AND a.ward_no != ''";

		query = query.replace("{DATE_FILTER}", dateFilter).replace("{TENANT_FILTER}", tenantFilter)
				.replace("{WARD_FILTER}", wardFilter);

		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			query.concat(" OFFSET "+dashboardDataSearch.getOffset());
			query.concat(" LIMIT "+dashboardDataSearch.getLimit());
		}
		
		return query;
	}

	public String getInterestShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(INTEREST_SHARE);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}
		
		filter.append(" order by pay.lastmodifiedtime desc ");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getAdvanceShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(ADVANCE_SHARE);

		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {

			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");

			LocalDate currentDate = LocalDate.now();
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
			String formattedDate = currentDate.format(formatter);

			toEpoch = getEndOfDayEpochMillis(formattedDate);
		}
		filter.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			filter.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			filter.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filter.append(" AND epa.ward_no != ''");
		}

		filter.append(" order by pay.lastmodifiedtime desc ");
		if(StringUtils.isEmpty(dashboardDataSearch.getLimit()) && StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}
		
		return filter.toString();
	}

	public static long getStartOfDayEpochMillis(String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.parse(dateStr, formatter);
		ZonedDateTime startOfDay = localDate.atStartOfDay(ZoneId.of("UTC"));
		return startOfDay.toInstant().toEpochMilli();
	}

	public static long getEndOfDayEpochMillis(String dateStr) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		LocalDate localDate = LocalDate.parse(dateStr, formatter);
		ZonedDateTime endOfDay = localDate.atTime(LocalTime.MAX).atZone(ZoneId.of("UTC"));
		return endOfDay.toInstant().toEpochMilli();
	}

}

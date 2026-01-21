package org.egov.pt.repository.builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.models.DashboardDataSearch;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DashboardDataQueryBuilder {

	public static final String TOTAL_PROPERTIES_REGISTERED = "select count(epp.propertyid) from eg_pt_property epp join eg_pt_address epa on epp.id=epa.propertyid where epp.status in ('ACTIVE','INWORKFLOW')";

	public static final String PROPERTIES_PENDING_WITH =
		    "WITH latest_actions AS (\n" +
		    "  SELECT \n" +
		    "    ewpv.businessid,\n" +
		    "    ewpv.\"action\",\n" +
		    "    ewpv.tenantid,\n" +
		    "    epp.id AS property_id,\n" +
		    "    epadd.ward_no,\n" +
		    "    ROW_NUMBER() OVER (PARTITION BY ewpv.businessid ORDER BY ewpv.lastmodifiedtime DESC) AS rn\n" +
		    "  FROM eg_wf_processinstance_v2 ewpv\n" +
		    "  JOIN eg_pt_property epp ON epp.acknowldgementnumber = ewpv.businessid\n" +
		    "  JOIN eg_pt_address epadd ON epp.id = epadd.propertyid\n" +
		    "  WHERE ewpv.\"action\" != ''\n" +
		    "  AND epp.status = 'INWORKFLOW'\n "+
		    "  /*FILTER_CONDITIONS*/\n" +
		    ")\n" +
		    "SELECT \n" +
		    "  CASE\n" +
		    "    WHEN action IN ('SENDBACKTOCITIZEN') THEN 'PENDINGWITHCITIZEN'\n" +
		    "    WHEN action IN ('REOPEN','SENDBACKTODOCKVERIFIER','SENDBACKTODOCVERIFIER','OPEN') THEN 'PENDINGWITHDOCVERIFIER'\n" +
		    "    WHEN action = 'VERIFY' THEN 'PENDINGWITHFILEDVERIFIER'\n" +
		    "    WHEN action = 'REJECT' THEN 'REJECTED'\n" +
		    "    WHEN action = 'FORWARD' THEN 'PENDINGWITHAPPROVER'\n" +
		    "    WHEN action = 'APPROVE' THEN 'APPROVED'\n" +
		    "  END AS action_st,\n" +
		    "  COUNT(*) AS count\n" +
		    "FROM latest_actions\n" +
		    "WHERE rn = 1\n" +
		    "GROUP BY action_st";


	public static final String PROPERTIES_APPROVED = "WITH approved AS (\r\n"
			+ "  SELECT \r\n"
			+ "    COUNT(ewpv.businessid) AS approved_count,\r\n"
			+ "    ep.tenantid,\r\n"
			+ "    epadd.ward_no\r\n"
			+ "  FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "  JOIN eg_pt_property ep \r\n"
			+ "    ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "  JOIN eg_pt_address epadd \r\n"
			+ "    ON ep.id = epadd.propertyid\r\n"
			+ "  WHERE ewpv.\"action\" = 'APPROVE' \r\n"
			+ "  AND ep.status = 'ACTIVE' \r\n"
			+ "  /*FILTER_CONDITIONS*/\n" 
			+ "  GROUP BY ep.tenantid, epadd.ward_no\r\n"
			+ ")\r\n"
			+ "SELECT SUM(approved.approved_count) AS total_approved_count\r\n"
			+ "FROM approved";
	
	public static final String PROPERTIES_REJECTED = "WITH rejected AS (\r\n"
			+ "  SELECT \r\n"
			+ "    COUNT(ewpv.businessid) AS rejected_count,\r\n"
			+ "    ep.tenantid,\r\n"
			+ "    epadd.ward_no\r\n"
			+ "  FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "  JOIN eg_pt_property ep \r\n"
			+ "    ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "  JOIN eg_pt_address epadd \r\n"
			+ "    ON ep.id = epadd.propertyid\r\n"
			+ "  WHERE ewpv.\"action\" = 'REJECT' \r\n"
			+ "  AND ep.status = 'INACTIVE' \r\n"
			+ "  /*FILTER_CONDITIONS*/\n" 
			+ "  GROUP BY ep.tenantid, epadd.ward_no\r\n"
			+ ")\r\n"
			+ "SELECT SUM(approved.rejected_count) AS total_rejected_count\r\n"
			+ "FROM rejected";

	public static final String PROPERTIES_SELF_ASSESSED = "select COUNT(epp.propertyid) AS count\r\n"
			+ "FROM eg_pt_asmt_assessment epaa\r\n" + "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE epp.status = 'ACTIVE'";
	
	public static final String PROPERTIES_PENDING_SELF_ASSESSMENT = "select COUNT(epp.propertyid) AS count\r\n"
			+ "from eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.propertyid not in (select distinct epaa.propertyid from eg_pt_asmt_assessment epaa) AND epp.status = 'ACTIVE'";

	public static final String PROPERTIES_PAID = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM eg_pg_transactions ept\r\n"
			+ "JOIN eg_pt_property epp ON ept.consumer_code = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN egcl_payment ep on ept.txn_id =ep.transactionnumber \r\n"
			+ "where epp.status = 'ACTIVE'";

	public static final String PROPERTIES_WITH_APPEAL_SUBMITTED = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM  eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid \r\n"
			+ "join eg_pt_property_appeal eppa on epp.propertyid =eppa.propertyid \r\n"
			+ "join eg_wf_processinstance_v2 ewpv on epp.acknowldgementnumber =ewpv.businessid \r\n"
			+ "where ewpv.\"action\" ='OPEN' AND epp.status = 'ACTIVE'";

	public static final String APPEALS_PENDING = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM  eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid \r\n"
			+ "join eg_pt_property_appeal eppa on epp.propertyid =eppa.propertyid \r\n"
			+ "join eg_wf_processinstance_v2 ewpv on epp.acknowldgementnumber =ewpv.businessid \r\n"
			+ "where ewpv.\"action\" ='OPEN' AND epp.status = 'ACTIVE'";
	
	public static final String TOTAL_TAX_COLLECTED="SELECT SUM(ep.totalamountpaid) AS todayscollection\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE ept.txn_status = 'SUCCESS' AND epp.status = 'ACTIVE'";
	
	public static final String TOTAL_TAX_COLLECTED_PROPERTIES ="SELECT \r\n"
			+ "   ept.txn_id , ept.txn_amount , ept.consumer_code , ept.tenant_id \r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept \r\n"
			+ "    ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep \r\n"
			+ "    ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE ept.txn_status = 'SUCCESS'\r\n"
			+ "  AND epp.status = 'ACTIVE'";
	
	public static final String PROPERTY_TAX_SHARE="SELECT SUM(ep.totalamountpaid) * 5.0 / 8 as PROPERTY_TAX\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE ept.txn_status = 'SUCCESS' AND epp.status = 'ACTIVE'";
	
	public static final String PENALTY_SHARE = 
			"WITH paid_bills AS ( " +
			"    SELECT bd.demandid, SUM(bd.totalamount) AS billsum " +
			"    FROM egbs_bill_v1 b " +
			"    JOIN egbs_billdetail_v1 bd ON b.id = bd.billid " +
			"    JOIN eg_pt_property p ON p.propertyid = bd.consumercode " +
			"    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"+
			"    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code " +
			"    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber " +
			"    WHERE t.txn_status = 'SUCCESS' AND p.status = 'ACTIVE' and b.status = 'PAID' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} " +
			"    GROUP BY bd.demandid HAVING SUM(bd.totalamount) > 0 " +
			"), " +
			"total_demand AS ( " +
			"    SELECT d.id AS demandid, SUM(dd.taxamount) AS demandsum " +
			"    FROM egbs_demand_v1 d " +
			"    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid " +
			"    JOIN eg_pt_property p ON p.propertyid = d.consumercode " +
			"    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"+
			"    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code " +
			"    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber " +
			"    WHERE t.txn_status = 'SUCCESS' AND p.status = 'ACTIVE' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} " +
			"    GROUP BY d.id HAVING SUM(dd.taxamount) > 0 " +
			"), " +
			"penalty_demand AS ( " +
			"    SELECT d.id AS demandid, SUM(dd.taxamount) AS penalty_amount " +
			"    FROM egbs_demand_v1 d " +
			"    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid " +
			"    JOIN eg_pt_property p ON p.propertyid = d.consumercode " +
			"    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"+
			"    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code " +
			"    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber " +
			"    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY') " +
			"    AND dd.taxamount > 0 AND t.txn_status = 'SUCCESS' AND p.status = 'ACTIVE' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} " +
			"    GROUP BY d.id " +
			") " +
			"SELECT SUM(todaypenaltycollection) AS total_penalty\r\n"
			+ "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "    SUM(pd.penalty_amount) AS todaypenaltycollection,pd.demandid,pb.demandid,td.demandid\r\n"
			+ "FROM penalty_demand pd\r\n"
			+ "JOIN paid_bills pb ON pd.demandid = pb.demandid\r\n"
			+ "JOIN total_demand td ON pd.demandid = td.demandid\r\n"
			+ "group by pd.demandid,pb.demandid,td.demandid\r\n"
			+ "HAVING SUM(pb.billsum) >= SUM(td.demandsum)\r\n"
			+ ") AS filtered";

	public static final String INTEREST_SHARE="SELECT SUM(ebv2.interestonamount) AS todayinterestcollection\r\n"
			+ "FROM egbs_bill_v1 ebv JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS' AND ebv.status = 'PAID' AND ebv2.interestonamount > 0 AND epp.status = 'ACTIVE'";
	
	public static final String ADVANCE_SHARE="SELECT sum(edv.advanceamount) as totalAdvance \r\n"
			+ "from egbs_demand_v1 edv \r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 ON edv.id = ebv2.demandid \r\n"
			+ "join egbs_bill_v1 ebv on ebv.id=ebv2.billid \r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS' AND ebv.status = 'PAID' AND edv.advanceamount > 0 AND epp.status = 'ACTIVE'\r\n"
			+ "";

	public static final String APPLICATION_DATA ="SELECT epp.propertyid, epp.tenantid \r\n"
		    +"FROM eg_pt_property epp \r\n"
		    +"JOIN eg_wf_processinstance_v2 ewpv \r\n"
		    +"ON epp.acknowldgementnumber = ewpv.businessid where 1=1 \r\n";

	public String getTotalPropertyRegisteredQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(TOTAL_PROPERTIES_REGISTERED);

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
		stringBuilder.append(" AND epp.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}
		return stringBuilder.toString();
	}

	public String getTotalPropertyPendingWithQuery(DashboardDataSearch dashboardDataSearch) {
	    StringBuilder filterBuilder = new StringBuilder();

	   
	    long fromEpoch, toEpoch;
	    if (StringUtils.hasText(dashboardDataSearch.getFromDate()) && StringUtils.hasText(dashboardDataSearch.getToDate())) {
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

	    
	    String finalQuery = PROPERTIES_PENDING_WITH.replace("/*FILTER_CONDITIONS*/", filterBuilder.toString());
	    return finalQuery;
	}

	
	public String getTotalPropertyApprovedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_APPROVED);

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
		stringBuilder.append(" AND ewpv.lastmodifiedtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epadd.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epadd.ward_no != ''");
		}
		
		String finalQuery = PROPERTIES_APPROVED.replace("/*FILTER_CONDITIONS*/", stringBuilder.toString());
		System.out.println("finalQuery::"+finalQuery);
		return finalQuery;
	}
	
	public String getTotalPropertyRejectedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_REJECTED);

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
		stringBuilder.append(" AND ewpv.lastmodifiedtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epadd.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epadd.ward_no != ''");
		}
		
		String finalQuery = PROPERTIES_REJECTED.replace("/*FILTER_CONDITIONS*/", stringBuilder.toString());
		System.out.println("finalQuery::"+finalQuery);
		return finalQuery;
	}
	
	public String getTotalPropertySelfassessedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_SELF_ASSESSED);

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
		stringBuilder.append(" AND epaa.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalPropertyPendingselfAssessmentQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_PENDING_SELF_ASSESSMENT);

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
		stringBuilder.append(" AND epp.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalPropertyPaidQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_PAID);

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
		stringBuilder.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalPropertyAppealSubmitedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTIES_WITH_APPEAL_SUBMITTED);

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
		stringBuilder.append(" AND ewpv.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalPropertyAppealPendingQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(APPEALS_PENDING);

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
		stringBuilder.append(" AND ewpv.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalTaxCollectedQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(TOTAL_TAX_COLLECTED);

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
		stringBuilder.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getTotalTaxCollectedPropertiesQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(TOTAL_TAX_COLLECTED_PROPERTIES);

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
		stringBuilder.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getPropertyTaxShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(PROPERTY_TAX_SHARE);

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
		stringBuilder.append(" AND ep.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getPenaltyShareQuery(DashboardDataSearch dashboardDataSearch) {

		String query = PENALTY_SHARE;

	    long fromEpoch, toEpoch;
	    if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate()) &&
	        !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {
	        fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
	        toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
	    } else {
	        fromEpoch = getStartOfDayEpochMillis("01-04-2025");
	        toEpoch = getEndOfDayEpochMillis(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
	    }

	    String dateFilter = " AND pay.createdtime BETWEEN " + fromEpoch + " AND " + toEpoch;
	    String tenantFilter = !StringUtils.isEmpty(dashboardDataSearch.getTenantid()) ?
	        " AND p.tenantid = '" + dashboardDataSearch.getTenantid() + "'" : "";
	    String wardFilter = !StringUtils.isEmpty(dashboardDataSearch.getWard()) ?
	        " AND a.ward_no = '" + dashboardDataSearch.getWard() + "'" : " AND a.ward_no != ''";

	    query = query.replace("{DATE_FILTER}", dateFilter)
	                 .replace("{TENANT_FILTER}", tenantFilter)
	                 .replace("{WARD_FILTER}", wardFilter);

	    return query;
	}
	
	public String getInterestShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(INTEREST_SHARE);

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
		stringBuilder.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getAdvanceShareQuery(DashboardDataSearch dashboardDataSearch) {

		StringBuilder stringBuilder = new StringBuilder(ADVANCE_SHARE);

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
		stringBuilder.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (!StringUtils.isEmpty(dashboardDataSearch.getTenantid())) {
			stringBuilder.append(" AND epp.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (!StringUtils.isEmpty(dashboardDataSearch.getWard())) {
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}

		return stringBuilder.toString();
	}
	
	public String getApplicationData(DashboardDataSearch dashboardDataSearch,RequestInfo requestInfo)
	{
		StringBuilder stringBuilder = new StringBuilder(APPLICATION_DATA);

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
		//stringBuilder.append(" AND ewpv.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);
		stringBuilder.append(" AND ewpv.assigner ='").append(requestInfo.getUserInfo().getUuid()).append("'");
		if(!StringUtils.isEmpty(dashboardDataSearch.getStatus()))
			stringBuilder.append(" AND ewpv.\"action\" ='").append(dashboardDataSearch.getStatus()).append("'");
		
		return stringBuilder.toString();
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

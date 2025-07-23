package org.egov.pt.repository.builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.egov.pt.models.DashboardDataSearch;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DashboardReportQueryBuilder {

	public static final String TOTAL_PROPERTIES_REGISTERED = "select epp.propertyid from eg_pt_property epp join eg_pt_address epa on epp.id=epa.propertyid where 1=1";

	public static final String PROPERTIES_PENDING_WITH = "WITH latest_actions AS (\r\n"
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
			+ "  WHERE ewpv.\"action\" != ''\r\n"
			+ "  /*FILTER_CONDITIONS*/\r\n"
			+ ")\r\n"
			+ "\r\n"
			+ "SELECT \r\n"
			+ "  CASE\r\n"
			+ "    WHEN action IN ('SENDBACKTOCITIZEN') THEN 'PENDINGWITHCITIZEN'\r\n"
			+ "    WHEN action IN ('REOPEN', 'SENDBACKTODOCKVERIFIER', 'SENDBACKTODOCVERIFIER', 'OPEN') THEN 'PENDINGWITHDOCVERIFIER'\r\n"
			+ "    WHEN action = 'VERIFY' THEN 'PENDINGWITHFILEDVERIFIER'\r\n"
			+ "    WHEN action = 'REJECT' THEN 'REJECTED'\r\n"
			+ "    WHEN action = 'FORWARD' THEN 'PENDINGWITHAPPROVER'\r\n"
			+ "    WHEN action = 'APPROVE' THEN 'APPROVED'\r\n"
			+ "  END AS action_st,\r\n"
			+ "  STRING_AGG(latest_actions.propertyid, ', ') AS property_ids\r\n"
			+ "FROM latest_actions\r\n"
			+ "WHERE rn = 1\r\n"
			+ "GROUP BY action_st";

	public static final String PROPERTIES_APPROVED = "SELECT count(ewpv.businessid) ,ep.tenantid,epadd.ward_no\r\n"
			+ "FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "JOIN eg_pt_property ep ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "JOIN eg_pt_address epadd ON ep.id = epadd.propertyid\r\n" + "WHERE ewpv.\"action\" ='APPROVE'\r\n";

	public static final String PROPERTIES_SELF_ASSESSED = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pt_asmt_assessment epaa\r\n"
			+ "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE 1=1";

	public static final String PROPERTIES_PENDING_SELF_ASSESSMENT = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.propertyid NOT IN (\r\n"
			+ "    SELECT DISTINCT epaa.propertyid\r\n"
			+ "    FROM eg_pt_asmt_assessment epaa\r\n"
			+ ")";

	public static final String PROPERTIES_PAID = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pg_transactions ept\r\n"
			+ "JOIN eg_pt_property epp ON ept.consumer_code = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE 1=1";

	public static final String PROPERTIES_WITH_APPEAL_SUBMITTED = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pt_property_appeal eppa ON epp.propertyid = eppa.propertyid\r\n"
			+ "JOIN eg_wf_processinstance_v2 ewpv ON epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "WHERE ewpv.\"action\" = 'OPEN'\r\n"
			+ "";

	public static final String APPEALS_PENDING = "SELECT epp.propertyid AS propertyid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pt_property_appeal eppa ON epp.propertyid = eppa.propertyid\r\n"
			+ "JOIN eg_wf_processinstance_v2 ewpv ON epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "WHERE ewpv.\"action\" = 'OPEN'\r\n"
			+ "";

	public static final String TOTAL_TAX_COLLECTED = "SELECT epp.propertyid as propertyid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PROPERTY_TAX_SHARE = "SELECT SUM(ep.totalamountpaid) * 5.0 / 8 as PROPERTY_TAX\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PENALTY_SHARE = "SELECT \r\n"
			+ "        d.consumercode AS consumercode\r\n"
			+ "    FROM egbs_demand_v1 d\r\n"
			+ "    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid\r\n"
			+ "    JOIN eg_pt_property p ON p.propertyid = d.consumercode\r\n"
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY')\r\n"
			+ "      AND dd.taxamount > 0 \r\n"
			+ "      AND t.txn_status = 'SUCCESS' \r\n"
			+ "      {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER}\r\n"
			+ "    GROUP BY d.consumercode";

	public static final String INTEREST_SHARE = "SELECT ebv2.consumercode AS consumercode\r\n"
			+ "FROM egbs_bill_v1 ebv\r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n"
			+ "  AND ebv2.interestonamount > 0";

	public static final String ADVANCE_SHARE = "SELECT edv.consumercode as consumercode\r\n"
			+ "FROM egbs_demand_v1 edv\r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 ON edv.id = ebv2.demandid\r\n"
			+ "JOIN egbs_bill_v1 ebv ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n"
			+ "  AND edv.advanceamount > 0";
	
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
			stringBuilder.append(" AND epa.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			stringBuilder.append(" AND epa.ward_no != ''");
		}
		
		String finalQuery = PROPERTIES_APPROVED.replace("/*FILTER_CONDITIONS*/", stringBuilder.toString());
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

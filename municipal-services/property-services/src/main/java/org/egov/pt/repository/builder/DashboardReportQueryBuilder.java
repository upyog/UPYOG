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
			+ "	epp.propertyid,\r\n"
			+ "	epp.tenantid\r\n"
			+ "from\r\n"
			+ "	eg_wf_processinstance_v2 ewpv\r\n"
			+ "join eg_pt_property epp\r\n"
			+ "    on\r\n"
			+ "	epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "join eg_pt_address epadd\r\n"
			+ "    on\r\n"
			+ "	epp.id = epadd.propertyid\r\n"
			+ "where\r\n"
			+ "	ewpv.\"action\" = 'APPROVE'\r\n"
			+ "	and epp.status = 'ACTIVE'\r\n"
			+ "    /*FILTER_CONDITIONS*/\r\n"
			+ "group by\r\n"
			+ "	epp.propertyid,\r\n"
			+ "	epp.tenantid,\r\n"
			+ "	epp.lastmodifiedtime\r\n"
			+ "order by\r\n"
			+ "	epp.lastmodifiedtime desc";
	
	public static final String PROPERTIES_REJECTED = "select\r\n"
			+ "	epp.propertyid,\r\n"
			+ "	epp.tenantid\r\n"
			+ "from\r\n"
			+ "	eg_wf_processinstance_v2 ewpv\r\n"
			+ "join eg_pt_property epp\r\n"
			+ "    on\r\n"
			+ "	epp.acknowldgementnumber = ewpv.businessid\r\n"
			+ "join eg_pt_address epadd\r\n"
			+ "    on\r\n"
			+ "	epp.id = epadd.propertyid\r\n"
			+ "where\r\n"
			+ "	ewpv.\"action\" = 'REJECT'\r\n"
			+ "	and epp.status = 'INACTIVE'\r\n"
			+ "  /*FILTER_CONDITIONS*/\r\n"
			+ "group by\r\n"
			+ "	epp.propertyid,\r\n"
			+ "	epp.tenantid,\r\n"
			+ "	ewpv.lastmodifiedtime\r\n"
			+ "order by\r\n"
			+ "	ewpv.lastmodifiedtime desc";

	public static final String PROPERTIES_SELF_ASSESSED = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid\r\n"
			+ "FROM eg_pt_asmt_assessment epaa\r\n" + "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE epp.status = 'ACTIVE'";

	public static final String PROPERTIES_PENDING_SELF_ASSESSMENT = "SELECT \r\n"
			+ "    epp.propertyid,\r\n"
			+ "    epp.tenantid\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.status = 'ACTIVE'\r\n"
			+ "AND NOT EXISTS (\r\n"
			+ "    SELECT 1\r\n"
			+ "    FROM eg_pt_asmt_assessment epaa\r\n"
			+ "    WHERE epaa.propertyid = epp.propertyid\r\n"
			+ ")";

	public static final String PROPERTIES_PAID = "SELECT epp.propertyid AS propertyid,epp.tenantid as tenantid,ept.txn_id as txn_id ,ept.txn_amount as txn_amount,\r\n"
			+ "ep.createdby as createdby,ep.createdtime as createdtime,ep.lastmodifiedby as lastmodifiedby,ep.lastmodifiedtime as lastmodifiedtime\r\n"
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

	public static final String TOTAL_TAX_COLLECTED = "SELECT epp.propertyid as propertyid,epp.tenantid as tenantid,ept.txn_id as txn_id,ept.txn_amount as txn_amount,\r\n"
			+ "ep.createdby as createdby,ep.createdtime as createdtime,ep.lastmodifiedby as lastmodifiedby,ep.lastmodifiedtime as lastmodifiedtime\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS' AND epp.status = 'ACTIVE'";
	
	public static final String TOTAL_TAX_COLLECTED_COUNT="SELECT \r\n"
			+ "    count(epp.propertyid) AS propertycount\r\n"
			+ "FROM eg_pt_property epp\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept \r\n"
			+ "    ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep \r\n"
			+ "    ON ept.txn_id = ep.transactionnumber\r\n"
			+ "WHERE ept.txn_status = 'SUCCESS'\r\n"
			+ "  AND epp.status = 'ACTIVE'";

	public static final String PROPERTY_TAX_SHARE = "SELECT SUM(ep.totalamountpaid) * 5.0 / 8 as PROPERTY_TAX\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PENALTY_SHARE = "WITH base_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        p.propertyid AS consumercode,\r\n"
			+ "        p.tenantid,\r\n"
			+ "        d.id AS demandid,\r\n"
			+ "        pay.createdby,\r\n"
			+ "        pay.createdtime,\r\n"
			+ "        pay.lastmodifiedby,\r\n"
			+ "        pay.lastmodifiedtime,\r\n"
			+ "        pay.transactionnumber\r\n"
			+ "    FROM eg_pt_property p\r\n"
			+ "    JOIN eg_pt_address a \r\n"
			+ "        ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t \r\n"
			+ "        ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay \r\n"
			+ "        ON t.txn_id = pay.transactionnumber\r\n"
			+ "    JOIN egbs_demand_v1 d \r\n"
			+ "        ON d.consumercode = p.propertyid\r\n"
			+ "    WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "      AND p.status = 'ACTIVE'\r\n"
			+ "      /*FILTER_CONDITIONS*/\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "bill_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        bd.demandid,\r\n"
			+ "        SUM(bd.totalamount) AS billsum\r\n"
			+ "    FROM egbs_billdetail_v1 bd\r\n"
			+ "    JOIN egbs_bill_v1 b \r\n"
			+ "        ON b.id = bd.billid\r\n"
			+ "    WHERE b.status = 'PAID'\r\n"
			+ "    GROUP BY bd.demandid\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "demand_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        dd.demandid,\r\n"
			+ "        SUM(dd.taxamount) AS demandsum\r\n"
			+ "    FROM egbs_demanddetail_v1 dd\r\n"
			+ "    GROUP BY dd.demandid\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "penalty_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        dd.demandid,\r\n"
			+ "        SUM(dd.taxamount) AS penalty\r\n"
			+ "    FROM egbs_demanddetail_v1 dd\r\n"
			+ "    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY')\r\n"
			+ "      AND dd.taxamount > 0\r\n"
			+ "    GROUP BY dd.demandid\r\n"
			+ ")\r\n"
			+ "\r\n"
			+ "SELECT\r\n"
			+ "    b.consumercode AS propertyid,\r\n"
			+ "    b.tenantid,\r\n"
			+ "    p.penalty,\r\n"
			+ "    bd.billsum AS totalbill,\r\n"
			+ "    b.createdby,\r\n"
			+ "    b.createdtime,\r\n"
			+ "    b.lastmodifiedby,\r\n"
			+ "    b.lastmodifiedtime,\r\n"
			+ "    bd.billsum - p.penalty AS actualbill,\r\n"
			+ "    b.transactionnumber AS txn_num\r\n"
			+ "FROM base_data b\r\n"
			+ "JOIN bill_data bd \r\n"
			+ "    ON bd.demandid = b.demandid\r\n"
			+ "JOIN demand_data td \r\n"
			+ "    ON td.demandid = b.demandid\r\n"
			+ "JOIN penalty_data p \r\n"
			+ "    ON p.demandid = b.demandid\r\n"
			+ "WHERE bd.billsum >= td.demandsum\r\n"
			+ "ORDER BY b.lastmodifiedtime";
	
	public static final String PENALTY_SHARE_COUNT = "WITH base_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        p.propertyid AS consumercode,\r\n"
			+ "        d.id AS demandid\r\n"
			+ "    FROM eg_pt_property p\r\n"
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code\r\n"
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "    JOIN egbs_demand_v1 d ON d.consumercode = p.propertyid\r\n"
			+ "    WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "      AND p.status = 'ACTIVE'\r\n"
			+ "      /*FILTER_CONDITIONS*/\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "bill_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        bd.demandid,\r\n"
			+ "        SUM(bd.totalamount) AS billsum\r\n"
			+ "    FROM egbs_billdetail_v1 bd\r\n"
			+ "    JOIN egbs_bill_v1 b ON b.id = bd.billid\r\n"
			+ "    WHERE b.status = 'PAID'\r\n"
			+ "    GROUP BY bd.demandid\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "demand_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        dd.demandid,\r\n"
			+ "        SUM(dd.taxamount) AS demandsum\r\n"
			+ "    FROM egbs_demanddetail_v1 dd\r\n"
			+ "    GROUP BY dd.demandid\r\n"
			+ "),\r\n"
			+ "\r\n"
			+ "penalty_data AS (\r\n"
			+ "    SELECT\r\n"
			+ "        dd.demandid,\r\n"
			+ "        SUM(dd.taxamount) AS penalty\r\n"
			+ "    FROM egbs_demanddetail_v1 dd\r\n"
			+ "    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY','PT_TIME_PENALTY')\r\n"
			+ "      AND dd.taxamount > 0\r\n"
			+ "    GROUP BY dd.demandid\r\n"
			+ ")\r\n"
			+ "\r\n"
			+ "SELECT COUNT(b.consumercode) AS property_count\r\n"
			+ "FROM base_data b\r\n"
			+ "JOIN bill_data bd ON bd.demandid = b.demandid\r\n"
			+ "JOIN demand_data td ON td.demandid = b.demandid\r\n"
			+ "JOIN penalty_data p ON p.demandid = b.demandid   \r\n"
			+ "WHERE bd.billsum >= td.demandsum";
	
	public static final String INTEREST_SHARE = "SELECT \r\n"
			+ "    ebv2.consumercode AS consumercode,\r\n"
			+ "    ebv2.tenantid,\r\n"
			+ "    SUM(ebv2.interestonamount) AS total_interest_amount,\r\n"
			+ "    SUM(ebv2.totalamount) AS total_bill_amount,\r\n"
			+ "    SUM(ebv2.totalamount) - SUM(ebv2.interestonamount) AS amount_without_interest,\r\n"
			+ "    MAX(pay.createdby) AS createdby,\r\n"
			+ "    MAX(pay.createdtime) AS createdtime,\r\n"
			+ "    MAX(pay.lastmodifiedby) AS lastmodifiedby,\r\n"
			+ "    MAX(pay.lastmodifiedtime) AS lastmodifiedtime\r\n"
			+ "FROM \r\n"
			+ "    egbs_bill_v1 ebv\r\n"
			+ "JOIN \r\n"
			+ "    egbs_billdetail_v1 ebv2 \r\n"
			+ "        ON ebv.id = ebv2.billid\r\n"
			+ "JOIN \r\n"
			+ "    eg_pt_property epp \r\n"
			+ "        ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN \r\n"
			+ "    eg_pt_address epa \r\n"
			+ "        ON epp.id = epa.propertyid\r\n"
			+ "JOIN \r\n"
			+ "    eg_pg_transactions t \r\n"
			+ "        ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN \r\n"
			+ "    egcl_payment pay \r\n"
			+ "        ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE \r\n"
			+ "    t.txn_status = 'SUCCESS'\r\n"
			+ "    AND ebv.status = 'PAID'\r\n"
			+ "    AND ebv2.interestonamount > 0\r\n"
			+ "    AND epp.status = 'ACTIVE'\r\n";

	public static final String INTEREST_SHARE_COUNT = "SELECT COUNT(DISTINCT ebv2.consumercode) AS property_count\r\n"
			+ "FROM egbs_bill_v1 ebv\r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 \r\n"
			+ "    ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp \r\n"
			+ "    ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t \r\n"
			+ "    ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay \r\n"
			+ "    ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE \r\n"
			+ "    t.txn_status = 'SUCCESS'\r\n"
			+ "    AND ebv.status = 'PAID'\r\n"
			+ "    AND ebv2.interestonamount > 0\r\n"
			+ "    AND epp.status = 'ACTIVE'";
	
	public static final String ADVANCE_SHARE = "SELECT \r\n"
			+ "    edv.consumercode AS consumercode,edv.tenantid as tenantid,\r\n"
			+ "    sum(edv.advanceamount) as advanceamount,sum(ebv2.totalamount) as dueamount,sum(pay.totalamountpaid) as collectedamount, \r\n"
			+ "    MAX(pay.createdby) AS createdby,MAX(pay.createdtime) AS createdtime,MAX(pay.lastmodifiedby) AS lastmodifiedby,MAX(pay.lastmodifiedtime) AS lastmodifiedtime \r\n"
			+ "FROM egbs_demand_v1 edv\r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 \r\n"
			+ "    ON edv.id = ebv2.demandid\r\n"
			+ "JOIN egbs_bill_v1 ebv \r\n"
			+ "    ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp \r\n"
			+ "    ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t \r\n"
			+ "    ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay \r\n"
			+ "    ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n"
			+ "  AND edv.advanceamount > 0\r\n"
			+ "  AND epp.status = 'ACTIVE'";
	
	public static final String ADVANCE_SHARE_COUNT = "SELECT count(distinct edv.consumercode) AS consumercodecount\r\n"
			+ "FROM egbs_demand_v1 edv\r\n"
			+ "JOIN egbs_billdetail_v1 ebv2 \r\n"
			+ "    ON edv.id = ebv2.demandid\r\n"
			+ "JOIN egbs_bill_v1 ebv \r\n"
			+ "    ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp \r\n"
			+ "    ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa \r\n"
			+ "    ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t \r\n"
			+ "    ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay \r\n"
			+ "    ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS'\r\n"
			+ "  AND ebv.status = 'PAID'\r\n"
			+ "  AND edv.advanceamount > 0\r\n"
			+ "  AND epp.status = 'ACTIVE'";

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
			filter.append(" LIMIT "+dashboardDataSearch.getLimit());
			filter.append(" OFFSET  "+dashboardDataSearch.getOffset());
			
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
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" order by epp.lastmodifiedtime desc");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" order by ep.lastmodifiedtime desc");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" order by eppa.lastmodifiedtime desc");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" order by eppa.lastmodifiedtime desc");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" order by ep.lastmodifiedtime desc");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}

		return filter.toString();
	}

	public String getPenaltyShareQuery(DashboardDataSearch dashboardDataSearch) {
		
		StringBuilder filterBuilder = new StringBuilder();
		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {
			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");
			toEpoch = getEndOfDayEpochMillis(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		}
		
		filterBuilder.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (StringUtils.hasText(dashboardDataSearch.getTenantid())) {
			filterBuilder.append(" AND p.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (StringUtils.hasText(dashboardDataSearch.getWard())) {
			filterBuilder.append(" AND a.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filterBuilder.append(" AND a.ward_no != ''");
		}

		//query = query.replace("{DATE_FILTER}", dateFilter).replace("{TENANT_FILTER}", tenantFilter)
//				//.replace("{WARD_FILTER}", wardFilter);
		
		/*
		 * String dateFilter = " AND pay.createdtime BETWEEN " + fromEpoch + " AND " +
		 * toEpoch; String tenantFilter =
		 * !StringUtils.isEmpty(dashboardDataSearch.getTenantid()) ?
		 * " AND p.tenantid = '" + dashboardDataSearch.getTenantid() + "'" : ""; String
		 * wardFilter = !StringUtils.isEmpty(dashboardDataSearch.getWard()) ?
		 * " AND a.ward_no = '" + dashboardDataSearch.getWard() + "'" :
		 * " AND a.ward_no != ''";
		 */

		//query = query.replace("{DATE_FILTER}", "/*{DATE_FILTER}*/").replace("{TENANT_FILTER}", "/*{DATE_FILTER}*/")
			//	.replace("{WARD_FILTER}", "/*{DATE_FILTER}*/");
		//query = query.replace("{DATE_FILTER}", dateFilter).replace("{TENANT_FILTER}", tenantFilter)
			//	.replace("{WARD_FILTER}", wardFilter);
		
		String query = PENALTY_SHARE.replace("/*FILTER_CONDITIONS*/", filterBuilder.toString());
	 
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			query = query + " OFFSET " + dashboardDataSearch.getOffset() + " LIMIT " + dashboardDataSearch.getLimit();
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
		
		filter.append(" group by ebv2.consumercode,ebv2.tenantid");
		filter.append(" order by pay.lastmodifiedtime desc ");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
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
		
		filter.append(" group by edv.consumercode,edv.tenantid");
		filter.append(" order by pay.lastmodifiedtime desc ");
		if(!StringUtils.isEmpty(dashboardDataSearch.getLimit()) && !StringUtils.isEmpty(dashboardDataSearch.getOffset()))
		{
			filter.append(" OFFSET ").append(dashboardDataSearch.getOffset());
			filter.append(" LIMIT ").append(dashboardDataSearch.getLimit());
		}
		
		return filter.toString();
	}
	
	public String getTotalTaxCollectedCount(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(TOTAL_TAX_COLLECTED_COUNT);

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
		return filter.toString();
	}
	
	public String getTotalPenaltyCollectedCount(DashboardDataSearch dashboardDataSearch) {
		
		StringBuilder filterBuilder = new StringBuilder();
		long fromEpoch, toEpoch;
		if (!StringUtils.isEmpty(dashboardDataSearch.getFromDate())
				&& !StringUtils.isEmpty(dashboardDataSearch.getToDate())) {
			fromEpoch = getStartOfDayEpochMillis(dashboardDataSearch.getFromDate());
			toEpoch = getEndOfDayEpochMillis(dashboardDataSearch.getToDate());
		} else {
			fromEpoch = getStartOfDayEpochMillis("01-04-2025");
			toEpoch = getEndOfDayEpochMillis(LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
		}
		
		filterBuilder.append(" AND pay.createdtime BETWEEN ").append(fromEpoch).append(" AND ").append(toEpoch);

		if (StringUtils.hasText(dashboardDataSearch.getTenantid())) {
			filterBuilder.append(" AND p.tenantid = '").append(dashboardDataSearch.getTenantid()).append("'");
		}

		if (StringUtils.hasText(dashboardDataSearch.getWard())) {
			filterBuilder.append(" AND a.ward_no = '").append(dashboardDataSearch.getWard()).append("'");
		} else {
			filterBuilder.append(" AND a.ward_no != ''");
		}
	
		String query = PENALTY_SHARE_COUNT.replace("/*FILTER_CONDITIONS*/", filterBuilder.toString());
	 
		return query;
	}
	
	public String getInterestCollectedCount(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(INTEREST_SHARE_COUNT);

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
		
		return filter.toString();
	}
	
	public String getAdvanceCollectedCount(DashboardDataSearch dashboardDataSearch) {

		StringBuilder filter = new StringBuilder(ADVANCE_SHARE_COUNT);

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

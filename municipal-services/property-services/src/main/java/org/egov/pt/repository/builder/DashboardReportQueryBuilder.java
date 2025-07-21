package org.egov.pt.repository.builder;

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

	public static final String PROPERTIES_SELF_ASSESSED = "select COUNT(epp.propertyid) AS count\r\n"
			+ "FROM eg_pt_asmt_assessment epaa\r\n" + "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE 1=1";

	public static final String PROPERTIES_PENDING_SELF_ASSESSMENT = "select COUNT(epp.propertyid) AS count\r\n"
			+ "from eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE epp.propertyid not in (select distinct epaa.propertyid from eg_pt_asmt_assessment epaa)";

	public static final String PROPERTIES_PAID = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM eg_pg_transactions ept\r\n" + "JOIN eg_pt_property epp ON ept.consumer_code = epp.propertyid\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN egcl_payment ep on ept.txn_id =ep.transactionnumber \r\n" + "where 1=1";

	public static final String PROPERTIES_WITH_APPEAL_SUBMITTED = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM  eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid \r\n"
			+ "join eg_pt_property_appeal eppa on epp.propertyid =eppa.propertyid \r\n"
			+ "join eg_wf_processinstance_v2 ewpv on epp.acknowldgementnumber =ewpv.businessid \r\n"
			+ "where ewpv.\"action\" ='OPEN'";

	public static final String APPEALS_PENDING = "SELECT COUNT(epp.propertyid) AS count\r\n"
			+ "FROM  eg_pt_property epp \r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid \r\n"
			+ "join eg_pt_property_appeal eppa on epp.propertyid =eppa.propertyid \r\n"
			+ "join eg_wf_processinstance_v2 ewpv on epp.acknowldgementnumber =ewpv.businessid \r\n"
			+ "where ewpv.\"action\" ='OPEN'";

	public static final String TOTAL_TAX_COLLECTED = "SELECT SUM(ep.totalamountpaid) AS todayscollection\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PROPERTY_TAX_SHARE = "SELECT SUM(ep.totalamountpaid) * 5.0 / 8 as PROPERTY_TAX\r\n"
			+ "FROM eg_pt_property epp\r\n" + "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
			+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'";

	public static final String PENALTY_SHARE = "WITH paid_bills AS ( "
			+ "    SELECT bd.demandid, SUM(bd.totalamount) AS billsum " + "    FROM egbs_bill_v1 b "
			+ "    JOIN egbs_billdetail_v1 bd ON b.id = bd.billid "
			+ "    JOIN eg_pt_property p ON p.propertyid = bd.consumercode "
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code "
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber "
			+ "    WHERE t.txn_status = 'SUCCESS' and b.status = 'PAID' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} "
			+ "    GROUP BY bd.demandid HAVING SUM(bd.totalamount) > 0 " + "), " + "total_demand AS ( "
			+ "    SELECT d.id AS demandid, SUM(dd.taxamount) AS demandsum " + "    FROM egbs_demand_v1 d "
			+ "    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid "
			+ "    JOIN eg_pt_property p ON p.propertyid = d.consumercode "
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code "
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber "
			+ "    WHERE t.txn_status = 'SUCCESS' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} "
			+ "    GROUP BY d.id HAVING SUM(dd.taxamount) > 0 " + "), " + "penalty_demand AS ( "
			+ "    SELECT d.id AS demandid, SUM(dd.taxamount) AS penalty_amount " + "    FROM egbs_demand_v1 d "
			+ "    JOIN egbs_demanddetail_v1 dd ON d.id = dd.demandid "
			+ "    JOIN eg_pt_property p ON p.propertyid = d.consumercode "
			+ "    JOIN eg_pt_address a ON p.id = a.propertyid\r\n"
			+ "    JOIN eg_pg_transactions t ON p.propertyid = t.consumer_code "
			+ "    JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber "
			+ "    WHERE dd.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY') "
			+ "    AND dd.taxamount > 0 AND t.txn_status = 'SUCCESS' {DATE_FILTER} {TENANT_FILTER} {WARD_FILTER} "
			+ "    GROUP BY d.id " + ") " + "SELECT SUM(todaypenaltycollection) AS total_penalty\r\n" + "FROM (\r\n"
			+ "    SELECT \r\n"
			+ "    SUM(pd.penalty_amount) AS todaypenaltycollection,pd.demandid,pb.demandid,td.demandid\r\n"
			+ "FROM penalty_demand pd\r\n" + "JOIN paid_bills pb ON pd.demandid = pb.demandid\r\n"
			+ "JOIN total_demand td ON pd.demandid = td.demandid\r\n"
			+ "group by pd.demandid,pb.demandid,td.demandid\r\n" + "HAVING SUM(pb.billsum) >= SUM(td.demandsum)\r\n"
			+ ") AS filtered";

	public static final String INTEREST_SHARE = "SELECT SUM(ebv2.interestonamount) AS todayinterestcollection\r\n"
			+ "FROM egbs_bill_v1 ebv JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS' AND ebv.status = 'PAID' AND ebv2.interestonamount > 0";

	public static final String ADVANCE_SHARE = "SELECT sum(edv.advanceamount) as totalAdvance \r\n"
			+ "from egbs_demand_v1 edv \r\n" + "JOIN egbs_billdetail_v1 ebv2 ON edv.id = ebv2.demandid \r\n"
			+ "join egbs_bill_v1 ebv on ebv.id=ebv2.billid \r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "JOIN eg_pg_transactions t ON epp.propertyid = t.consumer_code\r\n"
			+ "JOIN egcl_payment pay ON t.txn_id = pay.transactionnumber\r\n"
			+ "WHERE t.txn_status = 'SUCCESS' AND ebv.status = 'PAID' AND edv.advanceamount > 0\r\n" + "";

}

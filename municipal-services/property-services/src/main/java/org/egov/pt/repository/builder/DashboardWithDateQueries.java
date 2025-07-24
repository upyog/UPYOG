package org.egov.pt.repository.builder;

public class DashboardWithDateQueries {
	
	public static final String TOTAL_APPLICATION_COUNT_WITH_WARD =
		    "SELECT COUNT(epa.propertyid), epa.ward_no, ep.tenantid " +
		    "FROM eg_pt_property ep " +
		    "JOIN eg_pt_address epa ON ep.id = epa.propertyid " +
		    "WHERE epa.ward_no IS NOT NULL AND epa.ward_no != '' " +
		    "AND ep.createdtime BETWEEN ? AND ? " +
		    "GROUP BY ep.tenantid, epa.ward_no";
	
	public final static String TOTAL_ASSESMENT_COUNT_WITH_WARD = "select count(epaa.propertyid), epa.ward_no ,ep.tenantid\r\n"
			+ "  FROM eg_pt_property ep\r\n" + "  join eg_pt_address epa  \r\n" + "  on ep.id =epa.propertyid\r\n"
			+ "  join eg_pt_asmt_assessment epaa \r\n" + "  on epaa.propertyid =ep.propertyid \r\n"
			+ "  WHERE epa.ward_no !=''\r\n"
			+ "  and epaa.createdtime BETWEEN ? AND ?\r\n"
			+ "  group by ep.tenantid,epa.ward_no";

		public static final String TOTAL_PROPERTY_COUNT_WITH_WARD =
				"SELECT \r\n" + "  CASE\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', \r\n"
						+ "      'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND'\r\n" + "    ) THEN 'Residential'\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', \r\n"
						+ "      'AGRICULTURAL_COMMERCIAL_USE'\r\n" + "    ) THEN 'Commercial'\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_MIXED', 'TENANT_MIXED'\r\n"
						+ "    ) THEN 'Mixed Use'\r\n" + "    WHEN epp.usagecategory IN (\r\n"
						+ "      'USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT'\r\n" + "    ) THEN 'Institutional'\r\n"
						+ "  END AS usagecategory,\r\n" + "  sum(ep.totalamountpaid)::BIGINT AS totalpropertytaxamountpaid,\r\n"
						+ "  epp.tenantid,\r\n" + "  epa.ward_no\r\n" + "FROM eg_pt_property epp\r\n"
						+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
						+ "join eg_pg_transactions ept on epp.propertyid =ept.consumer_code \r\n"
						+ "join egcl_payment ep on ep.transactionnumber =ept.txn_id \r\n" + "WHERE epa.ward_no != ''\r\n"
						+ "  AND epp.usagecategory != ''\r\n" + "  AND ep.createdtime BETWEEN \r\n"
						+ "      ? AND \r\n"
						+ "      ? \r\n"
						+ "GROUP BY \r\n" + "  epp.tenantid,\r\n" + "  epa.ward_no,\r\n" + "  CASE\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', \r\n"
						+ "      'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND'\r\n" + "    ) THEN 'Residential'\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', \r\n"
						+ "      'AGRICULTURAL_COMMERCIAL_USE'\r\n" + "    ) THEN 'Commercial'\r\n"
						+ "    WHEN epp.usagecategory IN (\r\n" + "      'OWNER_MIXED', 'TENANT_MIXED'\r\n"
						+ "    ) THEN 'Mixed Use'\r\n" + "    WHEN epp.usagecategory IN (\r\n"
						+ "      'USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT'\r\n" + "    ) THEN 'Institutional'\r\n" + "  END\r\n"
						+ "";
		
		public final static String TOTAL_PROPERTY_APPROVED_WITH_WARD = "SELECT count(ewpv.businessid) ,ep.tenantid,epadd.ward_no\r\n"
				+ "FROM eg_wf_processinstance_v2 ewpv\r\n"
				+ "JOIN eg_pt_property ep ON ep.acknowldgementnumber = ewpv.businessid\r\n"
				+ "JOIN eg_pt_address epadd ON ep.id = epadd.propertyid\r\n"
				+ "WHERE ewpv.\"action\" != '' AND epadd.ward_no != '' and ewpv.\"action\" ='APPROVE'\r\n"
				+ "AND ewpv.lastmodifiedtime BETWEEN ? AND ?\r\n"
				+ "GROUP BY ep.tenantid, epadd.ward_no";

		public static final String TOTAL_PROPERTY_CLOSED_COUNT_WITH_WARD =
			    "SELECT COUNT(epaa.propertyid), epa.ward_no, ep.tenantid " +
			    "FROM eg_pt_property ep " +
			    "JOIN eg_pt_address epa ON ep.id = epa.propertyid " +
			    "JOIN eg_pt_asmt_assessment epaa ON epaa.propertyid = ep.propertyid " +
			    "WHERE epa.ward_no IS NOT NULL AND epa.ward_no != '' " +
			    "AND ep.status = 'ACTIVE' " +
			    "AND ep.createdtime BETWEEN ? AND ? " +
			    "GROUP BY ep.tenantid, epa.ward_no";
		
		public final static String TOTAL_PROPERTY_PAID_COUNT_WITH_WARD = "select count(epp.propertyid),epa.ward_no ,epp.tenantid from eg_pt_property epp \r\n"
				+ "join eg_pt_address epa on epp.id =epa.propertyid \r\n"
				+ "join eg_pg_transactions ept on epp.propertyid =ept.consumer_code\r\n"
				+ "join egcl_payment ep on ept.txn_id =ep.transactionnumber \r\n" + "and epa.ward_no !=''\r\n"
				+ "and ep.createdtime BETWEEN ? AND ?\r\n"
				+ " group by epp.tenantid,epa.ward_no";
		
		public final static String TOTAL_MOVED_APPLICATION_COUNT_WITH_WARD = "SELECT CASE\r\n"
				+ "    WHEN ewpv.\"action\" = 'OPEN' THEN 'OPEN'\r\n"
				+ "    WHEN ewpv.\"action\" = 'VERIFY' THEN 'DOCVERIFIED'\r\n"
				+ "    WHEN ewpv.\"action\" = 'FORWARD' THEN 'FIELDVERIFIED'\r\n"
				+ "    WHEN ewpv.\"action\" = 'REJECT' THEN 'REJECTED'\r\n"
				+ "    WHEN ewpv.\"action\" = 'REOPEN' THEN 'CORRECTIONPENDING'\r\n"
				+ "    WHEN ewpv.\"action\" = 'APPROVE' THEN 'APPROVED'\r\n" + "  END AS action_st,\r\n"
				+ "  COUNT(*) AS count,\r\n" + "  ep.tenantid, epadd.ward_no\r\n" + "FROM eg_wf_processinstance_v2 ewpv\r\n"
				+ "JOIN eg_pt_property ep ON ep.acknowldgementnumber = ewpv.businessid\r\n"
				+ "join eg_pt_address  epadd on ep.id = epadd.propertyid\r\n"
				+ "where ewpv.\"action\" !='' and epadd.ward_no !=''\r\n" + "and ewpv.lastmodifiedtime between ? and ? \r\n"
				+ "GROUP BY action_st, ep.tenantid,epadd.ward_no order by ep.tenantid, epadd.ward_no";
		
		public final static String TOTAL_PROPERTY_REGISTERED_COUNT_WITH_WARD = "SELECT\r\n"
				+ "	 count(epaa.propertyid),\r\n" + "    CASE\r\n"
				+ "        WHEN TO_TIMESTAMP(epaa.createdtime / 1000)::DATE >= make_date(EXTRACT(YEAR FROM TO_TIMESTAMP(epaa.createdtime / 1000))::INT, 4, 1)\r\n"
				+ "        THEN EXTRACT(YEAR FROM TO_TIMESTAMP(epaa.createdtime / 1000))::TEXT || '-' || RIGHT((EXTRACT(YEAR FROM TO_TIMESTAMP(epaa.createdtime / 1000)) + 1)::TEXT, 2)\r\n"
				+ "        ELSE (EXTRACT(YEAR FROM TO_TIMESTAMP(epaa.createdtime / 1000)) - 1)::TEXT || '-' || RIGHT(EXTRACT(YEAR FROM TO_TIMESTAMP(epaa.createdtime / 1000))::TEXT, 2)\r\n"
				+ "    END AS financialyear,epa.ward_no,epaa.tenantid\r\n" + " \r\n" + "from eg_pt_property epaa\r\n"
				+ "join eg_pt_address epa\r\n" + "on epa.propertyid =epaa.id\r\n" + "join eg_pg_transactions ept\r\n"
				+ "on ept.consumer_code =epaa.propertyid\r\n" + " \r\n" + "where\r\n"
				+ "	ept.created_time between ? and ?\r\n"
				+ "                             and epa.ward_no!=''\r\n"
				+ "                             and ept.txn_status = 'SUCCESS'\r\n"
				+ "                            group by financialyear,epa.ward_no,epaa.tenantid";
		
		public final static String TOTAL_ASSEDPROPERTIES_COUNT_WITH_WARD = "SELECT \r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END AS usagecategory,\r\n" + "  COUNT(epp.propertyid) AS count,\r\n" + "  epp.tenantid,\r\n"
				+ "  epa.ward_no\r\n" + "FROM eg_pt_asmt_assessment epaa\r\n"
				+ "JOIN eg_pt_property epp ON epaa.propertyid = epp.propertyid\r\n"
				+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE epa.ward_no != ''\r\n"
				+ "  AND epaa.createdtime BETWEEN ? AND ? \r\n"
				+ "GROUP BY epp.tenantid, epa.ward_no, \r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END";
		
		public final static String TOTAL_TRANSACTIONS_COUNT_WITH_WARD = "SELECT \r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END AS usagecategory,\r\n" + "  COUNT(epp.propertyid) AS count,\r\n" + "  epp.tenantid,\r\n"
				+ "  epa.ward_no\r\n" + "FROM eg_pg_transactions ept\r\n"
				+ "JOIN eg_pt_property epp ON ept.consumer_code = epp.propertyid\r\n"
				+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n" + "WHERE ept.txn_status = 'SUCCESS'\r\n"
				+ "  AND epa.ward_no != ''\r\n" + "  AND ept.created_time BETWEEN ? AND ? \r\n"
				+ "GROUP BY \r\n" + "  epp.tenantid,\r\n" + "  epa.ward_no,\r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END;\r\n" + "";
		
		public final static String TOTAL_TODAYS_COLLECTION_WITH_WARD = "SELECT \r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END AS usagecategory,\r\n" + "  SUM(ep.totalamountpaid)::BIGINT AS todayscollection,\r\n"
				+ "  epp.tenantid,\r\n" + "  epa.ward_no\r\n" + "FROM eg_pt_property epp\r\n"
				+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
				+ "JOIN eg_pg_transactions ept ON epp.propertyid = ept.consumer_code\r\n"
				+ "JOIN egcl_payment ep ON ept.txn_id = ep.transactionnumber\r\n" + "WHERE ept.txn_status = 'SUCCESS'\r\n"
				+ "  AND epa.ward_no != ''\r\n" + "  AND ep.createdtime BETWEEN ? AND ? \r\n"
				+ "GROUP BY \r\n" + "  epp.tenantid,\r\n" + "  epa.ward_no,\r\n" + "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END\r\n" + "";
		
		public static final String TOTAL_REBATE_COLLECTION_WITH_WARD =
			    "WITH paid_bills AS ( " +
			    "    SELECT ebv2.demandid, ebv2.tenantid, SUM(ebv2.totalamount) AS billsum " +
			    "    FROM egbs_bill_v1 ebv " +
			    "    JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid " +
			    "    JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode " +
			    "    JOIN eg_pt_address epa ON epp.id = epa.propertyid " +
			    "    JOIN eg_pg_transactions ept ON ept.consumer_code = epp.propertyid " +
			    "    JOIN egcl_payment ep ON ep.transactionnumber = ept.txn_id " +
			    "    WHERE ebv.status = 'PAID' AND ept.txn_status = 'SUCCESS' AND epa.ward_no != '' " +
			    "      AND ep.createdtime BETWEEN ? AND ? " +
			    "    GROUP BY ebv2.demandid, ebv2.tenantid " +
			    "    HAVING SUM(ebv2.totalamount) > 0 " +
			    "), " +
			    "total_demand AS ( " +
			    "    SELECT edv.id AS demandid, SUM(edv2.taxamount) AS demandsum " +
			    "    FROM egbs_demand_v1 edv " +
			    "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid " +
			    "    JOIN egbs_billdetail_v1 ebv2 ON ebv2.consumercode = edv.consumercode " +
			    "    JOIN egbs_bill_v1 ebv ON ebv.id = ebv2.billid " +
			    "    JOIN eg_pt_property epp ON epp.propertyid = edv.consumercode " +
			    "    JOIN eg_pt_address epa ON epp.id = epa.propertyid " +
			    "    JOIN eg_pg_transactions ept ON ept.consumer_code = epp.propertyid " +
			    "    JOIN egcl_payment ep ON ep.transactionnumber = ept.txn_id " +
			    "    WHERE ebv.status = 'PAID' AND ept.txn_status = 'SUCCESS' AND epa.ward_no != '' " +
			    "      AND ep.createdtime BETWEEN ? AND ? " +
			    "    GROUP BY edv.id " +
			    "    HAVING SUM(edv2.taxamount) > 0 " +
			    "), " +
			    "rebate_demand AS ( " +
			    "    SELECT edv.id AS demandid, epp.tenantid, epa.ward_no, " +
			    "        CASE " +
			    "            WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential' " +
			    "            WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial' " +
			    "            WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use' " +
			    "            WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional' " +
			    "        END AS usagecategory, " +
			    "        SUM(edv2.taxamount) AS rebate_amount " +
			    "    FROM egbs_demand_v1 edv " +
			    "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid " +
			    "    JOIN egbs_billdetail_v1 ebv2 ON ebv2.consumercode = edv.consumercode " +
			    "    JOIN egbs_bill_v1 ebv ON ebv.id = ebv2.billid " +
			    "    JOIN eg_pt_property epp ON epp.propertyid = edv.consumercode " +
			    "    JOIN eg_pt_address epa ON epp.id = epa.propertyid " +
			    "    JOIN eg_pg_transactions ept ON ept.consumer_code = epp.propertyid " +
			    "    JOIN egcl_payment ep ON ep.transactionnumber = ept.txn_id " +
			    "    WHERE edv2.taxheadcode IN ('PT_COMPLEMENTARY_REBATE', 'PT_MODEOFPAYMENT_REBATE') " +
			    "      AND edv2.taxamount < 0 AND epa.ward_no != '' " +
			    "      AND ebv.status = 'PAID' AND ept.txn_status = 'SUCCESS' " +
			    "      AND ep.createdtime BETWEEN ? AND ? " +
			    "    GROUP BY edv.id, epp.tenantid, epa.ward_no, " +
			    "        CASE " +
			    "            WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential' " +
			    "            WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial' " +
			    "            WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use' " +
			    "            WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional' " +
			    "        END " +
			    "    HAVING SUM(edv2.taxamount) > 0 " +
			    ") " +
			    "SELECT rd.usagecategory, SUM(rd.rebate_amount)::BIGINT AS todayrebategiven, rd.tenantid, rd.ward_no " +
			    "FROM rebate_demand rd " +
			    "JOIN paid_bills pb ON rd.demandid = pb.demandid " +
			    "JOIN total_demand td ON rd.demandid = td.demandid " +
			    "GROUP BY rd.tenantid, rd.ward_no, rd.usagecategory " +
			    "HAVING SUM(pb.billsum) >= SUM(td.demandsum)";
		
		public final static String TOTAL_PENALTY_COLLECTED_WITH_WARD = "WITH paid_bills AS (\r\n"
				+ "    SELECT \r\n"
				+ "        ebv2.demandid,\r\n"
				+ "        ebv2.tenantid,\r\n"
				+ "        SUM(ebv2.totalamount) AS billsum\r\n"
				+ "    FROM egbs_bill_v1 ebv\r\n"
				+ "    JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
				+ "    join eg_pt_property epp on epp.propertyid =ebv2.consumercode \r\n"
				+ "    join eg_pt_address epa on epa.propertyid =epp.id \r\n"
				+ "    join eg_pg_transactions ept on ept.consumer_code =epp.propertyid \r\n"
				+ "    join egcl_payment ep on ep.transactionnumber =ept.txn_id \r\n"
				+ "    WHERE ebv.status = 'PAID' and ept.txn_status ='SUCCESS' and epa.ward_no !=''\r\n"
				+ "      AND ep.createdtime BETWEEN ? AND ? \r\n"
				+ "    GROUP BY ebv2.demandid, ebv2.tenantid\r\n"
				+ "    having SUM(ebv2.totalamount)>0\r\n"
				+ "),\r\n"
				+ "total_demand AS (\r\n"
				+ "    SELECT \r\n"
				+ "        edv.id AS demandid,\r\n"
				+ "        SUM(edv2.taxamount) AS demandsum\r\n"
				+ "    FROM egbs_demand_v1 edv\r\n"
				+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
				+ "    JOIN egbs_billdetail_v1 ebv2 ON ebv2.consumercode = edv.consumercode \r\n"
				+ "    join egbs_bill_v1 ebv on ebv.id=ebv2.billid \r\n"
				+ "    join eg_pt_property epp on epp.propertyid =ebv2.consumercode \r\n"
				+ "    join eg_pt_address epa on epa.propertyid =epp.id \r\n"
				+ "    join eg_pg_transactions ept on ept.consumer_code =epp.propertyid \r\n"
				+ "    join egcl_payment ep on ep.transactionnumber =ept.txn_id \r\n"
				+ "    WHERE ebv.status = 'PAID' and ept.txn_status ='SUCCESS' and epa.ward_no !=''\r\n"
				+ "    AND ep.createdtime BETWEEN ? AND ?\r\n"
				+ "    GROUP BY edv.id\r\n"
				+ "    having SUM(edv2.taxamount)>0\r\n"
				+ "),\r\n"
				+ "penalty_demand AS (\r\n"
				+ "    SELECT \r\n"
				+ "        edv.id AS demandid,\r\n"
				+ "        epp.tenantid,\r\n"
				+ "        epa.ward_no,\r\n"
				+ "        CASE\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "            WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "        END AS usagecategory,\r\n"
				+ "        SUM(edv2.taxamount)::BIGINT AS penalty_amount\r\n"
				+ "    FROM egbs_demand_v1 edv\r\n"
				+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
				+ "    JOIN egbs_billdetail_v1 ebv2 ON ebv2.consumercode = edv.consumercode \r\n"
				+ "    join egbs_bill_v1 ebv on ebv.id=ebv2.billid\r\n"
				+ "    JOIN eg_pt_property epp ON epp.propertyid = edv.consumercode\r\n"
				+ "    JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
				+ "    join eg_pg_transactions ept on ept.consumer_code =epp.propertyid \r\n"
				+ "    join egcl_payment ep on ep.transactionnumber =ept.txn_id\r\n"
				+ "    WHERE edv2.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY')\r\n"
				+ "    and ebv.status = 'PAID' and ept.txn_status ='SUCCESS'\r\n"
				+ "      AND edv2.taxamount > 0\r\n"
				+ "      AND epa.ward_no != ''\r\n"
				+ "      AND ep.createdtime BETWEEN ? AND ? \r\n"
				+ "    GROUP BY edv.id, epp.tenantid, epa.ward_no,\r\n"
				+ "        CASE\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "            WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "            WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "        end\r\n"
				+ "        having SUM(edv2.taxamount)>0\r\n"
				+ ")\r\n"
				+ "SELECT \r\n"
				+ "    pd.usagecategory,\r\n"
				+ "    SUM(pd.penalty_amount)::BIGINT AS todaypenaltycollection,\r\n"
				+ "    pd.tenantid,\r\n"
				+ "    pd.ward_no\r\n"
				+ "FROM penalty_demand pd\r\n"
				+ "JOIN paid_bills pb ON pd.demandid = pb.demandid\r\n"
				+ "JOIN total_demand td ON pd.demandid = td.demandid\r\n"
				+ "GROUP BY pd.tenantid, pd.ward_no, pd.usagecategory\r\n"
				+ "HAVING SUM(pb.billsum) >= SUM(td.demandsum)\r\n"
				+ "";
		
		public final static String TOTAL_INTEREST_COLLECTED_WITH_WARD = "SELECT \r\n"
				+ "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END AS usagecategory,\r\n"
				+ "  SUM(ebv2.interestonamount) AS todayinterestcollection,\r\n"
				+ "  epp.tenantid,\r\n"
				+ "  epa.ward_no\r\n"
				+ "FROM egbs_bill_v1 ebv\r\n"
				+ "JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
				+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
				+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
				+ "join eg_pg_transactions ept on ept.consumer_code =epp.propertyid \r\n"
				+ "join egcl_payment ep on ep.transactionnumber =ept.txn_id\r\n"
				+ "WHERE epa.ward_no != ''\r\n"
				+ "  AND ebv.status = 'PAID' AND ept.txn_status ='SUCCESS'\r\n"
				+ "  AND ebv2.interestonamount > 0\r\n"
				+ "  AND (\r\n"
				+ "    ep.createdtime BETWEEN ? AND ? \r\n"
				+ "    OR\r\n"
				+ "    ebv2.lastmodifieddate BETWEEN ? AND ? \r\n"
				+ "  )\r\n"
				+ "GROUP BY \r\n"
				+ "  epp.tenantid,\r\n"
				+ "  epa.ward_no,\r\n"
				+ "  CASE\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_RESIDENTIAL', 'TENANT_RESIDENTIAL', 'AGRICULTURAL_WITHOUT_USE', 'APPURTENENT_LAND') THEN 'Residential'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_COMMERCIAL', 'TENANT_COMMERCIAL', 'AGRICULTURAL_COMMERCIAL_USE') THEN 'Commercial'\r\n"
				+ "    WHEN epp.usagecategory IN ('OWNER_MIXED', 'TENANT_MIXED') THEN 'Mixed Use'\r\n"
				+ "    WHEN epp.usagecategory IN ('USE_BY_STATE_GOVT', 'USE_BY_CENTRAL_GOVT') THEN 'Institutional'\r\n"
				+ "  END\r\n"
				+ "";


    private DashboardWithDateQueries() {
        // Private constructor to prevent instantiation
    }

}

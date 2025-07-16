package org.egov.pt.repository.builder;

public class DashboardWithDateQueries {
	
	public static final String TOTAL_APPLICATION_COUNT_WITH_WARD =
		    "SELECT COUNT(epa.propertyid), epa.ward_no, ep.tenantid " +
		    "FROM eg_pt_property ep " +
		    "JOIN eg_pt_address epa ON ep.id = epa.propertyid " +
		    "WHERE epa.ward_no IS NOT NULL AND epa.ward_no != '' " +
		    "AND ep.createdtime BETWEEN ? AND ? " +
		    "GROUP BY ep.tenantid, epa.ward_no";

		public static final String TOTAL_PROPERTY_COUNT_WITH_WARD =
		    "SELECT COUNT(ep.propertyid), epa.ward_no, ep.tenantid " +
		    "FROM eg_pt_property ep " +
		    "JOIN eg_pt_address epa ON ep.id = epa.propertyid " +
		    "WHERE epa.ward_no IS NOT NULL AND epa.ward_no != '' " +
		    "AND ep.lastmodifiedtime BETWEEN ? AND ? " +
		    "GROUP BY ep.tenantid, epa.ward_no";

		public static final String TOTAL_PROPERTY_CLOSED_COUNT_WITH_WARD =
			    "SELECT COUNT(epaa.propertyid), epa.ward_no, ep.tenantid " +
			    "FROM eg_pt_property ep " +
			    "JOIN eg_pt_address epa ON ep.id = epa.propertyid " +
			    "JOIN eg_pt_asmt_assessment epaa ON epaa.propertyid = ep.propertyid " +
			    "WHERE epa.ward_no IS NOT NULL AND epa.ward_no != '' " +
			    "AND ep.status = 'ACTIVE' " +
			    "AND ep.createdtime BETWEEN ? AND ? " +
			    "GROUP BY ep.tenantid, epa.ward_no";
		
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


    private DashboardWithDateQueries() {
        // Private constructor to prevent instantiation
    }

}

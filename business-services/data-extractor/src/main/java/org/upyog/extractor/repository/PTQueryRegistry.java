package org.upyog.extractor.repository;

public class PTQueryRegistry {

	public static final String SCALAR_METRICS_QUERY = "SELECT "
			+ "  (SELECT COUNT(*) FROM eg_pt_asmt_assessment WHERE createdtime >= :startTime AND createdtime <= :endTime AND tenantid = :tenantId) AS assessments, "
			+ "  (SELECT COUNT(*) FROM eg_pt_property WHERE createdtime >= :startTime AND createdtime <= :endTime AND tenantid = :tenantId) AS todaysTotalApplications, "
			+ "  (SELECT COUNT(DISTINCT businessid) FROM eg_wf_processinstance_v2 WHERE createdtime >= :startTime AND createdtime <= :endTime AND modulename = 'PT' AND action IN ('APPROVE', 'REJECT') AND tenantid = :tenantId) AS todaysClosedApplications, "
			+ "  (SELECT COUNT(DISTINCT eb.consumercode) FROM egcl_payment ep JOIN egcl_paymentdetail epd ON ep.id = epd.paymentid JOIN egcl_bill eb ON eb.id = epd.billid WHERE ep.transactiondate >= :startTime AND ep.transactiondate <= :endTime AND epd.businessservice = 'PT' AND ep.paymentstatus <> 'CANCELLED' AND ep.tenantid = :tenantId) AS noOfPropertiesPaidToday, "
			+ "  (SELECT COUNT(DISTINCT businessid) FROM eg_wf_processinstance_v2 WHERE createdtime >= :startTime AND createdtime <= :endTime AND modulename = 'PT' AND action = 'APPROVE' AND tenantid = :tenantId) AS todaysApprovedApplications, "
			+ "  (SELECT COUNT(DISTINCT p.propertyid) FROM eg_pt_property p JOIN eg_wf_processinstance_v2 pi ON pi.businessid = p.propertyid AND pi.action = 'APPROVE' JOIN eg_wf_businessservice_v2 bs ON bs.businessservice = 'PT.CREATE' AND bs.tenantid = p.tenantid WHERE pi.createdtime >= :startTime AND pi.createdtime <= :endTime AND p.tenantid = :tenantId AND (pi.createdtime - p.createdtime) <= COALESCE(bs.businessservicesla, 2160000000)) AS todaysApprovedApplicationsWithinSLA, "
			+ "  (SELECT COALESCE(AVG((pi.createdtime - p.createdtime) / (1000.0 * 60 * 60 * 24)), 0) FROM eg_pt_property p JOIN eg_wf_processinstance_v2 pi ON pi.businessid = p.propertyid AND pi.action = 'APPROVE' WHERE pi.createdtime >= :startTime AND pi.createdtime <= :endTime AND p.tenantid = :tenantId) AS avgDaysForApplicationApproval, "
			+ "  (SELECT COUNT(*) FROM eg_pt_property p JOIN eg_wf_businessservice_v2 bs ON bs.businessservice = 'PT.CREATE' AND bs.tenantid = p.tenantid WHERE p.status = 'INWORKFLOW' AND (:endTime - p.createdtime) > COALESCE(bs.businessservicesla, 2160000000) AND p.tenantid = :tenantId) AS pendingApplicationsBeyondTimeline";

	public static final String COLLECTION_METRICS_QUERY = "SELECT " + "  CASE "
			+ "    WHEN UPPER(p.usagecategory) LIKE 'RESIDENTIAL%' THEN 'RESIDENTIAL' "
			+ "    WHEN UPPER(p.usagecategory) LIKE '%COMMERCIAL%' THEN 'COMMERCIAL' "
			+ "    WHEN UPPER(p.usagecategory) LIKE '%INDUSTRIAL%' THEN 'INDUSTRIAL' " + "    ELSE 'OTHERS' "
			+ "  END AS usage_category, " + "  ep.paymentmode, " + "  ep.id AS payment_id, " + "  bad.taxHeadCode, "
			+ "  bad.adjustedamount AS tax_head_amount " + "FROM egcl_payment ep "
			+ "JOIN egcl_paymentdetail epd ON ep.id = epd.paymentid " + "JOIN egcl_bill eb ON epd.billid = eb.id "
			+ "JOIN egcl_billdetial ebd ON ebd.billid = eb.id "
			+ "JOIN egcl_billAccountDetail bad ON bad.billDetailid = ebd.id "
			+ "JOIN eg_pt_property p ON eb.consumercode = p.propertyid "
			+ "WHERE ep.transactiondate >= :startTime AND ep.transactiondate <= :endTime "
			+ "  AND epd.businessservice = 'PT' " + "  AND ep.paymentstatus <> 'CANCELLED' "
			+ "  AND ep.tenantid = :tenantId";

	public static final String PROPERTIES_REGISTERED_QUERY = "SELECT " + "  CASE "
			+ "    WHEN EXTRACT(MONTH FROM TO_TIMESTAMP(createdtime / 1000)) >= 4 "
			+ "    THEN TO_CHAR(TO_TIMESTAMP(createdtime / 1000), 'YYYY') || '-' || TO_CHAR(TO_TIMESTAMP(createdtime / 1000) + INTERVAL '1 year', 'YY') "
			+ "    ELSE TO_CHAR(TO_TIMESTAMP(createdtime / 1000) - INTERVAL '1 year', 'YYYY') || '-' || TO_CHAR(TO_TIMESTAMP(createdtime / 1000), 'YY') "
			+ "  END AS name, " + "  COUNT(*) AS value " + "FROM eg_pt_property "
			+ "WHERE creationreason = 'CREATE' AND tenantid = :tenantId " + "GROUP BY name " + "ORDER BY name";

	public static final String ASSESSED_PROPERTIES_QUERY = "SELECT " + "  CASE "
			+ "    WHEN UPPER(p.usagecategory) LIKE 'RESIDENTIAL%' THEN 'RESIDENTIAL' "
			+ "    WHEN UPPER(p.usagecategory) LIKE '%COMMERCIAL%' THEN 'COMMERCIAL' "
			+ "    WHEN UPPER(p.usagecategory) LIKE '%INDUSTRIAL%' THEN 'INDUSTRIAL' " + "    ELSE 'OTHERS' "
			+ "  END AS name, " + "  COUNT(DISTINCT a.propertyid) AS value " + "FROM eg_pt_asmt_assessment a "
			+ "JOIN eg_pt_property p ON a.propertyid = p.propertyid "
			+ "WHERE a.createdtime >= :startTime AND a.createdtime <= :endTime AND a.tenantid = :tenantId "
			+ "GROUP BY name";

	public static final String MOVED_APPLICATIONS_QUERY = "SELECT " + "  action AS name, " + "  COUNT(*) AS value "
			+ "FROM eg_wf_processinstance_v2 "
			+ "WHERE createdtime >= :startTime AND createdtime <= :endTime AND modulename = 'PT' AND tenantid = :tenantId "
			+ "GROUP BY name";
}

package org.upyog.employee.dasboard.query.constant;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class DashboardQueryConstant {

	// Dashbiard query constants
	public final StringBuilder DASHBOARD_QUERY_ALL = new StringBuilder("WITH Today_Collection AS ("
			+ "    SELECT CAST(SUM(amountpaid) AS BIGINT) AS Today_Collection " + "    FROM egcl_paymentdetail "
			+ "    WHERE createdtime >= EXTRACT(EPOCH FROM CURRENT_DATE) * 1000 "
			+ "    AND createdtime < EXTRACT(EPOCH FROM (CURRENT_DATE + INTERVAL '1 day')) * 1000"
			+ "), Total_Collection AS (" + "    SELECT CAST(SUM(amountpaid) AS BIGINT) AS Total_Collection "
			+ "    FROM egcl_paymentdetail " + "    WHERE createdtime >= ("
			+ "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) - INTERVAL '9 months') * 1000 END) "
			+ "    AND createdtime < (" + "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '15 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 END)"
			+ "), Total_Applications_Received AS ("
			+ "    SELECT COUNT(DISTINCT businessid) AS Total_Applications_Received "
			+ "    FROM eg_wf_processinstance_v2 " + "    WHERE createdtime >= ("
			+ "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) - INTERVAL '9 months') * 1000 END) "
			+ "    AND createdtime < (" + "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '15 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 END)"
			+ "), Total_Applications_Approved AS ("
			+ "    SELECT COUNT(DISTINCT businessid) AS Total_Applications_Approved "
			+ "    FROM eg_wf_processinstance_v2 " + "    WHERE action = 'APPROVE' " + "    AND createdtime >= ("
			+ "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) - INTERVAL '9 months') * 1000 END) "
			+ "    AND createdtime < (" + "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '15 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 END)"
			+ "), Total_Applications_Pending AS ("
			+ "    SELECT COUNT(DISTINCT businessid) AS Total_Applications_Pending "
			+ "    FROM eg_wf_processinstance_v2 " + "    WHERE businessid NOT IN ("
			+ "        SELECT DISTINCT businessid " + "        FROM eg_wf_processinstance_v2 "
			+ "        WHERE action IN ('ACTIVATE_CONNECTION', 'APPROVE', 'REJECT', 'CANCEL') " + "    ) "
			+ "    AND createdtime >= (" + "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) - INTERVAL '9 months') * 1000 END) "
			+ "    AND createdtime < (" + "        CASE WHEN EXTRACT(MONTH FROM NOW()) >= 4 "
			+ "        THEN EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '15 months') * 1000 "
			+ "        ELSE EXTRACT(EPOCH FROM DATE_TRUNC('year', NOW()) + INTERVAL '3 months') * 1000 END)" + ") "
			+ "SELECT " + "    CAST(tc.Today_Collection AS BIGINT) AS Today_Collection, "
			+ "    CAST(tcc.Total_Collection AS BIGINT) AS Total_Collection, " + "    tar.Total_Applications_Received, "
			+ "    taa.Total_Applications_Approved, " + "    tap.Total_Applications_Pending "
			+ "FROM Today_Collection tc, " + "    Total_Collection tcc, " + "    Total_Applications_Received tar, "
			+ "    Total_Applications_Approved taa, " + "    Total_Applications_Pending tap;");

	public final StringBuilder OBPAS_DASHBOARD_QUERY_ = new StringBuilder("SELECT "
			+ "(SELECT COUNT(DISTINCT applicationno) " + "FROM eg_bpa_buildingplan " + "WHERE businessservice = 'BPA' "
			+ "AND createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year') * 1000) AS Total_Applications_Received, "
			+ "(SELECT COUNT(DISTINCT applicationno) " + "FROM eg_bpa_buildingplan " + "WHERE businessservice = 'BPA' "
			+ "AND status = 'APPROVED' "
			+ "AND createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Applications_Approved, "
			+ "(SELECT COUNT(DISTINCT applicationno) " + "FROM eg_bpa_buildingplan " + "WHERE businessservice = 'BPA' "
			+ "AND createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) - "
			+ "(SELECT COUNT(DISTINCT applicationno) " + "FROM eg_bpa_buildingplan " + "WHERE businessservice = 'BPA' "
			+ "AND status = 'APPROVED' "
			+ "AND createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Applications_Pending, "
			+ "(SELECT SUM(o.amountpaid) " + "FROM egcl_bill b " + "JOIN egcl_paymentdetail o ON o.billid = b.id "
			+ "WHERE o.businessservice IN ('BPA.NC_OC_APP_FEE', 'BPA.NC_SAN_FEE', 'BPA.NC_APP_FEE', 'BPA.NC_OC_SAN_FEE', 'BPA.LOW_RISK_PERMIT_FEE', 'BPA.REG') "
			+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount;");

	public final StringBuilder ASSET_DASHBOARD_QUERY_ = new StringBuilder("SELECT "
			+ "COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
			+ "SUM(CASE WHEN pi.action = 'APPROVE' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
			+ "COUNT(DISTINCT CASE "
			+ "WHEN pi.action IN ('SUBMIT', 'SENDBACKTOEMPLOYEE', 'INITIATE', 'VERIFY', 'REJECT') "
			+ "AND pi.businessid NOT IN (" + "SELECT businessid " + "FROM eg_wf_processinstance_v2 "
			+ "WHERE action = 'APPROVE') THEN pi.businessid END) AS Total_Applications_Pending, "
			+ "(SELECT SUM(o.amountpaid) " + "FROM egcl_bill b " + "JOIN egcl_paymentdetail o ON o.billid = b.id "
			+ "WHERE o.businessservice IN ('asset-create') "
			+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
			+ "FROM eg_wf_processinstance_v2 pi " + "WHERE pi.businessservice = 'asset-create' "
			+ "AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder FSM_DASHBOARD_QUERY_ = new StringBuilder("SELECT "
			+ "COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
			+ "SUM(CASE WHEN pi.action = 'COMPLETED' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
			+ "COUNT(DISTINCT CASE "
			+ "WHEN pi.action IN ('DECLINE', 'CANCEL', 'SENDBACK', 'SUBMIT_FEEDBACK', 'SUBMIT', 'DSO_ACCEPT', 'ASSIGN', 'APPLY', 'CREATE', 'REJECT', 'REASSIGN', 'PAY', 'DSO_REJECT', 'RATE') "
			+ "AND pi.businessid NOT IN (" + "SELECT businessid " + "FROM eg_wf_processinstance_v2 "
			+ "WHERE action = 'COMPLETED') THEN pi.businessid END) AS Total_Applications_Pending, "
			+ "(SELECT SUM(o.amountpaid) " + "FROM egcl_bill b " + "JOIN egcl_paymentdetail o ON o.billid = b.id "
			+ "WHERE o.businessservice IN ('FSM') "
			+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
			+ "FROM eg_wf_processinstance_v2 pi " + "WHERE pi.businessservice = 'FSM' "
			+ "AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
			+ "AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder PGR_DASHBOARD_QUERY_ = new StringBuilder("SELECT \n"
			+ "    COUNT(DISTINCT pi.businessid) AS Total_Applications_Received,\n"
			+ "    SUM(CASE WHEN pi.action = 'RESOLVE' THEN 1 ELSE 0 END) AS Total_Applications_Approved,\n"
			+ "    COUNT(DISTINCT CASE \n"
			+ "        WHEN pi.action IN ('CANCEL', 'COMMENT', 'ASSIGN', 'EDIT', 'APPLY', 'ASSIGNEDBYAUTOESCALATION', 'REJECT', 'REASSIGN', 'REOPEN', 'RATE', 'FORWARD') \n"
			+ "        AND pi.businessid NOT IN (\n" + "            SELECT businessid \n"
			+ "            FROM eg_wf_processinstance_v2 \n" + "            WHERE action = 'RESOLVE'\n"
			+ "        ) THEN pi.businessid \n" + "    END) AS Total_Applications_Pending,\n"
			+ "    (SELECT SUM(o.amountpaid) \n" + "     FROM egcl_bill b \n"
			+ "     JOIN egcl_paymentdetail o ON o.billid = b.id \n" + "     WHERE o.businessservice IN ('PGR')\n"
			+ "     AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000  \n"
			+ "     AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year') * 1000  \n"
			+ "    ) AS Total_Amount \n" + "FROM \n" + "    eg_wf_processinstance_v2 pi \n" + "WHERE \n"
			+ "    pi.businessservice = 'PGR'\n"
			+ "    AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000  \n"
			+ "    AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year') * 1000;");

	public final StringBuilder CHB_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT wf.booking_no) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN wf.booking_status = 'BOOKED' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT wf.booking_no) - SUM(CASE WHEN wf.booking_status = 'BOOKED' THEN 1 ELSE 0 END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) " + "FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice IN ('chb-services') "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year') * 1000) AS Total_Amount "
					+ "FROM eg_chb_booking_detail wf "
					+ "WHERE wf.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND wf.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder PT_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT wf.propertyid) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN wf.status = 'ACTIVE' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT CASE WHEN wf.status = 'INWORKFLOW' THEN wf.propertyid END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) " + "FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice = 'PT' "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
					+ "FROM eg_pt_property wf "
					+ "WHERE wf.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND wf.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder PETSERVICES_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN pi.action = 'APPROVE' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT CASE WHEN pi.action IN ('APPLY', 'VERIFY', 'PAY') AND pi.businessid NOT IN "
					+ "(SELECT businessid FROM eg_wf_processinstance_v2 WHERE action = 'APPROVE') THEN pi.businessid END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice IN ('pet-services') "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
					+ "FROM eg_wf_processinstance_v2 pi " + "WHERE pi.businessservice = 'ptr' "
					+ "AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder EWASTE_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN pi.action = 'COMPLETEREQUEST' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT CASE WHEN pi.action IN ('SENDPICKUPALERT', 'CREATE', 'VERIFYPRODUCT') AND pi.businessid NOT IN "
					+ "(SELECT businessid FROM eg_wf_processinstance_v2 WHERE action = 'COMPLETEREQUEST') THEN pi.businessid END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice IN ('ewst') "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
					+ "FROM eg_wf_processinstance_v2 pi " + "WHERE pi.businessservice = 'ewst' "
					+ "AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder TL_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT wf.applicationnumber) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN wf.status = 'APPROVED' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT CASE WHEN wf.action IN ('SENDBACKTOCITIZEN', 'APPLY', 'INITIATE', 'FORWARD', 'NOWORKFLOW', 'RESUBMIT', 'SENDBACK') THEN wf.applicationnumber END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice = 'TL' "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
					+ "FROM eg_tl_tradelicense wf "
					+ "WHERE wf.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND wf.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder WATER_DASHBOARD_QUERY_ = new StringBuilder(
			"SELECT COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
					+ "SUM(CASE WHEN pi.action = 'ACTIVATE_CONNECTION' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
					+ "COUNT(DISTINCT CASE WHEN pi.action IN ('SEND_BACK_TO_CITIZEN', 'SEND_BACK_FOR_DOCUMENT_VERIFICATION', 'SUBMIT_APPLICATION', 'APPROVE_FOR_CONNECTION', 'SEND_BACK_FOR_FIELD_INSPECTION', 'VERIFY_AND_FORWARD', 'INITIATE', 'PAY', 'RESUBMIT_APPLICATION') "
					+ "AND pi.businessid NOT IN (SELECT businessid FROM eg_wf_processinstance_v2 WHERE action = 'ACTIVATE_CONNECTION') THEN pi.businessid END) AS Total_Applications_Pending, "
					+ "(SELECT SUM(o.amountpaid) FROM egcl_bill b JOIN egcl_paymentdetail o ON o.billid = b.id "
					+ "WHERE o.businessservice IN ('WS', 'WS.ONE_TIME_FEE') "
					+ "AND b.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND b.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year')) AS Total_Amount "
					+ "FROM eg_wf_processinstance_v2 pi " + "WHERE pi.businessservice = 'NewWS1' "
					+ "AND pi.createdtime >= EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '3 months') * 1000 "
					+ "AND pi.createdtime < EXTRACT(EPOCH FROM DATE_TRUNC('year', CURRENT_DATE) + INTERVAL '1 year');");

	public final StringBuilder SEWERAGE_DASHBOARD_QUERY_ = new StringBuilder("SELECT "
			+ "COUNT(DISTINCT pi.businessid) AS Total_Applications_Received, "
			+ "SUM(CASE WHEN pi.action = 'ACTIVATE_CONNECTION' THEN 1 ELSE 0 END) AS Total_Applications_Approved, "
			+ "COUNT(DISTINCT CASE "
			+ "    WHEN pi.action IN ('SEND_BACK_TO_CITIZEN', 'SEND_BACK_FOR_DOCUMENT_VERIFICATION', 'SUBMIT_APPLICATION', 'APPROVE_FOR_CONNECTION', 'SEND_BACK_FOR_FIELD_INSPECTION', 'VERIFY_AND_FORWARD', 'INITIATE', 'PAY', 'RESUBMIT_APPLICATION') "
			+ "    AND pi.businessid NOT IN (" + "        SELECT businessid " + "        FROM eg_wf_processinstance_v2 "
			+ "        WHERE action = 'ACTIVATE_CONNECTION' " + "    ) THEN pi.businessid "
			+ "END) AS Total_Applications_Pending, " + "(SELECT SUM(o.amountpaid) " + " FROM egcl_bill b "
			+ " JOIN egcl_paymentdetail o ON o.billid = b.id "
			+ " WHERE o.businessservice IN ('SWR', 'SWR.ONE_TIME_FEE')) AS Total_Amount " + "FROM "
			+ "eg_wf_processinstance_v2 pi " + "WHERE " + "pi.businessservice = 'NewSWR1';");

}

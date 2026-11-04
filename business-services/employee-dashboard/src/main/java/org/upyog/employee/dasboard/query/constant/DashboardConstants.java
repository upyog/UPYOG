package org.upyog.employee.dasboard.query.constant;

import java.util.HashMap;
import java.util.Map;

public class DashboardConstants {
	
	public static final String DATA_FETCHED_SUCCESSFULLY = "Data Fetched Successfully";

	public static final String OBPAS_DASHBOARD_VIEWER = "OBPAS";
	public static final String ASSET_DASHBOARD_VIEWER = "ASSET";
	public static final String FSM_DASHBOARD_VIEWER = "FSM";
	public static final String PGR_DASHBOARD_VIEWER = "PGR";
	public static final String CHB_DASHBOARD_VIEWER = "CHB";
	public static final String PTR_DASHBOARD_VIEWER = "PETSERVICES";
	public static final String EW_DASHBOARD_VIEWER = "EWASTE";
	public static final String TL_DASHBOARD_VIEWER = "TL";
	public static final String WATER_DASHBOARD_VIEWER = "WATER";
	public static final String PT_DASHBOARD_VIEWER = "PT";
	public static final String SEWERAGE_DASHBOARD_VIEWER = "Data Fetched Successfully";
	public static final String SV_DASHBOARD_VIEWER = "SV";
	public static final String TP_DASHBOARD_VIEWER = "TP";
	public static final String WT_DASHBOARD_VIEWER = "WT";
	public static final String MT_DASHBOARD_VIEWER = "MT";
	public static final String CND_DASHBOARD_VIEWER = "CND";
	public static final String ADS_DASHBOARD_VIEWER = "ADS";


	/**
	 * Maps role codes to their corresponding module names
	 * Used for role-based dashboard data retrieval
	 */
	public static final Map<String, String> ROLE_TO_MODULE_MAP = new HashMap<String, String>() {{
		put("OBPAS_DASHBOARD_VIEWER", "OBPAS");
		put("ASSET_DASHBOARD_VIEWER", "ASSET");
		put("FSM_DASHBOARD_VIEWER", "FSM");
		put("PGR_DASHBOARD_VIEWER", "PGR");
		put("CHB_DASHBOARD_VIEWER", "CHB");
		put("PTR_DASHBOARD_VIEWER", "PETSERVICES");
		put("EWASTE_DASHBOARD_VIEWER", "EWASTE");
		put("EW_DASHBOARD_VIEWER", "EWASTE");
		put("TL_DASHBOARD_VIEWER", "TL");
		put("WATER_DASHBOARD_VIEWER", "WATER");
		put("PT_DASHBOARD_VIEWER", "PT");
		put("SEWERAGE_DASHBOARD_VIEWER", "SEWERAGE");
		put("SV_DASHBOARD_VIEWER", "SV");
		put("TP_DASHBOARD_VIEWER", "TP");
		put("WT_DASHBOARD_VIEWER", "WT");
		put("MT_DASHBOARD_VIEWER", "MT");
		put("CND_DASHBOARD_VIEWER", "CND");
		put("ADS_DASHBOARD_VIEWER", "ADS");
	}};
	

}

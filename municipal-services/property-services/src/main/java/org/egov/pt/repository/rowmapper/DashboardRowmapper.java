package org.egov.pt.repository.rowmapper;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.WardwithTanent;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DashboardRowmapper implements ResultSetExtractor<List<WardwithTanent>> {

	@Override
	public List<WardwithTanent> extractData(ResultSet rs) throws SQLException, DataAccessException {
		List<WardwithTanent> wardwithtanent = new ArrayList<WardwithTanent>();
		while(rs.next())
		{
			int count=hasColumn(rs,"count")?rs.getInt("count"):0;
			String wardNo=rs.getString("ward_no");
			String tenantid=rs.getString("tenantid");
			String action=hasColumn(rs, "action_st")? rs.getString("action_st"):null;
			String financiyalyear=hasColumn(rs, "financialyear")?rs.getString("financialyear"):null;
			String usagecategory=hasColumn(rs, "usagecategory")?rs.getString("usagecategory"):null;
			BigDecimal todayscollection=hasColumn(rs, "todayscollection")?rs.getBigDecimal("todayscollection"):BigDecimal.ZERO;
			BigDecimal todayrebategiven=hasColumn(rs, "todayrebategiven")?rs.getBigDecimal("todayrebategiven"):BigDecimal.ZERO;
			BigDecimal todaypenaltycollection=hasColumn(rs, "todaypenaltycollection")?rs.getBigDecimal("todaypenaltycollection"):BigDecimal.ZERO;
			BigDecimal todayinterestcollection=hasColumn(rs, "todayinterstcollection")?rs.getBigDecimal("todayinterstcollection"):BigDecimal.ZERO;
			wardwithtanent.add(WardwithTanent.builder().count(count).WardNo(wardNo).Tanentid(tenantid).Action(action)
					.Financiyalyear(financiyalyear).Usagecategory(usagecategory).TodaysCollection(todayscollection)
					.Todayrebategiven(todayrebategiven).Todaypenaltycollection(todaypenaltycollection).Todayinterestcollection(todayinterestcollection).build());
		}
		return wardwithtanent;
	}
	
	private boolean hasColumn(ResultSet rs, String columnName) throws SQLException {
	    ResultSetMetaData metaData = rs.getMetaData();
	    int columnCount = metaData.getColumnCount();
	    for (int i = 1; i <= columnCount; i++) {
	        if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
	            return true;
	        }
	    }
	    return false;
	}


}

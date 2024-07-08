package org.egov.swcalculation.repository.builder;

import java.util.List;

import org.egov.swcalculation.constants.SWCalculationConstant;
import org.egov.swcalculation.web.models.BillGenerationSearchCriteria;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;

@Controller
public class SWCalculatorQueryBuilder {
	
	private static final String connectionNoListQuery = "SELECT distinct(conn.connectionno) FROM eg_sw_connection conn INNER JOIN eg_sw_service sw ON conn.id = sw.connection_id";
	
	private static final String LocalityListAsPerBatchQuery = "SELECT distinct(localitycode) FROM eg_bndry_mohalla conn";
	
	
	private static final String connectionNoNonCommercialListQuery = "SELECT distinct(conn.connectionno),sw.connectionexecutiondate "
			+ " FROM eg_sw_connection conn INNER JOIN eg_sw_service sw ON conn.id = sw.connection_id"
			+ " inner join eg_pt_property pt on conn.property_id= pt.propertyid ";
	
	private static final String distinctTenantIdsCriteria = "SELECT distinct(tenantid) FROM eg_sw_connection sw";

	private  static final String countQuery = "select count(*) from eg_sw_connection";

	private static final String INNER_JOIN_STRING = "INNER JOIN";

	private static final String LEFT_OUTER_JOIN_STRING = " LEFT OUTER JOIN ";

	private static String holderSelectValues = "connectionholder.tenantid as holdertenantid, connectionholder.connectionid as holderapplicationId, userid, connectionholder.status as holderstatus, isprimaryholder, connectionholdertype, holdershippercentage, connectionholder.relationship as holderrelationship, connectionholder.createdby as holdercreatedby, connectionholder.createdtime as holdercreatedtime, connectionholder.lastmodifiedby as holderlastmodifiedby, connectionholder.lastmodifiedtime as holderlastmodifiedtime";

	private static final String billGenerationSchedulerSearchQuery = "SELECT * from eg_sw_scheduler ";

	private static final String BILL_SCHEDULER_STATUS_UPDATE_QUERY = "UPDATE eg_sw_scheduler SET status=? where id=?";

	private static final String connectionNoByLocality = "SELECT distinct(conn.connectionno) FROM eg_sw_connection conn INNER JOIN eg_sw_service ws ON conn.id = ws.connection_id ";

	private static final String fiterConnectionBasedOnTaxPeriod =" AND conn.connectionno not in (select distinct consumercode from egbs_demand_v1 d ";

	private static final String BILL_SCHEDULER_STATUS_SEARCH_QUERY = "select status from eg_sw_scheduler ";
	
	private static final String LAST_DEMAND_GEN_FOR_CONN =" SELECT d.taxperiodfrom FROM egbs_demand_v1 d ";
	
	private static final String isConnectionDemandAvailableForBillingCycle ="select EXISTS (select 1 from egbs_demand_v1 d ";
	private static final String connectionNoListQuerysingle = "SELECT distinct(conn.connectionno),sw.connectionexecutiondate FROM eg_sw_connection conn INNER JOIN eg_sw_service sw ON conn.id = sw.connection_id";

	private final static String SEWERAGE_SEARCH_QUERY = "SELECT conn.*, sc.*, document.*, plumber.*, sc.connectionExecutionDate,"
			+ "sc.noOfWaterClosets, sc.noOfToilets,sc.proposedWaterClosets, sc.proposedToilets, sc.connectionType, sc.connection_id as connection_Id, sc.appCreatedDate,"
			+ "  sc.detailsprovidedby, sc.estimationfileStoreId , sc.sanctionfileStoreId , sc.estimationLetterDate,"
			+ " conn.id as conn_id, conn.tenantid, conn.applicationNo, conn.applicationStatus, conn.status, conn.connectionNo, conn.oldConnectionNo, conn.property_id,"
			+ " conn.roadcuttingarea, conn.action, conn.adhocpenalty, conn.adhocrebate, conn.createdBy as sw_createdBy,"
			+ " conn.lastModifiedBy as sw_lastModifiedBy, conn.createdTime as sw_createdTime, conn.lastModifiedTime as sw_lastModifiedTime,conn.additionaldetails, "
			+ " conn.adhocpenaltyreason, conn.adhocpenaltycomment, conn.adhocrebatereason, conn.adhocrebatecomment, conn.applicationType, conn.dateEffectiveFrom,"
			+ " conn.locality, conn.isoldapplication, conn.roadtype, document.id as doc_Id, document.documenttype, document.filestoreid, document.active as doc_active, plumber.id as plumber_id, plumber.name as plumber_name, plumber.licenseno,"
			+ " roadcuttingInfo.id as roadcutting_id, roadcuttingInfo.roadtype as roadcutting_roadtype, roadcuttingInfo.roadcuttingarea as roadcutting_roadcuttingarea, roadcuttingInfo.roadcuttingarea as roadcutting_roadcuttingarea, roadcuttingInfo.active as roadcutting_active,"
			+ " plumber.mobilenumber as plumber_mobileNumber, plumber.gender as plumber_gender, plumber.fatherorhusbandname, plumber.correspondenceaddress, plumber.relationship, " + holderSelectValues +
			" FROM eg_sw_connection conn "
			+  INNER_JOIN_STRING
			+" eg_sw_service sc ON sc.connection_id = conn.id"
			+  LEFT_OUTER_JOIN_STRING
			+ "eg_sw_applicationdocument document ON document.swid = conn.id"
			+  LEFT_OUTER_JOIN_STRING
			+ "eg_sw_plumberinfo plumber ON plumber.swid = conn.id"
			+ LEFT_OUTER_JOIN_STRING
			+ "eg_sw_connectionholder connectionholder ON connectionholder.connectionid = conn.id"
			+ LEFT_OUTER_JOIN_STRING
			+ "eg_sw_roadcuttinginfo roadcuttingInfo ON roadcuttingInfo.swid = conn.id ";

	
	private final static String SEWERAGE_SEARCH_DEMAND_QUERY = "SELECT conn.*, sc.*, sc.connectionExecutionDate,"
			+ "sc.noOfWaterClosets, sc.noOfToilets,sc.proposedWaterClosets, sc.proposedToilets, sc.connectionType, sc.connection_id as connection_Id, sc.appCreatedDate,"
			+ "  sc.detailsprovidedby, sc.estimationfileStoreId , sc.sanctionfileStoreId , sc.estimationLetterDate,"
			+ " conn.id as conn_id, conn.tenantid, conn.applicationNo, conn.applicationStatus, conn.status, conn.connectionNo, conn.oldConnectionNo, conn.property_id,"
			+ " conn.roadcuttingarea, conn.action, conn.adhocpenalty, conn.adhocrebate, conn.createdBy as sw_createdBy,"
			+ " conn.lastModifiedBy as sw_lastModifiedBy, conn.createdTime as sw_createdTime, conn.lastModifiedTime as sw_lastModifiedTime,conn.additionaldetails, "
			+ " conn.adhocpenaltyreason, conn.adhocpenaltycomment, conn.adhocrebatereason, conn.adhocrebatecomment, conn.applicationType, conn.dateEffectiveFrom,"
			+ " conn.locality, conn.isoldapplication, conn.roadtype, " + holderSelectValues
			+ " FROM eg_sw_connection conn "
			+  INNER_JOIN_STRING
			+" eg_sw_service sc ON sc.connection_id = conn.id"
			+ LEFT_OUTER_JOIN_STRING
			+ "eg_sw_connectionholder connectionholder ON connectionholder.connectionid = conn.id ";
			
	
	private final static String SEWERAGE_CONNECTION_SEARCH_QUERY = "SELECT conn.*, sc.*, sc.connectionExecutionDate,"
			+ "sc.noOfWaterClosets, sc.noOfToilets,sc.proposedWaterClosets, sc.proposedToilets, sc.connectionType, sc.connection_id as connection_Id, sc.appCreatedDate,"
			+ "  sc.detailsprovidedby, sc.estimationfileStoreId , sc.sanctionfileStoreId , sc.estimationLetterDate,"
			+ " conn.id as conn_id, conn.tenantid, conn.applicationNo, conn.applicationStatus, conn.status, conn.connectionNo, conn.oldConnectionNo, conn.property_id,"
			+ " conn.roadcuttingarea, conn.action, conn.adhocpenalty, conn.adhocrebate, conn.createdBy as sw_createdBy,"
			+ " conn.lastModifiedBy as sw_lastModifiedBy, conn.createdTime as sw_createdTime, conn.lastModifiedTime as sw_lastModifiedTime,conn.additionaldetails, "
			+ " conn.adhocpenaltyreason, conn.adhocpenaltycomment, conn.adhocrebatereason, conn.adhocrebatecomment, conn.applicationType, conn.dateEffectiveFrom,"
			+ " conn.locality, conn.isoldapplication, conn.roadtype,"
			+ holderSelectValues +
			" FROM eg_sw_connection conn "
			+  INNER_JOIN_STRING
			+" eg_sw_service sc ON sc.connection_id = conn.id"
			+ LEFT_OUTER_JOIN_STRING
			+ "eg_sw_connectionholder connectionholder ON connectionholder.connectionid = conn.id ";

	public static final String EG_SW_BILL_SCHEDULER_CONNECTION_STATUS_INSERT = "INSERT INTO eg_sw_bill_scheduler_connection_status "
			+ "(id, eg_sw_scheduler_id, locality, module, createdtime, lastupdatedtime, status, tenantid, reason, consumercode) VALUES (?,?,?,?,?,?,?,?,?,?);";

	public String getDistinctTenantIds() {
		return distinctTenantIdsCriteria;
	}

	public String getCountQuery() {
		return countQuery;
	}

	public String getConnectionNumberList(String tenantId, String connectionType, List<Object> preparedStatement, Integer batchOffset, Integer batchsize, Long fromDate, Long toDate) {
		StringBuilder query = new StringBuilder(SEWERAGE_SEARCH_QUERY);
		// Add connection type
		addClauseIfRequired(preparedStatement, query);
		query.append(" sc.connectiontype = ? ");
		preparedStatement.add(connectionType);

		// add tenantid
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		//Add not null condition
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");

		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno NOT IN (select distinct(consumercode) from egbs_demand_v1 dmd where (dmd.taxperiodfrom >= ? and dmd.taxperiodto <= ?) and businessservice = 'SW' and tenantid=?)");
		preparedStatement.add(fromDate);
		preparedStatement.add(toDate);
		preparedStatement.add(tenantId);
		
		addClauseIfRequired(preparedStatement, query);
		String orderbyClause = " conn.connectionno IN (select connectionno FROM eg_sw_connection where tenantid=? and connectionno is not null ORDER BY connectionno OFFSET ? LIMIT ?)";
		preparedStatement.add(tenantId);
		preparedStatement.add(batchOffset);
		preparedStatement.add(batchsize);
		query.append(orderbyClause);

		return query.toString();
	}
	
	
	public String getConnectionNumberListsingle(String tenantId, String connectionType, List<Object> preparedStatement,  Long fromDate, Long toDate, String Connectionno) {
		StringBuilder query = new StringBuilder(connectionNoListQuerysingle);

		addClauseIfRequired(preparedStatement, query);
		query.append(" sw.connectiontype = ? ");
		preparedStatement.add(connectionType);
		
		//Add status
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.status = 'Active'");

		
		//Get the activated connections status	
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.applicationstatus = ? ");
		preparedStatement.add(SWCalculationConstant.CONNECTION_ACTIVATED);
		

		// add tenantid
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		//Added connection number for testing Anonymous User issue
//		addClauseIfRequired(preparedStatement, query);
//		query.append(" conn.connectionno ='0603001817' ");
		
		//Add not null condition
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");
		if(Connectionno!=null && Connectionno!="")
		{
     
			addClauseIfRequired(preparedStatement, query);
			query.append(" conn.connectionno = ? ");
			preparedStatement.add(Connectionno);
}
		query.append(fetchConnectionsToBeGenerate(tenantId, fromDate, toDate, preparedStatement));
		return query.toString();
	}
	public String getConnectionNumberListForDemand(String tenantId, String connectionType, List<Object> preparedStatement, Long fromDate, Long toDate) {
		//StringBuilder query = new StringBuilder(connectionNoListQuery);
		//StringBuilder query = new StringBuilder(connectionNoListQuery);
		StringBuilder query = new StringBuilder(SEWERAGE_SEARCH_DEMAND_QUERY);
		// Add connection type
		addClauseIfRequired(preparedStatement, query);
		query.append(" sc.connectiontype = ? ");
		preparedStatement.add(connectionType);
		// add tenantid
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.tenantid = ? ");
		preparedStatement.add(tenantId);
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno NOT IN (select distinct(consumercode) from egbs_demand_v1 dmd where (dmd.taxperiodfrom >= ? and dmd.taxperiodto <= ?) and businessservice = 'SW' and tenantid=?)");
		preparedStatement.add(fromDate);
		preparedStatement.add(toDate);
		preparedStatement.add(tenantId);
		
		//addClauseIfRequired(preparedStatement, query);
		String orderbyClause = " order by conn.connectionno";
		query.append(orderbyClause);

		return query.toString();
		
	}
	
	public String fetchConnectionsToBeGenerate(String tenantId, Long taxPeriodFrom, Long taxPeriodTo, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(fiterConnectionBasedOnTaxPeriod);

		query.append(" WHERE d.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.status = 'ACTIVE' ");
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.businessservice = ? ");
		preparedStatement.add(SWCalculationConstant.SERVICE_FIELD_VALUE_SW);

		addClauseIfRequired(preparedStatement, query);
		query.append(" d.taxPeriodFrom = ? ");
		preparedStatement.add(taxPeriodFrom);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.taxPeriodTo = ? ) ");
		preparedStatement.add(taxPeriodTo);

		return query.toString();
	}
	
	public String getBillGenerationSchedulerQuery(BillGenerationSearchCriteria criteria,
			List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(billGenerationSchedulerSearchQuery);
		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" tenantid= ? ");
			preparedStatement.add(criteria.getTenantId());
		}
		if (criteria.getLocality() != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" locality = ? ");
			preparedStatement.add(criteria.getLocality());
		}
		if (criteria.getStatus() != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" status = ? ");
			preparedStatement.add(criteria.getStatus());
		}
		if (criteria.getBillingcycleStartdate() != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" billingcyclestartdate >= ? ");
			preparedStatement.add(criteria.getBillingcycleStartdate());
		}
		if (criteria.getBillingcycleEnddate() != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" billingcycleenddate <= ? ");
			preparedStatement.add(criteria.getBillingcycleEnddate());
		}
		
		query.append(" ORDER BY createdtime ");

		return query.toString();
	}

	public String getConnectionNumber(String tenantId, String consumerCode,String connectionType, List<Object> preparedStatement,Long fromDate, Long toDate) {
		//StringBuilder query = new StringBuilder(connectionNoListQuery);
		//StringBuilder query = new StringBuilder(connectionNoListQuery);
		StringBuilder query = new StringBuilder(SEWERAGE_CONNECTION_SEARCH_QUERY);
		// Add connection type
		addClauseIfRequired(preparedStatement, query);
		query.append(" sc.connectiontype = ? ");
		preparedStatement.add(connectionType);
		// add tenantid
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.tenantid = ? ");
		preparedStatement.add(tenantId);
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno NOT IN (select distinct(consumercode) from egbs_demand_v1 dmd where (dmd.taxperiodfrom >= ? and dmd.taxperiodto <= ?) and status='ACTIVE' and businessservice = 'SW' and tenantid=? and consumercode = ?)");
		preparedStatement.add(fromDate);
		preparedStatement.add(toDate);
		preparedStatement.add(tenantId);
		preparedStatement.add(consumerCode);
		
		addClauseIfRequired(preparedStatement, query);
		String orderbyClause = " conn.connectionno =? ";
		preparedStatement.add(consumerCode);
		query.append(orderbyClause);

		return query.toString();
		
	}
	
	public String isConnectionDemandAvailableForBillingCycle(String tenantId, Long taxPeriodFrom, Long taxPeriodTo, String consumerCode, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(isConnectionDemandAvailableForBillingCycle);

		addClauseIfRequired(preparedStatement, query);
		query.append(" d.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.consumercode = ? ");
		preparedStatement.add(consumerCode);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.status = 'ACTIVE' ");
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.taxPeriodFrom = ? ");
		preparedStatement.add(taxPeriodFrom);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.taxPeriodTo = ? ");
		preparedStatement.add(taxPeriodTo);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.businessservice = ? ) ");
		preparedStatement.add(SWCalculationConstant.SERVICE_FIELD_VALUE_SW);

		
		return query.toString();
	}
	
	/**
	 * Bill expire query builder
	 * 
	 * @param billIds
	 * @param preparedStmtList
	 */
	public String getBillSchedulerUpdateQuery(String schedulerId, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(BILL_SCHEDULER_STATUS_UPDATE_QUERY);

		return builder.toString();
	}


	private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}
	
	public String getConnectionsNoByLocality(String tenantId, String connectionType,String status,String locality, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(connectionNoByLocality);
		// add tenantid
		if(tenantId != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" conn.tenantid = ? ");
			preparedStatement.add(tenantId);
		}
		
		// Add connection type
		if(connectionType != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" ws.connectiontype = ? ");
			preparedStatement.add(connectionType);
		}

		//Active status	
		if(status != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" conn.status = ? ");
			preparedStatement.add(status);			
		}

//		addClauseIfRequired(preparedStatement, query);
//		query.append(" conn.connectionno = ? ");
//		preparedStatement.add("SW/107/2020-21/000018");

		if (locality != null) {
			addClauseIfRequired(preparedStatement, query);
			query.append(" locality = ? ");
			preparedStatement.add(locality);
		}
		
		//Getting only non exempted connection to generate bill
		addClauseIfRequired(preparedStatement, query);
		query.append(" (conn.additionaldetails->>'isexempted')::boolean is not true ");

		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");
		return query.toString();

	}
	
	public String getBillSchedulerSearchQuery(String locality, Long billFromDate, Long billToDate, String tenantId,
			List<Object> preparedStmtList) {

		StringBuilder query = new StringBuilder(BILL_SCHEDULER_STATUS_SEARCH_QUERY);

		addClauseIfRequired(preparedStmtList, query);
		query.append(" tenantid = ? ");
		preparedStmtList.add(tenantId);

		if (locality != null) {
			addClauseIfRequired(preparedStmtList, query);
			query.append(" locality = ? ");
			preparedStmtList.add(locality);
		}
		if (billFromDate != null) {
			addClauseIfRequired(preparedStmtList, query);
			query.append(" billingcyclestartdate = ? ");
			preparedStmtList.add(billFromDate);
		}
		if (billToDate != null) {
			addClauseIfRequired(preparedStmtList, query);
			query.append(" billingcycleenddate = ? ");
			preparedStmtList.add(billToDate);
		}
		

		return query.toString();
	}
	
	public String searchLastDemandGenFromDate(String consumerCode, String tenantId, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(LAST_DEMAND_GEN_FOR_CONN);

		addClauseIfRequired(preparedStatement, query);
		query.append(" d.businessservice = ? ");
		preparedStatement.add(SWCalculationConstant.SERVICE_FIELD_VALUE_SW);

		addClauseIfRequired(preparedStatement, query);
		query.append(" d.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.consumercode = ? ");
		preparedStatement.add(consumerCode);
		
		addClauseIfRequired(preparedStatement, query);
		query.append(" d.status = 'ACTIVE' ");
		
		query.append(" ORDER BY d.taxperiodfrom desc limit 1 ");
		
		return query.toString();
	}
	
	public String getConnectionNumberListForNonCommercial(String tenantId, String connectionType, String status, Long taxPeriodFrom, Long taxPeriodTo, String cone, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(connectionNoNonCommercialListQuery);
		// Add connection type
		addClauseIfRequired(preparedStatement, query);
		query.append(" sw.connectiontype = ? ");
		preparedStatement.add(connectionType);
		
		//Add status
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.status = ? ");
		preparedStatement.add(status);
		
		//Get the activated connections status	
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.applicationstatus = ? ");
		preparedStatement.add(SWCalculationConstant.CONNECTION_ACTIVATED);
		

		// add tenantid
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.tenantid = ? ");
		preparedStatement.add(tenantId);
		
		
		// add Not commercial for amritsar
		addClauseIfRequired(preparedStatement, query);
		query.append("pt.usagecategory != ? ");
		preparedStatement.add("NONRESIDENTIAL.COMMERCIAL");
		
		//Added connection number for testing Anonymous User issue
//		addClauseIfRequired(preparedStatement, query);
//		query.append(" conn.connectionno ='0603001817' ");
		
		//Add not null condition
		addClauseIfRequired(preparedStatement, query);
		query.append(" conn.connectionno is not null");

                if(cone!=null && cone!="")
		{
			addClauseIfRequired(preparedStatement, query);
			query.append(" conn.connectionno = ? ");
			preparedStatement.add(cone);
		}
		
		query.append(fetchConnectionsToBeGenerate(tenantId, taxPeriodFrom, taxPeriodTo, preparedStatement));

		return query.toString();
	}
	
	public String getLocalityListWithBatch(String tenantId, String batchCode, List<Object> preparedStatement) {
		StringBuilder query = new StringBuilder(LocalityListAsPerBatchQuery);
		// add batchcode
				addClauseIfRequired(preparedStatement, query);
				query.append(" conn.blockcode = ? ");
				preparedStatement.add(batchCode);				
				// add tenantid
				addClauseIfRequired(preparedStatement, query);
				query.append(" conn.tenantid = ? ");
				preparedStatement.add(tenantId);
	
		return query.toString();
	}
	
}

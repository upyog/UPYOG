package org.egov.pt.repository.builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.PtTaxCalculatorTrackerSearchCriteria;
import org.egov.pt.models.enums.BillStatus;
import org.egov.pt.models.enums.Status;
import org.egov.pt.web.contracts.TotalCountRequest;
import org.egov.tracer.model.CustomException;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.egov.pt.models.PtTaxCalculatorTracker;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PropertyQueryBuilder {

	@Autowired
	private PropertyConfiguration config;

	private static final String PT_TAX_CALCULATOR_TRACKER_SEARCH_QUERY = "SELECT * FROM eg_pt_tax_calculator_tracker eptct";

	private static final String PT_TAX_CALCULATOR_TRACKER_TENANT_ID_SEARCH_QUERY = "SELECT distinct(eptct.tenantid) FROM eg_pt_tax_calculator_tracker eptct";

	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN = "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";

	private static final String PROPERTY_DEFAULTER_SEARCH = "select * from (SELECT property.id as pid, property.propertyid, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.isbilling as isbilling, property.createdtime as pcreatedtime, property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder, address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails, owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus,owner.additionaldetails as oadditionaldetails, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime, unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime FROM EG_PT_PROPERTY property INNER JOIN EG_PT_ADDRESS address         ON property.id = address.propertyid INNER JOIN EG_PT_OWNER owner             ON property.id = owner.propertyid  LEFT OUTER JOIN EG_PT_UNIT unit ON property.id =  unit.propertyid  WHERE  property.tenantId= ?  AND property.usagecategory like ? AND address.locality = ? AND owner.status = ? AND property.status = ?) as propertydata"
			+ " LEFT OUTER JOIN (select consumercode,sum(taxdue) as taxDue,STRING_AGG(year || '(Rs.' || taxdue || ')',',')  as taxDueYear from ( select d.consumercode, (to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY')) as year ,  sum(dd.taxamount)-sum(dd.collectionamount) as taxdue from egbs_demanddetail_v1 dd, egbs_demand_v1 d "
			+ " where dd.demandid=d.id and d.status!='CANCELLED' and dd.tenantid='pg.citya' and d.tenantid='pg.citya' and (to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY')) != ? group by d.consumercode,(to_char((To_timestamp(d.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') || '-' || to_char((To_timestamp(d.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY'))) tax where taxDue>0 group by tax.consumercode) as taxdata on propertydata.propertyid=taxdata.consumercode";

	private static String PROEPRTY_AUDIT_QUERY = "select property from eg_pt_property_audit where propertyid=?";

	private static String PROPERTY_AUDIT_ENC_QUERY = "select * from eg_pt_property_audit where propertyid=?";

	private static String PROEPRTY_ID_QUERY = "select propertyid from eg_pt_property where id in (select propertyid from eg_pt_owner where userid IN {replace} AND status='ACTIVE') ";

	private static String REPLACE_STRING = "{replace}";

	private static String WITH_CLAUSE_QUERY = " WITH propertyresult AS ({replace}) SELECT * FROM propertyresult "
			+ "INNER JOIN (SELECT propertyid, min(statusorder) as minorder FROM propertyresult GROUP BY propertyid) as minresult "
			+ "ON minresult.propertyid=propertyresult.propertyid AND minresult.minorder=propertyresult.statusorder";

	// Select query

	private static String propertySelectValues = "property.id as pid, property.propertyid, property.isbilling as isbilling, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.createdtime as pcreatedtime,"
			+ " property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, property.business_service as pbusiness_service, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder, ";

	private static String addressSelectValues = "address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails, ";

	private static String institutionSelectValues = "institution.id as institutionid,institution.propertyid as institutionpid, institution.tenantid as institutiontenantid, institution.name as institutionname, institution.type as institutiontype, designation, nameofauthorizedperson, institution.createdby as institutioncreatedby, institution.lastmodifiedby as institutionlastmodifiedby, institution.createdtime as institutioncreatedtime, institution.lastmodifiedtime as institutionlastmodifiedtime, ";

	private static String propertyDocSelectValues = "pdoc.id as pdocid, pdoc.tenantid as pdoctenantid, pdoc.entityid as pdocentityid, pdoc.documenttype as pdoctype, pdoc.filestoreid as pdocfilestore, pdoc.documentuid as pdocuid, pdoc.status as pdocstatus, ";

	private static String ownerSelectValues = "owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime,owner.additionaldetails as oadditionaldetails,owner.name as owname,  ";

	private static String ownerDocSelectValues = " owndoc.id as owndocid, owndoc.tenantid as owndoctenantid, owndoc.entityid as owndocentityId, owndoc.documenttype as owndoctype, owndoc.filestoreid as owndocfilestore, owndoc.documentuid as owndocuid, owndoc.status as owndocstatus, ";

	private static String UnitSelectValues = "unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime, unit.additional_details as unitadditional_details ";

	private static final String TOTAL_APPLICATIONS_COUNT_QUERY = "select count(*) from eg_pt_property where tenantid = ?;";

	private static final String BASE_QUERY = "SELECT \n" + "     property.id AS pid,\n"
			+ "    property.id AS property_id,\n" + "    property.isbilling as isbilling,\n"
			+ "    property.propertyid AS propertyid,\n" + "    property.tenantid AS ptenantid,\n"
			+ "    property.surveyid,\n" + "    property.accountid,\n" + "    property.oldpropertyid,\n"
			+ "    property.status AS propertystatus,\n" + "    property.acknowldgementnumber,\n"
			+ "    property.propertytype,\n" + "    property.ownershipcategory,\n"
			+ "    property.usagecategory as pusagecategory,\n" + "    property.creationreason,\n"
			+ "    property.nooffloors,\n" + "    property.landarea,\n"
			+ "    property.superbuiltuparea as propertysbpa,\n" + "    property.linkedproperties,\n"
			+ "    property.source AS source,\n" + "    property.channel,\n" + "    property.createdby AS pcreatedby,\n"
			+ "    property.lastmodifiedby AS plastmodifiedby,\n" + "    property.createdtime AS pcreatedtime,\n"
			+ "    property.lastmodifiedtime AS plastmodifiedtime,\n"
			+ "    property.additionaldetails AS padditionaldetails,\n" + "    a.id AS addressid,\n"
			+ "    a.propertyid AS address_propertyid,\n" + "    a.tenantid AS address_tenantid,\n" + "    a.doorno,\n"
			+ "    a.plotno,\n" + "    a.buildingname,\n" + "    a.street,\n" + "    a.landmark,\n" + "    a.city,\n"
			+ "    a.pincode,\n" + "    a.locality,\n" + "    a.district,\n" + "    a.region,\n" + "    a.state,\n"
			+ "    a.country,\n" + "    a.latitude,\n" + "    a.longitude,\n"
			+ "    a.createdby AS address_createdby,\n" + "    a.createdtime AS address_createdtime,\n"
			+ "    a.lastmodifiedby AS address_lastmodifiedby,\n"
			+ "    a.lastmodifiedtime AS address_lastmodifiedtime,\n"
			+ "    a.additionaldetails AS addressadditionaldetails,\n" + "    o.ownerinfouuid,\n"
			+ "    o.tenantid AS owntenantid,\n" + "    o.propertyid AS owner_propertyid,\n"
			+ "    o.institutionid as owninstitutionid,\n" + "    o.userid,\n" + "    o.status AS ownstatus,\n"
			+ "    o.isprimaryowner,\n" + "    o.ownertype,\n" + "    o.ownershippercentage,\n"
			+ "    o.relationship,\n" + "    o.createdby AS owner_createdby,\n"
			+ "    o.createdtime AS owner_createdtime,\n" + "    o.lastmodifiedby AS owner_lastmodifiedby,\n"
			+ "    o.lastmodifiedtime AS owner_lastmodifiedtime,\n" + "    o.additionaldetails AS oadditionaldetails\n"
			// +" o.institutionid AS institution_id\n"
			+ "\n" + "FROM \n" + "    public.eg_pt_property property\n" + "LEFT JOIN \n"
			+ "    public.eg_pt_address a ON property.id = a.propertyid\n" + "LEFT JOIN \n"
			+ "    public.eg_pt_owner o ON property.id = o.propertyid\n" + "LEFT JOIN \n"
			+ "     public.eg_pt_institution i ON property.id  = i.propertyid";

	private static final String QUERY = SELECT

			+ propertySelectValues

			+ addressSelectValues

			+ institutionSelectValues

			+ propertyDocSelectValues

			+ ownerSelectValues

			+ ownerDocSelectValues

			+ UnitSelectValues

			+ " FROM EG_PT_PROPERTY property "

			+ INNER_JOIN + " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+ LEFT_JOIN + " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+ INNER_JOIN + " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+ LEFT_JOIN + " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";

	private static final String ID_QUERY = SELECT

			+ " property.id FROM EG_PT_PROPERTY property "

			+ INNER_JOIN + " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+ LEFT_JOIN + " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+ INNER_JOIN + " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+ LEFT_JOIN + " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";

	private static final String COUNT_QUERY = SELECT

			+ " count(distinct property.id) FROM EG_PT_PROPERTY property "

			+ INNER_JOIN + " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+ LEFT_JOIN + " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+ INNER_JOIN + " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+ LEFT_JOIN + " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+ LEFT_JOIN + " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY plastmodifiedtime DESC, pid) offset_ FROM " + "({})"
			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";

	private static final String LATEST_EXECUTED_MIGRATION_QUERY = "select * from eg_pt_enc_audit where tenantid = ? order by createdTime desc limit 1;";

	private static final String COUNT_STATUS_BASED_QUERY = "SELECT COUNT(DISTINCT property.id) AS count,"
			+ "COUNT(DISTINCT CASE WHEN property.status = 'INITIATED' THEN property.id END) AS applicationInitiated,"
			+ "COUNT(DISTINCT CASE WHEN property.status = 'PENDINGFORVERIFICATION' THEN property.id END) AS applicationPendingForVerification,"
			+ "COUNT(distinct case when property.status = 'PENDINGFORMODIFICATION' then property.id end) as applicationPendingForModification,"
			+ "COUNT(distinct case when property.status = 'PENDINGFORAPPROVAL' then property.id end) as applicationPendingForApproval,"
			+ "COUNT(distinct case when property.status = 'APPROVED' then property.id end) as applicationApproved,"
			+ "COUNT(distinct case when property.status = 'REJECTED' then property.id end) as applicationRejected "
			+ "from EG_PT_PROPERTY property";

	private static final String PROPERTY_OWNERS_SEARCH_QUERY = "select * from eg_pt_owner epo where mobile_number is null limit 500";

	private static final String CHECK_PROPERTY_MASTER_STATUS_QUERY = "SELECT " + "  data->>'ulbName' AS ulb_name, "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.Zones' THEN 'YES' ELSE 'NO' END) AS \"LocationFactor(F1)\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.BuildingStructure' THEN 'YES' ELSE 'NO' END) AS \"BuildingType(F2)\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.BuildingEstablishmentYear' THEN 'YES' ELSE 'NO' END) AS \"BuildingEstablishment(F3)\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.BuildingPurpose' THEN 'YES' ELSE 'NO' END) AS \"OccupationFactor(F4)\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.BuildingUse' THEN 'YES' ELSE 'NO' END) AS \"UsesOfBuilding(F5)\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.OverAllRebate' THEN 'YES' ELSE 'NO' END) AS \"OverallRebateRate\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.PenaltyRate' THEN 'YES' ELSE 'NO' END) AS \"PenaltyRate\", "
			+ "  MAX(CASE WHEN schemacode = 'ULBS.EarlyPaymentRebate' THEN 'YES' ELSE 'NO' END) AS \"EarlyPaymentRebate\", "
			+ "  MAX(CASE WHEN schemacode = 'PropertyTaxRate.PropertyTaxRate' THEN 'YES' ELSE 'NO' END) AS \"PropertyTaxCalculation\", "
			+ "  CASE " + "    WHEN COUNT(DISTINCT schemacode) = 9 THEN 'YES' " + "    ELSE 'NO' "
			+ "  END AS \"All_Complete\" " + "FROM eg_mdms_data " + "WHERE schemacode IN ( " + "  'ULBS.Zones', "
			+ "  'ULBS.BuildingEstablishmentYear', " + "  'ULBS.BuildingStructure', " + "  'ULBS.BuildingPurpose', "
			+ "  'ULBS.BuildingUse', " + "  'ULBS.OverAllRebate', " + "  'ULBS.PenaltyRate', "
			+ "  'ULBS.EarlyPaymentRebate', " + "  'PropertyTaxRate.PropertyTaxRate' " + ") ";

	// --------------------------	
	private static final String UNIQUE_WARDS_SEARCH_QUERY = "SELECT DISTINCT "
			+ "(additionaldetails::jsonb)->>'wardNumber' AS ward, " + "(additionaldetails::jsonb)->>'ulbName' AS ulb, "
			+ "district AS region " + "FROM eg_pt_address " + "WHERE TO_TIMESTAMP(createdtime / 1000)::date = "
			+ "TO_DATE(?, 'DD-MM-YYYY')";
	
	private static final String PROPERTY_APPROVER_QUERY = "SELECT e.code " + "FROM eg_userrole_v1 r "
			+ "INNER JOIN eg_hrms_employee e ON r.user_id = e.id " + "WHERE r.role_tenantid = ? "
			+ "AND r.role_code = 'PROPERTY_APPROVER'"
			+ "LIMIT 1";

	public String getUniqueWardsSearchQuery(String stringDate, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(UNIQUE_WARDS_SEARCH_QUERY);
		preparedStmtList.add(stringDate);

		return builder.toString();
	}
	
	public String getDataMetricsSearchQuery(long startEpoch, long endEpoch, String wardName, int slaDays,
			List<Object> preparedStmtList) {

		StringBuilder query = new StringBuilder();

		query.append("SELECT ")

				.append("COUNT(CASE WHEN p.createdtime BETWEEN ? AND ? THEN 1 END) AS todaysTotalApplications, ");
		preparedStmtList.add(startEpoch);// Total applications
		preparedStmtList.add(endEpoch);

		query.append("COUNT(CASE WHEN p.status IN ('APPROVED','REJECTED') ")
				.append("AND p.lastmodifiedtime BETWEEN ? AND ? THEN 1 END) AS todaysClosedApplications, ");
		preparedStmtList.add(startEpoch);// Closed applications
		preparedStmtList.add(endEpoch);

		query.append("COUNT(CASE WHEN p.status = 'APPROVED' ")
				.append("AND p.lastmodifiedtime BETWEEN ? AND ? THEN 1 END) AS todaysApprovedApplications, ");
		preparedStmtList.add(startEpoch);// Approved applications
		preparedStmtList.add(endEpoch);

		query.append("COUNT(CASE WHEN p.status = 'APPROVED' ").append("AND p.lastmodifiedtime BETWEEN ? AND ? ")
				.append("AND ((p.lastmodifiedtime - p.createdtime) / 86400000.0) <= ? ")
				.append("THEN 1 END) AS todaysApprovedApplicationsWithinSLA, ");
		preparedStmtList.add(startEpoch);// Approved within SLA
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(slaDays);

		query.append("COUNT(CASE WHEN p.status NOT IN ('APPROVED','PENDINGFORAPPROVAL') ")
				.append("AND ((EXTRACT(EPOCH FROM NOW())*1000 - p.createdtime) / 86400000.0) > ? ")
				.append("THEN 1 END) AS pendingApplicationsBeyondTimeline, ");
		preparedStmtList.add(slaDays);// Pending beyond SLA


		
		query.append("COALESCE(AVG(CASE WHEN p.status = 'APPROVED' ")
	     .append("AND p.lastmodifiedtime BETWEEN ? AND ? ")
	     .append("THEN ((p.lastmodifiedtime - p.createdtime) / 86400000.0) END), 0) ")
	     .append("AS avgDaysForApplicationApproval, ");
		preparedStmtList.add(startEpoch);// Avg days
		preparedStmtList.add(endEpoch);
		
		query.append("? AS stipulatedDays, ");
		preparedStmtList.add(slaDays);// Stipulated days

		query.append("COUNT(DISTINCT CASE ").append("WHEN b.status = 'PAID' ")
				.append("AND b.createddate BETWEEN ? AND ? ")
				.append("THEN b.consumercode END) AS noOfPropertiesPaidToday ");
		preparedStmtList.add(startEpoch);// Properties paid today
		preparedStmtList.add(endEpoch);

		query.append("FROM eg_pt_property p ").append("JOIN eg_pt_address addr ON addr.propertyid = p.id ")
				.append("LEFT JOIN egbs_bill_v1 b ON b.consumercode = p.propertyid ");

		query.append("WHERE addr.additionaldetails->>'wardNumber' = ? ");
		preparedStmtList.add(wardName);

		return query.toString();
	}

	private static final String TODAYS_MOVED_APPLICATION_QUERY = "SELECT p.status AS status, COUNT(*) AS value "
			+ "FROM eg_pt_property p " + "JOIN eg_pt_address addr ON addr.propertyid = p.id "
			+ "WHERE p.lastmodifiedtime BETWEEN ? AND ? " + "AND addr.additionaldetails->>'wardNumber' = ? "
			+ "GROUP BY p.status " + "ORDER BY p.status";

	public String getTodaysMovedApplicationQuery(long startEpoch, long endEpoch, String wardName,
			List<Object> preparedStmtList) {

		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(wardName);

		return TODAYS_MOVED_APPLICATION_QUERY;
	}

	private static final String PROPERTIES_REGISTERED_FY_QUERY =

			"SELECT " + "CASE " + "WHEN EXTRACT(MONTH FROM TO_TIMESTAMP(p.createdtime/1000)) >= 4 " + "THEN CONCAT( "
					+ "EXTRACT(YEAR FROM TO_TIMESTAMP(p.createdtime/1000)), '-', "
					+ "RIGHT((EXTRACT(YEAR FROM TO_TIMESTAMP(p.createdtime/1000)) + 1)::text, 2)) " + "ELSE CONCAT( "
					+ "EXTRACT(YEAR FROM TO_TIMESTAMP(p.createdtime/1000)) - 1, '-', "
					+ "RIGHT(EXTRACT(YEAR FROM TO_TIMESTAMP(p.createdtime/1000))::text, 2)) " + "END AS FYear, "
					+ "COUNT(*) AS value " + "FROM eg_pt_property p "
					+ "JOIN eg_pt_address addr ON addr.propertyid = p.id "
					+ "WHERE addr.additionaldetails->>'wardNumber' = ? " + "AND p.createdtime BETWEEN ? AND ? "
					+ "GROUP BY 1 " + "ORDER BY 1";

	public String getPropertiesRegisteredFYQuery(long startEpoch, long endEpoch,String wardName, List<Object> preparedStmtList) {
		
		preparedStmtList.add(wardName);
		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		
		return PROPERTIES_REGISTERED_FY_QUERY;
	}
	

	private static final String TRANSACTION_WARDS_QUERY =
		    "SELECT DISTINCT " +
		    "addr.additionaldetails->>'wardNumber' AS ward, " +
		    "addr.additionaldetails->>'ulbName' AS ulb, " +
		    "addr.district AS region " +
		    "FROM eg_pg_transactions t " +
		    "JOIN eg_pt_property p ON p.propertyid = t.consumer_code " +
		    "JOIN eg_pt_address addr ON addr.propertyid = p.id " +
		    "WHERE t.txn_status = 'SUCCESS' " +
		    "AND t.created_time BETWEEN ? AND ?";
	
	public String getTransactionWardsQuery(long startEpoch, long endEpoch, List<Object> preparedStmtList) {

	    preparedStmtList.add(startEpoch);
	    preparedStmtList.add(endEpoch);

	    return TRANSACTION_WARDS_QUERY;
	}

	private static final String ASSESSED_PROPERTIES_QUERY = "SELECT p.usagecategory AS name, COUNT(*) AS value "
			+ "FROM eg_pt_property p JOIN eg_pt_address addr ON addr.propertyid = p.id "
			+ "WHERE p.status = 'APPROVED' AND addr.additionaldetails->>'wardNumber' = ? "
			+ "AND p.createdtime BETWEEN ? AND ? GROUP BY p.usagecategory";

//	private static final String ASSESSED_PROPERTIES_QUERY =
//	        "SELECT u.usagecategoryv2 AS name, COUNT(*) AS value "
//	      + "FROM eg_pt_property p "
//	      + "JOIN eg_pt_unit u ON u.propertyid = p.id "
//	      + "JOIN eg_pt_address addr ON addr.propertyid = p.id "
//	      + "WHERE p.status = 'APPROVED' "
//	      + "AND addr.additionaldetails->>'wardNumber' = ? "
//	      + "AND p.createdtime BETWEEN ? AND ? "
//	      + "GROUP BY u.usagecategoryv2 "
//	      + "ORDER BY u.usagecategoryv2";
	
	public String getAssessedPropertiesQuery(long startEpoch, long endEpoch, String wardName,
			List<Object> preparedStmtList) {

		preparedStmtList.add(wardName);
		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);

		return ASSESSED_PROPERTIES_QUERY;
	}
  //it old query  
//	private static final String TRANSACTION_QUERY = "SELECT p.usagecategoryv2 AS name, COUNT(t.txn_id) AS value "
//			+ "FROM eg_pg_transactions t " + "JOIN eg_pt_property p ON p.propertyid = t.consumer_code "
//			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id WHERE t.txn_status = 'SUCCESS' "
//			+ "AND t.created_time BETWEEN ? AND ? AND addr.additionaldetails->>'wardNumber' = ? "
//			+ "GROUP BY p.usagecategoryv2 ORDER BY p.usagecategoryv2";
//
//	public String getTransactionsQuery(long startEpoch, long endEpoch, String wardName, List<Object> preparedStmtList) {
//
//		preparedStmtList.add(startEpoch);
//		preparedStmtList.add(endEpoch);
//		preparedStmtList.add(wardName);
//
//		return TRANSACTION_QUERY;
//	} 
	
//	public String getTransactionsQuery(long startEpoch, long endEpoch, String wardName, List<Object> preparedStmtList) {
//
//		preparedStmtList.add(startEpoch);
//		preparedStmtList.add(endEpoch);
//		preparedStmtList.add(wardName);
//
//		return TRANSACTION_USAGE_CATEGORY_QUERY;
//	}
	
	//Transaction query end here
//	private static final String TRANSACTION_QUERY =
//	        "SELECT u.usagecategoryv2 AS name,"
//	        + " COUNT(t.txn_id) AS value " +
//	        "FROM eg_pg_transactions t " +
//	        "JOIN eg_pt_property p ON p.propertyid = t.consumer_code " +
//	        "JOIN eg_pt_unit u ON u.propertyid = p.id " +  
//	        "JOIN eg_pt_address addr ON addr.propertyid = p.id " +
//	        "WHERE t.txn_status = 'SUCCESS' " +
//	        "AND t.created_time BETWEEN ? AND ? " +
//	        "AND addr.additionaldetails->>'wardNumber' = ? " +
//	        "GROUP BY u.usagecategoryv2 " +               
//	        "ORDER BY u.usagecategoryv2";
//
//	private static final String TODAY_COLLECTION_QUERY = "SELECT p.usagecategory AS name, "
//			+ "SUM(t.txn_amount) AS totalAmount " + "FROM eg_pg_transactions t "
//			+ "JOIN eg_pt_property p ON p.propertyid = t.consumer_code "
//			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id " + "WHERE t.txn_status = 'SUCCESS' "
//			+ "AND t.created_time BETWEEN ? AND ? " + "AND addr.additionaldetails->>'wardNumber' = ? "
//			+ "GROUP BY p.usagecategory " + "ORDER BY p.usagecategory;";

	//comment 
//	private static final String TODAY_COLLECTION_QUERY =
//	        "SELECT u.usagecategoryv2 AS name, " +
//	        "SUM(t.txn_amount) AS totalAmount " +
//	        "FROM eg_pg_transactions t " +
//	        "JOIN eg_pt_property p ON p.propertyid = t.consumer_code " +
//	        "JOIN eg_pt_unit u ON u.propertyid = p.id " +
//	        "JOIN eg_pt_address addr ON addr.propertyid = p.id " +
//	        "WHERE t.txn_status = 'SUCCESS' " +
//	        "AND t.created_time BETWEEN ? AND ? " +
//	        "AND addr.additionaldetails->>'wardNumber' = ? " +
//	        "GROUP BY u.usagecategoryv2 " +
//	        "ORDER BY u.usagecategoryv2;";
	
////new combine query transactiion and today collection query 
//	private static final String TRANSACTION_AND_COLLECTION_QUERY = "WITH txn_data AS ( " + "SELECT DISTINCT "
//			+ "t.txn_id, " + "t.txn_amount, " + "u.usagecategoryv2 " + "FROM eg_pg_transactions t "
//			+ "JOIN eg_pt_property p ON p.propertyid = t.consumer_code " + "JOIN eg_pt_unit u ON u.propertyid = p.id "
//			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id " + "WHERE t.txn_status = 'SUCCESS' "
//			+ "AND t.created_time BETWEEN ? AND ? " + "AND addr.additionaldetails->>'wardNumber' = ? " + ") "
//			+ "SELECT " + "usagecategoryv2 AS name, " 
//			+ "COUNT(txn_id) AS transactionCount, "
//			+ "SUM(txn_amount) AS totalAmount " + "FROM txn_data " + "GROUP BY usagecategoryv2 "
//			+ "ORDER BY usagecategoryv2";
	
	private static final String TRANSACTION_AND_COLLECTION_PAYMENT_QUERY ="WITH txn_data AS ( " + "SELECT DISTINCT " 
					+ "t.txn_id, " + "t.txn_amount, " + "u.usagecategoryv2, "
					+ "CASE " + "    WHEN t.gateway IN ('RAZORPAY', 'PAYTMPOS') THEN 'Digital' "
					+ "    ELSE 'Non Digital' " + "END AS paymentMode " + "FROM eg_pg_transactions t "
					+ "JOIN eg_pt_property p ON p.propertyid = t.consumer_code "
					+ "JOIN eg_pt_unit u ON u.propertyid = p.id " + "JOIN eg_pt_address addr ON addr.propertyid = p.id "
					+ "WHERE t.txn_status = 'SUCCESS' " + "AND t.product_info = 'PROPERTY' "
					+ "AND t.created_time BETWEEN ? AND ? " + "AND addr.additionaldetails->>'wardNumber' = ? " + ") "

					+ "SELECT " + "usagecategoryv2 AS name, " + "paymentMode, " + "COUNT(txn_id) AS transactionCount, "
					+ "SUM(txn_amount) AS totalAmount " + "FROM txn_data " + "GROUP BY usagecategoryv2, paymentMode "
					+ "ORDER BY usagecategoryv2, paymentMode";
	
	public String getTransactionAndCollectionAndPaymentQuery(long startEpoch, long endEpoch, String wardName,
			List<Object> preparedStmtList) {
		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(wardName);
		return TRANSACTION_AND_COLLECTION_PAYMENT_QUERY;
	}
	
//---end 	
	
	
	
//	public String getTodayCollectionQuery(long startEpoch, long endEpoch, String wardName,
//			List<Object> preparedStmtList) {
//
//		preparedStmtList.add(startEpoch);
//		preparedStmtList.add(endEpoch);
//		preparedStmtList.add(wardName);
//
//		return TODAY_COLLECTION_QUERY;
//	}
//	private static final String PAYMENT_CHANNEL_TYPE_QUERY = "SELECT CASE "
//			+ "WHEN t.gateway IN ('RAZORPAY', 'PAYTMPOS') THEN 'Digital' " + "ELSE 'Non Digital' END AS paymentMode, "
//			+ "SUM(t.txn_amount) AS totalAmount " + "FROM eg_pg_transactions t "
//			+ "JOIN eg_pt_property p ON p.propertyid = t.consumer_code "
//	        + "JOIN eg_pt_unit u ON u.propertyid = p.id "
//			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id " + "WHERE t.txn_status = 'SUCCESS' "
//			+ "AND t.product_info = 'PROPERTY' " + "AND t.created_time BETWEEN ? AND ? "
//			+ "AND addr.additionaldetails->>'wardNumber' = ? " + "GROUP BY CASE "
//			+ "WHEN t.gateway IN ('RAZORPAY', 'PAYTMPOS') THEN 'Digital' " + "ELSE 'Non Digital' END "
//			+ "ORDER BY paymentMode;";
//
//	public String getPaymentChannelTypeQuery(long startEpoch, long endEpoch, String wardName,
//			List<Object> preparedStmtList) {
//
//		preparedStmtList.add(startEpoch);
//		preparedStmtList.add(endEpoch);
//		preparedStmtList.add(wardName);
//
//		return PAYMENT_CHANNEL_TYPE_QUERY;
//	}

//	private static final String PROPERTY_TAX_QUERY = "SELECT p.usagecategory AS name, SUM(t.propertytax) AS value "
//			+ "FROM eg_pt_tax_calculator_tracker t JOIN eg_pt_property p ON p.propertyid = t.propertyid "
//			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id WHERE t.createdtime >= ? "
//			+ "AND t.createdtime < ? AND addr.additionaldetails->>'wardNumber' = ? GROUP BY p.usagecategory "
//			+ "ORDER BY p.usagecategory";
	
	private static final String PROPERTY_TAX_QUERY =
	        "SELECT u.usagecategoryv2 AS name, SUM(t.propertytax) AS value " +
	        "FROM eg_pt_tax_calculator_tracker t " +
	        "JOIN eg_pt_property p ON p.propertyid = t.propertyid " +
	        "JOIN eg_pt_unit u ON u.propertyid = p.id " +
	        "JOIN eg_pt_address addr ON addr.propertyid = p.id " +
	        "WHERE t.createdtime >= ? " +
	        "AND t.createdtime < ? " +
	        "AND addr.additionaldetails->>'wardNumber' = ? " +
	        "GROUP BY u.usagecategoryv2 " +
	        "ORDER BY u.usagecategoryv2";

	public String getPropertyTaxQuery(long startEpoch, long endEpoch, String wardName, List<Object> preparedStmtList) {

		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(wardName);

		return PROPERTY_TAX_QUERY;
	}

	private static final String TAX_METRICS_QUERY =
	        "SELECT u.usagecategoryv2 AS name, " +
	        "SUM(COALESCE(t.propertytax,0)) AS propertyTax, " +
	        "SUM(COALESCE((t.additionaldetails->>'cessAmount')::numeric,0)) AS cess, " +
	        "SUM(COALESCE(t.penaltyamount,0)) AS penalty, " +
	        "SUM(COALESCE((t.additionaldetails->>'interestAmount')::numeric,0)) AS interest, " +
	        "SUM(COALESCE(t.rebateamount,0)) AS rebate " +
	        "FROM eg_pt_tax_calculator_tracker t " +
	        "JOIN eg_pt_property p ON p.propertyid = t.propertyid " +
	        "JOIN eg_pt_unit u ON u.propertyid = p.id " +
	        "JOIN eg_pt_address addr ON addr.propertyid = p.id " +
	        "WHERE t.createdtime >= ? " +
	        "AND t.createdtime < ? " +
	        "AND addr.additionaldetails->>'wardNumber' = ? " +
	        "GROUP BY u.usagecategoryv2 " +
	        "ORDER BY u.usagecategoryv2";

	public String getTaxMetricsQuery(long startEpoch, long endEpoch, String wardName, List<Object> preparedStmtList) {

		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(wardName);

		return TAX_METRICS_QUERY;
	}

	private static final String PAYMENT_CHANNEL_TYPE_QUERY = "SELECT pay.paymentmode AS name, "
			+ "SUM(pay.totalamountpaid) AS value FROM egcl_payment pay "
			+ "JOIN egcl_bill b ON b.id = (pay.additionaldetails->'taxAndPayments'->0->>'billId') "
			+ "JOIN eg_pt_property p ON p.propertyid = b.consumercode "
			+ "JOIN eg_pt_address addr ON addr.propertyid = p.id WHERE pay.paymentstatus = 'DEPOSITED' "
			+ "AND pay.createdtime >= ? AND pay.createdtime < ? "
			+ "AND addr.additionaldetails->>'wardNumber' = ? GROUP BY pay.paymentmode " + "ORDER BY pay.paymentmode";
	
	private static final String PT_TAX_CALCULATOR_TRACKER_UPDATE_QUERY =
	        "UPDATE eg_pt_tax_calculator_tracker eptct";
	
	private static final String PT_TRACKER_UPDATE_BY_PROPERTY_ID = "UPDATE eg_pt_tax_calculator_tracker " +
            "SET bill_status = 'EXPIRED', " +
            "lastmodifiedby = ?, " +
            "lastmodifiedtime = ? " +
            "WHERE propertyid = ? " +
            "AND bill_status IN ('ACTIVE', 'ADVANCE_ADJUSTED')";
	
	private static final String PT_UPDATE_IS_PAYMENT_PROCESSING_QUERY =
	        "UPDATE eg_pt_tax_calculator_tracker " +
	        "SET is_payment_processing = ?, " +
	        "additionaldetails = CASE " +
	        "    WHEN ? = true THEN " +
	        "        jsonb_set( " +
	        "            CASE " +
	        "                WHEN jsonb_typeof(COALESCE(additionaldetails, '{}'::jsonb)) = 'array' " +
	        "                    THEN additionaldetails " +
	        "                ELSE '[{}]'::jsonb " +
	        "            END, " +
	        "            '{0,chequeTxnAmount}', " +
	        "            to_jsonb(CAST(? AS numeric)), " +
	        "            true " +
	        "        ) " +
	        "    ELSE additionaldetails " +
	        "END " +
	        "WHERE bill_id = ?";

			private static final String UPDATE_CUSTOM_TRACKER_AMOUNT =
		    "UPDATE eg_pt_tax_calculator_tracker "
		  + "SET propertytax = :amount, "
		  + "lastmodifiedby = :modifiedBy, "
		  + "lastmodifiedtime = :modifiedTime, "
		  + "additionaldetails = jsonb_set( "
		  + "   CASE "
		  + "       WHEN jsonb_typeof(additionaldetails) = 'object' "
		  + "       THEN additionaldetails "
		  + "       WHEN jsonb_typeof(additionaldetails) = 'array' "
		  + "       THEN jsonb_build_object('data', additionaldetails) "
		  + "       ELSE '{}'::jsonb "
		  + "   END, "
		  + "   '{reason}', "
		  + "   to_jsonb(:reason), "
		  + "   true "
		  + ") "
		  + "WHERE bill_id = :billId "
		  + "AND tenantid = :tenantId";

	public String getPaymentChannelTypeQuery(long startEpoch, long endEpoch, String wardName,
			List<Object> preparedStmtList) {

		preparedStmtList.add(startEpoch);
		preparedStmtList.add(endEpoch);
		preparedStmtList.add(wardName);

		return PAYMENT_CHANNEL_TYPE_QUERY;
	}

	private String addPaginationWrapper(String query, List<Object> preparedStmtList, PropertyCriteria criteria) {

		if (criteria.getIsSchedulerCall()) {
			return query;
		}

		Long limit = config.getDefaultLimit();
		Long offset = config.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() != null && criteria.getLimit() <= config.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > config.getMaxSearchLimit())
			limit = config.getMaxSearchLimit();

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

		preparedStmtList.add(offset);
		preparedStmtList.add(limit + offset);

		return finalQuery;
	}

	public String getPropertySearchQueryForDeafauterNotice(PropertyCriteria criteria, List<Object> preparedStmtList) {

		Boolean isEmpty = null == criteria.getLocality() || null == criteria.getPropertyType()
				|| criteria.getTenantId() == null;

		if (isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR",
					" Please provide tenant, locality, propertyType for the property search for defaulter notice");
		String currYearS;

		int currYear = ((new Date().getYear()) + 1900);
		int currmonth = ((new Date().getMonth()) + 1);
		if (currmonth >= 1 && currmonth <= 3)
			currYearS = (String.valueOf(currYear - 1)) + "-" + (String.valueOf(currYear).substring(2, 4));
		else
			currYearS = (String.valueOf(currYear)) + "-" + (String.valueOf(currYear + 1).substring(2, 4));

		StringBuilder builder = new StringBuilder(PROPERTY_DEFAULTER_SEARCH);
		preparedStmtList.add(criteria.getTenantId());
		if (criteria.getPropertyType().equals("ALL"))
			preparedStmtList.add("%%");
		else
			preparedStmtList.add("%" + criteria.getPropertyType().toUpperCase() + "%");
		preparedStmtList.add(criteria.getLocality());
//		preparedStmtList.add(Status.ACTIVE.toString());
//		preparedStmtList.add(Status.ACTIVE.toString());
		preparedStmtList.add(currYearS);

		return builder.toString();
	}

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, List<Object> preparedStmtList,
			Boolean isPlainSearch, Boolean onlyIds, Map<Integer, PropertyCriteria> propertyCriteriaMap) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
				&& CollectionUtils.isEmpty(criteria.getAcknowledgementIds())
				&& CollectionUtils.isEmpty(criteria.getOldpropertyids()) && CollectionUtils.isEmpty(criteria.getUuids())
				&& null == criteria.getMobileNumber() && null == criteria.getName() && null == criteria.getDoorNo()
				&& null == criteria.getOldPropertyId() && null == criteria.getDocumentNumbers()
				&& null == criteria.getLocality() && null == criteria.getPropertyType()
				&& (null == criteria.getFromDate() && null == criteria.getToDate())
				&& CollectionUtils.isEmpty(criteria.getCreationReason()) && StringUtils.isEmpty(criteria.getTenantId());

		if (isEmpty && !criteria.getIsSchedulerCall())
			throw new CustomException("EG_PT_SEARCH_ERROR", " No criteria given for the property search");

		StringBuilder builder = new StringBuilder();

		builder.append(QUERY);

		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" 1 = ? ");
		preparedStmtList.add(1);

		String whereClause = "";
		if (null != propertyCriteriaMap && !propertyCriteriaMap.isEmpty()) {
			List<String> clause = new ArrayList<>();
			propertyCriteriaMap.entrySet().forEach(propertyCriteriaValue -> {
				clause.add(
						"(" + addWhereClause(propertyCriteriaValue.getValue(), preparedStmtList, isPlainSearch) + ")");
			});
			if (!CollectionUtils.isEmpty(clause) && !clause.contains("()")) {
				addClauseIfRequired(preparedStmtList, builder);
				whereClause = String.join(" OR ", clause);
			}
		} else {
			addClauseIfRequired(preparedStmtList, builder);
			whereClause = addWhereClause(criteria, preparedStmtList, isPlainSearch);
		}

		builder.append(whereClause);

		String withClauseQuery = WITH_CLAUSE_QUERY.replace(REPLACE_STRING, builder);
//		if (onlyIds || criteria.getIsRequestForCount() || StringUtils.isNotEmpty(criteria.getTenantId()))
//			return builder.toString();
//		else 
		return addPaginationWrapper(withClauseQuery, preparedStmtList, criteria);
	}

	private String addWhereClause(PropertyCriteria criteria, List<Object> preparedStmtList, Boolean isPlainSearch) {
		StringBuilder builder = new StringBuilder();
		builder.append(" 1 = ? ");
		preparedStmtList.add(1);
		if (isPlainSearch) {
			Set<String> tenantIds = criteria.getTenantIds();
			if (!CollectionUtils.isEmpty(tenantIds)) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append("property.tenantid IN (").append(createQuery(tenantIds)).append(")");
				addToPreparedStatement(preparedStmtList, tenantIds);
			}
		} else {
			String tenantId = criteria.getTenantId();

			if (tenantId != null) {
				if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" property.tenantId LIKE ? ");
					preparedStmtList.add(tenantId + '%');
				} else {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" property.tenantId= ? ");
					preparedStmtList.add(tenantId);
				}
			}
		}
		if (criteria.getFromDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			// If user does NOT specify toDate, take today's date as the toDate by default
			if (criteria.getToDate() == null) {
				criteria.setToDate(Instant.now().toEpochMilli());
			}
			builder.append("property.createdTime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
		} else {
			if (criteria.getToDate() != null) {
				throw new CustomException("INVALID SEARCH", "From Date should be mentioned first");
			}
		}

		Set<String> statusStringList = new HashSet<>();
		if (!CollectionUtils.isEmpty(criteria.getStatus())) {
			criteria.getStatus().forEach(status -> {
				statusStringList.add(status.toString());
			});
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.status IN ( ").append(createQuery(statusStringList)).append(" )");
			addToPreparedStatement(preparedStmtList, statusStringList);
		}

		Set<String> statusStringList2 = new HashSet<>();
		if (!CollectionUtils.isEmpty(criteria.getStatusList())) {
			criteria.getStatusList().forEach(status -> {
				statusStringList2.add(status.toString());
			});
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.status IN ( ").append(createQuery(statusStringList2)).append(" )");
			addToPreparedStatement(preparedStmtList, statusStringList2);
		}

		Set<String> channelStringList = new HashSet<>();
		if (!CollectionUtils.isEmpty(criteria.getChannels())) {
			criteria.getChannels().forEach(channel -> {
				channelStringList.add(channel.toString());
			});
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.channel IN ( ").append(createQuery(channelStringList)).append(" )");
			addToPreparedStatement(preparedStmtList, channelStringList);
		}

		Set<String> creationReasonsList = criteria.getCreationReason();
		if (!CollectionUtils.isEmpty(creationReasonsList)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.creationreason IN ( ").append(createQuery(creationReasonsList)).append(" )");
			addToPreparedStatement(preparedStmtList, creationReasonsList);
		}

		Set<String> documentNumberList = criteria.getDocumentNumbers();

		if (!CollectionUtils.isEmpty(documentNumberList)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.status='ACTIVE' and owndoc.status='ACTIVE' and owndoc.documentuid  IN ( ")
					.append(createQuery(documentNumberList)).append(" )");
			addToPreparedStatement(preparedStmtList, documentNumberList);
		}

		if (null != criteria.getLocality()) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("address.locality = ?");
			preparedStmtList.add(criteria.getLocality());
		}
		if (null != criteria.getDoorNo()) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("address.doorno = ?");
			preparedStmtList.add(criteria.getDoorNo());
		}

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatement(preparedStmtList, propertyIds);
		}

		Set<String> acknowledgementIds = criteria.getAcknowledgementIds();
		if (!CollectionUtils.isEmpty(acknowledgementIds)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.acknowldgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatement(preparedStmtList, acknowledgementIds);
		}

		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
		}

		Set<String> oldpropertyids = criteria.getOldpropertyids();
		if (!CollectionUtils.isEmpty(oldpropertyids)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.oldpropertyid IN (").append(createQuery(oldpropertyids)).append(")");
			addToPreparedStatement(preparedStmtList, oldpropertyids);
		}

//		if (!CollectionUtils.isEmpty(criteria.getCreatedBy())) {
//			addClauseIfRequired(preparedStmtList, builder);
//			builder.append("property.createdBy IN (").append(createQuery(criteria.getCreatedBy())).append(")");
//			addToPreparedStatement(preparedStmtList, criteria.getCreatedBy());
//		}
//
//		if(!CollectionUtils.isEmpty(criteria.getCreatedBy()) && criteria.getCreatedBy() != null) {
//			if ( criteria.getIsCitizen()) {
//				addClauseIfRequired(preparedStmtList, builder);
//				builder.append("owner.userid IN (").append(createQuery(criteria.getCreatedBy())).append(")");
//				addToPreparedStatement(preparedStmtList, criteria.getCreatedBy());
//			}else {
//				addClauseIfRequired(preparedStmtList, builder);
//				builder.append("property.createdBy IN (").append(createQuery(criteria.getCreatedBy())).append(")");
//				addToPreparedStatement(preparedStmtList, criteria.getCreatedBy());
//			}	
//		}

		if (!CollectionUtils.isEmpty(criteria.getAdditionalDetailsPropertyIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.additionaldetails->>'propertyId' IN (")
					.append(createQuery(criteria.getAdditionalDetailsPropertyIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getAdditionalDetailsPropertyIds());
		}

		if (!CollectionUtils.isEmpty(criteria.getAddressAdditionalDetailsWardNumbers())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("address.additionaldetails->>'wardNumber' IN (")
					.append(createQuery(criteria.getAddressAdditionalDetailsWardNumbers())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getAddressAdditionalDetailsWardNumbers());
		}
		
		if (StringUtils.isNotBlank(criteria.getAddressAdditionalDetailsWardNumber())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("address.additionaldetails->>'wardNumber' = ?");
			preparedStmtList.add(criteria.getAddressAdditionalDetailsWardNumber());
		}

		if (!CollectionUtils.isEmpty(criteria.getOwnerOldCustomerIds())) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("owner.additionaldetails->>'ownerOldCustomerId' IN (")
					.append(createQuery(criteria.getOwnerOldCustomerIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getOwnerOldCustomerIds());
		}

		if (null != criteria.getName()) {
			addClauseIfRequired(preparedStmtList, builder);
			 if (Boolean.TRUE.equals(criteria.getIsFuzzyNameSearch())) {
		        builder.append("owner.name LIKE ?");
		        preparedStmtList.add("%" + criteria.getName() + "%");
		    } else {
		        builder.append("owner.name = ?");
		        preparedStmtList.add(criteria.getName());
		    }
		}

		if (null != criteria.getMobileNumber()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("owner.mobile_number = ?");
			preparedStmtList.add(criteria.getMobileNumber());
		}

		if (null != criteria.getIsActiveUnit() && criteria.getIsActiveUnit()) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("unit.active = ?");
			preparedStmtList.add(criteria.getIsActiveUnit());
		}

		return builder.toString();
	}

	public String getPropertyQueryForBulkSearch(PropertyCriteria criteria, List<Object> preparedStmtList,
			Boolean isPlainSearch) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
				&& CollectionUtils.isEmpty(criteria.getUuids());

		if (isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR",
					" No propertyid or uuid given for the property Bulk search");

		StringBuilder builder = new StringBuilder(QUERY);

		if (isPlainSearch) {
			Set<String> tenantIds = criteria.getTenantIds();
			if (!CollectionUtils.isEmpty(tenantIds)) {
				addClauseIfRequired(preparedStmtList, builder);
				builder.append("property.tenantid IN (").append(createQuery(tenantIds)).append(")");
				addToPreparedStatement(preparedStmtList, tenantIds);
			}
		} else {
			String tenantId = criteria.getTenantId();
			if (tenantId != null) {
				if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" property.tenantId LIKE ? ");
					preparedStmtList.add(tenantId + '%');
				} else {
					addClauseIfRequired(preparedStmtList, builder);
					builder.append(" property.tenantId= ? ");
					preparedStmtList.add(tenantId);
				}
			}
		}
		if (criteria.getFromDate() != null) {
			addClauseIfRequired(preparedStmtList, builder);
			// If user does NOT specify toDate, take today's date as the toDate by default
			if (criteria.getToDate() == null) {
				criteria.setToDate(Instant.now().toEpochMilli());
			}
			builder.append("property.createdTime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
		} else {
			if (criteria.getToDate() != null) {
				throw new CustomException("INVALID SEARCH", "From Date should be mentioned first");
			}
		}

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
		}

		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
		}
		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);
	}

	public String getPropertyIdsQuery(Set<String> ownerIds, String tenantId, List<Object> preparedStmtList) {

		StringBuilder query = new StringBuilder("(");
		query.append(createQuery(ownerIds));
		addToPreparedStatement(preparedStmtList, ownerIds);
		query.append(")");

		StringBuilder propertyIdQuery = new StringBuilder(PROEPRTY_ID_QUERY.replace(REPLACE_STRING, query));

		if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
			propertyIdQuery.append(AND_QUERY);
			propertyIdQuery.append(" tenantId LIKE ? ");
			preparedStmtList.add(tenantId + '%');
		} else {
			propertyIdQuery.append(AND_QUERY);
			propertyIdQuery.append(" tenantId= ? ");
			preparedStmtList.add(tenantId);
		}

		return propertyIdQuery.toString();
	}

	private String createQuery(Set<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, Set<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}

	private void addToPreparedStatementWithUpperCase(List<Object> preparedStmtList, Set<String> ids) {
		ids.forEach(id -> {
			preparedStmtList.add(id.toUpperCase());
		});
	}

	private static void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
		if (values.isEmpty())
			queryString.append(" WHERE ");
		else {
			queryString.append(" AND ");
		}
	}

	public String getpropertyAuditQuery() {
		return PROEPRTY_AUDIT_QUERY;
	}

	public String getTotalApplicationsCountQueryString(PropertyCriteria criteria, List<Object> preparedStatement) {
		preparedStatement.add(criteria.getTenantId());
		return TOTAL_APPLICATIONS_COUNT_QUERY;
	}

	public String getLastExecutionDetail(PropertyCriteria criteria, List<Object> preparedStatement) {
		preparedStatement.add(criteria.getTenantId());
		return LATEST_EXECUTED_MIGRATION_QUERY;
	}

	public String getpropertyAuditEncQuery() {
		return PROPERTY_AUDIT_ENC_QUERY;
	}

	public String getTaxCalculatedPropertiesSearchQuery(PtTaxCalculatorTrackerSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(PT_TAX_CALCULATOR_TRACKER_SEARCH_QUERY);

		builder.append(" WHERE 1 = 1 ");

		if (!StringUtils.isEmpty(criteria.getTenantId())) {
			String tenantId = criteria.getTenantId();

			String[] tenantIdChunks = tenantId.split("\\.");

			if (tenantIdChunks.length == 1) {
				andClauseIfRequired(preparedStmtList, builder);
				builder.append(" eptct.tenantid LIKE ? ");
				preparedStmtList.add(criteria.getTenantId() + '%');
			} else {
				andClauseIfRequired(preparedStmtList, builder);
				builder.append(" eptct.tenantid=? ");
				preparedStmtList.add(criteria.getTenantId());
			}
		}

		if (criteria.getBillId() != null) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.bill_id=? ");
			preparedStmtList.add(criteria.getBillId());
		}
		
		if (!CollectionUtils.isEmpty(criteria.getDemandIds())) {
		    andClauseIfRequired(preparedStmtList, builder);
		    builder.append(" eptct.demand_id IN (").append(createQuery(criteria.getDemandIds())).append(")");
		    addToPreparedStatement(preparedStmtList, criteria.getDemandIds());
		}

		if (!CollectionUtils.isEmpty(criteria.getTenantIds())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.tenantid IN (").append(createQuery(criteria.getTenantIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getTenantIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getUuids())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.uuid IN (").append(createQuery(criteria.getUuids())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getUuids());
		}
		if (!CollectionUtils.isEmpty(criteria.getPropertyIds())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.propertyid IN (").append(createQuery(criteria.getPropertyIds())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getPropertyIds());
		}
		if (!CollectionUtils.isEmpty(criteria.getFinancialYears())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.financialyear IN (").append(createQuery(criteria.getFinancialYears())).append(")");
			addToPreparedStatement(preparedStmtList, criteria.getFinancialYears());
		}
		if (!CollectionUtils.isEmpty(criteria.getBillStatus())) {
			andClauseIfRequired(preparedStmtList, builder);
			Set<String> billStatus = criteria.getBillStatus().stream().map(BillStatus::name)
					.collect(Collectors.toSet());
			builder.append(" eptct.bill_status IN (").append(createQuery(billStatus)).append(")");
			addToPreparedStatement(preparedStmtList, billStatus);
		}
		if (!CollectionUtils.isEmpty(criteria.getNotInBillStatus())) {
			andClauseIfRequired(preparedStmtList, builder);
			Set<String> notInBillStatus = criteria.getNotInBillStatus().stream().map(BillStatus::name)
					.collect(Collectors.toSet());
			builder.append(" eptct.bill_status NOT IN (").append(createQuery(notInBillStatus)).append(")");
			addToPreparedStatement(preparedStmtList, notInBillStatus);
		}
		if (criteria.getType() == "CYCLIC") {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" eptct.type=? ");
			preparedStmtList.add(criteria.getType());
		}
		
//		if (criteria.getRebateamount() != BigDecimal.ZERO) {
//			andClauseIfRequired(preparedStmtList, builder);
//			builder.append(" eptct.rebateamount > 0.00 ");
//			preparedStmtList.add(criteria.getType());
//		}

		return builder.toString();
	}

	public String getTaxCalculatedTenantIdsSearchQuery(PtTaxCalculatorTrackerSearchCriteria criteria,
			List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(PT_TAX_CALCULATOR_TRACKER_TENANT_ID_SEARCH_QUERY);

		builder.append(" WHERE 1 = 1 ");

		if (!CollectionUtils.isEmpty(criteria.getBillStatus())) {
			andClauseIfRequired(preparedStmtList, builder);
			Set<String> billStatus = criteria.getBillStatus().stream().map(BillStatus::name)
					.collect(Collectors.toSet());
			builder.append(" eptct.bill_status IN (").append(createQuery(billStatus)).append(")");
			addToPreparedStatement(preparedStmtList, billStatus);
		}

		if (!CollectionUtils.isEmpty(criteria.getNotInBillStatus())) {
			andClauseIfRequired(preparedStmtList, builder);
			Set<String> notInBillStatus = criteria.getNotInBillStatus().stream().map(BillStatus::name)
					.collect(Collectors.toSet());
			builder.append(" eptct.bill_status NOT IN (").append(createQuery(notInBillStatus)).append(")");
			addToPreparedStatement(preparedStmtList, notInBillStatus);
		}

		return builder.toString();
	}

	private static void andClauseIfRequired(List<Object> values, StringBuilder queryString) {
		queryString.append(" AND");
	}

	public String getStatusBasedCountQuery(TotalCountRequest totalCountRequest, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder();
		builder.append(COUNT_STATUS_BASED_QUERY);
		builder.append(" WHERE 1 = 1 ");
		if (!StringUtils.isEmpty(totalCountRequest.getTenantId())) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.tenantId = ? ");
			preparedStmtList.add(totalCountRequest.getTenantId());
		}
		return builder.toString();
	}

	public String getOwnerSearchQuery(List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder();
		builder.append(PROPERTY_OWNERS_SEARCH_QUERY);

		return builder.toString();
	}

	public String getLimitAndOrderByUpdatedTimeDesc(PtTaxCalculatorTrackerSearchCriteria criteria, String query,
			List<Object> preparedStmtList) {
		StringBuilder queryBuilder = new StringBuilder(query);
		if (null != criteria.getLimit()) {
			queryBuilder.append(" order by eptct.createdtime desc ");
			queryBuilder.append(" limit ? ");
			preparedStmtList.add(criteria.getLimit());
		}

		return queryBuilder.toString();
	}

	public String checkLastCreatedTime(PtTaxCalculatorTrackerSearchCriteria ptTaxCalculatorTrackerSearchCriteria,
			String query, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder(query);

		if (!query.contains("WHERE"))
			builder.append(" WHERE ");
		else
			builder.append(" AND ");

		builder.append(" eptct.createdtime >= ? ");
		preparedStmtList.add(ptTaxCalculatorTrackerSearchCriteria.getStartDateTime());
		if (null != ptTaxCalculatorTrackerSearchCriteria.getEndDateTime()) {
			builder.append(" AND ");
			builder.append(" eptct.createdtime <= ? ");
			preparedStmtList.add(ptTaxCalculatorTrackerSearchCriteria.getEndDateTime());
		}

		return builder.toString();
	}

	public String getPropertyMastersStatusQuery(String ulbName, List<Object> preparedStmtList) {
		StringBuilder builder = new StringBuilder();
		builder.append(CHECK_PROPERTY_MASTER_STATUS_QUERY);
//		builder.append(" WHERE 1 = 1 ");
		if (!StringUtils.isEmpty(ulbName)) {
			andClauseIfRequired(preparedStmtList, builder);
			builder.append(" data->>'ulbName' = ? ");
			preparedStmtList.add(ulbName);
		}
		// add group by and
		builder.append(" GROUP BY data->>'ulbName' ORDER BY ulb_name;");

		return builder.toString();
	}

	public String getOnlyPropertyIdQuery(PropertyCriteria criteria, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder();
		builder.append("SELECT DISTINCT property.propertyid FROM EG_PT_PROPERTY property WHERE 1=1 ");

		if (!CollectionUtils.isEmpty(criteria.getPropertyIds())) {
			Set<String> propertyIds = criteria.getPropertyIds().stream()
					.filter(id -> id != null && !id.trim().isEmpty()).collect(Collectors.toSet());
			if (!propertyIds.isEmpty()) {
				builder.append(" AND property.propertyid IN (").append(createQuery(propertyIds)).append(")");
				addToPreparedStatement(preparedStmtList, propertyIds);
			}
		}

		if (!CollectionUtils.isEmpty(criteria.getOldpropertyids())) {
			Set<String> oldPropertyIds = criteria.getOldpropertyids().stream()
					.filter(id -> id != null && !id.trim().isEmpty()).collect(Collectors.toSet());
			if (!oldPropertyIds.isEmpty()) {
				builder.append(" AND property.oldpropertyid IS NOT NULL AND property.oldpropertyid <> '' ")
						.append(" AND property.oldpropertyid IN (").append(createQuery(oldPropertyIds)).append(")");
				addToPreparedStatement(preparedStmtList, oldPropertyIds);
			}
		}

		if (!CollectionUtils.isEmpty(criteria.getOwnerOldCustomerIds())) {
			Set<String> ownerIds = criteria.getOwnerOldCustomerIds().stream()
					.filter(id -> id != null && !id.trim().isEmpty()).collect(Collectors.toSet());
			if (!ownerIds.isEmpty()) {
				builder.append(
						" AND EXISTS (SELECT 1 FROM EG_PT_OWNER owner WHERE owner.propertyid = property.id AND owner.additionaldetails->>'ownerOldCustomerId' IN (")
						.append(createQuery(ownerIds)).append("))");
				addToPreparedStatement(preparedStmtList, ownerIds);
			}
		}

		if (criteria.getTenantId() != null) {
			builder.append(" AND property.tenantid = ?");
			preparedStmtList.add(criteria.getTenantId());
		}

		return builder.toString();
	}


public String getActiveBillsQuery(String status, List<Object> preparedStmtList,String ulbName, String isforce, String ward, String created_at) {

    StringBuilder builder = new StringBuilder();
    boolean isFirstCondition = true;
    
    LocalDate yesterday = LocalDate.now().minusDays(1);

    long startOfDay = yesterday
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

    long endOfDay = yesterday.plusDays(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli();

    
    builder.append("SELECT * FROM eg_pt_tax_calculator_tracker  WHERE ");
   //builder.append("WHERE bill_status = ? ");
    if (status != null && !status.isEmpty()) {
        builder.append(" bill_status = ? ");
        preparedStmtList.add(status);
        isFirstCondition = false;

     }
    
    if(ulbName !=null && !ulbName.isEmpty()) {
        if (!isFirstCondition) {
            builder.append(" AND ");
        }

    	
        builder.append(" tenantid = ? ");
        preparedStmtList.add(ulbName);

    }
    
    
    
    if(ward !=null && !ward.isEmpty()) {
    	isFirstCondition = false;
    	 builder.append(" AND ");
         builder.append(" ward = ? ");
         preparedStmtList.add(ward);

    }
    
    if(ward != null && isforce != null) {
    	isFirstCondition = true;

    }
    
    
    if (!isFirstCondition) {
        builder.append(" AND ");
        builder.append(" createdtime BETWEEN ? AND ? ");
        preparedStmtList.add(startOfDay);
        preparedStmtList.add(endOfDay);

    }
    
    if(created_at !=null) {
    	
    	ZoneId zone = ZoneId.of("Asia/Kolkata");

    	LocalDate daterange = LocalDate.parse(created_at);

    	long start = daterange.atStartOfDay(zone).toInstant().toEpochMilli();
    	long end = daterange.plusDays(1).atStartOfDay(zone).toInstant().toEpochMilli();
    	builder.append(" AND createdtime BETWEEN ? AND ? ");
    	preparedStmtList.add(start);
    	preparedStmtList.add(end);

    }

    

    builder.append("ORDER BY uuid DESC");

    return builder.toString();
    }

	public String getUpdateStatusQuery(PtTaxCalculatorTracker tracker, List<Object> preparedStmtList) {
	
		StringBuilder builder = new StringBuilder(PT_TAX_CALCULATOR_TRACKER_UPDATE_QUERY);
	
		builder.append(" SET bill_status = ?, lastmodifiedby = ?, lastmodifiedtime = ?, advance_paid = ? ");
		builder.append(" WHERE 1 = 1 ");
		builder.append(" AND (eptct.bill_status = 'ACTIVE' OR eptct.bill_status = 'PARTIALLY_PAID' OR eptct.bill_status = 'ADVANCE_ADJUSTED') ");
	
		preparedStmtList.add(tracker.getBillStatus().name());
		preparedStmtList.add(tracker.getAuditDetails().getLastModifiedBy());
		preparedStmtList.add(tracker.getAuditDetails().getLastModifiedTime());
		preparedStmtList.add(tracker.getAdvancePaid());
	
		if (tracker.getDemandId() != null) {
			builder.append(" AND eptct.demand_id = ? ");
			preparedStmtList.add(tracker.getDemandId());
		}
		
		if (tracker.getPropertyId() != null) {
		    builder.append(" AND eptct.propertyid = ? ");
		    preparedStmtList.add(tracker.getPropertyId());
		}
	
		return builder.toString();
	}
	
	public String getUpdateAdditionalDetailsQuery(PtTaxCalculatorTracker tracker, List<Object> preparedStmtList) {

		StringBuilder builder = new StringBuilder(PT_TAX_CALCULATOR_TRACKER_UPDATE_QUERY);

		builder.append(" SET additionaldetails = CAST(? AS jsonb), ");
		builder.append(" lastmodifiedby = ?, ");
		builder.append(" lastmodifiedtime = ? ");
		builder.append(" WHERE 1 = 1 ");

		preparedStmtList.add(tracker.getAdditionalDetails().toString());
		preparedStmtList.add(tracker.getAuditDetails().getLastModifiedBy());
		preparedStmtList.add(tracker.getAuditDetails().getLastModifiedTime());

		if (tracker.getBillId() != null) {
			builder.append(" AND eptct.bill_id = ? ");
			preparedStmtList.add(tracker.getBillId());
		}

		return builder.toString();
	}
	
	public String getExpireActiveTrackersByPropertyIdQuery() {
	    return PT_TRACKER_UPDATE_BY_PROPERTY_ID;
	}	public String getCustomTrackerUpdateQuery() {
		return UPDATE_CUSTOM_TRACKER_AMOUNT;
	}
	

	public String getPropertyApproverUserNameQuery() {
		return PROPERTY_APPROVER_QUERY;
	}
	public String getUpdateIsProcessingPaymentQuery(List<Object> preparedStmtList, String billId, boolean isPaymentProcessing, String txnAmount) {
		preparedStmtList.add(isPaymentProcessing);
		preparedStmtList.add(isPaymentProcessing);
		preparedStmtList.add(txnAmount);
		preparedStmtList.add(billId);
		return PT_UPDATE_IS_PAYMENT_PROCESSING_QUERY;
	}

}

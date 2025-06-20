package org.egov.pt.repository.builder;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.AppealCriteria;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.Status;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class PropertyQueryBuilder {
	
	@Autowired
	private PropertyConfiguration config;
	
	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN  =  "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";
	
	
	public final static String INSERT_BIFURCATION_DETAILS_QUERY = "INSERT INTO eg_pt_bifurcation"
			+"(parent_property,property_details, max_bifurcation, createdtime, id,status,childpropertyuuid,createdby,lastmodifiedby,lastmodifiedtime)"
			+"VALUES(?, ? ::jsonb, ?, ?, ? ,?,?,?,?,?)";
	
	public final static String INSERT_DASHBOARD_DATA_LOG = "INSERT INTO dashboard_datapush_log"
			+"(date,time, request, response, status, exception_message)"
			+"VALUES(?, ? , ? ::jsonb, ? ::jsonb,?,?)";
	
	public final static String appealselectvalue="appeal.id as id, appeal.propertyid, appeal.status, appeal.creationreason, appeal.acknowldgementnumber,appeal.appealid,(CASE WHEN appeal.status='ACTIVE' then 0 WHEN appeal.status='INWORKFLOW' then 1 WHEN appeal.status='INACTIVE' then 2 ELSE 3 END) as statusorder, appeal.tenantid as tenantid,appeal.propertyaddress,appeal.assesmnetyear,appeal.nameofassigningofficer,appeal.designation,appeal.ruleunderorderpassed,appeal.dateoforder,appeal.dateofservice,appeal.dateofpayment,appeal.ownername,appeal.applicantaddress,appeal.reliefclaimed,appeal.statementoffacts,appeal.groundofappeal,appeal.createdby,appeal.lastmodifiedby,appeal.createdtime,appeal.lastmodifiedtime,";

	private static String PROEPRTY_AUDIT_QUERY = "select property from eg_pt_property_audit where propertyid=?";

	private static String PROPERTY_AUDIT_ENC_QUERY = "select * from eg_pt_property_audit where propertyid=?";

	private static String PROEPRTY_ID_QUERY = "select propertyid from eg_pt_property where id in (select propertyid from eg_pt_owner where userid IN {replace} AND status='ACTIVE') ";

	private static String REPLACE_STRING = "{replace}";
	
	private static String WITH_CLAUSE_QUERY = " WITH propertyresult AS ({replace}) SELECT * FROM propertyresult "
											+ "INNER JOIN (SELECT propertyid, min(statusorder) as minorder FROM propertyresult GROUP BY propertyid) as minresult "
											+ "ON minresult.propertyid=propertyresult.propertyid AND minresult.minorder=propertyresult.statusorder";
	
	private static String WITH_CLAUSE_QUERY_APPEAL = " WITH propertyresult AS ({replace}) SELECT * FROM propertyresult "
			+ "INNER JOIN (SELECT appealid, min(statusorder) as minorder FROM propertyresult GROUP BY appealid) as minresult "
			+ "ON minresult.appealid=propertyresult.appealid AND minresult.minorder=propertyresult.statusorder";
	
	
	private static String PROPERTY_BIFURCATION_QUERY = "select * from eg_pt_bifurcation where parent_property= ";

	 // Select query
	
	private static String propertySelectValues = "property.id as pid, property.propertyid, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.createdtime as pcreatedtime,"
			+ " property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails,property.exemption as exemption, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder,parentpropertyid,ispartofproperty,parentpropertyuuid,vacantusagecategory, BuildingPermission,";
	
	private static String addressSelectValues = "address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails,town_name, ward_no, leikai_name, village, patta_no, proper_house_no, common_name_of_building, principal_road_name, sub_side_road_name, type_of_road, dag_no, " ;

	private static String institutionSelectValues = "institution.id as institutionid,institution.propertyid as institutionpid, institution.tenantid as institutiontenantid, institution.name as institutionname, institution.type as institutiontype, designation, nameofauthorizedperson, institution.createdby as institutioncreatedby, institution.lastmodifiedby as institutionlastmodifiedby, institution.createdtime as institutioncreatedtime, institution.lastmodifiedtime as institutionlastmodifiedtime, ";

	private static String propertyDocSelectValues = "pdoc.id as pdocid, pdoc.tenantid as pdoctenantid, pdoc.entityid as pdocentityid, pdoc.documenttype as pdoctype, pdoc.filestoreid as pdocfilestore, pdoc.documentuid as pdocuid, pdoc.status as pdocstatus, ";

	private static String appealDocSelectValues = "pdoc.id as pdocid, pdoc.tenantid as pdoctenantid, pdoc.entityid as pdocentityid, pdoc.documenttype as pdoctype, pdoc.filestoreid as pdocfilestore, pdoc.documentuid as pdocuid, pdoc.status as pdocstatus,pdoc.createdby,pdoc.lastmodifiedby,pdoc.createdtime,pdoc.lastmodifiedtime ";
	
	private static String ownerSelectValues = "owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime, owner.isaownerdead as isownerdead, owner.dateofdeath as dateofdeath, ";

	private static String ownerDocSelectValues = " owndoc.id as owndocid, owndoc.tenantid as owndoctenantid, owndoc.entityid as owndocentityId, owndoc.documenttype as owndoctype, owndoc.filestoreid as owndocfilestore, owndoc.documentuid as owndocuid, owndoc.status as owndocstatus, ";
	
	private static String UnitSelectValues = "unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime,unit.ageofproperty as ageofproperty, unit.structuretype as structuretype ";

	private static final String TOTAL_APPLICATIONS_COUNT_QUERY = "select count(*) from eg_pt_property where tenantid = ?;";
	
	public final static String TOTAL_APPLICATION_COUNT_WITH_WARD="SELECT count(epa.propertyid),epa.ward_no ,ep.tenantid\r\n"
			+ "  FROM eg_pt_property ep\r\n"
			+ "  join eg_pt_address epa  \r\n"
			+ "  on ep.id =epa.propertyid\r\n"
			+ "  WHERE epa.ward_no !=''\r\n"
			+ "  and ep.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                        AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "  group by ep.tenantid,epa.ward_no";
	
	public final static String TOTAL_ASSESMENT_COUNT_WITH_WARD="select count(epaa.propertyid), epa.ward_no ,ep.tenantid\r\n"
			+ "  FROM eg_pt_property ep\r\n"
			+ "  join eg_pt_address epa  \r\n"
			+ "  on ep.id =epa.propertyid\r\n"
			+ "  join eg_pt_asmt_assessment epaa \r\n"
			+ "  on epaa.propertyid =ep.propertyid \r\n"
			+ "  WHERE epa.ward_no !=''\r\n"
			+ "  and epaa.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                        AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "  group by ep.tenantid,epa.ward_no";
	public final static String TOTAL_PROPERTY_CLOSED_COUNT_WITH_WARD="select count(epaa.propertyid), epa.ward_no ,ep.tenantid\r\n"
			+ "  FROM eg_pt_property ep\r\n"
			+ "  join eg_pt_address epa  \r\n"
			+ "  on ep.id =epa.propertyid\r\n"
			+ "  join eg_pt_asmt_assessment epaa \r\n"
			+ "  on epaa.propertyid =ep.propertyid \r\n"
			+ "  WHERE epa.ward_no !=''\r\n"
			+ "  and ep.status ='ACTIVE'\r\n"
			+ "  and ep.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                        AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "  group by ep.tenantid,epa.ward_no";
	public final static String TOTAL_PROPERTY_PAID_COUNT_WITH_WARD="select count(epp.propertyid),epa.ward_no ,epp.tenantid from eg_pt_property epp \r\n"
			+ "join eg_pt_address epa on epp.id =epa.propertyid \r\n"
			+ "join eg_pg_transactions ept on epp.propertyid =ept.consumer_code\r\n"
			+ "join egcl_payment ep on ept.txn_id =ep.transactionnumber \r\n"
			+ "and epa.ward_no !=''\r\n"
			+ "and ep.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "			AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "			  group by epp.tenantid,epa.ward_no";
	public final static String TOTAL_PROPERTY_APPROVED_WITH_WARD="SELECT count(ewpv.businessid) ,ep.tenantid,epadd.ward_no\r\n"
			+ "FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "JOIN eg_pt_property ep ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "JOIN eg_pt_address epadd ON ep.id = epadd.propertyid\r\n"
			+ "WHERE ewpv.\"action\" != '' AND epadd.ward_no != '' and ewpv.\"action\" ='APPROVE'\r\n"
			+ "AND ewpv.lastmodifiedtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "GROUP BY ep.tenantid, epadd.ward_no";
	public final static String TOTAL_MOVED_APPLICATION_COUNT_WITH_WARD="SELECT CASE\r\n"
			+ "    WHEN ewpv.\"action\" = 'OPEN' THEN 'OPEN'\r\n"
			+ "    WHEN ewpv.\"action\" = 'VERIFY' THEN 'DOCVERIFIED'\r\n"
			+ "    WHEN ewpv.\"action\" = 'FORWARD' THEN 'FIELDVERIFIED'\r\n"
			+ "    WHEN ewpv.\"action\" = 'REJECT' THEN 'REJECTED'\r\n"
			+ "    WHEN ewpv.\"action\" = 'REOPEN' THEN 'CORRECTIONPENDING'\r\n"
			+ "    WHEN ewpv.\"action\" = 'APPROVE' THEN 'APPROVE'\r\n"
			+ "  END AS action_st,\r\n"
			+ "  COUNT(*) AS count,\r\n"
			+ "  ep.tenantid, epadd.ward_no\r\n"
			+ "FROM eg_wf_processinstance_v2 ewpv\r\n"
			+ "JOIN eg_pt_property ep ON ep.acknowldgementnumber = ewpv.businessid\r\n"
			+ "join eg_pt_address  epadd on ep.id = epadd.propertyid\r\n"
			+ "where ewpv.\"action\" !='' and epadd.ward_no !=''\r\n"
			+ "and ewpv.lastmodifiedtime between\r\n"
			+ "EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "GROUP BY action_st, ep.tenantid,epadd.ward_no order by ep.tenantid, epadd.ward_no";
	public final static String TOTAL_PROPERTY_REGISTERED_COUNT_WITH_WARD="select epaa.financialyear,count(epaa.propertyid),epp.tenantid,epa.ward_no from eg_pt_asmt_assessment epaa \r\n"
			+ "join eg_pt_property epp on epaa.propertyid =epp.propertyid \r\n"
			+ "join eg_pt_address epa on epp.id=epa.propertyid \r\n"
			+ "where epa.ward_no !=''\r\n"
			+ "and epaa.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "group by epp.tenantid ,epa.ward_no ,epaa.financialyear";
	public final static String TOTAL_ASSEDPROPERTIES_COUNT_WITH_WARD="select epp.usagecategory ,count(epp.propertyid),epp.tenantid,epa.ward_no from eg_pt_asmt_assessment epaa \r\n"
			+ "join eg_pt_property epp on epaa.propertyid =epp.propertyid \r\n"
			+ "join eg_pt_address epa on epp.id=epa.propertyid \r\n"
			+ "where epa.ward_no !=''\r\n"
			+ "and epaa.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "group by epp.tenantid ,epa.ward_no ,epp.usagecategory";
	public final static String TOTAL_TRANSACTIONS_COUNT_WITH_WARD="select epp.usagecategory,count(epp.propertyid),epp.tenantid,epa.ward_no from eg_pg_transactions ept \r\n"
			+ "join eg_pt_property epp on ept.consumer_code =epp.propertyid \r\n"
			+ "join eg_pt_address epa on epp.id=epa.propertyid \r\n"
			+ "where ept.txn_status ='SUCCESS' and epa.ward_no !=''\r\n"
			+ "and ept.created_time BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "group by epp.tenantid ,epa.ward_no ,epp.usagecategory";
	public final static String TOTAL_TODAYS_COLLECTION_WITH_WARD="select epp.usagecategory,sum(ep.totalamountpaid) as todayscollection,epa.ward_no ,epp.tenantid from eg_pt_property epp \r\n"
			+ "join eg_pt_address epa on epp.id =epa.propertyid \r\n"
			+ "join eg_pg_transactions ept on epp.propertyid =ept.consumer_code\r\n"
			+ "join egcl_payment ep on ept.txn_id =ep.transactionnumber \r\n"
			+ "and epa.ward_no !=''\r\n"
			+ "and ep.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "	AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "	group by epp.tenantid,epa.ward_no,epp.propertyid ,epp.usagecategory";
	public final static String TOTAL_PROPERTY_COUNT_WITH_WARD="select epp.usagecategory,count(epp.propertyid),epp.tenantid,epa.ward_no from eg_pt_property epp join\r\n"
			+ "eg_pt_address epa on epp.id =epa.propertyid \r\n"
			+ "where epa.ward_no !='' and epp.usagecategory !=''\r\n"
			+ "and epp.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "group by epp.tenantid ,epa.ward_no ,epp.usagecategory";
	public final static String TOTAL_REBATE_COLLECTION_WITH_WARD="WITH paid_bills AS (\r\n"
			+ "    SELECT \r\n"
			+ "        ebv2.demandid,\r\n"
			+ "        ebv2.tenantid,\r\n"
			+ "        SUM(ebv2.totalamount) AS billsum\r\n"
			+ "    FROM egbs_bill_v1 ebv\r\n"
			+ "    JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "    WHERE ebv.status = 'PAID'\r\n"
			+ "      AND ebv2.createddate BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY ebv2.demandid, ebv2.tenantid\r\n"
			+ "),\r\n"
			+ "total_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        edv.id AS demandid,\r\n"
			+ "        SUM(edv2.taxamount) AS demandsum\r\n"
			+ "    FROM egbs_demand_v1 edv\r\n"
			+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
			+ "    WHERE edv2.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY edv.id\r\n"
			+ "),\r\n"
			+ "rebate_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        edv.id AS demandid,\r\n"
			+ "        epp.tenantid,\r\n"
			+ "        epa.ward_no,\r\n"
			+ "        epp.usagecategory,\r\n"
			+ "        SUM(edv2.taxamount) AS rebate_amount\r\n"
			+ "    FROM egbs_demand_v1 edv\r\n"
			+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
			+ "    JOIN eg_pt_property epp ON epp.propertyid = edv.consumercode\r\n"
			+ "    JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "    WHERE edv2.taxheadcode IN ('PT_COMPLEMENTARY_REBATE', 'PT_MODEOFPAYMENT_REBATE')\r\n"
			+ "      AND edv2.taxamount < 0\r\n"
			+ "      AND epa.ward_no != ''\r\n"
			+ "      AND edv2.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY edv.id, epp.tenantid, epa.ward_no, epp.usagecategory\r\n"
			+ ")\r\n"
			+ "SELECT \r\n"
			+ "    rd.usagecategory,\r\n"
			+ "    SUM(rd.rebate_amount)::BIGINT AS todayrebategiven,\r\n"
			+ "    rd.tenantid,\r\n"
			+ "    rd.ward_no\r\n"
			+ "FROM rebate_demand rd\r\n"
			+ "JOIN paid_bills pb ON rd.demandid = pb.demandid\r\n"
			+ "JOIN total_demand td ON rd.demandid = td.demandid\r\n"
			+ "GROUP BY rd.tenantid, rd.ward_no, rd.usagecategory\r\n"
			+ "HAVING SUM(pb.billsum) >= SUM(td.demandsum);\r\n"
			+ "";
	public final static String TOTAL_PENALTY_COLLECTED_WITH_WARD="WITH paid_bills AS (\r\n"
			+ "    SELECT \r\n"
			+ "        ebv2.demandid,\r\n"
			+ "        ebv2.tenantid,\r\n"
			+ "        SUM(ebv2.totalamount) AS billsum\r\n"
			+ "    FROM egbs_bill_v1 ebv\r\n"
			+ "    JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "    WHERE ebv.status = 'PAID'\r\n"
			+ "      AND ebv2.createddate BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY ebv2.demandid, ebv2.tenantid\r\n"
			+ "),\r\n"
			+ "total_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        edv.id AS demandid,\r\n"
			+ "        SUM(edv2.taxamount) AS demandsum\r\n"
			+ "    FROM egbs_demand_v1 edv\r\n"
			+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
			+ "    WHERE edv2.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY edv.id\r\n"
			+ "),\r\n"
			+ "penalty_demand AS (\r\n"
			+ "    SELECT \r\n"
			+ "        edv.id AS demandid,\r\n"
			+ "        epp.tenantid,\r\n"
			+ "        epa.ward_no,\r\n"
			+ "        epp.usagecategory,\r\n"
			+ "        SUM(edv2.taxamount)::BIGINT AS penalty_amount\r\n"
			+ "    FROM egbs_demand_v1 edv\r\n"
			+ "    JOIN egbs_demanddetail_v1 edv2 ON edv.id = edv2.demandid\r\n"
			+ "    JOIN eg_pt_property epp ON epp.propertyid = edv.consumercode\r\n"
			+ "    JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "    WHERE edv2.taxheadcode IN ('PT_PAST_DUE_PENALTY', 'PT_TIME_PENALTY')\r\n"
			+ "      AND edv2.taxamount > 0\r\n"
			+ "      AND epa.ward_no != ''\r\n"
			+ "      AND edv2.createdtime BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000\r\n"
			+ "                              AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "    GROUP BY edv.id, epp.tenantid, epa.ward_no, epp.usagecategory\r\n"
			+ ")\r\n"
			+ "SELECT \r\n"
			+ "    pd.usagecategory,\r\n"
			+ "    SUM(pd.penalty_amount)::BIGINT AS todaypenaltycollection,\r\n"
			+ "	pd.tenantid,\r\n"
			+ "    pd.ward_no\r\n"
			+ "FROM penalty_demand pd\r\n"
			+ "JOIN paid_bills pb ON pd.demandid = pb.demandid\r\n"
			+ "JOIN total_demand td ON pd.demandid = td.demandid\r\n"
			+ "GROUP BY pd.tenantid, pd.ward_no, pd.usagecategory\r\n"
			+ "HAVING SUM(pb.billsum) >= SUM(td.demandsum);\r\n"
			+ "";
	public final static String TOTAL_INTEREST_COLLECTED_WITH_WARD="SELECT epp.usagecategory,SUM(ebv2.interestonamount) as todayinterstcollection,epp.tenantid,epa.ward_no\r\n"
			+ "FROM egbs_bill_v1 ebv JOIN egbs_billdetail_v1 ebv2 ON ebv.id = ebv2.billid\r\n"
			+ "JOIN eg_pt_property epp ON epp.propertyid = ebv2.consumercode\r\n"
			+ "JOIN eg_pt_address epa ON epp.id = epa.propertyid\r\n"
			+ "WHERE epa.ward_no != '' AND ebv.status = 'PAID'AND ebv2.interestonamount > 0\r\n"
			+ "AND (ebv2.createddate BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000 AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000\r\n"
			+ "OR ebv2.lastmodifieddate BETWEEN EXTRACT(EPOCH FROM CURRENT_DATE) * 1000 AND EXTRACT(EPOCH FROM CURRENT_DATE + INTERVAL '1 day' - INTERVAL '1 second') * 1000 )\r\n"
			+ "GROUP BY epp.tenantid, epa.ward_no, epp.usagecategory";
	private static final String QUERY = SELECT 
			
			+	propertySelectValues    
			
			+   addressSelectValues     
			
			+   institutionSelectValues 
			
			+   propertyDocSelectValues
			
			+   ownerSelectValues 
			
			+   ownerDocSelectValues  
			
			+   UnitSelectValues

			+   " FROM EG_PT_PROPERTY property " 
			
			+   INNER_JOIN +  " EG_PT_ADDRESS address         ON property.id = address.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "
			
			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "
			
			+	LEFT_JOIN  +  " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";
	

	private static final String ID_QUERY = SELECT

			+   " property.id FROM EG_PT_PROPERTY property "

			+   INNER_JOIN +  " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+	LEFT_JOIN  +  " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";
	
	private static final String COUNT_QUERY = SELECT

			+   " count(distinct property.id) FROM EG_PT_PROPERTY property "

			+   INNER_JOIN +  " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+	LEFT_JOIN  +  " EG_PT_UNIT unit		          ON property.id =  unit.propertyid ";
	

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY plastmodifiedtime DESC, pid) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

	private static final String LATEST_EXECUTED_MIGRATION_QUERY = "select * from eg_pt_enc_audit where tenantid = ? order by createdTime desc limit 1;";
	
	
private static final String APPEAL_QUERY = SELECT 
			
			+	appealselectvalue
			
			+   appealDocSelectValues

			+   " FROM eg_pt_property_appeal appeal " 
			
			+ INNER_JOIN +  " EG_PT_DOCUMENT pdoc   ON appeal.id = pdoc.entityid ";

	private String addPaginationWrapper(String query, List<Object> preparedStmtList, PropertyCriteria criteria) {
		
		
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
	
private String appealAddPaginationWrapper(String query, List<Object> preparedStmtList, AppealCriteria criteria) {
		
		
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

	/**
	 * 
	 * @param criteria
	 * @param preparedStmtList
	 * @return
	 */
	public String getPropertySearchQuery(PropertyCriteria criteria, List<Object> preparedStmtList,Boolean isPlainSearch, Boolean onlyIds) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
					&& CollectionUtils.isEmpty(criteria.getAcknowledgementIds())
					&& CollectionUtils.isEmpty(criteria.getOldpropertyids())
					&& CollectionUtils.isEmpty(criteria.getUuids())
					&& null == criteria.getMobileNumber()
					&& null == criteria.getName()
					&& null == criteria.getDoorNo()
					&& null == criteria.getOldPropertyId()
					&& null == criteria.getLocality()
					&& (null == criteria.getFromDate() && null == criteria.getToDate())
					&& CollectionUtils.isEmpty(criteria.getCreationReason());
		
		if(isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR"," No criteria given for the property search");
		
		StringBuilder builder;

		if (onlyIds)
			builder = new StringBuilder(ID_QUERY);
		
		else if (criteria.getIsRequestForCount()) {
			builder = new StringBuilder(COUNT_QUERY);
			
		} else
			builder = new StringBuilder(QUERY);

		if(isPlainSearch)
		{
			Set<String> tenantIds = criteria.getTenantIds();
			if(!CollectionUtils.isEmpty(tenantIds))
			{
				addClauseIfRequired(preparedStmtList,builder);
				builder.append("property.tenantid IN (").append(createQuery(tenantIds)).append(")");
				addToPreparedStatement(preparedStmtList,tenantIds);
			}
		}
		else
		{
			String tenantId = criteria.getTenantId();
			
			if (tenantId != null) {
				if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId LIKE ? ");
					preparedStmtList.add(tenantId + '%');
				} else {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId= ? ");
					preparedStmtList.add(tenantId);
				}
			}
		}
		if (criteria.getFromDate() != null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			// If user does NOT specify toDate, take today's date as the toDate by default
			if (criteria.getToDate() == null)
			{
				criteria.setToDate(Instant.now().toEpochMilli());
			}
			builder.append("property.createdTime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
		}
		else
		{
			if(criteria.getToDate()!=null)
			{
				throw new CustomException("INVALID SEARCH", "From Date should be mentioned first");
			}
		}

		Set<String> statusStringList = new HashSet<>();
		if (!CollectionUtils.isEmpty(criteria.getStatus())) {
			criteria.getStatus().forEach(status -> {
				statusStringList.add(status.toString());
			});
			addClauseIfRequired(preparedStmtList,builder);
			builder.append(" property.status IN ( ")
				.append(createQuery(statusStringList))
				.append(" )");
			addToPreparedStatement(preparedStmtList, statusStringList);
		}

		Set<String> creationReasonsList = criteria.getCreationReason();
		if(!CollectionUtils.isEmpty(creationReasonsList)){
			addClauseIfRequired(preparedStmtList, builder);
			builder.append("property.creationreason IN ( ").append(createQuery(creationReasonsList)).append(" )");
			addToPreparedStatement(preparedStmtList, creationReasonsList);
		}
		
		if (null != criteria.getLocality()) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("address.locality = ?");
			preparedStmtList.add(criteria.getLocality());
		}
		
		
		//Added to ES Issue Removal
		  if (null != criteria.getDoorNo()) {
		  addClauseIfRequired(preparedStmtList,builder);
		  builder.append("address.doorno = ?");
		  preparedStmtList.add(criteria.getDoorNo());
		  }
		 

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
		}
		
		Set<String> acknowledgementIds = criteria.getAcknowledgementIds();
		if (!CollectionUtils.isEmpty(acknowledgementIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.acknowldgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, acknowledgementIds);
		}
		
		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
		}

		Set<String> oldpropertyids = criteria.getOldpropertyids();
		if (!CollectionUtils.isEmpty(oldpropertyids)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.oldpropertyid IN (").append(createQuery(oldpropertyids)).append(")");
			addToPreparedStatement(preparedStmtList, oldpropertyids);
		}
		
		if (!CollectionUtils.isEmpty(oldpropertyids)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.oldpropertyid IN (").append(createQuery(oldpropertyids)).append(")");
			addToPreparedStatement(preparedStmtList, oldpropertyids);
		}
		
		if (!StringUtils.isEmpty(criteria.getParentPropertyId())) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.parentPropertyId IN ('").append(criteria.getParentPropertyId()).append("')");
		}
		
		if (criteria.getIsPartOfProperty()) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.ispartofproperty =?");
			preparedStmtList.add(criteria.getIsPartOfProperty());
		}
		
		/* 
		 * Condition to evaluate if owner is active.
		 * Inactive owners should never be shown in results
		*/
		
		addClauseIfRequired(preparedStmtList,builder);
		builder.append("owner.status = ?");
		preparedStmtList.add(Status.ACTIVE.toString());
		

		String withClauseQuery = WITH_CLAUSE_QUERY.replace(REPLACE_STRING, builder);
		if (onlyIds || criteria.getIsRequestForCount())
			return builder.toString();
		else 
			return addPaginationWrapper(withClauseQuery, preparedStmtList, criteria);
	}
	
	
	
	
	public String getAppealSearchQuery(AppealCriteria criteria, List<Object> preparedStmtList) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
				|| CollectionUtils.isEmpty(criteria.getApplicationNumber());
		
		if(isEmpty)
			throw new CustomException("EG_PT_APPEAL_SEARCH_ERROR"," No criteria given for the property search");
		
		StringBuilder builder;

		
			builder = new StringBuilder(APPEAL_QUERY);

		
			String tenantId = criteria.getTenantId();
			
			if (tenantId != null) {
				if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId LIKE ? ");
					preparedStmtList.add(tenantId + '%');
				} else {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId= ? ");
					preparedStmtList.add(tenantId);
				}
			}
		
		
		/*
		 * Set<String> statusStringList = new HashSet<>(); if
		 * (!CollectionUtils.isEmpty(criteria.getStatus())) {
		 * criteria.getStatus().forEach(status -> {
		 * statusStringList.add(status.toString()); });
		 * addClauseIfRequired(preparedStmtList,builder);
		 * builder.append(" property.status IN ( ")
		 * .append(createQuery(statusStringList)) .append(" )");
		 * addToPreparedStatement(preparedStmtList, statusStringList); }
		 */

		

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
		}
		
		Set<String> acknowledgementIds = criteria.getApplicationNumber();
		if (!CollectionUtils.isEmpty(acknowledgementIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.acknowldgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, acknowledgementIds);
		}
		
		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
		}

			String withClauseQuery = WITH_CLAUSE_QUERY_APPEAL.replace(REPLACE_STRING, builder);
			return appealAddPaginationWrapper(withClauseQuery, preparedStmtList, criteria);
	}



	public String getPropertyQueryForBulkSearch(PropertyCriteria criteria, List<Object> preparedStmtList,Boolean isPlainSearch) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
				&& CollectionUtils.isEmpty(criteria.getUuids());

		if(isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR"," No propertyid or uuid given for the property Bulk search");

		StringBuilder builder = new StringBuilder(QUERY);

		if(isPlainSearch)
		{
			Set<String> tenantIds = criteria.getTenantIds();
			if(!CollectionUtils.isEmpty(tenantIds))
			{
				addClauseIfRequired(preparedStmtList,builder);
				builder.append("property.tenantid IN (").append(createQuery(tenantIds)).append(")");
				addToPreparedStatement(preparedStmtList,tenantIds);
			}
		}
		else
		{
			String tenantId = criteria.getTenantId();
			if (tenantId != null) {
				if (tenantId.equalsIgnoreCase(config.getStateLevelTenantId())) {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId LIKE ? ");
					preparedStmtList.add(tenantId + '%');
				} else {
					addClauseIfRequired(preparedStmtList,builder);
					builder.append(" property.tenantId= ? ");
					preparedStmtList.add(tenantId);
				}
			}
		}
		if (criteria.getFromDate() != null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			// If user does NOT specify toDate, take today's date as the toDate by default
			if (criteria.getToDate() == null)
			{
				criteria.setToDate(Instant.now().toEpochMilli());
			}
			builder.append("property.createdTime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
		}
		else
		{
			if(criteria.getToDate()!=null)
			{
				throw new CustomException("INVALID SEARCH", "From Date should be mentioned first");
			}
		}

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
		}

		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList,builder);
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

		if(tenantId.equalsIgnoreCase(config.getStateLevelTenantId())){
			propertyIdQuery.append(AND_QUERY);
			propertyIdQuery.append(" tenantId LIKE ? ");
			preparedStmtList.add(tenantId + '%');
		}
		else{
			propertyIdQuery.append(AND_QUERY);
			propertyIdQuery.append(" tenantId= ? ");
			preparedStmtList.add(tenantId);
		}

		return propertyIdQuery.toString();
	}
	
	
	
	
	public String getBifurcationPropertyIdsQuery( String parentPropertyId) {
		StringBuilder propertyIdQuery = new StringBuilder(PROPERTY_BIFURCATION_QUERY);
		propertyIdQuery.append("'"+parentPropertyId+"'");
		propertyIdQuery.append(" ORDER BY createdtime DESC");
		return propertyIdQuery.toString();
	}

	
	public String getAppealsearchQuery(AppealCriteria appealCriteria, List<Object> preparedStmtList)
	{
		StringBuilder builder=new StringBuilder(APPEAL_QUERY);
		
		String tenantId = appealCriteria.getTenantId();
		if(tenantId!=null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("appeal.tenantid= ?");
			preparedStmtList.add(tenantId);
		}
		Set<String> propertyId = appealCriteria.getPropertyIds();
		if(propertyId!=null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("appeal.propertyid IN (").append(createQuery(propertyId)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyId);
		}
		
		Set<String> acknowledgementIds=appealCriteria.getApplicationNumber();
		if(acknowledgementIds!=null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("appeal.acknowldgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, acknowledgementIds);
		}
		
		Set<String> appealid=appealCriteria.getAppealid();
		if(appealid!=null)
		{
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("appeal.appealid IN (").append(createQuery(appealid)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, appealid);
		}
		
		return builder.toString();
		
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

	private static void addClauseIfRequired(List<Object> values,StringBuilder queryString) {
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

}

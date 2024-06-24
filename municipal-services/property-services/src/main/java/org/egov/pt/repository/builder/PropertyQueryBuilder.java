package org.egov.pt.repository.builder;
import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.time.Instant;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.models.enums.Status;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Component
public class PropertyQueryBuilder {
	
	@Autowired
	private PropertyConfiguration config;
	
	private static final String SELECT = "SELECT ";
	private static final String INNER_JOIN = "INNER JOIN";
	private static final String LEFT_JOIN  =  "LEFT OUTER JOIN";
	private static final String AND_QUERY = " AND ";
	
	private static final String PROPERTY_DEFAULTER_SEARCH="select * from (SELECT property.id as pid, property.propertyid, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.createdtime as pcreatedtime, property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder, address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails, owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus,owner.additionaldetails as oadditionaldetails, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime, unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime  FROM EG_PT_PROPERTY property INNER JOIN EG_PT_ADDRESS address         ON property.id = address.propertyid INNER JOIN EG_PT_OWNER owner             ON property.id = owner.propertyid  LEFT OUTER JOIN EG_PT_UNIT unit ON property.id =  unit.propertyid  WHERE  property.tenantId= ?  AND property.usagecategory like ? AND address.locality = ? AND owner.status = ? AND property.status = ?) as propertydata"
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
	
	private static String propertySelectValues = "property.id as pid, property.propertyid, property.tenantid as ptenantid, surveyid, accountid, oldpropertyid, property.status as propertystatus, acknowldgementnumber, propertytype, ownershipcategory,property.usagecategory as pusagecategory, creationreason, nooffloors, landarea, property.superbuiltuparea as propertysbpa, linkedproperties, source, channel, property.createdby as pcreatedby, property.lastmodifiedby as plastmodifiedby, property.createdtime as pcreatedtime,"
	                + " property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 WHEN property.status='PENDINGWS' then 3  ELSE 4 END) as statusorder, ";
			//+ " property.lastmodifiedtime as plastmodifiedtime, property.additionaldetails as padditionaldetails, (CASE WHEN property.status='ACTIVE' then 0 WHEN property.status='INWORKFLOW' then 1 WHEN property.status='INACTIVE' then 2 ELSE 3 END) as statusorder, ";

	private static String addressSelectValues = "address.tenantid as adresstenantid, address.id as addressid, address.propertyid as addresspid, latitude, longitude, doorno, plotno, buildingname, street, landmark, city, pincode, locality, district, region, state, country, address.createdby as addresscreatedby, address.lastmodifiedby as addresslastmodifiedby, address.createdtime as addresscreatedtime, address.lastmodifiedtime as addresslastmodifiedtime, address.additionaldetails as addressadditionaldetails, " ;

	private static String institutionSelectValues = "institution.id as institutionid,institution.propertyid as institutionpid, institution.tenantid as institutiontenantid, institution.name as institutionname, institution.type as institutiontype, designation, nameofauthorizedperson, institution.createdby as institutioncreatedby, institution.lastmodifiedby as institutionlastmodifiedby, institution.createdtime as institutioncreatedtime, institution.lastmodifiedtime as institutionlastmodifiedtime, ";

	private static String propertyDocSelectValues = "pdoc.id as pdocid, pdoc.tenantid as pdoctenantid, pdoc.entityid as pdocentityid, pdoc.documenttype as pdoctype, pdoc.filestoreid as pdocfilestore, pdoc.documentuid as pdocuid, pdoc.status as pdocstatus, ";

	private static String ownerSelectValues = "owner.tenantid as owntenantid, ownerInfoUuid, owner.propertyid as ownpropertyid, userid, owner.status as ownstatus, isprimaryowner, ownertype, ownershippercentage, owner.institutionid as owninstitutionid, relationship, owner.createdby as owncreatedby, owner.createdtime as owncreatedtime,owner.lastmodifiedby as ownlastmodifiedby, owner.lastmodifiedtime as ownlastmodifiedtime,owner.additionaldetails as oadditionaldetails,  ";
	
	
	private static String ownerDocSelectValues = " owndoc.id as owndocid, owndoc.tenantid as owndoctenantid, owndoc.entityid as owndocentityId, owndoc.documenttype as owndoctype, owndoc.filestoreid as owndocfilestore, owndoc.documentuid as owndocuid, owndoc.status as owndocstatus, ";
	//
	private static String UnitSelectValues = "unit.id as unitid, unit.tenantid as unittenantid, unit.propertyid as unitpid, floorno, unittype, unit.usagecategory as unitusagecategory, occupancytype, occupancydate, carpetarea, builtuparea, plintharea, unit.superbuiltuparea as unitspba, arv, constructiontype, constructiondate, dimensions, unit.active as isunitactive, unit.additionaldetails as unitadditionaldetails, unit.createdby as unitcreatedby, unit.createdtime as unitcreatedtime, unit.lastmodifiedby as unitlastmodifiedby, unit.lastmodifiedtime as unitlastmodifiedtime ";
	private static final String TOTAL_APPLICATIONS_COUNT_QUERY = "select count(*) from eg_pt_property where tenantid = ?;";
	             	
	private static final String QUERY = SELECT 
			
			+	propertySelectValues    
			
			+   addressSelectValues     
			
			+   institutionSelectValues 
			
			+   propertyDocSelectValues
			
			+   ownerSelectValues 
			
			+   ownerDocSelectValues  
			
			+   UnitSelectValues

			+   " FROM EG_PT_PROPERTY property " 
			
			+   INNER_JOIN +  " EG_PT_ADDRESS address ON property.id = address.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "
			
			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid " 
			
			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "
			
			+	LEFT_JOIN  +  " EG_PT_UNIT unit ON property.id =  unit.propertyid ";
	

	private static final String ID_QUERY = SELECT

			+   " property.id FROM EG_PT_PROPERTY property "

			+   INNER_JOIN +  " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+	LEFT_JOIN  +  " EG_PT_UNIT unit ON property.id =  unit.propertyid ";
	
	private static final String COUNT_QUERY = SELECT

			+   " count(distinct property.id) FROM EG_PT_PROPERTY property "

			+   INNER_JOIN +  " EG_PT_ADDRESS address         ON property.id = address.propertyid "

			+   LEFT_JOIN  +  " EG_PT_INSTITUTION institution ON property.id = institution.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT pdoc           ON property.id = pdoc.entityid "

			+   INNER_JOIN +  " EG_PT_OWNER owner             ON property.id = owner.propertyid "

			+   LEFT_JOIN  +  " EG_PT_DOCUMENT owndoc         ON owner.ownerinfouuid = owndoc.entityid "

			+	LEFT_JOIN  +  " EG_PT_UNIT unit ON property.id =  unit.propertyid ";
	

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY plastmodifiedtime DESC, pid) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

	private static final String LATEST_EXECUTED_MIGRATION_QUERY = "select * from eg_pt_enc_audit where tenantid = ? order by createdTime desc limit 1;";

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
	
	public String getPropertySearchQueryForDeafauterNotice (PropertyCriteria criteria, List<Object> preparedStmtList)
	{
		
	Boolean isEmpty = null == criteria.getLocality() ||
 null == criteria.getPropertyType() || criteria.getTenantId()==null;
	
	if(isEmpty)
		throw new CustomException("EG_PT_SEARCH_ERROR"," Please provide tenant, locality, propertyType for the property search for defaulter notice");
	String currYearS;

	int currYear=((new Date().getYear())+1900);
	int currmonth=((new Date().getMonth())+1);
	if(currmonth>=1 && currmonth <=3)
		currYearS= (String.valueOf(currYear-1))+"-"+ (String.valueOf(currYear).substring(2,4));
	else
		currYearS=(String.valueOf(currYear))+"-"+ (String.valueOf(currYear+1).substring(2,4));
		
		
	StringBuilder builder= new StringBuilder(PROPERTY_DEFAULTER_SEARCH);
	preparedStmtList.add(criteria.getTenantId());
	if(criteria.getPropertyType().equals("ALL"))
		preparedStmtList.add("%%");
	else
		preparedStmtList.add("%"+criteria.getPropertyType().toUpperCase()+"%");
	preparedStmtList.add(criteria.getLocality());
	preparedStmtList.add(Status.ACTIVE.toString());
	preparedStmtList.add(Status.ACTIVE.toString());
	preparedStmtList.add(currYearS);

	return builder.toString();
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
					&& null == criteria.getDocumentNumbers()
					&& null == criteria.getLocality()
					&& null == criteria.getPropertyType()
					&& (null == criteria.getFromDate() && null == criteria.getToDate())
					&& CollectionUtils.isEmpty(criteria.getCreationReason());
		
		if(isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR"," No criteria given for the property search");
		
		StringBuilder builder;
		Boolean appendAndQuery = false;

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
				appendAndQuery = true;
			}
		}
		else
		{
			String tenantId = criteria.getTenantId();
			
			if (tenantId != null) {
				appendAndQuery = true;
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
		//	 If user does NOT specify toDate, take today's date as the toDate by default
			if (criteria.getToDate() == null)
			{
				criteria.setToDate(Instant.now().toEpochMilli());
			}
			builder.append("property.createdTime BETWEEN ? AND ?");
			preparedStmtList.add(criteria.getFromDate());
			preparedStmtList.add(criteria.getToDate());
			appendAndQuery = true;
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
		
		Set<String> documentNumberList = criteria.getDocumentNumbers();

		if(!CollectionUtils.isEmpty(documentNumberList)){
				if(appendAndQuery)
				builder.append(AND_QUERY);
			addClauseIfRequired(preparedStmtList, builder);
			builder.append(" property.status='ACTIVE' and owndoc.status='ACTIVE' and owndoc.documentuid  IN ( ").append(createQuery(documentNumberList)).append(" )");
			addToPreparedStatement(preparedStmtList, documentNumberList);
		}
		
		
		if (null != criteria.getLocality()) {
	if(appendAndQuery)
				builder.append(AND_QUERY);
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("address.locality = ?");
			preparedStmtList.add(criteria.getLocality());
		appendAndQuery= true;
		}
		if (null != criteria.getDoorNo()) {

			addClauseIfRequired(preparedStmtList,builder);
			builder.append("address.doorno = ?");
			preparedStmtList.add(criteria.getDoorNo());
		}

		Set<String> propertyIds = criteria.getPropertyIds();
		if (!CollectionUtils.isEmpty(propertyIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			if(appendAndQuery)
				//builder.append(AND_QUERY);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
			appendAndQuery= true;
		}
		
		Set<String> acknowledgementIds = criteria.getAcknowledgementIds();
		if (!CollectionUtils.isEmpty(acknowledgementIds)) {

			addClauseIfRequired(preparedStmtList,builder);
			if(appendAndQuery)
				builder.append(AND_QUERY);
			builder.append("property.acknowldgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, acknowledgementIds);
			appendAndQuery= true;
		}
		
		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {
			if(appendAndQuery)
				builder.append(AND_QUERY);
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
			appendAndQuery= true;
		}

		Set<String> oldpropertyids = criteria.getOldpropertyids();
		if (!CollectionUtils.isEmpty(oldpropertyids)) {
if(appendAndQuery)
				builder.append(AND_QUERY);
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("property.oldpropertyid IN (").append(createQuery(oldpropertyids)).append(")");
			addToPreparedStatement(preparedStmtList, oldpropertyids);
			appendAndQuery= true;
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


	public String getPropertyQueryForBulkSearch(PropertyCriteria criteria, List<Object> preparedStmtList,Boolean isPlainSearch) {

		Boolean isEmpty = CollectionUtils.isEmpty(criteria.getPropertyIds())
				&& CollectionUtils.isEmpty(criteria.getUuids());

		if(isEmpty)
			throw new CustomException("EG_PT_SEARCH_ERROR"," No propertyid or uuid given for the property Bulk search");

		StringBuilder builder = new StringBuilder(QUERY);
	//	Boolean appendAndQuery = false;
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
	//if(appendAndQuery)
				builder.append(AND_QUERY);
			builder.append("property.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatementWithUpperCase(preparedStmtList, propertyIds);
	//		appendAndQuery= true;
		}

		Set<String> uuids = criteria.getUuids();
		if (!CollectionUtils.isEmpty(uuids)) {

			addClauseIfRequired(preparedStmtList,builder);
//if(appendAndQuery)
				builder.append(AND_QUERY);
			builder.append("property.id IN (").append(createQuery(uuids)).append(")");
			addToPreparedStatement(preparedStmtList, uuids);
		//	appendAndQuery= true;
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

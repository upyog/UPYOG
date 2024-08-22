package org.egov.pt.calculator.repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.calculator.repository.rowmapper.AssessmentRowMapper;
import org.egov.pt.calculator.repository.rowmapper.DuePropertyRowMapper;
import org.egov.pt.calculator.repository.rowmapper.PropertyRowMapper;
import org.egov.pt.calculator.util.CalculatorUtils;
import org.egov.pt.calculator.util.Configurations;
import org.egov.pt.calculator.web.models.Assessment;
import org.egov.pt.calculator.web.models.CreateAssessmentRequest;
import org.egov.pt.calculator.web.models.DefaultersInfo;
import org.egov.pt.calculator.web.models.property.AuditDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

import org.egov.pt.calculator.web.models.property.Property;

/**
 * Persists and retrieves the assessment data from DB
 * 
 * @author kavi elrey
 */
@Repository
@Slf4j
public class AssessmentRepository {
	private static final String PROPERTY_SEARCH_QUERY = "select distinct prop.id,prop.propertyid,prop.acknowldgementNumber,prop.propertytype,prop.status,prop.ownershipcategory,prop.oldPropertyId,prop.createdby,prop.createdTime,prop.lastmodifiedby,prop.lastmodifiedtime,prop.tenantid from eg_pt_property prop inner join eg_pt_address addr ON prop.id = addr.propertyid and prop.tenantid=addr.tenantid left join eg_pt_unit unit ON prop.id = unit.propertyid and prop.tenantid=addr.tenantid where prop.status='ACTIVE' ";

	private static final String PROPERTY_ACTIVE_SEARCH_QUERY = "select distinct prop.propertyid,prop.id,prop,prop.tenantid from eg_pt_property prop where prop.status='ACTIVE' ";

	private static final String PROPERTY_COUNT_ACTIVE = "select count(distinct prop.propertyid)	from eg_pt_property prop where prop.status='ACTIVE'";

	private static final String ASSESSMENT_SEARCH_QUERY = "select id,assessmentnumber from eg_pt_asmt_assessment where status='ACTIVE' and propertyid=:propertyid and financialyear=:financialyear and tenantid=:tenantid";
	private static final String ASSESSMENT_SEARCH_QUERY_ID = "select id from eg_pt_asmt_assessment where status='ACTIVE' and propertyid=:propertyid and financialyear=:financialyear and tenantid=:tenantid";

	private static final String ASSESSMENT_SEARCH_QUERY_FOR_CANCEL = "select assessmentnumber from eg_pt_asmt_assessment where status='ACTIVE' and propertyid=:propertyid and financialyear=:financialyear and tenantid=:tenantid";

	private static final String ASSESSMENT_DETAIL_SEARCH_QUERY = "SELECT asmt.id as ass_assessmentid, asmt.financialyear as ass_financialyear, asmt.tenantId as ass_tenantid, asmt.assessmentNumber as ass_assessmentnumber, "
			+ "asmt.status as ass_status, asmt.propertyId as ass_propertyid, asmt.source as ass_source, asmt.assessmentDate as ass_assessmentdate,  "
			+ "asmt.additionalDetails as ass_additionaldetails, asmt.createdby as ass_createdby, asmt.createdtime as ass_createdtime, asmt.lastmodifiedby as ass_lastmodifiedby, "
			+ "asmt.lastmodifiedtime as ass_lastmodifiedtime, us.tenantId as us_tenantid, us.unitId as us_unitid, us.id as us_id, us.assessmentId as us_assessmentid, "
			+ "us.usageCategory as us_usagecategory, us.occupancyType as us_occupancytype, "
			+ "us.occupancyDate as us_occupancydate, us.active as us_active, us.createdby as us_createdby, "
			+ "us.createdtime as us_createdtime, us.lastmodifiedby as us_lastmodifiedby, us.lastmodifiedtime as us_lastmodifiedtime, "
			+ "doc.id as doc_id, doc.entityid as doc_entityid, doc.documentType as doc_documenttype, doc.fileStoreId as doc_filestoreid, doc.documentuid as doc_documentuid, "
			+ "doc.status as doc_status, doc.tenantid as doc_tenantid, "
			+ "doc.createdby as doc_createdby, doc.createdtime as doc_createdtime, doc.lastmodifiedby as doc_lastmodifiedby, doc.lastmodifiedtime as doc_lastmodifiedtime "
			+ "FROM eg_pt_asmt_assessment asmt LEFT OUTER JOIN eg_pt_asmt_unitusage us ON asmt.id = us.assessmentId LEFT OUTER JOIN eg_pt_asmt_document doc ON asmt.id = doc.entityid ";

	private static final String ASSESSMENT_JOB_DATA_INSERT_QUERY = "Insert into eg_pt_assessment_job (id,assessmentnumber,propertyid,financialyear,createdtime,status,error,additionaldetails,tenantid) values(:id,:assessmentnumber,:propertyid,:financialyear,:createdtime,:status,:error,:additionaldetails,:tenantid)";;

	private static final String OCUUPANCY_TYPE_RENTED = "RENTED";

	private static final String INNER_QUERY = "select pt.propertyid,sum(dd.taxamount - dd.collectionamount) balance,pt.tenantid from eg_pt_property pt, egbs_demand_v1 d,egbs_demanddetail_v1 dd where dd.demandid=d.id and dd.tenantid=d.tenantid and d.consumercode = pt.propertyid and d.tenantid = pt.tenantid and pt.status='ACTIVE' and d.status = 'ACTIVE' ";

	private static final String OUTER_QUERY = "select result.propertyid,result.tenantid,result.balance from ({duequery}) as result where result.balance > 0 ";

	private static final String GROUP_BY_CLAUSE = " group by pt.propertyid,pt.tenantid";

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	Configurations config;

	@Autowired
	private AssessmentRowMapper rowMapper;

	@Autowired
	private CalculatorUtils utils;

	@Autowired
	private PropertyRowMapper propertyRowMapper;

	@Autowired
	private AssessmentRowMapper assessmentRowmapper;

	@Autowired
	private DuePropertyRowMapper duePropertyRowMapper;

	/**
	 * Retrieves assessments for the given query
	 * 
	 * @param query
	 * @param preparedStatementList
	 * @return
	 */
	public List<Assessment> getAssessments(String query, Object[] preparedStatementList) {
		return jdbcTemplate.query(query, preparedStatementList, rowMapper);
	}

	/**
	 * Saves the assessments in to assessment table
	 * 
	 * @param assessments
	 * @param info
	 * @return
	 */
	public List<Assessment> saveAssessments(List<Assessment> assessments, RequestInfo info) {

		jdbcTemplate.batchUpdate(utils.getAssessmentInsertQuery(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int rowNum) throws SQLException {

				Assessment current = assessments.get(rowNum);
				AuditDetails audit = current.getAuditDetails();

				ps.setString(1, current.getUuid());
				ps.setString(2, current.getAssessmentNumber());
				ps.setString(3, current.getAssessmentYear());
				ps.setString(4, current.getDemandId());
				ps.setString(5, current.getPropertyId());
				ps.setString(6, current.getTenantId());
				ps.setString(7, audit.getCreatedBy());
				ps.setLong(8, audit.getCreatedTime());
				ps.setString(9, audit.getLastModifiedBy());
				if (audit.getLastModifiedTime() == null)
					ps.setLong(10, 0);
				else
					ps.setLong(10, audit.getLastModifiedTime());
			}

			@Override
			public int getBatchSize() {
				return assessments.size();
			}
		});
		return assessments;
	}

	public Integer getActivePropertyCount(CreateAssessmentRequest request) {
		StringBuilder query = new StringBuilder(PROPERTY_COUNT_ACTIVE);
		query.append(" and prop.tenantid=?");
		List<Object> preparedStmtList = new ArrayList<>();
		preparedStmtList.add(request.getTenantId());
		Integer count = jdbcTemplate.queryForObject(query.toString(), preparedStmtList.toArray(), Integer.class);
		return count;

	}

	public List<Property> fetchAllActivePropertieswithLimit(CreateAssessmentRequest request) {

		StringBuilder query = new StringBuilder(PROPERTY_ACTIVE_SEARCH_QUERY);
		final Map<String, Object> params = new HashMap<>();



		if (request.getLocality() != null) {
			query.append(" and addr.locality in (:locality) ");
			params.put("locality", request.getLocality());
		}
		// currently this filter is disabled in MDMS config
		if (request.getPropertyType() != null) {
			// query.append(" and SPLIT_PART(prop.usagecategory,'.',1) in (:propertytype)
			// ");
			query.append(" and prop.usagecategory in (:propertytype) ");
			params.put("propertytype", request.getPropertyType());
		}

		/*
		 * Include or exclude rented properties based on isRented flag in MDMS config if
		 * true then include rented properties, else exclude rented properties (If any
		 * one of the unit of the property is Rented then total property will be
		 * considered as Rented)
		 */

		if (!request.getIsRented()) {
			query.append(
					" and prop.id not in (select propertyid from eg_pt_unit where tenantid=:tenantid and occupancytype = :occupancytype)");
			params.put("occupancytype", OCUUPANCY_TYPE_RENTED);
		}

		query.append(" and prop.tenantid=:tenantid");
		params.put("tenantid", request.getTenantId());
		query.append(" offset :offset_ limit :limit");
		params.put("offset_", request.getOffset());
		params.put("limit", request.getLimit());
		return namedParameterJdbcTemplate.query(query.toString(), params, propertyRowMapper);
	}

	public List<Property> fetchAllActiveProperties(CreateAssessmentRequest request) {
		StringBuilder query = new StringBuilder(PROPERTY_SEARCH_QUERY);
		final Map<String, Object> params = new HashMap<>();
		if (request.getLocality() != null) {
			query.append(" and addr.locality in (:locality) ");
			params.put("locality", request.getLocality());
		}
		// currently this filter is disabled in MDMS config
		if (request.getPropertyType() != null) {
			// query.append(" and SPLIT_PART(prop.usagecategory,'.',1) in (:propertytype)
			// ");
			query.append(" and prop.usagecategory in (:propertytype) ");
			params.put("propertytype", request.getPropertyType());
		}

		/*
		 * Include or exclude rented properties based on isRented flag in MDMS config if
		 * true then include rented properties, else exclude rented properties (If any
		 * one of the unit of the property is Rented then total property will be
		 * considered as Rented)
		 */

		if (!request.getIsRented()) {
			query.append(
					" and prop.id not in (select propertyid from eg_pt_unit where tenantid=:tenantid and occupancytype = :occupancytype)");
			params.put("occupancytype", OCUUPANCY_TYPE_RENTED);
		}

		query.append(" and prop.tenantid=:tenantid");
		params.put("tenantid", request.getTenantId());
		return namedParameterJdbcTemplate.query(query.toString(), params, propertyRowMapper);
	}

	public List<DefaultersInfo> fetchAllPropertiesForReAssess(Long fromDate, Long toDate, String tenantId) {

		final Map<String, Object> params = new HashMap<>();
		List<DefaultersInfo> defaultersInfo = new ArrayList<>();
		StringBuilder dueQuery = new StringBuilder(INNER_QUERY);
		if (fromDate != null && toDate != null) {
			dueQuery.append(" and d.taxperiodfrom >=:fromDate and d.taxperiodto <=:toDate ");
			params.put("fromDate", fromDate);
			params.put("toDate", toDate);
		}
		dueQuery.append(" and pt.tenantId=:tenantId");
		params.put("tenantId", tenantId);
		dueQuery.append(GROUP_BY_CLAUSE);

		String mainQuery = OUTER_QUERY.replace("{duequery}", dueQuery);
		log.info("re-assess query" + mainQuery);
		try {
			defaultersInfo = namedParameterJdbcTemplate.query(mainQuery, params, duePropertyRowMapper);
		} catch (Exception ex) {
			log.info("exception while fetching PT details for reassess " + ex.getMessage());
		}
		return defaultersInfo;
	}

	public List<Assessment> fetchAssessments(String propertyId, String assessmentYear, String tenantId) {
		StringBuilder query = new StringBuilder(ASSESSMENT_DETAIL_SEARCH_QUERY);
		final Map<String, Object> params = new HashMap<>();

		query.append(" where asmt.tenantid = :tenantid");
		params.put("tenantid", tenantId);

		if (!StringUtils.isEmpty(assessmentYear)) {
			query.append(" and asmt.financialyear = :financialyear");
			params.put("financialyear", assessmentYear);
		}
		if (!StringUtils.isEmpty(tenantId)) {
			query.append(" and asmt.propertyId =:propertyid ");
			params.put("propertyid", propertyId);
		}
		query.append(" and asmt.status='ACTIVE' ");
		query.append(" ORDER BY asmt.createdtime DESC");

		log.info("Assessment search query" + query);
		log.info("Parmas " + params);
		List<Assessment> assessments = new ArrayList<>();
		try {
			assessments = namedParameterJdbcTemplate.query(query.toString(), params, assessmentRowmapper);
		} catch (final DataAccessException e) {
			log.info("exception in assessment search");
		}

		if (assessments.isEmpty())
			return Collections.emptyList();
		else
			return assessments;
	}

	public boolean isAssessmentExists(String propertyId, String assessmentYear, String tenantId) {
		StringBuilder query = new StringBuilder(ASSESSMENT_SEARCH_QUERY_ID);
		final Map<String, Object> params = new HashMap<>();
		params.put("propertyid", propertyId);
		params.put("financialyear", assessmentYear);
		params.put("tenantid", tenantId);
		List<String> assessmentIds = new ArrayList<>();
		try {
			assessmentIds = namedParameterJdbcTemplate.queryForList(query.toString(), params, String.class);
		} catch (final DataAccessException e) {

		}

		if (assessmentIds.isEmpty())
			return false;
		else
			return true;
	}

	public boolean isAssessmentExistsForCancellation(String propertyId, String assessmentYear, String tenantId) {
		StringBuilder query = new StringBuilder(ASSESSMENT_SEARCH_QUERY_FOR_CANCEL);
		final Map<String, Object> params = new HashMap<>();
		params.put("propertyid", propertyId);
		params.put("financialyear", assessmentYear);
		params.put("tenantid", tenantId);
		List<String> assessmentIds = new ArrayList<>();
		try {
			assessmentIds = namedParameterJdbcTemplate.queryForList(query.toString(), params, String.class);
		} catch (final DataAccessException e) {

		}

		if (assessmentIds.isEmpty())
			return false;
		else
			return true;
	}

	public void saveAssessmentGenerationDetails(Assessment assessment, String status, String additionalDetails,
			String error) {
		StringBuilder query = new StringBuilder(ASSESSMENT_JOB_DATA_INSERT_QUERY);
		final Map<String, Object> params = new HashMap<>();
		params.put("id", UUID.randomUUID());
		params.put("assessmentnumber", assessment.getAssessmentNumber());
		params.put("propertyid", assessment.getPropertyId());
		params.put("financialyear", assessment.getFinancialYear());
		params.put("createdtime", System.currentTimeMillis());
		params.put("status", status);
		params.put("error", error);
		params.put("tenantid", assessment.getTenantId());
		params.put("additionaldetails", additionalDetails);
		try {
			namedParameterJdbcTemplate.update(query.toString(), params);
		} catch (final DataAccessException e) {
			log.info("exception in saving assessment job details");
		}
	}

}

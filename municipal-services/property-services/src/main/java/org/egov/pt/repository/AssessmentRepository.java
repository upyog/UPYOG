package org.egov.pt.repository;

import java.util.*;

import org.egov.pt.models.Assessment;
import org.egov.pt.models.AssessmentSearchCriteria;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
import org.egov.pt.repository.builder.AssessmentQueryBuilder;
import org.egov.pt.repository.rowmapper.AssessmentRowMapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Repository
@Slf4j
public class AssessmentRepository {

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	@Autowired
	private AssessmentQueryBuilder queryBuilder;

	@Autowired
	private AssessmentRowMapper rowMapper;
	
	
	public List<Assessment> getAssessments(AssessmentSearchCriteria criteria){
		Map<String, Object> preparedStatementValues = new HashMap<>();
		List<Assessment> assessments = new ArrayList<>();
		String query = queryBuilder.getSearchQuery(criteria, preparedStatementValues);
		log.info("Query: "+query);
		log.debug("preparedStatementValues: "+preparedStatementValues);
		assessments = namedParameterJdbcTemplate.query(query, preparedStatementValues, rowMapper);
		return assessments;
	}

	public List<String> fetchAssessmentNumbers(AssessmentSearchCriteria criteria) {
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String basequery = "SELECT assessmentnumber from eg_pt_asmt_assessment";
		StringBuilder builder = new StringBuilder(basequery);
		if (!ObjectUtils.isEmpty(criteria.getTenantId())) {
			builder.append(" where tenantid = :tenantid");
			preparedStatementValues.put("tenantid", criteria.getTenantId());
		}
		String orderbyClause = " ORDER BY createdtime,id offset :offset limit :limit";
		preparedStatementValues.put("offset", criteria.getOffset());
		preparedStatementValues.put("limit", criteria.getLimit());
		builder.append(orderbyClause);
		return namedParameterJdbcTemplate.query(builder.toString(),
				preparedStatementValues,
				new SingleColumnRowMapper<>(String.class));
	}

	public List<Assessment> getAssessmentPlainSearch(AssessmentSearchCriteria criteria) {
		if ((criteria.getAssessmentNumbers() == null || criteria.getAssessmentNumbers().isEmpty())
				&& (criteria.getIds() == null || criteria.getIds().isEmpty())
				&& (criteria.getPropertyIds() == null || criteria.getPropertyIds().isEmpty()))
			throw new CustomException("PLAIN_SEARCH_ERROR", "Empty search not allowed!");
		return getAssessments(criteria);
	}
	/**
	 * Fetches the assessment from DB corresponding to given assessment for update
	 * @param assessment THe Assessment to be updated
	 * @return Assessment from DB
	 */
	public Assessment getAssessmentFromDB(Assessment assessment){

		AssessmentSearchCriteria criteria = AssessmentSearchCriteria.builder()
				.ids(Collections.singleton(assessment.getId()))
				.tenantId(assessment.getTenantId())
				.build();

		List<Assessment> assessments = getAssessments(criteria);

		if(CollectionUtils.isEmpty(assessments))
			throw new CustomException("ASSESSMENT_NOT_FOUND","The assessment with id: "+assessment.getId()+" is not found in DB");

		return assessments.get(0);
	}
	final String UPDATE_EGBS_BILL_V1_QUERY = "update egbs_bill_v1 set status='EXPIRED' where id=:id"; // use billid here from 2222 
	final String UPDATE_EGBS_DEMAND_V1_QUERY = "update egbs_demand_v1 set status = : status where id=:id"; // use demid here from 1111 only for required finyear
	final String UPDATE_EG_PT_ASMT_ASSESSMENT_QUERY = "update eg_pt_asmt_assessment set status='CANCELLED' where propertyid=:propertyid and assessmentnumber=:assessmentnumber and financialyear=:financialyear";

	
	public List<String> fetchDemand(Assessment assesment) {
		Map<String, Object> preparedStatementValues = new HashMap<>();
		String basequery = "select to_char((To_timestamp(egbs_demand_v1.taxperiodfrom/1000) at time Zone 'Asia/Kolkata'),'YYYY') ||'-'|| to_char((To_timestamp(egbs_demand_v1.taxperiodto/1000) at time Zone 'Asia/Kolkata'),'YY') as finyear, egbs_demand_v1.id as demid, egbs_demand_v1.status, (To_timestamp(egbs_demanddetail_v1.lastmodifiedtime/1000) at time Zone 'Asia/Kolkata') ddupdated, egbs_demanddetail_v1.taxheadcode, egbs_demanddetail_v1.taxamount as taxamt, egbs_demanddetail_v1.collectionamount as taxcoll, (egbs_demanddetail_v1.taxamount - egbs_demanddetail_v1.collectionamount) balance from egbs_demanddetail_v1 , egbs_demand_v1";
		StringBuilder builder = new StringBuilder(basequery);
		if (!ObjectUtils.isEmpty(assesment.getTenantId())) {
			builder.append(" where egbs_demanddetail_v1.demandid= egbs_demand_v1.demandid and egbs_demand_v1.status!='CANCELLED' and abs(egbs_demanddetail_v1.taxamount) > 0 and egbs_demand_v1.consumercode = :consumercode");
			preparedStatementValues.put("consumercode", assesment.getPropertyId());
		}
		String orderbyClause = " ORDER BY createdtime";
		builder.append(orderbyClause);
		return namedParameterJdbcTemplate.query(builder.toString(),
				preparedStatementValues,
				new SingleColumnRowMapper<>(String.class));
	}
	
	
	
	  public void update(Assessment assesment) {
	    // Adding params using MapSqlParameterSource class
		  
		
		  List<String> list = fetchDemand(assesment);  
		  System.out.println("list = "+list);
		  System.out.println("list size= "+list.size());
	    
	/*    SqlParameterSource namedParameters1 = new MapSqlParameterSource().addValue("status", "EXPIRED").addValue("id", assesment.getPropertyId());
	    int status1 = namedParameterJdbcTemplate.update(UPDATE_EGBS_BILL_V1_QUERY, namedParameters1); 
	  
	    
	    SqlParameterSource namedParameters2 = new MapSqlParameterSource().addValue("status", "CANCELLED").addValue("id", assesment.getPropertyId());
	    int status2 = namedParameterJdbcTemplate.update(UPDATE_EGBS_DEMAND_V1_QUERY, namedParameters2); 
	  
	    
	    SqlParameterSource namedParameters3 = new MapSqlParameterSource().addValue("status", "CANCELLED").addValue("propertyid", assesment.getPropertyId()).addValue("assessmentnumber", assesment.getAssessmentNumber()).addValue("financialyear",assesment.getFinancialYear());
	    int status3 = namedParameterJdbcTemplate.update(UPDATE_EG_PT_ASMT_ASSESSMENT_QUERY, namedParameters3); 
	    
	    System.out.println("status1 = " + status1);
	    System.out.println("status2 = " + status2);
	    System.out.println("status3 = " + status3);
	    
	    if(status1 != 0){
	      System.out.println("Assesment data updated for ID " + assesment.getId());
	    }else{
	      System.out.println("No Employee found with ID " + assesment.getStatus());
	    }
	    
	  */  
	    
	    
	    
	  }

}

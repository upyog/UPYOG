package org.egov.ptr.repository;

import java.util.*;

import org.egov.ptr.models.Assessment;
import org.egov.ptr.models.AssessmentSearchCriteria;
import org.egov.ptr.models.Property;
import org.egov.ptr.models.PropertyCriteria;
import org.egov.ptr.repository.builder.AssessmentQueryBuilder;
import org.egov.ptr.repository.rowmapper.AssessmentRowMapper;
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
		return new ArrayList<>();
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

		List<Assessment> assessments = new ArrayList<>();

		if(CollectionUtils.isEmpty(assessments))
			throw new CustomException("ASSESSMENT_NOT_FOUND","The assessment with id: "+assessment.getId()+" is not found in DB");

		return assessments.get(0);
	}


}

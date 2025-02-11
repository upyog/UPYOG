package org.egov.pqm.repository.querybuilder;

import com.google.common.base.Strings;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import org.egov.pqm.config.ServiceConfiguration;
import org.egov.pqm.web.model.Pagination;
import org.egov.pqm.web.model.SortBy;
import org.egov.pqm.web.model.TestSearchCriteria;
import org.egov.pqm.web.model.TestSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Component
public class TestQueryBuilder {


  @Autowired
  private ServiceConfiguration config;

  private static final String QUERY = "select count(*) OVER() AS full_count,test.* from eg_pqm_tests test";


  private static final String PAGINATION_WRAPPER = "{} {orderby} {pagination}";

  private static final String DOCUMENT_QUERY = "select * from eg_pqm_test_result_documents";

  private static final String QUALITYCRITERIA_QUERY = "select * from eg_pqm_test_criteria_results";

  public String getPqmSearchQuery(TestSearchRequest testSearchRequest,
      List<Object> preparedStmtList) {

    TestSearchCriteria criteria = testSearchRequest.getTestSearchCriteria();
    StringBuilder builder = new StringBuilder(QUERY);
    if (criteria.getTenantId() != null) {
      if (criteria.getTenantId().split("\\.").length == 1) {
        addClauseIfRequired(preparedStmtList, builder);
        builder.append(" test.tenantid like ?");
        preparedStmtList.add('%' + criteria.getTenantId() + '%');
      } else {
        addClauseIfRequired(preparedStmtList, builder);
        builder.append(" test.tenantid=? ");
        preparedStmtList.add(criteria.getTenantId());
      }
    }
    List<String> ids = criteria.getIds();
    if (!CollectionUtils.isEmpty(ids)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.id IN (").append(createQuery(ids)).append(")");
      addToPreparedStatement(preparedStmtList, ids);
    }

	List<String> testIds = criteria.getTestIds();
	if (!CollectionUtils.isEmpty(testIds)) {
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" test.testId IN (").append(createQuery(testIds)).append(")");
		addToPreparedStatement(preparedStmtList, testIds);
	}

	if (!Strings.isNullOrEmpty(criteria.getTestId())) {
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" test.testId like ?");
		preparedStmtList.add('%' + criteria.getTestId() + '%');
	}
	
	List<String> testCodes = criteria.getTestCode();
	if (!CollectionUtils.isEmpty(testCodes)) {
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" test.testCode IN (").append(createQuery(testCodes)).append(")");
		addToPreparedStatement(preparedStmtList, testCodes);
	}

    List<String> plantCodes = criteria.getPlantCodes();
    if (!CollectionUtils.isEmpty(plantCodes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.plantCode IN (").append(createQuery(plantCodes)).append(")");
      addToPreparedStatement(preparedStmtList, plantCodes);
    }

    List<String> processCodes = criteria.getProcessCodes();
    if (!CollectionUtils.isEmpty(processCodes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.processCode IN (").append(createQuery(processCodes)).append(")");
      addToPreparedStatement(preparedStmtList, processCodes);
    }

    List<String> stageCodes = criteria.getStageCodes();
    if (!CollectionUtils.isEmpty(stageCodes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.stageCode IN (").append(createQuery(stageCodes)).append(")");
      addToPreparedStatement(preparedStmtList, stageCodes);
    }

    List<String> materialCodes = criteria.getMaterialCodes();
    if (!CollectionUtils.isEmpty(materialCodes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.materialCode IN (").append(createQuery(materialCodes)).append(")");
      addToPreparedStatement(preparedStmtList, materialCodes);

    }

    List<String> deviceCodes = criteria.getDeviceCodes();
    if (!CollectionUtils.isEmpty(deviceCodes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.deviceCode IN (").append(createQuery(deviceCodes)).append(")");
      addToPreparedStatement(preparedStmtList, deviceCodes);

    }

    List<String> wfStatuses = criteria.getWfStatus();
    if (!CollectionUtils.isEmpty(wfStatuses)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.wfStatus IN (").append(createQuery(wfStatuses)).append(")");
      addToPreparedStatement(preparedStmtList, wfStatuses);
    }

    List<String> sourceTypes = criteria.getSourceType();
	if (!CollectionUtils.isEmpty(sourceTypes)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.sourceType IN (").append(createQuery(sourceTypes)).append(")");
		addToPreparedStatement(preparedStmtList, sourceTypes);

    }
	
	List<String> statuses = criteria.getStatus();
	if (!CollectionUtils.isEmpty(statuses)) {
		addClauseIfRequired(preparedStmtList, builder);
		builder.append(" test.status IN (").append(createQuery(statuses)).append(")");
		addToPreparedStatement(preparedStmtList, statuses);
	}
	 
    if (criteria.getLabAssignedTo() != null) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.labAssignedTo=? ");
      preparedStmtList.add(criteria.getLabAssignedTo());

    }

    if (criteria.getFromDate() != null && criteria.getToDate() != null) {

      Calendar fromDate = Calendar.getInstance(TimeZone.getDefault());
      fromDate.setTimeInMillis(criteria.getFromDate());
      fromDate.set(Calendar.HOUR_OF_DAY, 0);
      fromDate.set(Calendar.MINUTE, 0);
      fromDate.set(Calendar.SECOND, 0);

      Calendar toDate = Calendar.getInstance(TimeZone.getDefault());
      toDate.setTimeInMillis(criteria.getToDate());
      toDate.set(Calendar.HOUR_OF_DAY, 23);
      toDate.set(Calendar.MINUTE, 59);
      toDate.set(Calendar.SECOND, 59);
      toDate.set(Calendar.MILLISECOND, 0);

      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" test.lastmodifiedtime BETWEEN ").append(fromDate.getTimeInMillis())
          .append(" AND ")
          .append(toDate.getTimeInMillis());
    }

    return addPaginationWrapper(builder.toString(), preparedStmtList, testSearchRequest);

  }

  /**
   * @param query             prepared Query
   * @param preparedStmtList  values to be replaced on the query
   * @param testSearchRequest test search criteria
   * @return the query by replacing the placeholders with preparedStmtList
   */
  private String addPaginationWrapper(String query, List<Object> preparedStmtList,
			TestSearchRequest testSearchRequest) {

		Pagination pagination = testSearchRequest.getPagination();

		int limit = config.getDefaultLimit();
		int offset = config.getDefaultOffset();
		String finalQuery = PAGINATION_WRAPPER.replace("{}", query);

		if (pagination.getLimit() != null && pagination.getLimit() <= config.getMaxSearchLimit()) {
			limit = pagination.getLimit();
		}

		if (pagination.getLimit() != null && pagination.getLimit() > config.getMaxSearchLimit()) {
			limit = config.getMaxSearchLimit();
		}

		if (pagination.getOffset() != null) {
			offset = pagination.getOffset();
		}

		StringBuilder orderQuery = new StringBuilder();

		addOrderByClause(orderQuery, pagination);

		finalQuery = finalQuery.replace("{orderby}", orderQuery.toString());

		if (limit == -1) {
			finalQuery = finalQuery.replace("{pagination}", "");
		} else {
			finalQuery = finalQuery.replace("{pagination}", " offset ?  limit ?  ");
			preparedStmtList.add(offset);
			preparedStmtList.add(limit);
		}

		return finalQuery;

	}

  private void addClauseIfRequired(List<Object> values, StringBuilder queryString) {
    if (values.isEmpty()) {
      queryString.append(" WHERE ");
    } else {
      queryString.append(" AND");
    }
  }

  private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
    ids.forEach(id -> {
      preparedStmtList.add(id);
    });

  }

  private Object createQuery(List<String> ids) {
    StringBuilder builder = new StringBuilder();
    int length = ids.size();
    for (int i = 0; i < length; i++) {
      builder.append(" ?");
      if (i != length - 1) {
        builder.append(",");
      }
    }
    return builder.toString();
  }

  /**
   * @param builder
   * @param criteria
   */
	private void addOrderByClause(StringBuilder builder, Pagination criteria) {
		
		if (StringUtils.isEmpty(criteria.getSortBy()))
			builder.append(" ORDER BY test.lastmodifiedtime ");

		else if (criteria.getSortBy() == SortBy.wfStatus)
			builder.append(" ORDER BY test.wfStatus ");

		else if (criteria.getSortBy() == SortBy.testId)
			builder.append(" ORDER BY test.testId ");

		else if (criteria.getSortBy() == SortBy.scheduledDate)
			builder.append(" ORDER BY test.scheduledDate ");

		else if (criteria.getSortBy() == SortBy.plantCode)
			builder.append(" ORDER BY test.plantCode ");

		else if (criteria.getSortBy() == SortBy.processCode)
			builder.append(" ORDER BY test.processCode ");

		else if (criteria.getSortBy() == SortBy.stageCode)
			builder.append(" ORDER BY test.stageCode ");

		else if (criteria.getSortBy() == SortBy.materialCode)
			builder.append(" ORDER BY test.materialCode ");

		else if (criteria.getSortBy() == SortBy.deviceCode)
			builder.append(" ORDER BY test.deviceCode ");

		else if (criteria.getSortBy() == SortBy.createdTime)
			builder.append(" ORDER BY test.createdtime ");
		
		else if (criteria.getSortBy() == SortBy.id)
			builder.append(" ORDER BY test.id ");

		if (criteria.getSortOrder() == Pagination.SortOrder.ASC)
			builder.append(" ASC ");
		else
			builder.append(" DESC ");}


  public String getDocumentSearchQuery(List<String> idList, List<Object> preparedStmtList) {
    StringBuilder builder = new StringBuilder(DOCUMENT_QUERY);

    if (!CollectionUtils.isEmpty(idList)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" testId IN (").append(createQuery(idList)).append(")");
      addToPreparedStatement(preparedStmtList, idList);

      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" isActive=? ");
      preparedStmtList.add(true);
    }
    return builder.toString();
  }

  public String getQualityCriteriaQuery(List<String> idList, List<Object> preparedStmtList) {
    StringBuilder builder = new StringBuilder(QUALITYCRITERIA_QUERY);

    if (!CollectionUtils.isEmpty(idList)) {
      addClauseIfRequired(preparedStmtList, builder);
      builder.append(" testid IN (").append(createQuery(idList)).append(")");
      addToPreparedStatement(preparedStmtList, idList);

    }
    return builder.toString();
  }


}
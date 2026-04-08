package org.egov.noc.repository.builder;

import java.util.Arrays;
import java.util.List;

import org.egov.noc.config.NOCConfiguration;
import org.egov.noc.web.model.NocSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class NocQueryBuilder {

	@Autowired
	private NOCConfiguration nocConfig;
	
	@Value("${egov.noc.fuzzysearch.isFuzzyEnabled}")
	private boolean isFuzzyEnabled;

//	private static final String QUERY = "SELECT noc.*,nocdoc.*,noc.id as noc_id,noc.tenantid as noc_tenantId,noc.lastModifiedTime as "
//			+ "noc_lastModifiedTime,noc.createdBy as noc_createdBy,noc.lastModifiedBy as noc_lastModifiedBy,noc.createdTime as "
//			+ "noc_createdTime, nocdoc.uuid as noc_doc_id,"
//			+ "nocdoc.documenttype as noc_doc_documenttype,nocdoc.documentAttachment as noc_doc_documentAttachment"
//			+ " FROM eg_noc noc  LEFT OUTER JOIN "
//			+ "eg_noc_document nocdoc ON nocdoc.nocid = noc.id WHERE 1=1 ";






	private static final String QUERY =
			"SELECT noc.*, " +
					"jsonb_build_object(" +
					"'id', details.id, " +
					"'nocid', details.nocid, " +
					"'additionalDetails', details.additionalDetails" +
					") AS nocDetails, " +
					"jsonb_agg(DISTINCT jsonb_build_object(" +
					"'uuid', nocdoc.uuid, " +
					"'documentType', nocdoc.documenttype, " +
					"'documentAttachment', nocdoc.documentAttachment, 'order', nocdoc.doc_order)) AS documents, " +
					"jsonb_agg(DISTINCT jsonb_build_object(" +
					"'additionalDetails', nocowner.additionalDetails, " +
					"'uuid', nocowner.uuid " +
					")) AS owners " +
					"FROM eg_noc noc " +
					"LEFT JOIN eg_noc_details details ON details.nocid = noc.id " +
					"LEFT JOIN eg_noc_document nocdoc ON nocdoc.nocid = noc.id " +
					"LEFT JOIN eg_noc_owner nocowner ON nocowner.nocid = noc.id AND nocowner.status = true " +
					"WHERE 1=1";








//	private static final String QUERY =
//			"SELECT " +
//					"noc.*, " +
//					"nocdetails.*, " +
//
//					"DENSE_RANK() OVER (ORDER BY noc.lastModifiedTime DESC) AS offset_ " +
//					"FROM eg_noc noc " +
//					"LEFT JOIN eg_noc_details nocDetails ON nocDetails.nocId = noc.id " +
//					"WHERE 1=1 ";

//	private static final String QUERY =
//			"SELECT " +
//					"noc.*, " +
//					"nocdetails.id AS noc_details_id, " +
//					"nocdetails.nocid AS noc_details_nocid, " +
//					"nocdetails.additionaldetails AS noc_details_additionaldetails, " +
//					"nocdetails.tenantid AS noc_details_tenantid, " +
//					"DENSE_RANK() OVER (ORDER BY noc.lastModifiedTime DESC) AS offset_ " +
//					"FROM eg_noc noc " +
//					"LEFT JOIN eg_noc_details nocDetails ON nocDetails.nocId = noc.id " +
//					"WHERE 1=1 ";





//	private final String paginationWrapper = "SELECT * FROM "
//			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY noc_lastModifiedTime DESC) FROM " + "({})"
//			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";


	private final String DOCUMENT_CHECK_LIST_QUERY = "SELECT * from eg_noc_document_check_list where applicationno = ? AND tenantId = ?";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY lastModifiedTime DESC) FROM " + "({})"
			+ " result) ranked_result";


	public String getOwnerUserIdsQuery(String layoutId, List<Object> preparedStmtList) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT uuid FROM eg_noc_owner WHERE status = true and nocid = ?");

		preparedStmtList.add(layoutId);
		return sb.toString();
	}

	public String getNOCDocumantsCheckListQuery(String applicationNo, String tenantId, List<Object> params) {

		params.add(applicationNo);
		params.add(tenantId);

		return DOCUMENT_CHECK_LIST_QUERY;
	}

//	private final String paginationWrapper = "SELECT * FROM "
//			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY noc_lastModifiedTime DESC) offset_ FROM " + "({})"
//			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";
	
	private final String countWrapper = "SELECT COUNT(DISTINCT(noc_count.id)) FROM ({INTERNAL_QUERY}) as noc_count";

	/**
	 * To give the Search query based on the requirements.
	 * 
	 * @param criteria
	 *            NOC search criteria
	 * @param preparedStmtList
	 *            values to be replased on the query
	 * @return Final Search Query
	 */
	public String getNocSearchQuery(NocSearchCriteria criteria, List<Object> preparedStmtList, boolean isCount) {

		StringBuilder builder = new StringBuilder(QUERY);

		if (criteria.getTenantId() != null) {
	        addClauseIfRequired(builder);
	        builder.append(" noc.tenantid=? ");
	        preparedStmtList.add(criteria.getTenantId());
			log.info(criteria.getTenantId());
		}


		if ( !StringUtils.isEmpty(criteria.getVasikaNumber()) ) {
	        addClauseIfRequired(builder);
	        builder.append(" noc.vasikaNumber=? ");
	        preparedStmtList.add(criteria.getVasikaNumber());
			log.info(criteria.getVasikaNumber());
		}
		
		if ( !StringUtils.isEmpty(criteria.getVasikaDate()) ) {
	        addClauseIfRequired(builder);
	        builder.append(" noc.vasikaDate=? ");
	        preparedStmtList.add(criteria.getVasikaDate());
			log.info(criteria.getVasikaDate());
		}

		String applicationStatus = criteria.getApplicationStatus();
		if (applicationStatus != null) {
			List<String> applicationStatuses = Arrays.asList(applicationStatus.split(","));
			addClauseIfRequired(builder);
			if (isFuzzyEnabled) {
				builder.append(" noc.applicationstatus LIKE ANY(ARRAY[ ").append(createQuery(applicationStatuses)).append("])");
				addToPreparedStatementForFuzzySearch(preparedStmtList, applicationStatuses);
			} else {
				builder.append(" noc.applicationstatus IN (").append(createQuery(applicationStatuses)).append(")");
				addToPreparedStatement(preparedStmtList, applicationStatuses);
			}
		}

		List<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(builder);
			builder.append(" noc.id IN (").append(createQuery(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

//		List<String> ownerIds = criteria.getOwnerIds();
//		if (!CollectionUtils.isEmpty(ownerIds)) {
//			addClauseIfRequired(builder);
//			builder.append(" (noc.accountId IN (").append(createQuery(ownerIds)).append(")) ");
//			addToPreparedStatement(preparedStmtList, ownerIds);
//		}


		String applicationNo = criteria.getApplicationNo();
                if (applicationNo != null) {
                    List<String> applicationNos = Arrays.asList(applicationNo.split(","));
                    addClauseIfRequired(builder);
                    if (isFuzzyEnabled) {
                        builder.append(" noc.applicationNo LIKE ANY(ARRAY[ ").append(createQuery(applicationNos)).append("])");
                        addToPreparedStatementForFuzzySearch(preparedStmtList, applicationNos);
                    } else {
                        builder.append(" noc.applicationNo IN (").append(createQuery(applicationNos)).append(")");
                        addToPreparedStatement(preparedStmtList, applicationNos);
                    }
                }

		
		String approvalNo = criteria.getNocNo();
                if (approvalNo != null) {
                    List<String> approvalNos = Arrays.asList(approvalNo.split(","));
                    addClauseIfRequired(builder);
                    if (isFuzzyEnabled) {
                        builder.append(" noc.nocNo LIKE ANY(ARRAY[ ").append(createQuery(approvalNos)).append("])");
                        addToPreparedStatementForFuzzySearch(preparedStmtList, approvalNos);
                    } else {
                        builder.append(" noc.nocNo IN (").append(createQuery(approvalNos)).append(")");
                        addToPreparedStatement(preparedStmtList, approvalNos);
                    }
                }

		
//		String source = criteria.getSource();
//		if (source!=null) {
//			addClauseIfRequired(builder);
//			builder.append(" noc.source = ?");
//			preparedStmtList.add(criteria.getSource());
//			log.info(criteria.getSource());
//		}

//		String sourceRefId = criteria.getSourceRefId();
//                if (sourceRefId != null) {
//					sourceRefId = sourceRefId.replace("[","");
//					sourceRefId = sourceRefId.replace("]","");
//					List<String> sourceRefIds = Arrays.asList(sourceRefId.split(","));
//					addClauseIfRequired(builder);
//                    if (isFuzzyEnabled) {
//                        builder.append(" noc.sourceRefId LIKE ANY(ARRAY[ ").append(createQuery(sourceRefIds)).append("])");
//                        addToPreparedStatementForFuzzySearch(preparedStmtList, sourceRefIds);
//                    } else {
//                        builder.append(" noc.sourceRefId IN (").append(createQuery(sourceRefIds)).append(")");
//                        addToPreparedStatement(preparedStmtList, sourceRefIds);
//                    }
//                }

		
		String nocType = criteria.getNocType();
		if (nocType!=null) {
		        List<String> nocTypes = Arrays.asList(nocType.split(","));
			addClauseIfRequired(builder);
			builder.append(" noc.nocType IN (").append(createQuery(nocTypes)).append(")");
                        addToPreparedStatement(preparedStmtList, nocTypes);
                        log.info(nocType);
                }
                
                List<String> status = criteria.getStatus();
                if (status!=null) {
                        addClauseIfRequired(builder);
                        builder.append(" noc.status IN (").append(createQuery(status)).append(")");
                        addToPreparedStatement(preparedStmtList, status);
                }

		List<String> ownerIds = criteria.getOwnerIds(); // mapped to clu.accountId
		String createdBy = criteria.getCreatedBy();

		boolean hasOwnerIds  = (ownerIds != null && !ownerIds.isEmpty());
		boolean hasCreatedBy = (createdBy != null && !createdBy.isEmpty());

		if ((hasOwnerIds || hasCreatedBy) && criteria.getApplicationNo()==null){
			addClauseIfRequired(builder);
			builder.append(" ( ");

			boolean wroteOne = false;

			if (hasOwnerIds) {
				builder.append(" noc.accountId IN (").append(createQuery(ownerIds)).append(") ");
				addToPreparedStatement(preparedStmtList, ownerIds);
				wroteOne = true;
			}

			if (hasCreatedBy) {
				if (wroteOne) builder.append(" OR ");
				builder.append(" noc.createdby = ? ");
				preparedStmtList.add(createdBy);
			}

			builder.append(" ) ");
		}


		builder.append(" GROUP BY noc.id, noc.tenantid, noc.lastModifiedTime, noc.createdBy, ")
				.append("noc.lastModifiedBy, noc.createdTime, noc.applicationNo, noc.nocNo, noc.nocType,details.id, details.nocid, details.additionalDetails ");


		log.info(criteria.toString());
		log.info("Final Query");

		log.info(builder.toString());
		if(isCount)
	            return addCountWrapper(builder.toString());
		
		return addPaginationWrapper(builder.toString(), preparedStmtList, criteria);

	}

	/**
	 * 
	 * @param query
	 *            prepared Query
	 * @param preparedStmtList
	 *            values to be replased on the query
	 * @param criteria
	 *            bpa search criteria
	 * @return the query by replacing the placeholders with preparedStmtList
	 */
	private String addPaginationWrapper(String query, List<Object> preparedStmtList, NocSearchCriteria criteria) {

		int limit = nocConfig.getDefaultLimit();
		int offset = nocConfig.getDefaultOffset();
		String finalQuery = paginationWrapper.replace("{}", query);

		if (criteria.getLimit() != null && criteria.getLimit() <= nocConfig.getMaxSearchLimit())
			limit = criteria.getLimit();

		if (criteria.getLimit() != null && criteria.getLimit() > nocConfig.getMaxSearchLimit()) {
			limit = nocConfig.getMaxSearchLimit();
		}

		if (criteria.getOffset() != null)
			offset = criteria.getOffset();

//		if (limit == -1) {
//			finalQuery = finalQuery.replace("WHERE offset_ > ? AND offset_ <= ?", "");
//		} else {
//			preparedStmtList.add(offset);
//			preparedStmtList.add(limit + offset);
//		}


		if (limit != -1) {
			finalQuery += " ORDER BY createdtime DESC limit  ? offset ?";
			preparedStmtList.add(limit);
			preparedStmtList.add(offset);
		}


		log.info(finalQuery.toString());
		return finalQuery;

	}

	private void addClauseIfRequired(StringBuilder queryString) {
			queryString.append(" AND");
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, List<String> ids) {
		ids.forEach(preparedStmtList::add);

	}
	
	private void addToPreparedStatementForFuzzySearch(List<Object> preparedStmtList, List<String> ids) {
	    ids.forEach(id -> preparedStmtList.add("%"+id.trim()+"%"));
	}

	private Object createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}
	
	private String addCountWrapper(String query) {
	    return countWrapper.replace("{INTERNAL_QUERY}", query);
	}
}

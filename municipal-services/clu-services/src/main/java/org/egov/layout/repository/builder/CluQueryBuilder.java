package org.egov.layout.repository.builder;

import java.util.Arrays;
import java.util.List;

import org.egov.layout.config.CLUConfiguration;
import org.egov.layout.web.model.LayoutSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class CluQueryBuilder {

	@Autowired
	private CLUConfiguration nocConfig;
	
	@Value("${egov.clu.fuzzysearch.isFuzzyEnabled}")
	private boolean isFuzzyEnabled;

//	private static final String QUERY = "SELECT layout.*,nocdoc.*,layout.id as noc_id,layout.tenantid as noc_tenantId,layout.lastModifiedTime as "
//			+ "noc_lastModifiedTime,layout.createdBy as noc_createdBy,layout.lastModifiedBy as noc_lastModifiedBy,layout.createdTime as "
//			+ "noc_createdTime, nocdoc.uuid as noc_doc_id,"
//			+ "nocdoc.documenttype as noc_doc_documenttype,nocdoc.documentAttachment as noc_doc_documentAttachment"
//			+ " FROM eg_noc layout  LEFT OUTER JOIN "
//			+ "eg_noc_document nocdoc ON nocdoc.nocid = layout.id WHERE 1=1 ";



	private final String DOCUMENT_CHECK_LIST_QUERY = "SELECT * from eg_clu_document_check_list where applicationno = ? AND tenantId = ?";


	private static final String QUERY =
			"SELECT clu.*, " +
					"jsonb_build_object(" +
					"'id', details.id, " +
					"'cluid', details.cluid, " +
					"'additionalDetails', details.additionalDetails" +
					") AS nocDetails, " +
					"jsonb_agg(DISTINCT jsonb_build_object(" +
					"'uuid', cludoc.uuid, " +
					"'documentType', cludoc.documenttype, " +
					"'documentAttachment', cludoc.documentAttachment, 'order', cludoc.doc_order)) AS documents, " +
					"jsonb_agg(DISTINCT jsonb_build_object(" +
					"'additionalDetails', cluowner.additionalDetails, " +
					"'uuid', cluowner.uuid " +
					")) AS owners " +

					"FROM eg_clu clu " +
					"LEFT JOIN eg_clu_details details ON details.cluid = clu.id " +
					"LEFT JOIN eg_clu_document cludoc ON cludoc.cluid = clu.id " +
					"LEFT JOIN eg_clu_owner cluowner ON cluowner.cluid = clu.id AND cluowner.status = true " +
					"WHERE 1=1";







//	private static final String QUERY =
//			"SELECT " +
//					"layout.*, " +
//					"nocdetails.*, " +
//
//					"DENSE_RANK() OVER (ORDER BY layout.lastModifiedTime DESC) AS offset_ " +
//					"FROM eg_noc layout " +
//					"LEFT JOIN eg_noc_details nocDetails ON nocDetails.nocId = layout.id " +
//					"WHERE 1=1 ";

//	private static final String QUERY =
//			"SELECT " +
//					"layout.*, " +
//					"nocdetails.id AS noc_details_id, " +
//					"nocdetails.nocid AS noc_details_nocid, " +
//					"nocdetails.additionaldetails AS noc_details_additionaldetails, " +
//					"nocdetails.tenantid AS noc_details_tenantid, " +
//					"DENSE_RANK() OVER (ORDER BY layout.lastModifiedTime DESC) AS offset_ " +
//					"FROM eg_noc layout " +
//					"LEFT JOIN eg_noc_details nocDetails ON nocDetails.nocId = layout.id " +
//					"WHERE 1=1 ";





//	private final String paginationWrapper = "SELECT * FROM "
//			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY noc_lastModifiedTime DESC) FROM " + "({})"
//			+ " result) result_offset " + "WHERE offset_ > ? AND offset_ <= ?";


	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY lastModifiedTime DESC) FROM " + "({})"
			+ " result) ranked_result";


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

	public String getOwnerUserIdsQuery(String layoutId, List<Object> preparedStmtList) {
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT uuid FROM eg_clu_owner WHERE status = true and cluid = ?");

		preparedStmtList.add(layoutId);
		return sb.toString();
	}

	public String getNocSearchQuery(LayoutSearchCriteria criteria, List<Object> preparedStmtList, boolean isCount) {

		StringBuilder builder = new StringBuilder(QUERY);

		if (criteria.getTenantId() != null) {
	        addClauseIfRequired(builder);
	        builder.append(" clu.tenantid=? ");
	        preparedStmtList.add(criteria.getTenantId());
			log.info(criteria.getTenantId());
		}

		String applicationStatus = criteria.getApplicationStatus();
		if (applicationStatus != null) {
			List<String> applicationStatuses = Arrays.asList(applicationStatus.split(","));
			addClauseIfRequired(builder);
			if (isFuzzyEnabled) {
				builder.append(" clu.applicationstatus LIKE ANY(ARRAY[ ").append(createQuery(applicationStatuses)).append("])");
				addToPreparedStatementForFuzzySearch(preparedStmtList, applicationStatuses);
			} else {
				builder.append(" clu.applicationstatus IN (").append(createQuery(applicationStatuses)).append(")");
				addToPreparedStatement(preparedStmtList, applicationStatuses);
			}
		}



		List<String> ids = criteria.getIds();
		if (!CollectionUtils.isEmpty(ids)) {
			addClauseIfRequired(builder);
			builder.append(" clu.id IN (").append(createQuery(ids)).append(")");
			addToPreparedStatement(preparedStmtList, ids);
		}

//		List<String> ownerIds = criteria.getOwnerIds();
//		if (!CollectionUtils.isEmpty(ownerIds)) {
//			addClauseIfRequired(builder);
//			builder.append(" (clu.accountId IN (").append(createQuery(ownerIds)).append(")) ");
//			addToPreparedStatement(preparedStmtList, ownerIds);
//		}

		if ( !StringUtils.isEmpty(criteria.getVasikaNumber()) ) {
			addClauseIfRequired(builder);
			builder.append(" clu.vasikaNumber=? ");
			preparedStmtList.add(criteria.getVasikaNumber());
			log.info(criteria.getVasikaNumber());
		}

		if ( !StringUtils.isEmpty(criteria.getVasikaDate()) ) {
			addClauseIfRequired(builder);
			builder.append(" clu.vasikaDate=? ");
			preparedStmtList.add(criteria.getVasikaDate());
			log.info(criteria.getVasikaDate());
		}

		String applicationNo = criteria.getApplicationNo();
                if (applicationNo != null) {
                    List<String> applicationNos = Arrays.asList(applicationNo.split(","));
                    addClauseIfRequired(builder);
                    if (isFuzzyEnabled) {
                        builder.append(" clu.applicationNo LIKE ANY(ARRAY[ ").append(createQuery(applicationNos)).append("])");
                        addToPreparedStatementForFuzzySearch(preparedStmtList, applicationNos);
                    } else {
                        builder.append(" clu.applicationNo IN (").append(createQuery(applicationNos)).append(")");
                        addToPreparedStatement(preparedStmtList, applicationNos);
                    }
                }

		
		String approvalNo = criteria.getCluNo();
                if (approvalNo != null) {
                    List<String> approvalNos = Arrays.asList(approvalNo.split(","));
                    addClauseIfRequired(builder);
                    if (isFuzzyEnabled) {
                        builder.append(" clu.cluNo LIKE ANY(ARRAY[ ").append(createQuery(approvalNos)).append("])");
                        addToPreparedStatementForFuzzySearch(preparedStmtList, approvalNos);
                    } else {
                        builder.append(" clu.cluNo IN (").append(createQuery(approvalNos)).append(")");
                        addToPreparedStatement(preparedStmtList, approvalNos);
                    }
                }

		
//		String source = criteria.getSource();
//		if (source!=null) {
//			addClauseIfRequired(builder);
//			builder.append(" layout.source = ?");
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
//                        builder.append(" layout.sourceRefId LIKE ANY(ARRAY[ ").append(createQuery(sourceRefIds)).append("])");
//                        addToPreparedStatementForFuzzySearch(preparedStmtList, sourceRefIds);
//                    } else {
//                        builder.append(" layout.sourceRefId IN (").append(createQuery(sourceRefIds)).append(")");
//                        addToPreparedStatement(preparedStmtList, sourceRefIds);
//                    }
//                }

		
		String nocType = criteria.getCluType();
		if (nocType!=null) {
		        List<String> nocTypes = Arrays.asList(nocType.split(","));
			addClauseIfRequired(builder);
			builder.append(" clu.cluType IN (").append(createQuery(nocTypes)).append(")");
                        addToPreparedStatement(preparedStmtList, nocTypes);
                        log.info(nocType);
                }
                
                List<String> status = criteria.getStatus();
                if (status!=null) {
                        addClauseIfRequired(builder);
                        builder.append(" clu.status IN (").append(createQuery(status)).append(")");
                        addToPreparedStatement(preparedStmtList, status);
                }
//		if(criteria.getCreatedBy()!=null || !criteria.getCreatedBy().isEmpty())
//		{
//			addClauseIfRequired(builder);
//			builder.append(" clu.createdby=? ");
//			preparedStmtList.add(criteria.getCreatedBy());
//			log.info(criteria.getCreatedBy());
//		}




		List<String> ownerIds = criteria.getOwnerIds(); // mapped to clu.accountId
		String createdBy = criteria.getCreatedBy();

		boolean hasOwnerIds  = (ownerIds != null && !ownerIds.isEmpty());
		boolean hasCreatedBy = (createdBy != null && !createdBy.isEmpty());

		if ((hasOwnerIds || hasCreatedBy) && criteria.getApplicationNo()==null) {
			addClauseIfRequired(builder);
			builder.append(" ( ");

			boolean wroteOne = false;

			if (hasOwnerIds) {
				builder.append(" clu.accountId IN (").append(createQuery(ownerIds)).append(") ");
				addToPreparedStatement(preparedStmtList, ownerIds);
				wroteOne = true;
			}

			if (hasCreatedBy) {
				if (wroteOne) builder.append(" OR ");
				builder.append(" clu.createdby = ? ");
				preparedStmtList.add(createdBy);
			}

			builder.append(" ) ");
		}


		builder.append(" GROUP BY clu.id, clu.tenantid, clu.lastModifiedTime, clu.createdBy, ")
				.append("clu.lastModifiedBy, clu.createdTime, clu.applicationNo, clu.cluNo, clu.cluType,details.id, details.cluid, details.additionalDetails ");


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
	private String addPaginationWrapper(String query, List<Object> preparedStmtList, LayoutSearchCriteria criteria) {

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

	public String getCLUDocumantsCheckListQuery(String applicationNo, String tenantId, List<Object> params) {

		params.add(applicationNo);
		params.add(tenantId);

		return DOCUMENT_CHECK_LIST_QUERY;
	}
	
	private String addCountWrapper(String query) {
	    return countWrapper.replace("{INTERNAL_QUERY}", query);
	}
}

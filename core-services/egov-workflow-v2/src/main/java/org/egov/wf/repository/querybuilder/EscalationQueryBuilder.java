package org.egov.wf.repository.querybuilder;

import java.util.Arrays;
import java.util.List;

import org.egov.wf.web.models.EscalationSearchCriteria;
import org.springframework.stereotype.Component;

@Component
public class EscalationQueryBuilder {




    private static final String BASE_QUERY = "select businessId from (" +
            "  SELECT *,RANK () OVER (PARTITION BY businessId ORDER BY createdtime  DESC) rank_number " +
            " FROM eg_wf_processinstance_v2 WHERE businessservice = ? AND tenantid= ? ) wf  WHERE rank_number = 1 ";

    private static final String BASE_QUERY_BPA = "select businessId from (" +
            "  SELECT *,RANK () OVER (PARTITION BY businessId ORDER BY createdtime  DESC) rank_number " +
            " FROM eg_wf_processinstance_v2 WHERE businessservice = ? AND tenantid= ? AND action in ('PAY','POST_PAYMENT_APPLY') wf  WHERE rank_number = 1 ";

    private static final String QUERY_BPA = "select businessId from eg_wf_processinstance_v2 where businessid in ( ";


    /**
     * Builds query for searching escalated applications
     * @param criteria
     * @return
     */
    public String getEscalationQuery(EscalationSearchCriteria criteria, List<Object> preparedStmtList){


        StringBuilder builder = new StringBuilder(BASE_QUERY);

        preparedStmtList.add(criteria.getBusinessService());
        preparedStmtList.add(criteria.getTenantId());

        builder.append(" AND wf.status = ? ");
        preparedStmtList.add(criteria.getStatus());

        if(criteria.getStateSlaExceededBy() != null){
            builder.append(" AND (select extract(epoch from current_timestamp)) * 1000 - wf.createdtime - wf.statesla > ? ");
            preparedStmtList.add(criteria.getStateSlaExceededBy());
        }

        if(criteria.getBusinessSlaExceededBy() != null){
            builder.append(" AND (select extract(epoch from current_timestamp)) * 1000 - wf.createdtime - wf.businessservicesla > ? ");
            preparedStmtList.add(criteria.getBusinessSlaExceededBy());
        }

        return builder.toString();

    }
   
    
    public String getEscalationQueryForBPA(EscalationSearchCriteria criteria, List<Object> preparedStmtList){


        StringBuilder builder = new StringBuilder(BASE_QUERY);
       
        preparedStmtList.add(criteria.getBusinessService());
        preparedStmtList.add(criteria.getTenantId());

		builder.append(" AND wf.status IN (").append(createQuery(Arrays.asList(criteria.getStatus().split(",")))).append(" )");
		addToPreparedStatement(preparedStmtList, Arrays.asList(criteria.getStatus().split(",")));

        return builder.toString();

    }
    
    
    public String getEscalationQueryFiltered(EscalationSearchCriteria criteria,List<String> businessIds, List<Object> preparedStmtList){


        StringBuilder builder = new StringBuilder(BASE_QUERY_BPA);
        
        preparedStmtList.add(criteria.getBusinessService());
        preparedStmtList.add(criteria.getTenantId());

		builder.append(" AND wf.businessid IN (").append(createQuery(businessIds)).append(" )");
		addToPreparedStatement(preparedStmtList, businessIds);



        if(criteria.getStateSlaExceededBy() != null){
            builder.append(" AND (select extract(epoch from current_timestamp)) * 1000 - wf.createdtime > ? ");
            preparedStmtList.add(criteria.getStateSlaExceededBy());
        }

        if(criteria.getBusinessSlaExceededBy() != null){
            builder.append(" AND (select extract(epoch from current_timestamp)) * 1000 - wf.createdtime - wf.businessservicesla > ? ");
            preparedStmtList.add(criteria.getBusinessSlaExceededBy());
        }

        return builder.toString();

    }
    private String createQuery(List<String> ids) {
		StringBuilder builder = new StringBuilder();
		int length = ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

    private void addToPreparedStatement(List<Object> preparedStatement, List<String> ids) {
		preparedStatement.addAll(ids);
	}

}

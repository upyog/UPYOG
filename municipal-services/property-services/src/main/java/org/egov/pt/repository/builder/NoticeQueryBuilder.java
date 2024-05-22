package org.egov.pt.repository.builder;

import java.util.List;
import java.util.Set;


import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.NoticeCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

@Repository
public class NoticeQueryBuilder {

	@Autowired
	PropertyConfiguration noticeConfiguration;

	private static String noticesearchquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,epnc.\"comment\", epnc.noticeid from public.eg_pt_notice nt left join eg_pt_notice_comment epnc on epnc.noticeid = nt.uuid";
	private static String noticesearchwithauditquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,nt.createdby as nt_cb,nt.createdtime as nt_ct,nt.lastmodifiedby as nt_lmb,nt.lastmodifiedtime as nt_lmt,epnc.\"comment\", epnc.noticeid, epnc.createdby as cm_cb, epnc.createdtime as cm_ct, epnc.lastmodifiedby as cm_lmb, epnc.lastmodifiedtime as cm_lmt from public.eg_pt_notice nt left join eg_pt_notice_comment epnc on epnc.noticeid = nt.uuid";

	private static String noticesearchwitoutcommentquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno  from public.eg_pt_notice nt ";
	private static String noticesearchwithoutcommentauditquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,nt.createdby as nt_cb,nt.createdtime as nt_ct,nt.lastmodifiedby as nt_lmb,nt.lastmodifiedtime as nt_lmt from public.eg_pt_notice nt";


	private final String paginationAuditWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY nt_lmb DESC, noticenumber) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

	private final String paginationWrapper = "SELECT * FROM "
			+ "(SELECT *, DENSE_RANK() OVER (ORDER BY entrydate DESC, noticenumber) offset_ FROM " + "({})" + " result) result_offset "
			+ "WHERE offset_ > ? AND offset_ <= ?";

	public String noticesearchquery(NoticeCriteria noticeCriteria, List<Object> preparedStmtList) {
		// TODO Auto-generated method stub
		StringBuilder builder;
		if(noticeCriteria.isAudit())
			builder=new StringBuilder(noticesearchwithauditquery);
		else
			builder=new StringBuilder(noticesearchquery);

		if(!CollectionUtils.isEmpty(noticeCriteria.getTenantId()))
		{
			Set<String> tenantIds=noticeCriteria.getTenantId();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.tenantid IN (").append(createQuery(tenantIds)).append(")");
			addToPreparedStatement(preparedStmtList,tenantIds);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getNoticenumber()))
		{
			Set<String> noticeNumber=noticeCriteria.getNoticenumber();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.noticenumber IN (").append(createQuery(noticeNumber)).append(")");
			addToPreparedStatement(preparedStmtList,noticeNumber);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getPropertyIds()))
		{
			Set<String> propertyIds=noticeCriteria.getPropertyIds();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatement(preparedStmtList,propertyIds);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getAcknowledgementIds()))
		{
			Set<String> acknowledgementIds=noticeCriteria.getAcknowledgementIds();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.acknowledgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatement(preparedStmtList,acknowledgementIds);
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, noticeCriteria);
	}

	public String noticesearchwitoutcommentquery(NoticeCriteria noticeCriteria, List<Object> preparedStmtList) {
		// TODO Auto-generated method stub
		StringBuilder builder;
		if(noticeCriteria.isAudit())
			builder=new StringBuilder(noticesearchwithoutcommentauditquery);
		else
			builder=new StringBuilder(noticesearchwitoutcommentquery);

		if(!CollectionUtils.isEmpty(noticeCriteria.getTenantId()))
		{
			Set<String> tenantIds=noticeCriteria.getTenantId();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.tenantid IN (").append(createQuery(tenantIds)).append(")");
			addToPreparedStatement(preparedStmtList,tenantIds);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getNoticenumber()))
		{
			Set<String> noticeNumber=noticeCriteria.getNoticenumber();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.noticenumber IN (").append(createQuery(noticeNumber)).append(")");
			addToPreparedStatement(preparedStmtList,noticeNumber);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getPropertyIds()))
		{
			Set<String> propertyIds=noticeCriteria.getPropertyIds();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.propertyid IN (").append(createQuery(propertyIds)).append(")");
			addToPreparedStatement(preparedStmtList,propertyIds);
		}
		if(!CollectionUtils.isEmpty(noticeCriteria.getAcknowledgementIds()))
		{
			Set<String> acknowledgementIds=noticeCriteria.getAcknowledgementIds();
			addClauseIfRequired(preparedStmtList,builder);
			builder.append("nt.acknowledgementnumber IN (").append(createQuery(acknowledgementIds)).append(")");
			addToPreparedStatement(preparedStmtList,acknowledgementIds);
		}

		return addPaginationWrapper(builder.toString(), preparedStmtList, noticeCriteria);
	}

	private void addToPreparedStatement(List<Object> preparedStmtList, Set<String> Ids) {
		// TODO Auto-generated method stub
		Ids.forEach(id -> {
			preparedStmtList.add(id);
		});
	}

	private Object createQuery(Set<String> Ids) {
		// TODO Auto-generated method stub
		StringBuilder builder = new StringBuilder();
		int length = Ids.size();
		for (int i = 0; i < length; i++) {
			builder.append(" ?");
			if (i != length - 1)
				builder.append(",");
		}
		return builder.toString();
	}

	private void addClauseIfRequired(List<Object> preparedStmtList, StringBuilder builder) {
		// TODO Auto-generated method stub
		if (preparedStmtList.isEmpty())
			builder.append(" WHERE ");
		else {
			builder.append(" AND ");
		}

	}

	private String addPaginationWrapper(String query, List<Object> preparedStmtList, NoticeCriteria noticeCriteria) {

		Long limit = noticeConfiguration.getDefaultLimit();
		Long offset = noticeConfiguration.getDefaultOffset();
		String finalQuery;
		if(noticeCriteria.isAudit())
			finalQuery = paginationAuditWrapper.replace("{}", query);
		else
			finalQuery = paginationWrapper.replace("{}", query);

		if (noticeCriteria.getLimit() != null && noticeCriteria.getLimit() <= noticeConfiguration.getMaxSearchLimit())
			limit = noticeCriteria.getLimit();

		if (noticeCriteria.getLimit() != null && noticeCriteria.getLimit() > noticeConfiguration.getMaxSearchLimit())
			limit = noticeConfiguration.getMaxSearchLimit();

		if (noticeCriteria.getOffset() != null)
			offset = noticeCriteria.getOffset();

		preparedStmtList.add(offset);
		preparedStmtList.add(limit + offset);

		return finalQuery;
	}


}

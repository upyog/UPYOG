package org.egov.notice.repository.builder;

import java.util.List;
import java.util.Set;

import org.egov.notice.web.model.NoticeCriteria;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class NoticeQueryBuilder {
	
	
	private static String noticesearchquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,epnc.\"comment\", epnc.noticeid from public.eg_pt_notice nt inner join eg_pt_notice_comment epnc on epnc.noticeid = nt.uuid";
	private static String noticesearchwithauditquery="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,nt.createdby as nt_cb,nt.createdtime as nt_ct,nt.lastmodifiedby as nt_lmb,nt.lastmodifiedtime as nt_lmt,epnc.\"comment\", epnc.noticeid, epnc.createdby as cm_cb, epnc.createdtime as cm_ct, epnc.lastmodifiedby as cm_lmb, epnc.lastmodifiedtime as cm_lmt from public.eg_pt_notice nt inner join eg_pt_notice_comment epnc on epnc.noticeid = nt.uuid";
	
	public String noticesearchquery(NoticeCriteria noticeCriteria, List<Object> preparedStmtList) {
		// TODO Auto-generated method stub
		StringBuilder builder;
		if(noticeCriteria.isAudit())
			builder=new StringBuilder(noticesearchwithauditquery);
		else
			builder=new StringBuilder(noticesearchquery);
		
		if(!CollectionUtils.isEmpty(noticeCriteria.getTenantIds()))
		{
			Set<String> tenantIds=noticeCriteria.getTenantIds();
			System.out.println("tenantIds::"+tenantIds);
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
		
		return builder.toString();
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

}

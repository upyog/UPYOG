package org.egov.notice.repository;

import java.sql.SQLException;
import java.util.List;

import org.egov.notice.rowmapper.NoticeRowMapper;
import org.egov.notice.web.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepository {
	
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	@Autowired
	NoticeRowMapper noticeRowMapper;
	
	public List<Notice> getnotices() throws SQLException
	{
		String query="select nt.noticetype ,nt.tenantid ,nt.uuid ,nt.noticenumber ,nt.\"name\"  as username,nt.address ,nt.propertyid,nt.assessmentyear,nt.acknowledgementnumber,nt.dateofannualret,nt.entrydate,nt.entrytime,nt.place,nt.perticulars,nt.asreturnfiled,nt.aspermunispality,nt.resolutionon,nt.dated,nt.designation,nt.authorisedpersonname,nt.mobilenumber,nt.penaltyamount,nt.appealno,nt.createdby as nt_cb,nt.createdtime as nt_ct,nt.lastmodifiedby as nt_lmb,nt.lastmodifiedtime as nt_lmt,epnc.\"comment\", epnc.noticeid, epnc.createdby as cm_cb, epnc.createdtime as cm_ct, epnc.lastmodifiedby as cm_lmb, epnc.lastmodifiedtime as cm_lmt from public.eg_pt_notice nt inner join eg_pt_notice_comment epnc on epnc.noticeid = nt.uuid where nt.noticenumber in ('MN-NT-2024-04-17-000449')";
		System.out.println("data"+jdbcTemplate.query(query, noticeRowMapper));
		return jdbcTemplate.query(query, noticeRowMapper);
	}

}

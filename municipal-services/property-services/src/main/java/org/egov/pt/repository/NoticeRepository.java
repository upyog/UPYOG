package org.egov.pt.repository;

import java.util.ArrayList;
import java.util.List;

import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeCriteria;
import org.egov.pt.repository.builder.NoticeQueryBuilder;
import org.egov.pt.repository.rowmapper.NoticeAuditRowMapper;
import org.egov.pt.repository.rowmapper.NoticeRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NoticeRepository {

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Autowired
	NoticeRowMapper noticeRowMapper;

	@Autowired
	NoticeAuditRowMapper noticeAuditRowMapper;

	@Autowired
	NoticeQueryBuilder noticeQueryBuilder;

	public List<Notice> getnotices(NoticeCriteria noticeCriteria)
	{
		List<Notice> notice;
		notice=getnoticedata(noticeCriteria);
		return notice;

	}

	private List<Notice> getnoticedata(NoticeCriteria noticeCriteria) {
		// TODO Auto-generated method stub
		List<Object> preparedStmtList = new ArrayList<>();
		String query=noticeQueryBuilder.noticesearchquery(noticeCriteria,preparedStmtList);
		if(noticeCriteria.isAudit())
			return jdbcTemplate.query(query, preparedStmtList.toArray(), noticeAuditRowMapper);
		else
			return jdbcTemplate.query(query, preparedStmtList.toArray(), noticeRowMapper);
	}


}

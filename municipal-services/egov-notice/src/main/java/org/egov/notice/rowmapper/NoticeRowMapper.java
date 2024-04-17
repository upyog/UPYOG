package org.egov.notice.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.egov.notice.enums.NoticeType;
import org.egov.notice.web.model.AuditDetails;
import org.egov.notice.web.model.Notice;
import org.egov.notice.web.model.NoticeComment;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NoticeRowMapper implements ResultSetExtractor<List<Notice>> {


	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Notice> extractData(ResultSet rs) throws SQLException, DataAccessException {


		Map<String, Notice> noticeMap = new HashMap<>();
		while(rs.next()) {
			String currentAssessmentId = rs.getString("uuid");
			Notice notice = noticeMap.get(currentAssessmentId);
			if(null == notice) {
				notice = Notice.builder()
						.noticeuuid(rs.getString("uuid"))
						.noticeType(NoticeType.fromValue(rs.getString("noticetype")))
						.tenantId(rs.getString("tenantid"))
						.noticeNumber(rs.getString("noticenumber"))
						.address(rs.getString("address"))
						.propertyId(rs.getString("propertyid"))
						.acknowledgementNumber(rs.getString(""))
						.assessmentYear(rs.getString(""))
						.name(rs.getString("username"))
						.noticeComment(new ArrayList<>()).build();

				
				NoticeComment noticeComment = getNoticeComment(rs);
				if(noticeComment!=null)
					notice.getNoticeComment().add(noticeComment);

				


				AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("nt_cb"))
						.createdTime(rs.getLong("nt_ct")).lastModifiedBy(rs.getString("nt_lmb"))
						.lastModifiedTime(rs.getLong("nt_lmt")).build();
				notice.setAuditDetails(auditDetails);

				noticeMap.put(notice.getNoticeuuid(), notice);
			}
		}

		return new ArrayList<>(noticeMap.values());
	}



	private NoticeComment getNoticeComment(ResultSet rs) throws SQLException {
		if(null == rs.getString("noticeid"))
			return null;
		
		org.egov.notice.web.model.AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("cm_cb"))
				.createdTime(rs.getLong("cm_ct")).lastModifiedBy(rs.getString("cm_lmb"))
				.lastModifiedTime(rs.getLong("cm_lmt")).build();


		return NoticeComment.builder().uuid(rs.getString("uuid"))
				.comment(rs.getString("comment"))
				.auditDetails(auditDetails)
				.build();
	}



	



}

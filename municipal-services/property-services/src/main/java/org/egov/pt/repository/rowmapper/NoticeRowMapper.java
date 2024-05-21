package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeComment;
import org.egov.pt.models.enums.NoticeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NoticeRowMapper implements ResultSetExtractor<List<Notice>>{

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
						.acknowledgementNumber(rs.getString("acknowledgementnumber"))
						.assessmentYear(rs.getString("assessmentyear"))
						.name(rs.getString("username"))
						.dateOfAnnualRet(rs.getString("dateofannualret"))
						.entryDate(rs.getString("entrydate"))
						.entryTime(rs.getString("entrytime"))
						.place(rs.getString("place"))
						.particulars(rs.getString("perticulars"))
						.asreturnFiled(rs.getString("asreturnfiled"))
						.asperMunispality(rs.getString("aspermunispality"))
						.resolutionOn(rs.getString("resolutionon"))
						.dated(rs.getString("dated"))
						.designation(rs.getString("designation"))
						.authorisedpersonName(rs.getString("authorisedpersonname"))
						.mobilenumber(rs.getString("mobilenumber"))
						.penaltyAmount(rs.getString("penaltyamount"))
						.appealNo(rs.getString("appealno"))
						.noticeComment(new ArrayList<>()).build();


				NoticeComment noticeComment = getNoticeComment(rs);
				if(noticeComment!=null)
					notice.getNoticeComment().add(noticeComment);
				
				noticeMap.put(notice.getNoticeuuid(), notice);
			}
			else
				addcommentnotice(rs,notice);
		}

		return new ArrayList<>(noticeMap.values());
	}



	private NoticeComment getNoticeComment(ResultSet rs) throws SQLException {
		if(null == rs.getString("noticeid"))
			return null;

		return NoticeComment.builder().uuid(rs.getString("uuid"))
				.comment(rs.getString("comment"))
				.build();
	}

	private void addcommentnotice(ResultSet rs,Notice notice) throws SQLException
	{

		NoticeComment comment= NoticeComment.builder().uuid(rs.getString("uuid"))
				.comment(rs.getString("comment"))
				.build();

		notice.getNoticeComment().add(comment);
	}
	

}

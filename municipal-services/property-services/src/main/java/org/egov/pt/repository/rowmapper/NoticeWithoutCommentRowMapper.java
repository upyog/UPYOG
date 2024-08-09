package org.egov.pt.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.pt.models.AuditDetails;
import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeComment;
import org.egov.pt.models.enums.NoticeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class NoticeWithoutCommentRowMapper implements ResultSetExtractor<List<Notice>>{

	@Autowired
	private ObjectMapper mapper;

	@Override
	public List<Notice> extractData(ResultSet rs) throws SQLException, DataAccessException {


		Map<String, Notice> noticeMap = new HashMap<>();
		while(rs.next()) {
			String currentAssessmentId = rs.getString("uuid");
			Notice notice = noticeMap.get(currentAssessmentId);
			if(null == notice) {
				
				AuditDetails auditdetails = getAuditDetail(rs, "property");
				
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
						.resolutionOn(rs.getString("resolutionon"))
						.dated(rs.getString("dated"))
						.designation(rs.getString("designation"))
						.authorisedpersonName(rs.getString("authorisedpersonname"))
						.mobilenumber(rs.getString("mobilenumber"))
						.penaltyAmount(rs.getString("penaltyamount"))
						.appealNo(rs.getString("appealno"))
						.auditDetails(auditdetails)
						.noticeComment(new ArrayList<>()).build();

				noticeMap.put(notice.getNoticeuuid(), notice);
			}

		}

		return new ArrayList<>(noticeMap.values());
	}

	private AuditDetails getAuditDetail(ResultSet rs, String source) throws SQLException {

		switch (source) {

		case "property":

			Long lastModifiedTime = rs.getLong("nt_lmt");
			if (rs.wasNull()) {
				lastModifiedTime = null;
			}

			return AuditDetails.builder().createdBy(rs.getString("nt_cb"))
					.createdTime(rs.getLong("nt_ct")).lastModifiedBy(rs.getString("nt_lmb"))
					.lastModifiedTime(lastModifiedTime).build();

		default: 
			return null;

		}

	}

}

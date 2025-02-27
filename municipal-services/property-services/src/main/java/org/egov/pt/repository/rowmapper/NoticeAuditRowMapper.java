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
public class NoticeAuditRowMapper implements ResultSetExtractor<List<Notice>>{

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
						.resolutionOn(rs.getString("resolutionon"))
						.dated(rs.getString("dated"))
						.designation(rs.getString("designation"))
						.authorisedpersonName(rs.getString("authorisedpersonname"))
						.mobilenumber(rs.getString("mobilenumber"))
						.penaltyAmount(rs.getString("penaltyamount"))
						.appealNo(rs.getString("appealno"))
						.rule_23(rs.getBoolean("rule_23"))
						.rule_33(rs.getBoolean("rule_33"))
						.rule_34(rs.getBoolean("rule_34"))
						.rule_36(rs.getBoolean("rule_36"))
						.failedtoproducenecessarydocuments(rs.getBoolean("failedto_produce_necessary_documents"))
						.willfullyfurnishesincorrectinformation(rs.getBoolean("willfully_furnishes_incorrect_information"))
						.obstructanyauthorityappointed(rs.getBoolean("obstruct_any_authority_appointed"))
						.noticeComment(new ArrayList<>()).build();

				NoticeComment noticeComment = getNoticeComment(rs);
				if(noticeComment!=null)
					notice.getNoticeComment().add(noticeComment);
				
				AuditDetails auditDetails=getauditdetails(rs);
				if(auditDetails!=null)
				notice.setAuditDetails(auditDetails);

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

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("cm_cb"))
				.createdTime(rs.getLong("cm_ct")).lastModifiedBy(rs.getString("cm_lmb"))
				.lastModifiedTime(rs.getLong("cm_lmt")).build();


		return NoticeComment.builder().uuid(rs.getString("uuid"))
				.comment(rs.getString("comment"))
				.auditDetails(auditDetails)
				.particulars(rs.getString("particulars"))
				.asreturnFiled(rs.getString("asperreturnfiled"))
				.asperMunispality(rs.getString("aspermunicipality"))
				.remarks(rs.getString("remarks"))
				.build();
	}

	private void addcommentnotice(ResultSet rs,Notice notice) throws SQLException
	{

		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("cm_cb"))
				.createdTime(rs.getLong("cm_ct")).lastModifiedBy(rs.getString("cm_lmb"))
				.lastModifiedTime(rs.getLong("cm_lmt")).build();


		NoticeComment comment= NoticeComment.builder().uuid(rs.getString("uuid"))
				.comment(rs.getString("comment"))
				.auditDetails(auditDetails)
				.particulars(rs.getString("particulars"))
				.asreturnFiled(rs.getString("asperreturnfiled"))
				.asperMunispality(rs.getString("aspermunicipality"))
				.remarks(rs.getString("remarks"))
				.build();

		notice.getNoticeComment().add(comment);
	}
	
	private AuditDetails getauditdetails(ResultSet rs) throws SQLException
	{
		AuditDetails auditDetails = AuditDetails.builder().createdBy(rs.getString("nt_cb"))
				.createdTime(rs.getLong("nt_ct")).lastModifiedBy(rs.getString("nt_lmb"))
				.lastModifiedTime(rs.getLong("nt_lmt")).build();
		
		return auditDetails;
	}

}

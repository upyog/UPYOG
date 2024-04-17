package org.egov.notice.service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.notice.config.NoticeConfiguration;
import org.egov.notice.util.CommonUtils;
import org.egov.notice.web.model.Notice;
import org.egov.notice.web.model.NoticeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cedarsoftware.util.StringUtilities;

@Service
public class EnrichmentService {
	
	@Autowired
	NoticeConfiguration noticonfig;
	
	@Autowired
	CommonUtils noticeutil;
	
	public void enrichCreateRequest(NoticeRequest noticeRequest)
	{
		setIdgenIds(noticeRequest);
		setCommentIds(noticeRequest.getNotice());
		noticeRequest.getNotice().setAuditDetails(noticeutil.getAuditDetails(noticeRequest.getRequestInfo().getUserInfo().getUuid(), true));
		noticeRequest.getNotice().getNoticeComment().stream().forEach(audt->audt.setAuditDetails(noticeutil.getAuditDetails(noticeRequest.getRequestInfo().getUserInfo().getUuid(), true)));
	}

	private void setCommentIds(Notice noticerequest) {
		
		noticerequest.getNoticeComment().stream().filter(x-> StringUtilities.isEmpty(x.getUuid())).forEach(x-> x.setUuid(UUID.randomUUID().toString()));
	}

	private void setIdgenIds(NoticeRequest noticerequest) {
		// TODO Auto-generated method stub
		String tanetid=noticerequest.getNotice().getTenantId();
		String noticeId = noticeutil.getIdList(noticerequest.getRequestInfo(), tanetid, noticonfig.getNoticeidname(), noticonfig.getNoticeformat(), 1).get(0);
		noticerequest.getNotice().setNoticeNumber(noticeId);
		noticerequest.getNotice().setNoticeuuid(UUID.randomUUID().toString());
	}

}

package org.egov.notice.service;

import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.notice.config.NoticeConfiguration;
import org.egov.notice.util.CommonUtils;
import org.egov.notice.web.model.Notice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cedarsoftware.util.StringUtilities;

@Service
public class EnrichmentService {
	
	@Autowired
	NoticeConfiguration noticonfig;
	
	@Autowired
	CommonUtils noticeutil;
	
	public void enrichCreateRequest(Notice noticerequest, RequestInfo requestInfo)
	{
		setIdgenIds(noticerequest,requestInfo);
		setCommentIds(noticerequest);
	}

	private void setCommentIds(Notice noticerequest) {
		
		noticerequest.getNoticeComment().stream().filter(x-> StringUtilities.isEmpty(x.getUuid())).forEach(x-> x.setUuid(UUID.randomUUID().toString()));
	}

	private void setIdgenIds(Notice noticerequest, RequestInfo requestInfo) {
		// TODO Auto-generated method stub
		String tanetid=noticerequest.getTenantId();
		String noticeId = noticeutil.getIdList(requestInfo, tanetid, noticonfig.getNoticeidname(), noticonfig.getNoticeformat(), 1).get(0);
		noticerequest.setNoticeNumber(noticeId);
		noticerequest.setNoticeuuid(UUID.randomUUID().toString());
	}

}

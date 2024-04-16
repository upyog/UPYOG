package org.egov.notice.service;

import org.egov.notice.enums.NoticeType;
import org.egov.notice.web.model.Notice;

public class NoticeService {
	
	public Notice saveNoticeData(Notice notice)
	{
		if(notice.getNoticeType().equals(NoticeType.REC_MIS_DEF_RET))
		{
			
		}
		return notice;
	}

}

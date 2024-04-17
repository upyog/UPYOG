package org.egov.notice.service;

import org.egov.notice.config.NoticeConfiguration;
import org.egov.notice.enums.NoticeType;
import org.egov.notice.producer.NoticeProducer;
import org.egov.notice.repository.NoticeRepository;
import org.egov.notice.util.CommonUtils;
import org.egov.notice.web.model.Notice;
import org.egov.notice.web.model.NoticeRequest;
import org.egov.notice.web.model.NoticeResponse;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cedarsoftware.util.StringUtilities;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;


@Service
public class NoticeService {

	@Autowired
	EnrichmentService enrichmentService;

	@Autowired
	NoticeProducer noticeProducer;

	@Autowired
	NoticeConfiguration noticeconfig;
	
	@Autowired
	NoticeRepository noticeRepository;

	public Notice saveNoticeData(NoticeRequest noticeRequest)
	{
		validateForRecMistakeDefectiveReturn(noticeRequest.getNotice());
		enrichmentService.enrichCreateRequest(noticeRequest);
		noticeProducer.push(noticeconfig.getSavenoticetopic(), noticeRequest);
		return noticeRequest.getNotice();
	}

	private void validateForRecMistakeDefectiveReturn(Notice noticeFromRequest) {
		if(StringUtilities.isEmpty(noticeFromRequest.getAcknowledgementNumber())) {
			throw new CustomException("INVALID_ACK_NUM", "Invalid Acknowledgement Number");

		}
		if(StringUtilities.isEmpty(noticeFromRequest.getAssessmentYear())) {
			throw new CustomException("INVALID_ASMT_YEAR", "Invalid Assessment Year");

		}	

		if(StringUtilities.isEmpty(noticeFromRequest.getPropertyId())) {
			throw new CustomException("INVALID_PROP_NUM", "Invalid Property Id or UPIN");

		}




	}
	
	public List<Notice> getsearchdata() throws SQLException
	{
		return noticeRepository.getnotices();
	}

}

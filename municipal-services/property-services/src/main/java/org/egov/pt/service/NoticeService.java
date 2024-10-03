package org.egov.pt.service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeCriteria;
import org.egov.pt.producer.PropertyProducer;
import org.egov.pt.repository.NoticeRepository;
import org.egov.pt.web.contracts.NoticeRequest;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Service
public class NoticeService {
	


	@Autowired
	EnrichmentService enrichmentService;

	@Autowired
	PropertyProducer noticeProducer;

	@Autowired
	PropertyConfiguration noticeconfig;
	
	@Autowired
	NoticeRepository noticeRepository;

	public Notice saveNoticeData(NoticeRequest noticeRequest)
	{
		validateForRecMistakeDefectiveReturn(noticeRequest.getNotice());
		enrichmentService.enrichCreateNoticeRequest(noticeRequest);
		noticeProducer.push(noticeconfig.getSavenoticetopic(), noticeRequest);
		return noticeRequest.getNotice();
	}

	private void validateForRecMistakeDefectiveReturn(Notice noticeFromRequest) {
		if(StringUtils.isEmpty(noticeFromRequest.getAcknowledgementNumber())) 
			throw new CustomException("INVALID_ACK_NUM", "Invalid Acknowledgement Number");

		if(StringUtils.isEmpty(noticeFromRequest.getAssessmentYear()))
			throw new CustomException("INVALID_ASMT_YEAR", "Invalid Assessment Year");	

		if(StringUtils.isEmpty(noticeFromRequest.getPropertyId()))
			throw new CustomException("INVALID_PROP_NUM", "Invalid Property Id or UPIN");

	}
	
	public List<Notice> searchNotice(NoticeCriteria noticeCriteria, RequestInfoWrapper requestInfoWrapper)
	{
		List<Notice> notice;
		if(noticeCriteria.isAudit() && CollectionUtils.isEmpty(noticeCriteria.getNoticenumber()))
			throw new CustomException("EG_PT_NOTICE_AUDIT_ERROR", "Audit can only be provided for Noticenumbers");
		
		if(CollectionUtils.isEmpty(noticeCriteria.getTenantId()))
			throw new CustomException("EG_PT_NOTICE_TANENTID_ERROR","Please provide tanentID for search result");
		
		else
			notice=noticeRepository.getnotices(noticeCriteria);
		
		notice=notice.stream().sorted((x,y)->x.getAuditDetails().getCreatedTime().compareTo(y.getAuditDetails().getCreatedTime())).collect(Collectors.toList());
		return notice;
	}



}

package org.egov.pt.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.config.PropertyConfiguration;
import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeCriteria;
import org.egov.pt.models.OwnerInfo;
import org.egov.pt.models.Property;
import org.egov.pt.models.PropertyCriteria;
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
	
	@Autowired
	PropertyService propertyService;
	

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
		List<Notice> filterdNotice;
		Set<String> propertyIds=new HashSet<String>();
		Set<String> noticePropertyIds=new HashSet<String>();
		List<Property> properties = null;
		List<Property> FilterProperties = new ArrayList<Property>();
		RequestInfo requestInfo=requestInfoWrapper.getRequestInfo();
		PropertyCriteria propertyCriteria=new PropertyCriteria();
		
		if(noticeCriteria.isAudit() && CollectionUtils.isEmpty(noticeCriteria.getNoticenumber()))
			throw new CustomException("EG_PT_NOTICE_AUDIT_ERROR", "Audit can only be provided for Noticenumbers");
		
		if(CollectionUtils.isEmpty(noticeCriteria.getTenantId()))
			throw new CustomException("EG_PT_NOTICE_TANENTID_ERROR","Please provide tanentID for search result");
		
		else
			notice=noticeRepository.getnotices(noticeCriteria);
		
		notice=notice.stream().sorted((x,y)->y.getNoticeNumber().compareTo(x.getNoticeNumber())).collect(Collectors.toList());
		for (Notice noticelist : notice) {
			propertyIds.add(noticelist.getPropertyId());
		}
		
		propertyCriteria.setPropertyIds(propertyIds);
		properties=propertyService.searchProperty(propertyCriteria, requestInfo);
		for (Property property : properties) {
			for (OwnerInfo owner : property.getOwners()) {
				if(owner.getMobileNumber().equalsIgnoreCase(requestInfo.getUserInfo().getMobileNumber()))
					FilterProperties.add(property);
			}
		}
		
		for (Property property : FilterProperties) {
			noticePropertyIds.add(property.getPropertyId());
		}
		noticeCriteria.setPropertyIds(noticePropertyIds);
		notice=noticeRepository.getnotices(noticeCriteria);
		
		return notice;
	}



}

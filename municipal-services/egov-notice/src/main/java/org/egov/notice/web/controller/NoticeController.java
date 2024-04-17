package org.egov.notice.web.controller;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Map;

import javax.validation.Valid;

import com.fasterxml.jackson.databind.JsonNode;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.notice.service.NoticeService;
import org.egov.notice.util.ResponseInfoFactory;
import org.egov.notice.web.model.InboxRequest;
import org.egov.notice.web.model.InboxResponse;
import org.egov.notice.web.model.Notice;
import org.egov.notice.web.model.NoticeRequest;
import org.egov.notice.web.model.NoticeResponse;
import org.egov.notice.web.model.dss.InboxMetricCriteria;
import org.egov.notice.web.model.elasticsearch.InboxElasticSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Home redirection to swagger api documentation 
 */
@RestController
@RequestMapping("/v1")
public class NoticeController {
	
	
	
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	

	
	@Autowired
	NoticeService noticeService;
	
	
	@PostMapping(value = "/_save")
	public ResponseEntity<NoticeResponse> save( @RequestBody  NoticeRequest noticeRequest) {
		Notice notice = noticeService.saveNoticeData(noticeRequest);
		ResponseInfo resinfo=responseInfoFactory.createResponseInfoFromRequestInfoCommonContract(noticeRequest.getRequestInfo(), true);
		NoticeResponse response=NoticeResponse.builder().notice(Arrays.asList(notice)).responseInfo(resinfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value="/_search")
	public void search() throws SQLException
	{
		noticeService.getsearchdata();
	}

	
}

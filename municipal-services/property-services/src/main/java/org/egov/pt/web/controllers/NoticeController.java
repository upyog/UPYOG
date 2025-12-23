package org.egov.pt.web.controllers;

import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.pt.models.Notice;
import org.egov.pt.models.NoticeCriteria;
import org.egov.pt.service.NoticeService;
import org.egov.pt.util.ResponseInfoFactory;
import org.egov.pt.web.contracts.NoticeRequest;
import org.egov.pt.web.contracts.NoticeResponse;
import org.egov.pt.web.contracts.RequestInfoWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notice")
public class NoticeController {
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@Autowired
	NoticeService noticeService;
		
	@PostMapping(value = "/_save")
	public ResponseEntity<NoticeResponse> save( @RequestBody  NoticeRequest noticeRequest) {
		Notice notice = noticeService.saveNoticeData(noticeRequest);
		ResponseInfo resinfo=responseInfoFactory.createResponseInfoFromRequestInfo(noticeRequest.getRequestInfo(), true);
		NoticeResponse response=NoticeResponse.builder().notice(Arrays.asList(notice)).responseInfo(resinfo).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@PostMapping(value="/_search")
	public ResponseEntity<NoticeResponse> search(@RequestBody RequestInfoWrapper requestInfoWrapper,@ModelAttribute NoticeCriteria noticeCriteria)
	{
		List<Notice> notices;
		notices=noticeService.searchNotice(noticeCriteria,requestInfoWrapper);
		NoticeResponse noticeResponse=NoticeResponse.builder().responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true)).notice(notices).build();
		return new ResponseEntity<>(noticeResponse, HttpStatus.OK);
	}

	


}

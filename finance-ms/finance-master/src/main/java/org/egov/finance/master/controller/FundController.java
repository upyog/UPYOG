/**
 * Created on May 30, 2025.
 * 
 * @author bikashdhal
 */

package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.entity.Fund;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.FundRequest;
import org.egov.finance.master.model.response.FundResponse;
import org.egov.finance.master.model.response.FundResponse.FundResponseBuilder;
import org.egov.finance.master.service.FundService;
import org.egov.finance.master.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
@RestController
@RequestMapping("/fund")
@Slf4j
public class FundController {
	
	
	@Autowired
	private FundService fundService;
	
	 @Autowired
	    private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_save")
	private ResponseEntity<?>saveFund(@Valid @RequestBody FundRequest fund){
		final FundModel fundM = fundService.save(fund);
		ResponseInfo resInfo =responseInfoFactory.createResponseInfoFromRequestInfo(fund.getRequestInfo(), true);
		FundResponse response = FundResponse.builder()
		.responseInfo(resInfo)
		.funds(Arrays.asList(fundM))
		.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@PostMapping(value = "/_search")
	public ResponseEntity<?> _search(@RequestBody FundRequest request) {
		final List<FundModel> fundM  = fundService.search(request.getFund());
		ResponseInfo resInfo =responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		FundResponse response = FundResponse.builder()
		.responseInfo(resInfo)
		.funds(fundM)
		.build();
		return new ResponseEntity<>(response,HttpStatus.OK);
	}
	

}

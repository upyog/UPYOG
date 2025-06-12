/**
 * 
 */
package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.SubSchemeModel;
import org.egov.finance.master.model.request.SubSchemeRequest;
import org.egov.finance.master.model.response.SubSchemeResponse;
import org.egov.finance.master.service.SubSchemeService;
import org.egov.finance.master.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/**
 * SubSchemeController.java
 * 
 * @author bpattanayak
 * @date 11 Jun 2025
 * @version 1.0
 */

@RestController
@RequestMapping("/SubScheme")
public class SubSchemeController {
	
	private final SubSchemeService subSchemeService;
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	public SubSchemeController(SubSchemeService subSchemeService,ResponseInfoFactory responseInfoFactory) {
		this.subSchemeService = subSchemeService;
		this.responseInfoFactory=responseInfoFactory;
	}

	@PostMapping("/_save")
	public ResponseEntity<SubSchemeResponse> save(@Valid @RequestBody SubSchemeRequest subSchemeRequest)
	{
		final SubSchemeModel subSchemeModel=subSchemeService.save(subSchemeRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(subSchemeRequest.getRequestInfo(), true);
		SubSchemeResponse subSchemeResponse=SubSchemeResponse.builder().responseInfo(resInfo).subSchemeResponse(Arrays.asList(subSchemeModel)).build();
		return new ResponseEntity<>(subSchemeResponse,HttpStatus.CREATED);
	}
	
	@PostMapping("/_search")
	public ResponseEntity<SubSchemeResponse> search(@RequestBody SubSchemeRequest subSchemeRequest)
	{
		final List<SubSchemeModel> subSchemeModels=subSchemeService.search(subSchemeRequest.getSubSchemeRequest());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(subSchemeRequest.getRequestInfo(), true);
		SubSchemeResponse subSchemeResponse = SubSchemeResponse.builder().responseInfo(resInfo).subSchemeResponse(subSchemeModels).build();
		return new ResponseEntity<>(subSchemeResponse, HttpStatus.OK);
	}
	
	@PostMapping("/_update")
	public ResponseEntity<SubSchemeResponse> update(@Valid @RequestBody SubSchemeRequest subSchemeRequest)
	{
		final SubSchemeModel subSchemeModel=subSchemeService.update(subSchemeRequest);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(subSchemeRequest.getRequestInfo(), true);
		SubSchemeResponse subSchemeResponse=SubSchemeResponse.builder().responseInfo(resInfo).subSchemeResponse(Arrays.asList(subSchemeModel)).build();
		return new ResponseEntity<>(subSchemeResponse,HttpStatus.CREATED);
	}

}

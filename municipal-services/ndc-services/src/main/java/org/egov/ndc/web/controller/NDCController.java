/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.ndc.web.controller;

import org.egov.common.contract.response.ResponseInfo;
import org.egov.ndc.config.ResponseInfoFactory;
import org.egov.ndc.service.NDCService;
import org.egov.ndc.web.model.RequestInfoWrapper;
import org.egov.ndc.web.model.ndc.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ndc")
public class NDCController {
	
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@Autowired
	private NDCService ndcService;

	@PostMapping("/_create")
	public ResponseEntity<NdcApplicationResponse> createNdcApplication(@RequestParam(required = false) boolean skipWorkFlow,@RequestBody NdcApplicationRequest ndcApplicationRequest) {
		NdcApplicationRequest request = ndcService.createNdcApplication(skipWorkFlow,ndcApplicationRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();


		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PutMapping("/_update")
	public ResponseEntity<NdcApplicationResponse> updateNdcApplication(@RequestParam(required = false) boolean skipWorkFlow,@RequestBody NdcApplicationRequest ndcApplicationRequest) {
		NdcApplicationRequest request = ndcService.updateNdcApplication(skipWorkFlow, ndcApplicationRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/_delete")
	public ResponseEntity<NdcApplicationResponse> deleteNdcApplication(@RequestBody NdcDeleteRequest ndcDeleteRequest) {
		NdcApplicationRequest request = ndcService.deleteNdcApplication(ndcDeleteRequest);
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);

		NdcApplicationResponse response = NdcApplicationResponse.builder()
				.responseInfo(responseInfo)
				.applications(request.getApplications())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_search")
	public ResponseEntity<NdcApplicationSearchResponse> searchNdcApplications(
			@RequestBody RequestInfoWrapper requestInfoWrapper,
			@ModelAttribute NdcApplicationSearchCriteria criteria) {
		List<Application> applications = ndcService.searchNdcApplications(criteria,requestInfoWrapper.getRequestInfo() );
		ResponseInfo responseInfo = responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(), true);

		NdcApplicationSearchResponse response = NdcApplicationSearchResponse.builder()
				.responseInfo(responseInfo)
				.applications(applications)
				.totalCount(applications.size())
				.build();

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}
package org.upyog.employee.dasboard.web.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.annotations.ApiParam;

@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-03T17:01:27.450+05:30")

@Controller
@RequestMapping("/employee-dasboard")
public class EmployeeDashaboardApiController {

	private final ObjectMapper objectMapper;

	private final HttpServletRequest request;

	@Autowired
	public EmployeeDashaboardApiController(ObjectMapper objectMapper, HttpServletRequest request) {
		this.objectMapper = objectMapper;
		this.request = request;
	}

	@RequestMapping(value = "/employee-dashaboard/_search", method = RequestMethod.POST)
	public ResponseEntity<EmployeeDashboardResponse> employeeDashaboardSearchPost(
			@NotNull @ApiParam(value = "Unique id for a tenant.", required = true) @Valid @RequestParam(value = "tenantId", required = true) String tenantId,
			@ApiParam(value = "Parameter to carry Request metadata in the request body") @Valid @RequestBody org.egov.common.contract.request.RequestInfo requestInfo,
			@ApiParam(value = "Search based on status.") @Valid @RequestParam(value = "status", required = false) String status,
			@Size(max = 50) @ApiParam(value = "unique identifier of birth registration") @Valid @RequestParam(value = "ids", required = false) List<Long> ids,
			@Size(min = 2, max = 64) @ApiParam(value = "Unique application number for the Birth Registration Application") @Valid @RequestParam(value = "applicationNumber", required = false) String applicationNumber) {
		String accept = request.getHeader("Accept");
		if (accept != null && accept.contains("application/json")) {
			try {
				return new ResponseEntity<EmployeeDashboardResponse>(objectMapper.readValue(
						"{  \"ResponseInfo\" : {    \"ver\" : \"ver\",    \"resMsgId\" : \"resMsgId\",    \"msgId\" : \"msgId\",    \"apiId\" : \"apiId\",    \"ts\" : 0,    \"status\" : \"SUCCESSFUL\"  },  \"EmployeeDashbaord\" : \"{}\"}",
						EmployeeDashboardResponse.class), HttpStatus.NOT_IMPLEMENTED);
			} catch (IOException e) {
				return new ResponseEntity<EmployeeDashboardResponse>(HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}

		return new ResponseEntity<EmployeeDashboardResponse>(HttpStatus.NOT_IMPLEMENTED);
	}

}

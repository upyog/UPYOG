package org.upyog.employee.dasboard.web.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.upyog.employee.dasboard.service.EmployeeDashboardService;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardRequest;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;


@RestController
@Tag(name = "Employee Dashboard Controller", description = "Operations related to Employee Dashboard")
public class EmployeeDashaboardApiController {

	@Autowired
	private EmployeeDashboardService dashboardService;
	

	@PostMapping("/_search")
	public ResponseEntity<EmployeeDashboardResponse> getDashboardData(
			@Parameter(description = "Details of the Employee Dasboard for All the modules", required = true)
			@Valid @RequestBody EmployeeDashboardRequest employeeDashboardRequest
			) {

		EmployeeDashboardResponse response = dashboardService.getEmployeeDashboardData(employeeDashboardRequest);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

}

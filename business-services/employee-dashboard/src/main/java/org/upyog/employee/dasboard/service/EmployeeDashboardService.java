package org.upyog.employee.dasboard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.employee.dasboard.repository.ServiceRequestRepository;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardRequest;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardResponse;

@Service
public interface EmployeeDashboardService {

	
	
	EmployeeDashboardResponse getEmployeeDashboardData(EmployeeDashboardRequest employeeDashboardRequest);
}

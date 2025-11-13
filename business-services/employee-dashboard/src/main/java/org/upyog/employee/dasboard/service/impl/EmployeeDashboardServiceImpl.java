package org.upyog.employee.dasboard.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.employee.dasboard.query.constant.DashboardConstants;
import org.upyog.employee.dasboard.repository.impl.ServiceRequestRepositoryImpl;
import org.upyog.employee.dasboard.service.EmployeeDashboardService;
import org.upyog.employee.dasboard.util.ResponseInfoUtil;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardRequest;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardResponse;
import org.upyog.employee.dasboard.web.models.ResponseInfo;
import org.upyog.employee.dasboard.web.models.ResponseInfo.StatusEnum;

@Service
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {
	@Autowired
	private ServiceRequestRepositoryImpl dashboardRepository;

	public EmployeeDashboardResponse getEmployeeDashboardData(EmployeeDashboardRequest employeeDashboardRequest) {
		System.out.println("employeeDashboardRequest--" + employeeDashboardRequest);

		//Getting the data from the repository and setting the employee dashboard response
		EmployeeDashboardDetails dashboardDetails = dashboardRepository.fetchModuleData(employeeDashboardRequest);
		System.out.println("dashboardDetails--" + dashboardDetails);
		
		ResponseInfo info = ResponseInfoUtil.createResponseInfo(employeeDashboardRequest.getRequestInfo(), DashboardConstants.DATA_FETCHED_SUCCESSFULLY, StatusEnum.SUCCESSFUL
				);
		 EmployeeDashboardResponse response = EmployeeDashboardResponse.builder()
			        .responseInfo(info) 
			        .employeeDashbaord(dashboardDetails) 
			        .build();

		return response;
	}
}

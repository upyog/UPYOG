package org.upyog.employee.dasboard.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.employee.dasboard.repository.impl.ServiceRequestRepositoryImpl;
import org.upyog.employee.dasboard.service.EmployeeDashboardService;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardRequest;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardResponse;
import org.upyog.employee.dasboard.web.models.ResponseInfo;

@Service
public class EmployeeDashboardServiceImpl implements EmployeeDashboardService {
	@Autowired
	private ServiceRequestRepositoryImpl dashboardRepository;

	public EmployeeDashboardResponse getEmployeeDashboardData(EmployeeDashboardRequest employeeDashboardRequest) {
		System.out.println("employeeDashboardRequest--" + employeeDashboardRequest);

		//Getting the data from the repository and setting the employee dashboard response
		EmployeeDashboardDetails dashboardDetails = dashboardRepository.fetchModuleData(employeeDashboardRequest);
		System.out.println("dashboardDetails--" + dashboardDetails);

		ResponseInfo responseInfo = new ResponseInfo();
		if (employeeDashboardRequest.getRequestInfo() != null) {
			responseInfo.setApiId(employeeDashboardRequest.getRequestInfo().getApiId());
			responseInfo.setVer(employeeDashboardRequest.getRequestInfo().getVer());
			responseInfo.setTs(System.currentTimeMillis());
			responseInfo.setMsgId(employeeDashboardRequest.getRequestInfo().getMsgId());
			responseInfo.setResMsgId("");
			// responseInfo.setStatus("");
		}

		EmployeeDashboardResponse response = new EmployeeDashboardResponse();
		response.setEmployeeDashbaord(dashboardDetails);
		response.setResponseInfo(responseInfo);

		return response;
	}
}

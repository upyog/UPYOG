package org.upyog.employee.dasboard.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.upyog.employee.dasboard.query.constant.DashboardQueryConstant;
import org.upyog.employee.dasboard.repository.ServiceRequestRepository;
import org.upyog.employee.dasboard.repository.rowMapper.EmployeeDashboardDetailsRowMapper;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardDetails;
import org.upyog.employee.dasboard.web.models.EmployeeDashboardRequest;
import org.upyog.employee.dasboard.web.models.ModuleName;

@Repository
public class ServiceRequestRepositoryImpl implements ServiceRequestRepository {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	DashboardQueryConstant dashboardQueryConstant;

	@Autowired
	public ServiceRequestRepositoryImpl(JdbcTemplate jdbcTemplate) {

		this.jdbcTemplate = jdbcTemplate;
	}

	
	public static final Logger log = LoggerFactory.getLogger(ServiceRequestRepositoryImpl.class);

	//Selecting the query based on the moduleName 
	public EmployeeDashboardDetails fetchModuleData(EmployeeDashboardRequest employeeDashboardRequest) {
		ModuleName moduleName = employeeDashboardRequest.getModuleName();
		StringBuilder query;

		switch (moduleName) {
		case OBPAS:
			query = dashboardQueryConstant.OBPAS_DASHBOARD_QUERY_;

			break;
		case ASSET:
			query = dashboardQueryConstant.ASSET_DASHBOARD_QUERY_;
			break;
		case FSM:
			query = dashboardQueryConstant.FSM_DASHBOARD_QUERY_;
			break;
		case PGR:
			query = dashboardQueryConstant.PGR_DASHBOARD_QUERY_;
			break;
		case CHB:
			query = dashboardQueryConstant.CHB_DASHBOARD_QUERY_;
			break;
		case PT:
			query = dashboardQueryConstant.PT_DASHBOARD_QUERY_;
			break;
		case SEWERAGE:
			query = dashboardQueryConstant.SEWERAGE_DASHBOARD_QUERY_;
			break;	
		case PETSERVICES:
			query = dashboardQueryConstant.PETSERVICES_DASHBOARD_QUERY_;
			break;
		case TL:
			query = dashboardQueryConstant.TL_DASHBOARD_QUERY_;
			break;
		case WATER:
			query = dashboardQueryConstant.WATER_DASHBOARD_QUERY_;
			break;		
		case EWASTE:
			query = dashboardQueryConstant.EWASTE_DASHBOARD_QUERY_;
			break;
		case ALL:
			query = dashboardQueryConstant.DASHBOARD_QUERY_ALL;
			break;
		default:
			throw new IllegalArgumentException("Module Not found");
		}
		log.info("Executing query for module: {} - Query: {}", moduleName, query.toString());

		return jdbcTemplate.queryForObject(query.toString(), new EmployeeDashboardDetailsRowMapper(moduleName));
	}

	@Override
	public EmployeeDashboardDetails fetchModuleData(String moduleName) {
		// TODO Auto-generated method stub
		return null;
	}
}

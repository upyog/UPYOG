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
			query = DashboardQueryConstant.OBPAS_DASHBOARD_QUERY_;

			break;
		case ASSET:
			query = DashboardQueryConstant.ASSET_DASHBOARD_QUERY_;
			break;
		case FSM:
			query = DashboardQueryConstant.FSM_DASHBOARD_QUERY_;
			break;
		case PGR:
			query = DashboardQueryConstant.PGR_DASHBOARD_QUERY_;
			break;
		case CHB:
			query = DashboardQueryConstant.CHB_DASHBOARD_QUERY_;
			break;
		case PT:
			query = DashboardQueryConstant.PT_DASHBOARD_QUERY_;
			break;
		case SEWERAGE:
			query = DashboardQueryConstant.SEWERAGE_DASHBOARD_QUERY_;
			break;	
		case PETSERVICES:
			query = DashboardQueryConstant.PETSERVICES_DASHBOARD_QUERY_;
			break;
		case TL:
			query = DashboardQueryConstant.TL_DASHBOARD_QUERY_;
			break;
		case WATER:
			query = DashboardQueryConstant.WATER_DASHBOARD_QUERY_;
			break;		
		case EWASTE:
			query = DashboardQueryConstant.EWASTE_DASHBOARD_QUERY_;
			break;
		case ALL:
			query = DashboardQueryConstant.DASHBOARD_QUERY_ALL;
			break;
		default:
			throw new IllegalArgumentException("Module Not found");
		}
		log.info("Executing query for module: {} - Query: {}", moduleName, query.toString());

		return jdbcTemplate.queryForObject(query.toString(), new EmployeeDashboardDetailsRowMapper(moduleName));
	}

	
}

/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
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
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
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
 *
 */
package org.egov.pims.service;

import org.egov.commons.EgwStatus;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.service.EntityTypeService;
import org.egov.commons.utils.EntityType;
import org.egov.eis.entity.EmployeeView;
import org.egov.infra.admin.master.service.AppConfigValueService;
import org.egov.infra.exception.ApplicationRuntimeException;
import org.egov.infra.persistence.utils.Page;
import org.egov.infra.script.service.ScriptService;
import org.egov.infra.utils.DateUtils;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.model.PersonalInformation;

import org.hibernate.HibernateException;
import org.hibernate.Session;




import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author DivyaShree
 *
 */
public class PersonalInformationService extends PersistenceService<PersonalInformation, Integer> implements EntityTypeService 
{
	//named query tags
	private static final String ACTIVEEMPSBYLOGGEDINUSER="EMPVIEW-ACTIVE-EMPS-BYLOGGEDINUSER";
	private final String PERSONALINFOBYIDS="PERSONALINFO-BYIDS";
	private final String PERSONALINFOEMPCODESTARTSWITH="PERSONALINFO-EMPCODE-STARTSWITH";
	private final String EMPVIEWBYLOGGEDINUSER="EMPVIEW-EMPS-BYLOGGEDINUSER";
	private final String EMPVIEWDEPTIDSLOGGEDINUSER="EMPVIEW-DEPTIDS-LOGGEDINUSER";
	private static final String EMPVIEWACTIVEEMPS="EMPVIEW-ACTIVE-EMPS"; 
	private static final String EMPVIEWEMPSLASTASSPRD="EMPVIEW-EMPS-LASTASSPRD";

	public PersonalInformationService() {
		super(PersonalInformation.class);
	}

	public PersonalInformationService(Class<PersonalInformation> type) {
		super(type);
	}

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Autowired
	private EgwStatusHibernateDAO egwStatusHibernateDAO;
    
	public Session  getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}
	private ScriptService scriptService;

	public ScriptService getScriptService() {
		return scriptService;
	}

	public void setScriptService(ScriptService scriptService) {
		this.scriptService = scriptService;
	}


	/**
	 * since it is mapped to only one AccountDetailType -creditor it ignores the input parameter
	 */
	public List<EntityType> getAllActiveEntities(Integer employeeId) {
		List<EntityType> entities=new ArrayList<EntityType>();
		entities.addAll(findAllByNamedQuery("ACTIVE_EMPLOYEES"));
		return entities;
	}

	public List<EntityType> filterActiveEntities(String filterKey, int maxRecords, Integer accountDetailTypeId) {
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		List<EntityType> entities=new ArrayList<EntityType>();
		Page pg = findPageByNamedQuery("ACTIVE_EMPLOYEES_STARTSWITH", 0, pageSize,filterKey + "%" ,filterKey + "%");
		entities.addAll(pg.getList());
		return entities;
	}

	/**
	 * when filterbydept set to yes return employee list based on the login user who has the current assignment  
	 * when filterbydept set to no return all employee list  who has the assignment in the current/last assignment 
	 * @param userid
	 * @param autoValue
	 * @param maxRecords 
	 * @return employee list
	 */
	public List<PersonalInformation> getActiveEmpListByUserLogin(Integer userid,String autoValue,int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		autoValue=(autoValue+"%");
		String filterByDept = appConfigValuesService.getAppConfigValue("EIS-PAYROLL","FILTERBYDEPT","false");

		if(filterByDept!=null && filterByDept.toUpperCase().equals("YES"))
		{    	
			List deptIdList=getDeptsForLoggedInUser(userid);
			if(deptIdList.isEmpty())
				return Collections.emptyList();
			List<PersonalInformation> personalinfoIdList=findPageByNamedQuery(ACTIVEEMPSBYLOGGEDINUSER, 0, maxRecords,autoValue,deptIdList).getList();
			return personalinfoIdList;
		}
		else
		{
			return findPageByNamedQuery(EMPVIEWACTIVEEMPS, 0,pageSize,autoValue).getList();
			//return findAllByNamedQuery(PERSONALINFOEMPCODESTARTSWITH,autoValue);
		}	


	}
	/**
	 * return employee list based on the login user who has the assignment in the current period/MaxFromdate
	 * @param userid
	 * @param autoValue
	 * @param maxRecords 
	 * @return employee list
	 */
	public List<PersonalInformation> getEmpListByUserLogin(Integer userid,String autoValue,int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		autoValue=(autoValue+"%");
		String filterByDept = appConfigValuesService.getAppConfigValue("EIS-PAYROLL","FILTERBYDEPT","false");

		if(filterByDept!=null && filterByDept.toUpperCase().equals("YES"))
		{   
			List deptIdList=getDeptsForLoggedInUser(userid);
			if(deptIdList.isEmpty())
				return Collections.emptyList();

			List personalinfoIdList=findPageByNamedQuery(EMPVIEWBYLOGGEDINUSER, 0, pageSize,autoValue,autoValue,deptIdList).getList();
			return personalinfoIdList;
		}
		else
		{
			return findPageByNamedQuery(EMPVIEWEMPSLASTASSPRD,0,pageSize,autoValue,autoValue).getList();
		}	


	}
	/**
	 * Its applicable only when 'isfiltebydept' is set to yes 
	 * returns the departments for the logged in user dept ,if he/she is HOD then includes those departments as well
	 * @param userId
	 * @return DepartmentLsit of ids
	 */
	private List getDeptsForLoggedInUser(Integer userId){
		List<BigDecimal> deptList=	findPageByNamedQuery(EMPVIEWDEPTIDSLOGGEDINUSER, 0,null,userId,userId).getList();
		List<Integer> deptListInt=new ArrayList<Integer>();
		for(BigDecimal deptId:deptList)
		{ 
			if(deptId!=null)
			{
				deptListInt.add(deptId.intValue());
			}
		}
		return deptListInt;
	}


	public List<EmployeeView> getAllActiveEmployeesEmpViewByPrimaryAssignment(String filterKey, int maxRecords)
	{
		Integer pageSize = (maxRecords > 0 ? maxRecords : null);
		List<EmployeeView> personalInfEntities=new ArrayList<EmployeeView>();
		Page pg = findPageByNamedQuery("ALLACTIVE-EMPS-EMPVIEW", 0,pageSize,filterKey + "%");
		personalInfEntities.addAll(pg.getList());
		return personalInfEntities;
	}


	/**
	 * Returns List of Employees for the given status  and
	 * Date range considered for the status['Retired','Deceased'] and as of toDate for the status[ 'Employed','Suspended' ]
	 * @param statusid 
	 * @param fromDate
	 * @param toDate
	 * @return
	 */
	public  List<PersonalInformation> getEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate){
		List<PersonalInformation> employeeList;
		try {
			employeeList = buildQueryForEmpSearchByStatus(statusid, fromDate, toDate).getResultList();
		} catch (HibernateException he) {
			throw new ApplicationRuntimeException("Exception:" + he.getMessage(),he);
		}
		return employeeList;
	}

	public  Page getEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate,Integer pageNumber,Integer pageSize){
		org.hibernate.query.Query q = buildQueryForEmpSearchByStatus(statusid, fromDate, toDate);
		List list = q.setFirstResult((pageNumber - 1) * pageSize).setMaxResults(pageSize).getResultList();
		return new Page(pageNumber, pageSize, list);
	}

	public  int getTotalCountOfEmployeesByStatus(Integer statusid ,Date fromDate,Date toDate){
		return buildQueryForEmpSearchByStatus(statusid, fromDate, toDate).getResultList().size();
	}

	private org.hibernate.query.Query buildQueryForEmpSearchByStatus(Integer statusid, Date fromDate, Date toDate) {
		EgwStatus egwStatus = egwStatusHibernateDAO.findById(statusid, false);
		String hql;
		if (egwStatus.getModuletype().equals("Employee") && egwStatus.getDescription().equalsIgnoreCase("Employed")) {
			hql = "from PersonalInformation emp join emp.egpimsAssignment assPrd" +
					" where assPrd.fromDate <= :toDate and (assPrd.toDate >= :toDate or assPrd.toDate is null)";
		} else if (egwStatus.getModuletype().equals("Employee") && egwStatus.getDescription().equalsIgnoreCase("Retired")) {
			hql = "from PersonalInformation emp where emp.retirementDate between :fromDate and :toDate";
		} else {
			hql = "from PersonalInformation emp where emp.deathDate between :fromDate and :toDate";
		}
		org.hibernate.query.Query q = getCurrentSession().createQuery(hql);
		if (hql.contains(":toDate")) q.setParameter("toDate", toDate);
		if (hql.contains(":fromDate")) q.setParameter("fromDate", fromDate);
		return q;
	}
	
	/**
	 * This API returns the list of EmployeeView objects which have a current assignment or
	 * assignment as on date based on the parameters in the map.
	 * @param criteriaParams - HashMap<String,Object> where the following keys are supported:-
	 * "departmentId" 	- Pass the id of the department to restrict the employees to
	 * "designationId"  - Pass the id of the designation to restrict the resultset
	 * "isPrimary"      - Possible values "Y" or "N". If "Y", then only employees with 
	 * 					Primary assignment will be returned. If "N" only employees with 
	 * 					temporary assignment is returned. If this key is not present in the map,
	 * 					employees with both temporary as well as primary assignments are returned.
	 * "asOnDate"		- Value should be the date for which the employees need to have an
	 * 					assignment. If this key is not passed, employeed that have an assignment
	 * 					as of today will be returned.
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	// Refactored for Hibernate 6 / JDK 17 compatibility: replaced legacy org.hibernate.Criteria with dynamic HQL query and subList pagination
	public List<EmployeeView> getListOfEmployeeViewBasedOnCriteria(HashMap<String,Object> criteriaParams, Integer pageNo, Integer pageSize) {
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
		try {
			Date asOnDate = DateUtils.today();
			StringBuilder hql = new StringBuilder("from EmployeeView ev where 1=1");
			if (criteriaParams.containsKey("departmentId")) hql.append(" and ev.deptId.id = :departmentId");
			if (criteriaParams.containsKey("designationId")) hql.append(" and ev.desigId.designationId = :designationId");
			if (criteriaParams.containsKey("isPrimary")) hql.append(" and ev.isPrimary = :isPrimary");
			if (criteriaParams.containsKey("asOnDate")) asOnDate = (Date) criteriaParams.get("asOnDate");
			hql.append(" and ev.fromDate <= :asOnDate and ev.toDate >= :asOnDate order by ev.id asc");
			org.hibernate.query.Query<EmployeeView> q = getCurrentSession().createQuery(hql.toString(), EmployeeView.class);
			if (criteriaParams.containsKey("departmentId")) q.setParameter("departmentId", criteriaParams.get("departmentId"));
			if (criteriaParams.containsKey("designationId")) q.setParameter("designationId", criteriaParams.get("designationId"));
			if (criteriaParams.containsKey("isPrimary")) q.setParameter("isPrimary", criteriaParams.get("isPrimary"));
			q.setParameter("asOnDate", asOnDate);
			List<EmployeeView> all = q.getResultList();
			int start = (pageNo - 1) * pageSize;
			employeeList = all.subList(Math.min(start, all.size()), Math.min(start + pageSize, all.size()));
		} catch (HibernateException e) {
			throw new ApplicationRuntimeException("Error occured in searching for employees",e);
		}
		return employeeList;
	}
	/**
	 * This API returns the list of EmployeeView objects which have a current assignment or
	 * assignment as on date based on the parameters in the map.
	 * @param criteriaParams - HashMap<String,Object> where the following keys are supported:-
	 * "departmentId" 	- Pass the List of id of the department to restrict the employees to
	 * "designationId"  - Pass the id of the designation to restrict the resultset
	 * "isPrimary"      - Possible values "Y" or "N". If "Y", then only employees with 
	 * 					Primary assignment will be returned. If "N" only employees with 
	 * 					temporary assignment is returned. If this key is not present in the map,
	 * 					employees with both temporary as well as primary assignments are returned.
	 * "employeeName" 	- Pass employee name.
	 * "employeeCode" 	- Pass employee codes as list.		 
	 * "isActive" 	    - Pass Integer Value either 0 or 1.	 * 
	 * "asOnDate"		- Value should be the date for which the employees need to have an
	 * 					assignment. If this key is not passed, employeed that have an assignment
	 * 					as of today will be returned.
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	
	// Refactored for Hibernate 6 / JDK 17 compatibility: replaced legacy org.hibernate.Criteria with dynamic HQL query and subList pagination
	public List<EmployeeView> getListOfEmployeeViewBasedOnListOfDesignationAndOtherCriteria(HashMap<String,Object> criteriaParams, Integer pageNo, Integer pageSize) {
		List<EmployeeView> employeeList = new ArrayList<EmployeeView>();
		try {
			Date asOnDate = DateUtils.today();
			StringBuilder hql = new StringBuilder("from EmployeeView ev where 1=1");
			if (criteriaParams.containsKey("departmentId")) hql.append(" and ev.deptId.id = :departmentId");
			if (criteriaParams.containsKey("designationId")) hql.append(" and ev.desigId.designationId in :designationId");
			if (criteriaParams.containsKey("isPrimary")) hql.append(" and ev.isPrimary = :isPrimary");
			if (criteriaParams.containsKey("employeeName") && criteriaParams.get("employeeName") != null && !"".equals(criteriaParams.get("employeeName"))) hql.append(" and lower(ev.employeeName) like :employeeName");
			if (criteriaParams.containsKey("isActive") && criteriaParams.get("isActive") != null && !"".equals(criteriaParams.get("isActive"))) hql.append(" and ev.isActive = :isActive");
			if (criteriaParams.containsKey("employeeCode")) hql.append(" and ev.employeeCode in :employeeCode");
			if (criteriaParams.containsKey("asOnDate")) asOnDate = (Date) criteriaParams.get("asOnDate");
			hql.append(" and ev.fromDate <= :asOnDate and ev.toDate >= :asOnDate order by ev.id asc");
			org.hibernate.query.Query<EmployeeView> q = getCurrentSession().createQuery(hql.toString(), EmployeeView.class);
			if (criteriaParams.containsKey("departmentId")) q.setParameter("departmentId", criteriaParams.get("departmentId"));
			if (criteriaParams.containsKey("designationId")) q.setParameterList("designationId", (List<Integer>) criteriaParams.get("designationId"));
			if (criteriaParams.containsKey("isPrimary")) q.setParameter("isPrimary", criteriaParams.get("isPrimary"));
			if (criteriaParams.containsKey("employeeName") && criteriaParams.get("employeeName") != null && !"".equals(criteriaParams.get("employeeName"))) q.setParameter("employeeName", "%" + criteriaParams.get("employeeName").toString().toLowerCase() + "%");
			if (criteriaParams.containsKey("isActive") && criteriaParams.get("isActive") != null && !"".equals(criteriaParams.get("isActive"))) q.setParameter("isActive", Integer.valueOf(criteriaParams.get("isActive").toString()));
			if (criteriaParams.containsKey("employeeCode")) q.setParameterList("employeeCode", (List<String>) criteriaParams.get("employeeCode"));
			q.setParameter("asOnDate", asOnDate);
			List<EmployeeView> all = q.getResultList();
			int start = (pageNo - 1) * pageSize;
			employeeList = all.subList(Math.min(start, all.size()), Math.min(start + pageSize, all.size()));
		} catch (HibernateException e) {
			throw new ApplicationRuntimeException("Error occured in searching for employees",e);
		}
		return employeeList;
	}
	@Override
	public List getAssetCodesForProjectCode(Integer accountdetailkey)
			throws ValidationException {

		return null;
	}
	@Override
	public List<? extends EntityType> validateEntityForRTGS(List<Long> idsList)
			throws ValidationException {

		return null;
	}
	@Override
	public List<? extends EntityType> getEntitiesById(List<Long> idsList)
			throws ValidationException {

		return null;
	}

}

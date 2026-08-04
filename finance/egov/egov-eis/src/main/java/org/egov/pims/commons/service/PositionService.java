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
package org.egov.pims.commons.service;

import org.egov.eis.entity.Assignment;
import org.egov.infstr.services.PersistenceService;
import org.egov.pims.commons.Position;
import org.hibernate.Session;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Date;
import java.util.List;

public class PositionService extends PersistenceService<Position, Integer> {  
	
    @PersistenceContext
	private EntityManager entityManager;

	public PositionService() {
	    super(Position.class);
    }

    public PositionService(Class<Position> type) {
        super(type);
    }
	    
    public Session  getCurrentSession() {
			return entityManager.unwrap(Session.class);
		}
	/**
	 * gives vacant positions for given date range and designation 
	 * @param fromDate
	 * @param toDate
	 * @param designationMasterId
	 * @return
	 */
	public List<Position> getVacantPositionCriteria(Date fromDate, Date toDate, Integer designationMasterId)
	{
		StringBuilder hql = new StringBuilder(
			"select p from Position p where p.id not in (" +
			"select distinct a.position.id from Assignment a where a.isPrimary = 'Y' " +
			"and a.fromDate <= :fromDate and (a.toDate >= :toDate or a.toDate is null))");
		if (designationMasterId != null && !designationMasterId.equals(0))
			hql.append(" and p.deptDesig.designation.id = :desigId");
		hql.append(" order by p.name asc");
		org.hibernate.query.Query<Position> query = getCurrentSession().createQuery(hql.toString(), Position.class);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		if (designationMasterId != null && !designationMasterId.equals(0))
			query.setParameter("desigId", designationMasterId);
		return query.getResultList();
	}
	

}

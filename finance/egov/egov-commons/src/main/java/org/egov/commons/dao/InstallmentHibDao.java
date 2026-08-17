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
package org.egov.commons.dao;

import org.egov.commons.Installment;
import org.egov.infra.admin.master.entity.Module;
import org.egov.infra.utils.DateUtils;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Repository
public class InstallmentHibDao<T, id extends Serializable> implements InstallmentDao {

    @PersistenceContext
    private EntityManager entityManager;

    private Session getCurrentSession() {
        return entityManager.unwrap(Session.class);
    }

    @Override
    // Replaced removed setEntity and setDate methods with setParameter for Hibernate 6 query parameter binding
    public List<Installment> getInsatllmentByModule(final Module module) {
        final Query qry = getCurrentSession().createQuery("from Installment I where I.module=:module order by installmentYear desc");
        qry.setParameter("module", module);

        return qry.list();
    }

    @Override
    public List getInsatllmentByModule(final Module module, final Date year) {
        final Query qry = getCurrentSession()
                .createQuery("from Installment I where I.module=:module and I.installmentYear=:year");
        qry.setParameter("module", module);
        qry.setParameter("year", year);

        return qry.list();
    }

    @Override
    public Installment getInsatllmentByModule(final Module module, final Date year, final Integer installmentNumber) {
        final Query qry = getCurrentSession().createQuery(
                "from Installment I where I.module=:module and I.installmentYear=:year and I.installmentNumber =:instNum");
        qry.setParameter("module", module);
        qry.setParameter("year", year);
        qry.setParameter("instNum", installmentNumber);

        return (Installment) qry.uniqueResult();
    }

    @Override
    public Installment getInsatllmentByModuleForGivenDate(final Module module, final Date installmentDate) {
        final Query qry = getCurrentSession()
                .createQuery("from Installment I where I.module=:module and (I.fromDate <= :fromYear and I.toDate >=:toYear)");
        qry.setParameter("module", module);
        qry.setParameter("fromYear", installmentDate);
        qry.setParameter("toYear", installmentDate);

        return (Installment) qry.uniqueResult();

    }

    @Override
    public List<Installment> getEffectiveInstallmentsforModuleandDate(final Date dateToCompare, final int noOfMonths,
                                                                      final Module mod) {
        final Query qry = getCurrentSession().createQuery(
                "from org.egov.commons.Installment inst where  inst.toDate >= :dateToCompare and inst.toDate < :dateToComparemax   and inst.module=:module");
        qry.setParameter("dateToCompare", dateToCompare);
        qry.setParameter("module", mod);
        final Date dateToComparemax = DateUtils.add(dateToCompare, Calendar.MONTH, noOfMonths);
        qry.setParameter("dateToComparemax", dateToComparemax);

        return qry.list();
    }

    @Override
    public Installment getInsatllmentByModuleForGivenDateAndInstallmentType(final Module module, final Date installmentDate,
                                                                            final String installmentType) {
        final Query qry = getCurrentSession().createQuery(
                "from Installment I where I.module=:module and (I.fromDate <= :fromYear and I.toDate >=:toYear) and I.installmentType = :installmentType");
        qry.setParameter("module", module);
        qry.setParameter("fromYear", installmentDate);
        qry.setParameter("toYear", installmentDate);
        qry.setParameter("installmentType", installmentType);
        return (Installment) qry.uniqueResult();
    }
    
    @Override
    public List<Installment> getInstallmentsByModuleForGivenDateAndInstallmentType(final Module module, final Date installmentDate,
            final String installmentType) {
        final Query qry = getCurrentSession().createQuery(
                "from Installment I where I.module=:module and I.toDate >=:fromDate and I.toDate<=:tillDate and I.installmentType = :installmentType");
        qry.setParameter("module", module);
        qry.setParameter("fromDate", installmentDate);
        qry.setParameter("tillDate", new Date());
        qry.setParameter("installmentType", installmentType);
        return qry.list();
    }
    
    @Override
    public List<Installment> getInstallmentsByModuleBetweenFromDateAndToDateAndInstallmentType(final Module module,
            final Date fromDate, final Date toDate, final String installmentType) {
        final Query qry = getCurrentSession().createQuery(
                "from Installment I where I.module=:module and I.toDate >=:fromDate and I.toDate<=:tillDate and I.installmentType = :installmentType");
        qry.setParameter("module", module);
        qry.setParameter("fromDate", fromDate);
        qry.setParameter("tillDate", toDate);
        qry.setParameter("installmentType", installmentType);
        return qry.list();
    }
    
    @Override
    public List<Installment> getInstallmentsByModuleAndFromDateAndInstallmentType(final Module module,
            final Date fromDate, final Date currentDate, final String installmentType) {
        final Query qry = getCurrentSession().createQuery(
                "from Installment I where I.module=:module and I.toDate >=:fromDate and I.fromDate<=:tillDate and I.installmentType = :installmentType");
        qry.setParameter("module", module);
        qry.setParameter("fromDate", fromDate);
        qry.setParameter("tillDate", currentDate);
        qry.setParameter("installmentType", installmentType);
        return qry.list();
    }


    @Override
    public List<Installment> fetchInstallments(final Module module, final Date toInstallmentDate, final int noOfInstallmentToFetch) {
        final Query qry = getCurrentSession()
                .createQuery("from Installment I where I.module=:module and I.installmentYear<=:installmentYear order by installmentYear desc");
        qry.setParameter("module", module);
        qry.setParameter("installmentYear", toInstallmentDate);
        qry.setMaxResults(noOfInstallmentToFetch);
        return qry.list();
    }

    @Override
    public List<Installment> getAllInstallmentsByModuleAndStartDate(final Module module, final Date currDate ) {
        final Query qry = getCurrentSession().createQuery(" select inst  from Installment inst,CFinancialYear finYear where inst.module=:module and inst.fromDate >= (select fromDate from Installment "
                +" where module=:module and ((cast(:currDate as date)) between fromDate and toDate)) and cast(inst.toDate as date) <= cast(finYear.endingDate as date) "
               +"  and now() between finYear.startingDate and finYear.endingDate order by inst.fromDate");
        qry.setParameter("module", module.getId());
        qry.setParameter("currDate", currDate);
        return qry.list();
    }
    
    

    @Override
    public Installment fetchInstallmentByModuleAndInstallmentNumber(final Module module, final Integer installmentNumber) {
        return (Installment) getCurrentSession()
                .createQuery("from Installment I where I.module=:module and I.installmentNumber =:installmentNumber", Installment.class)
                .setParameter("module", module).setParameter("installmentNumber", installmentNumber).uniqueResult();
    }

    @Override
    public Installment getInsatllmentByModuleAndDescription(Module module, String description) {
        return (Installment) getCurrentSession()
                .createQuery("from Installment I where I.module=:module and I.description =:description", Installment.class)
                .setParameter("module", module).setParameter("description", description).uniqueResult();
    }

    public Installment findById(final int i, final boolean b) {
        return (Installment)getCurrentSession().get(Installment.class, i);
    }
    
    @Override
    public List<Installment> fetchPreviousInstallmentsInDescendingOrderByModuleAndDate(final Module module, final Date installmentDate, final int noOfInstallmentToFetch) {
        final Query qry = getCurrentSession()
                .createQuery("from Installment I where I.module=:module and I.toDate< :installmentDate order by fromDate desc");
        qry.setParameter("module", module);
        qry.setParameter("installmentDate", installmentDate);
        qry.setMaxResults(noOfInstallmentToFetch);
        return qry.list();
    }
    
    @Override
    public List<Installment> fetchNextInstallmentsByModuleAndDate(final Module module, final Date date)
    {

        final Query qry = getCurrentSession()
                .createQuery(
                        "from Installment I where I.module=:module and I.fromDate>= :installmentDate order by fromDate desc");
        qry.setParameter("module", module);
        qry.setParameter("installmentDate", date);

        return qry.list();

    }
    
    @Override
    public List<Installment> getInstallmentsByModuleAndInstallmentYearOrderByInstallmentYearDescending(final Module module,
            final int year) {

        final Query qry = getCurrentSession()
                .createQuery(
                        "from Installment I where I.module=:module and extract(year from I.installmentYear)=:year order by installment_year desc");
        qry.setParameter("module", module);
        qry.setParameter("year", year);

        return qry.list();

    }
}
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
package org.egov.egf.voucher.repository;

import org.egov.commons.CVoucherHeader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * @author venki
 *
 */

/**
 * Spring Data JPA repository for {@link CVoucherHeader} entities,
 * providing data access operations for journal vouchers.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD and pagination operations,
 * and defines additional query methods for voucher number lookups, journal voucher counts,
 * and payment counts used by the ULB (Urban Local Body) dashboard.</p>
 *
 * @see CVoucherHeader
 */
@Repository
public interface JournalVoucherRepository extends JpaRepository<CVoucherHeader, Long> {

    /**
     * Finds a single {@link CVoucherHeader} by its exact voucher number.
     *
     * @param voucherNumber the exact voucher number to search for; must not be {@code null}
     * @return the matching {@link CVoucherHeader}, or {@code null} if none found
     */

    CVoucherHeader findByVoucherNumber(final String voucherNumber);

    List<CVoucherHeader> findByVoucherNumberContainingIgnoreCase(final String voucherNumber);


    /**
     * Returns the total number of payment vouchers whose voucher date falls within
     * the given date range, for use on the ULB dashboard.
     *
     * <p>Counts records by joining {@code voucherheader} with {@code paymentheader}
     * on the voucher header ID, ensuring only vouchers that have an associated
     * payment entry are counted.</p>
     *
     * @param startDate the start of the date range (inclusive); must not be {@code null}
     * @param endDate   the end of the date range (inclusive); must not be {@code null}
     * @return the count of payment vouchers within the specified date range
     */

    //for ULB Dashboard
    @Query(value = "SELECT COUNT(*) FROM voucherheader vh INNER JOIN paymentheader ph ON vh.id = ph.voucherheaderid WHERE vh.voucherdate BETWEEN :startDate AND :endDate", nativeQuery = true)
    Long getPaymentsCount(@Param("startDate") Date startDate, @Param("endDate") Date endDate);


    Long countByVoucherDateBetween(Date startDate, Date endDate);


}

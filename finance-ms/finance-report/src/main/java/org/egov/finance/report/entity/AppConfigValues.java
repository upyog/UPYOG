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

package org.egov.finance.report.entity;


import static org.egov.finance.report.entity.AppConfigValues.SEQ_APPCONFIG_VALUE;

import java.util.Date;
import java.util.Objects;

import org.egov.finance.report.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "eg_appconfig_values")
@SequenceGenerator(name = SEQ_APPCONFIG_VALUE, sequenceName = SEQ_APPCONFIG_VALUE, allocationSize = 1)
public class AppConfigValues extends AuditDetailswithVersion {

    public static final String SEQ_APPCONFIG_VALUE = "SEQ_EG_APPCONFIG_VALUES";
    private static final long serialVersionUID = 1L;
    @Expose
    @Id
    @GeneratedValue(generator = SEQ_APPCONFIG_VALUE, strategy = GenerationType.SEQUENCE)
    private Long id;

    @NotBlank
    @SafeHtml
    @Length(max = 4000)
    @Column(name = "value")
    private String value;

    @NotNull
    @Temporal(TemporalType.DATE)
    @Column(name = "effective_from", updatable = false)
    private Date effectiveFrom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "key_id", nullable = false)
    @JsonIgnore
    private AppConfig config;

    @Transient
    private boolean markedForRemoval;

   

    public Date getEffectiveFrom() {
        return effectiveFrom;
    }

    public void setEffectiveFrom(final Date effectiveFrom) {
        this.effectiveFrom = effectiveFrom;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public AppConfig getConfig() {
        return config;
    }

    public void setConfig(final AppConfig config) {
        this.config = config;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void setMarkedForRemoval(final boolean markedForRemoval) {
        this.markedForRemoval = markedForRemoval;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (!(o instanceof AppConfigValues))
            return false;
        final AppConfigValues that = (AppConfigValues) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, value);
    }
}
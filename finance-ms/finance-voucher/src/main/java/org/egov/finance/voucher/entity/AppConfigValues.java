

package org.egov.finance.voucher.entity;


import static org.egov.finance.voucher.entity.AppConfigValues.SEQ_APPCONFIG_VALUE;

import java.util.Date;
import java.util.Objects;

import org.egov.finance.voucher.customannotation.SafeHtml;
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
    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(final Long id) {
        this.id = id;
    }

}
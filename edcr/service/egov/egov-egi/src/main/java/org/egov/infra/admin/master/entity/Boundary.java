package org.egov.infra.admin.master.entity;

import static org.egov.infra.admin.master.entity.Boundary.SEQ_BOUNDARY;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.egov.infra.persistence.entity.AbstractAuditable;
import org.egov.infra.persistence.validator.annotation.CompositeUnique;
import org.egov.infra.persistence.validator.annotation.DateFormat;
import org.egov.infra.persistence.validator.annotation.Unique;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.validator.constraints.Length;
import javax.validation.constraints.NotBlank;
import org.egov.infra.validation.SanitizeHtml;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

@Entity
@CompositeUnique(fields = {"boundaryNum", "boundaryType"}, enableDfltMsg = true)
@Unique(fields = "code", enableDfltMsg = true)
@Table(name = "EG_BOUNDARY")
@SequenceGenerator(name = SEQ_BOUNDARY, sequenceName = SEQ_BOUNDARY, allocationSize = 1)
public class Boundary extends AbstractAuditable {

    public static final String SEQ_BOUNDARY = "seq_eg_boundary";
    private static final long serialVersionUID = 3054956514161912026L;
    @Expose
    @Id
    @GeneratedValue(generator = SEQ_BOUNDARY, strategy = GenerationType.SEQUENCE)
    private Long id;

    @Length(max = 512)
    @SanitizeHtml
    @NotBlank
    private String name;

    @Length(max = 25)
    @SanitizeHtml
    @NotBlank
    private String code;

    private Long boundaryNum;

    @ManyToOne
    @JoinColumn(name = "boundaryType", updatable = false)
    private BoundaryType boundaryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent")
    @Fetch(value = FetchMode.SELECT)
    private Boundary parent;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "parent")
    @Fetch(value = FetchMode.SUBSELECT)
    @JsonIgnore
    private Set<Boundary> children = new HashSet<>();

    @DateFormat
    @DateTimeFormat(pattern = "dd-MM-yyyy")
    private Date fromDate;

    private Date toDate;

    private boolean active;

    private Long bndryId;

    @SanitizeHtml
    private String localName;

    private Float longitude;

    private Float latitude;

    @Length(max = 32)
    private String materializedPath;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    protected void setId(final Long id) {
        this.id = id;
    }

    public String getLocalName() {
        return localName;
    }

    public void setLocalName(final String boundaryNameLocal) {
        localName = boundaryNameLocal;
    }

    public Boundary getParent() {
        return parent;
    }

    public void setParent(final Boundary parent) {
        this.parent = parent;
    }

    public Set<Boundary> getChildren() {
        return children;
    }

    public void setChildren(final Set<Boundary> boundaries) {
        children = boundaries;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public boolean isLeaf() {
        return getChildren().isEmpty();
    }

    public BoundaryType getBoundaryType() {
        return boundaryType;
    }

    public void setBoundaryType(final BoundaryType boundaryType) {
        this.boundaryType = boundaryType;
    }

    public Long getBoundaryNum() {
        return boundaryNum;
    }

    public void setBoundaryNum(final Long number) {

        boundaryNum = number;
    }

    public boolean isRoot() {
        return getParent() == null;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(final Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(final Date toDate) {
        this.toDate = toDate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(final boolean active) {
        this.active = active;
    }

    public Long getBndryId() {
        return bndryId;
    }

    public void setBndryId(final Long bndryId) {
        this.bndryId = bndryId;
    }

    public Float getLongitude() {
        return longitude;
    }

    public void setLongitude(final Float longitude) {
        this.longitude = longitude;
    }

    public Float getLatitude() {
        return latitude;
    }

    public void setLatitude(final Float latitude) {
        this.latitude = latitude;
    }

    public String getMaterializedPath() {
        return materializedPath;
    }

    public void setMaterializedPath(final String materializedPath) {
        this.materializedPath = materializedPath;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other)
            return true;
        if (!(other instanceof Boundary))
            return false;
        Boundary boundary = (Boundary) other;
        return Objects.equal(boundaryNum, boundary.boundaryNum) &&
                Objects.equal(boundaryType, boundary.boundaryType);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(boundaryNum, boundaryType);
    }

}

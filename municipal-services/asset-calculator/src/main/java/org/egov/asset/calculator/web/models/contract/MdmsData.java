package org.egov.asset.calculator.web.models.contract;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "eg_mdms_data", schema = "public", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"id"})
})
public class MdmsData implements Serializable {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id;

    @Column(name = "tenantid", length = 255, nullable = false)
    private String tenantId;

    @Column(name = "uniqueidentifier", length = 255, nullable = false)
    private String uniqueIdentifier;

    @Column(name = "schemacode", length = 255, nullable = false)
    private String schemaCode;

    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private String data;

    @Column(name = "isactive", nullable = false)
    private Boolean isActive;

    @Column(name = "createdby", length = 64)
    private String createdBy;

    @Column(name = "lastmodifiedby", length = 64)
    private String lastModifiedBy;

    @Column(name = "createdtime")
    private Long createdTime;

    @Column(name = "lastmodifiedtime")
    private Long lastModifiedTime;
}

package org.egov.applyworkflow.web.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "eg_mdms_data", schema = "public")
public class EgMdmsData {

    @Id
    @Column(name = "id", length = 64, nullable = false)
    private String id = UUID.randomUUID().toString();

    @Column(name = "tenantid", length = 255, nullable = false)
    private String tenantId;

    @Column(name = "uniqueidentifier", length = 255, nullable = false)
    private String uniqueIdentifier;

    @Column(name = "schemacode", length = 255, nullable = false)
    private String schemaCode;

    //@Type(type = "jsonb")
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private String data;

    @Column(name = "isactive", nullable = false)
    private boolean isActive;

    @Column(name = "createdby", length = 64)
    private String createdBy;

    @Column(name = "lastmodifiedby", length = 64)
    private String lastModifiedBy;

    @Column(name = "createdtime")
    private Long createdTime;

    @Column(name = "lastmodifiedtime")
    private Long lastModifiedTime;
}
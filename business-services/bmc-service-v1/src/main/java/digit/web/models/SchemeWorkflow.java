package digit.web.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name="eg_wf_action_v2")
public class SchemeWorkflow {

    @Id
    @Column(name = "uuid")
    private String uuid;

    @Column(name = "tenantid")
    private String tenantId;

    @Column(name = "currentstate")
    private String currentState;

    @Column(name = "action")
    private String action;

    @Column(name = "nextstate")
    private String nextState;

    @Column(name = "roles")
    private String roles;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "createdtime")
    private Long createdTime;

    @Column(name = "lastmodifiedby")
    private String lastModifiedBy;

    @Column(name = "lastmodifiedtime")
    private Long lastModifiedTime;

    @Column(name = "active")
    private String active;

}

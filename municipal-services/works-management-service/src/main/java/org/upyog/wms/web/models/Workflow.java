package org.upyog.wms.web.models;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Workflow details sent with create / update. Not persisted by this service; egov-workflow-v2 keeps the
 * history. approvingDepartment, designation and approverName are the FRS Section 7 fields; they are passed
 * to the process instance as additionalDetails.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Workflow {

    @Size(max = 64)
    private String action;

    private List<String> assignes;

    @Size(max = 2048)
    private String comments;

    private List<Document> documents;

    @Size(max = 64)
    private String approvingDepartment;

    @Size(max = 256)
    private String designation;

    @Size(max = 256)
    private String approverName;
}

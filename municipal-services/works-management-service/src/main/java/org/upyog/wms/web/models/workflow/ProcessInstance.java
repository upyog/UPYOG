package org.upyog.wms.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.User;
import org.upyog.wms.web.models.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProcessInstance {

    private String id;

    private String tenantId;

    private String businessService;

    private String businessId;

    private String action;

    private String moduleName;

    private State state;

    private String comment;

    private List<Document> documents;

    private List<User> assignes;

    private Object additionalDetails;
}

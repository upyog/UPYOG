package org.upyog.wms.web.models.mdms;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdmsCriteria {

    private String tenantId;

    private List<ModuleDetail> moduleDetails;
}

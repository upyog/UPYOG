package org.upyog.wms.calculator.web.models.mdms;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDetail {

    private String moduleName;

    private List<MasterDetail> masterDetails;
}

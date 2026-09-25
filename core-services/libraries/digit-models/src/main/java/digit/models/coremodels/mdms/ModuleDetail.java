package digit.models.coremodels.mdms;

import lombok.*;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDetail {

    @NotNull
    @Size(max=256)
    private String moduleName;

    private List<MasterDetail> masterDetails;

}
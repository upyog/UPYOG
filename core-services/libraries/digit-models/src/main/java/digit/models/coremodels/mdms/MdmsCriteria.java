package digit.models.coremodels.mdms;

import lombok.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdmsCriteria {

    @NotNull
    @Size(max=256)
    private String tenantId;

    @NotNull
    @Valid
    private List<ModuleDetail> moduleDetails;
}

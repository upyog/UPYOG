package digit.web.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserSubSchemeMapping {

    private Long id;
    private String applicationNumber;
    private Long machineId;
    private Long courseId;
    private Long userId;
    private String tenantId;
    private Long createdOn;
    private String createdBy;
    private Long modifiedOn;
    private String modifiedBy;

}

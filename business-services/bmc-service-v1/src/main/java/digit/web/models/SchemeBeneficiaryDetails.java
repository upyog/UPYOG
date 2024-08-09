package digit.web.models;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SchemeBeneficiaryDetails {

    private Long machineId;
    private Long optedId;
    private Long courseId;
    private int has_applied_for_pension;
    private Instant startDate;
    private Instant endDate;
}

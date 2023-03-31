package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.birthcommon.model.DocumentDetails;
import org.ksmart.birth.birthcommon.model.demand.Demand;

import java.util.List;

public class CorrectionDob {
    @JsonProperty("childDOB")
    private Long dateOfBirth;

    @JsonProperty("documentDetails")
    private DocumentDetails documentDetails;
}

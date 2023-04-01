package org.ksmart.birth.web.model.correction;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.ksmart.birth.birthcommon.model.DocumentDetails;

import java.util.List;

public class CorrectionMotherDetails {
    @JsonProperty("childDOB")
    private Long dateOfBirth;

    @JsonProperty("DocumentDetails")
    private List<DocumentDetails> documentDetails;


}

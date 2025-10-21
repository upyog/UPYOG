package org.egov.ndc.calculator.web.models.ndc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NdcApplicationResponse {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Applicant")
    private ApplicantRequest applicant;

    @JsonProperty("NdcDetails")
    private List<NdcDetailsRequest> ndcDetails;

    @JsonProperty("Documents")
    private List<DocumentRequest> documents;

    public void addNdcDetailsItem(NdcDetailsRequest ndcDetailsItem) {
        if (this.ndcDetails == null) {
            this.ndcDetails = new ArrayList<>();
        }
        this.ndcDetails.add(ndcDetailsItem);
    }

    public void addDocumentsItem(DocumentRequest documentItem) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        this.documents.add(documentItem);
    }
}
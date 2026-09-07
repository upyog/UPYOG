package org.upyog.draft.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DraftResponse {

    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("Draft")
    private DraftDetail draft;

    @JsonProperty("Drafts")
    private List<DraftDetail> drafts;

    @JsonProperty("count")
    private Integer count;

    @JsonProperty("items")
    private List<DraftDetail> items;
}

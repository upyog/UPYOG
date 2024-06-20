package org.egov.wscalculation.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
public class MeterReadingResponses {
    @JsonProperty("ResponseInfo")
    private ResponseInfo responseInfo = null;
    
    @JsonProperty("meterReadingslist")
    @Valid
    private List<MeterReadingList> meterReadingslist = new ArrayList<>(); 
}

package org.egov.individual.web.models;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@ApiModel(description = "Representation of SearchResponse.")
@Validated

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResponse<E> {

    @JsonProperty("TotalCount")
    @Getter(AccessLevel.NONE)
    private Long totalCount;

    @JsonProperty("Response")
    @Builder.Default
    private List<E> response = new ArrayList<>();

    public Long getTotalCount() {
        if(totalCount == null)
            totalCount = (long) response.size();
        return totalCount;
    }
}
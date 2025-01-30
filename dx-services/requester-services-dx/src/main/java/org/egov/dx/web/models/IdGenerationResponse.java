package org.egov.dx.web.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.egov.dx.web.models.ResponseInfo;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IdGenerationResponse {

    private ResponseInfo responseInfo;

    private List<IdResponse> idResponses;

}

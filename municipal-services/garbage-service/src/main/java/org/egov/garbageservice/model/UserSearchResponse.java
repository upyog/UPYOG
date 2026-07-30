package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.response.ResponseInfo;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
/**
 * Paginated response from user service search containing UserV2 records and metadata.
 * Parsed when garbage-service looks up citizens by mobile or uuid.
 */
@Builder
public class UserSearchResponse {
    @JsonProperty("responseInfo")
    ResponseInfo responseInfo;

    @JsonProperty("user")
    List<UserSearchResponseContent> userSearchResponseContent;
}

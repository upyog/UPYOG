package org.egov.garbageservice.model;

import java.util.List;

import org.egov.common.contract.response.ResponseInfo;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

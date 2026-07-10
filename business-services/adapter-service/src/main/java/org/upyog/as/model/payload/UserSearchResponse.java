package org.upyog.as.model.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserSearchResponse {
	private List<UserInfo> user;
}
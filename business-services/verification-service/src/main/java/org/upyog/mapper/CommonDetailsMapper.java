package org.upyog.mapper;

import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

public interface CommonDetailsMapper {
	String getModuleName();
    CommonDetails mapJsonToCommonDetails(JsonNode json);
}

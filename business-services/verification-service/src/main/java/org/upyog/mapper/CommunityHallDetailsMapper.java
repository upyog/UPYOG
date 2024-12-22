package org.upyog.mapper;

import org.upyog.web.models.CommonDetails;

import com.fasterxml.jackson.databind.JsonNode;

public class CommunityHallDetailsMapper implements CommonDetailsMapper {

	@Override
    public String getModuleName() {
        return "communityhall";
    }
	
	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		// TODO Auto-generated method stub
		return null;
	}

}
